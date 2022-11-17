package sky.pro.animalshelter.controllerTest;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sky.pro.animalshelter.constants.ConstantsTest;
import sky.pro.animalshelter.controller.ReportController;
import sky.pro.animalshelter.controller.UserController;
import sky.pro.animalshelter.model.User;
import sky.pro.animalshelter.repository.AnimalRepository;
import sky.pro.animalshelter.repository.UserRepository;
import sky.pro.animalshelter.service.impl.AnimalServiceImpl;
import sky.pro.animalshelter.service.impl.UserServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {


    private User user1;
    private User user2;
    private JSONObject userObject1;
    private JSONObject userObject2;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AnimalRepository animalRepository;

    @SpyBean
    private UserServiceImpl userService;

    @SpyBean
    private AnimalServiceImpl animalService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() throws Exception {
        userObject2 = new JSONObject();
        userObject1 = new JSONObject();
        userObject1.put("id", ConstantsTest.USER_ID_1);
        userObject1.put("chat_id", ConstantsTest.USER_CHAT_ID_1);
        userObject1.put("name", ConstantsTest.USER_NAME_1);
        userObject1.put("phoneNumber", ConstantsTest.USER_PHONE_1);
        userObject1.put("email", ConstantsTest.USER_EMAIL_1);

        user1 = new User(ConstantsTest.USER_NAME_1, ConstantsTest.USER_PHONE_1, ConstantsTest.USER_EMAIL_1);
        user1.setId(ConstantsTest.USER_ID_1);
        user1.setChatId(ConstantsTest.USER_CHAT_ID_1);

        user2 = new User(ConstantsTest.USER_NAME_2, ConstantsTest.USER_PHONE_2, ConstantsTest.USER_EMAIL_2);
        user2.setId(ConstantsTest.USER_ID_2);
        user2.setChatId(ConstantsTest.USER_CHAT_ID_2);
        user2.setStatus(ConstantsTest.USER_STATUS_2);
    }

    @Test
    public void testShouldCreateUser() throws Exception {
        when(userRepository.save(any(User.class))).thenReturn(user1);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/user?type=DOG")
                        .content(userObject1.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ConstantsTest.USER_ID_1))
                .andExpect(jsonPath("$.chatId").value(ConstantsTest.USER_CHAT_ID_1))
                .andExpect(jsonPath("$.name").value(ConstantsTest.USER_NAME_1))
                .andExpect(jsonPath("$.phoneNumber").value(ConstantsTest.USER_PHONE_1))
                .andExpect(jsonPath("$.email").value(ConstantsTest.USER_EMAIL_1));
    }

    @Test
    public void testShouldEditUser() throws Exception {
        userObject2.put("id", ConstantsTest.USER_ID_1);
        userObject2.put("chat_id", ConstantsTest.USER_CHAT_ID_1);
        userObject2.put("name", ConstantsTest.USER_NAME_1);
        userObject2.put("phoneNumber", ConstantsTest.USER_PHONE_1);
        userObject2.put("email", ConstantsTest.USER_EMAIL_2);

        user1.setEmail(ConstantsTest.USER_EMAIL_2);
        when(userRepository.save(any(User.class))).thenReturn(user1);
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/user?chatId=123&type=DOG&status=GUEST")
                        .content(userObject2.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ConstantsTest.USER_ID_1))
                .andExpect(jsonPath("$.chatId").value(ConstantsTest.USER_CHAT_ID_1))
                .andExpect(jsonPath("$.name").value(ConstantsTest.USER_NAME_1))
                .andExpect(jsonPath("$.phoneNumber").value(ConstantsTest.USER_PHONE_1))
                .andExpect(jsonPath("$.email").value(ConstantsTest.USER_EMAIL_2));
    }

    @Test
    public void testShouldDeleteUser() throws Exception {
        doNothing().when(userRepository).deleteById(any(Long.class));
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/user/123")
                )
                .andExpect(status().isOk());
    }

    @Test
    public void testShouldGetUser() throws Exception {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user1));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/user/123")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ConstantsTest.USER_ID_1))
                .andExpect(jsonPath("$.chatId").value(ConstantsTest.USER_CHAT_ID_1))
                .andExpect(jsonPath("$.name").value(ConstantsTest.USER_NAME_1))
                .andExpect(jsonPath("$.phoneNumber").value(ConstantsTest.USER_PHONE_1))
                .andExpect(jsonPath("$.email").value(ConstantsTest.USER_EMAIL_1));
    }

    @Test
    public void testShouldGetAllUsers() throws Exception {
        List<User> userList = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(userList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/user/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(ConstantsTest.USER_ID_1))
                .andExpect(jsonPath("$[1].id").value(ConstantsTest.USER_ID_2))
                .andExpect(jsonPath("$[0].name").value(ConstantsTest.USER_NAME_1))
                .andExpect(jsonPath("$[1].name").value(ConstantsTest.USER_NAME_2));
    }
}