package gui.gameFrame;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import settings.Settings;

public class PlaySound {
    private static PlaySound instance;
    private Clip move;
    private Clip capture;

    private SwingWorker initialisationWorker;

    public static void initializePlaySound(){
        instance = new PlaySound();

        instance.initialisationWorker = new SwingWorker<Void, Void>(){
            @Override
            protected Void doInBackground() throws Exception {
                String soundPackage = Settings.getSelectedSoundPackage();

                try {
                    long inT = System.nanoTime();
                    instance.move = AudioSystem.getClip();
                    System.out.println("[getClip - Move] exT: " + (System.nanoTime() - inT));
                    inT = System.nanoTime();
                    instance.move.open(AudioSystem.getAudioInputStream(instance.getClass().getClassLoader().getResource("./sounds/" + soundPackage + "/Move.wav")));
                    System.out.println("[open - Move] exT: " + (System.nanoTime() - inT));
                } catch (UnsupportedAudioFileException e) {
                    System.out.println("[Sound] Unsupported audio file for Move");
                } catch (IOException e) {
                    System.out.println("[Sound] Cannot read audio file for Move");
                } catch (LineUnavailableException e) {
                    System.out.println("[Sound] Cannot open audio clip");
                }

                try {
                    instance.capture = AudioSystem.getClip();
                    instance.capture.open(AudioSystem.getAudioInputStream((instance.getClass().getClassLoader().getResource("./sounds/" + soundPackage + "/Capture.wav"))));
                } catch (UnsupportedAudioFileException e) {
                    System.out.println("[Sound] Unsupported audio file for Capture");
                } catch (IOException e) {
                    System.out.println("[Sound] Cannot read audio file for Capture");
                } catch (LineUnavailableException e) {
                    System.out.println("[Sound] Cannot open audio clip");
                }
                return null;
            }
        };

        instance.initialisationWorker.execute();
    }

    private static Thread makeCaptureWorker() {
        return new Thread(() ->{
            instance.capture.setFramePosition(0);
            instance.capture.start();
            Thread.currentThread().interrupt();
        });
    }

    private static Thread makeMoveWorker() {
        return new Thread(() -> {
            instance.move.setFramePosition(0);
            instance.move.start();
            Thread.currentThread().interrupt();
        });
    }

    public static void playMove(){
        waitForInitialization();
        makeMoveWorker().start();
    }

    public static void playCapture(){
        waitForInitialization();
        makeCaptureWorker().start();
    }

    private static void waitForInitialization() {
        try {
            instance.initialisationWorker.get();
        } catch (InterruptedException e) {
            System.out.println(e);
        } catch (ExecutionException e) {
            System.out.println(e);
        }
    }

}
