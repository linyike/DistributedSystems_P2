/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CS715_P2_Server;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author hke
 */
class Database extends MyObject {

    private int[] seatMax = new int[3];
    private AtomicInteger[] waitSessionNum = new AtomicInteger[3];
    private AtomicInteger[] audienceNum = new AtomicInteger[3];
    private AtomicInteger[] inSession2Num = new AtomicInteger[3];
    private boolean[] sessionBegin = new boolean[3];
    private boolean[] movieEnd = new boolean[3];
    private boolean[] sessionEnd = new boolean[3];

    private Vector[] waitingSessionStarts = new Vector[3];
    private Vector[] inSession = new Vector[3];
    private Vector[] waitingSession1 = new Vector[3];
    private Vector[] waitingAsk = new Vector[3];
    private Vector[] waitingSessionEnd = new Vector[3];

    private Vector[] waitingPresentation = new Vector[3];
    private Vector[] inPresentation = new Vector[3];
    private Vector[] waitingQnA = new Vector[3];

    private boolean ready = false;

    boolean getReady() {
        return ready;
    }

    public Database() {
        int i;
        for (i = 0; i < 3; i++) {
            waitingSessionStarts[i] = new Vector();
            inSession[i] = new Vector();
            waitingSession1[i] = new Vector();
            waitingAsk[i] = new Vector();
            waitingSessionEnd[i] = new Vector();

            waitingPresentation[i] = new Vector();
            inPresentation[i] = new Vector();
            waitingQnA[i] = new Vector();

            waitSessionNum[i] = new AtomicInteger(0);
            seatMax[i] = 0;
            audienceNum[i] = new AtomicInteger(0);
            sessionBegin[i] = false;
            movieEnd[i] = false;
            inSession2Num[i] = new AtomicInteger(0);
            sessionEnd[i] = false;
        }
    }

    public void waitSession(int sID) {
        waitSessionNum[sID].incrementAndGet();
        synchronized (waitingSessionStarts[sID]) {
            while (!sessionBegin[sID]) {
                try {
                    waitingSessionStarts[sID].wait();
                } catch (InterruptedException e) {
                    System.out.println("be notified all interrupt");
                    continue;
                }
            }
        }
    }

    public int getSeat(int sID) {
        int numOfSeat = -1;
        if (audienceNum[sID].get() < seatMax[sID]) {
            numOfSeat = audienceNum[sID].incrementAndGet();
            if ((audienceNum[sID].get() == seatMax[sID]) || (audienceNum[sID].get() == waitSessionNum[sID].get())) {
                if (waitingPresentation[sID].size() > 0) {
                    synchronized (waitingPresentation[sID].elementAt(0)) {
                        waitingPresentation[sID].elementAt(0).notify();
                    }
                }
            }
            return numOfSeat;
        } else {
            return numOfSeat;
        }
    }

    public void in1Session(int sID) {    //presentation and movie session
        synchronized (waitingSession1[sID]) {
            while (!movieEnd[sID]) {
                try {
                    waitingSession1[sID].wait();
                } catch (InterruptedException e) {
                    System.out.println("be notified all interrupt");
                    continue;
                }
            }
        }
    }

    public void in2Session(String name, int sID) { //question-answer session
        Object convey = new Object();
        int action;
        action = (int) random(2);

        if (action == 0) { // has question
            System.out.println("    " + name + " has questions.");
            inSession2Num[sID].incrementAndGet();
            waitingAsk[sID].addElement(convey);
            if ((inSession2Num[sID].get() == audienceNum[sID].get()) && waitingQnA[sID].size() > 0) {
                synchronized (waitingQnA[sID].elementAt(0)) {
                    waitingQnA[sID].elementAt(0).notify();
                }
            }
            synchronized (convey) {
                while (true) // wait to be notified, not interrupted
                {
                    try {
                        convey.wait();
                        break;
                    } // notify() after interrupt() race condition ignored
                    catch (InterruptedException e) {
                        continue;
                    }
                }
            }
            System.out.println("    " + name + " gets answer.");
        } else {    //no question
            inSession2Num[sID].incrementAndGet();
            System.out.println("    " + name + " has no question.");
            if (inSession2Num[sID].get() == audienceNum[sID].get()&& waitingQnA[sID].size() > 0) {
                synchronized (waitingQnA[sID].elementAt(0)) {
                    waitingQnA[sID].elementAt(0).notify();
                }
            }
        }
        waitSessionEnd(sID);
    }

    public void sWaitPresentation(int sID) {
        ready = true;

        Object convey = new Object();
        synchronized (convey) {
            waitingPresentation[sID].addElement(convey);
            while (true) // wait to be notified, not interrupted
            {
                try {
                    convey.wait();
                    break;
                } // notify() after interrupt() race condition ignored
                catch (InterruptedException e) {
                    continue;
                }
            }
        }
    }

    public void sInPresentation(int sID) {
        Object convey = new Object();
        synchronized (convey) {
            inPresentation[sID].addElement(convey);
            while (true) // wait to be notified, not interrupted
            {
                try {
                    convey.wait();
                    break;
                } // notify() after interrupt() race condition ignored
                catch (InterruptedException e) {
                    continue;
                }
            }
        }
    }

    public void sWaitQnA(int sID) throws InterruptedException {
        Object convey = new Object();

        synchronized (convey) {

            waitingQnA[sID].addElement(convey);
            while (true) // wait to be notified, not interrupted
            {
                try {
                    convey.wait();
                    break;
                } // notify() after interrupt() race condition ignored
                catch (InterruptedException e) {
                    continue;
                }
            }
            Thread.sleep(10);
        }
    }

    public void sAnswerQ(int sID, int aTime) throws InterruptedException {
        int naptime;
        if (waitingAsk[sID].size() > 0) {
            naptime = (int) random(aTime / waitingAsk[sID].size());
            while (waitingAsk[sID].size() > 0) {
                synchronized (waitingAsk[sID].elementAt(0)) {
                    waitingAsk[sID].elementAt(0).notify();
                }
                waitingAsk[sID].removeElementAt(0);

                Thread.sleep(naptime);
            }
        }
    }

    public void waitSessionEnd(int sID) {    //both vistors and speake wait here
        synchronized (waitingSessionEnd[sID]) {
            while (!sessionEnd[sID]) {
                try {
                    waitingSessionEnd[sID].wait();//addElement(convey);
                } catch (InterruptedException e) {
                    System.out.println("be notified all interrupt");
                    continue;
                }
            }

        }
    }

    public void cStartDay(int sc) {
        int i;
        for (i = 0; i < 3; i++) {
            seatMax[i] = sc;
        }
    }

    public void cStartSession(int sID) {
        sessionBegin[sID] = true;
        if (waitSessionNum[sID].get() > 0) {
            synchronized (waitingSessionStarts[sID]) {
                waitingSessionStarts[sID].notifyAll();
            }
        }
    }

    public void cStartMovie(int sID) {
        if (inPresentation[sID].size() > 0) {
            synchronized (inPresentation[sID].elementAt(0)) {
                inPresentation[sID].elementAt(0).notify();
            }
            inPresentation[sID].removeElementAt(0);
        }
    }

    public void cEndMovie(int sID) {
        movieEnd[sID] = true;

        synchronized (waitingSession1[sID]) {
            waitingSession1[sID].notifyAll();
        }

    }

    public void cEndSession(int sID) {
        sessionEnd[sID] = true;
        synchronized (waitingSessionEnd[sID]) {
            waitingSessionEnd[sID].notifyAll();
        }
    }
}
