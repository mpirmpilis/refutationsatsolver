package sat.solver;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.DefaultEditorKit;


/**
 * @author Georgios Mpirmpilis (csd3296)
 * <p> This class illustrates the GUI for the desktop app only </p>
 * <br><br><b>View</b> : Creates a new GUI with the ability to either upload a file
 * or directly give the problem in the textbox. Pressing "Solve" button, runs the actual
 * algorithm that solves the problem
 */


public class View {
	private Solver solve;
	
	private JFrame frame;
	private JButton browseButton, solveButton;
	private JFileChooser fileChooser;
	private JLabel description = new JLabel("A SAT Solver for CS180 (Logic) using the Refutation Method");
	private JTextField pathTextbox = new JTextField();
	private JPopupMenu popup = new JPopupMenu();


	// setup GUI and all of it's components
	public View() throws IOException {
		this.solve = new Solver();
		frame = new JFrame("Refutation Systems SAT solver for CS180");
		browseButton = new JButton("Browse");
		solveButton = new JButton("Solve");

		
		fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFocusable(false);
		
		solveButton.setBounds(125,90,87,20);
		solveButton.setBorderPainted(false);
		solveButton.setFocusPainted(false);
		
		browseButton.setBounds(340,60,87,20);
		pathTextbox.setBounds(20,60,300,20);
		
		/* config browse button */
		browseButton.setBorder(BorderFactory.createEmptyBorder());
		browseButton.setBorderPainted(false);
		browseButton.setFocusPainted(false);
		
		description.setBounds(70,-5,300,50);
		ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
		frame.setIconImage(icon.getImage());
		
		

		Action cut = new DefaultEditorKit.CutAction();
        cut.putValue(Action.NAME, "Cut");
        cut.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
        popup.add(cut);

        Action copy = new DefaultEditorKit.CopyAction();
        copy.putValue(Action.NAME, "Copy");
        copy.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
        popup.add(copy);

        Action paste = new DefaultEditorKit.PasteAction();
        paste.putValue(Action.NAME, "Paste");
        paste.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));
        popup.add(paste);

        pathTextbox.setComponentPopupMenu(popup);
        
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setResizable(false);
		frame.setSize(450, 160);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		
		frame.add(pathTextbox);
		frame.add(description);
		frame.add(solveButton);
		frame.add(browseButton);
        JLabel Background = new JLabel(new ImageIcon(getClass().getResource("/resources/bg.jpg")));
        Background.setBounds(0, 0, 477, 155);
        frame.add(Background);
		addBrowserListener();
		addSolveListener();
	}
	
	
	
	public void addBrowserListener() {
		browseButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				// set to open to Desktop for more easiness :)
	            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home")+"/Desktop");
	            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
	            
	            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	            fileChooser.setFileFilter(filter);
	            fileChooser.setAcceptAllFileFilterUsed(false);

	            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
	            	File selectedFile = fileChooser.getSelectedFile();
		            if (selectedFile == null) {
		            	pathTextbox.setText("");
		            } else {
		            	pathTextbox.setText(fileChooser.getSelectedFile().getAbsolutePath());
		            }
	            }
	         } 
		});
	}
	
	
	public ImageIcon getSuccessIcon() {
		return new ImageIcon(getClass().getResource("/resources/success.png"));		
	}
	
	
	
	public void addSolveListener() {
		solveButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
	            try {
					solve.solveProblem(pathTextbox.getText());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	         } 
		});
	}
}
