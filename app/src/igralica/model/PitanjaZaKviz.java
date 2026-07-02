package igralica.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

import igralica.controller.KvizKontroler;
import igralica.dialogs.ObavjestenjaDijalog;
import igralica.utility.FileLogger;

import static igralica.controller.PocetnaStranaKontroler.korisnik;
import static igralica.controller.GlavnaStranaKontroler.lblBrojBodovaNaProfilu;

public class PitanjaZaKviz {

	private String pitanje;
	private String tacanOdgovor;
	private ArrayList<String> pogresniOdgovori;

	private static ArrayList<Button> dugmad;
	private Button slucajnoOdabranoDugme;

	private static int indeksPitanja = 0;
	private static int brojPitanja = 1;
	private static int rezultat = 0;
	private static int brojTacnihOdgovora = 0;

	public PitanjaZaKviz(String pitanje, String tacanOdgovor, String pogresanOdgovor1, String pogresanOdgovor2) {
		this.pitanje = pitanje;
		this.tacanOdgovor = tacanOdgovor;
		this.pogresniOdgovori = new ArrayList<>();
		this.pogresniOdgovori.add(pogresanOdgovor1);
		this.pogresniOdgovori.add(pogresanOdgovor2);
	}

	public static ArrayList<PitanjaZaKviz> ucitajPitanja(String putanja) {
		ArrayList<PitanjaZaKviz> listaPitanja = new ArrayList<>();

		try {
			Files.lines(Paths.get(putanja)).forEach(linijaDatoteke -> {
				// Ignorise linije sa komentarima: '//'
				if (linijaDatoteke.startsWith("//") || linijaDatoteke.isEmpty()) {
					return;
				}
				String[] podaci = linijaDatoteke.split(";");
				listaPitanja
						.add(new PitanjaZaKviz(podaci[0].trim(), podaci[1].trim(), podaci[2].trim(), podaci[3].trim()));
			});
		} catch (IOException ex) {
			FileLogger.log(Level.SEVERE, null, ex);
			ObavjestenjaDijalog.showErrorDialog("Greska", "Greska tokom ucitavanja datoteke sa pitanja!",
					"Datoteka sa pitanjima nije pronadjena na sljedecoj putanji: \n"
							+ new File(putanja).getAbsolutePath());
		}
		if (listaPitanja.isEmpty()) {
			ObavjestenjaDijalog.showErrorDialog("Upozorenje", "Upozorenje tokom ucitavanja pitanja!",
					"Datoteka sa pitanjima je prazna. \n");
		}
		return listaPitanja;
	}

	public static void postaviDugmad(Button... nizDugmadi) {
		dugmad = new ArrayList<>(Arrays.asList(nizDugmadi));
	}

	public void prikaziPitanje(Label lblPitanje, Label lblBrojPitanja) {

		ArrayList<Button> pomocnaDugmad = new ArrayList<>(dugmad);

		for (Button b : pomocnaDugmad) {
			b.getStyleClass().remove("tacno");
			b.getStyleClass().remove("pogresno");
		}

		lblPitanje.setText(this.pitanje);
		lblBrojPitanja.setText("Pitanje broj: " + brojPitanja);

		Random rand = new Random();
		int slucajanBroj = rand.nextInt(3);
		slucajnoOdabranoDugme = pomocnaDugmad.get(slucajanBroj);

		// Postavi korektno dugme za tacan odgovor
		pomocnaDugmad.get(slucajanBroj).setText(this.tacanOdgovor);
		pomocnaDugmad.remove(slucajanBroj);
		// Sada nema potrebe da provjeravam ostalu dugmad

		Collections.shuffle(this.pogresniOdgovori);
		for (Button b : pomocnaDugmad)
			b.setText(this.pogresniOdgovori.get(pomocnaDugmad.indexOf(b)));
	}

	public void provjeriOdgovor(Button dugme, ArrayList<PitanjaZaKviz> pitanja, Label lblRezultat) {

		if (this.slucajnoOdabranoDugme == dugme) {
			dugme.getStyleClass().add("tacno");
			rezultat += 20;
			brojTacnihOdgovora += 1;
		} else {
			dugme.getStyleClass().add("pogresno");
			this.slucajnoOdabranoDugme.getStyleClass().add("tacno");
			rezultat -= 30;
		}
		lblRezultat.setText("Broj osvojenih poena: " + rezultat);

		if (pitanja.size() == brojPitanja) {
			if (brojTacnihOdgovora == 5) {
				rezultat += 50;
				lblRezultat.setText("Broj osvojenih poena: " + rezultat);
			}

			obracunajBodove();
			prikaziDijalog();

			KvizKontroler.setKraj(true);
			vratiPocetneVrijednosti();
		}
		if (!KvizKontroler.isKraj()) {
			brojPitanja += 1;
			indeksPitanja += 1;
		}
	}

	private void obracunajBodove() {
		korisnik.getTrenutnaIgra().setBrojOsvojenihPoena(rezultat);
		korisnik.setBrojPoenaNaProfilu(korisnik.getBrojPoenaNaProfilu() + rezultat);
		lblBrojBodovaNaProfilu.setText(Integer.toString(korisnik.getBrojPoenaNaProfilu()));
	}

	private void prikaziDijalog() {

		// Racunanje procenta
		int procenatUspjesnosti = (int) ((brojTacnihOdgovora / 5.0) * 100);

		// Prikaz obavjestenja
		String naslov = "Obavjestenje";
		String zaglavlje = "Zavrsili ste igru \"Kviz\"!";
		String poruka = "Rezultat: " + rezultat + "\nBroj tacnih odgovora: " + brojTacnihOdgovora
				+ "\nBroj postavljenih pitanja: 5" + "\nProcenat uspjesnosti: (" + procenatUspjesnosti + "%)";
		ObavjestenjaDijalog.showInfoDialog(naslov, zaglavlje, poruka);
	}

	private void vratiPocetneVrijednosti() {
		indeksPitanja = 0;
		brojPitanja = 1;
		rezultat = 0;
		brojTacnihOdgovora = 0;
	}

	public static int getIndeksPitanja() {
		return indeksPitanja;
	}

	public static void setIndeksPitanja(int indeksPitanja) {
		PitanjaZaKviz.indeksPitanja = indeksPitanja;
	}

}
