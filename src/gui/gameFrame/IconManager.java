package gui.gameFrame;

import settings.Settings;
import core.PieceType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import core.Color;

public class IconManager {

    private ImageIcon[] icons;
    private JLabel blackCheckMate, whiteCheckMate;
    private String iconPackage;

    public static ImageIcon reverseBoardIcon;

    public IconManager() {
        icons = new ImageIcon[12];
        iconPackage = Settings.getSelectedIconPackage();
        loadIcons();
    }

    public void loadIcons() {

        reverseBoardIcon = new ImageIcon((new ImageIcon(
                IconManager.class.getClassLoader().getResource("./icons/General/ReverseBoard.png"))).
                getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));

        for(int i = 0; i < 12; i++)
            icons[i] = getIcon(i);

        BufferedImage img = null;
        try {
            img = ImageIO.read(getIconURL(0));
        } catch (IOException e) {
            System.out.println("[IconManager] Cannot read icon");
        }
        BufferedImage rImg = new BufferedImage(img.getHeight(), img.getWidth(), 6); //Force to use a specific type of image
        Graphics2D g2d = rImg.createGraphics();
        g2d.rotate(Math.PI /2, img.getHeight()/2.0f, img.getWidth()/2.0f);
        g2d.drawImage(img, null, 0, 0);
        g2d.dispose();
        whiteCheckMate = new JLabel(new ImageIcon(rImg.getScaledInstance(BoardPanel.TILE_DIMENSION, BoardPanel.TILE_DIMENSION, Image.SCALE_SMOOTH)));

        img = null;
        try {
            img = ImageIO.read(getIconURL(6));
        } catch (IOException e) {
            System.out.println("[IconManager] Cannot read icon");
        }
        rImg = new BufferedImage(img.getHeight(), img.getWidth(), 6); //Force to use a specific type of image
        g2d = rImg.createGraphics();
        g2d.rotate(Math.PI /2, img.getHeight()/2.0f, img.getWidth()/2.0f);
        g2d.drawImage(img, null, 0, 0);
        g2d.dispose();
        blackCheckMate = new JLabel(new ImageIcon(rImg.getScaledInstance(BoardPanel.TILE_DIMENSION, BoardPanel.TILE_DIMENSION, Image.SCALE_SMOOTH)));
    }

    public JLabel getCheckMateLabel(Color c){
        return c == Color.WHITE ? whiteCheckMate : blackCheckMate;
    }

    private ImageIcon getIcon(int code){
        URL url = getIconURL(code);
        return new ImageIcon((new ImageIcon(url)).getImage().getScaledInstance(BoardPanel.TILE_DIMENSION, BoardPanel.TILE_DIMENSION, Image.SCALE_SMOOTH));
    }

    public ImageIcon getIcon(PieceType pT, Color c){
        return icons[pieceCode(pT, c)];
    }

    public ImageIcon getSmallIcon (PieceType pT, Color c){
        return new ImageIcon(icons[pieceCode(pT, c)].getImage().getScaledInstance(50,50, Image.SCALE_SMOOTH));
    }

    public URL getIconURL(int code){
        String iconName = makeIconName(code);
        return IconManager.class.getClassLoader().getResource(iconName);
    }

    private String makeIconName(int code){
        return "icons/" + Settings.getSelectedIconPackage() + "/" + pieceIconName(code) + ".png";
    }

    private int pieceCode(PieceType pT, Color c) {
        int code = c == Color.WHITE ? 0 : 6;
        return code + switch (pT){
            case KING -> 0;
            case PAWN -> 1;
            case ROOK -> 2;
            case QUEEN -> 3;
            case BISHOP -> 4;
            case KNIGHT -> 5;
        };
    }

    private String pieceIconName(int code){
        String iconName = code > 5 ? "B" : "W";
        if(code > 5)
            code -= 6;
        return iconName + switch (code){
            case 0 -> "K";
            case 1 -> "P";
            case 2 -> "R";
            case 3 -> "Q";
            case 4 -> "B";
            case 5 -> "N";
            default -> throw new IllegalStateException("[IconManager] Unexpected value: " + code);
        };
    }

}
