package sky.pro.animalshelter.service;

import sky.pro.animalshelter.model.Animal;

public interface AnimalService {
    Animal getAnimalByName(Animal.AnimalTypes type);
}