import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.Position;
import javax.swing.text.View;
import java.nio.charset.StandardCharsets;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpellCheck extends Thread {
	private static List<String> dic;
	private static String specialChars;
	private JTextPane pane;
	static Path dicPath;
	
	private static Highlighter highlighter;
	private Highlighter.HighlightPainter painter;
	
	SpellCheck(JTextPane pane){
		this.pane = pane;
		highlighter = new UnderlineHighlighter(null);
		this.painter = new UnderlineHighlighter.UnderlineHighlightPainter(Color.red);
		pane.setHighlighter(highlighter);
		dicPath = Paths.get(System.getProperty("user.dir") + "\\files\\dictionary.txt");
		
		dic = Collections.emptyList();
		try {
			dic = Files.readAllLines(dicPath, StandardCharsets.UTF_8);
			
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "cannot open dictionary", "Error", JOptionPane.INFORMATION_MESSAGE);
		}
	
		specialChars = ".,'\";:\\|][}{+=-_)(*&^%$#@!`~/ \n";
		
	}
	
	public void run() {
		
		synchronized(this) {
			while(true) {
				try {
					startJob();
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Thread.yield();
			}
		}
	}
	
	public static List<String> getSuggestions(String word) {
		
		List<String> suggestedWords = new ArrayList<String>();
		if (!dic.contains(word))
			suggestedWords.add(findSuggestedWord(dic, word));
		return suggestedWords;
	}
	
	public static String findSuggestedWord(List<String> words, String word) {
		int i ;
		int heuristic=0;
		String return_string="";
		for(i=0;i<words.size();i++) {
			if(word.equals(words.get(i)))
				return words.get(i);
			
		}
		
		
		int j;
		for(i=0;i<words.size();i++) {
			int new_heuristic=0;
			int len;
			if(words.get(i).length()<word.length()) {
				len=words.get(i).length();
			}
			else {
				len=word.length();
			}
			for(j=0;j<len;j++) {
				if(words.get(i).charAt(j)==word.charAt(j)) {
					new_heuristic++;
				}
			}
			if(new_heuristic>heuristic) {
				heuristic=new_heuristic;
				return_string=words.get(i);
			
			}
			
		}
		return return_string;
	}
	
	private void startJob() throws BadLocationException {
		deletePreviosHighlights();
		
	    Document doc = pane.getDocument();
	    String text = doc.getText(0, doc.getLength()).toLowerCase();
	    
	    
	    int startIndex = 0;
	    int endIndex = 0;
	    boolean isLastSpecial = true;
	    String word;
	    
	    
	    for (int i = 0; i < text.length(); i++){
	        char c = text.charAt(i);        
	    
	    	if (specialChars.indexOf(c) != -1) {
	    		if (!isLastSpecial) {
	    			word = text.substring(startIndex, endIndex);
		    		if ( !dic.contains(word) )
			    		highlighter.addHighlight(startIndex, endIndex , painter);
	    		}
	    		startIndex = endIndex + 1 ;
	    		endIndex = startIndex;
	    		isLastSpecial = true;
	    	}
	    	else {
	    		endIndex += 1;
	    		isLastSpecial = false;
	    	}
	    
	    }
	    
	}
	
	private void deletePreviosHighlights() {
		Highlighter.Highlight[] highlights = highlighter.getHighlights();
	    for (int i = 0; i < highlights.length; i++) {
	      Highlighter.Highlight h = highlights[i];
	      if (h.getPainter() instanceof UnderlineHighlighter.UnderlineHighlightPainter) {
	        highlighter.removeHighlight(h);
	      }
	    }
	}

	public static void addWordToAllDictionary(String word) {
		dic.add(word);
		try {
		    Files.write(dicPath, ("\n"+ word).getBytes(), StandardOpenOption.APPEND);
		}catch (IOException e) {
		    
		}	
	}

	public static void addWordToThisDictionary(String word) {
		dic.add(word);
	
	}
}

class UnderlineHighlighter extends DefaultHighlighter {
	  public UnderlineHighlighter(Color c) {
	    painter = (c == null ? sharedPainter : new UnderlineHighlightPainter(c));
	  }

	  // Convenience method to add a highlight with
	  // the default painter.
	  public Object addHighlight(int p0, int p1) throws BadLocationException {
	    return addHighlight(p0, p1, painter);
	  }

	  public void setDrawsLayeredHighlights(boolean newValue) {
	    // Illegal if false - we only support layered highlights
	    if (newValue == false) {
	      throw new IllegalArgumentException(
	          "UnderlineHighlighter only draws layered highlights");
	    }
	    super.setDrawsLayeredHighlights(true);
	  }

	  // Painter for underlined highlights
	  public static class UnderlineHighlightPainter extends
	      LayeredHighlighter.LayerPainter {
	    public UnderlineHighlightPainter(Color c) {
	      color = c;
	    }
	    
	    public void paint(Graphics g, int offs0, int offs1, Shape bounds,
	            JTextComponent c) {
	          // Do nothing: this method will never be called
	        }

	    public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds,
	        JTextComponent c, View view) {
	      g.setColor(color == null ? c.getSelectionColor() : color);

	      Rectangle alloc = null;
	      if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
	        if (bounds instanceof Rectangle) {
	          alloc = (Rectangle) bounds;
	        } else {
	          alloc = bounds.getBounds();
	        }
	      } else {
	        try {
	          Shape shape = view.modelToView(offs0,
	              Position.Bias.Forward, offs1,
	              Position.Bias.Backward, bounds);
	          alloc = (shape instanceof Rectangle) ? (Rectangle) shape
	              : shape.getBounds();
	        } catch (BadLocationException e) {
	          return null;
	        }
	      }

	      FontMetrics fm = c.getFontMetrics(c.getFont());
	      int baseline = alloc.y + alloc.height - fm.getDescent() + 1;
	      g.drawLine(alloc.x, baseline, alloc.x + alloc.width, baseline);
	      g.drawLine(alloc.x, baseline + 1, alloc.x + alloc.width,
	          baseline + 1);

	      return alloc;
	    }

	    protected Color color; // The color for the underline
	  }

	  // Shared painter used for default highlighting
	  protected static final Highlighter.HighlightPainter sharedPainter = new UnderlineHighlightPainter(
	      null);

	  // Painter used for this highlighter
	  protected Highlighter.HighlightPainter painter;
	}
