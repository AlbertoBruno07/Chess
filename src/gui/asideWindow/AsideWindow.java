package gui.asideWindow;

import core.Game;
import core.Move;
import movesHistory.MovesHistory;
import core.PieceType;

import settings.Settings;
import gui.gameFrame.BoardPanel;
import gui.gameFrame.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

public class AsideWindow extends JFrame {
    private static AsideWindow instance;
    
    public static boolean isOnPreviewsBoard = false;
    private JPanel panel;
    private JScrollPane scrollPane;
    private int moveBtnNumber = 0;
    MovesHistory movesHistory;
    BoardPanel bP;
    private Game game;
    private IconManager iconManager;
    private JLabel whiteTimeLabel, blackTimeLabel;
    private static Semaphore semaphore;
    MoveButton clickedButton;
    private JLabel whitePoints;
    private JLabel blackPoints;

    private AsideWindow(IconManager iM){
        iconManager = iM;
        setTitle("Aside Window");
        setSize(270, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Settings.getColor1().equals(Color.DARK_GRAY) ? Color.GRAY : Settings.getColor1()); //ciao bruno da reby
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setResizable(true);

        JPanel topPanel = new JPanel(new GridLayout());
        topPanel.setBackground(Settings.getColor1().equals(Color.DARK_GRAY) ? Color.GRAY : Settings.getColor1());
        topPanel.setMaximumSize(new Dimension(300, 200));
        add(topPanel, BorderLayout.PAGE_START);

        JLabel label = new JLabel("Move history");
        label.setFont(new Font(label.getFont().getName(), Font.PLAIN,
                28));
        topPanel.add(label, BorderLayout.WEST);

        panel = new JPanel();
        panel.setBackground(Settings.getColor1().equals(Color.DARK_GRAY) ? Color.GRAY : Settings.getColor1());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(panel);
        add(scrollPane);

        JPanel bottomPanel = new JPanel(new GridLayout());
        bottomPanel.setMaximumSize(new Dimension(300, 200));
        bottomPanel.setBackground(Settings.getColor1().equals(Color.DARK_GRAY) ? Color.GRAY : Settings.getColor1());
        add(bottomPanel, BorderLayout.PAGE_END);

        JButton reversBoard = new JButton(IconManager.reverseBoardIcon);
        reversBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                instance.bP.reverseBoard();
            }
        });
        bottomPanel.add(reversBoard, BorderLayout.WEST);

        blackPoints = new JLabel();
        blackPoints.setFont(new Font(label.getFont().getName(), Font.PLAIN,
                23));
        blackPoints.setIcon(iconManager.getSmallIcon(PieceType.KING, core.Color.BLACK));
        blackPoints.setText("0");
        bottomPanel.add(blackPoints, BorderLayout.CENTER);

        whitePoints = new JLabel();
        whitePoints.setFont(new Font(label.getFont().getName(), Font.PLAIN,
                23));
        whitePoints.setIcon(iconManager.getSmallIcon(PieceType.KING, core.Color.WHITE));
        whitePoints.setText("0");
        bottomPanel.add(whitePoints, BorderLayout.EAST);

        semaphore = new Semaphore(1);
    }

    public static void initializeAsideWindow(BoardPanel bp, Game game, MovesHistory mH, IconManager iM, Image icon){
        if(instance != null)
            instance.dispose();

        instance = new AsideWindow(iM);
        instance.bP = bp;
        instance.game = game;
        instance.movesHistory = mH;
        instance.setIconImage(icon);
    }

    public static AsideWindow getInstance() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        AsideWindow instanceToReturn = instance;
        semaphore.release();

        return instanceToReturn;
    }

    public static void makeVisible(){
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        instance.setVisible(true);
        semaphore.release();
    }

    public static void addAnElement(Move m){
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        MoveButton btn = new MoveButton(m.toString(), instance.moveBtnNumber);
        btn.setFont(new Font(btn.getFont().getName(), Font.PLAIN, 18));
        btn.setBackground(Settings.getColor1().equals(Color.DARK_GRAY) ? Color.GRAY : Settings.getColor1());
        btn.setForeground(Color.BLACK);
        btn.setIcon(instance.iconManager.getSmallIcon(m.getSourcePiece().getType(), m.getSourcePiece().getColor()));
        btn.addActionListener(new ElementActionListener(btn));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        instance.panel.add(btn);
        SwingUtilities.invokeLater(() -> instance.scrollPane.getVerticalScrollBar()
                .setValue(instance.scrollPane.getVerticalScrollBar().getMaximum()));
        instance.scrollPane.updateUI();
        instance.moveBtnNumber++;

        semaphore.release();
    }

    public static void updateScore(core.Color color, int newPoints) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(color == core.Color.BLACK)
            instance.blackPoints.setText("" + newPoints);
        else
            instance.whitePoints.setText("" + newPoints);

        semaphore.release();
    }

    public static void makeTime(timer.Timer timer){

        JPanel endPanel = new JPanel(new BorderLayout());
        endPanel.setMaximumSize(new Dimension(250,100));
        endPanel.setBackground(Settings.getColor1().equals(Color.DARK_GRAY) ? Color.GRAY : Settings.getColor1());

        JPanel whiteTime = new JPanel(new BorderLayout());
        whiteTime.setBackground(Settings.getColor1().equals(Color.DARK_GRAY) ? Color.GRAY : Settings.getColor1());
        instance.whiteTimeLabel = new JLabel();
        instance.whiteTimeLabel.setFont(new Font(instance.whiteTimeLabel.getFont().getName(), instance.whiteTimeLabel.getFont().getStyle(), 28));
        JLabel whiteKingSymbol = new JLabel(instance.iconManager.getSmallIcon(PieceType.KING, core.Color.WHITE));
        whiteTime.setMaximumSize(new Dimension(270,100));
        whiteTime.add(whiteKingSymbol, BorderLayout.WEST);
        whiteTime.add(instance.whiteTimeLabel);
        updateTime(core.Color.WHITE, timer.getInitialTime());

        JPanel blackTime = new JPanel(new BorderLayout());
        blackTime.setBackground(Settings.getColor1().equals(Color.DARK_GRAY) ? Color.GRAY : Settings.getColor1());
        instance.blackTimeLabel = new JLabel();
        instance.blackTimeLabel.setFont(new Font(instance.blackTimeLabel.getFont().getName(), instance.blackTimeLabel.getFont().getStyle(), 28));
        JLabel blackKingSymbol = new JLabel(instance.iconManager.getSmallIcon(PieceType.KING, core.Color.BLACK));
        blackTime.setMaximumSize(new Dimension(270,100));
        blackTime.add(blackKingSymbol, BorderLayout.WEST);
        blackTime.add(instance.blackTimeLabel);
        updateTime(core.Color.BLACK, timer.getInitialTime());

        endPanel.add(whiteTime, BorderLayout.WEST);
        endPanel.add(blackTime, BorderLayout.EAST);

        instance.add(endPanel);
    }

    public static void updateTime(core.Color turn, int newTime) {
        if(instance == null) //Shouldn't happen, but better safe than sorry
            return;

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int min = newTime/60;
        int sec = newTime - (min*60);
        if(turn == core.Color.WHITE)
            instance.whiteTimeLabel.setText("" + min + ":" + (sec < 10 ? "0" : "") + sec);
        else
            instance.blackTimeLabel.setText("" + min + ":" + (sec < 10 ? "0" : "") + sec);

        semaphore.release();
    }
}
