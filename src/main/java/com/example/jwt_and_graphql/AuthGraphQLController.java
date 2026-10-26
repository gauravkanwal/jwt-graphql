package com.example.jwt_and_graphql;

import com.example.jwt_and_graphql.entity.User;
import com.example.jwt_and_graphql.repository.UserRepository;
import com.example.jwt_and_graphql.service.AuthService;
import com.example.jwt_and_graphql.service.CustomUserDetails;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AuthGraphQLController {
    private final AuthService authService;
    private final UserRepository userRepository;
    AuthGraphQLController(AuthService authService, UserRepository userRepository){
        this.authService=authService;
        this.userRepository=userRepository;
    }

    @MutationMapping
    public User register(
            @Argument String name,
            @Argument String username,
            @Argument String password
    ){
        return authService.register(name, username, password);
    }

    @MutationMapping
    public String login(
            @Argument String username,
            @Argument String password
    ){
        return authService.login(username,password);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public User myProfile(){
        Authentication authentication= SecurityContextHolder
                .getContext().
                getAuthentication();

        CustomUserDetails userDetails=(CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser();
    }

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> allUsers(){
        return userRepository.findAll();
    }


}
