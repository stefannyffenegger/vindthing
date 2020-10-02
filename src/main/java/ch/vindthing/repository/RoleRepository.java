package ch.vindthing.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import ch.vindthing.models.ERole;
import ch.vindthing.models.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}
