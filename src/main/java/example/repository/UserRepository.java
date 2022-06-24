package example.repository;

import example.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Репозиторий Hibernate для crud операций с юзерами в БД
 */
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findПожалуйстаByUsername(String username);
}
