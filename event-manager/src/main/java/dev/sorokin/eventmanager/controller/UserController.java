package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.converter.user.UserDtoConverter;
import dev.sorokin.eventmanager.dto.user.request.SignInUserRequest;
import dev.sorokin.eventmanager.dto.user.request.SignUpUserRequest;
import dev.sorokin.eventmanager.security.jwt.JwtTokenResponse;
import dev.sorokin.eventmanager.dto.user.response.UserDtoResponse;
import dev.sorokin.eventmanager.security.jwt.AuthenticationService;
import dev.sorokin.eventmanager.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserDtoConverter userDtoConverter;
    private final AuthenticationService authenticationService;

    @Autowired
    public UserController(UserService userService, UserDtoConverter userDtoConverter, AuthenticationService authenticationService) {
        this.userService = userService;
        this.userDtoConverter = userDtoConverter;
        this.authenticationService = authenticationService;
    }


    @SecurityRequirements()
    @PostMapping
    public ResponseEntity<UserDtoResponse> registerUser(@RequestBody @Valid SignUpUserRequest signUpRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userDtoConverter.toDto(userService.registerUser(signUpRequest)));
    }

    @SecurityRequirements()
    @PostMapping("/auth")
    public ResponseEntity<JwtTokenResponse> authenticate(@RequestBody @Valid SignInUserRequest signInRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new JwtTokenResponse(authenticationService.authenticateUser(signInRequest)));
    }


    @GetMapping("/{userId}")
    public ResponseEntity<UserDtoResponse> findUserById(@PathVariable("userId") Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDtoConverter.toDto(userService.findUserById(userId)));
    }

}
