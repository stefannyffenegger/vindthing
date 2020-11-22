package ch.vindthing.repository;

import ch.vindthing.model.Item;
import ch.vindthing.model.Store;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StoreRepository extends MongoRepository<Store, String> {
  Optional<Store> findByName(String name);

  Optional<Store> findById(String id);

  Optional<Item> findByItemsId(String id);

  Boolean existsByName(String name);
}
