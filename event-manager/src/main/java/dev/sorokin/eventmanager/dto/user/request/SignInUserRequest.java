package dev.sorokin.eventmanager.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignInUserRequest(

        @NotBlank
        @Size(min = 5)
        String login,

        @NotBlank
        @Size(min = 5)
        String password
) {
}
