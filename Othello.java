//*Group 5 Project: Othello Game*//
//*Members:Seth Stovall, Kevin Massey, Juston Swanner, Armando Minor*//
//*This Java Code runs and implements a classic Othello game*//
//*Run Code to play the game*//
//*User is BLACK; Computer is WHITE*//

import java.awt.*;
import java.applet.*;

public class Othello extends Applet implements Runnable
{
    Thread othelloGame;                              // declare a thread for the game

    boolean black_shown=false;                       // create instance
    boolean show =false;                             // create instance
    final int BLACK = 1;                             // declare state of each square
    final int WHITE = 2;
    final int empty = 0;
    final int outOfBounds = -1;

    final static int Game[][] = new int[10][10];    // create a 10x10 board

    protected int blackChipCount = 0;               // create chip count for black
    protected int whiteChipCount = 0;               // create chip count for white

    public void start()                             // start the thread
    {
        if (othelloGame == null)
        {
            black_shown=false;
            othelloGame = new Thread(this);
            othelloGame.start();
        }
    }

    public void stop()                             // stop the thread
    {
        if (othelloGame != null)
        {
            othelloGame.stop();
            othelloGame = null;
            black_shown=false;
        }
    }

    public synchronized void run()                // initialize board
    {
        setBackground(Color.green);

        for (int i=0; i<10; i++)                  // initialize out of bounds
        {
            Game[i][0] = outOfBounds;             // create boundaries with arrays to form the game board
            Game[i][9] = outOfBounds;
            Game[0][i] = outOfBounds;
            Game[9][i] = outOfBounds;
        }

        for (int i=1; i<9; i++)                   // initialize game board to be empty
            for (int j=1; j<9; j++)               // by making all spaces blank
                Game[i][j] = empty;

        Game[4][4] = WHITE;                      // set up game board for play
        Game[5][4] = BLACK;
        Game[4][5] = BLACK;
        Game[5][5] = WHITE;

        while (othelloGame !=null)               // signal game to wait after painting
        {                                        // black and before white responds
            while (!black_shown)
            {
                try {
                    wait();
                } catch (InterruptedException e){ }
                black_shown=false;
                showStatus("Good move!");
                pause(1000);
                whiteResponds();
            }
        }
    }

    // BLACK turn and update screen
    public synchronized boolean mouseUp(Event evt, int x, int y)
    {
        Dimension gameBoard = size();                 // find out which square was clicked
        int column = (x*8)/gameBoard.width + 2;       // column
        int row = (y*8)/gameBoard.height + 1/2 ;      // row
        boolean black_done;                           // true if black cannot move anywhere

        if (legalMove(row,column,BLACK,WHITE,true))   // run if legal move exist
        {
            Game[row][column] = BLACK;                // set that square to black
            repaint();                                // if the move is legal
            black_shown=true;
            notify();
        }
        else showStatus("You can't put a chip there!");
        black_done=true;                             // check if black can move if not alert player
        for (int i=1; i<9; i++)
            for (int j=1; j<9; j++)
                if (legalMove(i,j,BLACK,WHITE,false) )
                    black_done=false;

        if (black_done)                            // if black can't move white takes turn
            for (int i=1; i<65; i++)
                whiteResponds();

        return true;
    }
    
    //*Computer (WHITE) takes an automated turn by checking all possible moves
    public void whiteResponds()
    {
        boolean found;   	                 //true if a legal square is found
        int i, j;				             //indices for loops

        found=false;
        if (legalMove(1,1,WHITE,BLACK,true)) //first check corners
        {
            Game[1][1]=WHITE;
            found=true;
        }
        if ( (!found) && (legalMove(8,8,WHITE,BLACK,true)) )
        {
            Game[8][8]=WHITE;
            found=true;
        }
        if ( (!found) && (legalMove(1,8,WHITE,BLACK,true)) )
        {
            Game[1][8]=WHITE;
            found=true;
        }
        if ( (!found) && (legalMove(8,1,WHITE,BLACK,true)) )
        {
            Game[8][1]=WHITE;
            found=true;
        }

        i=3;				// check center squares
        while ((!found) && (i < 7))
        {
            j=3;
            while ( (!found) && (j < 7))
            {
                if	(legalMove(i,j,WHITE,BLACK,true))
                {
                    Game[i][j]=WHITE;
                    found=true;
                }
                j++;
            }
            i++;
        }

        i=3;
        while ((!found) && (i < 7))    // then check edges except for those
        {                           // surrounding a corner
            if (legalMove(1,i,WHITE,BLACK,true))
            {
                Game[1][i]=WHITE;
                found=true;
            }
            if ( (!found) && (legalMove(8,i,WHITE,BLACK,true)))
            {
                Game[8][i]=WHITE;
                found=true;
            }
            if ( (!found) && (legalMove(i,1,WHITE,BLACK,true)))
            {
                Game[i][1]=WHITE;
                found=true;
            }
            if ( (!found) && (legalMove(i,8,WHITE,BLACK,true)))
            {
                Game[i][8]=WHITE;
                found=true;
            }
            i++;
        }


        i=3;
        while ((!found) && (i < 7))		// next check inner edges
        {
            if (legalMove(2,i,WHITE,BLACK,true))
            {
                Game[2][i]=WHITE;
                found=true;
            }
            if ( (!found) && (legalMove(7,i,WHITE,BLACK,true)))
            {
                Game[7][i]=WHITE;
                found=true;
            }
            if ( (!found) && (legalMove(i,2,WHITE,BLACK,true)))
            {
                Game[i][2]=WHITE;
                found=true;
            }
            if ( (!found) && (legalMove(i,7,WHITE,BLACK,true)))
            {
                Game[i][7]=WHITE;
                found=true;
            }
            i++;
        }

        i=1;		// finally check squares surrounding a corner
        while ((!found) && (i < 9))
        {
            j=1;
            while ((!found) && (j < 9))
            {
                if (legalMove(i,j,WHITE,BLACK,true))
                {
                    found=true;
                    Game[i][j]=WHITE;
                }
                j++;
            }
            i++;
        }

        repaint();
    }

