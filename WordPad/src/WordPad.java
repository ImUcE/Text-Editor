import java.util.ArrayList;
import java.util.List;

public class WordPad {

	public static void main(String[] args) {
		List<Thread> threads = new ArrayList<Thread>();
		
		Thread threadGUI = new WordPadGUI(threads);
		threads.add(threadGUI);
		
		
		Thread threadSpellCheck = new SpellCheck(((WordPadGUI) threadGUI).getTextPane());
		threads.add(threadSpellCheck);
		threadSpellCheck.setDaemon(true);
		
		Thread threadAutoSave = new AutoSave(0.1, (WordPadGUI) threadGUI);
		threads.add(threadAutoSave);
		threadAutoSave.setDaemon(true);
		
		threadGUI.start();
		threadSpellCheck.start();
		threadAutoSave.start();
	}

}
