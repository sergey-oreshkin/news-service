package example.controller;


import example.entity.UserEntity;
import example.exception.UserAlreadyExistException;
import example.model.Credentials;
import example.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.h2.security.auth.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Общий контроллер
 */

@RestController
@CrossOrigin(origins = "*") // разрешаем Cross Origin запросы с любых адресов(НЕБЕЗОПАСНО В РЕАЛЬНОМ ПРОЕКТЕ!)
@RequiredArgsConstructor
public class JwtExampleController {

    private final String secretWord = "secretWord"; //комбинация сомволов для шифрования jwt токена

    private final int expirationTime = 300_000; //количество миллисекунд через которое истечет срок действия токена

    private final UserRepository userRepository; // объект для запросов в базу данных

    private final PasswordEncoder passwordEncoder; // объект который умеет шифровать строки

    private final AuthenticationManager authenticationManager; //объект из Spring Security который может проверить аутентификацию

    /**
     * Cохраняет юзера в БД и возвращает токен в случае успеха
     *
     * @param cred - объект в который мапятся username и password
     * @return Map со значениями {"username", "token"}
     * @throws UserAlreadyExistException
     */
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody Credentials cred) {
        if (userRepository.findПожалуйстаByUsername(cred.getUsername()).isPresent()) { //проверяем что юзернэйм не занят
            throw new UserAlreadyExistException("User already exist");
        }
        // создаем объект юзера для записи в БД
        UserEntity user = new UserEntity();
        user.setUsername(cred.getUsername());
        user.setPassword(passwordEncoder.encode(cred.getPassword()));
        // сохраняем юзера в БД
        userRepository.save(user);
        // возвращаем username и token полученые в методе getRespBodyWithToken и хттп статус 200
        return new ResponseEntity<>(getRespBodyWithToken(cred.getUsername()), HttpStatus.OK);
    }

    /**
     * Проверяет username и password юзера и возвращает token в случае успеха
     *
     * @param cred - объект в который мапятся username и password
     * @return Map со значениями {"username", "token"}
     * @throws AuthenticationException
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Credentials cred) {
        /**
         *  проверяем аутентификацию средствами Spring Security
         *  если аутентификация не будет пройдена спринг бросит AuthenticationException и ответит кодом 403         *
         */
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(cred.getUsername(), cred.getPassword())
        );
        // возвращаем username и token полученые в методе getRespBodyWithToken и хттп статус 200
        return new ResponseEntity<>(getRespBodyWithToken(cred.getUsername()), HttpStatus.OK);
    }

    /**
     * Защищенный эндпоинт, попасть в него можно только имея валидный токен(настраивается в SecurityConfig)
     *
     * @return username
     */
    @GetMapping("/check")
    public ResponseEntity<String> checkAuth() {
        // Получаем объек аутентификации текущего запроса из SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Получаем имя пользователя из текущей аутентификации
        String username = authentication.getName();
        return new ResponseEntity<>("Вы прошли все проверки " + username, HttpStatus.OK);
    }

    /**
     * Собирает токен по заданным параметрам
     *
     * @param username
     * @return Map со значениями {"username", "token"}
     */
    private Map<String, String> getRespBodyWithToken(String username) {
        Map<String, String> body = new HashMap<>();
        // Claims и Jwts - классы из библиотеки jjwt
        //Claims это как бы тело токена, его полезная нагрузка. Записываем туда username
        Claims claims = Jwts.claims().setSubject(username);
        // получаем текущую дату и вычисляем дату истечения токена
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);
        // собираем токен в строку
        String token = Jwts.builder()
                .setClaims(claims) // устанавливает тело
                .setIssuedAt(now) // устанавливаем дату создания
                .setExpiration(expiration) // устанавливаем дату истечения
                .signWith(SignatureAlgorithm.HS512, secretWord) // указываем алгоритм шифрования и пароль для расшифровки
                .compact(); //собираем в строку
        body.put("username", username); // добавляем в мапу юзернэйм
        body.put("token", token); // добавляем в мапу токен
        return body;
    }
}
