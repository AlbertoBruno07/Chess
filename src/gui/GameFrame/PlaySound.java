package gui.GameFrame;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import Settings.Settings;

public class PlaySound {
    private static PlaySound instance = new PlaySound();
    private Clip move;
    private Clip capture;

    public static void initializePlaySound(){
        String soundPackage = Settings.getSelectedSoundPackage();

        try {
            instance.move = AudioSystem.getClip();
            instance.move.open(AudioSystem.getAudioInputStream(instance.getClass().getClassLoader().getResource("./sounds/"+soundPackage+"/Move.wav")));
        } catch (UnsupportedAudioFileException e) {
            System.out.println("[Sound] Unsupported audio file for Move");
        } catch (IOException e) {
            System.out.println("[Sound] Cannot read audio file for Move");
        } catch (LineUnavailableException e) {
            System.out.println("[Sound] Cannot open audio clip");
        }

        try {
            instance.capture = AudioSystem.getClip();
            instance.capture.open(AudioSystem.getAudioInputStream((instance.getClass().getClassLoader().getResource("./sounds/"+soundPackage+"/Capture.wav"))));
        } catch (UnsupportedAudioFileException e) {
            System.out.println("[Sound] Unsupported audio file for Capture");
        } catch (IOException e) {
            System.out.println("[Sound] Cannot read audio file for Capture");
        }catch (LineUnavailableException e) {
            System.out.println("[Sound] Cannot open audio clip");
        }

    }

    public static void playMove(){

        SwingUtilities.invokeLater( () -> {
            instance.move.setFramePosition(0);
            instance.move.start();
        });

    }

    public static void playCapture(){

        SwingUtilities.invokeLater( () -> {
            instance.capture.setFramePosition(0);
            instance.capture.start();
        });

    }

}
