/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CS715_P2_Client;

import java.io.*;
import java.net.*;

class scVisitor implements Runnable {

    private String hostName = "localhost";
    private int portNumber = 707;
    private String name = "";
    public static long time = System.currentTimeMillis();

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - time) + "]" + getName() + ": " + m);
    }

    protected final String getName() {
        return name;
    }

    public scVisitor(String name, int id) {
        this.name = name + id;
        new Thread(this).start();
    }

    @Override
    public void run() {
        int sID = 0;
        while (sID < 3) {
            try (
                    Socket kkSocket = new Socket(hostName, portNumber);
                    PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(kkSocket.getInputStream()));) {

                String fromServer;

                boolean full = false;

                msg(" waits for Session" + sID);
                out.println("v#" + name + "#" + sID);

                while ((fromServer = in.readLine()) != null) {

                    System.out.println("Server ==> " + name + ": " + fromServer);
                    if (fromServer.equals("Session" + sID + " Full")) {
                        full = true;
                    }
                }
                if (full) {
                    sID++;
                    full = false;
                    continue;
                }
                break;

            } catch (UnknownHostException e) {
                System.err.println("Don't know about host " + hostName);
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to "
                        + hostName);
                System.exit(1);
            }
        }
    }
}

class scSpeaker implements Runnable {

    private String hostName = "localhost";
    private int portNumber = 707;
    private String name = "";
    public static long time = System.currentTimeMillis();

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - time) + "]" + getName() + ": " + m);
    }

    protected final String getName() {
        return name;
    }

    public scSpeaker(String name) {
        this.name = name;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            int sID;
            for (sID = 0; sID < 3; sID++) {
                Socket kkSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(kkSocket.getInputStream()));
                {

                    String fromServer;

                    msg(" waits for presentation of Session" + sID);
                    out.println("s#" + name + "#" + sID);

                    while ((fromServer = in.readLine()) != null) {
                        System.out.println("Server ==> " + name + ": " + fromServer);
                        if (fromServer.equals(" finishes Session" + sID)) {
                            break;
                        }
                    }
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to "
                    + hostName);
            System.exit(1);
        }

    }
}

public class Client {

    public static void main(String[] args) throws IOException {
        new scSpeaker("Speaker");//1 Speaker
        for (int i = 0; i < 20; i++) {//20 Vistors
            new scVisitor("Vistor", i);
        }
    }
}
