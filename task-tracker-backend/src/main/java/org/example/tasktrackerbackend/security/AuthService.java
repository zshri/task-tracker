package org.example.tasktrackerbackend.security;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbackend.component.EmailTaskProducer;
import org.example.tasktrackerbackend.model.EmailTask;
import org.example.tasktrackerbackend.model.User;
import org.example.tasktrackerbackend.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Сервис для аутентификации и регистрации пользователей.
 * Предоставляет методы для аутентификации пользователей и создания новых учетных записей.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final EmailTaskProducer emailTaskProducer;


    /**
     * Аутентифицирует пользователя по email и паролю.
     *
     * @param authRequest запрос на аутентификацию, содержащий email и пароль
     * @return ответ с JWT токеном доступа
     * @throws BadCredentialsException если предоставленные учетные данные неверны
     */
    public AuthResponse authenticate(AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
            log.info("User with email: {} - successfully authenticated", authRequest.getEmail());
        } catch (BadCredentialsException e) {
            log.info("User with email: {} - not authenticated", authRequest.getEmail());
            throw new BadCredentialsException("Bad credentials");
        }
        User user = userRepository.findByEmail(authRequest.getEmail());
        String accessToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    /**
     * Регистрирует нового пользователя.
     *
     * @param registerRequest запрос на регистрацию, содержащий данные нового пользователя
     * @return ответ с JWT токеном доступа
     */
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(bCryptPasswordEncoder.encode(registerRequest.getPassword()))
                .build();

        userRepository.save(user);

        log.info("User successfully saved in database -> {}", registerRequest.getUsername());

        EmailTask welcomeEmail = new EmailTask(registerRequest.getEmail(), "Добро пожаловать!", "Спасибо за регистрацию, " + registerRequest.getUsername() + "!");
        emailTaskProducer.sendEmailTask(welcomeEmail);

        String accessToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .build();
    }

}
