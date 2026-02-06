package game;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.TinySound;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Math.random;

public class SoundManager{
    private static AtomicBoolean bgmReady = new AtomicBoolean(false);

    private static Thread soundThread;
    private static Music[] bgms;
    public static void setMasterVolume(double volume){
        if (!TinySound.isInitialized()){
            setMasterVolumeWait(volume);
            return;
        }
        TinySound.setGlobalVolume(volume/100.0);
    }
    public static void setBGMVolume(double volume){
        if (!TinySound.isInitialized()){
            setBGMVolumeWait(volume);
            return;
        }
        for (Music bgm : bgms){
            bgm.setVolume(volume/100.0);
        }
    }
    private static void setMasterVolumeWait(double volume){
        new Thread(() ->{
            try {
                while (!bgmReady.get()){Thread.sleep(50);}
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            setMasterVolume(volume);
        }).start();
    }
    private static void setBGMVolumeWait(double volume){
        new Thread(() ->{
            try {
                while (!bgmReady.get()){Thread.sleep(50);}
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            setBGMVolume(volume);
        }).start();
    }

    public static void startBGM(){
        soundThread = new Thread(SoundManager::playBGM);
        soundThread.start();
    }

    private static void playBGM(){
        TinySound.init();
        bgms = new Music[]{
                TinySound.loadMusic(new File("src/main/res/sounds/bgm/The Golden Armada.wav")),
                TinySound.loadMusic(new File("src/main/res/sounds/bgm/Wings Of Liberty.wav")),
                TinySound.loadMusic(new File("src/main/res/sounds/bgm/Heavens Devils.wav"))
        };
        bgmReady.set(true);
        int bgmNum;
        while (soundThread != null){
            bgmNum = (int)(random()*3);
            bgms[bgmNum].play(false);
            try {
                while (bgms[bgmNum].playing()){
                    Thread.sleep(50);
                }
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
