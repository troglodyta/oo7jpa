package za.co.OO7J;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import za.co.OO7J.utils.GeneralParameters;
import za.co.OO7J.utils.SettingsUtil;

/**
 * @author pvz 19-Apr-2006
 * 
 * @hibernate.class table="document"
 * 
 */
@Entity
@Table(name = "document")
@NamedQueries({
    @NamedQuery(name = "document.selectDocumentWithTitle", query = "select document from Document document where document.title = :title"),
    @NamedQuery(name = "document.selectDocumentWithId", query = "select document from Document document where document.docId = :id")
})
public class Document implements Serializable {

    // hibernate specific:
    private long document_id;
    private String title = null;
    private int docId;
    private String text = null;
    // relationship: documentation
    private CompositePart part = null;

    /**
     *
     * 21-Apr-2006
     *
     * Needed by Hibernate
     */
    public Document() {
    }

    public Document(int newId, CompositePart compositePart) {

        // prepare and fill in the document title

        if (SettingsUtil.debugMode) {
            System.out.println("Document::Document(title = %s)\n" + getTitle());
        }

        setPart(compositePart);
        setDocId(newId);

        // pvz: TODO: Set title and text using a large document.

        // prepare and fill in the document title
        // TODO: NOTE: add more functionality to determine manual size etc.
        // I deviated here!
        setTitle(GeneralParameters.documentText + " " + newId);
        setText(GeneralParameters.documentText);

        if (SettingsUtil.debugMode) {
            System.out.println("Document() -> title = " + title);
        }

        // setTextLength(GeneralParameters.manualText.length());

        // pvz: TODO: add indexes

        // if (documentIndex == NULL_LINK) {
        // Index docIndex("document index");
        //
        // documentIndex = docIndex.getIndex();
        // }
        // documentIndex->add(cpId, *this);

    }

    /**
     * @hibernate.property column="doc_id"
     *
     * @return 30-Apr-2006
     */
    public int getDocId() {
        return docId;
    }

    public void setDocId(int id) {
        this.docId = id;
    }

    /**
     * name="compositePart"
     *
     * @hibernate.one-to-one property-ref="documentation"
     *
     * @return 30-Apr-2006
     *
     */
    @OneToOne
    public CompositePart getPart() {
        return part;
    }

    public void setPart(CompositePart part) {
        this.part = part;
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
     * @hibernate.property column="title" index="title_index"
     *
     * @return 30-Apr-2006
     */
    //@Index(name = "title_index")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * NOT REALLY PART OF THE BASIC MODEL utility method
     *
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
     * original comment:
     *
     * replaceText Method for use in traversals
     *
     * Returns 1= found and 0 = not found
     *
     * @param oldString
     * @param newString
     * @return 24-Apr-2006
     */
    public int replaceText(String oldString, String newString) {

        if (SettingsUtil.debugMode) {
            System.out.println("Manual.changeText/replaceText() -> title = "
                    + getTitle());
        }

        // check to see if the text starts with the old string

        boolean foundMatch = false;

        // if so, change it to start with the new string instead
        foundMatch = (getText().indexOf(oldString) == 0);
        if (foundMatch) {
            setText(getText().replaceAll(oldString, newString));
        }

        if (SettingsUtil.debugMode) {
            if (foundMatch) {
                System.out.println("Manual.changeText/replaceText() -> [changed from "
                        + oldString + " to " + newString);
            } else {
                System.out.println("Manual.changeText/replaceText() -> [no match, so no change was made]");
            }
        }

        if (foundMatch) {
            return 1;
        } else {
            return 0;
        }

    }

    /**
     * @return 19-Apr-2006
     *
     * @hibernate.id generator-class="native" column="document_id"
     */
    @Id
    @Column(name = "document_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public long getDocument_id() {
        return document_id;
    }

    public void setDocument_id(long document_id) {
        this.document_id = document_id;
    }
}
