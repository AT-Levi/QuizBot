package uz.pdp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import uz.pdp.handlers.UserStateHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

    public static final String ADMIN_PASSWORD = "qwerty";
    public static List<BotAdminUser> userList;
    public static List<Quizz> quizzList;

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        quizzList = mapper.readValue(new File("src/main/resources/questions.txt"), new TypeReference<List<Quizz>>() {});
        userList = mapper.readValue(new File("src/main/resources/users.txt"), new TypeReference<List<BotAdminUser>>() {});

        TelegramBot bot = new TelegramBot("6993847421:AAGsCIOec-LNYtP_WGsxLNvZaXRPTOKiJQ8");

        bot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(List<Update> updates) {
                for (Update update : updates) {
                    if (update.message() != null) {
                        UserStateHandler.handleMessage(bot, update.message(), quizzList);
                    } else if (update.callbackQuery() != null) {
                        UserStateHandler.handleCallbackQuery(bot, update.callbackQuery(), quizzList);
                    }
                }
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        });
    }

    public static void saveUsersToFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("src/main/resources/users.txt"), userList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
