package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {
    NotificationTask findBySendMessageDateTime(LocalDateTime dateTime);
}
