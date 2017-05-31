package com.company;

import user.User;
import user.UserFactory;
import user.UserInGame;

import static java.lang.System.currentTimeMillis;

/**
 * Генератор новых игроков
 *
 * Created by Никита on 30.05.2017.
 */
public class Generator extends Thread {

    @Override
    public void run() {

        while (true) {
            User user = UserFactory.create();
            Main.userQueue.add(new UserInGame(user, currentTimeMillis()));
            //System.out.println("Queue size is " + Main.userQueue.size()); // можно подсмотреть за размером очереди
            try {
                sleep(Main.GENERATOR_SLEEP_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
