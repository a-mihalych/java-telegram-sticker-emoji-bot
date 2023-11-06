package ru.mihalych.stickeremojibot.bot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Getter
@PropertySource("classpath:config.properties")
public class BotConfig {

    @Value("${bot.token}")
    String token;
    @Value("${bot.name}")
    String botName;
    @Value("${bot.username}")
    String BotUsername;
    @Value("${bot.chatId}")
    long chatId;
}
