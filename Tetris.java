import java.util.*;
import java.awt.*;
import java.awt.Font;

public class Tetris
{
    //******************************************************************
    //                       INSTANCE FEILDS
    //******************************************************************
	private int[][]		curr		= new int[4][4];		//a current piece
	private int 		row 		= 0;					//row(piece top left) in landscape (where 0 is first row in border)
	private int 		col 		= 0;					//col(piece top left) in landscape (where 0 is first column in border)
	public int[][]		landscape	= new int[28][18];		//holds pieces on landscape, with border all around of thickness 4, -1 means empty
	private int			shift		= 4;					//holds where visible landscape starts (landscape[shift, shift])
    public boolean      gameOver    = false;                //becomes true when game is over

    // 0 = I
	private int[][] Ipiece = {
					{0,1,0,0},
					{0,1,0,0},
					{0,1,0,0},
					{0,1,0,0}
				};
	// 1 = J
	private int[][] Jpiece = {
					{0,0,1,0},
					{0,0,1,0},
					{0,1,1,0},
					{0,0,0,0}
				};
	// 2 = L
	private int[][] Lpiece = {
					{0,1,0,0},
					{0,1,0,0},
					{0,1,1,0},
					{0,0,0,0}
				};
	// 3 = O
	private int[][] Opiece = {
					{0,0,0,0},
					{0,1,1,0},
					{0,1,1,0},
					{0,0,0,0}
				};
	// 4 = S
	private int[][] Spiece = {
					{0,1,0,0},
					{0,1,1,0},
					{0,0,1,0},
					{0,0,0,0}
				};
	// 5 = T
	private int[][] Tpiece = {
					{0,0,0,0},
					{0,1,1,1},
					{0,0,1,0},
					{0,0,0,0}
				};
	// 6 = Z
	private int[][] Zpiece = {
					{0,0,1,0},
					{0,1,1,0},
					{0,1,0,0},
					{0,0,0,0}
			};
	private int[][][] 		pieces		= {Ipiece, Jpiece, Lpiece, Opiece, Spiece, Tpiece, Zpiece};		//holds pieces

    private Color           blue        = new Color(99, 191, 227);
    private Color           darkBlue    = new Color(75, 137, 189);
    private Color           orange      = new Color(234, 147, 80);
    private Color           yellow      = new Color(253, 196, 73);
    private Color           green       = new Color(133, 200, 137);
    private Color           purple      = new Color(228, 105, 131);
    private Color           red         = new Color(217, 99, 88);
    private	Color[] 		colors      = {blue, darkBlue, orange, yellow, green, purple, red}; //holds pieces' colors

	private Point			panelTopLeft= new Point();			//point positioned at the top left of the panel
	private Point			fakeLandTopLeft = new Point(); 		//point on panel where fake landscape starts (with border of 4 squares)
	private int				square		= 10;					//size of one square
	private int				border		= 0;					//border of background
	private int				code 		= 0;					//code of the piece

    public int              linesCleared= 0;                    //number of lines cleared since last level

    //******************************************************************
    //                       CONSTRUCTOR
    //******************************************************************
	public Tetris(int code)
	{
		for (int row = 0; row < 4; ++row)
			for(int col = 0; col < 4; ++col)
				curr[row][col] = pieces[code][row][col];
		row = 1;
		col = 7;
		this.code = code;

		for (int r = 0; r < landscape.length; ++r)
			for(int c = 0; c < landscape[r].length; ++c)
				landscape[r][c] = -1;

		//landscape[4][4] = 1;		//top left
		//landscape[23][13] = 1;	//bottom right
	}

    //******************************************************************
    //                       PUBLIC METHODS
    //******************************************************************
	public void moveLeft()
    //piece moved left
    //undone if it was conflicting (canMove() = false)
	{
		col--;
		if(!canMove())
			col++;
	}

	public void moveRight()
    //piece moved right
    //undone if it was conflicting (canMove() = false)
	{
		col++;
		if(!canMove())
			col--;
	}

