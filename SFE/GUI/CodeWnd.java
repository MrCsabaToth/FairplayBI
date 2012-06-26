// CodeWnd.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;


/**
 * The Main Application window
 *
 * This window shows the code of the program,
 * And the editor buttons.
 */
public class CodeWnd extends JFrame {
	//~ Instance fields --------------------------------------------------------

	// Variables
	public CodeWndPanel codeWndPanel; // Panel of this window
	private EditWnd     editWnd = null; // Editor Window
	public Program      program = null; // The program

	//~ Constructors -----------------------------------------------------------

	public CodeWnd() {
		super("Secure Function Editor");

		//	setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		initProgram();

		Container content = getContentPane();

		if (program == null) {
			System.exit(0);
		}

		codeWndPanel = new CodeWndPanel(program, this);

		content.add(codeWndPanel);

		// Close listener
		addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
	}

	public CodeWnd(String filename, Object gLock) {
		super("Secure Function Editor");

		//	setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		initProgram();

		Container content = getContentPane();

		if (program == null) {
			System.exit(0);
		}

		program.outputFilename     = filename;
		program.globalLock         = gLock;

		codeWndPanel = new CodeWndPanel(program, this);

		content.add(codeWndPanel);

		// Close listener
		addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
	}

	//~ Methods ----------------------------------------------------------------

	// Public methods

	/**
	 * Create and initialize a new program
	 */
	public void initProgram() {
		if (program == null) {
			program = new Program(DEFAULT_PROGNAME, DEFAULT_DESCRIPTION);
		} else {
			program.init(DEFAULT_PROGNAME, DEFAULT_DESCRIPTION);
			codeWndPanel.setProgramCode();
		}
	}

	//~ Static fields/initializers ---------------------------------------------

	// Constants
	public static final String DEFAULT_PROGNAME    = "myProgram";
	public static final String DEFAULT_DESCRIPTION = "*** myProgram ***";
}
