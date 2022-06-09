package thread;

import javax.sound.sampled.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayer extends Thread{
    private List<String> musicFiles;
    private boolean run = true;
    private AudioFormat audioFormat;
    private AudioInputStream audioStream;
    private SourceDataLine sourceDataLine; // Դ������
    private boolean isLoop = true;
    //private Game g = null;

    public MusicPlayer(String msc,boolean isLoop){
    	//this.g=g;
    	this.isLoop=isLoop;
        musicFiles = new ArrayList<String>();
        musicFiles.add(msc);
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
    	if(isLoop)
    	{
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
    	else
    	{
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
    public void run() { // ��ʼ����
        play();
    }

    private void stopMusic(){ //��ͣ
        synchronized(this){
            run = false;
            notifyAll();
        }
    }
    //������������
    private void continueMusic(){
        synchronized(this){
            run = true;
            notifyAll();
        }
    }
    
    public void stops(){ //�ⲿ���ã�ֹͣ����
        new Thread(new Runnable(){
            public void run(){
                stopMusic();
            }
        }).start();
    }
    //�ⲿ���ÿ��Ʒ�����������Ƶ�߳�
    public void continues(){
        new Thread(new Runnable(){
            public void run(){
                continueMusic();
            }
        }).start();
    }

}
