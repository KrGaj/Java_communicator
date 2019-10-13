package communicate;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Thread.sleep;

/**
 * Klasa reprezentuje wątek klienta, który czyta oraz wysyła wiadomości.
 */

public class ClientReader implements Runnable {
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private CopyOnWriteArrayList<Message> messagesIncoming;
    private Controller ctrl;
    private TextArea conversationArea;

    private MessageHandler outgoingMessagesHandler;

    private String ID;

    private final int port=4000;


    ClientReader(Controller ctrl) throws Exception {
        this.ctrl=ctrl;
        conversationArea=this.ctrl.getConversationArea();
        socket=new Socket(InetAddress.getLocalHost(), port);
        messagesIncoming=new CopyOnWriteArrayList<>();
        outgoingMessagesHandler =ctrl.getMessageHandler();

        ID=ctrl.getID();
    }

    public void run() {
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(ID);
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        while(true) {
            try {
                //System.out.println("checkpoint");
                //ROZWIĄZANE:
                // blokuje się na czytaniu
                //sprawdzić dokładnie czytanie i pisanie, bo tutaj się blokuje
                //zdebugować messagesOutgoing czy w tej liście cos jest?
                //żeby klient rzeczywiście coś wysłał do serwera, bo teraz nic nie wysyła
                sleep(100);
                readMsgFromServer();

                printMessageToClient();
                sleep(100);
                sendMsgToServer();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metoda czyta wiadomość z serwera.
     */
    private synchronized void readMsgFromServer() {

        try {
            //if(objectInputStream.readInt()==5) {
            //tu sie zatrzymuje - ROZWIĄZANE
            int testInt;
            if(objectInputStream.available()!=0) {
                System.out.println("Czytam...");
                testInt=objectInputStream.readInt();
                Message msg = (Message) objectInputStream.readObject();
                messagesIncoming.add(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Metoda wysyła wiadomość do serwera.
     * Aby uniknąć blokowania, uprzednio wysyłany jest pojedynczy int, dzięki czemu serwer może sprawdzić, czy w danej chwili strumień faktycznie zawiera jakiś element.
     */
    private synchronized void sendMsgToServer() {
        //System.out.println("Wysylam wiadomosc do serwera...");

        if(!outgoingMessagesHandler.isEmpty()) {
            try {
                objectOutputStream.writeInt(7); //"próbny int"
                System.out.println("Wysyłam: "+ outgoingMessagesHandler.getMessage());
                objectOutputStream.writeObject(outgoingMessagesHandler.getMessage());
                System.out.println("Wysłano");
                outgoingMessagesHandler.removeFromQueue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metoda wypisuje w oknie klienta otrzymaną wiadomość.
     */
    synchronized void printMessageToClient() {
        if(!messagesIncoming.isEmpty()) {
            Platform.runLater(() -> {
                        Message msg = messagesIncoming.get(0);
                        System.out.println("Klient: "+msg.getAddressee()+" otrzymuje: "+msg.getText());
                        messagesIncoming.remove(0);
                        conversationArea.appendText(msg.getSender() + ": " + msg.getText() + "\n");
                    }
            );
        }
    }
}
