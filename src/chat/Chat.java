package chat;

import io.UsersRegistry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Object that manage the chat.
 * 
 * @author Claudio Cusano
 */
public class Chat {
    
    Map<String, RegisteredUser> registeredUsers;
    Map<String, Participant> loggedUsers;
    UsersRegistry registry;
    Administrator administrator;
    
    /**
     * Create the chat object.
     * 
     * @param filename path to the file with the data about the users
     */
    public Chat(String filename) {
        loggedUsers = new ConcurrentHashMap<>();
        registry = new UsersRegistry(filename);
        registeredUsers = registry.loadUSers();        
        administrator = new Administrator();
        
        String msg = "" + registeredUsers.size() + " registered users:";
        for (String s : registeredUsers.keySet())
            msg += " " + s;
        Logger.getLogger(Chat.class.getName()).log(Level.INFO, msg);
    }
    
    /**
     * Login an already registered user.
     * 
     * @param nickname the nickname
     * @param password email of the user
     * @param password the password
     * @return the user object corresponding to the given credentials
     * @throws ChatError 
     */
    public RegisteredUser login(String nickname, char[] password) throws ChatError {
        RegisteredUser u = registeredUsers.get(nickname);
        if (u == null || !u.checkPassword(password))
            throw new ChatError("Invalid username or password");
        loggedUsers.put(u.getNickname(), u);
        return u;
    }
    
    /**
     * Logout the user.
     * 
     * @param user the user to logout
     */
    public void logout(RegisteredUser user) {
        loggedUsers.remove(user.getNickname());
    }
    
    /**
     * Request the registration of a new user
     * 
     * @param nickname the nickname
     * @param password email of the user
     * @param password the password
     * @throws ChatError 
     */
    public synchronized void register(String nickname, String email, char[] password) throws ChatError {
        // Check if there are registeredUsers with the same email or nickname.
        if (registeredUsers.get(nickname) != null)
            throw new ChatError("Nickname already taken");                  
        registeredUsers.put(nickname, new RegisteredUser(nickname, email, password));
        registry.saveUsers(registeredUsers);
    }

    /**
     * Send a message to a given user, given his nickname.
     * 
     * @param sender user who send the massage
     * @param recipient nickname of the recipient
     * @param message text message to be sent
     * @throws ChatError 
     */    
    public void sendMessage(Participant sender, String recipient, String message) throws ChatError {
        Participant p = loggedUsers.get(recipient);
        if (p == null)
            throw new ChatError("User '" + recipient + "' is unknown or offline");
        p.deliverMessage(sender, message);
    }
    
    /**
     * Send a message to all the logged users.
     * 
     * @param sender user who send the massage
     * @param message text message to be sent
     * @throws ChatError 
     */
    public void broadCastMessage(Participant sender, String message) throws ChatError {
        for (Participant p : loggedUsers.values())
            p.deliverMessage(sender, message);
    }
    
    /**
     * Send a welcome message to a user.
     * 
     * @param recipient nickname of the recipient
     * @throws ChatError 
     */
    public void welcome(String recipient) throws ChatError {
        String msg = "Hello " + recipient + ", welcome to the chat!";
        sendMessage(administrator, recipient, msg);
        msg = "" + loggedUsers.size() + " logged users:";
        for (String s : loggedUsers.keySet())
            msg += " " + s;
        sendMessage(administrator, recipient, msg);
    }

    /**
     * Close the chat service.
     * 
     * @param delaySecs wait a few seconds before the shutdown
     */
    public void shutdown(int delaySecs) {
        for (Participant p : loggedUsers.values())
            p.deliverMessage(administrator, "The chat is closing in " + delaySecs + " seconds...");
        try {
            Thread.sleep(delaySecs * 1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
        }
        loggedUsers.clear();
    }
}
