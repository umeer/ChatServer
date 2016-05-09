package chat;

/**
 * A participant to the chat.
 * 
 * @author Claudio Cusano
 */
public interface Participant {
    /**
     * Nickname of the participant.
     * 
     * @return the nickname
     */
    String getNickname();
    
    /**
     * Deliver a message to the participant.
     * 
     * @param sender the participant who sent the message
     * @param msg  the text of the message
     */
    void deliverMessage(Participant sender, String msg);
}
