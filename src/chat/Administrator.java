package chat;

/**
 * The administrator "pseudo user"
 * 
 * @author Claudio Cusano
 */
class Administrator implements Participant {

    @Override
    public String getNickname() {
        return "<administrator>";
    }

    @Override
    public void deliverMessage(Participant sender, String msg) {
        // this should never happen
        sender.deliverMessage(this, "Please don't talk to the administrator");
    }
}
