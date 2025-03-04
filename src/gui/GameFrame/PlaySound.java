package gui.GameFrame;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import Settings.Settings;

public class PlaySound {
    private static PlaySound instance = new PlaySound();
    private Clip move;
    private Clip capture;

    public static void initializePlaySound(){
        String soundPackage = Settings.getSelectedSoundPackage();
        try {
            instance.move = AudioSystem.getClip();
            instance.move.open(AudioSystem.getAudioInputStream((new File("src/sounds/"+soundPackage+"/Move.wav")).getAbsoluteFile()));
        } catch (UnsupportedAudioFileException e) {
            System.out.println("[Sound] Unsupported audio file for Move");
        } catch (IOException e) {
            System.out.println("[Sound] Cannot read audio file for Move");
        } catch (LineUnavailableException e) {
            System.out.println("[Sound] Cannot open audio clip");
        }
        try {
            instance.capture = AudioSystem.getClip();
            instance.capture.open(AudioSystem.getAudioInputStream((new File("src/sounds/"+soundPackage+"/Capture.wav")).getAbsoluteFile()));
        } catch (UnsupportedAudioFileException e) {
            System.out.println("[Sound] Unsupported audio file for Capture");
        } catch (IOException e) {
            System.out.println("[Sound] Cannot read audio file for Capture");
        }catch (LineUnavailableException e) {
            System.out.println("[Sound] Cannot open audio clip");
        }
    }

    public static void playMove(){
        instance.move.start();
        instance.move.setFramePosition(0);
    }

    public static void playCapture(){
        instance.capture.start();
        instance.capture.setFramePosition(0);
    }

}
