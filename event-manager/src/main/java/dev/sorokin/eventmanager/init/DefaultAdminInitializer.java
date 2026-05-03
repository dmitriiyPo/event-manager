package dev.sorokin.eventmanager.init;

import dev.sorokin.eventmanager.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultAdminInitializer {

    private final UserService userService;
    private final PasswordEncoder encoder;

    @Autowired
    public DefaultAdminInitializer(UserService userService, PasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }


    @PostConstruct
    public void init() {
        if (userService.existsByLogin("admin")) {
            return;
        }
        userService.createAdmin(
                "admin",
                30,
                encoder.encode("admin")
        );
    }



}
