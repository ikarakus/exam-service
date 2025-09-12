package com.exam.service;


import com.exam.entities.User;
import java.util.Optional;

public interface UserService {

    Optional<User> getUser(Long id);

}
