package sky.pro.animalshelter.service.impl;

import org.springframework.stereotype.Service;
import sky.pro.animalshelter.model.Animal;
import sky.pro.animalshelter.repository.AnimalRepository;
import sky.pro.animalshelter.service.AnimalService;

@Service
public class AnimalServiceImpl implements AnimalService {

    private final AnimalRepository animalRepository;

    public AnimalServiceImpl(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    @Override
    public Animal getAnimalByName(Animal.AnimalTypes type) {

        return animalRepository.getAnimalBy(type);
    }
}