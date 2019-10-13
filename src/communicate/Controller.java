package communicate;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Random;

public class Controller {
    private final int port=4000;

    @FXML
    VBox pane;

    @FXML
    HBox hboxForButton;

    private String ID;

    private Label nameTextLabel;
    private TextField myName;
    //private String myNick;
    private String addresseeNick;
    private String msgText;

    private Label addresseeNameLabel;
    private TextField addressee;

    private Label conversationAreaLabel;
    private TextArea conversationArea;

    private Label yourAnswerLabel;
    private TextField yourAnswer;

    private Button sendMsgButton;

    private MessageHandler msgHdlr;


    public void initialize() throws Exception {
        System.out.println("Startuję...");
        Random generator=new Random();
        int tempID=generator.nextInt();
        ID=Integer.toString(tempID);

        msgHdlr=new MessageHandler();

        nameTextLabel=new Label("Twój nick:");
        myName=new TextField();
        myName.setText(ID);

        addresseeNameLabel=new Label("Nick Twojego rozmówcy:");
        addressee=new TextField();

        conversationAreaLabel=new Label("Rozmowa:");
        conversationArea=new TextArea();

        yourAnswerLabel=new Label("Twoja odpowiedź:");
        yourAnswer=new TextField();

        //dodaję elementy, ustawiam odległości pomiędzy elementami oraz akapity
        pane.getChildren().addAll(nameTextLabel, myName, addresseeNameLabel, addressee,
                conversationAreaLabel, conversationArea, yourAnswerLabel, yourAnswer);
        pane.setSpacing(10);  //tylko dla HBox lub VBox!
        pane.setPadding(new Insets(10));

        sendMsgButton =new Button("Wyślij");
        sendMsgButton.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(sendMsgButton, Priority.ALWAYS);
        hboxForButton.getChildren().add(sendMsgButton);
        hboxForButton.setPadding(new Insets(10));

        sendMsgButton.setOnAction(new EventHandler<ActionEvent>() {
            //tu muszę odczytać dane z pól tekstowych

            @Override
            public void handle(ActionEvent actionEvent) {
                send(actionEvent);
            }
        });
    }

    /**
     * Zamieszcza wiadomość w kolejce oczekujących na wysłanie.
     * Metoda przewidywana do wywoływania przez przycisk "Wyślij".
     * @param event
     */
    private synchronized void send(ActionEvent event) {
        //myNick=myName.getText();
        addresseeNick=addressee.getText();
        msgText=yourAnswer.getText();

        conversationArea.appendText("Ty: "+msgText+"\n");

        Message msg=new Message(ID, addresseeNick, msgText);
        System.out.println(msg.getText());
        msgHdlr.addToQueue(msg);

    }

    /**
     * Zwraca referencję do pola tekstowego, w którym wypisywane są wszystkie wiadomości
     * @return
     */
    TextArea getConversationArea() {
        return conversationArea;
    }

    MessageHandler getMessageHandler() {
        return msgHdlr;
    }

    String getID() {
        return ID;
    }

}
