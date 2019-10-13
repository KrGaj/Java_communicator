package communicate;

import java.io.Serializable;

/**
 * Klasa reprezentuje pojedynczą wiadomość.
 */

public class Message implements Serializable {

    private final String sender;
    private final String addressee;
    private final String text;

    /**
     * Konstruktor.
     * @param sender ID nadawcy
     * @param addressee ID odbiorcy
     * @param text treść wiadomości
     */
    public Message(String sender, String addressee, String text) {
        this.sender=sender;
        this.addressee=addressee;
        this.text=text;
    }

    String getSender() {
        return sender;
    }

    String getAddressee() {
        return addressee;
    }

    String getText() {
        return text;
    }
}
