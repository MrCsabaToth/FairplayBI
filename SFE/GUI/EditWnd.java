// EditWnd.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;

import java.awt.Container;

import javax.swing.JFrame;


/**
 * The Editor Window
 *
 * This window includes all the editing controls.
 * It is composed of multiple switching panels,
 * as the different editing windows.
 */
public class EditWnd extends JFrame {
	//~ Instance fields --------------------------------------------------------

	// Variables
	public EditWndPanel editWndPanel; // The Panel of this window
	public JFrame       codeWnd; // The code (main) Window
	private Program     program; // The program

	//~ Constructors -----------------------------------------------------------

	public EditWnd(JFrame codeWnd) {
		super("Editor");

		this.codeWnd     = codeWnd;
		program          = ((CodeWnd) codeWnd).program;

		Container content = getContentPane();

		editWndPanel = new EditWndPanel(program, this);

		content.add(editWndPanel);

		//	setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	//~ Methods ----------------------------------------------------------------

	/**
	 * Refresh all type-lists in the Editor by rereading
	 * data from the program.
	 */
	public void updateTypes() {
		editWndPanel.funcPanel.updateTypes();
		editWndPanel.editVarPanel.updateTypes();
		editWndPanel.typesPanel.updateTypes();
	}
}
