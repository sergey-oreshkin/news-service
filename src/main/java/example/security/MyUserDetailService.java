package example.security;

import example.entity.UserEntity;
import example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * реализуем интерфейс UserDetailsService который будет создавать юзера для спринг секьюрити
 */
@Service
@RequiredArgsConstructor
public class MyUserDetailService implements UserDetailsService {

    private final UserRepository userRepository; // автовайрим наш репозиторий

    /**
     * @param username - это имя текущуго пользователя
     * @return объект UserDetails - это стандартный объект юзера для спринг секьюрити с котрым он умеет работать
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // получаем из БД юзера по имени, или бросаем исключение
        UserEntity user = userRepository.findПожалуйстаByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        // возвращаем объект UserDetails со вшитыми именем и паролем
        return new User(user.getUsername(), user.getPassword(), Collections.emptyList());
    }
}
