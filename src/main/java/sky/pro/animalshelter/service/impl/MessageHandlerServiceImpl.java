package sky.pro.animalshelter.service.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sky.pro.animalshelter.constants.Constants;
import sky.pro.animalshelter.listener.AnimalShelterBotUpdatesListener;
import sky.pro.animalshelter.model.Report;
import sky.pro.animalshelter.model.User;
import sky.pro.animalshelter.service.MessageHandlerService;
import sky.pro.animalshelter.service.ReportService;
import sky.pro.animalshelter.service.UserService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class MessageHandlerServiceImpl implements MessageHandlerService {

    private static final Logger logger = LoggerFactory.getLogger(AnimalShelterBotUpdatesListener.class);

    private final TelegramBot animalShelterBot;
    private final UserService userService;
    private final ReportService reportService;
    private static boolean registrationRequired = false;
    private static int sendingReportStatus = 0;
    // 1 - пользователь нажал кнопку "отправить отчет",
    // 2 - пользователь отправил текст,
    // 3 - пользователь отправил фото.
    private static String reportText;

    public MessageHandlerServiceImpl(TelegramBot animalShelterBot,
                                     UserService userService,
                                     ReportService reportService) {
        this.animalShelterBot = animalShelterBot;
        this.userService = userService;
        this.reportService = reportService;
    }

    @Override
    public void handleMessage(Message inputMessage, long chatId) {
        if (inputMessage.text() != null) {
            switch (inputMessage.text()) {

                // общие кнопки

                case Constants.START:
                    sendMessage(chatId, Constants.GREETINGS, chooseShelter());
                    break;
                case Constants.BACK_TO_CHOOSE_ANIMAL:
                    sendMessage(chatId, Constants.CHOOSE_OPTION, chooseShelter());
                    break;
                case Constants.CONTACT:
                    if (userService.getUserByChatId(chatId) != null && !userService.getUserByChatId(chatId).getStatus().equals(User.UserStatus.GUEST)) {
                        sendMessage(chatId, "Если вы хотите изменить свои контактные данные, пожалуйста, нажмите кнопку \"Позвать волонтера\". ");
                    } else {
                        sendMessage(chatId, Constants.CONTACT);
                        registrationRequired = true;
                    }
                    break;
                case Constants.CALL_VOLUNTEER_CMD:
                    String userContact = inputMessage.chat().username();
                    if (userContact == null) {
                        sendMessage(chatId, "Пожалуйста, отправьте ваш номер телефона формате +79991234567 без пробелов.");
                    } else {
                        sendMessage(chatId, Constants.CALL_VOLUNTEER_TEXT);
                        sendMessage(Constants.VOLUNTEERS_CHAT_ID, "Пожалуйста, свяжитесь с пользователем: https://t.me/" + userContact);
                    }
                    break;
                case Constants.DOCUMENTS_CMD:
                    sendMessage(chatId, Constants.DOCUMENTS_TEXT);
                    break;
                case Constants.REFUSAL_REASONS_CMD:
                    sendMessage(chatId, Constants.REFUSAL_REASONS_TEXT);
                    break;
                case Constants.SEND_REPORT_CMD:
                    sendingReportStatus = 1;
                    if (userService.adopterOnTrialExists(chatId)) {
                        if (!reportService.reportWasSentToday(LocalDate.now(), userService.getUserByChatId(chatId).getId())) {
                            sendMessage(chatId, Constants.REPORT_FORM);
                        } else {
                            sendMessage(chatId, "Вы уже направляли сегодня отчет.");
                        }
                    } else {
                        sendMessage(chatId, """
                                Вероятно, вас нет в моей базе данных усыновителей питомцев.\s

                                Пожалуйста, нажмите в меню кнопку "Позвать волонтера" и мы обязательно с вами свяжемся.""");
                    }
                    break;
                case Constants.UNKNOWN_FILE:
                    sendMessage(chatId, Constants.UNKNOWN_FILE);
                    break;

                //команды приют собак

                case Constants.DOG_SHELTER_CMD:
                case Constants.BACK_TO_DOG_START_MENU_CMD:
                    sendMessage(chatId, "Добро пожаловать в приют для собак!\n" + Constants.CHOOSE_OPTION, dogStartMenuButtons());
                    break;
                case Constants.DOG_SHELTER_INFO_CMD:
                    sendMessage(chatId, Constants.CHOOSE_OPTION, dogShelterInfoButtons());
                    break;
                case Constants.DOG_ABOUT_US_CMD:
                    sendMessage(chatId, Constants.DOG_ABOUT_US_TEXT);
                    break;
                case Constants.DOG_WORKING_HOURS_CMD:
                    sendMessage(chatId, Constants.DOG_WORKING_HOURS_TEXT);
                    break;
                case Constants.DOG_SECURITY_CONTACT_CMD:
                    sendMessage(chatId, Constants.DOG_SECURITY_CONTACT_TEXT);
                    break;
                case Constants.DOG_SAFETY_RECOMMENDATION_CMD:
                    sendMessage(chatId, Constants.DOG_SAFETY_RECOMMENDATION_TEXT);
                    break;
                case Constants.HOW_TO_TAKE_DOG_CMD:
                case Constants.BACK_TO_DOG_RECOMMENDATION_MENU_CMD:
                    sendMessage(chatId, Constants.CHOOSE_OPTION, dogRecommendationButtons());
                    break;
                case Constants.MEET_THE_DOG_CMD:
                    sendMessage(chatId, Constants.MEET_THE_DOG_TEXT);
                    break;
                case Constants.DOG_TRANSPORTING_AND_ADVICE_CMD:
                case Constants.BACK_TO_DOG_TRANSPORT_AND_ADVICE_MENU_CMD:
                    sendMessage(chatId, Constants.CHOOSE_OPTION, dogTransportAndAdviceDogButtons());
                    break;
                case Constants.DOG_TRANSPORTING_CMD:
                    sendMessage(chatId, Constants.DOG_TRANSPORTING_TEXT);
                    break;
                case Constants.DOG_ADVICE_CMD:
                    sendMessage(chatId, Constants.DOG_COMMON_ADVICE_TEXT, dogSpecificAdviceMenu());
                    break;
                case Constants.ADVICE_FOR_PUPPY_CMD:
                    sendMessage(chatId, Constants.ADVICE_FOR_PUPPY_TEXT);
                    break;
                case Constants.ADVICE_FOR_ADULT_DOG_CMD:
                    sendMessage(chatId, Constants.ADVICE_FOR_ADULT_DOG_TEXT);
                    break;
                case Constants.ADVICE_FOR_SPECIAL_DOG_CMD:
                    sendMessage(chatId, Constants.ADVICE_FOR_SPECIAL_DOG_TEXT);
                    break;
                case Constants.CYNOLOGIST_CMD:
                case Constants.BACK_TO_CYNOLOGIST_MENU_CMD:
                    sendMessage(chatId, Constants.CHOOSE_OPTION, cynologistMenu());
                    break;
                case Constants.CYNOLOGIST_ADVICE_CMD:
                    sendMessage(chatId, Constants.CYNOLOGIST_ADVICE_TEXT);
                    break;
                case Constants.CYNOLOGIST_CONTACTS_CMD:
                    sendMessage(chatId, Constants.CYNOLOGIST_CONTACTS_TEXT);
                    break;

                //команды приют кошек

                case Constants.CAT_SHELTER_CMD:
                case Constants.BACK_TO_CAT_START_MENU_CMD:
                    sendMessage(chatId, "Добро пожаловать в приют для кошек!\n" + Constants.CHOOSE_OPTION, catStartMenuButtons());
                    break;
                case Constants.CAT_SHELTER_INFO_CMD:
                    sendMessage(chatId, Constants.CHOOSE_OPTION, catShelterInfoButtons());
                    break;
                case Constants.CAT_ABOUT_US_CMD:
                    sendMessage(chatId, Constants.CAT_ABOUT_US_TEXT);
                    break;
                case Constants.CAT_WORKING_HOURS_CMD:
                    sendMessage(chatId, Constants.CAT_WORKING_HOURS_TEXT);
                    break;
                case Constants.CAT_SECURITY_CONTACT_CMD:
                    sendMessage(chatId, Constants.CAT_SECURITY_CONTACT_TEXT);
                    break;
                case Constants.CAT_SAFETY_RECOMMENDATION_CMD:
                    sendMessage(chatId, Constants.CAT_SAFETY_RECOMMENDATION_TEXT);
                    break;
                case Constants.HOW_TO_TAKE_CAT_CMD:
                case Constants.BACK_TO_CAT_RECOMMENDATION_MENU_CMD:
                    sendMessage(chatId, Constants.CHOOSE_OPTION, catRecommendationButtons());
                    break;
                case Constants.MEET_THE_CAT_CMD:
                    sendMessage(chatId, Constants.MEET_THE_CAT_TEXT);
                    break;
                case Constants.CAT_TRANSPORTING_AND_ADVICE_CMD:
                case Constants.BACK_TO_CAT_TRANSPORT_AND_ADVICE_MENU_CMD:
                    sendMessage(chatId, Constants.CHOOSE_OPTION, catTransportAndAdviceDogButtons());
                    break;
                case Constants.CAT_TRANSPORTING_CMD:
                    sendMessage(chatId, Constants.CAT_TRANSPORTING_TEXT);
                    break;
                case Constants.CAT_ADVICE_CMD:
                    sendMessage(chatId, Constants.CAT_COMMON_ADVICE_TEXT, catSpecificAdviceMenu());
                    break;
                case Constants.ADVICE_FOR_KITTEN_CMD:
                    sendMessage(chatId, Constants.ADVICE_FOR_KITTEN_TEXT);
                    break;
                case Constants.ADVICE_FOR_ADULT_CAT_CMD:
                    sendMessage(chatId, Constants.ADVICE_FOR_ADULT_CAT_TEXT);
                    break;
                case Constants.ADVICE_FOR_SPECIAL_CAT_CMD:
                    sendMessage(chatId, Constants.ADVICE_FOR_SPECIAL_CAT_TEXT);
                    break;
                case Constants.FELINOLOGIST_CMD:
                case Constants.BACK_TO_FELINOLOGIST_MENU_CMD:
                    sendMessage(chatId, Constants.CHOOSE_OPTION, felinologystMenu());
                    break;
                case Constants.FELINOLOGIST_ADVICE_CMD:
                    sendMessage(chatId, Constants.FELINOLOGIST_ADVICE_TEXT);
                    break;
                case Constants.FELINOLOGIST_CONTACTS_CMD:
                    sendMessage(chatId, Constants.FELINOLOGIST_CONTACTS_TEXT);
                    break;

                default:
                    if (inputMessage.text().startsWith("+") && inputMessage.text().length() == 12) {
                        sendMessage(Constants.VOLUNTEERS_CHAT_ID, "Пожалуйста, свяжитесь с пользователем: https://t.me/" + inputMessage.text());
                        sendMessage(chatId, "Спасибо, волонтер свяжется с вами в ближайшее время.");
                    } else if (registrationRequired) {
                        logger.info("Registration data has been sent");
                        sendMessage(chatId, userService.registrationUser(inputMessage));
                        sendMessage(Constants.VOLUNTEERS_CHAT_ID, "Пользователь " + userService.getUserByChatId(chatId).getName() +
                                " (" + userService.getUserByChatId(chatId).getPhoneNumber() + ") оставил контакты для связи.");
                        registrationRequired = false;
                    } else if (sendingReportStatus == 1) {
                        sendMessage(chatId, Constants.PHOTO_REPORT_REQUIRED);
                        reportText = inputMessage.text();
                        sendingReportStatus = 2;
                    } else {
                        sendMessage(chatId, Constants.INVALID_CMD);
                    }
            }

        } else if (inputMessage.photo() != null && sendingReportStatus == 2) {
            File file = getFile(inputMessage);
            String filePath = animalShelterBot.getFullFilePath(file);
            Integer fileSize = file.fileSize();

            try {

                Report report = reportService.handlePhoto(inputMessage, fileSize, filePath, reportText);
                sendMessage(chatId, "Спасибо! Ваш отчет отправлен волонтеру на проверку.");

                java.io.File localFile = reportService.downloadFile(filePath, inputMessage);

                User user = userService.getUserByChatId(chatId);

                sendMessage(Constants.VOLUNTEERS_CHAT_ID, "Вам поступил отчет на проверку: \n"
                        + "\n\uD83D\uDFE2Пользователь: "
                        + "\nId: " + user.getId()
                        + "\nИмя: " + user.getName()
                        + "\nИспытательный срок: " + user.getStartTrialDate() + " - " + user.getEndTrialDate()
                        + "\n\n\uD83D\uDFE2Отчет: "
                        + "\nId: " + report.getId()
                        + "\nНомер: " + reportService.countUserReports(user.getId())
                        + "\nСодержание: " + report.getReportText());

                sendDocument(Constants.VOLUNTEERS_CHAT_ID, localFile);

            } catch (Exception e) {
                e.printStackTrace();
            }

            sendingReportStatus = 0;

        } else {
            sendMessage(chatId, Constants.UNKNOWN_FILE);
        }
    }

    private File getFile(Message inputMessage) {
        List<PhotoSize> photos = List.of(inputMessage.photo());
        PhotoSize photo = photos.stream()
                .max(Comparator.comparing(PhotoSize::fileSize)).orElse(null);
        GetFile request = new GetFile(Objects.requireNonNull(photo).fileId());
        GetFileResponse getFileResponse = animalShelterBot.execute(request);
        return getFileResponse.file();
    }

    @Override
    public void sendMessage(Long chatId, String inputMessage, Keyboard keyboard) {
        SendMessage outputMessage = new SendMessage(chatId, inputMessage)
                .replyMarkup(keyboard);
        try {
            animalShelterBot.execute(outputMessage);
        } catch (Exception e) {
            logger.info("Exception was thrown in sendMessage method with keyboard ");
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(Long chatId, String inputMessage) {
        SendMessage outputMessage = new SendMessage(chatId, inputMessage);
        try {
            animalShelterBot.execute(outputMessage);
        } catch (Exception e) {
            logger.info("Exception was thrown in sendMessage message method ");
            e.printStackTrace();
        }
    }

    @Override
    public void sendDocument(Long chatId, java.io.File file) {
        SendDocument photo = new SendDocument(chatId, file);
        try {
            animalShelterBot.execute(photo);
        } catch (Exception e) {
            logger.info("Exception was thrown in sendMessage message method ");
            e.printStackTrace();
        }
    }

    private static ReplyKeyboardMarkup chooseShelter() {
        logger.info("Choose shelter keyboard was called");
        return new ReplyKeyboardMarkup(
                Constants.CAT_SHELTER_CMD, Constants.DOG_SHELTER_CMD)
                .resizeKeyboard(true)
                .selective(true);
    }

    /**
     * Кнопки главного меню для приюта для собак
     *
     * @return buttons
     */

    private static Keyboard dogStartMenuButtons() {
        return new ReplyKeyboardMarkup(
                new KeyboardButton[]{
                        new KeyboardButton(Constants.DOG_SHELTER_INFO_CMD),
                        new KeyboardButton(Constants.HOW_TO_TAKE_DOG_CMD),
                },
                new KeyboardButton[]{
                        new KeyboardButton(Constants.SEND_REPORT_CMD),
                        new KeyboardButton(Constants.CALL_VOLUNTEER_CMD)
                },
                new KeyboardButton[]{
                        new KeyboardButton(Constants.BACK_TO_CHOOSE_ANIMAL)
                })
                .resizeKeyboard(true);
    }

    /**
     * Меню "О приюте"
     *
     * @return buttons
     */

    private static ReplyKeyboardMarkup dogShelterInfoButtons() {
        logger.info("Dog shelter info keyboard was called");
        return new ReplyKeyboardMarkup(
                new String[]{Constants.DOG_ABOUT_US_CMD, Constants.DOG_WORKING_HOURS_CMD},
                new String[]{Constants.DOG_SAFETY_RECOMMENDATION_CMD, Constants.DOG_SECURITY_CONTACT_CMD},
                new String[]{Constants.CONTACT, Constants.CALL_VOLUNTEER_CMD},
                new String[]{Constants.BACK_TO_DOG_START_MENU_CMD})
                .resizeKeyboard(true);
    }

    /**
     * Кнопки меню "Как взять собаку"
     *
     * @return buttons
     */

    private static ReplyKeyboardMarkup dogRecommendationButtons() {
        logger.info("Dogs recommendations Keyboard was called");
        return new ReplyKeyboardMarkup(
                new String[]{Constants.MEET_THE_DOG_CMD, Constants.DOCUMENTS_CMD},
                new String[]{Constants.DOG_TRANSPORTING_AND_ADVICE_CMD, Constants.CYNOLOGIST_CMD},
                new String[]{Constants.REFUSAL_REASONS_CMD, Constants.CONTACT},
                new String[]{Constants.CALL_VOLUNTEER_CMD, Constants.BACK_TO_DOG_START_MENU_CMD})
                .resizeKeyboard(true);
    }

    /**
     * Меню с рекомендациями по поводу перевозки собаки и обустройства дома
     *
     * @return buttons
     */

    private static ReplyKeyboardMarkup dogTransportAndAdviceDogButtons() {
        logger.info("Dog's transportation and advice keyboard was called");
        return new ReplyKeyboardMarkup(
                new String[]{Constants.DOG_TRANSPORTING_CMD, Constants.DOG_ADVICE_CMD},
                new String[]{Constants.BACK_TO_DOG_RECOMMENDATION_MENU_CMD}
        )
                .resizeKeyboard(true);
    }

    /**
     * Меню с советами по благоустройству дома для щенков, взрослых собак и собак-инвалидов
     *
     * @return buttons
     */

    private static ReplyKeyboardMarkup dogSpecificAdviceMenu() {
        logger.info("Specific advice keyboard was called");
        return new ReplyKeyboardMarkup(
                new String[]{Constants.ADVICE_FOR_PUPPY_CMD, Constants.ADVICE_FOR_ADULT_DOG_CMD, Constants.ADVICE_FOR_SPECIAL_DOG_CMD},
                new String[]{Constants.BACK_TO_DOG_TRANSPORT_AND_ADVICE_MENU_CMD}
        )
                .resizeKeyboard(true);
    }

    /**
     * Кнопки меню с контактами и рекомендациями кинологов
     *
     * @return buttons
     */

    private static ReplyKeyboardMarkup cynologistMenu() {
        logger.info("Cynologist keyboard was called");
        return new ReplyKeyboardMarkup(
                new String[]{Constants.CYNOLOGIST_ADVICE_CMD, Constants.CYNOLOGIST_CONTACTS_CMD},
                new String[]{Constants.BACK_TO_DOG_RECOMMENDATION_MENU_CMD}
        )
                .resizeKeyboard(true);
    }

    // cats

    private static Keyboard catStartMenuButtons() {
        return new ReplyKeyboardMarkup(
                new KeyboardButton[]{
                        new KeyboardButton(Constants.CAT_SHELTER_INFO_CMD),
                        new KeyboardButton(Constants.HOW_TO_TAKE_CAT_CMD),
                },
                new KeyboardButton[]{
                        new KeyboardButton(Constants.SEND_REPORT_CMD),
                        new KeyboardButton(Constants.CALL_VOLUNTEER_CMD)
                }, new KeyboardButton[]{
                new KeyboardButton(Constants.BACK_TO_CHOOSE_ANIMAL)
        })
                .resizeKeyboard(true);
    }

    /**
     * Меню "О приюте"
     *
     * @return buttons
     */

    private static ReplyKeyboardMarkup catShelterInfoButtons() {
        logger.info("Cat shelter info keyboard was called");
        return new ReplyKeyboardMarkup(
                new String[]{Constants.CAT_ABOUT_US_CMD, Constants.CAT_WORKING_HOURS_CMD},
                new String[]{Constants.CAT_SAFETY_RECOMMENDATION_CMD, Constants.CAT_SECURITY_CONTACT_CMD},
                new String[]{Constants.CONTACT, Constants.CALL_VOLUNTEER_CMD},
                new String[]{Constants.BACK_TO_CAT_START_MENU_CMD})
                .resizeKeyboard(true);
    }

    /**
     * Меню "Как взять кота"
     *
     * @return buttons
     */

    private static ReplyKeyboardMarkup catRecommendationButtons() {
        logger.info("Cats recommendations Keyboard was called");
        return new ReplyKeyboardMarkup(
                new String[]{Constants.MEET_THE_CAT_CMD, Constants.DOCUMENTS_CMD},
                new String[]{Constants.CAT_TRANSPORTING_AND_ADVICE_CMD, Constants.FELINOLOGIST_CMD},
                new String[]{Constants.REFUSAL_REASONS_CMD, Constants.CONTACT},
                new String[]{Constants.CALL_VOLUNTEER_CMD, Constants.BACK_TO_CAT_START_MENU_CMD})
                .resizeKeyboard(true);
    }

    /**
     * Меню с рекомендациями по поводу перевозки собаки и обустройства дома
     *
     * @return buttons
     */

    private static ReplyKeyboardMarkup catTransportAndAdviceDogButtons() {
        logger.info("Cat's transportation and advice keyboard was called");
        return new ReplyKeyboardMarkup(
                new String[]{Constants.CAT_TRANSPORTING_CMD, Constants.CAT_ADVICE_CMD},
                new String[]{Constants.BACK_TO_CAT_RECOMMENDATION_MENU_CMD}
        )
                .resizeKeyboard(true);
    }

    /**
     * Меню с советами по благоустройству дома для котят, взрослых кошек и кошек-инвалидов.
     *
     * @return buttons
     */

    private static ReplyKeyboardMarkup catSpecificAdviceMenu() {
        logger.info("Specific cat advice keyboard was called");
        return new ReplyKeyboardMarkup(
                new String[]{Constants.ADVICE_FOR_KITTEN_CMD, Constants.ADVICE_FOR_ADULT_CAT_CMD, Constants.ADVICE_FOR_SPECIAL_CAT_CMD},
                new String[]{Constants.BACK_TO_CAT_TRANSPORT_AND_ADVICE_MENU_CMD}
        )
                .resizeKeyboard(true);
    }

    /**
     * Кнопки меню с контактами и рекомендациями фелинологов
     *
     * @return buttons
     */

    private static ReplyKeyboardMarkup felinologystMenu() {
        logger.info("Cynologist keyboard was called");
        return new ReplyKeyboardMarkup(
                new String[]{Constants.FELINOLOGIST_ADVICE_CMD, Constants.FELINOLOGIST_CONTACTS_CMD},
                new String[]{Constants.BACK_TO_CAT_RECOMMENDATION_MENU_CMD}
        )
                .resizeKeyboard(true);
    }


    @Scheduled(cron = "0 0 12 * * *")
    public void sendReminders() {
        sendReminderAboutLackOfReport();
        sendRemindersToVolunteerAboutEndOfTrial();
        sendNotificationAboutSuccessReport();
        sendNotificationAboutDeclinedReport();
        sendNotificationAboutResultOfTrial();
    }

    @Override
    public void sendReminderAboutLackOfReport() {
        List<User> adoptersList = userService.getAllAdopters(User.UserStatus.ADOPTER_ON_TRIAL);
        if (!adoptersList.isEmpty()) {
            SendMessage reminderToUser = null;
            SendMessage reminderToVolunteer = null;
            for (User user : adoptersList) {
                Report lastReport = reportService.getLastReportByUserId(user.getId());
                if (lastReport != null && lastReport.getSentDate().isBefore(LocalDate.now())) {
                    reminderToUser = new SendMessage(user.getChatId(), "Вчера мы не получили от Вас отчет о питомце. " +
                            "Пожалуйста, отправьте отчет. В противном случае волонтеры приюта будут обязаны лично проверять условия содержания животного. ");
                    if (lastReport.getSentDate().isBefore(LocalDate.now().minusDays(1))) {
                        reminderToVolunteer = new SendMessage(Constants.VOLUNTEERS_CHAT_ID, "Усыновитель " + user.getName() + " " + user.getPhoneNumber() + " не присылал отчет в течение двух дней. " +
                                "\nНеобходимо с ним связаться как можно скорее.");
                    }

                }
            }
            if (reminderToUser != null) {
                animalShelterBot.execute(reminderToUser);
            }
            if (reminderToUser != null) {
                animalShelterBot.execute(reminderToVolunteer);
            }
        }
    }

    @Override
    public void sendRemindersToVolunteerAboutEndOfTrial() {
        List<User> adoptersList = userService.getAdoptersWithEndOfTrial(User.UserStatus.ADOPTER_ON_TRIAL, LocalDate.now());
        if (!adoptersList.isEmpty()) {
            SendMessage reminder = null;
            for (User user : adoptersList) {
                reminder = new SendMessage(Constants.VOLUNTEERS_CHAT_ID, "Сегодня у усыновителя " + user.getName() + " " + user.getPhoneNumber() + " заканчивается испытательный срок. " +
                        "\nНеобходимо принять решение, прошел ли усыновитель испытательный срок или требуется продление срока.");
            }
            animalShelterBot.execute(reminder);
        }
    }

    @Override
    public void sendNotificationAboutSuccessReport() {
        List<User> adoptersListWithAcceptedReports = userService.getAdoptersByReportStatusAndSentDate(Report.ReportStatus.ACCEPTED, LocalDate.now().minusDays(1));
        if (!adoptersListWithAcceptedReports.isEmpty()) {
            SendMessage reminder = null;
            for (User user : adoptersListWithAcceptedReports) {
                reminder = new SendMessage(user.getChatId(), "Поздравляем! Ваш вчерашний отчет был проверен и одобрен волонтером. Продолжайте в том же духе!");
            }
            animalShelterBot.execute(reminder);
        }
    }

    @Override
    public void sendNotificationAboutDeclinedReport() {
        List<User> adoptersListWithDeclinedReports = userService.getAdoptersByReportStatusAndSentDate(Report.ReportStatus.DECLINED, LocalDate.now().minusDays(1));
        if (!adoptersListWithDeclinedReports.isEmpty()) {
            SendMessage reminder = null;
            for (User user : adoptersListWithDeclinedReports) {
                reminder = new SendMessage(user.getChatId(), Constants.BAD_REPORT_WARNING);
            }
            animalShelterBot.execute(reminder);
        }
    }

    @Override
    public void sendNotificationAboutResultOfTrial() {
        List<User> adopterListWithSuccessTrial = userService.getAdoptersByStatusAndReportDate(User.UserStatus.OWNER, LocalDate.now().minusDays(1));
        if (!adopterListWithSuccessTrial.isEmpty()) {
            SendMessage reminder;
            for (User user : adopterListWithSuccessTrial) {
                reminder = new SendMessage(user.getChatId(), Constants.TRIAL_PASSED);
                animalShelterBot.execute(reminder);
            }
        }

        List<User> adopterListWithTrialFailed = userService.getAdoptersByStatusAndReportDate(User.UserStatus.ADOPTER_TRIAL_FAILED, LocalDate.now());
        if (!adopterListWithTrialFailed.isEmpty()) {
            SendMessage reminder;
            for (User user : adopterListWithTrialFailed) {
                reminder = new SendMessage(user.getChatId(), Constants.TRIAL_NOT_PASSED);
                animalShelterBot.execute(reminder);
            }
        }

        List<User> adopterListWithExtendedTrial = userService.getAdoptersByStatusAndExtendedTrial(User.UserStatus.ADOPTER_ON_TRIAL);
        if (!adopterListWithExtendedTrial.isEmpty()) {
            SendMessage reminder;
            for (User user : adopterListWithExtendedTrial) {
                reminder = new SendMessage(user.getChatId(), Constants.TRIAL_EXTENDED + user.getEndTrialDate());
                animalShelterBot.execute(reminder);
            }
        }
    }
}