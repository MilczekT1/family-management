package pl.konradboniecki.budget.familymanagement.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.konradboniecki.budget.familymanagement.model.Family;

import java.util.Optional;

@Repository
public interface FamilyRepository extends MongoRepository<Family, String> {
   Optional<Family> findById(String id);

   Optional<Family> findByOwnerId(String ownerId);

   Family save(Family family);

   void deleteById(String id);

   boolean existsById(String id);
}
