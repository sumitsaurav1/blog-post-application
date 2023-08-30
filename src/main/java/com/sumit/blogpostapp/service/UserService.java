package com.sumit.blogpostapp.service;


import com.sumit.blogpostapp.model.User;
import com.sumit.blogpostapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService
{
    @Autowired
    private UserRepository userRepository;

    public User save(User user){
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
       return userRepository.findOneByEmail(email);
    }
}
