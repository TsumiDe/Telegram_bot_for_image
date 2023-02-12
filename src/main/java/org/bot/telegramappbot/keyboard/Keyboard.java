package org.bot.telegramappbot.keyboard;

import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
public class Keyboard {
    public InlineKeyboardMarkup keyboardMarkup(Map<String, String> keyboardData) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (Map.Entry<String, String> data: keyboardData.entrySet()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(data.getKey());
            button.setCallbackData(data.getValue());
            buttons.add(button);
        }
        markup.setKeyboard(Collections.singletonList(buttons));
        return markup;
    }
}
