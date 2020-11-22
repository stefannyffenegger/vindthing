package ch.vindthing.repository;

import java.util.Optional;

import ch.vindthing.model.Item;
import ch.vindthing.model.Store;
import org.springframework.data.mongodb.repository.MongoRepository;

import ch.vindthing.model.User;

public interface UserRepository extends MongoRepository<User, String> {
  Optional<User> findByName(String name);

  Optional<User> findByEmail(String email);

  Boolean existsByName(String name);

  Boolean existsByEmail(String email);
}
