package org.parser.parsermail.bot.handlers;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.parser.parsermail.bot.MyTelegramBot;
import org.parser.parsermail.services.JobVacanciesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
public class CommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(MyTelegramBot.class);

    @Getter
    Set<Long> activeUsers = new ConcurrentSkipListSet<>();

    private JobVacanciesService jobVacanciesService;

    @Autowired
    public void setJobVacanciesService(JobVacanciesService jobVacanciesService) {
        this.jobVacanciesService = jobVacanciesService;
    }

    public void handle(@NotNull Update update, TelegramLongPollingBot bot) {
        String command = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        switch (command) {
            case "/start":
                activeUsers.add(chatId);
                sendMessage(bot, chatId, "Bot started. You will receive job updates.");
                break;
            case "/stop":
                activeUsers.remove(chatId);
                sendMessage(bot, chatId, "Bot stopped. You will no longer receive job updates.");
                break;
            case "/reset":
                jobVacanciesService.clearVacanciesCollection();
                sendMessage(bot, chatId, "All vacancies have been reset.");
                break;
            case "/showliked":
                break;
            default:
                sendMessage(bot, chatId, "Unknown command.");
                break;
        }
    }

    private void sendMessage(@NotNull TelegramLongPollingBot bot, @NotNull Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void showLikedVacancies(TelegramLongPollingBot bot, Long chatId) {
    }

    public boolean isUserActive(Long chatId) {
        return activeUsers.contains(chatId);
    }
}
