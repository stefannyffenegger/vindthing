package ch.vindthing.repository;

import ch.vindthing.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ConfTokenRepository extends MongoRepository<Item, String> {
  Optional<Item> findByConfirmationToken(String confirmationToken);

  Optional<Item> findById(String id);
}
