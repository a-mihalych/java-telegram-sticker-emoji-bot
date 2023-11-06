package ru.mihalych.stickeremojibot.bot.controller;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Dice;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.mihalych.stickeremojibot.bot.components.BotCommands;
import ru.mihalych.stickeremojibot.bot.components.TextType;
import ru.mihalych.stickeremojibot.bot.config.BotConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class BotImpl extends TelegramLongPollingBot implements BotCommands {

    private final BotConfig botConfig;

    public BotImpl(BotConfig botConfig) {
        log.info("\n*** Запущен конструктор бота");
        this.botConfig = botConfig;
        try {
            this.execute(new SetMyCommands(COMMANDS, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("\n!!! class BotImpl, конструктор, создание меню: {}", e.getMessage());
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(getChatId());
        sendMessage.setText("Был запущен sticker-emoji-bot!");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("\n!!! class BotImpl, конструктор, отправка приветственного сообщения: {}", e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotUsername();
    }

    public long getChatId() {
        return botConfig.getChatId();
    }

    public String getBotName() {
        return botConfig.getBotName();
    }

    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        log.info("\n+++ Новое сообщение: {}", update);
        parseUpdate(update);
    }

    private void parseUpdate(Update update) {
        Message message = update.getMessage();
        long chatId = message.getChatId();
        int messageId = update.getMessage().getMessageId();
        String txtForSend = "Что-то пошло не так";
        if (message.hasText()) {
            txtForSend = getTextForSend(message.getText());
        }
        if (message.hasDice()) {
            Dice dice = message.getDice();
            txtForSend = String.format("Результат: %d\n%s", dice.getValue(), getEmojiKods(dice.getEmoji()));
        }
        if (message.hasSticker()) {
            txtForSend = "Код стикера: " + message.getSticker().getFileId();
        }
        sendTextUpdate(chatId, messageId, txtForSend);
    }

    private TextType parseText(String updateTxt) {
        String txt = updateTxt.trim();
        if (txt.startsWith("/") && (!txt.contains(" "))) {
            return TextType.COMMAND;
        }
        List<String> emojis = EmojiParser.extractEmojis(txt);
        if (emojis.size() > 0) {
            return TextType.SMILE;
        }
        return TextType.TEXT;
    }

    private String getTextForSend(String updateTxt) {
        TextType textType = parseText(updateTxt);
        String txtSend = HELP_TEXT;
        switch (textType) {
            case COMMAND:
                if (COMMAND_START.equals(updateTxt)) {
                    txtSend = "Бот запущен!";
                } else {
                    if (!COMMAND_HELP.equals(updateTxt)) {
                        txtSend = "Эта команда не потдерживается в данном боте";
                    }
                }
                break;
            case TEXT:
                txtSend = "Спасибо за текст, но оказался бесполезным";
                break;
            case SMILE:
                txtSend = getEmojiKods(updateTxt);
                break;
        }
        return txtSend;
    }

    private String getEmojiKods(String txt) {
        StringBuilder result = new StringBuilder();
        Set<String> txtEmojis = new HashSet<>(EmojiParser.extractEmojis(txt));
        if (txtEmojis.size() > 0) {
            result.append("Коды смайликов:").append("\n");
            for (String txtEmoji : txtEmojis) {
                Emoji emoji = EmojiManager.getByUnicode(txtEmoji);
                String emojiAndKod = emoji.getUnicode() + " = " + emoji.getAliases();
                result.append(emojiAndKod).append("\n");
            }
        }
        return result.toString();
    }

    private void sendTextUpdate(long chatId, int messageId, String txt) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyToMessageId(messageId);
        sendMessage.setText(txt);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("\n!!! class BotImpl, метод sendTextUpdate, отправка сообщения: {}", e.getMessage());
        }
    }
}
