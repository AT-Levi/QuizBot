package uz.pdp.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import uz.pdp.Quizz;
import uz.pdp.UserStateManager;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QuizHandler {

    public static void handleStartQuizz(TelegramBot bot, Long userId, List<Quizz> quizzList) {
        UserStateManager.setCurrentQuestion(userId, 0);
        UserStateManager.setUserScore(userId, 0);
        sendQuestion(bot, userId, quizzList, 0);


        ReplyKeyboardMarkup quizKeyboardMarkup = new ReplyKeyboardMarkup(
                new KeyboardButton("Back")).resizeKeyboard(true);

        SendMessage backMessage = new SendMessage(userId, "'Back' tugmasini bosib orqaga qaytishingiz mumkin.").replyMarkup(quizKeyboardMarkup);
        bot.execute(backMessage);
    }

    public static void sendQuestion(TelegramBot bot, Long userId, List<Quizz> quizzList, int questionIndex) {
        if (questionIndex >= quizzList.size() || questionIndex >= 30) {
            int score = UserStateManager.getUserScore(userId);
            SendMessage endMessage = new SendMessage(userId, "Test tugadi. Siz yig'gan ball: " + score);
            bot.execute(endMessage);
            UserStateManager.removeCurrentQuestion(userId);
            UserStateManager.removeUserScore(userId);
            return;
        }

        Quizz question = quizzList.get(questionIndex);
        SendMessage questionMessage = new SendMessage(userId,
                "Question " + (questionIndex + 1) + ": " + question.getQuestion()
                        + "\nA) " + question.getAoption()
                        + "\nB) " + question.getBoption()
                        + "\nC) " + question.getCoption()
                        + "\nD) " + question.getDoption());

        InlineKeyboardButton firstOption = new InlineKeyboardButton("A").callbackData("A");
        InlineKeyboardButton secondOption = new InlineKeyboardButton("B").callbackData("B");
        InlineKeyboardButton thirdOption = new InlineKeyboardButton("C").callbackData("C");
        InlineKeyboardButton fourthOption = new InlineKeyboardButton("D").callbackData("D");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(
                new InlineKeyboardButton[]{firstOption, secondOption},
                new InlineKeyboardButton[]{thirdOption, fourthOption}
        );

        questionMessage.replyMarkup(inlineKeyboardMarkup);
        int messageId = bot.execute(questionMessage).message().messageId();
        UserStateManager.setUserMessageId(userId, messageId);


        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> handleTimeout(bot, userId, quizzList), 30, TimeUnit.SECONDS);
        UserStateManager.setUserTimer(userId, scheduler);
    }

    private static void handleTimeout(TelegramBot bot, Long userId, List<Quizz> quizzList) {
        Integer questionIndex = UserStateManager.getCurrentQuestion(userId);
        if (questionIndex != null) {

            Integer messageId = UserStateManager.getUserMessageId(userId);
            if (messageId != null) {
                bot.execute(new DeleteMessage(userId, messageId));
            }


            questionIndex++;
            UserStateManager.setCurrentQuestion(userId, questionIndex);
            sendQuestion(bot, userId, quizzList, questionIndex);
        }
    }

    public static void handleCreateQuizz(TelegramBot bot, Long userId) {
        SendMessage createQuizzMessage = new SendMessage(userId, "Kechirasiz!! hozircha bu funksiyamiz ohirigacham yaratilmadi.");
        bot.execute(createQuizzMessage);

        ReplyKeyboardMarkup createQuizzKeyboardMarkup = new ReplyKeyboardMarkup(
                new KeyboardButton("Back")).resizeKeyboard(true);

        SendMessage backMessage = new SendMessage(userId, "'Back' tugmasini bosib orqaga qaytishingiz mumkin.").replyMarkup(createQuizzKeyboardMarkup);
        bot.execute(backMessage);
    }

    public static void stopQuiz(TelegramBot bot, Long userId) {
        Integer messageId = UserStateManager.getUserMessageId(userId);
        if (messageId != null) {
            bot.execute(new DeleteMessage(userId, messageId));
            UserStateManager.removeUserMessageId(userId);
        }
        UserStateManager.removeCurrentQuestion(userId);
        UserStateManager.removeUserScore(userId);

        ScheduledExecutorService scheduler = UserStateManager.getUserTimer(userId);
        if (scheduler != null) {
            scheduler.shutdownNow();
            UserStateManager.removeUserTimer(userId);
        }
    }
}
