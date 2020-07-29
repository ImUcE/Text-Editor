
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import javax.swing.*;
import java.awt.BorderLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.Utilities;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;



public class WordPadGUI extends Thread{
	 FileHandler fileHandler;
	 java.util.List<Thread> threads;
	 JFrame frame;
	 HashMap<Object, Action> textPaneActions;
	 JMenuBar menuBar;
	 JMenu styleMenu;
	 JMenu editMenu;
	 UndoManager undoManager;
	 StyledDocument doc;
	 UndoAction undoAction;
	 Action cutAction;
	 JPopupMenu suggestionsPopupMenu;
	 JMenu fileMenu;
	 JMenuItem openItem;
	 JMenuItem saveItem;
	 JMenuItem saveAsItem;
	 JMenuItem newItem;
	 JFontChooser fontChooser;
	 JMenuItem fontItem;
	 JScrollPane scrollPane;
	 JTextPane textPane;
	 JDialog backgroundDialog;
	 JDialog foregroundDialog;
	 JColorChooser bColorChooser;
	 JColorChooser fColorChooser;
	 JMenuItem bColorItem;
	 JMenuItem fColorItem;
	 JSeparator separator_1;
	 JSeparator separator_2;
	 JSeparator separator_4;

	/**
	 * Launch the application.
	 */
	public void run() {
				try {
					this.frame.setVisible(true);	
				} catch (Exception e) {
					e.printStackTrace();
				}
	}

	/**
	 * Create the application.
	 */
	public WordPadGUI(java.util.List<Thread> threads) {
		this.threads = threads;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("deprecation")
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 751, 663);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ImageIcon icon = new ImageIcon(System.getProperty("user.dir")+"\\src\\icon.png");
		frame.setIconImage(icon.getImage());
		
		scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		textPane = new JTextPane();
		scrollPane.setViewportView(textPane);
		textPaneActions = createActionTable(textPane);
		
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		editMenu = new JMenu("Edit");
		menuBar.add(editMenu);
		
		styleMenu = new JMenu("Style");
		menuBar.add(styleMenu);

