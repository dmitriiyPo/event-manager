package dev.sorokin.eventmanager.security.jwt;

import dev.sorokin.eventmanager.dto.user.request.SignInUserRequest;
import dev.sorokin.eventmanager.model.User;
import dev.sorokin.eventmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenManager jwtTokenManager;
    private final UserService userService;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager, JwtTokenManager jwtTokenManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenManager = jwtTokenManager;
        this.userService = userService;
    }

    public String authenticateUser(SignInUserRequest signInRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.login(),
                        signInRequest.password()
                )
        );

        User user = userService.findByLogin(signInRequest.login());

        return jwtTokenManager.generateToken(user.login(), user.role().name());
    }


    public User getCurrentAuthenticatedUserOrThrow() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Authentication not present");
        }
        String login = authentication.getName();
        return userService.findByLogin(login);
    }
}
