package gui.GameFrame;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class GameDynamicsListener implements MouseListener, MouseMotionListener {
    private BoardPanel bP;
    private int r, c;

    public GameDynamicsListener(BoardPanel bP, int r, int c) {
        this.bP = bP;
        this.r = r;
        this.c = c;
    }

    public BoardPanel getbP() {
        return bP;
    }

    public void setbP(BoardPanel bP) {
        this.bP = bP;
    }

    private int getTileC(int mouseX){
        return mouseX/BoardPanel.TILE_DIMENSION;
    }

    private int getTileR(int mouseY){
        return mouseY/BoardPanel.TILE_DIMENSION;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(!AsideWindow.isOnPreviewsBoard) {
            int R = r, C = c;
            if(bP.isReversed){
                R = 7-r;
                C = 7-c;
            }
            bP.onMove(R, C);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(!AsideWindow.isOnPreviewsBoard) {
            int R = r, C = c;
            if(bP.isReversed){
                R = 7-r;
                C = 7-c;
            }
            bP.movePreview(R, C, false);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        bP.flushMovePreview();
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
