package chat;

/**
 * Errors related to the communication protocol.
 * 
 * @author Claudio Cusano
 */
public class ChatError extends Exception {

    /**
     * Creates a new instance of <code>ChatError</code> without detail message.
     */
    public ChatError() {
    }

    /**
     * Constructs an instance of <code>ChatError</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public ChatError(String msg) {
        super(msg);
    }
}
