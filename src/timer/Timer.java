package timer;

import core.Color;
import gui.asideWindow.AsideWindow;

import java.util.concurrent.Semaphore;

public class Timer {
    private int initialTime;
    private int timeWhite, timeBlack;
    private Semaphore semaphore;

    public Timer(int initialTime) {
        this.initialTime = initialTime;
        timeWhite = initialTime; timeBlack = initialTime;
        semaphore = new Semaphore(1);
    }

    public void tick(Color turn){
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (turn == Color.WHITE)
                AsideWindow.updateTime(turn, --timeWhite);
            else
                AsideWindow.updateTime(turn, --timeBlack);
        }
    }

    public int getInitialTime() {
        return initialTime;
    }

    public int getTime(Color color) {
        if(color == Color.BLACK)
            return timeBlack;
        else
            return timeWhite;
    }

    public void updateTime(Color c, int newTime){
        if(c == Color.BLACK){
            timeBlack = newTime;
            AsideWindow.updateTime(c, timeBlack);
        } else{
            timeWhite = newTime;
            AsideWindow.updateTime(c, timeWhite);
        }
    }
}
