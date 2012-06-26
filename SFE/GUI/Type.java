// Type.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;

import javax.swing.DefaultListModel;


/**
 * Type
 *
 * A Type is a new type definition.
 * It may be a primitive type (Int, Boolean) or
 * a synonym for another type, enum, struct or array.
 *
 * A type marked with 'must=true' cannot be deleted.
 *
 * Initialize with:
 * Type(name)
 * Type(name, type)
 * Type(name, type, int-bits, array-elems)
 * Type(name, enum-elems)
 * Type(name, struct-elems)
 *
 */
public class Type {
	//~ Instance fields --------------------------------------------------------

	// Class state variables
	public ProgramDocument  nameDoc; // Name
	public Type             type       = null; // Type
	public int              nBits      = 0; // Number of bits
	public String           nArray     = ""; // Number of elements
	public String           enumVals   = null; // Enum elements
	public DefaultListModel structVars = null; // Struct members
	public boolean          _enum       = false;
	public boolean          struct     = false;
	private boolean         primitive  = false;
	private boolean         must       = false;
	private boolean         immutable  = false;
	private boolean         anonymous  = false;

	//~ Constructors -----------------------------------------------------------

	// Constructors

	/**
	 * Build a primitive type. Type will be immutable and undeletable.
	 *
	 * @param name Type name
	 */
	public Type(String name) {
		nameDoc       = new ProgramDocument(name);
		primitive     = true;
		must          = true;
		immutable     = true;

		if (this.nameDoc.length() == 0) {
			anonymous = true;
		}
	}

	/**
	 * Build a simple type (bool, int)
	 *
	 * @param name Type name
	 * @param type Type original type
	 */
	public Type(String name, Type type) {
		nameDoc       = new ProgramDocument(name);
		this.type     = type;

		if (this.nameDoc.length() == 0) {
			anonymous = true;
		}
	}

	/**
	 * Build an array or an Int<>
	 *
	 * @param name Type name
	 * @param type original type
	 * @param bits number of bits for Int<>
	 * @param array Elements for array
	 */
	public Type(String name, Type type, int bits, String array) {
		nameDoc       = new ProgramDocument(name);
		this.type     = type;
		nBits         = bits;
		nArray        = array;

		if (this.nameDoc.length() == 0) {
			anonymous = true;
		}
	}

	// Enums

	/**
	 * Build an Enumerated type
	 *
	 * @param name Type name
	 * @param vals Enum elements (as long string)
	 */
	public Type(String name, String vals) {
		nameDoc      = new ProgramDocument(name);
		_enum         = true;
		enumVals     = vals;

		if (this.nameDoc.length() == 0) {
			anonymous = true;
		}
	}

	// Structs

	/**
	 * Buile a struct type
	 *
	 * @param name Type name
	 * @param vars List of struct members
	 */
	public Type(String name, DefaultListModel vars) {
		struct         = true;
		nameDoc        = new ProgramDocument(name);
		structVars     = vars; // Vector of Variables

		if (this.nameDoc.length() == 0) {
			anonymous = true;
		}
	}

	//~ Methods ----------------------------------------------------------------

	// Public methods

	/**
	 * Reset type, and set original type
	 *
	 * @param type Original type
	 */
	public void setType(Type type) {
		nBits          = DEFAULT_INT_BITS;
		nArray         = "";
		this.type      = type;
		_enum           = false;
		enumVals       = null;
		struct         = false;
		structVars     = null;
	}

	/**
	 * Set type as Int<>, and set number of bits
	 *
	 * @param bits number of bits
	 */
	public void setBits(int bits) {
		nBits          = bits;
		nArray         = "";
		type           = INT;
		_enum           = false;
		enumVals       = null;
		struct         = false;
		structVars     = null;
	}

	/**
	 * Set type as array
	 *
	 * @param type Original type
	 * @param bits Number of bits if Int<> used
	 * @param array Number of elements for array
	 */
	public void setArray(Type type, int bits, String array) {
		nBits          = bits;
		nArray         = array;
		this.type      = type;
		_enum           = false;
		enumVals       = null;
		struct         = false;
		structVars     = null;
	}

	/**
	 * Set type as Enumerated type
	 *
	 * @param enumData Enum elements
	 */
	public void setEnum(String enumData) {
		nBits          = DEFAULT_INT_BITS;
		nArray         = "";
		type           = null;
		_enum           = true;
		enumVals       = enumData;
		struct         = false;
		structVars     = null;
	}

	/**
	 * Set type as a structure
	 *
	 * @param vars Structure members
	 */
	public void setStruct(DefaultListModel vars) {
		nBits          = DEFAULT_INT_BITS;
		nArray         = "";
		type           = null;
		_enum           = false;
		enumVals       = null;
		struct         = true;
		structVars     = vars;
	}

	/**
	 * Mark type as must (undeletable)
	 *
	 * @param must true or false
	 */
	public void setMust(boolean must) {
		this.must = must;
	}

	/**
	 * Mark type as immutable (read only)
	 *
	 * @param im true or false
	 */
	public void setImmutable(boolean im) {
		this.immutable = im;
	}

	/**
	 * Is type a primitive type
	 */
	public boolean isPrimitive() {
		return primitive;
	}

	/**
	 * Is type a must type (undeletable)
	 */
	public boolean isMust() {
		return must;
	}

	/**
	 * Is type immutable (read only)
	 */
	public boolean isImmutable() {
		return immutable;
	}

	/**
	 * Is type an array
	 */
	public boolean isArray() {
		return (nArray.length() > 0);
	}

	/**
	 * Is type an enumerated type
	 */
	public boolean isEnum() {
		return _enum;
	}

	/**
	 * Is type a structure
	 */
	public boolean isStruct() {
		return struct;
	}

	/**
	 * Get Type name
	 */
	public String getName() {
		if (nameDoc.getDoc().length() == 0) {
			return toString();
		}

		return nameDoc.getDoc();
	}

	/**
	 * Compare types by name
	 */
	public boolean equals(Object other) {
		if (other instanceof Type) {
			return nameDoc.equals(((Type) other).nameDoc);
		}

		return false;
	}

	/**
	 * Get string representation of type
	 */
	public String toString() {
		if (primitive) {
			return nameDoc.getDoc();
		}

		String res = "";

		if (nameDoc.getDoc().length() > 0) {
			res += (nameDoc.getDoc() + "=");
		}

		// enum
		if (_enum) {
			return res + "enum {" + enumVals + "}";
		}
		// struct
		else if (struct) {
			res += "struct {\n\t";

			for (int s = 0; s < structVars.size(); s++) {
				res += structVars.get(s);

				String comm =
					((Variable) structVars.get(s)).commentDoc.getDoc();

				if (comm.length() > 0) {
					res += (" /** " + comm + " */");
				}

				if (s != (structVars.size() - 1)) {
					res += ",\n\t";
				}
			}

			return res + "}";
		} else {
			// array or int
			String baseType = type.nameDoc.getDoc();

			if (baseType.equals(TYPE_INT) && (nBits > 0)) {
				baseType = TYPE_INT + "<" + nBits + ">";
			}

			return res += (baseType +
			       ((nArray.length() > 0) ? ("[" + nArray + "]") : ""));
		}
	}

	//~ Static fields/initializers ---------------------------------------------

	// Constants
	public static final int DEFAULT_INT_BITS = 8;

	// Built-in type names
	public static final String TYPE_INT     = "Int";
	public static final String TYPE_BOOLEAN = "Boolean";

	// Built-in types
	public static final Type INT     = new Type(TYPE_INT);
	public static final Type BOOLEAN = new Type(TYPE_BOOLEAN);
}
;
