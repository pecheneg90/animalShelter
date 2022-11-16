package sky.pro.animalshelter.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sky.pro.animalshelter.model.Animal;
import sky.pro.animalshelter.model.User;
import sky.pro.animalshelter.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            tags = "Усыновители",
            summary = "Добавление нового усыновителя",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Добавляемый усыновитель\n" +
                            "\nОбязательные поля для добавления: name, phoneNumber, email, status, animal->type, startTrialDate, endTrialDate\n" +
                            "\nДоступные статусы: ADOPTER_ON_TRIAL, ADOPTER_TRIAL_FAILED, OWNER\n" +
                            "\nДоступные типы животных (ОБЯЗАТЕЛЬНО также выбирать в списке выше): DOG, CAT"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Новый усыновитель добавлен"
                    )
            }
    )
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user,
                                           @Parameter(description = "Тип животного")
                                           @RequestParam Animal.AnimalTypes type) {
        if (user != null) {
            User newUser = userService.createUserByVolunteer(user, type);
            return ResponseEntity.ok(newUser);
        } else return ResponseEntity.noContent().build();
    }

    @Operation(
            tags = "Усыновители",
            summary = "Редактирование данных пользователя",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Редактируемый пользователь\n" +
                            "\nПоля для редактирования: name, phoneNumber, email, status, animal->type, startTrialDate, endTrialDate\n" +
                            "\nДоступные статусы: GUEST, ADOPTER_ON_TRIAL, ADOPTER_TRIAL_FAILED, OWNER\n" +
                            "\nДоступные типы животных (ОБЯЗАТЕЛЬНО также выбирать в списке выше): DOG, CAT, NO_ANIMAL"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные пользователя изменены"
                    )
            }
    )
    @PutMapping
    public ResponseEntity<User> editUser(@RequestBody User user,
                                         @Parameter(description = "Тип животного")
                                         @RequestParam Animal.AnimalTypes type) {
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        User editedUser = userService.createUserByVolunteer(user, type);
        return ResponseEntity.ok(editedUser);
    }

    @Operation(
            tags = "Усыновители",
            summary = "Поиск пользователя по ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденный пользователь"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@Parameter(description = "ID пользователя")
                                        @PathVariable Long id) {
        User user = userService.getUserById(id);
        if (userService.getUserById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @Operation(
            tags = "Усыновители",
            summary = "Удаление пользователя из базы данных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь удален"
                    )
            }
    )
    @DeleteMapping("{id}")
    public ResponseEntity deleteUser(@Parameter(description = "ID пользователя")
                                     @PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            tags = "Усыновители",
            summary = "Получение списка всех пользователей",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список всех пользователей"
                    )
            }
    )
    @GetMapping("/all")
    public ResponseEntity<Collection<User>> getAllUsers() {
        Collection<User> users = userService.getAllUsers();
        if (users == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(users);
    }
}