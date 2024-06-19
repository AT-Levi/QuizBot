package uz.pdp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class UserStateManager {

    public static final String ADVERTISEMENT_STATE = "WAITING_FOR_ADVERTISEMENT_TEXT";
    public static final String PHOTO_ADVERTISEMENT_STATE = "WAITING_FOR_PHOTO_ADVERTISEMENT";

    private static final Map<Long, String> userStates = new HashMap<>();
    private static final Map<Long, Integer> userCurrentQuestion = new HashMap<>();
    private static final Map<Long, Integer> userMessageId = new HashMap<>();
    private static final Map<Long, Integer> userScores = new HashMap<>();
    private static final Map<Long, ScheduledExecutorService> userTimers = new HashMap<>();

    public static synchronized void setUserState(Long userId, String state) {
        userStates.put(userId, state);
    }

    public static synchronized String getUserState(Long userId) {
        return userStates.get(userId);
    }

    public static synchronized void removeUserState(Long userId) {
        userStates.remove(userId);
    }

    public static synchronized void setCurrentQuestion(Long userId, int questionIndex) {
        userCurrentQuestion.put(userId, questionIndex);
    }

    public static synchronized Integer getCurrentQuestion(Long userId) {
        return userCurrentQuestion.get(userId);
    }

    public static synchronized void removeCurrentQuestion(Long userId) {
        userCurrentQuestion.remove(userId);
    }

    public static synchronized void setUserScore(Long userId, int score) {
        userScores.put(userId, score);
    }

    public static synchronized int getUserScore(Long userId) {
        return userScores.getOrDefault(userId, 0);
    }

    public static synchronized void removeUserScore(Long userId) {
        userScores.remove(userId);
    }

    public static synchronized void setUserMessageId(Long userId, int messageId) {
        userMessageId.put(userId, messageId);
    }

    public static synchronized Integer getUserMessageId(Long userId) {
        return userMessageId.get(userId);
    }

    public static synchronized void removeUserMessageId(Long userId) {
        userMessageId.remove(userId);
    }

    public static synchronized void setUserTimer(Long userId, ScheduledExecutorService timer) {
        userTimers.put(userId, timer);
    }

    public static synchronized ScheduledExecutorService getUserTimer(Long userId) {
        return userTimers.get(userId);
    }

    public static synchronized void removeUserTimer(Long userId) {
        userTimers.remove(userId);
    }
}
