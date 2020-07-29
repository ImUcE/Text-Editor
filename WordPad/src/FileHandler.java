import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

public class FileHandler {
	WordPadGUI gui;
	final  JFileChooser fc;
	File file;
	String fileName;
	Boolean isSaved;
	Boolean firstSave;
	
	FileHandler(WordPadGUI gui){
		this.gui = gui;
		fc = new JFileChooser();
		isSaved = false;
		firstSave = false;
		fileName = "Untitled";
		gui.frame.setTitle("WordPad - " + fileName );
		file = null;
		
	}
	
	class Listener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			//for opening file
			if(e.getSource() == gui.openItem) {
				fc.setDialogTitle("Save");
				beforeDiscard();
				fc.setDialogTitle("Open");
				open();
			}
			else if (e.getSource() == gui.saveItem ) {
				fc.setDialogTitle("Save");
				if(file == null) saveAs();
				else save();
				
				if(!firstSave) {
					firstSave = true;
					gui.saveAsItem.setEnabled(true);
				}
				
			}
			else if (e.getSource() == gui.saveAsItem ) {
				fc.setDialogTitle("Save");
				beforeDiscard();
				fc.setDialogTitle("Save As...");
				saveAs();
			}
			else if (e.getSource() == gui.newItem ) {
				fc.setDialogTitle("Save");
				beforeDiscard();
				newFile();
			}
		        
		}
	}
	
	public Listener createListener() {
		return new Listener();
	}
	
	void fileChanged() {
		isSaved = false;
	}
	
	void beforeDiscard() {
		if (!isSaved) {
			int respond = JOptionPane.showConfirmDialog(gui.frame,
                    "<html>"+fileName+" has changed.<br>Do you want to save it?<html>",
                    "Save",
                    JOptionPane.YES_NO_OPTION);
			if(respond == JOptionPane.YES_OPTION) {
				if(file == null) saveAs();
				else save();
			}
		}
	}
	void readFile() throws BadLocationException {
		try {
			  gui.textPane.setText("");
		      Scanner sc = new Scanner(file);
		      while (sc.hasNextLine()) {
		        String line = sc.nextLine();
		        gui.doc.insertString(gui.doc.getLength(), line + "\n", null);
		      }
		      sc.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}
	void open() {
		int returnVal = fc.showOpenDialog(gui.getFrame());
		if (returnVal != JFileChooser.APPROVE_OPTION) return;
		file = fc.getSelectedFile();
        try {
			readFile();
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        fileName = file.getName();
        gui.frame.setTitle("WordPad - "+fileName );
	}
	
	Boolean saveAs() {
		File temp;
		do
		{
			int returnVal = fc.showSaveDialog(gui.frame);
	        if (returnVal != JFileChooser.APPROVE_OPTION) return false;
		    temp = fc.getSelectedFile();
		    if(!temp.exists()) break;
		    int respond = JOptionPane.showConfirmDialog(gui.frame,
                    "<html>"+temp.getPath()+" already exists.<br>Do you want to replace it?<html>",
                    "Save As",
                    JOptionPane.YES_NO_OPTION);
		    if(respond == JOptionPane.YES_OPTION) break;
		}while(true);
		
        file = temp;
        fileName = file.getName();
        gui.frame.setTitle("WordPad - "+fileName );
        return save(); 
       }
	
	Boolean save() {
		try {
        	BufferedWriter outFile = new BufferedWriter(new FileWriter(file));
			gui.textPane.write(outFile);
			outFile.close();
			isSaved = true;
			return true;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
        
	}
	
	void newFile() {
		file = null;
		fileName = "Untitled";
        gui.frame.setTitle("WordPad - " + fileName );
		gui.textPane.setText("");
	}

}
