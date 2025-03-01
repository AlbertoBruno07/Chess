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

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX(), y = e.getY();
        int c = x/BoardPanel.TILE_DIMENSION, r = y/BoardPanel.TILE_DIMENSION;
        bP.onMove(r, c);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
