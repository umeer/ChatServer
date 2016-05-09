package chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A users registered in the system.
 * 
 * @author Claudio Cusano
 */
public class RegisteredUser implements Participant {
    String nickname;
    String email;
    char[] password;
    List<RegisteredUserObserver> observers;
    
    /**
     * Create the user.
     * 
     * @param nickname the nickname
     * @param email the email 
     * @param password  the password
     */
    public RegisteredUser(String nickname, String email, char[] password) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        observers = new ArrayList<>();
    }
    
    @Override
    public String getNickname() {
        return nickname;
    }

    /**
     * Email of the user.
     * 
     * @return 
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Password of the user.
     * 
     * @return 
     */
    public char[] getPassword() {
        return password;
    }
    
    @Override
    public void deliverMessage(Participant sender, String msg) {
        for (RegisteredUserObserver obs : observers)
            obs.messageTaken(sender, msg);
    }
    
    /**
     * Check if the given password matches that of the user.
     * 
     * @param password the password to be checked
     * @return whether the password match or not
     */
    boolean checkPassword(char[] password) {        
        return Arrays.equals(password, this.password);
    }

    /**
     * Register a new observer for the user.
     * 
     * @param observer the observer to be registered
     */
    public void addObserver(RegisteredUserObserver observer) {
        observers.add(observer);
    }
    
    /**
     * Detach the observer from the user.
     * 
     * @param observer the observer to be detached
     */
    public void removeObserver(RegisteredUserObserver observer) {
        observers.remove(observer);
    }
}
