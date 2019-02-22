package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class TalkServerMain extends Application { 

	public static void main(String[] args) {
		launch(args); 
	}

	@Override
	public void start(Stage serverStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("../View/talk_server_root.fxml"));
		Scene scene = new Scene(root);
		serverStage.setScene(scene);
		serverStage.show();
		serverStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
            	serverStage.close();
                Platform.exit();
                System.exit(0);
            }
        });
	}
}
