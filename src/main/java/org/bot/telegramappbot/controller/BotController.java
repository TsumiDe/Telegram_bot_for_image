package org.bot.telegramappbot.controller;
import org.bot.telegramappbot.CommunicateWithPython;
import org.bot.telegramappbot.commands.Commands;
import org.bot.telegramappbot.config.SpringBootTgBotConfig;
import org.bot.telegramappbot.keyboard.Keyboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class BotController extends TelegramLongPollingBot {
    private final SpringBootTgBotConfig config;
    private final Commands commands;
    private final CommunicateWithPython communicate;
    private final Keyboard keyboard;
    private final List<String> accessFiles = Arrays.asList("png", "jpg", "jpeg");
    private String callbackQueryData;
    private String isHas;

    @Autowired
    public BotController(SpringBootTgBotConfig config, Commands commands, CommunicateWithPython communicate,
                         Keyboard keyboard) {
        this.config = config;
        this.commands = commands;
        this.communicate = communicate;
        this.keyboard = keyboard;
    }

    @Override
    public String getBotUsername() {return config.getBotName();}

    @Override
    public String getBotToken() {return config.getBotToken();}

    @Override
    public void onUpdateReceived(Update update) {
        isHas = config.isHas(update);
        SendMessage messageText;
        switch (isHas) {
            case "hasText" -> {
                Message message = update.getMessage();
                try {
                    if (message.getText().equals("/start")) {
                        messageText = commands.setMessage(message.getChatId(), "Choose action");
                        Map<String, String> keyboardData =
                                Map.of("Remove background", "remove_background",
                                        "Information of bot", "bot_info");
                        messageText.setReplyMarkup(keyboard.keyboardMarkup(keyboardData));
                        sendMessage(messageText);
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
            case "hasCallbackQuery" -> {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                Long chatId = callbackQuery.getMessage().getChatId();
                switch (callbackQuery.getData()) {
                    case "remove_background" -> {
                        messageText = commands.setMessage(chatId, "Send me photo without compress");
                        sendMessage(messageText);
                        callbackQueryData = callbackQuery.getData();
                    }
                    case "bot_info" -> sendMessage(commands.setMessage(chatId,
                            "This bot can remove the background from your photo"));
                }
            }
            case "hasDocument" -> {
                if (callbackQueryData.equals("remove_background")) {
                    try {
                        Message message = update.getMessage();
                        List<String> documentName =
                                Arrays.asList(message.getDocument().getFileName().split("\\."));
                        if (accessFiles.contains(documentName.get(documentName.size() - 1))) {
                            messageText = commands.setMessage(message.getChatId(), "photo in processing");
                            sendMessage(messageText);
                            String fileName = commands.getDocument(
                                    getBotToken(),
                                    message.getDocument().getFileId(),
                                    config.getInputImagesPath()
                            );
                            communicate.communicate();
                            sendDocument(commands.setDocument(message,
                                    String.format("%s/%s", config.getOutputImagesPath(), fileName)));
                        } else {
                            messageText = commands.setMessage(message.getChatId(),
                                    "photo is in the wrong format");
                            sendMessage(messageText);
                        }
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    } finally {
                        List<File> filesPath = Arrays.asList(new File(config.getInputImagesPath()),
                                new File(config.getOutputImagesPath()));
                        Stream.of(filesPath.get(0), filesPath.get(1))
                                .map(file -> Objects.requireNonNull(file.listFiles()))
                                .flatMap(Arrays::stream)
                                .forEach(file -> {
                                    if (!file.delete()) {
                                        System.out.printf("Failed to delete: %s", file.getName());
                                    }
                                });
                    }
                }
            }
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        updates.forEach(this::onUpdateReceived);
        if (isHas.equals("hasDocument")) {callbackQueryData = "Empty";}
    }

    public void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendDocument(SendDocument sendDocument) {
        try {
            execute(sendDocument);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