		cutAction = getActionByName(DefaultEditorKit.cutAction);
		cutAction.putValue(Action.NAME, "Cut");
		editMenu.add(cutAction);
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK), DefaultEditorKit.cutAction);
		Action copyAction= getActionByName(DefaultEditorKit.copyAction);
		copyAction.putValue(Action.NAME, "Copy");
		editMenu.add(copyAction);
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK), DefaultEditorKit.copyAction);
		
		Action pasteAction= getActionByName(DefaultEditorKit.pasteAction);
		pasteAction.putValue(Action.NAME, "Paste");
		editMenu.add(pasteAction);
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK), DefaultEditorKit.pasteAction);
		
		Action boldAction = new StyledEditorKit.BoldAction();
	    boldAction.putValue(Action.NAME, "Bold");
		styleMenu.add(boldAction);
		
		Action italicAction = new StyledEditorKit.ItalicAction();
		italicAction.putValue(Action.NAME, "Italic");
		styleMenu.add(italicAction);
		
		Action underlineAction = new StyledEditorKit.UnderlineAction();
		underlineAction.putValue(Action.NAME, "Underline");
		styleMenu.add(underlineAction);
		
		undoAction = new UndoAction("Undo");
		separator_4 = new JSeparator();
		editMenu.add(separator_4);
		editMenu.add(undoAction);
		
		textPane.getActionMap().put("Undo", undoAction);
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK), "Undo");
		
		undoManager = new UndoManager();
		doc = (StyledDocument) textPane.getDocument();
		doc.addUndoableEditListener(new OurUndoableEditListener());
	
        
		//setting file handling
		fileHandler = new FileHandler(this);
		
		newItem = new JMenuItem("New");
		newItem.addActionListener(fileHandler.createListener());
		fileMenu.add(newItem);
		
		openItem = new JMenuItem("Open");
		openItem.addActionListener(fileHandler.createListener());
		
		separator_2 = new JSeparator();
		fileMenu.add(separator_2);
		fileMenu.add(openItem);
		
		saveItem = new JMenuItem("Save");
		saveItem.addActionListener(fileHandler.createListener());
		fileMenu.add(saveItem);
		
		saveAsItem = new JMenuItem("Save As...");
		saveAsItem.addActionListener(fileHandler.createListener());
		fileMenu.add(saveAsItem);
		saveAsItem.setEnabled(false);
				
		//setting suggestions pop up menu
		suggestionsPopupMenu = new JPopupMenu();
        textPane.addMouseListener(new OurMouseListener());
		
		//setting spell checking
		textPane.getDocument().addDocumentListener(new DocumentListener() {
		      public void insertUpdate(DocumentEvent evt) {
		    	  synchronized(threads.get(1)) {
		    		  threads.get(1).notifyAll();
		    	  }
		      }

		      public void removeUpdate(DocumentEvent evt) {
		    	  synchronized(threads.get(1)) {
		    		  threads.get(1).notifyAll();
		    	  }
		      }

		      public void changedUpdate(DocumentEvent evt) {
		    	  synchronized(threads.get(1)) {
		    		  threads.get(1).notifyAll();
		    	  }
		      }
		    });
		
		//setting font option
		fontChooser = new JFontChooser();
		
		JSeparator separator = new JSeparator();
		styleMenu.add(separator);
		fontItem = new JMenuItem("Choose Font");
		styleMenu.add(fontItem);
		fontItem.addActionListener( new FontListener());
		
		//setting color option
		bColorChooser = new JColorChooser();
		
		separator_1 = new JSeparator();
		styleMenu.add(separator_1);
		bColorItem = new JMenuItem("Choose Background Color");
		styleMenu.add(bColorItem);
		bColorItem.addActionListener( new colorListener());
		
		fColorChooser = new JColorChooser();
		fColorItem = new JMenuItem("Choose Foreground Color");
		styleMenu.add(fColorItem);
		fColorItem.addActionListener( new colorListener());
		
		
		
	}
	
	private class FontListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			   int result = fontChooser.showDialog(frame);
			   if (result == JFontChooser.OK_OPTION)
			   {
			        Font font = fontChooser.getSelectedFont(); 
			        textPane.setFont(font);
			   }
		}
	}
	
	private class colorListener implements ActionListener{
		public void actionPerformed (ActionEvent e) {
			if(e.getSource() == bColorItem) {
				backgroundDialog=JColorChooser.createDialog
						(textPane,
						"Background Color",
						false,
						bColorChooser,
						new ActionListener(){
							public void actionPerformed(ActionEvent e){
							textPane.setBackground(bColorChooser.getColor());
							}
						},
						null);		

				backgroundDialog.setVisible(true);
			}
			else if(e.getSource() == fColorItem){
				foregroundDialog=JColorChooser.createDialog
						(textPane,
						"Foreground Color",
						false,
						fColorChooser,
						new ActionListener(){
							public void actionPerformed(ActionEvent e){
							textPane.setForeground(fColorChooser.getColor());
							textPane.setCaretColor(fColorChooser.getColor());
							}
						},
						null);		

				foregroundDialog.setVisible(true);
			}
			
		}
	}
	
	
	protected class OurUndoableEditListener implements UndoableEditListener {
	    public void undoableEditHappened( UndoableEditEvent e) {
	        undoManager.addEdit(e.getEdit());
	    }
	} 
	
	protected class UndoAction extends AbstractAction{
		private static final long serialVersionUID = 1L;
		public UndoAction(String name) {
			super(name);
		}
		public void actionPerformed(ActionEvent e) {
		    try {
		        undoManager.undo();
		    } catch (CannotUndoException ex) {
		        System.out.println("Unable to undo: " + ex);
		        ex.printStackTrace();
		    }
		}
	}
	
	private HashMap<Object, Action> createActionTable(JTextComponent textComponent) {
        HashMap<Object, Action> actions = new HashMap<Object, Action>();
        Action[] actionsArray = textComponent.getActions();
        for (int i = 0; i < actionsArray.length; i++) {
            Action a = actionsArray[i];
            actions.put(a.getValue(Action.NAME), a);
        }
        return actions;
    }
	
	private Action getActionByName(String name) {
	    return textPaneActions.get(name);
	}
	
	public Document getDoc() {
		return doc;
	}
	
	public String getStrDoc() throws BadLocationException {
		    System.out.println(doc.getLength());
			return  (String)doc.getText(0, doc.getLength());
	}
	
	
	
	private class OurMouseListener extends MouseAdapter	 {
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                int rightClickPosition = textPane.viewToModel2D(e.getPoint());
                textPane.setCaretPosition(rightClickPosition);
                Boolean show = refreshSuggestionsPopupMenu(suggestionsPopupMenu);
                if (show) suggestionsPopupMenu.show(textPane, e.getX(), e.getY());
            }
        }
	}
	
	public void correctWord(String w, AttributeSet atr) throws BadLocationException {
		int caretPosition = textPane.getCaretPosition();
        int start = Utilities.getWordStart(textPane, caretPosition);
        int end = Utilities.getWordEnd(textPane, caretPosition);
        doc.remove(start, end-start);
		doc.insertString(start, w, atr);
	}
	public String getWordAtCaret() {
	      try 
	      {
	          int caretPosition = textPane.getCaretPosition();
	          int start = Utilities.getWordStart(textPane, caretPosition);
	          int end = Utilities.getWordEnd(textPane, caretPosition);
	          return textPane.getText(start, end - start);
	      } catch (BadLocationException e) {
	          System.err.println(e);
	          return null;
	      }
	}
	
	private Boolean refreshSuggestionsPopupMenu(JPopupMenu pm) {
		 pm.removeAll();
		 String word = getWordAtCaret();
		 java.util.List<String> words = SpellCheck.getSuggestions(word);
		 for (String w : words) {
			 JMenuItem item = new JMenuItem(w);
			 pm.add(item);
			 item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						correctWord(w, null);
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
					
				} 
			 });
		 }
		 JMenuItem ignoreThis = new JMenuItem("Ignore for This Document");
		 pm.add(ignoreThis);
		 ignoreThis.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SpellCheck.addWordToThisDictionary(word);
				synchronized(threads.get(1)) {
		    		  threads.get(1).notifyAll();
		    	  }
				
			}
		 });
		 JMenuItem addDic = new JMenuItem("Add to Dictionary");
		 pm.add(addDic);
		 addDic.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SpellCheck.addWordToAllDictionary(word);
					synchronized(threads.get(1)) {
			    		  threads.get(1).notifyAll();
			    	  }
				}
			 });
		 
		 if(words.isEmpty()) return false;
		 else return true;
	}
	
	public JTextPane getTextPane() {
		return textPane;
	}
	
	public JFrame getFrame() {
		return frame;
	}
}

