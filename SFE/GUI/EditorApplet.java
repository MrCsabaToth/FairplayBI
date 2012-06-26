// EditorApplet.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JApplet;


/**
 * This is the Applet class which will run the application.
 *
 * Include this applet in your HTML file.
 *
 * For example:
 *        <applet name=EditorApp code="EditorApplet.class" width=00, height=00>
 *        </applet>
 *
 * The code can be read by the browser, by using the genCode() method.
 * For example, from a JavaScript, you can write:
 *
 * code = document.EditorApp.genCode();
 *
 */
public class EditorApplet extends JApplet implements WindowListener {
	//~ Instance fields --------------------------------------------------------

	CodeWnd codeWnd; // The main window
	EditWnd editWnd; // The editow window

	//~ Constructors -----------------------------------------------------------

	/**
	 * Constructor for the applet
	 */
	public EditorApplet() {
		codeWnd = new CodeWnd();
		codeWnd.pack();
		codeWnd.setVisible(true);

		editWnd = new EditWnd(codeWnd);
		editWnd.pack();
		editWnd.setLocation(400, 380);

		codeWnd.codeWndPanel.setEditWnd(editWnd);
		editWnd.setResizable(false);
	}

	//~ Methods ----------------------------------------------------------------

	/**
	 * Generate the current program code from the editor.
	 *
	 * @return The program code as a long string
	 */
	public String genCode() {
		String str = "Can't find program\n";

		str = codeWnd.program.generateCode();

		return str;
	}

	// Default applet methods
	public void windowDeactivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		codeWnd.setVisible(false);
		editWnd.setVisible(false);
	}
}
