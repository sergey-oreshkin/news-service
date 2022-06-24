package example.model;

import lombok.Value;

/**
 * Модель в которую мапятся пришедшие с фронта username и password
 */
@Value
public class Credentials {
    String username;
    String password;
}
