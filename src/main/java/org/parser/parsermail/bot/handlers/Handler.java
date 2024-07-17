package org.parser.parsermail.bot.handlers;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Handler {

    void handle(Update update, TelegramLongPollingBot bot);
}
