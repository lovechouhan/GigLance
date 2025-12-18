package com.minor.freelancing.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.minor.freelancing.Entities.User;
import com.minor.freelancing.Repositories.UserRepository;

@Service
public class SecurityCustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find user by email (the User entity and its subclasses implement UserDetails)
        User user = userRepo.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        // Return the User (or subclass) directly. JPA will return the concrete subclass
        // (Client or Freelancer)
        // which already implements UserDetails and contains authorities, password and
        // enabled state.
        return user;

    }

}
