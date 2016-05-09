package net;

import chat.Chat;
import chat.Participant;
import io.UsersRegistry;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Claudio Cusano
 */
public class ChatServer {
    
    Chat chat;
    int port;
    ServerSocket serverSocket = null;
    
    /**
     * Server that accept connections from chat users.
     * 
     * @param chat the chat served by the server
     * @param port TCP port
     */
    public ChatServer(Chat chat, int port) {
        this.chat = chat;
        this.port = port;
    }
    
    public void startServer() throws IOException {
        serverSocket = new ServerSocket(port);
        
        while (true) {
            Socket socket = serverSocket.accept();
            RemoteUser u = new RemoteUser(chat, socket);
            Logger.getLogger(UsersRegistry.class.getName()).log(Level.INFO,
                        "Accepting a new user");
            u.start();
        }
    }
    
    public static void main(String[] args) {
        final int PORT = 8888;
        final String USERS_FILE = "users.txt";
        
        final Chat chat = new Chat(USERS_FILE);
        ChatServer server = new ChatServer(chat, PORT);
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Logger.getLogger(UsersRegistry.class.getName()).log(Level.INFO,
                        "Shutting down the server...");
                chat.shutdown(3);
            }
        });
        
        try {
            server.startServer();
        } catch (IOException ex) {
            Logger.getLogger(UsersRegistry.class.getName()).log(Level.INFO, null, ex);
        }
    }
}
