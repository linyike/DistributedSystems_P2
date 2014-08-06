/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CS715_P2_Server;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import static CS715_P2_Server.Clock.time;

public class Communication extends Thread {

    private Socket socket = null;
    private int commuPortNum = 717;
    private Clock clock;
    private Database db;

    public Communication(Socket socket, Clock clock, Database db) {
        super("KKMultiServerThread");
        this.socket = socket;
        this.clock = clock;
        this.db = db;
    }

    public void msg(String name, String m) {
        System.out.println("[" + (System.currentTimeMillis() - time) + "] " + name + ": " + m);
    }

    public void run() {
        int type, numOfSeat, sID;
        String name;
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));) {
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                String tem[] = inputLine.split("#");
                if (tem[0].equals("v")) {
                    type = 0;
                } else {
                    type = 1;
                }
                name = tem[1];
                sID = Integer.parseInt(tem[2]);

                if (type == 0) {
                    db.waitSession(sID);
                    msg(name, " waits for Session" + sID);
                    out.println(" waits for Session" + sID);
                    db.waitSession(sID);
                    numOfSeat = db.getSeat(sID);
                    if (numOfSeat < 0) { //not get the seats, wait for next session
                        out.println("Session" + sID + " Full");
                        break;
                    } else { //get seat, enter session
                        msg(name, " enters Session" + sID + ", takes seat:" + numOfSeat);
                        out.println(" enters Session" + sID + ", takes seat:" + numOfSeat);
                    }
                    db.in1Session(sID); // presentation and movie session
                    msg(name, " thinks question in Session" + sID);
                    out.println(" thinks question in Session" + sID);
                    db.in2Session(name, sID); // question-answer session
                    msg(name, " finishes Session" + sID);
                    out.println(" finishes Session" + sID);
                    break;
                } else {
                    try {
                        msg(name, " waits for presentation of Session" + sID);
                        out.println(" waits for presentation of Session" + sID);
                        db.sWaitPresentation(sID);
                        msg(name, " gives presentation for Session" + sID);
                        out.println(" gives presentation for Session" + sID);
                        db.sInPresentation(sID);
                        msg(name, " ends presentation");
                        out.println(" ends presentation");
                        msg(name, " starts movie and waits for QnA");
                        out.println(" starts movie and waits for QnA");
                        db.sWaitQnA(sID);
                        msg(name, " starts answer questions for Session" + sID);
                        out.println(" starts answer questions for Session" + sID);
                        db.sAnswerQ(sID, 15);
                        db.waitSessionEnd(sID);
                        msg(name, " finishes Session" + sID);
                        out.println(" finishes Session" + sID);
                        break;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
