package za.co.OO7J;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import za.co.OO7J.utils.GeneralParameters;
import za.co.OO7J.utils.SettingsUtil;

/**
 * @author pvz 19-Apr-2006
 * 
 * @hibernate.class table="manual"
 */
@Entity
@Table(name = "manual")

public class Manual implements Serializable {
	// hibernate specific
	private long manual_id;

	private String title = null;

	private int id;

	private String text = null;

	private int textLength;

	// relationship: man
	private Module module;

	/**
	 * 
	 * 21-Apr-2006
	 * 
	 * Needed by Hibernate
	 */
	public Manual() {

	}

	public Manual(int newId, Module module) {
		setId(newId);
		setModule(module);
		// title(TitleSize), text(ManualSize)

		// prepare and fill in the document title
		// TODO: NOTE: add more functionality todetermine manual size etc.
		// I deviated here!
		setTitle(GeneralParameters.manualText + " " + newId);
		setText(GeneralParameters.manualText);
		setTextLength(GeneralParameters.manualText.length());

		if (SettingsUtil.debugMode) {
			System.out.println("Manual() -> title = " + title);
		}

	}// end constructor Manual

	/**
	 * 
	 * Used in traversals
	 * 
	 * @param charToFind
	 * @return 24-Apr-2006
	 */
	public int searchText(String charToFind) {
		if (SettingsUtil.debugMode) {
			System.out.println("Manual.searchText() title = " + getTitle());
		}

		// count occurrences of the indicated letter (for busy work)
		int count = 0;

		StringBuffer bf = new StringBuffer(getText());
		// TODO: GET NUMBER OF OCCURENCES: check for correctness!!!!!!pvz
		Pattern pattern = Pattern.compile(charToFind);
		Matcher matcher = pattern.matcher(getText());
		while (matcher.find()) {
			count++;
		}

		if (SettingsUtil.debugMode) {
			System.out.println("Manual.searchText() [found " + count + " "
					+ charToFind + " among %d characters]");
		}
		return count;

	}

	/**
	 * 
	 * 24-Apr-2006
	 */
	public int firstLast() {
		int length = getText().length();

		if (getText().charAt(0) == getText().charAt(length - 1)) {
			return 1;
		}
		return 0;
	}

	/**
	 * 
	 * original comment:
	 * 
	 * replaceText Method for use in traversals
	 * 
	 * @param oldString
	 * @param newString
	 * @return 24-Apr-2006
	 */
	public boolean replaceText(String oldString, String newString) {

		if (SettingsUtil.debugMode) {
			System.out.println("Manual.changeText/replaceText() -> title = "
					+ getTitle());
		}

		// check to see if the text starts with the old string

		boolean foundMatch = false;

		// if so, change it to start with the new string instead
		foundMatch = (getText().indexOf(oldString) == 0);
		if (foundMatch) {
			getText().replaceAll(oldString, newString);
		}

		if (SettingsUtil.debugMode) {
			if (foundMatch) {
				System.out
						.println("Manual.changeText/replaceText() -> [changed from "
								+ oldString + " to " + newString);
			} else {
				System.out
						.println("Manual.changeText/replaceText() -> [no match, so no change was made]");
			}
		}

		return foundMatch;
	}

	/**
	 * @hibernate.property column="id"
	 * 
	 * @return 30-Apr-2006
	 * 
	 */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return 19-Apr-2006 name="man"
	 * 
	 * @hibernate.one-to-one property-ref="manual"
	 */

	@OneToOne(fetch=FetchType.LAZY)
	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	/**
	 * @hibernate.property column="text"
	 * 
	 * @return 30-Apr-2006
	 */
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @hibernate.property column="text_length"
	 * 
	 * @return 30-Apr-2006
	 */
	public int getTextLength() {
		return textLength;
	}

	public void setTextLength(int textLength) {
		this.textLength = textLength;
	}

	/**
	 * 
	 * @hibernate.property column="title"
	 * 
	 * @return 30-Apr-2006
	 */
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/*
	 * 
	 * HIBERNATE SPECIFIC
	 * 
	 */

	/**
	 * @return 19-Apr-2006
	 * 
	 * @hibernate.id generator-class="native" column="manual_id"
	 */
	@Id
	@Column(name = "manual_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public long getManual_id() {
		return manual_id;
	}

	public void setManual_id(long manual_id) {
		this.manual_id = manual_id;
	}

}
