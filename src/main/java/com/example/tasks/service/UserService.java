package com.example.tasks.service;


import com.example.tasks.entity.User;
import com.example.tasks.enums.Role;
import com.example.tasks.exception.EmailAlreadyExistsException;
import com.example.tasks.exception.InvalidEmailException;
import com.example.tasks.exception.InvalidPasswordException;
import com.example.tasks.exception.InvalidRoleException;
import com.example.tasks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Сервис для управления пользователями системы.
 * <p>
 * Обеспечивает регистрацию новых пользователей, создание администраторов,
 * валидацию учетных данных и обработку бизнес-правил для сущности {@link User}.
 * </p>
 *
 * @author AlinaSheveleva
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final int MIN_PASSWORD_LENGTH = 8;

    private static final String DIGIT_REGEX = ".*\\d.*";
    private static final String LETTER_REGEX = ".*[A-Za-z].*";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param email    электронная почта (должна соответствовать формату)
     * @param password пароль (не менее 8 символов, минимум 1 цифра и 1 буква)
     * @param roles    набор ролей пользователя (не может быть пустым)
     * @return зарегистрированный пользователь
     * @throws EmailAlreadyExistsException если email уже зарегистрирован
     * @throws InvalidEmailException       при невалидном формате email
     * @throws InvalidPasswordException    при нарушении требований к паролю
     * @throws InvalidRoleException        если не указаны роли
     */
    public User registerUser(String email, String password, Set<Role> roles) {
        validateEmail(email);
        validatePassword(password);
        validateRoles(roles);

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already exists: " + email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new EmailAlreadyExistsException("Email already exists: " + email);
        }
    }

    /**
     * Создает пользователя с ролью администратора.
     *
     * @param email    электронная почта
     * @param password пароль
     * @return пользователь с ролью ROLE_ADMIN
     */
    public User createAdmin(String email, String password) {
        return registerUser(email, password, Set.of(Role.ROLE_ADMIN));
    }

    /**
     * Создает обычного пользователя.
     *
     * @param email    электронная почта
     * @param password пароль
     * @return пользователь с ролью ROLE_USER
     */
    public User createUser(String email, String password) {
        return registerUser(email, password, Set.of(Role.ROLE_USER));
    }

    /**
     * Валидирует формат электронной почты.
     *
     * @param email проверяемый email
     * @throws InvalidEmailException если email:
     * - пустой
     * - не соответствует формату name@domain.com
     */
    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidEmailException("Email cannot be empty");
        }

        if (!EMAIL_REGEX.matcher(email).matches()) {
            throw new InvalidEmailException("Invalid email format: " + email);
        }
    }

    /**
     * Валидирует пароль по критериям:
     * <ul>
     *   <li>Не менее 8 символов</li>
     *   <li>Содержит минимум 1 цифру</li>
     *   <li>Содержит минимум 1 букву</li>
     * </ul>
     *
     * @param password проверяемый пароль
     * @throws InvalidPasswordException при нарушении любого из требований
     */
    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new InvalidPasswordException("Password cannot be empty");
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new InvalidPasswordException("Password must be at least "
                    + MIN_PASSWORD_LENGTH + " characters");
        }

        if (!password.matches(DIGIT_REGEX)) {
            throw new InvalidPasswordException("Password must contain at least one digit");
        }

        if (!password.matches(LETTER_REGEX)) {
            throw new InvalidPasswordException("Password must contain at least one letter");
        }
    }

    /**
     * Проверяет валидность набора ролей.
     *
     * @param roles набор ролей
     * @throws InvalidRoleException если:
     * - roles == null
     * - набор ролей пуст
     */
    private void validateRoles(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new InvalidRoleException("User must have at least one role");
        }
    }
}
