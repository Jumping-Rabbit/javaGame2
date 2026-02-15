package game;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.TinySound;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Math.random;

public class SoundManager{

    private static Thread soundThread;
    private static Music[] bgms;
    private static AtomicBoolean isInit = new AtomicBoolean(false);
    private static void setBGMs(){
        bgms = new Music[]{
                TinySound.loadMusic(new File("src/main/res/sounds/bgm/The Golden Armada.wav")),
                TinySound.loadMusic(new File("src/main/res/sounds/bgm/Wings Of Liberty.wav")),
                TinySound.loadMusic(new File("src/main/res/sounds/bgm/Heavens Devils.wav"))
        };
    }
    public static void setMasterVolume(double volume){
        if (!isInit.get()){
            TinySound.init();
            setBGMs();
            isInit.set(true);
        }
        TinySound.setGlobalVolume(volume/100.0);
    }
    public static void setBGMVolume(double volume){
        if (!isInit.get()){
            TinySound.init();
            setBGMs();
            isInit.set(true);
        }
        for (Music bgm : bgms){
            bgm.setVolume(volume/100.0);
        }
    }

    public static void startBGM(){
        soundThread = new Thread(SoundManager::playBGM);
        soundThread.start();
    }

    private static void playBGM(){
        if (!isInit.get()) {
            TinySound.init();
            setBGMs();
            isInit.set(true);
        }

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
