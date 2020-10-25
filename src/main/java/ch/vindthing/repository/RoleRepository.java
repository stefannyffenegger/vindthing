package ch.vindthing.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import ch.vindthing.model.ERole;
import ch.vindthing.model.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}
