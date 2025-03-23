package gui.asideWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static gui.asideWindow.AsideWindow.updateScore;
import static gui.asideWindow.AsideWindow.getInstance;
import static gui.asideWindow.AsideWindow.isOnPreviewsBoard;

public class ElementActionListener implements ActionListener {
    private MoveButton thisButton;

    public ElementActionListener(MoveButton thisButton) {
        this.thisButton = thisButton;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {

            if (getInstance().clickedButton != null)
                if(getInstance().clickedButton != thisButton)
                    getInstance().clickedButton.reset();

            getInstance().clickedButton = thisButton;
            if (!thisButton.state) {
                updateScore(core.Color.BLACK, getInstance().movesHistory.
                        getMoveScoreBlack(thisButton.id));
                updateScore(core.Color.WHITE, getInstance().movesHistory.
                        getMoveScoreWhite(thisButton.id));
                thisButton.clicked();
                isOnPreviewsBoard = true;
                getInstance().bP.setCheckCoords(getInstance().movesHistory.getMoveCheckR(thisButton.id),
                        getInstance().movesHistory.getMoveCheckC(thisButton.id));
                getInstance().bP.setLastSourceCoords(getInstance().movesHistory.getMoveSourceR(thisButton.id),
                        getInstance().movesHistory.getMoveSourceC(thisButton.id));
                getInstance().bP.displayBoard(getInstance().movesHistory.getMoveBoard(thisButton.id));
            }
            else {
                updateScore(core.Color.BLACK, getInstance().movesHistory.
                        getMoveScoreBlack(-1));
                updateScore(core.Color.WHITE, getInstance().movesHistory.
                        getMoveScoreWhite(-1));
                getInstance().clickedButton = null;
                thisButton.reset();
                isOnPreviewsBoard = false;
                getInstance().bP.setCheckCoords(getInstance().movesHistory.getMoveCheckR(-1),
                        getInstance().movesHistory.getMoveCheckC(-1));
                getInstance().bP.setLastSourceCoords(getInstance().movesHistory.getMoveSourceR(-1),
                        getInstance().movesHistory.getMoveSourceC(-1));
                getInstance().bP.displayBoard(getInstance().movesHistory.getMoveBoard(-1));
            }
        });
    }
}
