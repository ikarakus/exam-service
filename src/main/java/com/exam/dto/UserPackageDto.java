package com.exam.dto;


import com.exam.entities.User;
import java.io.Serializable;
import java.sql.Timestamp;

public class UserPackageDto implements Serializable {
	private Long id;
    private User user;
    private Integer packageId;
    private Byte packageMinute;
    private Byte packageDay;
    private Byte packageMonth;
    private Byte discountPercent;
    private String currency;
    private Double amount;
    private Double actualAmount;
    private Double discountedAmount;
    private Timestamp startDate;
    private Timestamp endDate;
    private String promoCode;
    private Long paymentId;
    private Long installmentId;
    private Byte packageType;
    private Boolean renewal;
    private String courseLang;
    private Double walletAmount;
    private String transactionId;
    private String originalTransactionId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public Byte getPackageMinute() {
        return packageMinute;
    }

    public void setPackageMinute(Byte packageMinute) {
        this.packageMinute = packageMinute;
    }

    public Byte getPackageDay() {
        return packageDay;
    }

    public void setPackageDay(Byte packageDay) {
        this.packageDay = packageDay;
    }

    public Byte getPackageMonth() {
        return packageMonth;
    }

    public void setPackageMonth(Byte packageMonth) {
        this.packageMonth = packageMonth;
    }

    public Byte getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Byte discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(Double actualAmount) {
        this.actualAmount = actualAmount;
    }

    public Double getDiscountedAmount() {
        return discountedAmount;
    }

    public void setDiscountedAmount(Double discountedAmount) {
        this.discountedAmount = discountedAmount;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getInstallmentId() {
        return installmentId;
    }

    public void setInstallmentId(Long installmentId) {
        this.installmentId = installmentId;
    }

    public Byte getPackageType() {
        return packageType;
    }

    public void setPackageType(Byte packageType) {
        this.packageType = packageType;
    }

    public Boolean getRenewal() {
        return renewal;
    }

    public void setRenewal(Boolean renewal) {
        this.renewal = renewal;
    }

    public String getCourseLang() {
        return courseLang;
    }

    public void setCourseLang(String courseLang) {
        this.courseLang = courseLang;
    }

    public Double getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(Double walletAmount) {
        this.walletAmount = walletAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOriginalTransactionId() {
        return originalTransactionId;
    }

    public void setOriginalTransactionId(String originalTransactionId) {
        this.originalTransactionId = originalTransactionId;
    }
}
