package org.parser.parsermail.bot;

import org.jetbrains.annotations.NotNull;
import org.parser.parsermail.bot.handlers.CommandHandler;
import org.parser.parsermail.bot.services.MessageService;
import org.parser.parsermail.entities.JobVacancy;
import org.parser.parsermail.parser.ParsingService;
import org.parser.parsermail.services.JobVacanciesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(MyTelegramBot.class);

    private final Map<Command, CommandHandler> commandHandlers = new HashMap<>();

    private ParsingService parsingService;

    private JobVacanciesService jobVacanciesService;

    private MessageService messageService;

    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    public MyTelegramBot(CommandHandler commandHandler) {
        commandHandlers.put(Command.START, commandHandler);
        commandHandlers.put(Command.RESET, commandHandler);
        commandHandlers.put(Command.SHOW_LIKED, commandHandler);
        commandHandlers.put(Command.STOP, commandHandler);
    }

    @Autowired
    public void setParsingService(ParsingService parsingService) {
        this.parsingService = parsingService;
    }

    @Autowired
    public void setJobVacanciesService(JobVacanciesService jobVacanciesService) {
        this.jobVacanciesService = jobVacanciesService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
    }

    @Async
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void fetchAndSendData() {
        List<JobVacancy> validJobVacancies = this.parsingService.parseAndGetValidJobs();

        validJobVacancies.forEach(System.out::println);

        logger.info("Valid jobs size: {}", validJobVacancies.size());

        logger.info("Whole amount of jobs: {}", this.jobVacanciesService.findAll().size());

        this.messageService.scheduleAndSendMessages(validJobVacancies, 5, this);
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    private boolean isGroupChat(@NotNull Chat chat) {
        return chat.isGroupChat() || chat.isSuperGroupChat();
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }
}
