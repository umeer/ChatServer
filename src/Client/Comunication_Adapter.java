package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Comunication_Adapter {

    private String address;
    private int port;

    PrintWriter out;
    BufferedReader in;

    public Comunication_Adapter(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public boolean start_connection() {
        try {
            Socket s = new Socket(address, port);
            out = new PrintWriter(s.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public void close_connection() {
        out.println("BYE");
    }

    public boolean sign_up(String user, String pass, String email) {
        out.println("REGISTER " + user + " " + email + " " + pass);
        boolean wait = true;
        try {
            while (wait) {
                if (in.readLine().compareTo("OK") == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (IOException ex) {
            return false;
        }
        return false;
    }

    public boolean login(String user, String pass) {
        out.println("LOGIN " + user + " " + pass);
        boolean wait = true;
        try {
            while (wait) {
                if (in.readLine().compareTo("OK") == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (IOException ex) {
            return false;
        }
        return false;
    }

    public boolean send(String message) {
        out.println("BROADCAST " + message);
        return true;
    }

    public String read() throws IOException {
        String newText = in.readLine();
        if (newText.compareTo("OK") != 0) {
            newText = newText.substring(7);
            return "\n" + newText;
        } else {
            return "";
        }
    }

}
