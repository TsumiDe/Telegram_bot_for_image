package org.bot.telegramappbot;

import org.bot.telegramappbot.config.SpringBootTgBotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import java.io.*;

@Configuration
public class CommunicateWithPython {
    private final SpringBootTgBotConfig botConfig;

    @Autowired
    public CommunicateWithPython(SpringBootTgBotConfig botConfig) {
        this.botConfig = botConfig;
    }
    public void communicate() throws IOException, InterruptedException {
        String command = String.format("python %s/%s", System.getProperty("user.dir"), botConfig.getPythonScriptPath());
        ProcessBuilder pb = new ProcessBuilder(command.split(" "));
        Process process = pb.start();
        process.waitFor();
    }
}
