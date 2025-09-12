package com.exam.util;

import com.exam.dto.ResponseDto;
import com.exam.dto.ResultCodes;
import com.exam.exception.UserNotFoundException;
import com.exam.exception.UserSecurityException;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionException;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class Helper {

	private static final Logger logger = LoggerFactory.getLogger(Helper.class);
	private static final String[] IP_HEADER_CANDIDATES = {
			"X-Forwarded-For",
			"Proxy-Client-IP",
			"WL-Proxy-Client-IP",
			"HTTP_X_FORWARDED_FOR",
			"HTTP_X_FORWARDED",
			"HTTP_X_CLUSTER_CLIENT_IP",
			"HTTP_CLIENT_IP",
			"HTTP_FORWARDED_FOR",
			"HTTP_FORWARDED",
			"HTTP_VIA",
			"REMOTE_ADDR" };


	public static String getClientIpAddress(HttpServletRequest request) {
		for (String header : IP_HEADER_CANDIDATES) {
			String ip = Optional.ofNullable(request.getHeader(header)).orElse(request.getRemoteAddr());
			if (ip.equals("0:0:0:0:0:0:0:1")) ip = "127.0.0.1";
			Assert.isTrue(ip.chars().filter($ -> $ == '.').count() == 3, "Illegal IP: " + ip);
			return ip;
		}
		return request.getRemoteAddr();
	}
	public static void fillResponse(ResponseDto<?> response, int resultCode, String errorMessage) {

		response.setResultCode(resultCode);
		response.setErrorMessage(errorMessage);
	}

	public static String getUsername (HttpServletRequest req) {
		Principal principal = req.getUserPrincipal();
		return principal.getName();
//		if (principal != null)
//			return principal.getName();
//		else
//			return (String)req.getAttribute("username");
	}

	public static void handleException(ResponseDto<?> response, Exception e) {
		try {
			if (e instanceof DataIntegrityViolationException) {
				DataIntegrityViolationException ex = (DataIntegrityViolationException) e;
				fillResponse(response, ResultCodes.DATABASE_EXCEPTION, ex.getCause().getCause().toString());
			}
			else if (e instanceof DataAccessException) {
				DataAccessException ex = (DataAccessException) e;
				fillResponse(response, ResultCodes.DATABASE_EXCEPTION, ex.getCause().toString());
			}
			else if (e instanceof TransactionException) {
				TransactionException ex = (TransactionException) e;
				fillResponse(response, ResultCodes.TRANSACTION_EXCEPTION, ex.getCause().toString());
			}
			else if (e instanceof IOException) {
				IOException ex = (IOException) e;
				fillResponse(response, ResultCodes.IO_EXCEPTION, ex.getCause().toString());
			}
			else if (e instanceof AmazonServiceException) {
				AmazonServiceException ex = (AmazonServiceException) e;
				fillResponse(response, ResultCodes.AMAZON_EXCEPTION, ex.getErrorMessage());
			}
			else if (e instanceof AmazonClientException) {
				AmazonClientException ex = (AmazonClientException) e;
				fillResponse(response, ResultCodes.AMAZON_CLIENT_EXCEPTION, ex.getCause().toString());
			}
			else if (e instanceof InvalidParameterException) {
				InvalidParameterException ex = (InvalidParameterException) e;
				fillResponse(response, ResultCodes.INVALID_PARAMETER_EXCEPTION, ex.getMessage());
			}
			else if (e instanceof EntityNotFoundException) {
				EntityNotFoundException ex = (EntityNotFoundException) e;
				fillResponse(response, ResultCodes.NOT_FOUND_EXCEPTION, ex.getCause().toString());
			}
			else if (e instanceof NoSuchElementException) {
				NoSuchElementException ex = (NoSuchElementException) e;
				fillResponse(response, ResultCodes.NOT_FOUND_EXCEPTION, ex.getCause().toString());
			}
			else if (e instanceof UserNotFoundException) {
				fillResponse(response, ResultCodes.USER_NOT_FOUND,"user not found");
			}
			else if (e instanceof UserSecurityException) {
				fillResponse(response, ResultCodes.SECURITY_ERROR,"user authentication is required to access this resource");
			}
			else {
				fillResponse(response, ResultCodes.UNEXPECTED_ERROR, e.toString());

			}

			logger.error(String.valueOf(response.getResultCode()),e);
		} catch (Exception e1) {
			logger.error(String.valueOf(ResultCodes.UNEXPECTED_ERROR),e);
		}
	}


	public static String getMd5(String input) {
		try {
			// Static getInstance method is called with hashing SHA
			MessageDigest md = MessageDigest.getInstance("MD5");

			// digest() method called
			// to calculate message digest of an input
			// and return array of byte
			byte[] messageDigest = md.digest(input.getBytes());

			// Convert byte array into signum representation
			BigInteger no = new BigInteger(1, messageDigest);

			// Convert message digest into hex value
			String hashtext = no.toString(16);

			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}

			return hashtext;
		}

		// For specifying wrong message digest algorithms
		catch (NoSuchAlgorithmException e) {
			System.out.println("Exception thrown"
					+ " for incorrect algorithm: " + e);
			return null;
		}
	}


	public static Locale getClientLocale() {
		try {
			return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getLocale();
		} catch (Exception ex) {
			// client has not defined his locale, fallback to english
			return Locale.ENGLISH;
		}
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

	public static void authorize(HttpServletRequest req) throws UserSecurityException {
		if (req.getUserPrincipal() == null) {
			throw new UserSecurityException();
		}
	}

	public static Long toNearestMinute(Long d) {
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(d);
		if (c.get(Calendar.SECOND) >= 30)
			c.add(Calendar.MINUTE, 1);
		c.set(Calendar.SECOND, 0);
		return c.getTimeInMillis();
	}

	public static <T> List<T> toList(Optional<T> opt) {
		return opt
				.map(Collections::singletonList)
				.orElseGet(Collections::emptyList);
	}
}
