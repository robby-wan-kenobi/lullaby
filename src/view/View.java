package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import controller.Controller;

import model.Model;

public class View extends JPanel implements ActionListener, KeyListener{
	private Model model = new Model();
	private Controller controller = new Controller(model, this);
	
	private JTextArea statusArea;
	private JPanel statusPanel;
	private ContentPanel contentPanel;
	
	private boolean blackedOut;
	private DisplayMode origDisplay;
	private GraphicsDevice device;
	
	private Blank blank;
	
	public View(){
		super(new BorderLayout());
		addKeyListener(this);
		setFocusable(true);
		
		blackedOut = false;
		
		// Top Banner Panel
		JPanel topBanner = new JPanel(){
			public void paint(Graphics g){
				super.paint(g);
				Graphics2D g2 = (Graphics2D)g;
				g2.setFont(new Font("Arial", Font.BOLD, 14));
				g2.drawString("Robby's Lullaby Machine", 25, 25);
				g2.setColor(Color.black);
				g2.setStroke(new BasicStroke(3));
				g2.drawLine(0, 30, this.getWidth(), 30);
			}
		};
		topBanner.setPreferredSize(new Dimension(400, 30));
		topBanner.setBackground(Color.white);
		add(topBanner, BorderLayout.NORTH);
		
		statusPanel = null;
		
		add(addStatusPanel(), BorderLayout.SOUTH);
		
		// Content Panel
		contentPanel = new ContentPanel(controller);
		contentPanel.addKeyListener(this);
		add(contentPanel, BorderLayout.CENTER);
	}
	
	private JPanel addStatusPanel(){
		statusPanel = new JPanel(new BorderLayout());
		statusPanel.setPreferredSize(new Dimension(400, 30));
		statusPanel.setBackground(Color.darkGray);
		
		statusArea = new JTextArea();
		statusArea.setEnabled(false);
		statusArea.setDisabledTextColor(Color.black);
		statusPanel.add(statusArea);
		
		JPanel buttonPanel = new JPanel(new BorderLayout());
		
		JButton clear = new JButton("Clear");
		clear.addActionListener(this);
		clear.setActionCommand("CLEAR");
		buttonPanel.add(clear, BorderLayout.WEST);
		
		JButton about = new JButton("About");
		about.addActionListener(this);
		about.setActionCommand("ABOUT");
		buttonPanel.add(about, BorderLayout.EAST);
		
		statusPanel.add(buttonPanel, BorderLayout.EAST);
		
		return statusPanel;
	}
	
	public void setStatus(String status){
		statusArea.setDisabledTextColor(Color.black);
		statusArea.setText(status);
		repaint();
	}
	
	public void errorMessage(String message){
		statusArea.setDisabledTextColor(Color.red);
		statusArea.setText(message);
		repaint();
	}
	
	public void blackOut(){
		if(!controller.getBlackoutMode() || blackedOut)
			return;
		
		JFrame mainFrame = (JFrame)(this.getParent().getParent().getParent().getParent());
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    device = env.getDefaultScreenDevice();

// Taken from http://gpsnippets.blogspot.com/2007/08/toggle-fullscreen-mode.html
//------------------------------------------------------------------------------
	    origDisplay = device.getDisplayMode();
	    setVisible(false);
	    mainFrame.dispose();
	    mainFrame.setUndecorated(true);
	    device.setFullScreenWindow(mainFrame);
//------------------------------------------------------------------------------
	    
// Taken from http://stackoverflow.com/questions/1984071/how-to-hide-cursor-in-a-swing-application
//------------------------------------------------------------------------------------------------
		// Transparent 16 x 16 pixel cursor image.
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		
		// Create a new blank cursor.
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
				cursorImg, new Point(0, 0), "blank cursor");
		
		// Set the blank cursor to the JFrame.
		mainFrame.getContentPane().setCursor(blankCursor);
//------------------------------------------------------------------------------------------------
		blackedOut = true;
		
		blank = new Blank();
		for(GraphicsDevice gd: env.getScreenDevices()){
			if(gd != device){
				gd.setFullScreenWindow(blank);
			}
		}
	}
	
	public void unBlackOut(){
		JFrame mainFrame = (JFrame)(this.getParent().getParent().getParent().getParent());
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    
		device.setDisplayMode(origDisplay);
		mainFrame.dispose();
		mainFrame.setUndecorated(false);
	    device.setFullScreenWindow(null);
	    setVisible(true);
	    mainFrame.setVisible(true);
		
		mainFrame.getContentPane().setCursor(Cursor.getDefaultCursor());
		
		blackedOut = false;
		
		blank.dispose();
	}
	
//	Anything that is static for all panels will go in this paint method.
//	Everything else will go in subsequent panels.
	public void paint(Graphics g){
		super.paint(g);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE && controller.getBlackoutMode()){
			unBlackOut();
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("ABOUT")){
			String message = "Lullaby 1.0 - 12/8/2012\n";
			message += "Author: Robby Canady\n";
			message += "Supported file types: mp3\n";
			message += "Convertable file types: m4a, wav\n\n";
			message += "Used some ideas/code from the following sites:\n";
			message += "http://gpsnippets.blogspot.com/2007/08/toggle-fullscreen-mode.html\n";
			message += "http://codeidol.com/java/swing/Lists-and-Combos/Reorder-a-JList-with-Drag-and-Drop/\n";
			message += "http://www.velocityreviews.com/forums/t130884-process-runtime-exec-causes-subprocess-hang.html\n";
			message += "http://stackoverflow.com/questions/1984071/how-to-hide-cursor-in-a-swing-application\n\n";
			message += "Used the following Java library:\n";
			message += "java-audio-player (maryb.player - http://code.google.com/p/java-audio-player/)\n\n";
			message += "Used the following programs for file conversion (both made by debian - http://www.debian.org/):\n";
			message += "faad\n";
			message += "lame\n";
			JOptionPane.showMessageDialog(this, message, "About", JOptionPane.INFORMATION_MESSAGE);
		}
		else if(e.getActionCommand().equals("CLEAR")){
			controller.clearStatus();
		}
	}
}
