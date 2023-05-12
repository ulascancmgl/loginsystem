package com.basiclogin.loginsystem.Configuration;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.basiclogin.loginsystem.Entity.Role;
import com.basiclogin.loginsystem.Entity.User;
import com.basiclogin.loginsystem.Repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Primary
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUserName(userName);

        if (user.isPresent()) {
            return new org.springframework.security.core.userdetails.User(user.get().getUserName(),
                    user.get().getPassword(),
                    mapRolesToAuthorities(user.get().getRole()));
        } else {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Role role) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.name()));
        return authorities;
    }

}
