package igralica.dialogs;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ObavjestenjaDijalog {

	private static void showDialog(AlertType tip, String naslov, String zaglavlje, String poruka) {
		Alert alert = new Alert(tip);
		alert.setTitle(naslov);
		alert.setHeaderText(zaglavlje);
		alert.setContentText(poruka);
		alert.showAndWait();
	}

	public static void showErrorDialog(String naslov, String zaglavlje, String poruka) {
		showDialog(AlertType.ERROR, naslov, zaglavlje, poruka);
		Platform.exit();
	}

	public static void showInfoDialog(String naslov, String zaglavlje, String poruka) {
		showDialog(AlertType.INFORMATION, naslov, zaglavlje, poruka);
	}

	public static void showWarningDialog(String naslov, String zaglavlje, String poruka) {
		showDialog(AlertType.WARNING, naslov, zaglavlje, poruka);
	}

}
