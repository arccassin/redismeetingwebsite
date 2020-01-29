import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.redisson.config.Config;

import java.util.Date;
import java.util.Random;

import static java.lang.System.out;
import static java.lang.System.setOut;

/**
 * Created by User on 27 Янв., 2020
 */
public class RedisStorage {

    public RedisStorage(int userCount) {
        this.userCount = userCount;
    }

    private RedissonClient redisson;

    private RKeys rKeys;

    private int userCount;

    private RScoredSortedSet<String> regUsers;

    private final static String KEY = "REG_USERS";

    private double getTs(){
        return new Date().getTime();
    }

    void init() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        try {
            redisson = Redisson.create(config);
        } catch (RedisConnectionException Exc) {
            out.println("Не удалось подключиться к Redis");
            out.println(Exc.getMessage());
        }
        rKeys = redisson.getKeys();
        regUsers = redisson.getScoredSortedSet(KEY);
        rKeys.delete(KEY);
    }

    void shutdown() {
        redisson.shutdown();
    }

    int showNextUser(){
        int id = getFirstUser();
        addLast(id);
        return id;
    }

    int showRandomUser(){
        int user_id = new Random().nextInt(userCount) + 1;
        addLast(user_id);
        return user_id;
    }

    //показываем пользователя на главной: возвращаем первого и удаляем
    int getFirstUser() {
        //ZPOPMIN REG_USERS
        return Integer.valueOf(regUsers.takeFirst());
    }

    //добавляем пользователя в конец
    private void addLast(int user_id)
    {
        //ZADD REG_USERS
        regUsers.add(getTs(), String.valueOf(user_id));
    }

    //регистрация первых пользователей
    public void firstRegistrations(int count) {
        for (int i = 1; i < count + 1; i++) {
            addLast(i);
        }
    }
}

//    docker run --rm --name skill-redis -p 127.0.0.1:6379:6379/tcp -d redis
//    docker exec -it skill-redis redis-cli
