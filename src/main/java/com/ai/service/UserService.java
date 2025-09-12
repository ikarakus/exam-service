package com.ai.service;


import com.ai.entities.User;
import java.util.Optional;

public interface UserService {

    Optional<User> getUser(Long id);

}
