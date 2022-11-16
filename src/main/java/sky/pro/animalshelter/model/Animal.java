package sky.pro.animalshelter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name="animal")
@Setter
@Getter
@NoArgsConstructor
public class Animal implements Serializable {

    public enum AnimalTypes {

        DOG,
        CAT,
        NO_ANIMAL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AnimalTypes type;

    @OneToMany(mappedBy = "animal",cascade = CascadeType.ALL)
    @JsonIgnore
    public Set<User> users;


    public Animal(long id, AnimalTypes type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Animal)) return false;
        Animal animal = (Animal) o;
        return id.equals(animal.id) && type == animal.type && Objects.equals(users, animal.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, users);
    }

    @Override
    public String toString() {
        return "Animal{" +
                "id=" + id +
                ", type=" + type +
                ", users=" + users +
                '}';
    }
}