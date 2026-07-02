package igralica.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import igralica.dialogs.ObavjestenjaDijalog;

import static igralica.controller.PocetnaStranaKontroler.korisnik;
import static igralica.controller.GlavnaStranaKontroler.lblBrojBodovaNaProfilu;
import static igralica.controller.GlavnaStranaKontroler.tblRangLista;

public class LotoKontroler {

	@FXML
	private TextField tf1;

	@FXML
	private TextField tf2;

	@FXML
	private TextField tf3;

	@FXML
	private TextField tf4;

	@FXML
	private TextField tf5;

	@FXML
	private TextField tf6;

	@FXML
	private TextField tf7;

	@FXML
	private TextArea taIzvuceniBrojevi;

	@FXML
	private Button btnProvjeriUnos;

	@FXML
	private Button btnPokreniSimulaciju;

	@FXML
	private Button btnIzadji;

	@FXML
	private Label lblBrojPogodaka;

	@FXML
	private Label lblBrojPoena;

	private List<Integer> uneseniBrojevi = new ArrayList<>();
	private List<Integer> izvuceniBrojevi = new ArrayList<>();
	private int brojPogodaka;
	private int brojOsvojenihPoena;

	@FXML
	void initialize() {
		btnPokreniSimulaciju.setDisable(true);
		btnIzadji.setDisable(true);
	}

	@FXML
	void provjeriUnos(ActionEvent event) {
		try {
			konvertujBrojeve();
			if (daLiJeIspravanUnos()) {
				btnProvjeriUnos.setDisable(true);
				btnPokreniSimulaciju.setDisable(false);
			}
		} catch (NumberFormatException ex) {
			uneseniBrojevi.clear();
			String upozorenje = "Uneseni su alfabetski karakteri!";
			ObavjestenjaDijalog.showWarningDialog("Upozorenje", "Niste pravilno unijeli brojeve!", upozorenje);

		}
	}

	@FXML
	void pokreniSimulaciju(ActionEvent event) {
		btnPokreniSimulaciju.setDisable(true);
		btnIzadji.setDisable(false);

		izvlacenjeBrojeva();
		pokreniVaranjeKorisnika();
		ispisBrojeva();
		ispisRezultata();
	}

	@FXML
	void izadji(ActionEvent event) {
		izracunajBodoveNaProfilu();
		tblRangLista.refresh();
		GlavnaStranaKontroler.osvjezi();
		final Node source = (Node) event.getSource();
		final Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}

	private void konvertujBrojeve() throws NumberFormatException {
		uneseniBrojevi.add(Integer.parseInt(tf1.getText()));
		uneseniBrojevi.add(Integer.parseInt(tf2.getText()));
		uneseniBrojevi.add(Integer.parseInt(tf3.getText()));
		uneseniBrojevi.add(Integer.parseInt(tf4.getText()));
		uneseniBrojevi.add(Integer.parseInt(tf5.getText()));
		uneseniBrojevi.add(Integer.parseInt(tf6.getText()));
		uneseniBrojevi.add(Integer.parseInt(tf7.getText()));
	}

	private boolean daLiJeIspravanUnos() {
		boolean ispravno = false;

		if (daLiImaPraznaPolja()) {
			uneseniBrojevi.clear();
			String upozorenje = "Prazna polja!";
			ObavjestenjaDijalog.showWarningDialog("Upozorenje", "Niste pravilno unijeli brojeve!", upozorenje);
			return ispravno;
		}
		if (daLiImaDuplikate()) {
			uneseniBrojevi.clear();
			String upozorenje = "Duplikati!";
			ObavjestenjaDijalog.showWarningDialog("Upozorenje", "Niste pravilno unijeli brojeve!", upozorenje);
			return ispravno;
		}
		if (daLiSuBrojeviVanOpsega()) {
			uneseniBrojevi.clear();
			String upozorenje = "Van opsega!";
			ObavjestenjaDijalog.showWarningDialog("Upozorenje", "Niste pravilno unijeli brojeve!!", upozorenje);
			return ispravno;
		}
		ispravno = true;
		return ispravno;
	}

	private boolean daLiImaPraznaPolja() {
		if (uneseniBrojevi.size() != 7)
			return true;
		return false;
	}

	private boolean daLiImaDuplikate() {
		Set<Integer> set = new HashSet<Integer>(uneseniBrojevi);
		if (set.size() < uneseniBrojevi.size())
			return true;
		return false;
	}

	private boolean daLiSuBrojeviVanOpsega() {
		Iterator<Integer> iterator = uneseniBrojevi.iterator();
		while (iterator.hasNext()) {
			Integer broj = iterator.next();
			if (broj < 1 || broj > 70)
				return true;
		}
		return false;
	}

