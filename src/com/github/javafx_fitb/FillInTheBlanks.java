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

public class FillInTheBlanks extends TextFlow {

	private static final String FXML_LOCATION = "FillInTheBlanks.fxml";
	
	private ArrayList<String> prompts;

	private int maxInputLength = 3;
	private int numBlanks = 0;
	
	private EventType<? extends Event> inputEventType;
	private EventHandler<? extends Event> inputEventFilter;
	
	/**
	 * Class constructor. This component is a <code>TextFlow</code> with 
     * <code>Text</code>s for text and <code>TextField</code>s for blanks.
     *
     * @see    TextFlow
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
        
    /**
     * Returns the maximum number of characters allowed in each of this
     * component's <code>TextField</code>s (i.e. blanks).
     *
     * @return    the character limit for each blank
     * @see com.github.javafx_fitb.FillInTheBlanks#setMaxInputLength(int) setMaxInputLength
     */
	public int getMaxInputLength() {
		return maxInputLength;
	}
	
    /**
     * Sets the maximum number of characters allowed in each of this
     * component's <code>TextField</code>s (i.e. blanks).
     *
     * @param maxInputLength    the new character limit for each blank. 
     *                          Characters exceeding the new limit will 
     *                          not be removed automatically.
     * @see com.github.javafx_fitb.FillInTheBlanks#getMaxInputLength(int) getMaxInputLength
     */
	public void setMaxInputLength(int maxInputLength) {
		this.maxInputLength = maxInputLength;
	}

    /** 
     * Returns the number of <code>TextField</code>s currently in this
     * component. All <code>TextField</code>s, blank or not, are included
     * in this count.
     *
     * @return    the number of <code>TextField</code>s
     */
	public int getNumBlanks() {
		return numBlanks;
	}
	
	/**                           
     * Defines a series of prompts to be used for this component's <code>TextField</code>s.
     * Prompts are applied to the <code>TextField</code>s in the order in which 
     * they are passed into this method. Prompts outnumbering the <code>TextField</code> count
     * are ignored. A given prompt is only visible when its <code>TextField</code> is blank. 
	 * <p>
	 * <pre>
	 * {@code
     * FillInTheBlanks fitb = new FillInTheBlanks();
	 * fitb.setContents("January _, _", _);
     *
     * // contents: "January |Day|, |Year|"
     * // the third prompt, "Season", is not added to the component
	 * fitb.setPrompts("Day", "Year", "Season");
	 * }
	 * </pre>
	 * @param prompts...    a variable number of <code>String</code>s to use as
     *                      prompts for the component 
     * @see FillInTheBlanks#setPrompts(List<String>) setPrompts(List<String>)
	 */
	public void setPrompts(String... prompts) {
		this.prompts = new ArrayList<>(Arrays.asList(prompts));
		applyPrompts();
	}
	
	/**                           
     * Defines and applies a <code>List</code> of prompts to be used for this component's 
     * <code>TextField</code>s. Prompts are applied to the <code>TextField</code>s according 
     * to their order within the <code>List</code>. Prompts outnumbering the <code>TextField</code> count
     * are ignored. A given prompt is only visible when its <code>TextField</code> is blank. 
	 * <p>
	 * <pre>
	 * {@code
     * FillInTheBlanks fitb = new FillInTheBlanks();
	 * fitb.setContents("January _, _", _);
     *
     * ArrayList<String> prompts = new ArrayList<>();
     * prompts.add("Day");
     * prompts.add("Year");
     * prompts.add("Season");
     *
     * // contents: "January |Day|, |Year|"
     * // the third prompt, "Season", is not added to the component
	 * fitb.setPrompts(prompts);
	 * }
	 * </pre>
	 * @param prompts    a <code>List</code> of <code>String</code>s to use as
     *                   prompts for the component 
     * @see com.github.javafx_fitb.FillInTheBlanks#setPrompts(String...) setPrompts(String...)
	 */
	public void setPrompts(List<String> prompts) {
		this.prompts = new ArrayList<>(prompts);
		applyPrompts();
	}
	
