package dev.sorokin.eventmanager.dto.user.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignUpUserRequest(

        @NotBlank
        @Size(min = 5)
        String login,

        @NotBlank
        @Size(min = 5)
        String password,

        @Min(18)
        @NotNull
        Integer age
) {
}
