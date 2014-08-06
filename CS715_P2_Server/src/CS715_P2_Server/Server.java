/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CS715_P2_Server;

import java.net.*;
import java.io.*;

public class Server {

    public static void main(String[] args) throws IOException, InterruptedException {

        int portNumber = 707;
        boolean listening = true;
        Database db = new Database();
        Clock clock = new Clock(db);
        
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                new Communication(serverSocket.accept(), clock, db).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}
