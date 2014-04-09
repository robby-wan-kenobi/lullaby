package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Blank extends JFrame{
	public Blank(){
		super("");
		getContentPane().setBackground(Color.black);
		
		setVisible(false);
	    dispose();
	    setUndecorated(true);
	    
 // Taken from http://stackoverflow.com/questions/1984071/how-to-hide-cursor-in-a-swing-application
 //------------------------------------------------------------------------------------------------
  		// Transparent 16 x 16 pixel cursor image.
  		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
  		
  		// Create a new blank cursor.
  		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
  				cursorImg, new Point(0, 0), "blank cursor");
  		
  		// Set the blank cursor to the JFrame.
  		getContentPane().setCursor(blankCursor);
  //------------------------------------------------------------------------------------------------
	}
}
