package gui.StartDialBox;

import Settings.Settings;
import gui.GameFrame.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsPanel extends JFrame{
    private JComboBox<String> selectIconPackage;
    private JComboBox<String> selectSoundPackage;
    private static SettingsPanel instance;

    private StartDialBox startDialBox;

    private static boolean isOpened;

    private SettingsPanel(Image icon, StartDialBox startDialBox) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //Do not wanna leave pending object
        setSize(300, 400);
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

        JButton apply = new JButton("Apply");
        apply.setBorderPainted(false);
        apply.setFocusPainted(false);
        apply.setBounds(195, 315, 75, 30);
        apply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                apply();
            }
        });
        add(apply);

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
        Settings.saveToFile();
        startDialBox.updateIconManager(new IconManager());
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
