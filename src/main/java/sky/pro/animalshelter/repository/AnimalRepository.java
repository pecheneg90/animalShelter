package sky.pro.animalshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sky.pro.animalshelter.model.Animal;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Animal.AnimalTypes> {

    @Query("SELECT a FROM Animal a WHERE a.type = ?1")
    Animal getAnimalBy(Animal.AnimalTypes type);
}