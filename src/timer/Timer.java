package timer;

import core.Color;
import gui.GameFrame.AsideWindow;

public class Timer {
    private int initialTime;
    private int timeWhite, timeBlack;

    public Timer(int initialTime) {
        this.initialTime = initialTime;
        timeWhite = initialTime; timeBlack = initialTime;
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
}
