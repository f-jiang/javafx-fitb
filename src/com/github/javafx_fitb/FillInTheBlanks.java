package com.github.javafx_fitb;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * @author Feilan Jiang
 * 
 */
public class FillInTheBlanks extends TextFlow {

	private static final String FXML_LOCATION = "FillInTheBlanks.fxml";
	
	private ArrayList<String> prompts;

	private int maxInputLength = 3;
	private int numBlanks = 0;
	
	private EventType<? extends Event> inputEventType;
	private EventHandler<? extends Event> inputEventFilter;
	
	/**
	 * Initializes a new <code>FillInTheBlanks</code>. This object consists of a text string with <code>TextField</code>s
	 * inserted in place of the blanks.
	 */
	public FillInTheBlanks() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_LOCATION));
		
		loader.setRoot(this);
		loader.setController(this);
		
        try {
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
	}
        
	public int getMaxInputLength() {
		return maxInputLength;
	}
	
	public void setMaxInputLength(int maxInputLength) {
		this.maxInputLength = maxInputLength;
	}

	public int getNumBlanks() {
		return numBlanks;
	}
	
	/**
	 * Specifies a text prompt to be displayed in this object's <code>TextField</code>s. 
	 * The prompts are applied in the order in which they are provided.
	 * <p>
	 * <pre>
	 * {@code
	 * fillInTheBlanks.update("January _, _", _);	// I now have two TextFields, one for each blank
	 * fillInTheBlanks.setPrompts("Day", "Month");	// the first blank's prompt is "Day", the second blank's prompt is "Month" 
	 * }
	 * </pre>
	 * @param prompts	a series or array of prompts to use
	 */
	public void setPrompts(String... prompts) {
		this.prompts = new ArrayList<>(Arrays.asList(prompts));
		applyPrompts();
	}
	
	public void setPrompts(List<String> prompts) {
		this.prompts = new ArrayList<>(prompts);
		applyPrompts();
	}
	
	/**
	 * 
	 * Set an event filter to process <code>TextField</code> input. The event filter can be used for collecting data and 
	 * restricting/validating input. The event filter is added to every <code>TextField</code> in this <code>FillInTheBlanks</code>.
	 * 
	 * @param inputEventFilter	the event filter to use for this <code>FillInTheBlanks</code>'s <code>TextField</code>s
	 */
	public <T extends InputEvent> void addInputEventFilter(EventType<T> inputEventType, EventHandler<? super T> inputEventFilter) {
		this.inputEventType = inputEventType;
		this.inputEventFilter = inputEventFilter;
		applyInputEventFilter();
	}

	public <T extends InputEvent> void removeInputEventFilter(EventType<T> inputEventType, EventHandler<? super T> inputEventFilter) {
		for (Node child : getChildren()) {
			if (child instanceof TextField) {
				((TextField) child).removeEventFilter(inputEventType, inputEventFilter);
			}
		}		
	}
	
	/**
	 * Updates the text of this <code>FillInTheBlanks</code>. A special symbol, specified by <code>blankRegex</code>, can be used
	 * to indicate the locations of blanks.
	 * <p>
	 * For this version of <code>update()</code>, the text must be a <code>List</code> of <code>String</code>s. Blank-symbol detection is done on a per-piece basis.
	 * For instance, using a <code>blankRegex</code> of <code>"_"</code> on the piece <code>"_*x + "</code> would not yield any blanks.
	 * @param textPieces	the <code>List</code> of <code>String</code>s to which this <code>FillInTheBlanks</code>'s text will be set
	 * @param blankRegex	the regular expression (symbol) to use for finding blanks
	 */
	// TODO instead of textPieces, have (List<String> textPieces, boolean addLeadingBlank, boolean addTrailingBlank)
	public void setContents(List<String> textPieces, String blankRegex) {
		updateChildren(textPieces, blankRegex);
		
		if (inputEventFilter != null) {
			applyInputEventFilter();
		}
	}

	/**
	 * Updates the text of this <code>FillInTheBlanks</code>. A special symbol, specified by <code>blankRegex</code>, can be used
	 * to indicate the locations of blanks.
	 * <p>
	 * For this version of <code>update()</code>, the text must be an array of <code>String</code>s. Blank-symbol detection is done on a per-piece basis.
	 * For instance, using a <code>blankRegex</code> of <code>"_"</code> on the piece <code>"_*x + "</code> would not yield any blanks.
	 * @param textPieces	the array of <code>String</code>s to which this <code>FillInTheBlanks</code>'s text will be set
	 * @param blankRegex	the regular expression (symbol) to use for finding blanks
	 */
	// for a text piece to be considered blank the blankRegex must be the only thing in it
	public void setContents(String[] textPieces, String blankRegex) {
		setContents(new ArrayList<String>(Arrays.asList(textPieces)), blankRegex);
	}

	/**
	 * Updates the text of this <code>FillInTheBlanks</code>. A special symbol, specified by <code>blankRegex</code>, can be used
	 * to indicate the locations of blanks.
	 * @param text			the <code>String</code> to which this <code>FillInTheBlanks</code>'s text will be set
	 * @param blankRegex	the regular expression (symbol) to use for finding blanks
	 */
	public void setContents(String text, String blankRegex) {
		// we use this version of split so there can be blank elems at the end
		ArrayList<String> textPieces = new ArrayList<>(Arrays.asList(text.split(blankRegex, -1)));
		
		for (int i = 0; i < textPieces.size() - 1; i++) {	
			if (textPieces.get(i).matches("")) {
				continue;
			} else if (!textPieces.get(i + 1).matches("")) {
				textPieces.add(++i, "");
			}
		}		
		
		setContents(textPieces, "");
	}

	/**
	 * Returns a <code>String</code> containing the text of this <code>FillInTheBlanks</code>. 
	 * Anything inside the blanks' <code>TextField</code>s is also included in this <code>String</code>. 
	 * 
	 * @return	the text of this <code>FillInTheBlanks</code>
	 */
	// TODO @override getText() if possible
	public String getText() {
		StringBuilder sb = new StringBuilder();

		for (Node child : getChildren()) {
			if (child instanceof TextField) {
				sb.append(((TextField) child).getText());
			} else if (child instanceof Text) {
				sb.append(((Text) child).getText());
			}
		}

		return sb.toString();
	}

	public List<String> getInputTexts() {
		ArrayList<String> blanks = new ArrayList<>();
		
		for (Node child : getChildren()) {
			if (child instanceof TextField) {
				blanks.add(((TextField) child).getText());
			}
		}

		return blanks;		
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Event> void applyInputEventFilter() {
		for (Node child : getChildren()) {
			if (child instanceof TextField) {
				((TextField) child).addEventFilter((EventType<T>) inputEventType, (EventHandler<? super T>) inputEventFilter);
			}
		}				
	}
	
	private void applyPrompts() {
		if (prompts != null) {
			int i = 0;
			int l = prompts.size();
			
			for (Node child : getChildren()) {
				if (i >= l) {
					break;
				}
				
				if (child instanceof TextField) {
					((TextField) child).setPromptText(prompts.get(i++)); 
				}
			}
		}
	}
	
	private void updateChildren(List<String> textPieces, String blankRegex) {
		TextField blank;
		Text text;
		ObservableList<Node> children = getChildren();
		
		children.clear();
		numBlanks = 0;
		
		for (String piece : textPieces) {
			if (piece.matches(blankRegex)) {	// this is a blank
				blank = new TextField();
				blank.setPrefWidth(50);
				children.add(blank);
				numBlanks++;
			} else {
				text = new Text(piece);
				children.add(text);
			}
		}
		
		applyPrompts();		
	}
}
