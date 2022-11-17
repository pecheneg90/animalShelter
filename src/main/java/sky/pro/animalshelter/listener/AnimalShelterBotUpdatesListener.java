package sky.pro.animalshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sky.pro.animalshelter.service.MessageHandlerService;
import sky.pro.animalshelter.service.ReportService;
import sky.pro.animalshelter.service.UserService;

import javax.annotation.PostConstruct;
import java.util.List;


@Service
public class AnimalShelterBotUpdatesListener implements UpdatesListener {

    private static final Logger logger = LoggerFactory.getLogger(AnimalShelterBotUpdatesListener.class);


    private final TelegramBot animalShelterBot;
    private final UserService userService;
    private final ReportService reportService;
    private final MessageHandlerService messageHandler;

    public AnimalShelterBotUpdatesListener(TelegramBot animalShelterBot,
                                           UserService userService,
                                           ReportService reportService,
                                           MessageHandlerService messageHandler) {
        this.animalShelterBot = animalShelterBot;
        this.userService = userService;
        this.reportService = reportService;
        this.messageHandler = messageHandler;
    }

    @PostConstruct
    public void init() {
        animalShelterBot.setUpdatesListener(this);
    }

    /**
     * Check and process chat's updates
     *
     * @param updates used for receiving and checking different types of updates
     * @return confirmed all updates
     */

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            Message message = update.message();
            // check if the update has a message and message has text
            if (message != null) {
                messageHandler.handleMessage(message, extractChatId(message));
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
    private long extractChatId(Message message) {
        return message.chat().id();
    }
}