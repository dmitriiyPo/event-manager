package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.converter.user.UsersEntityConverter;
import dev.sorokin.eventmanager.dto.user.request.SignUpUserRequest;
import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.enums.UserRole;
import dev.sorokin.eventmanager.model.User;
import dev.sorokin.eventmanager.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UsersEntityConverter usersEntityConverter;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UsersEntityConverter usersEntityConverter, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.usersEntityConverter = usersEntityConverter;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.REPEATABLE_READ,
            rollbackFor = {EntityExistsException.class}
    )
    public User registerUser(SignUpUserRequest signUpRequest) {
        if (userRepository.existsByLogin(signUpRequest.login())) {
            throw new EntityExistsException("User with login " + signUpRequest.login() + " already exists");
        }
        String hashedPassword = passwordEncoder.encode(signUpRequest.password());

        UserEntity userToSave = new UserEntity(
                null,
                signUpRequest.login(),
                hashedPassword,
                signUpRequest.age(),
                UserRole.USER.name()
        );

        UserEntity savedUser = userRepository.save(userToSave);
        return usersEntityConverter.toDomain(savedUser);
    }


    @Transactional(readOnly = true)
    public User findByLogin(String login) {
        UserEntity user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("User with login " + login + " not found"));
        return usersEntityConverter.toDomain(user);
    }


    @Transactional(readOnly = true)
    public User findUserById(Long userId) {
        UserEntity foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        return usersEntityConverter.toDomain(foundUser);
    }


    public void createAdmin(String login, Integer age, String password) {
        UserEntity admin = new UserEntity(null, login, password, age, UserRole.ADMIN.name());
        userRepository.save(admin);
    }


    public boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }
}
