package dev.sorokin.eventmanager.dto.user.response;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserDtoResponse(

        @NotNull
        Long id,

        @NotBlank
        @Size(min = 5)
        String login,

        @Min(18)
        @NotNull
        Integer age,

        @NotBlank
        String role
){

}
