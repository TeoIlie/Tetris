import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.awt.image.*;

public class TetrisPanel extends Panel implements KeyListener, MouseListener
{
	//******************************************************************
	//                       INSTANCE FEILDS
	//******************************************************************
	private Tetris			game		= null;					//object responsible with tetris processing
	private Color 			background 	= new Color(255,255,255);//background color

	private int				code		= 2;					//code for the first piece type in Tetris

	private Dimension 		dim			= null ;				//dimension
	private BufferedImage 	osi  		= null ;				//off-screen image for double-buffering
	private Graphics  		osg 		= null ;				//off-screen graphics of double-buffering

	private final int SHIFT_R 			= KeyEvent.VK_RIGHT;	//shifts piece right, with right arrow
	private final int SHIFT_L 			= KeyEvent.VK_LEFT;		//shifts piece left, with left arrow
	private final int HARD_DROP 		= KeyEvent.VK_SPACE;	//drops piece, with spacebar
    private final int SOFT_DROP 		= KeyEvent.VK_DOWN;	//drops piece, with spacebar
	private final int ROTATE_CW 		= KeyEvent.VK_UP;		//rotate piece clockwise with up arrow
	private final int ROTATE_CCW 		= KeyEvent.VK_Z;		//rotate piece counterclockwise with down arrow

	public static int		level		= 0;					//current level
	private	int				count		= 0;					//count until next level
    private int[]			counts		= {1000, 800, 700, 600, 500, 400, 350, 300, 250, 200, 150, 100, 50};	//holds idle runs for the current level (until program drop)
	private int 			delay		= 1000;					//timer intial delay
	private int 			interval 	= 1;					//timer interval
	private Timer 			timer		= new Timer() ;			//timer to move pieces
	private TimerTask 		task 		= new TimerTask()		//TimerTask to program movement of pieces
	{
		public void run()
		{
            //increase level every 10 lines cleared
            if(game != null && game.linesCleared == 10 && level < counts.length-1)
            {
                level++;
                game.linesCleared = 0;
            }
			if(count < counts[level])
				count++;
			else
			{
				count = 0;
				//perform reaction
				game.moveDown();
			}
			//display the gameplay every millisecond
			repaint();
		}
	};

	//******************************************************************
	//                       CONSTRUCTOR
	//******************************************************************
	public TetrisPanel()
	{
		addKeyListener(this);
		addMouseListener(this);
		setBackground(background);
		timer.scheduleAtFixedRate(task, delay, interval ) ;
	}

	//******************************************************************
	//                       PUBLIC METHODS
	//******************************************************************
	public void paint (Graphics g)
	//allocates new memory for double-buffering
	{
		dim = getSize();
		osi = new BufferedImage( dim.width, dim.height, BufferedImage.TYPE_INT_RGB ) ;
		osg = osi.getGraphics();
		update( g ) ;
	}
	public void update( Graphics g )
	//implements drawing instrcutions
	{
		// paint the entire off-screen image using the background color
		osg.setColor( background );
		osg.fillRect(0, 0, dim.width, dim.height ) ;

		//DRAWING INSTRUCTIONS

        //opening title
		if(game == null)
        {
			game = new Tetris(code);
            try
            {
                Thread.sleep(10);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
            for(int i = 0; i < 100; ++i)
            {
                osg.setColor( background );
                osg.fillRect(0, 0, dim.width, dim.height ) ;
                int fontSize = 40;

                osg.setFont(new Font("Cochin", Font.BOLD, fontSize));
                osg.setColor(new Color((int)(50 + i*2.0),(int)(50 + i*2.0),(int)(50 + i*2.0)));
                osg.drawString("Welcome to Tetris!", dim.width/2 - 160, dim.height/2 - 40);
                g.drawImage( osi, 0, 0, this );
                try
                {
                    Thread.sleep(15);
                }
                catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
            }
            try
            {
                Thread.sleep(100);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        }

        //display game during gameplay
        if(!game.gameOver)
            game.display(osg, dim);

        //display "Game Over" at end
        else
        {
			osg.setColor( background );
			osg.fillRect(0, 0, dim.width, dim.height ) ;
			int fontSize = 40;

			osg.setFont(new Font("Cochin", Font.BOLD, fontSize));
			osg.setColor(new Color((int)(50),(int)(50),(int)(50)));
			osg.drawString("Game Over!", dim.width/2 - 100, dim.height/2 - 50);
			osg.setFont(new Font("Cochin", Font.BOLD, fontSize - 20));
			osg.drawString("Click to Restart", dim.width/2 - 60, dim.height/2 - 20);
			g.drawImage( osi, 0, 0, this );
        }

		// copy the offscreen image to the screen
		g.drawImage( osi, 0, 0, this );
	}

	public void keyPressed(KeyEvent ke)
	//used to program user key inputs
	{
		int code = ke.getKeyCode();
		switch(code)
		//program all user input actions, and their reactions
		{
			case SHIFT_R:
				game.moveRight();
				break;
			case SHIFT_L:
				game.moveLeft();
				break;
			case HARD_DROP:
				game.hardDrop();
				break;
            case SOFT_DROP:
                game.moveDown();
                break;
			case ROTATE_CW:
				game.rotateClockwise();
				break;
			case ROTATE_CCW:
				game.rotateCounterClockwise();
				break;
            default:
                break;

		}
	}

	public void keyReleased(KeyEvent ke)
	{}

	public void keyTyped(KeyEvent ke)
	{}

	public void mouseClicked(MouseEvent e)
	//used only to restart game when it is over
	{
		if(game.gameOver)
		//if game has finsihed, restart the game
		{
			//clear landscape
			for(int r = 0; r < game.landscape.length; ++r)
				for(int c = 0; c < game.landscape[r].length; ++c)
					game.landscape[r][c] = -1;

			//create new piece
			game.nextPiece();

			//set level to 0
			level = 0;

			//set gameOver false
			game.gameOver = false;
		}
	}

	public void	mouseEntered(MouseEvent e)
	{}

	public void	mouseExited(MouseEvent e)
	{}

	public void	mousePressed(MouseEvent e)
	{}

	public void	mouseReleased(MouseEvent e)
	{}
}
