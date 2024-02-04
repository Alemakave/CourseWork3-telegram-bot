package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class NotificationTask {
    @Id
    @GeneratedValue
    private Long id;
    private Long chatId;
    private String taskText;
    //Date and time the notification was sent
    private LocalDateTime sendMessageDateTime;

    public NotificationTask() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getTaskText() {
        return taskText;
    }

    public void setTaskText(String taskText) {
        this.taskText = taskText;
    }

    public LocalDateTime getSendMessageDateTime() {
        return sendMessageDateTime;
    }

    public void setSendMessageDateTime(LocalDateTime sendMessageDateTime) {
        this.sendMessageDateTime = sendMessageDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationTask)) return false;

        NotificationTask that = (NotificationTask) o;

        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(chatId, that.chatId)) return false;
        if (!Objects.equals(taskText, that.taskText)) return false;
        return Objects.equals(sendMessageDateTime, that.sendMessageDateTime);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (chatId != null ? chatId.hashCode() : 0);
        result = 31 * result + (taskText != null ? taskText.hashCode() : 0);
        result = 31 * result + (sendMessageDateTime != null ? sendMessageDateTime.hashCode() : 0);
        return result;
    }
}
