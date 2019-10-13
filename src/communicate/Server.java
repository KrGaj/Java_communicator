package communicate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

/**
 * Klasa serwera. Do każdego nowego gniazda (klienta) zostaje przypisany nowy wątek.
 * Metoda main() pośredniczy (przechowywanie oraz wymiana danych) pomiędzy wątkami serwerowymi.
 */

public class Server implements Runnable {
    private Socket socket;
    private String ID;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private CopyOnWriteArrayList<Message> messages;


    /**
     * Konstruktor
     * @param msg lista wiadomości przechowywanych przez serwer
     * @param sock gniazdo łączące klienta z serwerem
     * @throws Exception błąd wejścia/wyjścia
     */

    Server(CopyOnWriteArrayList<Message> msg, Socket sock) throws Exception {
        messages=msg;
        socket=sock;
        objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
        objectInputStream=new ObjectInputStream(socket.getInputStream());
        ID=(String) objectInputStream.readObject();
        System.out.println(ID);
    }

    @Override
    public void run() {
        System.out.println("Startuje watek...");
        test: while(true) { //dla pewności dodałem label do pętli
            try {
                sleep(100);
            //System.out.println("Odbieram...");
                receiveMsgFromClient();
            //System.out.println("Wysylam...");
                sleep(100);
                sendMsgToClient();
            }
            catch(IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
                break test; //System.exit() wyrzuca cały serwer
            }
        }
        try {
            System.out.println(ID+": Kończę działanie. Do widzenia.");
            objectOutputStream.close();
            objectInputStream.close();
            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda służy do wysłania wiadomości klientowi. Z puli wiadomości wybierana jest ta adresowana ID zgodnym z ID wątku wywołującego.
     * Aby uniknąć blokującego oczekiwania na wiadomość, przed wysłaniem wiadomości wysyłany jest pojedynczy int.
     * Dzięki temu metoda ObjectInputStream.available() może oszacować liczbę bajtów w strumieniu.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    synchronized void sendMsgToClient() throws IOException, ClassNotFoundException {   //ten sychronized do przemyślenia!
        if(!messages.isEmpty()) {
            for (Message x : messages) {
                if (x.getAddressee().equals(ID)) {
                    //wyślij
                    //try {
                    objectOutputStream.writeInt(5);
                        System.out.println("Wysyłam: "+x.getText()+" do: "+x.getAddressee());
                        objectOutputStream.writeObject(x);
                        System.out.println("Wysłano");
                        //objectOutputStream.flush();
                        messages.remove(x);
                    break;
                }
            }
        }
    }

    /**
     * Metoda odbiera wiadomości od klienta. Przed odczytem sprawdzane jest, czy strumień zawiera bajty, które można odczytać.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    synchronized void receiveMsgFromClient() throws IOException, ClassNotFoundException {
        //System.out.println(     "Czekam na wiadomość....");
        //try {
        int testInt;    //"próbny int
        if(objectInputStream.available()!=0) {
        //if(objectInputStream.readInt()==5) {
            testInt=objectInputStream.readInt();    //czytam "próbnego inta"
            Message msg = (Message) objectInputStream.readObject();
            System.out.println("Otrzymałem wiadomość: " + msg.getText());
            messages.add(msg);
        }
        //}
        /*catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();

        }*/
    }

    /**
     * Główna metoda oraz główny wątek serwera. Zarządza wątkami przypisanymi do poszczególnych gniazd.
     * @param args
     */
    public static void main(String[] args) {
        final int port=4000;
        final int maxUsers=50;

        ExecutorService executorService= Executors.newFixedThreadPool(maxUsers);

        CopyOnWriteArrayList<Message> messages = new CopyOnWriteArrayList<>();
        ServerSocket servSocket;

        System.out.println("Serwer uruchomiony");

        try {
             servSocket = new ServerSocket(port, maxUsers);

            while(true) {

                executorService.execute(new Server(messages, servSocket.accept()));

                //fajnie byłoby wysłać wszystkim nową listę użytkowników, ale to potem - niech się domyślają xD
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Nie udało się utworzyć/przypisać gniazda użytkownikowi");
            System.exit(1);
        }

    }

}
