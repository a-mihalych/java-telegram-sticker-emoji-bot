package ru.mihalych.stickeremojibot.bot.components;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

public interface BotCommands {

    String COMMAND_START = "/start";
    String DESCRIPTION_START = "старт бота";
    String COMMAND_HELP = "/help";
    String DESCRIPTION_HELP = "помощь";
    String HELP_TEXT = "Этот бот выдаёт текстовый код отправленых ему смайликов и стикеров";

    List<BotCommand> COMMANDS = List.of(new BotCommand(COMMAND_START, DESCRIPTION_START),
                                        new BotCommand(COMMAND_HELP, DESCRIPTION_HELP));
}
