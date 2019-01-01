import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Hashtable;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Hauptklasse extends Canvas implements Runnable, ActionListener, ChangeListener {

	private static final long serialVersionUID = 1L;
	public int breite = 125;
	public int hoehe = 125;
	public int faktor = 5;
	public static final String NAME = "Lights out";
	private boolean inOptionen;
	private boolean modusZwei;
	private boolean laeuft;
	private int anzahlFelderX = 5;
	private int anzahlFelderY = 5;
	private int reihenPixel = anzahlFelderX * 25 * 25;
	private boolean wurdeVeraendert;
	private boolean hatGewonnen;

	private JFrame frame = new JFrame(NAME);
	private JButton knopf = new JButton("OPTIONEN");
	private JButton knopf2 = new JButton("Modus wechseln");	
	private JButton knopf3 = new JButton("Zurück");
	
	private JSlider schiebeReglerX = new JSlider(JSlider.VERTICAL, 3, 10, anzahlFelderX);
	private JSlider schiebeReglerY = new JSlider(JSlider.VERTICAL, 3, 10, anzahlFelderY);
	private BorderLayout layout = new BorderLayout();
	private Hashtable<Integer, JLabel> feldWerteX = new Hashtable<Integer, JLabel>();
	private Hashtable<Integer, JLabel> feldWerteY = new Hashtable<Integer, JLabel>();

	private Input input = new Input(this);
	private Einleser einleser = new Einleser("LightsOut.png");
	private BufferedImage bild = new BufferedImage(breite, hoehe, BufferedImage.TYPE_INT_RGB);

	private int[] pixel = ((DataBufferInt) bild.getRaster().getDataBuffer()).getData();
	private boolean[] lichter;


	public Hauptklasse() {

		setMinimumSize(new Dimension(breite * faktor, hoehe * faktor));
		setMaximumSize(new Dimension(breite * faktor, hoehe * faktor));
		setPreferredSize(new Dimension(breite * faktor, hoehe * faktor));

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(layout);
		frame.add(this, BorderLayout.CENTER);

		knopf.setBackground(new Color(28));
		knopf.addActionListener(this);
		frame.add(knopf, BorderLayout.EAST);

		frame.pack();

		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		knopf2.setBackground(new Color(28));
		knopf2.addActionListener(this);
		knopf2.setPreferredSize(new Dimension(500, 100));
		knopf3.setPreferredSize(new Dimension(100, 500));
		knopf3.setBackground(new Color(28));
		knopf3.addActionListener(this);

		schiebeReglerX.setPreferredSize(new Dimension(200, 100));
		schiebeReglerX.setPaintLabels(true);
		schiebeReglerX.addChangeListener(this);
		for (int i = 2; i < 10; i++) {
			feldWerteX.put(i, new JLabel(i + "X"));
		}
		schiebeReglerX.setLabelTable(feldWerteX);

		schiebeReglerY.setPreferredSize(new Dimension(200, 100));
		schiebeReglerY.setPaintLabels(true);
		schiebeReglerY.addChangeListener(this);
		for (int i = 2; i < 10; i++) {
			feldWerteY.put(i, new JLabel(i + "Y"));
		}
		schiebeReglerY.setLabelTable(feldWerteY);

		spielAufbau();
	}

	public synchronized void start() {
		laeuft = true;
		new Thread(this).start();
	}

	public synchronized void stop() {
		laeuft = false;
	}

	private void spielAufbau() { //Das Spielfeld wird aufgebaut
		lichter = new boolean[anzahlFelderX * anzahlFelderY];
		for (int i = 0; i < (lichter.length); i++) {
			setzeFeld(0, i);
		}
		Random zufallsGenerator = new Random();
		for (int i = 0; i < lichter.length; i++) {
			if (zufallsGenerator.nextBoolean()) {
				spielerZieht1(i % anzahlFelderX, i / anzahlFelderX);
			}
		}
		hatGewonnen = false;
	}

	public void run() {
		wurdeVeraendert = false;
		while (laeuft) {
			render();
			if (input.mausGeklickt) {
				if (!hatGewonnen) {
					if (modusZwei) {
						spielerZieht2(input.mausX / (25 * faktor), input.mausY / (25 * faktor));
					} else {
						spielerZieht1(input.mausX / (25 * faktor), input.mausY / (25 * faktor));
					}
				}
			}
		}
	}

	private void spielerZieht1(int xPos, int yPos) { // Fuer den ersten Spielmodus
		input.mausGeklickt = false;
		int feld = xPos + yPos * anzahlFelderX;
		aendereFeld(feld);
		for (int y = -1; y < 2; y++) {
			for (int x = -1; x < 2; x++) {
				if (Math.abs(x + y) == 1 // fuer das Kreuz
						&& xPos + x < (anzahlFelderX) && xPos + x >= 0  // fuer den vertikalen  Rand
						&& yPos + y < (anzahlFelderY) && yPos + y >= 0  // fuer den horizontalen Rand
				) {
					aendereFeld(feld + x + y * anzahlFelderX);
				}
			}
		}
		pruefeAufGewinn();
	}

	private void spielerZieht2(int xPos, int yPos) { // Fuer den zweiten Spielmodus
		input.mausGeklickt = false;
		for (int y = 0; y <= yPos; y++) {
			for (int x = 0; x <= xPos; x++) {
				aendereFeld((x) + (y) * anzahlFelderX);
			}
		}
		pruefeAufGewinn();
	}

	private void pruefeAufGewinn() {
		for (int i = 0; !lichter[i]; i++) { // Zaehlt so lange weiter, wie die
											// Lichter aus sind.
			if (i == lichter.length - 1) {  // Wenn alle Lichter aus sind...
				for (int j = 0; j < pixel.length; j++) {
					pixel[j] = 52480; // Macht das BufferedImage grün, wenn man gewonnen hat
				} 
				hatGewonnen = true;
				return;
			}
		}
	}

	private void aendereFeld(int feldPos) {
		if (lichter[feldPos]) { // macht ein helles Feld dunkel
			lichter[feldPos] = false;
			setzeFeld(0, feldPos);
		} else {
			lichter[feldPos] = true; // macht ein dunkeles Feld hell
			setzeFeld(3, feldPos);
		}
	}

	private void setzeFeld(int feldVorlagePos, int feldPos) { // gibt einem Feld ein bestimmtes Design
		for (int y = 0; y < 25; y++) {
			for (int x = 0; x < 25; x++) {
				pixel[(x + y * breite) + feldPos % anzahlFelderX * 25
						+ feldPos / anzahlFelderX * reihenPixel] = einleser.pixel[x + y * 150 + feldVorlagePos % 5 * 25
								+ feldVorlagePos / 5 * 3125];
			}
		}
	}

	private synchronized void render() {
		try {
			BufferStrategy bs = getBufferStrategy();
			if (bs == null) {
				createBufferStrategy(3);
				return;
			}

			Graphics g = bs.getDrawGraphics();
			g.drawImage(bild, 0, 0, getWidth(), getHeight(), null);
			g.dispose();
			bs.show();
		} catch (Exception e) {

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == knopf) { // Das Optionsmenü wird aufgerufen
			laeuft = false;
			inOptionen = true;
			frame.remove(layout.getLayoutComponent(BorderLayout.EAST));
			frame.add(knopf2, BorderLayout.NORTH);
			frame.remove(layout.getLayoutComponent(BorderLayout.CENTER));
			frame.add(knopf3, BorderLayout.CENTER);
			frame.add(schiebeReglerY, BorderLayout.EAST);
			frame.add(schiebeReglerX, BorderLayout.WEST);

			frame.pack();
		} else {
			if (wurdeVeraendert) {
				reihenPixel = anzahlFelderX * 25 * 25;
				hoehe = 25 * anzahlFelderY;
				breite = 25 * anzahlFelderX;
				faktor = 4 - (anzahlFelderX + anzahlFelderY) / 9;
				
				setMinimumSize(new Dimension(breite * faktor, hoehe * faktor));
				setMaximumSize(new Dimension(breite * faktor, hoehe * faktor));
				setPreferredSize(new Dimension(breite * faktor, hoehe * faktor));
				
				bild = new BufferedImage(anzahlFelderX * 25, anzahlFelderY * 25, BufferedImage.TYPE_INT_RGB);
				pixel = ((DataBufferInt) bild.getRaster().getDataBuffer()).getData();
				input = new Input(this);
			}
			if (e.getSource() == knopf2) { // Der Modus wird gewechselt
				if (modusZwei) {
					modusZwei = false;
				} else {
					modusZwei = true;
				}
				spielAufbau();
			} else if (wurdeVeraendert) { // Zurück zum Spiel
				spielAufbau();
			}
			zumSpiel();

		}
	}

	private void zumSpiel() { // ändert das Layout so, dass das Feld wieder angezeigt wird

		frame.remove(layout.getLayoutComponent(BorderLayout.NORTH));
		frame.remove(layout.getLayoutComponent(BorderLayout.CENTER));
		frame.remove(layout.getLayoutComponent(BorderLayout.EAST));
		frame.remove(layout.getLayoutComponent(BorderLayout.WEST));

		frame.add(knopf, BorderLayout.EAST);
		frame.add(this, BorderLayout.CENTER);
		frame.pack();

		laeuft = true;
		new Thread(this).start();
		inOptionen = false;
	}

	@Override
	public void stateChanged(ChangeEvent e) { // fuer den Schiebereger
		anzahlFelderX = schiebeReglerX.getValue();
		anzahlFelderY = schiebeReglerY.getValue();
		wurdeVeraendert = true;
	}

	public static void main(String[] args) {
		new Hauptklasse().start();
	}
}