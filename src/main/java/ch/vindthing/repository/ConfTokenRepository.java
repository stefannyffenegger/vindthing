package ch.vindthing.repository;

import ch.vindthing.model.ConfirmationToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ConfTokenRepository extends MongoRepository<ConfirmationToken, String> {
  Optional<ConfirmationToken> findByConfirmationToken(String confirmationToken);

  Optional<ConfirmationToken> findById(String id);
}
