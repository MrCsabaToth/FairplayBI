// ProgPanel.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.text.BadLocationException;


/**
 * An Editor Panel.
 *
 * This panel is used in the Editor window
 * to edit program general properties
 */
public class ProgPanel extends JPanel implements EditorPanel {
	//~ Instance fields --------------------------------------------------------

	// Class variables
	private Program program; // The program
	private JFrame  mainWindow; // The editor window

	// Graphic Components
	private JLabel nameLbl;

	// Graphic Components
	private JLabel      descLbl;
	private JTextField  nameFld;
	private JTextArea   descArea;
	private JScrollPane descScroll;
	private JButton     updateDescBtn;
	private JButton     newBtn;

	//~ Constructors -----------------------------------------------------------

	// Constructor

	/**
	 * Construct a new ProgPanel
	 *
	 * @param program The program
	 * @param wnd The Editor Window
	 */
	public ProgPanel(Program program, JFrame wnd) {
		super();

		this.program     = program;
		mainWindow       = wnd;

		setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED),
		                                           "Program Settings"));

		nameLbl           = new JLabel("Program Name: ");
		descLbl           = new JLabel("Description:     ");
		nameFld           = new JTextField(program.programName, null, 8);
		newBtn            = new JButton("Start A New Program");
		updateDescBtn     = new JButton(" Update Description ");

		descArea = new JTextArea(7, 28);
		descArea.setText(program.programDesc.getDoc());

		descArea.setLineWrap(true);

		updateDescription();

		descScroll = new JScrollPane(descArea);
		descScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		descScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		updateDescBtn.addActionListener(new ButtonsListener());
		newBtn.addActionListener(new ButtonsListener());

		Box                box = new Box(BoxLayout.Y_AXIS);

		GridBagLayout      layout = new GridBagLayout();
		JPanel             pane   = new JPanel(layout);
		GridBagConstraints c      = new GridBagConstraints();

		c.fill        = GridBagConstraints.HORIZONTAL;
		c.anchor      = GridBagConstraints.NORTH;
		c.ipadx       = 5;
		c.ipady       = 5;
		c.weightx     = 1.0;

		c.gridwidth = 1;
		pane.add(nameLbl);
		layout.setConstraints(nameLbl, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		pane.add(nameFld);
		layout.setConstraints(nameFld, c);

		JLabel lab1 = new JLabel(" ");
		pane.add(lab1);
		layout.setConstraints(lab1, c);

		c.gridwidth = 1;
		pane.add(descLbl);
		layout.setConstraints(descLbl, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		pane.add(descScroll);
		layout.setConstraints(descArea, c);

		c.gridwidth     = 1;
		c.gridx         = 1;
		c.gridy         = GridBagConstraints.RELATIVE;
		c.anchor        = GridBagConstraints.EAST;
		pane.add(updateDescBtn);
		layout.setConstraints(updateDescBtn, c);

		box.add(Box.createVerticalStrut(70));
		box.add(pane);

		add(box);
	}

	//~ Methods ----------------------------------------------------------------

	// public methods

	/**
	 * Reset panel controls
	 */
	public void reset() {
	}

	// private methods
	// Update the program description as a comment
	// in the Prorgam
	private void updateDescription() {
		try {
			program.programDesc.setDoc("");

			int beg;
			int off;

			for (int i = 0; i < descArea.getLineCount(); i++) {
				beg     = descArea.getLineStartOffset(i);
				off     = descArea.getLineEndOffset(i) - beg;
				program.programDesc.appendDoc("// " +
				                              descArea.getText(beg, off));
			}
		} catch (BadLocationException ex) {
		}
	}

	//~ Inner Classes ----------------------------------------------------------

	// Listeners
	// ActionListener for controls
	class ButtonsListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				JButton btn = (JButton) e.getSource();

				if (btn == updateDescBtn) {
					updateDescription();
				}

				if (btn == newBtn) {
					/*
					((MyWindow)mainWindow).initProgram();
					*/
				}
			} catch (ClassCastException ex) {
			}
		}
	}
}
