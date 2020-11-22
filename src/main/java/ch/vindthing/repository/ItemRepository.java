package ch.vindthing.repository;

import ch.vindthing.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

@Deprecated
public interface ItemRepository extends MongoRepository<Item, String> {
  Optional<Item> findByName(String name);

  Optional<Item> findById(String id);

  Boolean existsByName(String name);
}
