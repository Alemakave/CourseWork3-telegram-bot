package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.TaskFormatException;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            // Process your updates here

            long chatId = update.message().chat().id();
            if (update.message().text().equals("/start")) {
                telegramBot.execute(new SendMessage(chatId, String.format("Привет %s!", update.message().chat().username())));
            } else {
                try {
                    NotificationTask task = parseNotificationTask(chatId, update.message().text());
                    notificationTaskRepository.save(task);
                    telegramBot.execute(new SendMessage(chatId, "Задача добавлена на " + task.getSendMessageDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))));
                } catch (TaskFormatException e) {
                    telegramBot.execute(new SendMessage(chatId, e.getMessage()));
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public NotificationTask parseNotificationTask(long chatId, String message) throws TaskFormatException {
        Pattern pattern = Pattern.compile("([0-9.:\\s]{16})(\\s)([\\W\\w+]+)");
        Matcher matcher = pattern.matcher(message);

        if (!matcher.matches()) {
            throw new TaskFormatException("Сообщение имеет некорректное форматирование. Необходимо чтоб сообщение было вида: 01.01.2022 20:00 Сделать домашнюю работу");
        }

        String dateTimeAsText = matcher.group(1);
        String notificationText = matcher.group(3);

        LocalDateTime dateTime = LocalDateTime.parse(dateTimeAsText, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

        NotificationTask task = new NotificationTask();
        task.setChatId(chatId);
        task.setTaskText(notificationText);
        task.setSendMessageDateTime(dateTime);

        return task;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void findAndSendNotification() {
        LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        NotificationTask notificationTask = notificationTaskRepository.findBySendMessageDateTime(dateTime);
        if (notificationTask != null) {
            telegramBot.execute(new SendMessage(notificationTask.getChatId(), notificationTask.getTaskText()));
        }
    }
}
