package application;

import Controller.PhoneRootController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SmatPhoneMain extends Application{

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Stage phoneStage = new Stage(StageStyle.UTILITY);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/phone_exterior.fxml"));
		Parent rootPhoneExterior = loader.load();
		PhoneRootController phoneRootController=loader.getController();
		phoneRootController.phoneStage=phoneStage;
		Scene scenePhoneExterior = new Scene(rootPhoneExterior);
		scenePhoneExterior.getStylesheets().add(getClass().getResource("../css/phone_exterior.css").toString());
		phoneStage.setScene(scenePhoneExterior);
		phoneStage.setResizable(false);
		phoneStage.setTitle("Smart Phone");
		phoneStage.show();		
	}

}

