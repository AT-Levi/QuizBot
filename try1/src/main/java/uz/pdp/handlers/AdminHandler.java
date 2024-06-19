package uz.pdp.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import uz.pdp.BotAdminUser;
import uz.pdp.Main;
import uz.pdp.UserStateManager;

import java.util.List;

public class AdminHandler {

    public static void handleAdminCommand(TelegramBot bot, Long userId) {
        SendMessage adminPasswordMessage = new SendMessage(userId, "Admin parolni kiriting:");
        bot.execute(adminPasswordMessage);
        UserStateManager.setUserState(userId, "WAITING_FOR_ADMIN_PASSWORD");
    }

    public static void handleAdminPassword(TelegramBot bot, Long userId, String password) {
        if (Main.ADMIN_PASSWORD.equals(password)) {
            SendMessage adminPageMessage = new SendMessage(userId, "Correct!!!\nAdminga kirildi!");


            ReplyKeyboardMarkup adminPageKeyboardMarkup = new ReplyKeyboardMarkup(
                    new KeyboardButton("Send Advertisement"),
                    new KeyboardButton("Send Photo Advertisement"),
                    new KeyboardButton("Back")).resizeKeyboard(true);

            adminPageMessage.replyMarkup(adminPageKeyboardMarkup);
            bot.execute(adminPageMessage);

            UserStateManager.setUserState(userId, "ADMIN_PAGE");
        } else {

            SendMessage wrongPasswordMessage = new SendMessage(userId, "Noto'g'ri parol. Yana bir bor urinib koring! ");
            bot.execute(wrongPasswordMessage);
        }
    }

    public static void handleSendAdvertisement(TelegramBot bot, Long userId) {
        SendMessage requestMessage = new SendMessage(userId, "Reklama jo'natishingiz mumkin.");
        bot.execute(requestMessage);

        ReplyKeyboardMarkup adminKeyboardMarkup = new ReplyKeyboardMarkup(
                new KeyboardButton("Back")).resizeKeyboard(true);

        SendMessage backMessage = new SendMessage(userId, "'Back' tugmasini bosib orqaga qaytishingiz mumkin.").replyMarkup(adminKeyboardMarkup);
        bot.execute(backMessage);

        UserStateManager.setUserState(userId, UserStateManager.ADVERTISEMENT_STATE);
    }

    public static void handleSendPhotoAdvertisement(TelegramBot bot, Long userId) {
        SendMessage requestMessage = new SendMessage(userId, "Rasim tashlang.");
        bot.execute(requestMessage);

        ReplyKeyboardMarkup adminKeyboardMarkup = new ReplyKeyboardMarkup(
                new KeyboardButton("Back")).resizeKeyboard(true);

        SendMessage backMessage = new SendMessage(userId, "Вы можете вернуться в главное меню в любое время, нажав 'Back'.").replyMarkup(adminKeyboardMarkup);
        bot.execute(backMessage);

        UserStateManager.setUserState(userId, UserStateManager.PHOTO_ADVERTISEMENT_STATE);
    }

    public static void sendAdvertisementToAllUsers(TelegramBot bot, String advertisementText, List<BotAdminUser> userList) {
        for (BotAdminUser user : userList) {
            bot.execute(new SendMessage(user.getId(), advertisementText));
        }
    }

    public static void sendPhotoAdvertisementToAllUsers(TelegramBot bot, String fileId, List<BotAdminUser> userList) {
        for (BotAdminUser user : userList) {
            bot.execute(new SendPhoto(user.getId(), fileId));
        }
    }
}
