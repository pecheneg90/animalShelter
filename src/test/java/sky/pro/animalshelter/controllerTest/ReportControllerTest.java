package sky.pro.animalshelter.controllerTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sky.pro.animalshelter.constants.ConstantsTest;
import sky.pro.animalshelter.controller.ReportController;
import sky.pro.animalshelter.model.Animal;
import sky.pro.animalshelter.model.Report;
import sky.pro.animalshelter.model.User;
import sky.pro.animalshelter.repository.AnimalRepository;
import sky.pro.animalshelter.repository.ReportRepository;
import sky.pro.animalshelter.repository.UserRepository;
import sky.pro.animalshelter.service.impl.AnimalServiceImpl;
import sky.pro.animalshelter.service.impl.ReportServiceImpl;
import sky.pro.animalshelter.service.impl.UserServiceImpl;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReportController.class)
public class ReportControllerTest {
    private final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private Report report1;
    private Report report2;
    private User user1;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReportRepository reportRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AnimalRepository animalRepository;

    @SpyBean
    private ReportServiceImpl reportService;
    @SpyBean
    private UserServiceImpl userService;
    @SpyBean
    private AnimalServiceImpl animalService;

    @InjectMocks
    private ReportController reportController;

    @BeforeEach
    public void setUp() throws Exception {

        Animal animal = new Animal(ConstantsTest.ANIMAL_ID, ConstantsTest.TYPE);

        user1 = new User(ConstantsTest.USER_NAME_1, ConstantsTest.USER_PHONE_1, ConstantsTest.USER_EMAIL_1);
        user1.setId(ConstantsTest.USER_ID_1);
        user1.setChatId(ConstantsTest.USER_CHAT_ID_1);
        user1.setStatus(User.UserStatus.ADOPTER_ON_TRIAL);
        user1.setAnimal(animal);

        report1 = new Report(ConstantsTest.REPORT_ID_1, ConstantsTest.USER_ID_1,
                ConstantsTest.REPORT_TEXT_1, ConstantsTest.FILE_PATH_1, ConstantsTest.FILE_SIZE_1,
                ConstantsTest.PREVIEW_1, ConstantsTest.SENT_DATE_1, ConstantsTest.REPORT_STATUS_1);
        report2 = new Report(ConstantsTest.REPORT_ID_1, ConstantsTest.USER_ID_1,
                ConstantsTest.REPORT_TEXT_1, ConstantsTest.FILE_PATH_1, ConstantsTest.FILE_SIZE_1,
                ConstantsTest.PREVIEW_1, ConstantsTest.SENT_DATE_1, ConstantsTest.REPORT_STATUS_2);
    }

    @Test
    public void getReportPreviewTest() throws Exception {

        when(reportRepository.findById(any(Long.class))).thenReturn(Optional.of(report1));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/report/1/preview")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE));
        verify(reportRepository, times(1)).findById(ConstantsTest.REPORT_ID_1);
    }

    @Test
    public void shouldThrowExceptionIfGetReportImageIsNotSuccess() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(reportRepository.findById(any(Long.class))).thenReturn(null);
        Assertions.assertThrows(NullPointerException.class, () -> reportController.getReportImage(ConstantsTest.REPORT_ID_1, response));
    }

    @Test
    public void getReportTextTest() throws Exception {
        when(reportRepository.findById(any(Long.class))).thenReturn(Optional.of(report1));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/report/1")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ConstantsTest.REPORT_ID_1))
                .andExpect(jsonPath("$.clientId").value(ConstantsTest.USER_ID_1))
                .andExpect(jsonPath("$.reportText").value(ConstantsTest.REPORT_TEXT_1))
                .andExpect(jsonPath("$.filePath").value(ConstantsTest.FILE_PATH_1))
                .andExpect(jsonPath("$.sentDate").value(ConstantsTest.SENT_DATE_1.toString()))
                .andExpect(jsonPath("$.status").value(ConstantsTest.REPORT_STATUS_1.toString()));
    }

    @Test
    public void getAllUserReports() throws Exception {

        when(reportRepository.findByUserId(any(Long.class))).thenReturn(Optional.of(List.of(report1)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/report/getAll/1")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(ConstantsTest.REPORT_ID_1))
                .andExpect(jsonPath("$[0].clientId").value(ConstantsTest.USER_ID_1))
                .andExpect(jsonPath("$[0].reportText").value(ConstantsTest.REPORT_TEXT_1))
                .andExpect(jsonPath("$[0].filePath").value(ConstantsTest.FILE_PATH_1))
                .andExpect(jsonPath("$[0].fileSize").value(ConstantsTest.FILE_SIZE_1))
                .andExpect(jsonPath("$[0].sentDate").value(ConstantsTest.SENT_DATE_1.toString()))
                .andExpect(jsonPath("$[0].status").value(ConstantsTest.REPORT_STATUS_1.toString()));
    }

    @Test
    public void editReportTest() throws Exception {
        when(reportRepository.findById(any(Long.class))).thenReturn(Optional.of(report2));
        logger.info("Report: " + reportRepository.findById(ConstantsTest.REPORT_ID_1).get());
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/report?id=1&status=ACCEPTED")
                )
                .andExpect(status().isOk());
    }
}
