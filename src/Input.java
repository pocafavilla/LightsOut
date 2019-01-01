import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;

public class Input implements MouseInputListener {
	public int mausX;
	public int mausY;

	public Input(Hauptklasse hauptklasse) {
		hauptklasse.requestFocus();
		hauptklasse.addMouseListener(this);

	}

	boolean mausGeklickt = false;

	@Override
	public void mouseClicked(MouseEvent e) {
		mausGeklickt = true;
		mausX = e.getX();
		mausY = e.getY();

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}
}