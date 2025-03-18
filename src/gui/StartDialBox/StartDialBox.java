package gui.StartDialBox;

import Settings.Settings;
import core.OnlineComunicationManager;
import gui.GameFrame.GameFrame;
import gui.GameFrame.IconManager;
import gui.GameFrame.PlaySound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

public class StartDialBox {

    private SwingWorker<Void, Void> backgroundWorker = new SwingWorker<Void, Void>() {
        @Override
        protected Void doInBackground() throws Exception {
            gameFrame = new GameFrame(blackIcon, iconManager);
            return null;
        }
    };

    Thread futureGameFrameMaker;

    private JFrame mainFrame;
    private Image blackIcon;
    private Image whiteIcon;
    private Image settingsGearIcon;

    private static IconManager iconManager;
    private GameFrame gameFrame;
    private JFrame popup;

    public StartDialBox() {

        Settings.initializeSettings();
        loadIcon();

        mainFrame = new JFrame("JavaChess");
        mainFrame.setIconImage(blackIcon);
        mainFrame.setSize(600, 300);
        mainFrame.setLayout(null);
        mainFrame.setResizable(false);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

        //Settings
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

        //Online Game
        JButton startOnlineGame = new JButton("Online Game");
        startOnlineGame.setBorderPainted(false);
        startOnlineGame.setFocusPainted(false);
        startOnlineGame.setBounds(360, 100, 130, 40);
        startOnlineGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startOnlineGame();
            }
        });
        mainFrame.add(startOnlineGame);

        //Image
        JLabel label = new JLabel(new ImageIcon(whiteIcon.getScaledInstance(230, 230, Image.SCALE_SMOOTH)));
        label.setSize(230,230);
        label.setLocation(20, 15);
        mainFrame.add(label);

        mainFrame.setVisible(true);

        iconManager = new IconManager();

        backgroundWorker.execute();

        //long iniT = System.nanoTime();
        PlaySound.initializePlaySound();
        //System.out.println("[Playsound] exT = " + (System.nanoTime() - iniT));
    }

    private void startOnlineGame() {
        popup = new JFrame();
        popup.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        popup.setSize(330, 150);
        JPanel panel = new JPanel(null);
        JLabel msg = new JLabel("You are the WHITE. Waiting for the opponent.");
        popup.setTitle("Joshua");
        popup.setIconImage(blackIcon);
        msg.setForeground(Color.WHITE);
        msg.setSize(320, 50);
        msg.setLocation(30, 30);
        panel.setBackground(Color.BLACK);
        panel.add(msg);
        popup.add(panel);

        JFrame onlineSettings = new JFrame();
        onlineSettings.setSize(350, 320);
        onlineSettings.setTitle("Online Settings");
        onlineSettings.setIconImage(blackIcon);
        onlineSettings.setResizable(false);
        onlineSettings.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel requests = new JPanel(null);
        requests.setBackground(Color.BLACK);

        onlineSettings.add(requests);

        JTextField URL = new JTextField("localhost");
        URL.setSize(150, 25);
        URL.setLocation(135, 30);
        JLabel urlL = new JLabel("URL");
        urlL.setLocation(65,20);
        urlL.setSize(100, 50);
        urlL.setForeground(Color.WHITE);
        requests.add(urlL);
        requests.add(URL);

        JTextField port = new JTextField("500");
        port.setSize(150, 25);
        port.setLocation(135, 85);
        JLabel portL = new JLabel("Port");
        portL.setLocation(65,75);
        portL.setSize(100, 50);
        portL.setForeground(Color.WHITE);
        requests.add(port);
        requests.add(portL);

        JTextField game = new JTextField();
        game.setSize(150, 25);
        game.setLocation(135, 140);
        JLabel gameL = new JLabel("Game");
        gameL.setLocation(65,130);
        gameL.setSize(100, 50);
        gameL.setForeground(Color.WHITE);
        requests.add(game);
        requests.add(gameL);
        
        JButton ok = new JButton("OK");
        ok.setLocation(250, 240);
        ok.setSize(70,30);
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String urlV = URL.getText();
                int portV = Integer.parseInt(port.getText());
                int gameV = Integer.parseInt(game.getText());

                OnlineComunicationManager commManager =
                        new OnlineComunicationManager(urlV, portV, gameV);
                int resOfCreation = commManager.makeSocket();
                switch (resOfCreation){
                    case -1 :
                        JOptionPane.showMessageDialog(null, "Problem in connection");
                        break;
                    case 2:
                        JOptionPane.showMessageDialog(null, "Game does not exist");
                        commManager.closeSocket();
                        break;
                    case 100:
                        JOptionPane.showMessageDialog(null, "Game is full");
                        commManager.closeSocket();
                        break;
                    case 0, 1:
                        popup.setVisible(true);
                        mainFrame.setVisible(false);
                        new Thread( () -> {
                            commManager.getStart();
                            gameFrame.makeOnlineGame(resOfCreation, commManager);
                            popup.dispose();
                            startNormalGame();
                        }).start();
                        break;
                    default:
                        break;
                }
                onlineSettings.dispose();
            }
        });
        requests.add(ok);

        onlineSettings.setVisible(true);
    }

    public void makeGameFrame(){
        if(gameFrame != null)
            gameFrame.dispose();
        futureGameFrameMaker = new Thread(() -> gameFrame = new GameFrame(blackIcon, iconManager));
        futureGameFrameMaker.start();
    }

    public GameFrame getGameFrame() {
        return gameFrame;
    }

    public static IconManager getIconManager() {
        return iconManager;
    }

    private void waitForGame(){
        try {
            backgroundWorker.get();
        } catch (InterruptedException e) {
            System.out.println(e);
        } catch (ExecutionException e) {
            System.out.println(e);
        }

        if(futureGameFrameMaker != null) {
            try {
                futureGameFrameMaker.join();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    private void settingsPanel() {
        waitForGame();

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
            //launchGui(blackIcon, iconManager);
            try {
                backgroundWorker.get();
            } catch (InterruptedException e) {
                System.out.println(e);
            } catch (ExecutionException e) {
                System.out.println(e);
            }

            if(futureGameFrameMaker != null) {
                try {
                    futureGameFrameMaker.join();
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }

            gameFrame.makeVisible();
            mainFrame.setVisible(false);
        }
    }
}
