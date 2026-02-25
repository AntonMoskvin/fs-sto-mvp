package com.sfsto.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Map;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    // Simple in-memory user store for MVP
    private static final Map<String, String> USERS = Map.of(
            "user@sfsto.test", "CLIENT",
            "admin@sfsto.test", "STATION_ADMIN"
    );

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!USERS.containsKey(username)) {
            throw new UsernameNotFoundException("User not found");
        }
        String role = USERS.get(username);
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("N/A")
                .authorities(Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role)))
                .accountLocked(false)
                .build();
    }
}
