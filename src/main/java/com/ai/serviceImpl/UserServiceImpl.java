package com.ai.serviceImpl;

import com.ai.config.CommonConfig;
import com.ai.entities.*;
import com.ai.repository.UserRepository;
import com.ai.service.UserService;
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
