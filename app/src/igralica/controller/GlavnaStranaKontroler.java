package igralica.controller;

import static igralica.controller.PocetnaStranaKontroler.korisnik;
import static igralica.dialogs.RegistracijaKorisnikaDijalog.mapaBodovaNaProfilu;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import igralica.model.Igra;
import igralica.model.Kljuc;
import igralica.model.Kljuc.StatusKljuca;
import igralica.dialogs.ObavjestenjaDijalog;
import igralica.utility.CSVUtils;
import igralica.utility.FileLogger;
import igralica.utility.FxmlLoader;
import igralica.utility.Putanje;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class GlavnaStranaKontroler implements Putanje {

	@FXML
	private ImageView ivAvatar;

	@FXML
	private HBox hbKontejnerZaLabelu;

	@FXML
	private Label lblIme;

	@FXML
	private Button btnPogodi;

	@FXML
	private Button btnInfoPogodi;

	@FXML
	private Button btnKviz;

	@FXML
	private Button btnInfoKviz;

	@FXML
	private Button btnLoto;

	@FXML
	private Button btnInfoLoto;

	@FXML
	private Button btnMojBroj;

	@FXML
	private Button btnInfoMojBroj;

    @FXML
    private HBox hbKontejnerZaRangListu;

	@FXML
    private HBox hbKontejnerZaTabelu;

	@FXML
	private Button btnSacuvajRezultat;

	@FXML
	private Button btnIzadji;

	public static String tipIgre;

	public static TableView<Igra> tblRangLista;
	public static TableColumn<Igra, String> tcPozicija;
	public static TableColumn<Igra, String> tcImeIgraca;
	public static TableColumn<Igra, String> tcDatumIgranja;
	public static TableColumn<Igra, String> tcBrojBodova;
	public static ComboBox<String> cbRangLista;
	public static Label lblBrojBodovaNaProfilu;

	public static HashMap<String, Kljuc> mapaKljuceva = new HashMap<String, Kljuc>();
	public static ObservableList<Igra> listaOdigranihIgara;
	public static ObservableList<Igra> listaGrupisanihIgara;

	@FXML
	void initialize() throws MalformedURLException {

		mapaBodovaNaProfilu = ucitajBodoveNaProfilu(PUTANJA_DO_BODOVA_KORISNIKA);
		korisnik.setBrojPoenaNaProfilu(mapaBodovaNaProfilu.get(korisnik.getKorisnickoIme()));

		kreirajLabeluZaBodoveNaProfilu();
		lblBrojBodovaNaProfilu.setText(Integer.toString(korisnik.getBrojPoenaNaProfilu()));

		kreirajTabelu();
		kreirajPadajucuListu();

		mapaKljuceva = ucitajKljuceve(PUTANJA_DO_LISTE_KLJUCEVA);

		cbRangLista.getItems().add("Pogodi broj");
		cbRangLista.getItems().add("Kviz");
		cbRangLista.getItems().add("Loto");
		cbRangLista.getItems().add("Moj broj");

		listaOdigranihIgara = ucitajOdigraneIgre(PUTANJA_DO_RANG_LISTE);

		tcPozicija.setCellValueFactory(new PropertyValueFactory<>("pozicijaURangListi"));
		tcDatumIgranja.setCellValueFactory(new PropertyValueFactory<>("datumIgranja"));
		tcBrojBodova.setCellValueFactory(new PropertyValueFactory<>("brojOsvojenihPoena"));
		tcImeIgraca.setCellValueFactory(new PropertyValueFactory<>("imeIgraca"));

		lblIme.setText(korisnik.getKorisnickoIme());

		prikazSlike();

	}

	@FXML
	void akcijaPogodi(ActionEvent event) {
		setTipIgre("Pogodi broj");
		if (daLiJeIgraAktivirana("Pogodi broj")) {
			if (daLiImaDovoljnoBodova("Pogodi broj")) {
				kreirajIgru("Pogodi broj");
				FxmlLoader.load(getClass(), "/igralica/view/PogodiBroj.fxml", "Pogodi broj");
			} else {
				ObavjestenjaDijalog.showWarningDialog("Upozorenje", "Upozorenje tokom pokretanja igre.",
						"Nije moguce pokrenuti igru \"Pogodi broj\" \nNemate dovoljno bodova na profilu!");
			}
		} else {
			ObavjestenjaDijalog.showWarningDialog("Upozorenje", "Upozorenje tokom pokretanja igre.",
					"Nije moguce pokrenuti igru \"Pogodi broj\" \nIgra do sada nije aktivirana ili je trajanje kljuca isteklo!");
			FxmlLoader.load(getClass(), "/igralica/view/UnosKljuca.fxml", "Unos kljuca");
		}
	}

	@FXML
	void infoPogodi(ActionEvent event) {
		InformacijeOIgriKontroler.setTipIgreInfo("Pogodi broj");
		FxmlLoader.load(getClass(), "/igralica/view/InformacijeOIgri.fxml", "Info");
	}

	@FXML
	void akcijaKviz(ActionEvent event) {
		setTipIgre("Kviz");
		if (daLiJeIgraAktivirana("Kviz")) {
			if (daLiImaDovoljnoBodova("Kviz")) {
				kreirajIgru("Kviz");
				FxmlLoader.load(getClass(), "/igralica/view/Kviz.fxml", "Kviz");
			} else {
				ObavjestenjaDijalog.showWarningDialog("Upozorenje", "Upozorenje tokom pokretanja igre.",
						"Nije moguce pokrenuti igru \"Kviz\" \nNemate dovoljno bodova na profilu!");
			}
		} else {
			ObavjestenjaDijalog.showWarningDialog("Upozorenje", "Upozorenje tokom pokretanja igre.",
					"Nije moguce pokrenuti igru \"Kviz\" \nIgra do sada nije aktivirana ili je trajanje kljuca isteklo!");
			FxmlLoader.load(getClass(), "/igralica/view/UnosKljuca.fxml", "Unos kljuca");
		}
	}

	@FXML
	void infoKviz(ActionEvent event) {
		InformacijeOIgriKontroler.setTipIgreInfo("Kviz");
		FxmlLoader.load(getClass(), "/igralica/view/InformacijeOIgri.fxml", "Info");
	}

	@FXML
	void akcijaLoto(ActionEvent event) {
		setTipIgre("Loto");
		if (daLiJeIgraAktivirana("Loto")) {
			if (daLiImaDovoljnoBodova("Loto")) {
				kreirajIgru("Loto");
				FxmlLoader.load(getClass(), "/igralica/view/Loto.fxml", "Loto");
			} else {
				ObavjestenjaDijalog.showWarningDialog("Upozorenje", "Upozorenje tokom pokretanja igre.",
						"Nije moguce pokrenuti igru \"Loto\" \nNemate dovoljno bodova na profilu!");
			}
		} else {
			ObavjestenjaDijalog.showWarningDialog("Upozorenje", "Upozorenje tokom pokretanja igre.",
					"Nije moguce pokrenuti igru \"Loto\" \nIgra do sada nije aktivirana ili je trajanje kljuca isteklo!");
			FxmlLoader.load(getClass(), "/igralica/view/UnosKljuca.fxml", "Unos kljuca");
		}
	}

	@FXML
	void infoLoto(ActionEvent event) {
		InformacijeOIgriKontroler.setTipIgreInfo("Loto");
		FxmlLoader.load(getClass(), "/igralica/view/InformacijeOIgri.fxml", "Info");
	}

	@FXML
	void akcijaMojBroj(ActionEvent event) {

	}

	@FXML
	void infoMojBroj(ActionEvent event) {

	}

	@FXML
	void izaberiRangListu(ActionEvent event) {
		listaGrupisanihIgara = FXCollections.observableArrayList();
		listaGrupisanihIgara.clear();
		String izbor = (String) cbRangLista.getValue();
		for (Igra igra : listaOdigranihIgara) {
			if (izbor.equals(igra.getTipIgre()))
				listaGrupisanihIgara.add(igra);
		}

		Collections.sort(listaGrupisanihIgara, new Comparator<Igra>() {
			public int compare(Igra igra1, Igra igra2) {
				return Integer.valueOf(igra1.getBrojOsvojenihPoena())
						.compareTo(Integer.valueOf(igra2.getBrojOsvojenihPoena()));
			}
		});

		Collections.reverse(listaGrupisanihIgara);
		int velicinaStatistike = listaGrupisanihIgara.size() > 10 ? 10 : listaGrupisanihIgara.size();
		ArrayList<Igra> prvihDeset = new ArrayList<Igra>(listaGrupisanihIgara.subList(0, velicinaStatistike));
		for (int i = 0; i < prvihDeset.size(); i++)
			prvihDeset.get(i).setPozicijaURangListi(i + 1);
		tblRangLista.setItems(FXCollections.observableList(prvihDeset));
		tblRangLista.refresh();
	}

	@FXML
	void sacuvajRezultat(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Snimi rezultate");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("CSV Files", "*.csv"));
		fileChooser.setInitialDirectory(new File("Statistika"));
		File odaberiFajl = fileChooser.showSaveDialog((Stage) btnSacuvajRezultat.getScene().getWindow());

		if (odaberiFajl != null) {
			try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(odaberiFajl)))) {

				// zaglavlje
				CSVUtils.writeLine(out, Arrays.asList("Korisnicko ime", "Vrsta igre", "Datum igranja", "Broj poena"));
				for (Igra igra : listaOdigranihIgara) {
					List<String> lista = new ArrayList<>();

					lista.add(igra.getImeIgraca());
					lista.add(igra.getTipIgre());
					lista.add(igra.getDatumIgranja());
					lista.add(Integer.toString(igra.getBrojOsvojenihPoena()));

					CSVUtils.writeLine(out, lista);
				}
			} catch (IOException e) {
				FileLogger.log(Level.SEVERE, null, e);
				ObavjestenjaDijalog.showErrorDialog("Greska", "Ne moze se snimiti		 fajl!",
						"Greska tokom snimanja fajla na lokaciji: \n" + odaberiFajl.getAbsolutePath());
			}
		}

	}

	@FXML
	void izadji(ActionEvent event) {
		sacuvajOdigranuIgru(listaOdigranihIgara, PUTANJA_DO_RANG_LISTE);
		sacuvajKljuceve(mapaKljuceva, PUTANJA_DO_LISTE_KLJUCEVA);
		mapaBodovaNaProfilu.put(korisnik.getKorisnickoIme(), korisnik.getBrojPoenaNaProfilu());
		sacuvajBodoveNaProfilu(mapaBodovaNaProfilu, PUTANJA_DO_BODOVA_KORISNIKA);
		System.exit(0);
	}

	private void prikazSlike() throws MalformedURLException {
		ivAvatar.setVisible(true);
		File file = korisnik.getFotografija();
		String slikaKorisnika = file.toURI().toURL().toString();

		Image slika = new Image(slikaKorisnika, true);
		ivAvatar.setSmooth(true);
		ivAvatar.setCache(true);
		ivAvatar.setImage(slika);
	}

	private void kreirajLabeluZaBodoveNaProfilu() {
		lblBrojBodovaNaProfilu = new Label(Integer.toString(korisnik.getBrojPoenaNaProfilu()));
		lblBrojBodovaNaProfilu.setFont(new Font("System", 17));
		lblBrojBodovaNaProfilu.setTextFill(Color.web("#e42f06"));
		lblBrojBodovaNaProfilu.setFont(Font.font("Verdana", FontWeight.BOLD, 70));
		lblBrojBodovaNaProfilu.setFont(Font.font("Verdana", FontPosture.ITALIC, 20));
		hbKontejnerZaLabelu.getChildren().add(lblBrojBodovaNaProfilu);
	}

	@SuppressWarnings("unchecked")
	public void kreirajTabelu() {
		tblRangLista = new TableView<Igra>();
		tblRangLista.setId("tblRangLista");
		tblRangLista.setMaxHeight(280.0);
		tblRangLista.setPrefHeight(280.0);
		tblRangLista.setMinHeight(280.0);
		tblRangLista.setStyle("-fx-background-color: #d5f6f6;");

		tcPozicija = new TableColumn<Igra, String>();
		tcImeIgraca = new TableColumn<Igra, String>();
		tcDatumIgranja = new TableColumn<Igra, String>();
		tcBrojBodova = new TableColumn<Igra, String>();

		tcPozicija.setId("tcPozicija");
		tcPozicija.setEditable(false);
		tcPozicija.setMaxWidth(50.0);
		tcPozicija.setMinWidth(50.0);
		tcPozicija.setPrefWidth(50.0);
		tcPozicija.setText("Pozicija");

		tcImeIgraca.setId("tcImeIgraca");
		tcImeIgraca.setEditable(false);
		tcImeIgraca.setMaxWidth(145.0);
		tcImeIgraca.setMinWidth(145.0);
		tcImeIgraca.setPrefWidth(145.0);
		tcImeIgraca.setText("Ime igraca");

		tcDatumIgranja.setId("tcDatumIgranja");
		tcDatumIgranja.setEditable(false);
		tcDatumIgranja.setMaxWidth(180.0);
		tcDatumIgranja.setMinWidth(180.0);
		tcDatumIgranja.setPrefWidth(180.0);
		tcDatumIgranja.setText("Datum igranja");

		tcBrojBodova.setId("tcPozicija");
		tcBrojBodova.setEditable(false);
		tcBrojBodova.setMaxWidth(100.0);
		tcBrojBodova.setMinWidth(100.0);
		tcBrojBodova.setPrefWidth(100.0);
		tcBrojBodova.setText("Broj bodova");

		tblRangLista.getColumns().addAll(tcPozicija, tcImeIgraca, tcDatumIgranja, tcBrojBodova);
		tblRangLista.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

		hbKontejnerZaTabelu.getChildren().add(tblRangLista);
	}

	private void kreirajPadajucuListu(){
		cbRangLista = new ComboBox<String>();
		cbRangLista.setId("cbRangLista");
		cbRangLista.setOnAction( e -> izaberiRangListu(e));
		cbRangLista.setPrefHeight(250);
		cbRangLista.setPrefWidth(145.0);
		cbRangLista.setStyle("-fx-background-color: gold;");
		hbKontejnerZaRangListu.getChildren().add(cbRangLista);
	}

	private boolean daLiJeIgraAktivirana(String tipIgre) {
		for (Map.Entry<String, Kljuc> ulaz : mapaKljuceva.entrySet()) {
			if (ulaz.getValue().getImeVlasnikaKljuca().equals(korisnik.getKorisnickoIme())
					&& ulaz.getValue().getTipIgre().equals(tipIgre)) {
				if ((daLiJeIstekaoKljuc(ulaz.getValue().getVrijemeDeaktiviranjaKljuca())
						&& ulaz.getValue().getStatusKLjuca().equals(StatusKljuca.AKTIVAN))
						|| ulaz.getValue().getStatusKLjuca().equals(StatusKljuca.ISKORISCEN)) {
					// ako je kljuc neiskoristen ime se nece podudarati tako da
					// ne mozemo uci u ovaj iskaz
					ulaz.getValue().setStatusKLjuca(StatusKljuca.ISKORISCEN);
					mapaKljuceva.put(ulaz.getKey(), ulaz.getValue());
				} else
					return true;
			}
		}
		return false;
	}

	private boolean daLiJeIstekaoKljuc(LocalDateTime vrijemeDeaktiviranjaKljuca) {
		LocalDateTime trenutnoVrijeme = LocalDateTime.now();
		return trenutnoVrijeme.isAfter(vrijemeDeaktiviranjaKljuca);
	}

	private boolean daLiImaDovoljnoBodova(String tipIgre) {
		boolean rezultat = false;
		switch (tipIgre) {
		case "Pogodi broj":
			if (korisnik.getBrojPoenaNaProfilu() > 0)
				rezultat = true;
			break;
		case "Kviz":
			if (korisnik.getBrojPoenaNaProfilu() > 0)
				rezultat = true;
			break;
		case "Loto":
			if (korisnik.getBrojPoenaNaProfilu() > 100)
				rezultat = true;
			break;

		default:
		}
		return rezultat;
	}

	private void kreirajIgru(String tipIgre) {
		LocalDateTime vrijeme = LocalDateTime.now();
		DateTimeFormatter formatVremena = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
		String datumIgranja = formatVremena.format(vrijeme);
		korisnik.setTrenutnaIgra(new Igra(tipIgre, korisnik.getKorisnickoIme(), datumIgranja, 0));
		listaOdigranihIgara.add(korisnik.getTrenutnaIgra());
	}

	/*
	 * Serijalizacija broja bodova na profilu
	 */
	public static boolean sacuvajBodoveNaProfilu(HashMap<String, Integer> mapaBodova, String putanja) {
		File putanjaFile = new File(putanja);
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(putanjaFile))) {
			oos.writeObject(new HashMap<String, Integer>(mapaBodova));
			oos.close();
			return true;
		} catch (IOException ex) {
			FileLogger.log(Level.SEVERE, null, ex);
			ObavjestenjaDijalog.showErrorDialog("Greska", "Greska tokom serijalizacije bodova na profilu korisnika.",
					"Nije moguce sacuvati bodove na sljedecoj putanji: \n" + putanjaFile.getAbsolutePath());
		}
		return false;
	}

	/*
	 * Deserijalizacija broja bodova na profilu
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<String, Integer> ucitajBodoveNaProfilu(String putanja) {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(putanja)))) {
			HashMap<String, Integer> mapa = (HashMap<String, Integer>) ois.readObject();
			return mapa;
		} catch (EOFException ex) {
			// izuzetak se baca ako je na pocetku data datoteka prazna, sto je i
			// slucaj za prvog registrovanog korisnika. Slucaj samo upisati u log.
			FileLogger.log(Level.WARNING, null, ex);
			return new HashMap<String, Integer>();
		} catch (ClassNotFoundException | IOException ex) {
			FileLogger.log(Level.WARNING, null, ex);
			ObavjestenjaDijalog.showWarningDialog("Upozorenje",
					"Upozorenje tokom deserijalizacije bodova na profilu korisnika.",
					"Nije moguce ucitati bodove sa sljedece putanje: \n" + new File(putanja).getAbsolutePath());
		}
		return null;
	}

	/*
	 * Serijalizacija kljuceva
	 */
	private static boolean sacuvajKljuceve(HashMap<String, Kljuc> mapaKljuceva, String putanja) {
		File putanjaFile = new File(putanja);
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(putanjaFile))) {
			oos.writeObject(new HashMap<String, Kljuc>(mapaKljuceva));
			oos.close();
			return true;
		} catch (IOException ex) {
			FileLogger.log(Level.SEVERE, null, ex);
			ObavjestenjaDijalog.showErrorDialog("Greska", "Greska tokom serijalizacije kljuceva.",
					"Nije moguce sacuvati kljuceve na sljedecoj putanji: \n" + putanjaFile.getAbsolutePath());
		}
		return false;
	}

	/*
	 * Deserijalizacija kljuceva
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, Kljuc> ucitajKljuceve(String putanja) {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(putanja)))) {
			HashMap<String, Kljuc> mapa = (HashMap<String, Kljuc>) ois.readObject();
			return mapa;
		} catch (EOFException ex) {
			FileLogger.log(Level.WARNING, null, ex);
			return new HashMap<String, Kljuc>();
		} catch (ClassNotFoundException | IOException ex) {
			FileLogger.log(Level.WARNING, null, ex);
			ObavjestenjaDijalog.showWarningDialog("Upozorenje", "Upozorenje tokom deserijalizacije kljuceva.",
					"Nije moguce ucitati kljuceve sa sljedece putanje: \n" + new File(putanja).getAbsolutePath());
		}
		return null;
	}

	/*
	 * Serijalizacija odigranih igara
	 */
	private boolean sacuvajOdigranuIgru(ObservableList<Igra> listaOdigranihIgara, String putanja) {
		File putanjaFile = new File(putanja);
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(putanjaFile))) {
			oos.writeObject(new ArrayList<Igra>(listaOdigranihIgara));
			oos.close();
			return true;
		} catch (IOException ex) {
			FileLogger.log(Level.SEVERE, null, ex);
			ObavjestenjaDijalog.showErrorDialog("Greska", "Greska tokom serijalizacije odigranih igara.",
					"Nije moguce sacuvati rang listu na sljedecoj putanji: \n" + putanjaFile.getAbsolutePath());
		}
		return false;
	}

	/*
	 * Deserijalizacija odigranih igara
	 */
	@SuppressWarnings("unchecked")
	private ObservableList<Igra> ucitajOdigraneIgre(String putanja) {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(putanja)))) {
			ArrayList<Igra> list = (ArrayList<Igra>) ois.readObject();
			return FXCollections.observableArrayList(list);
		} catch (EOFException ex) {

		} catch (ClassNotFoundException | IOException ex) {
			FileLogger.log(Level.WARNING, null, ex);
			ObavjestenjaDijalog.showWarningDialog("Upozorenje", "Upozorenje tokom deserijalizacije odigranih igara.",
					"Nije moguce ucitati rang listu sa sljedece putanje: \n" + new File(putanja).getAbsolutePath());

		}
		return FXCollections.observableArrayList();
	}

	public static String getTipIgre() {
		return tipIgre;
	}

	public static void setTipIgre(String tipIgre) {
		GlavnaStranaKontroler.tipIgre = tipIgre;
	}

	public static void osvjezi(){
		listaGrupisanihIgara = FXCollections.observableArrayList();
		listaGrupisanihIgara.clear();
		String izbor = (String) cbRangLista.getValue();
		if(izbor == null)
			izbor = "Sve igre";
		for (Igra igra : listaOdigranihIgara) {
			if (izbor.equals(igra.getTipIgre()))
				listaGrupisanihIgara.add(igra);
		}

		Collections.sort(listaGrupisanihIgara, new Comparator<Igra>() {
			public int compare(Igra igra1, Igra igra2) {
				return Integer.valueOf(igra1.getBrojOsvojenihPoena())
						.compareTo(Integer.valueOf(igra2.getBrojOsvojenihPoena()));
			}
		});

		Collections.reverse(listaGrupisanihIgara);
		int velicinaStatistike = listaGrupisanihIgara.size() > 10 ? 10 : listaGrupisanihIgara.size();
		ArrayList<Igra> prvihDeset = new ArrayList<Igra>(listaGrupisanihIgara.subList(0, velicinaStatistike));
		for (int i = 0; i < prvihDeset.size(); i++)
			prvihDeset.get(i).setPozicijaURangListi(i + 1);
		tblRangLista.setItems(FXCollections.observableList(prvihDeset));
		tblRangLista.refresh();
	}
}
