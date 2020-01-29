import java.util.Random;

/**
 * Created by User on 27 Янв., 2020
 */
public class WebsiteTest {

    private static final int USER_COUNT = 20;

    private static final int DELAY = 1000;

    private static final int PROBABILITY_IS_ONE_ON = 10;

    private static void printTextToNextUser(int UserId) {
        String log = "— На главной странице показываем пользователя " + UserId;
        System.out.println(log);
    }

    private static void printTextToRandomUser(int UserId) {
        String log = "> Пользователь " + UserId + " оплатил платную услугу";
        System.out.println(log);
    }


    public static void main(String[] args) throws InterruptedException {
        RedisStorage redis = new RedisStorage(USER_COUNT);
        redis.init();
        redis.firstRegistrations(USER_COUNT);

        while(true){
            int caseCounter = new Random().nextInt(PROBABILITY_IS_ONE_ON) + 1;
            if (1 == caseCounter) {
                int userId = redis.showRandomUser();
                printTextToRandomUser(userId);
                printTextToNextUser(userId);
            } else {
                int userId = redis.showNextUser();
                printTextToNextUser(userId);
            }
            Thread.sleep(DELAY);
        }
    }
}
