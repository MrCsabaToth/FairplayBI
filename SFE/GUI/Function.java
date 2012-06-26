// Function.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;

import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


/**
 * Function
 *
 * A Function includes all function information.
 * It contains the following data:
 * name, description, return type, arguments,
 * local variables and body.
 *
 * A Function renders itself using generateCode().
 * A function marked with 'must=true' cannot be deleted.
 *
 * Initialize with:
 * Function(program, name)
 * Function(program, name, return-type, must)
 *
 */
public class Function {
	//~ Instance fields --------------------------------------------------------

	// Class state variables
	private Program program; // The program 

	// Data Model
	public ProgramDocument  nameDoc; // Function name
	public Type             returnType  = null; // Return type
	public Vector           descDoc     = null; // Function description
	public DefaultListModel arguments; // Arguments
	public DefaultListModel variables; // Local variables
	public DefaultListModel body; // Function code
	public ProgramDocument  functionDoc; // Function body document
	boolean                 must        = false; // Is function undeletable

	//~ Constructors -----------------------------------------------------------

	public Function(Program program, String name) {
		this.program     = program;
		this.nameDoc     = new ProgramDocument(name);
		this.descDoc     = new Vector();

		arguments     = new DefaultListModel();
		variables     = new DefaultListModel();
		body          = new DefaultListModel();

		arguments.addListDataListener(new ListListener());
		variables.addListDataListener(new ListListener());
		body.addListDataListener(new ListListener());

		functionDoc = new ProgramDocument();
	}

	/**
	 * Build an undeletable function
	 *
	 * @param program The program
	 * @param name Function name
	 * @param retType return type
	 * @param must is function undeletable
	 */
	public Function(Program program, String name, Type retType, boolean must) {
		this.program     = program;
		this.nameDoc     = new ProgramDocument(name);
		this.descDoc     = new Vector();
		this.must        = must;
		returnType       = retType;
		arguments        = new DefaultListModel();
		variables        = new DefaultListModel();
		body             = new DefaultListModel();

		arguments.addListDataListener(new ListListener());
		variables.addListDataListener(new ListListener());
		body.addListDataListener(new ListListener());

		functionDoc = new ProgramDocument();
	}

	//~ Methods ----------------------------------------------------------------

	// Public methods

	/**
	 * Update function body document
	 */
	public void updateDoc() {
		functionDoc.setDoc(generateCode());
	}

	/**
	 * Is function undeletable
	 */
	public boolean isMust() {
		return must;
	}

	/**
	 * Compares function by name
	 */
	public boolean equals(Object other) {
		if (other instanceof Function) {
			return nameDoc.equals(((Function) other).nameDoc);
		}

		return false;
	}

	/**
	 * String representation of the function
	 */
	public String toString() {
		String res = returnType.getName() + " " + nameDoc.getDoc() + "(";

		if (arguments != null) {
			for (int i = 0; i < arguments.size(); i++) {
				res += arguments.get(i);

				if (i != (arguments.size() - 1)) {
					res += ", ";
				}
			}
		}

		res += ")";

		return res;
	}

	/**
	 * Get the function description
	 *
	 * @return String containing the description
	 */
	public String getDescription() {
		String desc = "";

		for (int i = 0; i < descDoc.size(); i++) {
			desc += descDoc.get(i);
		}

		return desc;
	}

	/**
	 * Generate and return the function code
	 *
	 * @return The function code as a long string
	 */
	public String generateCode() {
		String code = "";

		String ident = "    ";

		// Function description (commented)
		for (int i = 0; i < descDoc.size(); i++) {
			code += ("// " + descDoc.get(i));
		}

		code += ("function " + toString() + "{\n\n");

		// Variables
		for (int i = 0; i < variables.size(); i++) {
			code += (ident + "var " + variables.get(i) + ";");

			String comm = ((Variable) variables.get(i)).commentDoc.getDoc();

			if (comm.length() > 0) {
				code += (" /** " + comm + " */");
			}

			code += "\n";
		}

		code += "\n";

		// Body
		for (int i = 0; i < body.size(); i++)
			code += (ident + body.get(i) + "\n");

		code += "\n";

		code += "}\n";

		return code;
	}

	//~ Inner Classes ----------------------------------------------------------

	// Listeners
	// ListListener for all containers
	class ListListener implements ListDataListener {
		public void contentsChanged(ListDataEvent e) {
			program.updateDoc();
		}

		public void intervalAdded(ListDataEvent e) {
			program.updateDoc();
		}

		public void intervalRemoved(ListDataEvent e) {
			program.updateDoc();
		}
	}
}
;
