package chip8;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class Sound {
    private boolean isPlaying;
    private AudioFormat audioFormat;
    private SourceDataLine sourceDataLine;
    private Thread playThread;

    private byte[] buffer;

    public Sound()
    {
        try
        {
            audioFormat = new AudioFormat(Utils.AUDIO_SAMPLE_RATE, Utils.AUDIO_SAMPLE_SIZE, Utils.AUDIO_CHANNELS, true, false);
            sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
            sourceDataLine.open(audioFormat);
            isPlaying = false;
            buffer = new byte[256];

            for (int i = 0; i < buffer.length; i++)
                buffer[i] = 121;

            for (int i = buffer.length / 3; i < 2 * buffer.length / 3; i++)
                buffer[i] = (byte)255-121;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void playSound()
    {
        if(isPlaying) { return; }
        isPlaying = true;
        playThread = new PlayThread();
        playThread.setPriority(Thread.MAX_PRIORITY);
        playThread.start();
    }

    public void stopSound()
    {
        isPlaying = false;
    }

    class PlayThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                sourceDataLine.start();
                do
                {
                    sourceDataLine.write(buffer, 0, buffer.length);
                } while (isPlaying);
                sourceDataLine.stop();
                sourceDataLine.flush();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
