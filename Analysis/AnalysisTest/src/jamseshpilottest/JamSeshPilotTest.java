
package jamseshpilottest;

import be.hogent.tarsos.dsp.AudioDispatcher;
import be.hogent.tarsos.dsp.AudioEvent;
import be.hogent.tarsos.dsp.AudioPlayer;
import be.hogent.tarsos.dsp.AudioProcessor;
import be.hogent.tarsos.dsp.filters.HighPass;
import be.hogent.tarsos.dsp.pitch.PitchDetectionHandler;
import be.hogent.tarsos.dsp.pitch.PitchDetectionResult;
import be.hogent.tarsos.dsp.pitch.PitchProcessor;
import be.hogent.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import java.applet.Applet;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author thomaspinella
 */
public class JamSeshPilotTest extends Applet implements ActionListener, MouseMotionListener, MouseListener
{
    AudioDispatcher dispatch;
    Canvas C1;
    Graphics myG;
    int width = 800;
    int height = 500;
    int counter = 0;
    float p = 0;
    
    public void init()
    {
        setSize(width, height);
        C1 = new Canvas();
        C1.setSize(width, height);
        C1.setBackground(Color.white);
        add(C1);
        C1.addMouseListener(this);
        C1.addMouseMotionListener(this);
        myG = C1.getGraphics();
        myG.setColor(Color.black);
        try 
        {
            AudioDispatcher d = AudioDispatcher.fromDefaultMicrophone(1024, 0);
            float sr = 44100;//The sample rate
            //High pass filter, let everything pass above 20Hz
            AudioProcessor highPass = new HighPass(20, sr);
            d.addAudioProcessor(highPass);
            //Pitch detection, print estimated pitches on standard out
            PitchDetectionHandler printPitch = new PitchDetectionHandler() 
            {
                @Override
                public void handlePitch(PitchDetectionResult pitchDetectionResult,AudioEvent audioEvent) 
                {
                    if (pitchDetectionResult.getPitch() != -1)
                    {
                        float pitch = pitchDetectionResult.getPitch();
                        myG.fillRect(counter, height - (int)(pitch / 10), 1, height - (int)(pitch / 10));
                        System.out.println(pitch);
                        p = pitch;
                        counter++;
                        if (counter >= 800)
                        {
                            counter = 0;
                            myG.setColor(Color.white);
                            myG.fillRect(0, 0, width, height);
                            myG.setColor(Color.black);
                        }
                    }
                }
                
                
            };
            PitchEstimationAlgorithm algo = PitchEstimationAlgorithm.AMDF; //Yin sucks. MPM is good. AMDF is better.
            AudioProcessor pitchEstimator = new PitchProcessor(algo, sr, 1024, printPitch);
            d.addAudioProcessor(pitchEstimator);
            //Add an audio effect (delay)s
            //Mix some noise with the audio (synthesis)
            //d.addAudioProcessor(new NoiseGenerator(0.3));
            //Play the audio on the loudspeakers
            //d.addAudioProcessor(new AudioPlayer(new AudioFormat(sr, 16, 1, true,true)));
            dispatch = d; //Doesn't necessarily have to be global...
            
            Thread threadOne = new Thread(dispatch);
            threadOne.start();
            
            Runnable runnableSynthesize = new Runnable()
            {
                private float oldP = p;
                public void run()
                {
                    while (true)
                    {
                        if (oldP == p)
                        {
                            generateSound(p);
                        }
                        oldP = p;
                        
                    }
                }
                
                public void generateSound(float frequency)
                {
                    double sampleRate = 44100.0;
                    frequency *= (3/2);
                    double amplitude = 1.0; 
                    double seconds = 1.0;
                    double twoPiF = (float)(2 * Math.PI * frequency);
                    float[] buffer = new float[(int) (seconds * sampleRate)];
                    for (int sample = 0; sample < buffer.length; sample++) 
                    {
                        double time = sample / sampleRate;
                        buffer[sample] = (float)(amplitude * Math.sin(twoPiF * time));
                    }
                    
                    
                    final byte[] byteBuffer = new byte[buffer.length * 2];
                    int bufferIndex = 0;
                    for (int i = 0; i < byteBuffer.length; i++) 
                    {
                        final int x = (int) (buffer[bufferIndex++] * 32767.0);
                        byteBuffer[i] = (byte) x;
                        i++;
                        byteBuffer[i] = (byte) (x >>> 8);
                    }
                    
                    try
                    {
                        boolean bigEndian = false;
                        boolean signed = true;
                        int bits = 16;
                        int channels = 1;
                        AudioFormat format;
                        format = new AudioFormat((float)sampleRate, bits, channels, signed, bigEndian);
                        
                        SourceDataLine line;
                        DataLine.Info info;
                        info = new DataLine.Info(SourceDataLine.class, format);
                        line = (SourceDataLine) AudioSystem.getLine(info);
                        line.open(format);
                        line.start();
                        line.write(byteBuffer, 0, byteBuffer.length);
                        line.close();
                    } catch(Exception e) {}
                }
            };
            
            Thread threadTwo = new Thread(runnableSynthesize);
            threadTwo.start();
        } catch(Exception e) {}
    }
    
    public void actionPerformed(ActionEvent e)
    {
        
    }

    public void mousePressed(MouseEvent e)
    {
        
    }
    
    public void mouseClicked(MouseEvent e)
    {
        
    }
    
    public void mouseEntered(MouseEvent e)
    {
    
    }
    
    public void mouseExited(MouseEvent e)
    {
    
    }
    public void mouseReleased(MouseEvent e)
    {
    
    }
    
    public void mouseDragged(MouseEvent e)
    {
    
    }
    
    public void mouseMoved(MouseEvent e)
    {
        
    }
    
    public void paint(Graphics g)
    {
        C1.setLocation(0, 0);
    }
}