	private void izvlacenjeBrojeva() {
		Random rand = new Random();
		int i = 0;
		while (i < 20) {
			Integer broj = rand.nextInt(70) + 1;
			boolean duplikat = false;
			for (Integer n : izvuceniBrojevi) {
				if (broj == n) {
					duplikat = true;
				}
			}
			if (!duplikat) {
				izvuceniBrojevi.add(broj);
				i++;
			}
		}
	}

	/*
	 * Prosjecni broj osvojenih poena u igri Loto iznosi 80. 
	 * Zelimo da on iznosi 60 poena i da na taj nacin broj izgubljenih poena bude za 40% veci od
	 * broja osvojenih (ulaze se 100 poena). 
	 * Prije varanja: (10+20+30+40+50+60+70)*(20/70) = 80 poena. 
	 * Da bi se postigao prosjek od 60 poena zbir koeficijenata treba smanjiti sa postojecih 280 na 210.
	 * Brojevima u poljima tf3, tf5 i tf6 daje se 50% sanse da ukoliko su u pocetnom izvlacenju pogodjeni od strane korisnika, 
	 * zaista i budu izvuceni.
	 * Naime, brojevi sa kojima se vara vrijede redom 30, 50 i 60 poena, tj. sa 50% sanse da budu izvuceni umanjuju zbir koefiacijenata za 70. 
	 * Dodatni poeni za svih sedam pogodjenih brojeva nisu razmatrani, zbog male vjerovatnoce da ta kombiancija bude izvucena.
	 */
	private void pokreniVaranjeKorisnika() {
		if (daLiJeBrojPogodjen(Integer.parseInt(tf3.getText())) && daLiDaVaram())
			varaj(Integer.parseInt(tf3.getText()));
		if (daLiJeBrojPogodjen(Integer.parseInt(tf5.getText())) && daLiDaVaram())
			varaj(Integer.parseInt(tf5.getText()));
		if (daLiJeBrojPogodjen(Integer.parseInt(tf6.getText())) && daLiDaVaram())
			varaj(Integer.parseInt(tf6.getText()));
	}

	private boolean daLiJeBrojPogodjen(Integer broj) {
		return izvuceniBrojevi.contains(broj);
	}

	/*
	 * Metoda daje 50% sansi korisniku da sacuva pogodjeni broj
	 */
	private boolean daLiDaVaram() {
		Random rand = new Random();
		int slucajanBroj = rand.nextInt(2);
		if (slucajanBroj == 0)
			return true;
		return false;
	}

	private void varaj(Integer pogodjeniBroj) {
		Random rand = new Random();
		boolean izvucen = true;
		int noviBroj = 0;
		while (izvucen) {
			noviBroj = rand.nextInt(70) + 1;
			if (!uneseniBrojevi.contains(noviBroj))
				izvucen = false;
		}
		// Izbaci i dodaj broj na kraju kako se ne bi slucajno vratio isti broj.
		izvuceniBrojevi.remove(pogodjeniBroj);
		izvuceniBrojevi.add(noviBroj);
	}

	private void ispisBrojeva() {
		LocalDateTime vrijemeSimulacije = LocalDateTime.now();
		DateTimeFormatter formatVremena = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
		taIzvuceniBrojevi.appendText("Loto izvlacenje " + formatVremena.format(vrijemeSimulacije) + "\n\n");

		int i = 0;
		Iterator<Integer> iterator = izvuceniBrojevi.iterator();
		while (iterator.hasNext()) {
			taIzvuceniBrojevi.appendText(String.format("%8d", iterator.next()));
			if ((i + 1) % 5 == 0)
				taIzvuceniBrojevi.appendText("\n");
			i++;
		}
	}

	private void ispisRezultata() {
		int i = 1;
		Iterator<Integer> iterator = uneseniBrojevi.iterator();
		while (iterator.hasNext()) {
			if (izvuceniBrojevi.contains(iterator.next())) {
				brojPogodaka++;
				brojOsvojenihPoena += i * 10;
			}
			i++;
		}
		if (brojPogodaka == 7)
			brojOsvojenihPoena += 100;
		lblBrojPogodaka.setText(Integer.toString(brojPogodaka));
		lblBrojPoena.setText(Integer.toString(brojOsvojenihPoena));
	}

	private void izracunajBodoveNaProfilu() {
		korisnik.getTrenutnaIgra().setBrojOsvojenihPoena(brojOsvojenihPoena);
		korisnik.setBrojPoenaNaProfilu(korisnik.getBrojPoenaNaProfilu() + brojOsvojenihPoena - 100);
		lblBrojBodovaNaProfilu.setText(Integer.toString(korisnik.getBrojPoenaNaProfilu()));
	}
}
