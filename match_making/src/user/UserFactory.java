package user;

import java.util.Random;

/**
 * Просто класс для создания игроков
 *
 * Created by Никита on 30.05.2017.
 */
public class UserFactory {

    public static User create(int rank){
        return new User(rank);
    }

    public static User create(){
        Random r = new Random();
        int rank = r.nextInt(30) + 1;
        return new User(rank);
    }

}
