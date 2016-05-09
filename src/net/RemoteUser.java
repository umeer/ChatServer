package net;

import chat.Chat;
import chat.ChatError;
import chat.Participant;
import chat.RegisteredUser;
import chat.RegisteredUserObserver;
import io.UsersRegistry;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manage the connection with a remote user.
 * 
 * @author Claudio Cusano
 */
class RemoteUser extends Thread implements RegisteredUserObserver {
    
    Chat chat;
    Socket socket;
    PrintWriter out;
    BufferedReader in;

    RegisteredUser user;    
    boolean stop;
    Map<String, Command> commands;
         
    /** 
    * Create a user connected with a socket.
    * 
    * @param chat the main chat object
    * @param socket the socket used to communicate
    */
    RemoteUser(Chat chat, Socket socket) throws IOException {
        this.chat = chat;
        this.socket = socket;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        user = null;
        stop = false;
        registerCommands();
    }
    
    // Log a message related to the user
    private void log(Level level, String msg) {
        String name = (user == null ? "null" : user.getNickname());
        String txt = "[" + name + "] " + msg;
        Logger.getLogger(UsersRegistry.class.getName()).log(level, txt);
    }
    
    
    // Send an error message to the remote user.
    private void error(String message) {
        log(Level.WARNING, "Sent error: " + message);
        out.println("ERR " + message);
    }
    
    // Send an ok maesage to the remote user.
    private void ok() {
        log(Level.INFO, "'OK' sent");
        out.println("OK");
    }
    
    // Create the dispatch table mapping commands to actions.
    private void registerCommands() {
        commands = new HashMap<>();
        commands.put("HI!", new Command() {
            @Override
            public void execute(String args) {
                out.println("HI!");
            }
        });
        commands.put("BYE", new Command() {
            @Override
            public void execute(String args) {
                stop = true;
            }
        });
        commands.put("LOGIN", new Command() {
            @Override
            public void execute(String args) throws ChatError {
                if (user != null) {
                    error("already logged");
                    return;
                }
                String[] tokens = args.split("\\s+", 2);
                if (tokens.length != 2) {
                    error("username and password are required");
                    return;
                }
                user = chat.login(tokens[0], tokens[1].toCharArray());            
                user.addObserver(RemoteUser.this);
                chat.welcome(user.getNickname());
            }
        });
        commands.put("REGISTER", new Command() {
            @Override
            public void execute(String args) throws ChatError {
                String[] tokens = args.split("\\s+", 3);
                if (tokens.length != 3) {
                    error("username, email and password are required");
                    return;
                }
                chat.register(tokens[0], tokens[1], tokens[2].toCharArray());
                ok();
            }
        });
        commands.put("SEND", new Command() {
            @Override
            public void execute(String args) throws ChatError {
                if (user == null) {
                    error("must be logged-in");
                    return;
                }
                String[] tokens = args.split("\\s+", 2);
                if (tokens.length < 2) {
                    error("a recipient i required");
                    return;
                }
                String msg = (tokens.length > 1 ? tokens[1] : "");
                chat.sendMessage(user, tokens[0], msg);
                ok();
            }
        });
        commands.put("BROADCAST", new Command() {
            @Override
            public void execute(String args) throws ChatError {
                if (user == null) {
                    error("must be logged-in");
                    return;
                }
                chat.broadCastMessage(user, args);
            }
        });
    }
    
    
    /**
     * Execute the given command.
     * 
     * @param command name of the command
     * @param args optional argument(s) for the command
     * @throws ChatError 
     */
    private void executeCommand(String command, String args) throws ChatError {
        Command cmd = commands.get(command);
        if (cmd == null) {
            error("Unknwown command '" + command + "'");
            return;
        }
        cmd.execute(args);
    }
    
    @Override
    public void run() {
        try {           
            stop = false;
            // Main loop of the thread.
            while (!stop) {
                // Read and process a line
                String line = in.readLine();
                log(Level.INFO, "Received: " + line);
                String[] tokens = line.trim().split("\\s+", 2);
                if (tokens.length == 0)
                    continue;  // skip empty lines
                String command = tokens[0].toUpperCase();
                String arg = (tokens.length > 1 ? tokens[1] : "");
                
                try {
                    executeCommand(command, arg);
                } catch (ChatError err) {
                    error(err.getMessage());
                }
            }            
        } catch (IOException ex) {
            Logger.getLogger(UsersRegistry.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (user != null) {
                user.removeObserver(this);
                chat.logout(user);
            }
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(UsersRegistry.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void messageTaken(Participant sender, String message) {
        out.println("MESSAGE " + sender.getNickname() + " " + message);
    }
}

/**
 * A command received from the remote user.
 * 
 * @author Claudio Cusano
 */
interface Command {
    void execute(String args) throws ChatError;
}