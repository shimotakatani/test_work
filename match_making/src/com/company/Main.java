package com.company;

import user.UserInGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import static java.lang.System.currentTimeMillis;

/**
 * Основной класс приложения
 * Прогнал при настройках MAIN_SLEEP_MILLIS = 500; GENERATOR_SLEEP_MILLIS = 1000;
 * Очередь за 15 минут прогона больше 50 игроков не поднималась,
 * игра в среднем каждые 8 секунд(что логично из-за генерации пользователей)
 * <p>
 * Created by Никита on 30.05.2017.
 */
public class Main {

    //Параметры для настройки приложения
    public static final int TIME_COEF = 5000; //ВременнОй коэффициент, указан в задании
    public static final int COUNT_USER_IN_PARTY = 8; //Количество игроков в партии, указано в задании
    public static final int MAIN_SLEEP_MILLIS = 500; //Период проверки подходящей партии в очереди игроков
    public static final int GENERATOR_SLEEP_MILLIS = 1000; //Период генерации нового игрока

    public static Queue<UserInGame> userQueue; //Очередь игроков
    public static Generator generator; //Генератор новых игроков

    //Основной метод приложения
    public static void main(String[] args) {

        System.out.println("Match-making v0.0.1");

        userQueue = new PriorityBlockingQueue();
        generator = new Generator();
        generator.start();

        /*
        * Зациклено, чтобы Матчмейкер имитировал работу в реальном времени на боевом сервере
        * */
        while (true) {
            List<UserInGame> party = getParty(); //Подбираем партию

            if (party.size() > 0) { //Если партия пустая - значит не сформировалась

                System.out.println(printParty(party)); //Печатаем время и состав партии(хотя наглядней уровни игроков)
                removeUsersForParty(party); //Удаляем игроков, нашедших партию, из очереди

                try {
                    Thread.sleep(MAIN_SLEEP_MILLIS); //Ждём до следующей проверки
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * Пробуем выбрать партию из очереди игроков (Жадный алгоритм)
     *
     * @return список игрок в партии, если список пуст(size=0) - значит партия не собралась
     * @author nponosov
     */
    private static List<UserInGame> getParty() {
        List<UserInGame> usersForParty = new ArrayList<>();

        //Зафиксировал очередь, чтобы во время поиска партии вновь добавленные не влияли на результат
        List<UserInGame> users = new ArrayList<>();
        userQueue.forEach(userInGame -> users.add(userInGame));


        for (UserInGame user : users) { //перебираем всех, чтобы попробовали быть первыми в партии
            usersForParty.add(user);

            /*
            * Вот тут жадность алгоритма: перебирать все возможные сочетания игроков не стали, выбираем по первым,
            * и пробуем формировать партию. В общем очередь содержала игроков в порядке их входа в игру(генерации),
            * поэтому есть некоторое оправдание, что жадный алгоритм выберет хороший, хоть и не оптимальный подбор.
            * Но всё ещё возможны ситуации, когда будут возможны партии, но алгоритм их не найдёт на данном времени.
            * Эта проблема сдерживается правилом, по которому допускаются игроки в партию: из него следует, что
            * чем больше времени игрок ожидает, тем с более разноуровневым соперником он может оказаться в партии.
            * */
            for (UserInGame otherUser : users) { //перебираем снова всех, чтобы каждого попробовать добавить в партию
                if (checkWithListUsersForParty(usersForParty, otherUser) && (usersForParty.size() < COUNT_USER_IN_PARTY)) {
                    usersForParty.add(otherUser);
                }
            }
            //если в партии меньше COUNT_USER_IN_PARTY игроков- она не сформировалась
            if (usersForParty.size() < COUNT_USER_IN_PARTY) {
                usersForParty.clear();
            } else {
                break; //а иначе - мы собрали партию, пора выходить из внешнего цикла
            }
        }

        return usersForParty;
    }

    /**
     * Проверка, подходит ли новый(otherUser) игрок для формирующейся партии
     *
     * @param users     - формирующаяся партия,
     * @param otherUser - проверяемый игрок,
     * @return true - если игрок подходит для партии, иначе false
     * @author nponosov
     */
    private static boolean checkWithListUsersForParty(List<UserInGame> users, UserInGame otherUser) {
        boolean isChecked = true;
        //для всей формирующейся партии прогоняем алгоритм проверки
        for (UserInGame user : users) {
            if (users.contains(otherUser) || !checkUsersForParty(user, otherUser)) {
                isChecked = false;
                break;
            }
        }
        return isChecked;
    }

    /**
     * Проверка, подходят ли два игрока для одной партии(алгоритм из задания)
     *
     * @param userA - один игрок
     * @param userB - другой игрок
     * @return true - если подходят, иначе false
     * @author nponosov
     */
    private static boolean checkUsersForParty(UserInGame userA, UserInGame userB) {
        Long currentTime = currentTimeMillis();
        int diffRank = Math.abs(userA.getUser().getRank() - userB.getUser().getRank()); //разница в уровнях
        return (diffRank <= ((currentTime - userA.getEnterTime()) / TIME_COEF)) && (diffRank <= ((currentTime - userB.getEnterTime()) / TIME_COEF));
    }

    /**
     * Сборка строки по партии для вывода
     *
     * @param party - собранная партия
     * @return Строка для вывода
     * @author nponosov
     */
    private static String printParty(List<UserInGame> party) {
        Long currentTime = currentTimeMillis();
        StringBuilder printParty = new StringBuilder("In " + currentTime + " ms ranks:");
        //party.forEach(userInParty -> printParty.append(" " + userInParty.getUser().getRank()));//а уровни нагляднее при выводе
        party.forEach(userInParty -> printParty.append(" " + userInParty.getUser().getUser()));//это по заданию так надо
        return printParty.toString();
    }

    /**
     * Удаление игроков сформировавшейся партии из очереди игроков
     *
     * @param users - сформировавшаяся партия
     * @author nponosov
     */
    private static void removeUsersForParty(List<UserInGame> users) {
        users.forEach(user -> userQueue.remove(user));
    }
}
