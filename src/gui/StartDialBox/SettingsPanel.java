package gui.StartDialBox;

import Settings.Settings;
import gui.GameFrame.IconManager;
import gui.GameFrame.PlaySound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsPanel extends JFrame{
    private JComboBox<String> selectIconPackage;
    private JComboBox<String> selectSoundPackage;
    private static SettingsPanel instance;
    private JButton colorPicker1, colorPicker2, colorPicker3, colorPicker4;

    private StartDialBox startDialBox;

    private static boolean isOpened;

    private SettingsPanel(Image icon, StartDialBox startDialBox) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //Do not wanna leave pending object
        setSize(300, 410);
        setIconImage(icon);
        getContentPane().setBackground(Color.BLACK);
        setLayout(null);
        setResizable(false);
        setTitle("JavaChess - Settings");

        JLabel setIcon = new JLabel("Icon Pack:");
        setIcon.setSize(100, 50);
        setIcon.setLocation(40, 15);
        setIcon.setForeground(Color.WHITE);
        add(setIcon);

        selectIconPackage = new JComboBox<>(Settings.possibleIconPacks);
        selectIconPackage.setBounds(150, 50, 120, 20);
        selectIconPackage.setLocation(130, 30);
        setSelectedIconPackageAsDefault();
        add(selectIconPackage);

        JLabel setSound = new JLabel("Sound Pack:");
        setSound.setSize(100, 50);
        setSound.setLocation(40, 65);
        setSound.setForeground(Color.WHITE);
        add(setSound);

        selectSoundPackage = new JComboBox<>(Settings.possibleSoundPacks);
        selectSoundPackage.setBounds(150, 50, 120, 20);
        selectSoundPackage.setLocation(130, 80);
        setSelectedSoundPackageAsDefault();
        add(selectSoundPackage);

        JLabel c1 = new JLabel("Color 1");
        c1.setSize(100, 50);
        c1.setLocation(50, 115);
        c1.setForeground(Color.WHITE);
        add(c1);

        colorPicker1 = new JButton();
        colorPicker1.setSize(30,30);
        colorPicker1.setLocation(160, 125);
        colorPicker1.setBackground(Settings.getColor1());
        colorPicker1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color c = JColorChooser.showDialog(
                        null,
                        "Color picker - 1",
                        Settings.getColor1());
                colorPicker1.setBackground(c);
            }
        });
        add(colorPicker1);

        JLabel c2 = new JLabel("Color 2");
        c2.setSize(100, 50);
        c2.setLocation(50, 165);
        c2.setForeground(Color.WHITE);
        add(c2);

        colorPicker2 = new JButton();
        colorPicker2.setSize(30,30);
        colorPicker2.setLocation(160, 175);
        colorPicker2.setBackground(Settings.getColor2());
        colorPicker2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color c = JColorChooser.showDialog(
                        null,
                        "Color picker - 2",
                        Settings.getColor2());
                colorPicker2.setBackground(c);
            }
        });
        add(colorPicker2);

        JLabel c3 = new JLabel("Color 3");
        c3.setSize(100, 50);
        c3.setLocation(50, 215);
        c3.setForeground(Color.WHITE);
        add(c3);

        colorPicker3 = new JButton();
        colorPicker3.setSize(30,30);
        colorPicker3.setLocation(160, 225);
        colorPicker3.setBackground(Settings.getColor3());
        colorPicker3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color c = JColorChooser.showDialog(
                        null,
                        "Color picker - 3",
                        Settings.getColor3());
                colorPicker3.setBackground(c);
            }
        });
        add(colorPicker3);

        JLabel c4 = new JLabel("Color 4");
        c4.setSize(100, 50);
        c4.setLocation(50, 265);
        c4.setForeground(Color.WHITE);
        add(c4);

        colorPicker4 = new JButton();
        colorPicker4.setSize(30,30);
        colorPicker4.setLocation(160, 275);
        colorPicker4.setBackground(Settings.getColor4());
        colorPicker4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color c = JColorChooser.showDialog(
                        null,
                        "Color picker - 4",
                        Settings.getColor4());
                colorPicker4.setBackground(c);
            }
        });
        add(colorPicker4);

        JButton apply = new JButton("Apply");
        apply.setBorderPainted(false);
        apply.setBounds(195, 325, 75, 30);
        apply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                apply();
            }
        });
        add(apply);

        JButton reset = new JButton("Reset");
        reset.setBorderPainted(false);
        reset.setFocusPainted(false);
        reset.setBounds(105, 325, 75, 30);
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Settings.initializeSettingsToDefault();
                Settings.saveToFile();
                instance.dispose();
                instance = new SettingsPanel(icon, startDialBox);
                instance.setVisible(true);
            }
        });
        add(reset);


        this.startDialBox = startDialBox;
    }

    private void setSelectedSoundPackageAsDefault() {
        selectSoundPackage.setSelectedItem(Settings.getSelectedSoundPackage());
    }

    private void setSelectedIconPackageAsDefault() {
        selectIconPackage.setSelectedItem(Settings.getSelectedIconPackage());
    }

    private void apply() {
        Settings.setSelectedIconPackage((String) selectIconPackage.getSelectedItem());
        Settings.setSelectedSoundPackage((String) selectSoundPackage.getSelectedItem());
        Settings.setColor1(colorPicker1.getBackground());
        Settings.setColor2(colorPicker2.getBackground());
        Settings.setColor3(colorPicker3.getBackground());
        Settings.setColor4(colorPicker4.getBackground());
        Settings.saveToFile();
        startDialBox.updateIconManager(new IconManager());
        long iniT = System.nanoTime();
        PlaySound.initializePlaySound();
        System.out.println("[Playsound] exT = " + (System.nanoTime() - iniT));
        startDialBox.makeGameFrame();
        dispose();
    }

    public static SettingsPanel getInstance(Image icon, StartDialBox startDialBox){
        isOpened = true;
        if(instance == null)
            instance = new SettingsPanel(icon, startDialBox);
        instance.setVisible(true);
        return instance;
    }

    @Override
    public void dispose() {
        isOpened = false;
        super.dispose();
    }

    public static boolean isOpened(){
        return isOpened;
    }

}
