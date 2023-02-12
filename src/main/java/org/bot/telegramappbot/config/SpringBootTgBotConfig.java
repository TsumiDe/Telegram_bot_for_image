package org.bot.telegramappbot.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Configuration
@PropertySource("/application.properties")
@Data
@ComponentScan("org.bot.telegramappbot")
public class SpringBootTgBotConfig {
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    @Value("${python.script.path}")
    private String pythonScriptPath;
    @Value("${input.images.path}")
    private String inputImagesPath;
    @Value("${output.images.path}")
    private String outputImagesPath;
    private String trueAction;

    public String isHas(Update update) {
        Map<String, Boolean> hasList = Map.of("hasText", update.hasMessage() && update.getMessage().hasText(),
                "hasCallbackQuery", update.hasCallbackQuery(),
                "hasDocument", update.hasMessage() && update.getMessage().hasDocument());
        for (Map.Entry<String, Boolean> isTrue: hasList.entrySet()) {
            if (isTrue.getValue()) {
                this.trueAction = isTrue.getKey();
            }
        }
        return this.trueAction;
    }
}
