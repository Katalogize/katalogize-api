package com.katalogizegroup.katalogize.config.login.user;

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

    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        Optional<User> user = userRepository.getUserByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email : " + email);
        }
        return UserPrincipal.create(user.get());
    }

    public UserDetails loadUserById(String id) {
        User user = userRepository.findById(Integer.parseInt(id)).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        return UserPrincipal.create(user);
    }

}
