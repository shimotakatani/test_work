package user;

import java.util.UUID;

/**
 * Класс игрока
 *
 * Created by Никита on 30.05.2017.
 */
public class User {

    private UUID user; //id игрока, имя поля взял из задания... и тип совсем не для ников
    private int rank; //уровень игрока

    User(int rank){ //небольшие ограничения по уровню, взяты из задания
        if (rank < 1) {
            rank = 1;
        }
        if (rank > 30) {//хотя тут можно было бы и 1 поставить, но пока уточнения нет
            rank = 30;
        }
        this.rank = rank;
        user = UUID.randomUUID();//Поскольку некому вводить имена, то сделал конструктор только со случайной генерацией id
    }

    public UUID getUser() {
        return user;
    }

    public void setUser(UUID user) {
        this.user = user;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
