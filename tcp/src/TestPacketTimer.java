import java.util.Timer;

public class TestPacketTimer {
    
    public static void main(String[] args) {
        
        Timer tmr = new Timer();
        int seqBase = 100;
        int seqNum = seqBase;
        int[] packetsArray = new int[100];
        packetsArray[0] = 1010101;
        packetsArray[1] = packetsArray[0]*2;
        packetsArray[2] = packetsArray[0]*3;
        PacketTimer[] pts = new PacketTimer[100];
        //try {
            //pts[0] = new PacketTimer01(tmr, 2000, seqNum+0-seqBase, packetsArray);
            //Thread.sleep(4000);
            //pts[1] = new PacketTimer01(tmr, 2000, seqNum+1-seqBase, packetsArray);
            //Thread.sleep(4000);
            //pts[2] = new PacketTimer01(tmr, 2000, seqNum+2-seqBase, packetsArray);
            
            pts[0] = new PacketTimer(tmr, 2000, seqNum+0-seqBase, packetsArray);
            pts[1] = new PacketTimer(tmr, 4000, seqNum+1-seqBase, packetsArray);
            pts[2] = new PacketTimer(tmr, 6000, seqNum+2-seqBase, packetsArray);
        //} catch(InterruptedException ex) {
        //    ex.printStackTrace();
        //}
        //PacketTimer01 pt = new PacketTimer01(tmr, 2000, seqNum);
        
        boolean done = false;
        while(!done) {
            try {
                //System.out.println("sleeping...");
                Thread.sleep(3000);
                //Thread.sleep(1000);
                pts[1].cancel();
                Thread.sleep(5000);
                done = true;
            } catch(InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        tmr.cancel();
        pts = null;
    }
}
