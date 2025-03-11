package gui.GameFrame;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class GameDynamicsListener implements MouseListener, MouseMotionListener {
    private BoardPanel bP;

    public GameDynamicsListener(BoardPanel bP) {
        this.bP = bP;
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
            int c = getTileC(e.getX()), r = getTileR(e.getY());
            bP.onMove(r, c);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

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
        if(!AsideWindow.isOnPreviewsBoard) {
            int c = getTileC(e.getX()), r = getTileR(e.getY());
            bP.movePreview(r, c, false);
        }
    }
}
