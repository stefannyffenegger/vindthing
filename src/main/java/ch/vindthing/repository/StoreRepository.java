package ch.vindthing.repository;

import ch.vindthing.model.Store;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StoreRepository extends MongoRepository<Store, String> {
  Optional<Store> findByName(String name);

  Boolean existsByName(String name);
}
