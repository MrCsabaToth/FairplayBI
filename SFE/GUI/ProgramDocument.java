// ProgramDocument.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


/**
 * ProgramDocument is a PlainDocument extended type,
 * with some additional comfortable operations
 */
public class ProgramDocument extends PlainDocument {
	//~ Constructors -----------------------------------------------------------

	/**
	 * Create a new ProgramDocument
	 */
	public ProgramDocument() {
		super();
	}

	/**
	 * Create a new ProgramDocument
	 *
	 * @param doc Initial string to put in document
	 */
	public ProgramDocument(String doc) {
		super();
		setDoc(doc);
	}

	//~ Methods ----------------------------------------------------------------

	/**
	 * Set document data
	 *
	 * @param doc Data to put in document
	 */
	public void setDoc(String doc) {
		try {
			remove(0, getLength());
			insertString(0, doc, null);
		} catch (BadLocationException ex) {
		}
	}

	/**
	 * Get document data
	 *
	 * @return Document in a string
	 */
	public String getDoc() {
		try {
			return getText(0, getLength());
		} catch (BadLocationException ex) {
		}

		return "";
	}

	/**
	 * Add data to the end of the document
	 *
	 * @param doc String to add
	 */
	public void appendDoc(String doc) {
		try {
			insertString(getLength(), doc, null);
		} catch (BadLocationException ex) {
		}
	}

	/**
	 * Get document length
	 *
	 * @return Document length
	 */
	public int length() {
		return getDoc().length();
	}

	/**
	 * Compare documents by data
	 */
	public boolean equals(Object other) {
		if (other instanceof ProgramDocument) {
			return getDoc().equals(((ProgramDocument) other).getDoc());
		}

		return false;
	}

	/**
	 * String representation of document
	 */
	public String toString() {
		return getDoc();
	}
}
