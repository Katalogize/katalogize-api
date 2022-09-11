package com.katalogizegroup.katalogize.config.security.user;

import com.katalogizegroup.katalogize.models.User;
import com.katalogizegroup.katalogize.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Optional<User> user = userRepository.getUserByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username : " + username);
        }
        return UserPrincipal.create(user.get());
    }

    public UserDetails loadUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        return UserPrincipal.create(user);
    }

}
