package igralica.controller;

import static igralica.controller.GlavnaStranaKontroler.tblRangLista;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import igralica.model.PitanjaZaKviz;
import igralica.utility.Putanje;

public class KvizKontroler implements Putanje {

	@FXML
	private Label lblPitanje;

	@FXML
	private Button dugme1;

	@FXML
	private Button dugme2;

	@FXML
	private Button dugme3;

	@FXML
	private Label lblRezultat;

	@FXML
	private Label lblBrojPitanja;

	private static ArrayList<PitanjaZaKviz> pitanja;
	private static ArrayList<PitanjaZaKviz> slucajnoOdabranaPitanja;

	private static int duzinaPauze = 2000;
	private static boolean kraj;

	@FXML
	void initialize() {

		pitanja = PitanjaZaKviz.ucitajPitanja(PUTANJA_DO_LISTE_PITANJA);

		Collections.shuffle(pitanja);
		slucajnoOdabranaPitanja = new ArrayList<PitanjaZaKviz>(pitanja.subList(0, 5));

		PitanjaZaKviz.postaviDugmad(dugme1, dugme2, dugme3);

		prikaziPrvoPitanje();
	}

	private void prikaziPrvoPitanje() {
		slucajnoOdabranaPitanja.get(PitanjaZaKviz.getIndeksPitanja()).prikaziPitanje(lblPitanje, lblBrojPitanja);

		dugme1.setOnAction(this::klikniNaDugme);
		dugme2.setOnAction(this::klikniNaDugme);
		dugme3.setOnAction(this::klikniNaDugme);
	}

	private void klikniNaDugme(ActionEvent event) {

		dugme1.setDisable(true);
		dugme2.setDisable(true);
		dugme3.setDisable(true);

		slucajnoOdabranaPitanja.get(PitanjaZaKviz.getIndeksPitanja()).provjeriOdgovor((Button) event.getTarget(),
				slucajnoOdabranaPitanja, lblRezultat);

		// Pauza izmedju pitanja bez zaustavljanja UI niti
		Timer vrijeme = new Timer();
		vrijeme.schedule(new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					// Prikazuj sljedeca pitanja
					slucajnoOdabranaPitanja.get(PitanjaZaKviz.getIndeksPitanja()).prikaziPitanje(lblPitanje,
							lblBrojPitanja);
					dugme1.setDisable(false);
					dugme2.setDisable(false);
					dugme3.setDisable(false);
				});
			}
		}, duzinaPauze);

		if (kraj) {
			kraj = false;
			tblRangLista.refresh();
			GlavnaStranaKontroler.osvjezi();
			final Node source = (Node) event.getSource();
			final Stage stage = (Stage) source.getScene().getWindow();
			stage.close();
		}
	}

	public static boolean isKraj() {
		return kraj;
	}

	public static void setKraj(boolean kraj) {
		KvizKontroler.kraj = kraj;
	}

}