	public void rotateClockwise()
    //piece rotated clockwise,
    //by transposing and flipping vertically
    //undone if it was conflicting (canMove() = false)
	{
		// transpose
		transpose(curr);

		// swap columns
		for (int  j = 0; j < curr[0].length/2; j++)
		{
			for (int i = 0; i < curr.length; i++)
			{
				int x = curr[i][j];
				curr[i][j] = curr[i][curr[0].length -1 -j];
				curr[i][curr[0].length -1 -j] = x;
			}
		}
        if(!canMove())
            rotateCounterClockwise();
	}

	public void rotateCounterClockwise()
    //piece rotated counterclockwise,
    //by transposing and flipping horizontally
    //undone if it was conflicting (canMove() = false)
	{
		// transpose
		transpose(curr);

		//  swap rows
		for (int  i = 0; i < curr.length/2; i++)
		{
			for (int j = 0; j < curr[0].length; j++)
			{
				int x = curr[i][j];
				curr[i][j] = curr[curr.length -1 -i][j];
				curr[curr.length -1 -i][j] = x;
			}
		}
        if(canMove() == false)
            rotateClockwise();
	}

	public void moveDown()
	//piece moved one square down
	//only move if not conflicting (canMove() = false)
    {
        if(canMove())
        {
            row++;
		}
	}

	public void hardDrop()
    //piece falls straight down
    //until conflicting (canMove() = false)
	{
        while(canMove())
            ++row;
	}

	public void display(Graphics osg, Dimension dim)
    //the method that shows gameplay
    //shows background, piece, and landscape
	{
		//BACKGROUND
		if(dim.width*2 > dim.height)
			square = (int)((double)(dim.height*80/100)/21);
		else
			square = (int)((double)(dim.width*80/100)/21);

        int fontSize = 30;
        //print title
        osg.setFont(new Font("Cochin", Font.BOLD, fontSize));
        osg.setColor(Color.GRAY);
        osg.drawString("TETRIS", dim.width/2 - square * 2, 50);

        border = square/2;
        panelTopLeft.x = dim.width/2 - square*5-border;
        panelTopLeft.y = dim.height/2 - square*10-border;

        fontSize = 20;
        osg.setFont(new Font("Cochin", Font.BOLD, fontSize));
        //print level
        osg.drawString("Level: " + (TetrisPanel.level+1), panelTopLeft.x, panelTopLeft.y - 2);

        Color roundRect = new Color(200,200,200);
		osg.setColor(roundRect);
		osg.fillRoundRect(panelTopLeft.x, panelTopLeft.y, square*10+2*border, square*20+2*border, square/2, square/2);

        Color inRect = new Color(255,255,255);
		osg.setColor(inRect);
		osg.fillRect(panelTopLeft.x + border, panelTopLeft.y + border, square*10+1, square*20+1);
		panelTopLeft.x += (border + 1);
		panelTopLeft.y += (border + 1);

        Color blocks = new Color(224,224,224);
		osg.setColor(blocks);
        //draw grid
		for(int row = 0; row < 20; ++row)
			for(int col = 0; col < 10; ++col)
				osg.fillRect(panelTopLeft.x + col*square+1, panelTopLeft.y + row*square+1, square-2, square-2);

		fakeLandTopLeft.x = panelTopLeft.x - square*4;
		fakeLandTopLeft.y = panelTopLeft.y - square*4;

		//LANDSCAPE
		for(int r = shift; r < landscape.length-shift; ++r)
			for(int c = shift; c < landscape[r].length - shift; ++c)
				if(landscape[r][c] != -1)
				{
					osg.setColor(colors[landscape[r][c]]);
					osg.fillRect(fakeLandTopLeft.x + c*square+1, fakeLandTopLeft.y + r*square+1, square-2, square-2);
				}

		//PEICE
		osg.setColor(colors[code]);
		for(int r = 0; r < 4; ++r)
			for(int c = 0; c < 4; ++c)
				if(curr[r][c] == 1 && (row + r) >= shift)
					osg.fillRect(fakeLandTopLeft.x + col*square + c*square+1, fakeLandTopLeft.y + row*square + r*square+1, square-2, square-2);
	}

