// GUIMain.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;

public class GUIMain {
	//~ Methods ----------------------------------------------------------------

	public static void GUI_Usage() {
		System.out.println("Usage: java -cp <class-path> GUI.GUIMain <filename>");
		System.exit(1);
	}

	// Main routine for activating the GUI as part of SFE
	public static void guiMain(String filename) throws InterruptedException {
		CodeWnd codeWnd = new CodeWnd(filename, gLock);
		codeWnd.pack();
		codeWnd.setVisible(true);

		EditWnd editWnd = new EditWnd(codeWnd);
		editWnd.pack();

		editWnd.setLocation(400, 380);

		codeWnd.codeWndPanel.setEditWnd(editWnd);

		editWnd.setResizable(false);

		// Wait until window threads finish and release this global lock
		synchronized (gLock) {
			gLock.wait();
		}
	}

	// Main program for activating the GUI stand-alone
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			GUI_Usage();
		}

		guiMain(args[0]);
	}

	//~ Static fields/initializers ---------------------------------------------

	public static Object gLock = new Object();
}
