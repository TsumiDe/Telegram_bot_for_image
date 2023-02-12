package org.bot.telegramappbot.commands;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.io.*;
import java.net.URL;
import java.util.Arrays;

@Configuration
public class Commands {
    /* Принимает текстовое сообщение от пользователя и текст(который дожен быть отправлен,
       как ответ на это сообщение). Обратно передает SendMessage, который будет отправлен пользователю ботом */
    public SendMessage setMessage(Long chatId, String text) {
        SendMessage messageText = new SendMessage();
        messageText.setChatId(chatId.toString());
        messageText.setText(text);
        return messageText;
    }

    /* Принимает сообщение-документ от пользователя */
    public String getDocument(String token, String fileId, String inputImagesPath) throws IOException, JSONException {
        URL url = new URL(String.format("https://api.telegram.org/bot%s/getFile?file_id=%s", token, fileId));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
        String getFileResponse = bufferedReader.readLine();
        JSONObject jsonObjectResult = new JSONObject(getFileResponse);
        JSONObject jsonObjectPath = jsonObjectResult.getJSONObject("result");
        String filePath = jsonObjectPath.getString("file_path");
        File localFile = new File(String.format("%s/%s", inputImagesPath, Arrays.asList(filePath.split("/")).get(1)));
        InputStream inputStream = new URL(String.format("https://api.telegram.org/file/bot%s/%s", token, filePath))
                .openStream();
        FileUtils.copyInputStreamToFile(inputStream, localFile);
        bufferedReader.close();
        inputStream.close();
        return localFile.getName();
    }

    public SendDocument setDocument(Message message, String filePath) {
        InputFile outputDocument = new InputFile(new File(filePath));
        SendDocument messageDocument = new SendDocument();
        messageDocument.setChatId(message.getChatId().toString());
        messageDocument.setDocument(outputDocument);
        return messageDocument;
    }
}
