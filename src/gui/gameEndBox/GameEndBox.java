package gui.gameEndBox;

import gui.gameFrame.GameFrame;
import gui.gameFrame.GuiLauncher;

import javax.swing.*;
import javax.swing.text.IconView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import core.Color;
import gui.startDialBox.StartDialBox;

public class GameEndBox extends JFrame {

    public GameEndBox(Color winner, GameFrame gameFrame){
        setSize(280, 150);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Game Ended");
        setIconImage(StartDialBox.getBlackIcon());

        JPanel panel = new JPanel(null);
        panel.setBackground(java.awt.Color.BLACK);

        JLabel label = new JLabel("" + (winner != Color.WHITE ? "White" : "Black") + " won the game");
        label.setForeground(java.awt.Color.WHITE);
        label.setSize(200, 100);
        label.setLocation(25, 0);

        JButton btn = new JButton("Home");
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setSize(90, 30);
        btn.setLocation(140, 70);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(gameFrame.game.isAnOnlineGame()){
                    gameFrame.game.getTimeThread().interrupt();
                    gameFrame.game.getComunicationManager().closeSocket();
                }
                gameFrame.dispose();
                StartDialBox newStartDialBox = new StartDialBox();
                dispose();
            }
        });

        panel.add(label);
        panel.add(btn);
        add(panel);
        setVisible(true);
    }
}
