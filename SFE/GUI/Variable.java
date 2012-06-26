// Variable.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

/**
 * Variable
 *
 * A Variable is a nametag given to a type, or to an
 * array of a type
 *
 * Initialize with:
 * Variable(name, type)
 * Variable(name, type, nArray)
 */
package SFE.GUI;

public class Variable {
	//~ Instance fields --------------------------------------------------------

	// Data Model
	public ProgramDocument nameDoc; // Variable name
	public Type            type; // Variable type
	public ProgramDocument commentDoc; // Variable Comment

	// Class vars
	public int     nBits  = 0; // Number of bits for Int<>
	public String  nArray = ""; // Number of array elements
	public boolean must   = false; // Is variable undeletable

	//~ Constructors -----------------------------------------------------------

	public Variable(String name, Type type) {
		nameDoc        = new ProgramDocument(name);
		commentDoc     = new ProgramDocument("");
		this.type      = type;
	}

	/**
	 * Build an array variable
	 *
	 * @param name Variable name
	 * @param type Variable type
	 * @param array Array elements
	 * @param bits Number of bits for Ints
	 */
	public Variable(String name, Type type, String array, int bits) {
		nameDoc        = new ProgramDocument(name);
		commentDoc     = new ProgramDocument("");
		this.type      = type;
		nArray         = array;
		nBits          = bits;
	}

	//~ Methods ----------------------------------------------------------------

	// Public methods

	/**
	 * Compare variables by name
	 */
	public boolean equals(Object other) {
		if (other instanceof Variable) {
			return nameDoc.equals(((Variable) other).nameDoc);
		}

		return false;
	}

	/**
	 * Get the string representation of the variable
	 */
	public String toString() {
		String str = type.getName();

		if (type == Type.INT) {
			str += ("<" + nBits + ">");
		}

		if (nArray.length() > 0) {
			str += ("[" + nArray + "]");
		}

		str += (" " + nameDoc);

		return str;
	}
}
;