	/**
	 * Register an event filter to each of this component's <code>TextField</code>s. The event filter can be used for collecting data and 
	 * restricting or validating input.
	 * 
	 * @param inputEventType      the type of the events to receive by the filter
	 * @param inputEventFilter    the filter to register to this component's <code>TextField</code>s
     * @see com.github.javafx_fitb.FillInTheBlanks#removeInputEventFilter
     * @see javafx.scene.Node#addEventFilter
	 */
	public <T extends InputEvent> void addInputEventFilter(EventType<T> inputEventType, EventHandler<? super T> inputEventFilter) {
		this.inputEventType = inputEventType;
		this.inputEventFilter = inputEventFilter;
		applyInputEventFilter();
	}

	/**
	 * Unregisters an previously registered filter to each of this component's <code>TextField</code>s.
	 * 
	 * @param inputEventType      the type of the events to receive by the filter
	 * @param inputEventFilter    the filter to register to this component's <code>TextField</code>s
     * @see com.github.javafx_fitb.FillInTheBlanks#addInputEventFilter
     * @see javafx.scene.Node#removeEventFilter
	 */
	public <T extends InputEvent> void removeInputEventFilter(EventType<T> inputEventType, EventHandler<? super T> inputEventFilter) {
		for (Node child : getChildren()) {
			if (child instanceof TextField) {
				((TextField) child).removeEventFilter(inputEventType, inputEventFilter);
			}
		}		
	}
	
	/**
     * Sets the text and blanks of this component by adding <code>Text</code>s and <code>TextField</code>s,
     * respectively. Blanks are inserted in between each <code>String</code> in the <code>List</code>, and, optionally, 
     * before the first <code>String</code> and after the last.
     *
	 * @param textPieces          the <code>List</code> of <code>String</code>s to use as this component's text
     * @param addLeadingBlank     add a blank before the first <code>String</code>
     * @param addTrailingBlank    add a blank after the last <code>String</code>
     * @see com.github.javafx_fitb.FillInTheBlanks#setContents(String[], boolean, boolean)
     * @see com.github.javafx_fitb.FillInTheBlanks#setContents(String, String)
	 */
	public void setContents(List<String> textPieces, boolean addLeadingBlank, boolean addTrailingBlank) {
		for (int i = 1; i < textPieces.size(); i += 2) {
			textPieces.add(i, "");
		}
		
		if (addLeadingBlank) {
			textPieces.add(0, "");
		}

		if (addTrailingBlank) {
			textPieces.add("");
		}
		
		updateChildren(textPieces, "");
		
		if (inputEventFilter != null) {
			applyInputEventFilter();
		}
	}

	/**
     * Sets the text and blanks of this component by adding <code>Text</code>s and <code>TextField</code>s,
     * respectively. Blanks are inserted in between each <code>String</code> in the array, and, optionally, 
     * before the first <code>String</code> and after the last.
     *
	 * @param textPieces          the array of <code>String</code>s to use as this component's text
     * @param addLeadingBlank     add a blank before the first <code>String</code>
     * @param addTrailingBlank    add a blank after the last <code>String</code>
     * @see com.github.javafx_fitb.FillInTheBlanks#setContents(List<String>, boolean, boolean)
     * @see com.github.javafx_fitb.FillInTheBlanks#setContents(String, String)
	 */
	public void setContents(String[] textPieces, boolean addLeadingBlank, boolean addTrailingBlank) {
		setContents(new ArrayList<String>(Arrays.asList(textPieces)), addLeadingBlank, addTrailingBlank);
	}

	/**
     * Sets the text and blanks of this component by adding <code>Text</code>s and <code>TextField</code>s,
     * respectively. Each matching instance of <code>blankRegex</code> within <code>text</code> is replaced
     * with a blank, and the remaining subtrings are stored in <code>Text</code> components.
     *
	 * @param text          a <code>String</code> containing the texts and blanks to be added
	 * @param blankRegex    a regular expression used to find blanks within <code>text</code> 
     * @see com.github.javafx_fitb.FillInTheBlanks#setContents(List<String>, boolean, boolean)
     * @see com.github.javafx_fitb.FillInTheBlanks#setContents(String[], boolean, boolean)
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
		
		updateChildren(textPieces, "");
		
		if (inputEventFilter != null) {
			applyInputEventFilter();
		}
	}

	/**
	 * Returns a <code>String</code> with the combined contents of this component's
     * <code>Text</code>s and <code>TextField</code>s.
	 * 
	 * @return    the text contents of this component
	 */
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

    /**
     * Returns a <code>List</code> with the contents of this component's <code>TextField</code>s.
     *
     * @return    a <code>List</code> with this component's <code>TextField</code>s' contents
     */
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
