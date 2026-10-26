package com.example.jwt_and_graphql.service;

import com.example.jwt_and_graphql.entity.Role;
import com.example.jwt_and_graphql.entity.User;
import com.example.jwt_and_graphql.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JWTService jwtService,
            AuthenticationManager authenticationManager
    ){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtService=jwtService;
        this.authenticationManager=authenticationManager;
    }

    public User register(String name, String username, String password){
        if(userRepository.existsByUsername(username)){
            throw  new RuntimeException("Username already exists");
        }

        User user=new User();
        user.setName(name);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(password));
        user.setUsername(username);

        return userRepository.save(user);
    }

    public String login(String username, String password){
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );
        UserDetails userDetails=(CustomUserDetails)authentication.getPrincipal();

        return jwtService.generateToken(userDetails);
    }
}
