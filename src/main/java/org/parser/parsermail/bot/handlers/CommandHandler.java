package org.parser.parsermail.bot.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class CommandHandler implements Handler {

    @Override
    public void handle(Update update, TelegramLongPollingBot bot) {

    }
}
