package gui.GameFrame;

import core.Game;
import core.Move;
import core.MovesHistory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AsideWindow extends JFrame {
    private static AsideWindow instance;
    
    public static boolean isOnPreviewsBoard = false;
    private JPanel panel;
    private JScrollPane scrollPane;
    private int moveBtnNumber = 0;
    private MovesHistory movesHistory;
    private BoardPanel bP;
    private Game game;
    private IconManager iconManager;

    //Usefull to have some extra attributes accessible straight-forward
    private static class MoveButton extends JButton{
        private boolean state;
        private int id;

        public void changeState(){
            state = !state;
        }

        public void reset(){
            state = false;
            super.setBackground(Color.GRAY);
        }

        public void clicked(){
            state = true;
            super.setBackground(Color.GREEN);
        }

        public MoveButton(String text, int id) {
            super(text);
            this.id = id;
            state = false;
        }
    }

    private MoveButton clickedButton;

    private AsideWindow(){
        setSize(240, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.GRAY); //ciao bruno da reby
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setResizable(false);

        JLabel label = new JLabel("Move history");
        label.setFont(new Font(label.getFont().getName(), Font.PLAIN,
                28));
        add(label, BorderLayout.PAGE_START);

        panel = new JPanel();
        panel.setBackground(Color.GRAY);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(panel);
        add(scrollPane);

        JButton reversBoard = new JButton(IconManager.reverseBoardIcon);
        reversBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                instance.bP.reverseBoard();
            }
        });
        add(reversBoard, BorderLayout.PAGE_END);

        setVisible(true);
    }

    public static void initializeAsideWindow(BoardPanel bp, Game game, MovesHistory mH, IconManager iM){
        instance = new AsideWindow();
        instance.bP = bp;
        instance.game = game;
        instance.movesHistory = mH;
        instance.iconManager = iM;
    }

    public static AsideWindow getInstance() {
        return instance;
    }

    public static void addAnElement(Move m){
        MoveButton btn = new MoveButton(m.toString(), instance.moveBtnNumber);
        btn.setFont(new Font(btn.getFont().getName(), Font.PLAIN, 18));
        btn.setBackground(Color.GRAY);
        btn.setForeground(Color.BLACK);
        btn.setIcon(instance.iconManager.getSmallIcon(m.getSourcePiece().getType(), m.getSourcePiece().getColor()));
        btn.addActionListener(new ActionListener() {
            private MoveButton thisButton = btn;
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (instance.clickedButton != null)
                        if(instance.clickedButton != thisButton)
                            instance.clickedButton.reset();

                    instance.clickedButton = thisButton;
                    if (!thisButton.state) {
                        thisButton.clicked();
                        isOnPreviewsBoard = true;
                        instance.bP.displayBoard(instance.movesHistory.getMoveBoard(thisButton.id));
                        if(instance.movesHistory.getMoveCheckR(thisButton.id) != -1)
                            instance.bP.highlightKingCheck(instance.movesHistory.getMoveCheckR(thisButton.id),
                                    instance.movesHistory.getMoveCheckC(thisButton.id));
                    }
                    else {
                        instance.clickedButton = null;
                        thisButton.reset();
                        isOnPreviewsBoard = false;
                        instance.bP.displayBoard(instance.movesHistory.getMoveBoard(-1));
                        if(instance.movesHistory.getMoveCheckR(-1) != -1)
                            instance.bP.highlightKingCheck(instance.movesHistory.getMoveCheckR(-1),
                                    instance.movesHistory.getMoveCheckC(-1));
                    }
                });
            }
        });
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        instance.panel.add(btn);
        SwingUtilities.invokeLater(() -> instance.scrollPane.getVerticalScrollBar().setValue(instance.scrollPane.getVerticalScrollBar().getMaximum()));
        instance.scrollPane.updateUI();
        instance.moveBtnNumber++;
    }
}
