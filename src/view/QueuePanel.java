package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import controller.Controller;

public class QueuePanel extends JPanel implements ActionListener, Observer{
	
	private Controller controller;
	
	private Graphics2D graphics;
	
	private JList queueJList;
	private JList availableJList;
	private JButton playPauseButton;
	private DragSource dragSource;
	private DropTarget dropTarget;
	private List availableList;
	private int startIndex;
	
	private JLabel playlistLabel;
	
	private String newCommand = "NEW";
	private String addCommand = "ADD";
	private String removeCommand = "REMOVE";
	private String playCommand = "PLAY";
	private String loadCommand = "LOAD";
	private String saveCommand = "SAVE";
	private String newPlaylistCommand = "NEW_PLAYLIST";
	private String stopCommand = "STOP";
	private String skipCommand = "SKIP";

	private Object cell;
	
	private class CellRenderer extends DefaultListCellRenderer {
		private boolean overPotentialCell;
		private boolean beforeCell;
		public CellRenderer(){
			super();
			overPotentialCell = false;
			beforeCell = false;
		}
		@Override
		public Component getListCellRendererComponent(	JList list,
														Object value,
														int index,
														boolean isSelected,
														boolean hasFocus){
			beforeCell = false;
			overPotentialCell = (cell == value) && list.getSelectedIndex() != index;
			boolean show = isSelected && (cell == null);
			if(index < list.getSelectedIndex())
				beforeCell = true;
			return super.getListCellRendererComponent(list, value, index, show, hasFocus);
		}
		@Override
		public void paint(Graphics g){
			super.paint(g);
			if(overPotentialCell){
				g.setColor(Color.black);
				if(beforeCell)
					g.drawLine(0, 0, getWidth(), 0);
				else
					g.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
			}
		}
	}
	
	
	private class QueueListener implements MouseListener, DragSourceListener, DragGestureListener, DropTargetListener{
		@Override
		public void dragGestureRecognized(DragGestureEvent dge) {
			StringSelection selection = new StringSelection(queueJList.getSelectedValue().toString());
			startIndex = queueJList.locationToIndex(dge.getDragOrigin());
			dragSource.startDrag(dge, DragSource.DefaultMoveDrop, selection, this);
		}

		@Override
		public void dragDropEnd(DragSourceDropEvent dsde) {
		}

		@Override
		public void dragEnter(DragSourceDragEvent dsde) {
		}

		@Override
		public void dragExit(DragSourceEvent dse) {
		}

		@Override
		public void dragOver(DragSourceDragEvent dsde) {
		}

		@Override
		public void dropActionChanged(DragSourceDragEvent dsde) {
		}

