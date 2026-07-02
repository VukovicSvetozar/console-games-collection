package igralica.controller;

import static igralica.controller.PocetnaStranaKontroler.korisnik;
import static igralica.controller.GlavnaStranaKontroler.mapaKljuceva;

import igralica.model.Kljuc;
import igralica.model.Kljuc.StatusKljuca;

import java.io.File;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class InformacijeOIgriKontroler {

	@FXML
	private ImageView ivSlikaIgre;

	@FXML
	private Label lblInformacija;

	@FXML
	private TextArea taOpisIgre;

	@FXML
	private Button btnOtkaziIgru;

	@FXML
	private Button btnIzadji;

	private static Entry<String, Kljuc> kljucKorisnika;
	private static String tipIgreInfo;

	@FXML
	void initialize() throws MalformedURLException {
		ubaciSliku(tipIgreInfo);
		ubaciOpisIgre(tipIgreInfo);
		kljucKorisnika = provjeraStatusaIgre(tipIgreInfo);
		if (kljucKorisnika != null) {
			String poruka = "Kljuc je aktiviran:  " + kljucKorisnika.getValue().getVrijemeAktiviranjaKljuca()
					+ "\n" + "Kljuc je validan do: " + kljucKorisnika.getValue().getVrijemeDeaktiviranjaKljuca();
			lblInformacija.setText(poruka);
			btnOtkaziIgru.setDisable(false);

		} else {
			lblInformacija.setText("Igra nije aktivna.");
			btnOtkaziIgru.setDisable(true);
		}
	}

	@FXML
	void otkaziIgru(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Dijalog potvrde");
		alert.setHeaderText("Zelite da odjavite igru sa profila!");
		alert.setContentText("Da li ste sigurni?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			kljucKorisnika.getValue().setStatusKLjuca(StatusKljuca.ISKORISCEN);
			izadji(event);
		}
	}

	@FXML
	void izadji(ActionEvent event) {
		final Node source = (Node) event.getSource();
		final Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}

	private Entry<String, Kljuc> provjeraStatusaIgre(String tipIgre) {
		for (Map.Entry<String, Kljuc> ulaz : mapaKljuceva.entrySet()) {
			if (ulaz.getValue().getImeVlasnikaKljuca().equals(korisnik.getKorisnickoIme())
					&& ulaz.getValue().getTipIgre().equals(tipIgre)
					&& nijeIstekaoKljuc(ulaz.getValue().getVrijemeDeaktiviranjaKljuca())
					&& ulaz.getValue().getStatusKLjuca().equals(StatusKljuca.AKTIVAN))
				return ulaz;
		}
		return null;
	}

	private boolean nijeIstekaoKljuc(LocalDateTime vrijemeDeaktiviranjaKljuca) {
		LocalDateTime trenutnoVrijeme = LocalDateTime.now();
		return vrijemeDeaktiviranjaKljuca.isAfter(trenutnoVrijeme);
	}

	private void ubaciSliku(String tipIgre) throws MalformedURLException {
		String prilagodjeniTip = tipIgre.equals("Pogodi broj") ? "PogodiBroj" : tipIgre;
		File fajl = new File("/igralica/style" + File.separator + prilagodjeniTip + "Igra.jpg");
		String putanjaSlike = fajl.toString();
		Image slika = new Image(putanjaSlike, true);
		ivSlikaIgre.setImage(slika);
	}

	private void ubaciOpisIgre(String tipIgre) {
		String opisIgre = "";

		switch (tipIgre) {
		case "Pogodi broj":
			opisIgre = "\n" + "Ovo je igra pogadjanja zamisljenog broja u intervalu od 0 do 100." + "\n\n"
					+ "Aplikacija na slucajan nacin bira odredjeni broj, " + "a zatim korisnik pokusava da ga pogodi."
					+ "\n\n" + "Dozvoljeno je pet pokusaja." + "\n\n"
					+ "Poslije svakog pokusaja aplikacija korisnika da li je uneseni"
					+ " broj veci ili manji od trazenog." + "\n\n"
					+ "Aplikacija nema negativne bodove, a ukoliko korisnik pogodi "
					+ "trazeni broj dobija 100/broj_pokusaja bodova za svoj profil.";
			break;
		case "Kviz":
			opisIgre = "\n" + "Korisniku se prikazuje 5 slucajno odabranih pitanja." + "\n\n"
					+ "Uz svako pitanje korisniku se prikazuju 3 moguca odgovora, "
					+ "nakon cega korisnik bira odgovor za koji smatra da je tacan." + "\n\n"
					+ "Za svaki tacan odgovor korisnik dobija 20 bodova. "
					+ "Ako korisnik odgovori tacno na sva pitanja dobija dodatno jos 50 bodova bonusa. "
					+ "Svaki pogresan odgovor korisniku oduzima 30 bodova";
			break;
		case "Loto":
			opisIgre = "\n" + "Za pokretanje ove igre korisnik ulaze 100 bodova." + "\n\n"
					+ "Korisnik unosi 7 razlicitih brojeva u opsegu od 1 do 45." + "\n\n"
					+ "Racunar na slucajan nacin bira 20 brojeva." + "\n\n"
					+ "Za svaki pogodjeni broj korisnik dobija (redni_broj_pogotka)*10 bodova na svom korisnickom profilu.";
			break;
		default:
			opisIgre = "Nepoznata igra!";
		}
		taOpisIgre.setText(opisIgre);
	}

	public static String getTipIgreInfo() {
		return tipIgreInfo;
	}

	public static void setTipIgreInfo(String tipIgreInfo) {
		InformacijeOIgriKontroler.tipIgreInfo = tipIgreInfo;
	}

}
