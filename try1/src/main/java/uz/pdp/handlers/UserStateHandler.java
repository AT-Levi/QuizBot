package uz.pdp.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import uz.pdp.BotAdminUser;
import uz.pdp.Main;
import uz.pdp.Quizz;
import uz.pdp.UserStateManager;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class UserStateHandler {

    public static void handleMessage(TelegramBot bot, Message message, List<Quizz> quizzList) {
        User from = message.from();
        Long userId = from.id();
        String text = message.text() != null ? message.text().trim() : "";

        ReplyKeyboardMarkup mainKeyboardMarkup = new ReplyKeyboardMarkup(
                new KeyboardButton("Start Quizz"),
                new KeyboardButton("Create quizz"),
                new KeyboardButton("Admin")).resizeKeyboard(true);

        if (text.equalsIgnoreCase("/start")) {
            if (!isUserInList(userId)) {
                Main.userList.add(new BotAdminUser(userId, from.username()));
                Main.saveUsersToFile();
            }
            SendMessage startMessage = new SendMessage(userId, "Добро пожаловать! Выберите действие:").replyMarkup(mainKeyboardMarkup);
            bot.execute(startMessage);
        } else if (UserStateManager.ADVERTISEMENT_STATE.equals(UserStateManager.getUserState(userId))) {
            AdminHandler.sendAdvertisementToAllUsers(bot, text, Main.userList);
            UserStateManager.removeUserState(userId);
            bot.execute(new SendMessage(userId, "Reklama muofaqiyatliy jo'natildi"));
        } else if (UserStateManager.PHOTO_ADVERTISEMENT_STATE.equals(UserStateManager.getUserState(userId)) && message.photo() != null) {
            AdminHandler.sendPhotoAdvertisementToAllUsers(bot, message.photo()[message.photo().length - 1].fileId(), Main.userList);
            UserStateManager.removeUserState(userId);
            bot.execute(new SendMessage(userId, "Reklama muofaqiyatliy jo'natildi"));//foto
        } else if ("WAITING_FOR_ADMIN_PASSWORD".equals(UserStateManager.getUserState(userId))) {
            AdminHandler.handleAdminPassword(bot, userId, text);
        } else if (text.equalsIgnoreCase("Start Quizz")) {
            QuizHandler.handleStartQuizz(bot, userId, quizzList);
        } else if (text.equalsIgnoreCase("Create quizz")) {
            QuizHandler.handleCreateQuizz(bot, userId);
        } else if (text.equalsIgnoreCase("Admin")) {
            AdminHandler.handleAdminCommand(bot, userId);
        } else if (text.equalsIgnoreCase("Send Advertisement")) {
            AdminHandler.handleSendAdvertisement(bot, userId);
        } else if (text.equalsIgnoreCase("Send Photo Advertisement")) {
            AdminHandler.handleSendPhotoAdvertisement(bot, userId);
        } else if (text.equalsIgnoreCase("Back")) {
            handleBackCommand(bot, userId);
        } else {
            SendMessage initialResponse = new SendMessage(userId, "Выберите действие:").replyMarkup(mainKeyboardMarkup);
            bot.execute(initialResponse);
        }
    }

    public static void handleCallbackQuery(TelegramBot bot, CallbackQuery callbackQuery, List<Quizz> quizzList) {
        long chatId = callbackQuery.message().chat().id();
        String callbackData = callbackQuery.data();
        bot.execute(new AnswerCallbackQuery(callbackQuery.id()));

        Integer currentQuestionIndex = UserStateManager.getCurrentQuestion(chatId);
        if (currentQuestionIndex == null) {
            return; // No active quiz for the user
        }

        // Check the answer and proceed to the next question
        Quizz currentQuestion = quizzList.get(currentQuestionIndex);
        if (currentQuestion.getRightOption().equalsIgnoreCase(callbackData)) {
            bot.execute(new SendMessage(chatId, "Correct answer!"));
            UserStateManager.setUserScore(chatId, UserStateManager.getUserScore(chatId) + 1);
        } else {
            bot.execute(new SendMessage(chatId, "Wrong answer! The correct answer was " + currentQuestion.getRightOption() + "."));
        }


        bot.execute(new DeleteMessage(chatId, callbackQuery.message().messageId()));
        UserStateManager.removeUserMessageId(chatId);

        currentQuestionIndex++;
        UserStateManager.setCurrentQuestion(chatId, currentQuestionIndex);


        ScheduledExecutorService scheduler = UserStateManager.getUserTimer(chatId);
        if (scheduler != null) {
            scheduler.shutdownNow();
            UserStateManager.removeUserTimer(chatId);
        }

        QuizHandler.sendQuestion(bot, chatId, quizzList, currentQuestionIndex);
    }

    private static boolean isUserInList(Long userId) {
        return Main.userList.stream().anyMatch(user -> user.getId().equals(userId));
    }

    private static void handleBackCommand(TelegramBot bot, Long userId) {

        QuizHandler.stopQuiz(bot, userId);

        UserStateManager.removeUserState(userId);

        ReplyKeyboardMarkup mainKeyboardMarkup = new ReplyKeyboardMarkup(
                new KeyboardButton("Start Quizz"),
                new KeyboardButton("Create quizz"),
                new KeyboardButton("Admin")).resizeKeyboard(true);
        SendMessage backMessage = new SendMessage(userId, "Вы вернулись в главное меню. Выберите действие:").replyMarkup(mainKeyboardMarkup);
        bot.execute(backMessage);
    }
}
