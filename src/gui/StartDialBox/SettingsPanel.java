package gui.StartDialBox;

import Settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsPanel extends JFrame{
    private JComboBox<String> selectIconPackage;
    private static SettingsPanel instance;

    private static boolean isOpened;

    private SettingsPanel(Image icon) {
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
    }

    private void setSelectedIconPackageAsDefault() {
        selectIconPackage.setSelectedItem(Settings.getSelectedIconPackage());
    }

    private void apply() {
        Settings.setSelectedIconPackage((String) selectIconPackage.getSelectedItem());
        Settings.saveToFile();
        dispose();
    }

    public static SettingsPanel getInstance(Image icon){
        isOpened = true;
        if(instance == null)
            instance = new SettingsPanel(icon);
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