	private static void transpose(int[][] a)
    //used for transposing a matrix, for the rotate methods
	{
		for (int i = 0; i <a.length; i++)
		{
			for (int j = i; j < a[0].length; j++)
			{
				int temp = a[i][j];
				a[i][j] = a[j][i];
				a[j][i] = temp;
			}
		}
	}

	public boolean canMove()
    //check for sides and landscape contact
    //if it has touched the landscape, it makes it part of the landscape
    //and restarts the next piece from the top
	{
		boolean flag = true;					//initially assume possible
		int[][] tempLand = new int[28][18];
		int[][] tempCurr = new int[4][4];
		for(int r = 0; r < landscape.length; ++r)
			for(int c = 0; c < landscape[r].length; ++c)
				tempLand[r][c] = landscape[r][c];
		for(int r = 0; r < curr.length; ++r)
				for(int c = 0; c < curr[r].length; ++c)
				tempCurr[r][c] = curr[r][c];
		for(int r = 0; r < curr.length; ++r)
			for(int c = 0; c < curr[r].length; ++c)
				if(tempCurr[r][c] == 1)
                    if(tempLand [row + r][col + c] != -1)
                        return false;
                    else
                        tempLand[row + r][col + c] = 8;

		//now tempLand holds -1 for empty and 0 to 6 and up for filled for land and 8 for current piece

		//now we can check for borders
		for(int r = 0; r < landscape.length; ++r)
		{
			for(int c = 0; c < 4; ++c)
				if(tempLand[r][c] != -1)
					flag = false;

			for(int c = 14; c < 18; ++c)
				if(tempLand[r][c] != -1)
					flag = false;
		}

		//now check for touching landscape
		int above = 0;
		int below = 0;
		for(int r = 1; r < tempLand.length; ++r)
		{
			for(int c = 0; c < tempLand[0].length; ++c)
			{
				above = tempLand[r-1][c];
				below = tempLand[r][c];
				if((above == 8 &&  (below >= 0 && below <= 6)) || (above == 8 && r == 24))
				{
					flag = false;

					//add piece curr to landscape
					for(int i = 0; i < tempLand.length; ++i)
						for(int j = 0; j < tempLand[0].length; ++j)
						{
							if(tempLand[i][j] == 8)
								landscape[i][j] = code;
							else
								landscape[i][j] = tempLand[i][j];
						}

                    //clear lines
                    clearLines();

                    //check if game over
                    checkGameOver();

					//move on to next piece
					nextPiece();
                    return false;
				}
			}
		}
		return flag;
	}

	public void nextPiece()
    //generate new piece, different from previous
    //places it at the top
	{
		Random rnd = new Random();
        int nextCode = rnd.nextInt(7);

        while(nextCode == code)
            nextCode = rnd.nextInt(7);

        code = nextCode;

		for(int r = 0; r < 4; ++r)
			for(int c = 0; c < 4; ++c)
				this.curr[r][c] = this.pieces[code][r][c];

		this.row = 1;
		this.col = 7;
	}

    public void clearLines()
    //clear full lines in landscape
    //shift landscape above cleared lines down
    {
        boolean fullLine = true;    //false means line must be cleared
        for(int r = shift; r < landscape.length - shift; ++r)
        {
            fullLine = true;
            for(int c = shift; c < landscape[r].length - shift; ++c)
                if(landscape[r][c] == -1)
                    fullLine = false;
            //clear line
            if(fullLine)
            {
                linesCleared++;
                for(int r1 = r; r1 > 0; --r1)
                    for(int c1 = shift; c1 < landscape[r1].length - shift; ++c1)
                        landscape[r1][c1] = landscape[r1-1][c1];
                 for(int c1 = shift; c1 < landscape[0].length - shift; ++c1)
                     landscape[0][c1] = -1;
            }
        }
    }

    public void checkGameOver()
    //check if game is over
    //set boolean gamePver true if the game is overflowing
    //(the top 4 rows of the fake landscape must be empty)
    {
        for(int r = 0; r < 4; ++r)
            for(int c = 0; c < landscape[0].length; ++c)
                if(landscape[r][c] != -1)
                    gameOver = true;
    }
}








