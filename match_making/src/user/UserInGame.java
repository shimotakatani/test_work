package user;

/**
 * Класс для хранения игроков в очереди
 *
 * Created by Никита on 30.05.2017.
 */
public class UserInGame implements Comparable<UserInGame>{

    private User user; //игрок

    private Long enterTime; //время входа(когда положили в очередь)

    public UserInGame(User user, Long enterTime){
        this.user = user;
        this.enterTime = enterTime;
    }

    //равенство проверяем по id пользователей
    public boolean equals(UserInGame otherUser){
        return (this.getUser().getUser().equals(otherUser.getUser().getUser()));
    }

    //Сравниваем по времени входа
    @Override
    public int compareTo(UserInGame otherUser) {
        if (otherUser.enterTime > this.enterTime) {
            return 1;
        } else {
            if (otherUser.enterTime < this.enterTime) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(Long enterTime) {
        this.enterTime = enterTime;
    }
}
