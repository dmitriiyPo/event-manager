package dev.sorokin.eventmanager.converter.user;

import dev.sorokin.eventmanager.dto.user.response.UserDtoResponse;
import dev.sorokin.eventmanager.enums.UserRole;
import dev.sorokin.eventmanager.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoConverter {

    public UserDtoResponse toDto(User user) {
        return new UserDtoResponse(
                user.id(),
                user.login(),
                user.age(),
                user.role().name()
        );
    }


    public User toDomain(UserDtoResponse user) {
        return new User(
                user.id(),
                user.login(),
                null,
                user.age(),
                UserRole.valueOf(user.role())
        );
    }

}
