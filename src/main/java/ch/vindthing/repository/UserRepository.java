package ch.vindthing.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import ch.vindthing.models.User;

public interface UserRepository extends MongoRepository<User, String> {
  Optional<User> findByName(String name);

  Optional<User> findByEmail(String email);

  Boolean existsByName(String name);

  Boolean existsByEmail(String email);
}
