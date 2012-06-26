// Constant.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;


/**
 * Constant
 *
 * A constant is implemented as a label-value pair.
 *
 * Initialize with:
 * Constant(name, value)
 *
 */
public class Constant {
	//~ Instance fields --------------------------------------------------------

	// Data model
	public ProgramDocument nameDoc; // Name
	public ProgramDocument valueDoc; // Value

	//~ Constructors -----------------------------------------------------------

	public Constant(String name, String value) {
		nameDoc      = new ProgramDocument(name);
		valueDoc     = new ProgramDocument(value);
	}

	//~ Methods ----------------------------------------------------------------

	// Public methods

	/**
	 * Compares two constants by name
	 */
	public boolean equals(Object other) {
		if (other instanceof Constant) {
			return nameDoc.equals(((Constant) other).nameDoc);
		}

		return false;
	}

	/**
	 * Get string representation of constant
	 */
	public String toString() {
		return nameDoc + "=" + valueDoc;
	}
}
;
