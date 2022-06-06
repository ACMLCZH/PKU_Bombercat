package thread;

import javax.sound.sampled.*;

import main.Game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayer extends Thread{
    private List<String> musicFiles;
    private boolean run = true;
    private AudioFormat audioFormat;
    private AudioInputStream audioStream;
    private SourceDataLine sourceDataLine; // 源数据线
    //private Game g = null;

    public MusicPlayer(){
    	//this.g=g;
        musicFiles = new ArrayList<String>();
        musicFiles.add("music\\music01.wav");
        try {
        	System.out.print(musicFiles.get(0)+"\n");
            audioStream = AudioSystem.getAudioInputStream(new File(musicFiles.get(0)));
            audioFormat = audioStream.getFormat();
            DataLine.Info datalineinfo = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(datalineinfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void play(){
        while (true){
            try{
                synchronized (this){
                    run=true;
                }
                audioStream = AudioSystem.getAudioInputStream(new File(musicFiles.get(0)));
                int count;
                byte tempBuff[] = new byte[1024];

                while((count = audioStream.read(tempBuff,0,tempBuff.length)) != -1) {
                    synchronized (this) {
                        while (!run)
                            wait();
                    }
                    sourceDataLine.write(tempBuff, 0, count);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() { // 开始进程
        play();
    }

    private void stopMusic(){ //暂停
        synchronized(this){
            run = false;
            notifyAll();
        }
    }
    //继续播放音乐
    private void continueMusic(){
        synchronized(this){
            run = true;
            notifyAll();
        }
    }

    public void stops(){ //外部调用，停止音乐
        new Thread(new Runnable(){
            public void run(){
                stopMusic();
            }
        }).start();
    }
    //外部调用控制方法：继续音频线程
    public void continues(){
        new Thread(new Runnable(){
            public void run(){
                continueMusic();
            }
        }).start();
    }

}
