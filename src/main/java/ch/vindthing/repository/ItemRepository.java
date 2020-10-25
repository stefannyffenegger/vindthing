package ch.vindthing.repository;

import ch.vindthing.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ItemRepository extends MongoRepository<Item, String> {
  Optional<Item> findByName(String name);

  Boolean existsByName(String name);
}
