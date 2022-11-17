package sky.pro.animalshelter.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "client")
@Setter
@Getter
@NoArgsConstructor
public class User {


    public enum UserStatus {

        GUEST,
        ADOPTER_ON_TRIAL,
        ADOPTER_TRIAL_FAILED,
        OWNER

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @ManyToOne
    @JoinColumn(name = "animal_type", referencedColumnName = "type")
    private Animal animal;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Report> reportList;

    @Column(name = "start_trial_date")
    private LocalDate startTrialDate;

    @Column(name = "end_trial_date")
    private LocalDate endTrialDate;

    public User(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public User(String name, String phoneNumber, String email, Animal animal) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.animal = animal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getId().equals(user.getId()) && getChatId().equals(user.getChatId()) && getName().equals(user.getName()) && getPhoneNumber().equals(user.getPhoneNumber()) && getEmail().equals(user.getEmail()) && getStatus() == user.getStatus() && getAnimal().equals(user.getAnimal()) && reportList.equals(user.reportList) && getStartTrialDate().equals(user.getStartTrialDate()) && getEndTrialDate().equals(user.getEndTrialDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getChatId(), getName(), getPhoneNumber(), getEmail(), getStatus(), getAnimal(), reportList, getStartTrialDate(), getEndTrialDate());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", animal=" + animal +
                ", startTrialDate=" + startTrialDate +
                ", endTrialDate=" + endTrialDate +
                '}';
    }
}