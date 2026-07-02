package igralica.application;

import javafx.application.Application;
import javafx.stage.Stage;

import igralica.utility.FxmlLoader;

public class PokretanjeAplikacije extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		FxmlLoader.load(getClass(), "/igralica/view/PocetnaStrana.fxml", "Početna strana");
	}

	public static void main(String[] args) {
		launch(args);
	}

}
