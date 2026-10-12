package utb.fai;

import java.io.*;
import java.net.*;

public class TelnetClient {

    private String serverIp;
    private int port;
    private Socket socket;

    public TelnetClient(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
    }

    public void run() {
        try {
            socket = new Socket(serverIp, port);
            System.out.println("Klient se uspesne pripojil k serveru");
            Thread readerThread = new Thread(() -> readFromServer());
            Thread writerThread = new Thread(() -> writeToServer());
            readerThread.start();
            writerThread.start();
            try {
                writerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Klient byl ukoncen!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFromServer()  {
        try {
            BufferedReader in = new  BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToServer() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            String line;
            while ((line = in.readLine())  != null) {
                out.println(line);
                if (line.equals("/QUIT")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Klient (" + socket.getInetAddress() + ") byl odpojen!");
        }
    }
}
