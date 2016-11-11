import java.util.Timer;
import java.util.TimerTask;
//import java.lang.InterruptedException;
//import java.util.Vector;

public class PacketTimer {
    
    TimerTask tmrTask;
    int[] array;
    
    
    public PacketTimer(Timer pktTimer, int timeout, int seqNum, int[] pktArray) {
        final int seqNumFinal = seqNum;
        array = pktArray;
        tmrTask = new TimerTask() {
            @Override
            public void run()
            {
                //System.out.println("new timer task");
                resend(seqNumFinal);
            }
        };
        timerMethod(pktTimer, timeout);
    }
    
    private void timerMethod(Timer tmr, int tmOut) {
        tmr.schedule(tmrTask, tmOut);
    } 
    

    private void resend(int seqNum) {
        //System.out.println("array[seqNum]: " + array[seqNum]);
        System.out.println("resending packet: " + array[seqNum]);
    }
    
    public void cancel() {
        tmrTask.cancel();
    }
    
    static public int calcTimeout() {
    	return 0;
    }
    
}
