// Progra.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;

import javax.swing.DefaultListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


/**
 * Program
 *
 * A Program includes all program data. It is composed of:
 * name, description, constants, types, functions.
 *
 * All programs include primitive types, must types (input, output, etc.)
 * and must functions (output).
 *
 * A program renders itself by the generateCode() method.
 *
 * Initialize with:
 * Program(name, description)
 *
 */
public class Program {
	//~ Instance fields --------------------------------------------------------

	// Data Model
	public ProgramDocument  programName; // Name
	public ProgramDocument  programDesc; // Description
	public ProgramDocument  programDoc; // Code
	public DefaultListModel types; // Typedefs
	public DefaultListModel constants; // Constants
	public DefaultListModel functions; // Functions
	private CodeWndPanel    codePanel      = null;
	public String           outputFilename;
	public Object           globalLock;

	//~ Constructors -----------------------------------------------------------

	// Constructors

	/**
	 * Create a new Program
	 *
	 * @param name Program name
	 * @param description Program description (commented)
	 */
	public Program(String name, String description) {
		init(name, description);
	}

	//~ Methods ----------------------------------------------------------------

	// Public Methods

	/**
	 * Initialize program.
	 * Here program default types and functions are declared.
	 * Used also to cleanup existing program.
	 *
	 * @param name Program name
	 * @param description Program description (commented)
	 */
	public void init(String name, String description) {
		initModel();

		programName.setDoc(name);
		programDesc.setDoc(description);

		// Add primitive and must types
		types.addElement(Type.INT);
		types.addElement(Type.BOOLEAN);

		// Input and output types
		Type aliceInput =
			new Type("AliceInput", Type.INT, Type.DEFAULT_INT_BITS, "");
		Type aliceOutput =
			new Type("AliceOutput", Type.INT, Type.DEFAULT_INT_BITS, "");
		Type bobInput =
			new Type("BobInput", Type.INT, Type.DEFAULT_INT_BITS, "");
		Type bobOutput =
			new Type("BobOutput", Type.INT, Type.DEFAULT_INT_BITS, "");

		aliceInput.setMust(true);
		aliceOutput.setMust(true);
		bobInput.setMust(true);
		bobOutput.setMust(true);

		DefaultListModel invars  = new DefaultListModel();
		DefaultListModel outvars = new DefaultListModel();

		Variable         alicein  = new Variable("alice", aliceInput);
		Variable         bobin    = new Variable("bob", bobInput);
		Variable         aliceout = new Variable("alice", aliceOutput);
		Variable         bobout   = new Variable("bob", bobOutput);

		alicein.must      = true;
		bobin.must        = true;
		aliceout.must     = true;
		bobout.must       = true;

		invars.addElement(alicein);
		invars.addElement(bobin);
		outvars.addElement(aliceout);
		outvars.addElement(bobout);

		Type inputType  = new Type("Input", invars);
		Type outputType = new Type("Output", outvars);

		inputType.setMust(true);
		outputType.setMust(true);
		inputType.setImmutable(true);
		outputType.setImmutable(true);

		types.addElement(aliceInput);
		types.addElement(aliceOutput);
		types.addElement(bobInput);
		types.addElement(bobOutput);
		types.addElement(inputType);
		types.addElement(outputType);

		// Add output function
		Function outputFunc = new Function(this, "output", outputType, true);
		Variable outvar = new Variable("input", inputType);
		outvar.must = true;
		outputFunc.arguments.addElement(outvar);
		outputFunc.descDoc.add("This is the main function\n");
		outputFunc.updateDoc();

		functions.addElement(outputFunc);

		outputFunc.updateDoc();
		updateDoc();
	}

	/**
	 * Generate and get the whole program code
	 *
	 * @return The program code as a string
	 */
	public String generateCode() {
		String code = "";

		// Program
		code += (programDesc + "\n");
		code += ("program " + programName.getDoc() + " {\n\n");

		// Constants
		code += "// Constants\n";

		for (int i = 0; i < constants.size(); i++)
			code += (constants.get(i) + ";\n");

		code += "\n";

		// Types
		code += "// Type Definitions\n";

		for (int i = 2; i < types.size(); i++)
			code += ("type " + types.get(i) + ";\n");

		code += "\n";

		// Functions
		code += "// Function Definitions\n\n";

		for (int i = 0; i < functions.size(); i++) {
			code += ((Function) functions.get(i)).generateCode();
			code += "\n";
		}

		code += "}\n";

		return code;
	}

	/**
	 * Get the string representation
	 */
	public String toString() {
		return "program " + programName.getDoc() + ": " + programDesc.getDoc();
	}

	// Private Methods

	/**
	 * Initialize data model
	 */
	private void initModel() {
		programDoc     = new ProgramDocument();

		programName     = new ProgramDocument();
		programDesc     = new ProgramDocument();

		types         = new DefaultListModel();
		constants     = new DefaultListModel();
		functions     = new DefaultListModel();

		programDoc.addDocumentListener(new DocListener());
		programName.addDocumentListener(new DocListener());
		programDesc.addDocumentListener(new DocListener());

		types.addListDataListener(new ListListener());
		constants.addListDataListener(new ListListener());
		functions.addListDataListener(new ListListener());
	}

	/**
	 * Update code panel where this program will be shown
	 */
	public void setCodePanel(CodeWndPanel c) {
		codePanel = c;
	}

	/**
	 * Update the program Document object with
	 * the program code
	 * Also, if shown in a window, take care of restoring
	 * the position
	 */
	public void updateDoc() {
		// save scrollbar location
		int pos = 0;

		if (codePanel != null) {
			pos = codePanel.progText.getCaretPosition();
		}

		programDoc.setDoc(generateCode());

		// set scrollbar location
		if (codePanel != null) {
			codePanel.progText.setCaretPosition(pos);
		}
	}

	//~ Inner Classes ----------------------------------------------------------

	// Listeners
	// Document listener only for program name 
	class DocListener implements DocumentListener {
		public void changedUpdate(DocumentEvent e) {
		}

		public void insertUpdate(DocumentEvent e) {
			try {
				ProgramDocument d = (ProgramDocument) e.getDocument();

				if ((d == programName) || (d == programDesc)) {
					updateDoc();
				}
			} catch (ClassCastException ex) {
			}
		}

		public void removeUpdate(DocumentEvent e) {
			try {
				ProgramDocument d = (ProgramDocument) e.getDocument();

				if ((d == programName) || (d == programDesc)) {
					updateDoc();
				}
			} catch (ClassCastException ex) {
			}
		}
	}

	// List listener for all containers
	class ListListener implements ListDataListener {
		public void contentsChanged(ListDataEvent e) {
			updateDoc();
		}

		public void intervalAdded(ListDataEvent e) {
			updateDoc();
		}

		public void intervalRemoved(ListDataEvent e) {
			updateDoc();
		}
	}
}
;
