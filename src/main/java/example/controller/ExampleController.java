package example.controller;


import example.entity.UserEntity;
import example.exception.UserAlreadyExistException;
import example.model.Credentials;
import example.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ExampleController {

    private final String secretWord = "secretWord";

    private final int expirationTime = 300_000;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody Credentials cred){
        if (userRepository.findByUsername(cred.getUsername()).isPresent()) {
            throw new UserAlreadyExistException("user already exist");
        }
        UserEntity user = new UserEntity();
        user.setUsername(cred.getUsername());
        user.setPassword(passwordEncoder.encode(cred.getPassword()));
        userRepository.save(user);
        return new ResponseEntity<>(getRespBodyWithToken(cred.getUsername()), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Credentials cred){
        log.warn(secretWord);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(cred.getUsername(), cred.getPassword())
        );
        return new ResponseEntity<>(getRespBodyWithToken(cred.getUsername()), HttpStatus.OK);
    }

    @GetMapping("/check")
    public String checkAuth(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return "Вы прошли все проверки " + username;
    }

    private Map<String,String> getRespBodyWithToken(String username){
        Map<String,String> body = new HashMap<>();
        Claims claims = Jwts.claims().setSubject(username);
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 1_000_000))
                .signWith(SignatureAlgorithm.HS512, secretWord)
                .compact();
        body.put("username", username);
        body.put("token", token);
        return body;
    }
}
