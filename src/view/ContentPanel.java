package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import controller.Controller;

public class ContentPanel extends JPanel implements ActionListener, KeyListener{
	
	Controller controller;
	
	private QueuePanel queuePanel;
	
	private String continualLabel = "Play all in playlist";
	private String timerLabel = "Play for";
	static String continual = "CONTINUAL";
	static String timer = "TIMER";
	static String time = "TIME";
	static String sleep = "SLEEP";
	static String blackOut = "BLACKOUT";
	
	private boolean blackOutOnPlay;
	
	JCheckBox blackoutMode;
	JCheckBox sleepMode;
	
	private JTextField timerText;
	
	public ContentPanel(Controller cont){
		super(new BorderLayout());
		
		blackOutOnPlay = true;
		
		controller = cont;
		
		setFocusable(true);
		setBackground(Color.gray);
		
		setPreferredSize(new Dimension(400, 460));
		
		timerText = null;
		
		queuePanel = new QueuePanel(cont, controller.getDefaultPlaylist());
		
		this.add(addModeRadioPanel(), BorderLayout.NORTH);
		this.add(queuePanel, BorderLayout.CENTER);
	}
	
	private JPanel addModeRadioPanel(){
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel mode = new JPanel(new GridBagLayout()){
			public void paint(Graphics g){
				super.paint(g);
				Graphics2D g2 = (Graphics2D)g;
				g2.setColor(Color.black);
				g2.setStroke(new BasicStroke(3));
				g2.drawLine(0, 50, this.getWidth(), 50);
			}
		};
		mode.setBackground(Color.gray);
		mode.setPreferredSize(new Dimension(400, 50));
		
		boolean timerSelected = controller.getTimerMode();
		
		JRadioButton continualMode = new JRadioButton(continualLabel);
		continualMode.setActionCommand(continual);
		continualMode.setSelected(!timerSelected);
		continualMode.setBackground(Color.gray);
		continualMode.setForeground(Color.black);
		continualMode.addActionListener(this);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.6;
		constraints.gridx = 0;
		constraints.gridy = 0;
		mode.add(continualMode, constraints);
		
		JRadioButton timerMode = new JRadioButton(timerLabel);
		timerMode.setActionCommand(timer);
		timerMode.setSelected(timerSelected);
		timerMode.setBackground(Color.gray);
		timerMode.setForeground(Color.black);
		timerMode.addActionListener(this);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.1;
		constraints.gridx = 1;
		constraints.gridy = 0;
		mode.add(timerMode, constraints);
		
		ButtonGroup radio = new ButtonGroup();
		radio.add(continualMode);
		radio.add(timerMode);
		
		timerText = new JTextField(controller.getTimerDuration());
		timerText.setDisabledTextColor(Color.black);
		timerText.setBackground(controller.getTimerMode() ? Color.white : Color.lightGray);
		timerText.setEnabled(controller.getTimerMode());
		timerText.addKeyListener(this);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.2;
		constraints.gridx = 2;
		constraints.gridy = 0;
		mode.add(timerText, constraints);
		
		JLabel timerLabel = new JLabel(" minutes");
		timerLabel.setBackground(Color.gray);
		timerLabel.setForeground(Color.black);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.1;
		constraints.gridx = 3;
		constraints.gridy = 0;
		mode.add(timerLabel, constraints);
		
		JPanel checkBoxes = new JPanel(new BorderLayout());
		checkBoxes.setBackground(Color.gray);
		
		sleepMode = new JCheckBox("Sleep when done");
		sleepMode.setSelected(controller.getSleepMode());
		sleepMode.setBackground(Color.gray);
		sleepMode.setForeground(Color.black);
		sleepMode.setActionCommand(sleep);
		sleepMode.addActionListener(this);
		checkBoxes.add(sleepMode, BorderLayout.NORTH);
		
		blackoutMode = new JCheckBox("Blackout on play");
		blackoutMode.setSelected(controller.getBlackoutMode());
		blackoutMode.setBackground(Color.gray);
		blackoutMode.setForeground(Color.black);
		blackoutMode.setActionCommand(blackOut);
		blackoutMode.addActionListener(this);
		checkBoxes.add(blackoutMode, BorderLayout.CENTER);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.1;
		constraints.gridx = 4;
		constraints.gridy = 0;
		mode.add(checkBoxes, constraints);
		
		return mode;
	}
	
	public void paint(Graphics g){
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.black);
		g2.setStroke(new BasicStroke(2));
		g2.drawLine(0, getHeight(), getWidth(), getHeight());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(timer)){
			timerText.setEnabled(true);
			timerText.setBackground(Color.white);
			controller.setTimerInterval(timerText.getText());
		}
		else if(e.getActionCommand().equals(continual)){
			timerText.setEnabled(false);
			timerText.setBackground(Color.lightGray);
			controller.setContinualMode();
		}
		else if(e.getActionCommand().equals(sleep)){
			controller.setSleepMode(sleepMode.isSelected());
		}
		else if(e.getActionCommand().equals(blackOut)){
			controller.setBlackOutMode(blackoutMode.isSelected());
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		controller.setTimerInterval(timerText.getText());
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
}
