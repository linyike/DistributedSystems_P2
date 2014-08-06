/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CS715_P2_Server;

import java.util.logging.Level;
import java.util.logging.Logger;
import static CS715_P2_Server.MyObject.random;

/**
 *
 * @author hke
 */
public class Clock extends Thread {

    private Database db;
    private int startTime = 10;  // first Session starts 
    private int presentationTime = 15;
    private int movieTime = 60;
    private int QnATime = 15;
    private int breakTime = 15;
    private int sessionCapacity = 6;
    public static long time = System.currentTimeMillis();

    public Clock(Database db) {
        this.db = db;
        new Thread(this).start();
    }

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - time) + "] clock: " + m);
    }

    @Override
    public void run() {
        int sID = 0;
        int napping;
        try {
            msg(" Day Begin");
            db.cStartDay(sessionCapacity);
            while (!db.getReady()) {
                Thread.sleep(100);
            }
            napping = 5000 + (int) random(startTime);
            Thread.sleep(napping);
            for (sID = 0; sID < 3; sID++) {
                msg(" Starts Session" + sID);
                db.cStartSession(sID); // session duration: 15+60+15 min 
                napping = presentationTime;
                Thread.sleep(napping);// presentation: 15 mins
                db.cStartMovie(sID);
                napping = movieTime;
                Thread.sleep(napping);// movie: 60 mins
                msg(" Ends Movie of Session" + sID);
                db.cEndMovie(sID);
                napping = QnATime;
                Thread.sleep(napping);// Q&A: 15 mins
                msg(" Ends Session" + sID);
                db.cEndSession(sID);
                if (sID < 2) {
                    msg(" Has A Break of 15 Minutes");
                    napping = breakTime;
                    Thread.sleep(napping); // break: 15mins
                } else {
                    msg(" Day Ends");
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Clock.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
