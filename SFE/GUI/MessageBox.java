// MessageBox.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * MessageBox
 *
 * A Message box for displaying text, with an 'Ok' button.
 *
 */
class MessageBox extends Dialog implements ActionListener {
	//~ Instance fields --------------------------------------------------------

	private int max_line_size = 0;
	private int num_lines = 1;

	//~ Constructors -----------------------------------------------------------

	/**
	 * Constructs a MessageBox for displaying a single line of text.
	 *
	 * @param parent_frame  The parent Frame
	 * @param title The window title for the message box.
	 * @param text The text to display in the message box.
	 */
	public MessageBox(Frame parent_frame, String title, String text) {
		super(parent_frame, title, true);

		drawOk();

		Panel textPanel = new Panel();
		textPanel.add(new Label(text, Label.CENTER));
		add("Center", textPanel);
		max_line_size = text.length();

		showBox();
	}

	/**
	 * Constructs a MessageBox for displaying multiple lines of text.
	 *
	 * @param parent_frame  The parent Frame
	 * @param title The window title for the message box.
	 * @param text The multi-line text to display in the message box.
	 */
	public MessageBox(Frame parent_frame, String title, String[] text) {
		super(parent_frame, title, true);

		drawOk();

		Panel textPanel = new Panel(new GridLayout(text.length, 1));

		for (int i = 0; i < text.length; i++) {
			textPanel.add(new Label(text[i], Label.CENTER));

			if (text[i].length() > max_line_size) {
				max_line_size = text[i].length();
			}
		}

		add("Center", textPanel);

		num_lines = text.length;

		showBox();
	}

	//~ Methods ----------------------------------------------------------------

	// Adds the 'Ok' button to the message box.
	private void drawOk() {
		Panel  buttonPanel = new Panel();
		Button okButton = new Button("Ok");
		okButton.addActionListener(this);
		buttonPanel.add(okButton);
		add("South", buttonPanel);
	}

	// displays the message box 
	private void showBox() {
		setLocation(300, 300);
		setSize((max_line_size * 7) + 30, (num_lines * 14) + 100);
		setVisible(true);
	}

	// Close the MessageBox on clicking 'Ok'
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof Button) {
			String eStr = e.getActionCommand();

			if (eStr.equals("Ok")) {
				setVisible(false);
				dispose();
			}
		}
	}
}
