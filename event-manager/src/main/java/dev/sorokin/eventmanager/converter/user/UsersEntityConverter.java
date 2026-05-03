package dev.sorokin.eventmanager.converter.user;

import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.enums.UserRole;
import dev.sorokin.eventmanager.model.User;
import org.springframework.stereotype.Component;

@Component
public class UsersEntityConverter {

    public UserEntity toEntity(User user) {
        return new UserEntity(
                user.id(),
                user.login(),
                user.passwordHash(),
                user.age(),
                user.role().name()
        );
    }


    public User toDomain(UserEntity user) {
        return new User(
                user.getId(),
                user.getLogin(),
                user.getPassword(),
                user.getAge(),
                UserRole.valueOf(user.getRole())
        );
    }

}
