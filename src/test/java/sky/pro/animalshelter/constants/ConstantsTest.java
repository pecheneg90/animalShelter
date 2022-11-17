package sky.pro.animalshelter.constants;

import com.pengrad.telegrambot.model.Message;
import sky.pro.animalshelter.model.Animal;
import sky.pro.animalshelter.model.Report;
import sky.pro.animalshelter.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static sky.pro.animalshelter.model.Animal.AnimalTypes.DOG;

public interface ConstantsTest {

    String REGEX_BOT_MESSAGE = "([\\W+]+)(\\s)(\\+7\\d{3}[-.]?\\d{3}[-.]?\\d{4})(\\s)([a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+)";
    Animal.AnimalTypes TYPE = DOG;
    Long ANIMAL_ID = 1L;


    LocalDate SENT_DATE = LocalDate.now().minusDays(1);

    Long USER_ID_1 = 1L;
    Long USER_CHAT_ID_1 = 1L;
    String USER_NAME_1 = "Иван Иванов";
    String USER_PHONE_1 = "+71234567890";
    String USER_EMAIL_1 = "email1@email.com";
    User.UserStatus USER_STATUS_1 = User.UserStatus.GUEST;

    Long USER_ID_2 = 2L;
    Long USER_CHAT_ID_2 = 2L;
    String USER_NAME_2 = "Петр Петров";
    String USER_PHONE_2 = "+79288374234";
    String USER_EMAIL_2 = "email2@email.com";
    User.UserStatus USER_STATUS_2 = User.UserStatus.GUEST;

    String USER_MESSAGE_1 = "Иван Иванов +71234567890 email1@email.com";
    Message REPORT_MESSAGE = new Message();

    User.UserStatus USER_STATUS_3 = User.UserStatus.ADOPTER_ON_TRIAL;
    LocalDate TEST_TRIAL_DATE = LocalDate.of(2022,12,31);
    LocalDate START_TRIAL_DATE = LocalDate.now().minusDays(30);


    Long REPORT_ID_1 = 1L;
    String REPORT_TEXT_1 = "ОК";
    String FILE_PATH_1 = "https://ichef.bbci.co.uk/news/800/cpsprodpb/16620/production/_91408619_55df76d5-2245-41c1-8031-07a4da3f313f.jpg.webp";
    LocalDate SENT_DATE_1 = LocalDate.of(2022,12,10);
    Integer FILE_SIZE_1 = 1024;
    byte[] PREVIEW_1 = "f6d73k9r".getBytes(StandardCharsets.UTF_8);
    Report.ReportStatus REPORT_STATUS_1 = Report.ReportStatus.SENT;
    Long REPORT_ID_2 = 2L;
    String REPORT_TEXT_2 = "ОК2";
    String FILE_PATH_2 = "https://memepedia.ru/wp-content/uploads/2016/07/GaecXsgZG8Y.jpg";
    LocalDate SENT_DATE_2 = LocalDate.of(2022,4,8);
    Report.ReportStatus REPORT_STATUS_2 = Report.ReportStatus.ACCEPTED;
    String FILE_PATH_3 = null;
}
