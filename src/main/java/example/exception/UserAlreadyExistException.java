package example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение для случая когда при регистрации имя пользователя уже занято
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // сообщаем спрингу что при этом исключении нужно отвечать хттп кодом 400
public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
