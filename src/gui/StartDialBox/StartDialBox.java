package gui.StartDialBox;

import Settings.Settings;
import gui.GameFrame.IconManager;
import gui.GameFrame.PlaySound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static gui.GameFrame.GuiLauncher.launchGui;

public class StartDialBox {

    private JFrame mainFrame;
    private Image blackIcon;
    private Image whiteIcon;
    private Image settingsGearIcon;

    private static IconManager iconManager;

    public StartDialBox() {

        Settings.initializeSettings();
        loadIcon();

        mainFrame = new JFrame("JavaChess");
        mainFrame.setIconImage(blackIcon);
        mainFrame.setSize(600, 300);
        mainFrame.setLayout(null);
        mainFrame.setResizable(false);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        mainFrame.getContentPane().setBackground(Color.BLACK);

        //Start Normal Game Button
        JButton startNormalGame = new JButton("Start New Game");
        startNormalGame.setBorderPainted(false);
        startNormalGame.setFocusPainted(false);
        startNormalGame.setBounds(360, 30, 130, 40);
        startNormalGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startNormalGame();
            }
        });
        mainFrame.add(startNormalGame);

        JButton settings = new JButton(new ImageIcon(settingsGearIcon.getScaledInstance(20,20, Image.SCALE_SMOOTH)));
        settings.setBorderPainted(false);
        settings.setFocusPainted(false);
        settings.setBounds(540, 220, 30, 30);
        settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settingsPanel();
            }
        });
        mainFrame.add(settings);

        //Image
        JLabel label = new JLabel(new ImageIcon(whiteIcon.getScaledInstance(230, 230, Image.SCALE_SMOOTH)));
        label.setSize(230,230);
        label.setLocation(20, 15);
        mainFrame.add(label);

        iconManager = new IconManager();

        mainFrame.setVisible(true);
    }

    private void settingsPanel() {
        SettingsPanel p = SettingsPanel.getInstance(blackIcon, this);
    }

    public void updateIconManager(IconManager iM){
        iconManager = iM;
    }

    private void loadIcon() {
        whiteIcon = new ImageIcon(getClass().getClassLoader().getResource("icons/General/CheckRain_White.png")).getImage();
        blackIcon = new ImageIcon(getClass().getClassLoader().getResource("icons/General/CheckRain_Black.png")).getImage();
        settingsGearIcon = (new ImageIcon(getClass().getClassLoader().getResource("icons/General/SettingsIcon.png"))).getImage();
    }

    private void startNormalGame() {
        if(!SettingsPanel.isOpened()) {
            long iniT = System.nanoTime();
            PlaySound.initializePlaySound();
            System.out.println("[Playsound] exT = " + (System.nanoTime() - iniT));
            iniT = System.nanoTime();
            launchGui(blackIcon, iconManager);
            mainFrame.dispose();
        }
    }
}
