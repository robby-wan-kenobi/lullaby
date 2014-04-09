import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ObjectInputStream.GetField;

import javax.swing.*;

import view.View;
import utilities.Sound;

public class Lullaby{
	
	private static int xPos = 100;
	private static int yPos = 100;
	private static int xSize = 600;
	private static int ySize = 500;
	
	public static void createLullaby(){
		JFrame frame = new JFrame("Lullaby 1.0");
		frame.getContentPane().setBackground(Color.black);
		
		frame.setBounds(xPos, yPos, xSize, ySize);
		//TODO: For anything that needs to happen on close...like save
		frame.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent evt){
					System.exit(0);
				}
			}
		);
		
		//Also can add:
		// menu bar (JMenuBar)
		// etc...
		// add JPanel that's the view
		View view = new View();
		frame.addKeyListener(view);
		frame.getContentPane().add(view);
		frame.setVisible(true);
		
//		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//		gd.setFullScreenWindow(frame);
	}
	public static void main(String[] args){
		//System.out.println("Welcome");
		
		createLullaby();
	}
}
