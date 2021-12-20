	import java.awt.* ;
import java.awt.event.*;

public class TetrisWindow extends Frame implements WindowListener
{
	TetrisPanel panel = new TetrisPanel();

	TetrisWindow()
	//the construcotr creates a Frame with a TetrisPanel panel
	{
		setTitle("Tetris");
		add(panel);
		addWindowListener(this);
		setResizable(false);
		setVisible(true);
		setSize(600,800);
	}

	public void windowActivated(WindowEvent e)
	{}

	public void windowClosed(WindowEvent e)
	{}

	public void windowClosing(WindowEvent e)
	{
	//this method, of class WindowListener, makes the 'x' window button close the game
		System.exit(0);
	}

	public void windowDeactivated(WindowEvent e)
	{}

	public void windowDeiconified(WindowEvent e)
	{}

	public void windowIconified(WindowEvent e)
	{}

	public void windowOpened(WindowEvent e)
	{}
}
