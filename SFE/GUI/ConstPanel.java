// ConstPanel.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.PlainDocument;


/**
 * An Editor Panel.
 *
 * This panel is used in the Editor window
 * to edit the program constants
 */
public class ConstPanel extends JPanel implements EditorPanel {
	//~ Instance fields --------------------------------------------------------

	// Class vars
	private Program program; // The program
	private JFrame  mainWindow; // The editor window

	// Data Model
	private ProgramDocument  nameDoc; // Constant name
	private ProgramDocument  valDoc; // Constant value
	private DefaultListModel consts; // Constants list

	// Graphic components
	private JScrollPane scroll;
	private JList       list;
	private JTextField  constName;
	private JTextField  constVal;
	private JButton     addButton;
	private JButton     delButton;

	//~ Constructors -----------------------------------------------------------

	// Constructors

	/**
	 * Construct a new ConstPanel
	 *
	 * @param program The Program
	 * @param wnd The Editor Window
	 */
	public ConstPanel(Program program, JFrame wnd) {
		super();
		this.program     = program;
		mainWindow       = wnd;

		consts = program.constants;

		setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED),
		                                           "Constants"));

		Box box = new Box(BoxLayout.X_AXIS);

		Box leftBox  = new Box(BoxLayout.Y_AXIS);
		Box rightBox = new Box(BoxLayout.Y_AXIS);

		list       = new JList(consts);
		scroll     = new JScrollPane(list);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setMaximumSize(new Dimension(350, 100));
		scroll.setPreferredSize(new Dimension(250, 100));

		list.addListSelectionListener(new ListListener());

		// Form
		JPanel namePane = new JPanel(new FlowLayout());
		JPanel valPane = new JPanel(new FlowLayout());
		JPanel btnPane = new JPanel(new FlowLayout());

		// Components
		constName     = new JTextField(10);
		constVal      = new JTextField(10);
		addButton     = new JButton("New Constant");
		delButton     = new JButton("Remove");

		constName.setEnabled(false);
		constVal.setEnabled(false);

		// some listeners
		addButton.addActionListener(new FormActionListener());
		delButton.addActionListener(new FormActionListener());

		namePane.add(new JLabel("Name: "));
		namePane.add(constName);
		valPane.add(new JLabel("Value: "));
		valPane.add(constVal);
		btnPane.add(addButton);
		btnPane.add(delButton);

		leftBox.add(Box.createVerticalStrut(30));
		leftBox.add(btnPane);
		leftBox.add(namePane);
		leftBox.add(valPane);

		rightBox.add(Box.createVerticalStrut(50));
		rightBox.add(new JLabel("Constants:"));
		rightBox.add(scroll);

		box.add(leftBox);
		box.add(Box.createHorizontalStrut(40));
		box.add(rightBox);

		add(box);
	}

	//~ Methods ----------------------------------------------------------------

	// Public methods

	/**
	 * Reset the panel controls
	 */
	public void reset() {
	}

	//~ Inner Classes ----------------------------------------------------------

	// Listeners
	// DocumentListener for name and value
	class DocListener implements DocumentListener {
		public void changedUpdate(DocumentEvent e) {
		}

		public void insertUpdate(DocumentEvent e) {
			try {
				ProgramDocument d = (ProgramDocument) e.getDocument();
				program.updateDoc();
				list.repaint();
			} catch (ClassCastException ex) {
			}
		}

		public void removeUpdate(DocumentEvent e) {
			try {
				ProgramDocument d = (ProgramDocument) e.getDocument();
				program.updateDoc();
				list.repaint();
			} catch (ClassCastException ex) {
			}
		}
	}

	// ListListener for constant list
	class ListListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			try {
				JList l = (JList) e.getSource();

				constName.setEnabled(true);
				constVal.setEnabled(true);

				Constant c = (Constant) l.getSelectedValue();
				nameDoc     = c.nameDoc;
				valDoc      = c.valueDoc;

				constName.setDocument(nameDoc);
				constVal.setDocument(valDoc);

				nameDoc.addDocumentListener(new DocListener());
				valDoc.addDocumentListener(new DocListener());
			} catch (ClassCastException ex) {
			}
		}
	}

	// ActionListener for controls
	class FormActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				Object o = e.getSource();

				if (o instanceof JButton) {
					JButton btn = (JButton) o;

					if (btn == addButton) {
						Constant newConst =
							new Constant(DEFAULT_CONST_NAME, DEFAULT_CONST_VALUE);

						int      index = 1;

						while (consts.contains(newConst)) {
							newConst.nameDoc.setDoc(DEFAULT_CONST_NAME + "_" +
							                        index);
							index++;
						}

						if (! consts.contains(newConst)) {
							consts.addElement(newConst);
							list.setSelectedIndex(consts.getSize() - 1);
							constName.setEnabled(true);
							constVal.setEnabled(true);

							constName.requestFocus();
							constName.selectAll();
						} else {
							new MessageBox(mainWindow, "Error",
							               "Constant " + DEFAULT_CONST_NAME +
							               " already exists!");
						}
					}

					if (btn == delButton) {
						int selIndex = list.getSelectedIndex();

						// select next if available
						if (selIndex != -1) {
							consts.removeElement(list.getSelectedValue());

							if (selIndex < consts.getSize()) {
								list.setSelectedIndex(selIndex);
							} else if (consts.getSize() > 0) {
								list.setSelectedIndex(selIndex - 1);
							}
						}

						program.updateDoc();
						list.repaint();

						// empty form
						if (list.getSelectedIndex() == -1) {
							constName.setDocument(new PlainDocument());
							constVal.setDocument(new PlainDocument());
							constName.setEnabled(false);
							constVal.setEnabled(false);
						}
					}
				}
			} catch (ClassCastException ex) {
			}
		}
	}

	//~ Static fields/initializers ---------------------------------------------

	// Constants
	private static final String DEFAULT_CONST_NAME  = "_c";
	private static final String DEFAULT_CONST_VALUE = "0";
}
