package igralica.dialogs;

import static igralica.controller.GlavnaStranaKontroler.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import igralica.model.Korisnik;
import igralica.utility.PasswordUtils;
import igralica.utility.Putanje;
import igralica.utility.ValidacijaUnosa;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RegistracijaKorisnikaDijalog implements Putanje{

	private Korisnik korisnik;
	private boolean potvrdjeno;

	private Stage primaryStage;
	private String naslovDijaloga;

	private TextField tfImeKorisnika;
	private TextField tfPrezimeKorisnika;
	private TextField tfKorisnickoIme;
	private PasswordField pfLozinka;
	private PasswordField pfLozinkaPonovljeno;
	private TextField tfFotografija;
	private ImageView ivFotografija;

	public static ArrayList<String> listaKorisnickihImena = new ArrayList<>();
	public static HashMap<String, Integer> mapaBodovaNaProfilu = new HashMap<String, Integer>();

	/**
	 * Inicijalizacija dijaloga za registraciju
	 *
	 * @param naslov
	 *            naslov dijaloga
	 * @param korisnik
	 *            dodavanje korisnika na sistem
	 **/
	public RegistracijaKorisnikaDijalog(String naslov, Korisnik korisnik) {

		this.naslovDijaloga = naslov;

		tfImeKorisnika = new TextField();
		tfPrezimeKorisnika = new TextField();
		tfKorisnickoIme = new TextField();
		pfLozinka = new PasswordField();
		pfLozinkaPonovljeno = new PasswordField();
		tfFotografija = new TextField();
		ivFotografija = new ImageView(
				new Image(getClass().getResourceAsStream("/igralica/style/PodrazumijevanaSlika.png")));

		ucitajListuKorisnickihImena();
		setKorisnik(korisnik);
	}

	/**
	 * Dinamicko kreiranje GUI forme
	 **/
	public void kreirajIPrikazi() {

		GridPane gridpane = new GridPane();
		gridpane.setAlignment(Pos.CENTER);
		gridpane.setHgap(15);
		gridpane.setVgap(10);
		gridpane.setPadding(new Insets(15, 15, 15, 15));
		gridpane.setId("root");

		ColumnConstraints kolona1 = new ColumnConstraints();
		kolona1.setPercentWidth(60);
		ColumnConstraints kolona2 = new ColumnConstraints();
		kolona2.setPercentWidth(50);
		gridpane.getColumnConstraints().addAll(kolona1, kolona2);

		// Labela i tekst polje za ime korisnika
		Label lblNaziv = new Label("Ime:");
		lblNaziv.setId("labela");
		gridpane.add(new VBox(5, lblNaziv, tfImeKorisnika), 0, 0, 1, 1);

		// Labela i tekst polje za prezime korisnika
		Label lblPrezime = new Label("Prezime:");
		lblPrezime.setId("labela");
		gridpane.add(new VBox(5, lblPrezime, tfPrezimeKorisnika), 0, 1, 1, 1);

		// Labela i tekst polje za korisničko ime
		Label lblKorisnickoIme = new Label("Korisničko ime:");
		lblKorisnickoIme.setId("labela");
		gridpane.add(new VBox(5, lblKorisnickoIme, tfKorisnickoIme), 0, 2, 1, 1);

		// Labela i tekst polje za lozinku
		Label lblLozinka = new Label("Lozinka:");
		lblLozinka.setId("labela");
		gridpane.add(new VBox(5, lblLozinka, pfLozinka), 0, 3, 1, 1);

		// Labela i tekst polje za ponavljanje lozinke
		Label lblPonoviLozinku = new Label("Ponovi lozinku:");
		lblPonoviLozinku.setId("labela");
		gridpane.add(new VBox(5, lblPonoviLozinku, pfLozinkaPonovljeno), 0, 4, 1, 1);

		// Fotografija
		ivFotografija.setFitHeight(200);
		ivFotografija.setFitWidth(200);
		ivFotografija.setSmooth(true);
		ivFotografija.setCache(true);
		GridPane.setHalignment(ivFotografija, HPos.CENTER);
		gridpane.add(ivFotografija, 1, 0, 1, 4);

		// Labela, tekst polje i dugme za izbor fotografije
		Label lblFotografija = new Label("Fotografija:");
		lblFotografija.setId("labela");
		tfFotografija.setPrefWidth(180);

		Button btnUnesiteFotografiju = new Button("Unesi fotografiju");
		btnUnesiteFotografiju.setId("dugme");
		btnUnesiteFotografiju.setPrefWidth(200);
		btnUnesiteFotografiju.setOnAction(e -> ucitajFotografiju());

		gridpane.add(new VBox(5, lblFotografija, new HBox(5, btnUnesiteFotografiju)), 1, 4, 1, 1);

		// Dugmad za potvrdu i otkazivanje
		Button btnPotvrdi = new Button("Potvrdi");
		btnPotvrdi.setId("dugme");
		btnPotvrdi.setOnAction(e -> potvrdi());

		Button btnOtkazi = new Button("Otkazi");
		btnOtkazi.setId("dugme");
		btnOtkazi.setOnAction(e -> zatvori());

		HBox hbButtons = new HBox(10, btnPotvrdi, btnOtkazi);
		hbButtons.setPadding(new Insets(10, 0, 0, 0));
		hbButtons.setAlignment(Pos.CENTER);
		gridpane.add(hbButtons, 0, 5, 2, 1);

		// Postavi scenu, prikazi dijalog i cekaj da ga korisnik zatvori
		Scene scene = new Scene(gridpane);
		scene.getStylesheets().add(getClass().getResource("/igralica/style/RegistracijaDijalog.css").toExternalForm());
		primaryStage = new Stage();
		primaryStage.setScene(scene);
		primaryStage.setTitle(naslovDijaloga);
		primaryStage.setResizable(false);
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.showAndWait();
	}

	/**
	 * Provjera da li je korisnik kliknuo na dugme potvrdi u dijalogu.
	 *
	 * @return true ako je korisnik kliknuo na dugme potvrdi
	 **/
	public boolean isPotvrdjeno() {
		return potvrdjeno;
	}

	/**
	 * Seter za korisnike koji se registruju u dijalogu
	 *
	 * @param korisnik
	 *
	 **/
	private void setKorisnik(Korisnik korisnik) {
		this.korisnik = korisnik;

		tfImeKorisnika.setText(korisnik.getIme());
		tfPrezimeKorisnika.setText(korisnik.getPrezime());
		tfKorisnickoIme.setText(korisnik.getKorisnickoIme());

		if (korisnik.getFotografija() != null && korisnik.getFotografija().isFile()) {
			tfFotografija.setText(korisnik.getFotografija().getAbsolutePath());
			ivFotografija.setImage(new Image(korisnik.getFotografija().toURI().toString()));
		}
	}

	/*
	 * Metoda za unos fotografije od strane korisnika
	 */
	private void ucitajFotografiju() {
		FileChooser fileChooser = new FileChooser();

		fileChooser.setTitle("Otvori fotografiju");
		fileChooser.setInitialDirectory(new File(PUTANJA_DO_SLIKA_KORISNIKA));
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Image Files", "*.jpg", "*.png"));

		File odabranaFotografija = fileChooser.showOpenDialog(primaryStage);
		if (odabranaFotografija != null) {
			String apsolutnaPutanja = odabranaFotografija.getAbsolutePath();
			String roditeljskaPutanja = odabranaFotografija.getParent();
			String relativnaPutanja = new File(roditeljskaPutanja).toURI().relativize(new File(apsolutnaPutanja).toURI()).getPath();
			relativnaPutanja = "." + File.separator + PUTANJA_DO_SLIKA_KORISNIKA + relativnaPutanja;
			tfFotografija.setText(relativnaPutanja);
			ivFotografija.setImage(new Image(odabranaFotografija.toURI().toString()));
		}
	}

	/*
	 * Validacija korisnickih unosa tokom registracije.
	 *
	 * @return true ako je korisnik pravilno unio potrebne podatke
	 **/
	private boolean provjeriUnos() {
		String porukaOPogresnomUnosu = "";

		if (ValidacijaUnosa.praznoPolje(tfImeKorisnika.getText())) {
			porukaOPogresnomUnosu += "Unesite ime korisnika!\n";
		}

		if (ValidacijaUnosa.praznoPolje(tfPrezimeKorisnika.getText())) {
			porukaOPogresnomUnosu += "Unesite prezime korisnika!\n";
		}

		if (ValidacijaUnosa.praznoPolje(tfKorisnickoIme.getText())) {
			porukaOPogresnomUnosu += "Unesite korisničko ime!\n";
		}

		File photo = new File(tfFotografija.getText());
		if (!photo.isFile()) {
			porukaOPogresnomUnosu += "Unesite fotografiju!\n";
		}

		if (porukaOPogresnomUnosu.length() != 0) {
			ObavjestenjaDijalog.showWarningDialog("Upozorenje",
					"Niste unijeli sva polja!\nUnesite sljedeca polja u dijalog:", porukaOPogresnomUnosu);
			return false;
		} else if (ValidacijaUnosa.vecPostoji(tfKorisnickoIme.getText())) {
			ObavjestenjaDijalog.showWarningDialog("Upozorenje", "Uneseno korisnicko ime vec postoji!",
					"Pokusajte ponovo.");
			return false;
		} else if (ValidacijaUnosa.duzinaLozinke(pfLozinka.getText())) {
			ObavjestenjaDijalog.showWarningDialog("Upozorenje", "Lozinka mora biti duža od cetiri karaktera!",
					"Pokusajte ponovo.");
			return false;
		} else if (ValidacijaUnosa.nepodudaranjeLozinki(pfLozinka.getText(), pfLozinkaPonovljeno.getText())) {
			ObavjestenjaDijalog.showWarningDialog("Upozorenje", "Unesene lozinke se ne podudaraju!", "Pokusajte ponovo.");
			return false;
		} else
			return true;
	}

	/*
	 * Metoda se poziva kada korisnik klikne na dugme potvrdi
	 * Upis podataka o korisniku na fajl-sistem.
	 */
	private void potvrdi() {
		if (provjeriUnos()) {
			korisnik.setIme(tfImeKorisnika.getText());
			korisnik.setPrezime(tfPrezimeKorisnika.getText());
			korisnik.setKorisnickoIme(tfKorisnickoIme.getText());
			korisnik.setFotografija(new File(tfFotografija.getText()));
			korisnik.setBrojPoenaNaProfilu(10);

			unesiBodove(korisnik.getKorisnickoIme());

			try (PrintWriter pw = new PrintWriter(
					new BufferedWriter(new FileWriter(PUTANJA_DO_LISTE_KORISNIKA, true)), true)) {
				pw.println(upisiPodatke());
			} catch (IOException e) {
				e.printStackTrace();
			}

			potvrdjeno = true;
			zatvori();
		}
	}

	/*
	 * Foramtiranje zapisa za upis podataka o korisniku na fajl-sistem
	 *
	 * @return String kao format zapisa o korisnik koji se registruje na sistem
	 **/
	private String upisiPodatke() {
		String linijaUpisa;
		String salt = PasswordUtils.getSalt(30);
		String enkriptovanaIBase64KodovanaKorisnickaLozinka = PasswordUtils.generateSecurePassword(pfLozinka.getText(),
				salt);

		linijaUpisa = korisnik.getKorisnickoIme() + "#" + korisnik.getIme() + "#" + korisnik.getPrezime() + "#" + salt
				+ "#" + enkriptovanaIBase64KodovanaKorisnickaLozinka + "#" + korisnik.getFotografija();
		return linijaUpisa;
	}

	/*
	 * Ucitavanje korisnickih imena sa fajl-sistema
	 **/

	private void ucitajListuKorisnickihImena() {
		try (BufferedReader in = new BufferedReader(new FileReader(PUTANJA_DO_LISTE_KORISNIKA))) {
			String linijaDatoteke;
			String[] podaci;
			while ((linijaDatoteke = in.readLine()) != null) {
				podaci = linijaDatoteke.split("#");
				listaKorisnickihImena.add(podaci[0]);
			}
		} catch (IOException ex) {
			String porukaOGresci = "IO greska se javlja tokom citanja iz stream-a.!";
			ObavjestenjaDijalog.showErrorDialog("Greska", "Doslo je do greske!", porukaOGresci);
		}
	}

	/**
	 * Unos bodova na profilu korisnika tokom registracije
	 **/
	private static void unesiBodove(String korisnickoIme){
		mapaBodovaNaProfilu = ucitajBodoveNaProfilu(PUTANJA_DO_BODOVA_KORISNIKA);
		mapaBodovaNaProfilu.put(korisnickoIme, 10);
		sacuvajBodoveNaProfilu(mapaBodovaNaProfilu, PUTANJA_DO_BODOVA_KORISNIKA);
	}

	/**
	 * Zatvaranje dijaloga
	 **/
	private void zatvori() {
		primaryStage.close();
	}

}
