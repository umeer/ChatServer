package chat;

/**
 * Interface for objects osserving a user.
 * 
 * @author Claudio Cusano
 */
public interface RegisteredUserObserver {
    
    /**
     * Notify that the user received a message.
     * 
     * @param sender participant who sent the message
     * @param message text of the message
     */
    void messageTaken(Participant sender, String message);
}
