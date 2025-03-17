package com.example.tasks.controller;


import com.example.tasks.dto.AuthRequest;
import com.example.tasks.dto.AuthResponse;
import com.example.tasks.dto.RegisterRequest;
import com.example.tasks.entity.User;
import com.example.tasks.service.JwtUtil;
import com.example.tasks.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * Контроллер для обработки запросов аутентификации и регистрации пользователей.
 * <p>
 * Обеспечивает endpoints для входа в систему, регистрации обычных пользователей и администраторов.
 * Использует JWT для аутентификации и Spring Security для управления доступом.
 * </p>
 *
 * @author AlinaSheveleva
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    /**
     * Аутентифицирует пользователя и возвращает JWT-токен.
     *
     * @param request DTO с учетными данными (email и пароль)
     * @return ответ с JWT-токеном в формате {@link AuthResponse}
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    /**
     * Регистрирует нового пользователя с ролью ROLE_USER.
     *
     * @param request DTO с регистрационными данными (email и пароль)
     * @return созданный пользователь с статусом 201 Created
     */
    @PostMapping("/register/user")
    public ResponseEntity<User> registerUser(@Valid @RequestBody RegisterRequest request) {
        User user = userService.createUser(request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /**
     * Регистрирует нового пользователя с ролью ROLE_ADMIN.
     *
     * @param request DTO с регистрационными данными (email и пароль)
     * @return созданный администратор с статусом 201 Created
     */
    @PostMapping("/register/admin")
    public ResponseEntity<User> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        User user = userService.createAdmin(request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}