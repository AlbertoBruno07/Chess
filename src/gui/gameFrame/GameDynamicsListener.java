package gui.gameFrame;

import gui.asideWindow.AsideWindow;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class GameDynamicsListener implements MouseListener, MouseMotionListener {
    private BoardPanel bP;
    private int r, c;
    
    private static int mouseR, mouseC;

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

    public static int getMouseR() {
        return mouseR;
    }

    public static int getMouseC() {
        return mouseC;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(!AsideWindow.isOnPreviewsBoard) {
            mouseR = r; mouseC = c;
            if(bP.isReversed){
                mouseR = 7-r;
                mouseC = 7-c;
            }
            bP.onMove(mouseR, mouseC);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mouseR = r; mouseC = c;
        if(bP.isReversed){
            mouseR = 7-r;
            mouseC = 7-c;
        }
        if(!AsideWindow.isOnPreviewsBoard) {
            bP.movePreview(mouseR, mouseC, false);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        mouseR = -1; mouseC = -1;
        bP.flushMovePreview();
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