    //* stabilizer for legal move
    public boolean legalMove(int r, int c, int color, int othercolor,
                             boolean flip)
    {
        int i,j;                                  // position on board
        boolean legal;                            // true if move is legal
        int stepCount;                            // counts the stepping across the board

        legal = false;

        if (Game[r][c] == empty)                  // square clicked must be empty
        {
            for (int xDir=-1; xDir < 2; xDir++)
                for (int yDir=-1; yDir < 2; yDir++)
                {
                    stepCount = 0;
                    do
                    {
                        stepCount++;
                        i = r + stepCount*xDir;   // steps along x-axis
                        j = c + stepCount*yDir;   // steps along y-axis
                    }
                    while ( (i > 0) && (i < 9) && (j > 0) && (j < 9) &&
                            (Game[i][j] == othercolor));
                    if (( i > 0) && (i < 9) && (j > 0) && (j < 9) &&
                            (stepCount > 1) &&
                            // You must move more than one step for legal move
                            (Game[i][j] == color) )
                    { legal = true;
                        if (flip)
                            for (int k = 1; k < stepCount; k++)
                                Game[r+xDir*k][c+yDir*k] = color;
                    }
                }
        }
        return legal;
    }


    void pause(int time)                //short time delay
    {
        try { othelloGame.sleep(time); }
        catch (InterruptedException e) { }
    }


    //*Set up the game board with the correct layout

    public void paint(Graphics board)
    {
        Dimension d = size();
        board.setColor(Color.black);
        int xoff = d.width/8;
        int yoff = d.height/8;

        blackChipCount =0;                      // initialize counts to 0
        whiteChipCount =0;
        boolean done;                           // determines game is over

        for (int i=1; i<=8; i++)                // draw the grid lines for the board game
        {
            board.drawLine(i * xoff, 0, i * xoff, d.height);
            board.drawLine(0, i * yoff, d.width, i * yoff);
        }

        for (int i=1; i<9; i++)                // scan board for black discs
            for (int j=1; j<9; j++)
            {
                if (Game[i][j] == BLACK)       // draw black discs
                {
                    board.fillOval((j * yoff + 3) - yoff, (i * xoff + 3) - xoff, 33, 33);
                    blackChipCount++;
                }
            }

        board.setColor(Color.white);
        for (int i=1; i<9; i++)                // scan board for white discs
            for (int j=1; j<9; j++)
            {
                if (Game[i][j] == WHITE)       // draw white discs
                {
                    board.fillOval((j * yoff + 3) - yoff, (i * xoff + 3) - xoff, 33, 33);
                    whiteChipCount++;
                }
            }

        board.setColor(Color.blue);           // letter color set to blue

        done=true;

        for (int i=1; i<9; i++)
            for (int j=1; j<9; j++)
                if ((legalMove(i,j,BLACK,WHITE,false)) ||
                        (legalMove(i,j,WHITE,BLACK,false)))
                    done=false;

        if (done)                            // if game is complete declare winner
        {
            if (whiteChipCount > blackChipCount)
                board.drawString("White won with "+ whiteChipCount +" discs.",10,20);
            else if (blackChipCount > whiteChipCount)
                board.drawString("Black won with "+ blackChipCount +" discs.",10,20);
            else board.drawString("Tied game",10,20);
        }
        else                                // if game is currently running show current winner
        {
            if (whiteChipCount > blackChipCount)
                board.drawString(" White is winning with "+ whiteChipCount +" discs",10,20);
            else if (blackChipCount > whiteChipCount)
                board.drawString(" Black is winning with "+ blackChipCount +" discs",10,20);
            else board.drawString("Game is tied",10,20);
        }

        if (show)                           // if show is true and move is legal draw game piece
        {
            for (int i=1; i<9; i++)
                for (int j=1; j<9; j++)
                    if (legalMove(i,j,BLACK,WHITE,false))
                        board.fillOval((j*yoff+15)-yoff,(i*xoff+15)-xoff,5,5);
        }
    }
}