		@Override
		public void drop(DropTargetDropEvent dtde) {
			dtde.acceptDrop(DnDConstants.ACTION_MOVE);
	        String item = "";
	        try {
	        	item = (String)(dtde.getTransferable().getTransferData(DataFlavor.stringFlavor));
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	        int index = queueJList.locationToIndex(dtde.getLocation());
	        controller.removeFromPlaylist(startIndex);
	        controller.addToPlaylist(item, index);
	        queueJList.setListData(controller.getPlaylist().toArray());
	        dtde.dropComplete(true);
	        cell = null;
		}

		@Override
		public void dragEnter(DropTargetDragEvent dtde) {
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			int index = queueJList.locationToIndex(dtde.getLocation());
			cell = queueJList.getModel().getElementAt(index);
			repaint();
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent dtde) {
		}
		@Override
		public void mouseClicked(MouseEvent arg0) {
			if(arg0.getClickCount() == 2){
				int index = queueJList.locationToIndex(arg0.getPoint());
				String selected = (String)(queueJList.getModel().getElementAt(index));
				System.out.println(index);
				System.out.println(selected);
				controller.play(index);
			}
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
	}

	private class AvailableListener implements MouseListener{
		@Override
		public void mouseClicked(MouseEvent arg0) {
			if(arg0.getClickCount() == 2){
				int index = availableJList.locationToIndex(arg0.getPoint());
				String selected = (String)(availableJList.getModel().getElementAt(index));
				controller.addToPlaylist(selected);
			}
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
	}
	
	public QueuePanel(Controller cont, String playlist){
		super(new GridBagLayout());
		
		QueueListener qListener = new QueueListener();
		AvailableListener aListener = new AvailableListener();
		
		cell = null;
		
		controller = cont;
		controller.addObserver(this);
		
		GridBagConstraints constraints = new GridBagConstraints();
		setPreferredSize(new Dimension(400, 400));
		setBackground(Color.darkGray);
		
		JLabel availableLabel = new JLabel("Available Songs");
		availableLabel.setForeground(Color.white);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.5;
		constraints.gridx = 0;
		constraints.gridy = 0;
		add(availableLabel, constraints);
		
		JLabel queueLabel = new JLabel("Playlist");
		queueLabel.setForeground(Color.white);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.5;
		constraints.gridx = 3;
		constraints.gridy = 0;
		add(queueLabel, constraints);

		playlistLabel = new JLabel(playlist);
		playlistLabel.setForeground(Color.white);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.5;
		constraints.gridx = 4;
		constraints.gridy = 0;
		constraints.gridwidth = 3;
		add(playlistLabel, constraints);
		
		availableJList = new JList(controller.getAvailableSongs().toArray());
		availableJList.addMouseListener(aListener);
		JScrollPane available = new JScrollPane(availableJList);
		available.setPreferredSize(new Dimension(200, 280));
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.5;
		constraints.gridwidth = 3;
		constraints.gridx = 0;
		constraints.gridy = 1;
		add(available, constraints);
		
		queueJList = new JList(controller.getPlaylist(playlist).toArray());
		queueJList.addMouseListener(qListener);
		queueJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		queueJList.setCellRenderer(new CellRenderer());
		JScrollPane queue = new JScrollPane(queueJList);
		queue.setPreferredSize(new Dimension(200, 280));
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.5;
		constraints.gridwidth = 4;
		constraints.gridx = 3;
		constraints.gridy = 1;
		add(queue, constraints);
		
		JButton newSong = new JButton("Add New Song");
		newSong.setActionCommand(newCommand);
		newSong.addActionListener(this);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.25;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 2;
		add(newSong, constraints);
		
		JButton addSongs = new JButton("Add to Playlist");
		addSongs.setActionCommand(addCommand);
		addSongs.addActionListener(this);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.25;
		constraints.insets = new Insets(0, 0, 0, 2);
		constraints.gridwidth = 2;
		constraints.gridx = 1;
		constraints.gridy = 2;
		add(addSongs, constraints);
		
		JButton removeSong = new JButton("Remove");
		removeSong.setActionCommand(removeCommand);
		removeSong.addActionListener(this);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0, 2, 0, 0);
		constraints.weightx = 0.125;
		constraints.gridwidth = 1;
		constraints.gridx = 3;
		constraints.gridy = 2;
		add(removeSong, constraints);
		
		JButton loadPlaylist = new JButton("Load");
		loadPlaylist.setActionCommand(loadCommand);
		loadPlaylist.addActionListener(this);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 0.125;
		constraints.gridwidth = 1;
		constraints.gridx = 4;
		constraints.gridy = 2;
		add(loadPlaylist, constraints);
		
		JButton savePlaylist = new JButton("Save");
		savePlaylist.setActionCommand(saveCommand);
		savePlaylist.addActionListener(this);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.125;
		constraints.gridwidth = 1;
		constraints.gridx = 5;
		constraints.gridy = 2;
		add(savePlaylist, constraints);
		
		JButton newPlaylist = new JButton("New");
		newPlaylist.setActionCommand(newPlaylistCommand);
		newPlaylist.addActionListener(this);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.125;
		constraints.gridwidth = 1;
		constraints.gridx = 6;
		constraints.gridy = 2;
		add(newPlaylist, constraints);
		
		playPauseButton = new JButton("Play");
		playPauseButton.setActionCommand(playCommand);
		playPauseButton.addActionListener(this);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(2, 10, 0, 10);
		constraints.gridwidth = 1;
		constraints.gridx = 1;
		constraints.gridy = 3;
		add(playPauseButton, constraints);
		
		/*JButton pauseSongs = new JButton("Pause");
		pauseSongs.setActionCommand("");
		pauseSongs.addActionListener(this);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.25;
		constraints.gridwidth = 1;
		constraints.gridx = 2;
		constraints.gridy = 3;
		add(pauseSongs, constraints);*/
		
		JButton skipSong = new JButton("Skip");
		skipSong.setActionCommand(skipCommand);
		skipSong.addActionListener(this);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 1;
		constraints.gridx = 3;
		constraints.gridy = 3;
		add(skipSong, constraints);
		
		JButton stopSongs = new JButton("Stop");
		stopSongs.setActionCommand(stopCommand);
		stopSongs.addActionListener(this);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 1;
		constraints.gridx = 4;
		constraints.gridy = 3;
		add(stopSongs, constraints);
		
		startIndex = 0;
		
		dropTarget = new DropTarget(queueJList, qListener);
		dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(queueJList, DnDConstants.ACTION_MOVE, qListener);
	}
	
	public void paint(Graphics g){
		super.paint(g);
	}
	
	private void savePlaylist(String dialogMessage){
		if(controller.getPlaylistName().equals("")){
			String saveName = (String) JOptionPane.showInputDialog(this, dialogMessage, "Save", JOptionPane.PLAIN_MESSAGE, null, null, "Playlist");
			if(saveName != null && saveName.equals("")){
				savePlaylist("Save current playlist as (at least 1 character):");
				return;
			}
			else if(saveName != null){
				controller.savePlaylist(saveName);
			}
		}
		else{
			controller.savePlaylist();
		}
	}
	
	private void savePlaylist(){
		if(controller.getPlaylist().size() == 0){
			controller.setStatus("Playlist not saved because it's empty.");
			return;
		}
		savePlaylist("Save current playlist as:");
	}
	
// Event Handlers
//---------------

	

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(newCommand)){
			JFileChooser chooser = new JFileChooser(new File(controller.getAddPath()));
			chooser.setMultiSelectionEnabled(true);
			int res = chooser.showOpenDialog(this);
			if(res == JFileChooser.APPROVE_OPTION){
				File[] addFiles = chooser.getSelectedFiles();
				String path = chooser.getCurrentDirectory().getAbsolutePath();
				controller.setAddPath(path);
				for(File addFile: addFiles)
					controller.newSong(addFile.getAbsolutePath());
			}
		}
		else if(e.getActionCommand().equals(addCommand)){
			if(availableJList.getSelectedValues().length != 0){
				for(Object item: availableJList.getSelectedValues()){
					String value = item.toString();
					controller.addToPlaylist(value);
				}
			}
		}
		else if(e.getActionCommand().equals(removeCommand)){
			String selectedItem = null;
			if(queueJList.getSelectedValue() != null){
				selectedItem = queueJList.getSelectedValue().toString();
			}
			controller.removeFromPlaylist(selectedItem);
		}
		else if(e.getActionCommand().equals(playCommand)){
			controller.playPushed();
		}
		else if(e.getActionCommand().equals(loadCommand)){
			String playlist = (String)JOptionPane.showInputDialog(
										this,
										"Choose your playlist",
										"Playlist",
										JOptionPane.PLAIN_MESSAGE,
										null,
										controller.getPlaylists(),
										"");
			if(playlist != null)
				controller.loadPlaylist(playlist);
		}
		else if(e.getActionCommand().equals(saveCommand)){
			savePlaylist();
		}
		else if(e.getActionCommand().equals(newPlaylistCommand)){
			if(controller.getPlaylist().size() == 0)
				return;
			savePlaylist();
			controller.newPlaylist();
		}
		else if(e.getActionCommand().equals(stopCommand)){
			controller.stopPlaylist();
		}
		else if(e.getActionCommand().equals(skipCommand)){
			controller.skipSong();
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg1 instanceof String){
			if(arg1.equals(removeCommand)){
				queueJList.setListData(controller.getPlaylist().toArray());
			}
			else if(arg1.equals(addCommand)){
				queueJList.setListData(controller.getPlaylist().toArray());
			}
			else if(arg1.equals(loadCommand)){
				String playlistName = controller.getPlaylistName();
				if(playlistName.length() > 30)
					playlistName = playlistName.substring(0, 30) + "...";
				playlistLabel.setText(playlistName);
				queueJList.setListData(controller.getPlaylist().toArray());
			}
			else if(arg1.equals(newPlaylistCommand)){
				playlistLabel.setText(controller.getPlaylistName());
				queueJList.setListData(controller.getPlaylist().toArray());
			}
			else if(arg1.equals(saveCommand)){
				String playlistName = controller.getPlaylistName();
				if(playlistName.length() > 30)
					playlistName = playlistName.substring(0, 30) + "...";
				playlistLabel.setText(playlistName);
			}
			else if(arg1.equals(newCommand)){
				availableJList.setListData(controller.getAvailableSongs().toArray());
			}
			else if(arg1.equals("PLAY")){
				playPauseButton.setText("Pause");
			}
			else if(arg1.equals("PAUSE")){
				playPauseButton.setText("Play");
			}
			else if(arg1.equals("STOP")){
				playPauseButton.setText("Play");
			}
		}
	}
}
