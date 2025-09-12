package com.exam.serviceImpl;

import com.exam.config.CommonConfig;
import com.exam.entities.*;
import com.exam.repository.UserRepository;
import com.exam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    CommonConfig commonConfig;

    @Autowired
    private UserRepository userRepository;


    @Override
    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }


}
