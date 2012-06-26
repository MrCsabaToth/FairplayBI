// FuncPanel.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


/**
 * An Editor Panel.
 *
 * This panel is used in the Editor window
 * to add, delete and edit function definitions.
 */
public class FuncPanel extends JPanel implements EditorPanel {
	//~ Instance fields --------------------------------------------------------

	// Class variables
	private Program program; // The program
	private JFrame  editWindow; // The Editor window

	// Data model
	private DefaultListModel functions; // The functions
	private DefaultListModel arguments   = null; // Current func arguments
	private Function         currentFunc = null; // Current function

	// Graphic components
	private JScrollPane scroll;
	private JList       list;
	private JTextArea   descArea;
	private JScrollPane descScroll;
	private JButton     updateDescBtn;
	private JTextField  funcName;
	private JComboBox   retTypes;
	private JButton     addButton;
	private JButton     delButton;
	private JScrollPane argScroll;
	private JList       argList;
	private JButton     newArgBtn;
	private JButton     delArgBtn;
	private VarPane     argsPane;

	//~ Constructors -----------------------------------------------------------

	// Constructors

	/**
	 * Construct a new FuncPanel
	 *
	 * @param program The program
	 * @param wnd The Editor Window
	 */
	public FuncPanel(Program program, JFrame wnd) {
		super();
		this.program     = program;
		editWindow       = wnd;

		functions = program.functions;

		Box box = new Box(BoxLayout.Y_AXIS);

		Box topBox    = new Box(BoxLayout.Y_AXIS);
		Box bottomBox = new Box(BoxLayout.Y_AXIS);

		setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED),
		                                           "Functions"));

		// Components
		list       = new JList(functions);
		scroll     = new JScrollPane(list);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setPreferredSize(new Dimension(500, 60));
		scroll.setMaximumSize(new Dimension(500, 60));

		list.addListSelectionListener(new FuncListListener());

		funcName     = new JTextField(10);
		retTypes     = new JComboBox(program.types.toArray());
		retTypes.setMaximumSize(new Dimension(100, 25));
		retTypes.setPreferredSize(new Dimension(100, 25));

		// Description
		descArea = new JTextArea(2, 20);
		descArea.setLineWrap(true);
		descScroll = new JScrollPane(descArea);
		descScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		descScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		updateDescBtn = new JButton("Update");
		updateDescBtn.addActionListener(new FormActionListener());
		updateDescBtn.setFont(updateDescBtn.getFont().deriveFont(Font.PLAIN, 11));
		updateDescBtn.setPreferredSize(new Dimension(76, 23));
		updateDescBtn.setMaximumSize(new Dimension(76, 23));

		addButton     = new JButton("New Function");
		delButton     = new JButton("Remove");

		newArgBtn     = new JButton("New Arg");
		delArgBtn     = new JButton("Remove");

		argsPane = new VarPane(program, new Updater());

		// some listeners
		retTypes.addItemListener(new TypesListener());
		addButton.addActionListener(new FormActionListener());
		delButton.addActionListener(new FormActionListener());
		newArgBtn.addActionListener(new FormActionListener());
		delArgBtn.addActionListener(new FormActionListener());

		// name panel
		JPanel namePane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		namePane.add(new JLabel("Return Type: "));
		namePane.add(retTypes);
		namePane.add(new JLabel("Name: "));
		namePane.add(funcName);

		// Description
		JPanel descPane = new JPanel(new BorderLayout(1, 1));
		descPane.add(new JLabel("Description:"), BorderLayout.WEST);
		descPane.add(descScroll, BorderLayout.CENTER);
		descPane.add(updateDescBtn, BorderLayout.EAST);

		// label panel
		JPanel labelPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelPane.add(new JLabel("Function List:"));

		// buttons panel
		JPanel btnPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		btnPane.add(addButton);
		btnPane.add(delButton);

		// Bottom - Argument list
		JPanel bottom = new JPanel(new BorderLayout(5, 5));

		bottom.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED),
		                                                  "Function Arguments"));

		Box argBox = new Box(BoxLayout.X_AXIS);

		Box rightArgBox = new Box(BoxLayout.Y_AXIS);

		argList       = new JList(new DefaultListModel());
		argScroll     = new JScrollPane(argList);

		argScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		argScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		argScroll.setPreferredSize(new Dimension(140, 80));

		argList.addListSelectionListener(new ArgListListener());

		// Disable controls
		disableControls();

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		p1.add(newArgBtn);
		p1.add(delArgBtn);

		rightArgBox.add(p1);
		rightArgBox.add(argScroll);

		argBox.add(argsPane);
		argBox.add(rightArgBox);

		bottom.add(argBox);

		topBox.add(btnPane);
		topBox.add(namePane);
		topBox.add(descPane);
		topBox.add(Box.createVerticalStrut(5));
		topBox.add(labelPane);
		topBox.add(scroll);

		bottomBox.add(bottom); // arguments panel

		box.add(topBox);
		topBox.add(Box.createVerticalStrut(15));
		box.add(bottomBox);

		add(box);

		list.clearSelection();
	}

	//~ Methods ----------------------------------------------------------------

	// Public methods

	/**
	 * Reread the type definitions from the Program
	 */
	void updateTypes() {
		argsPane.updateTypes();
		retTypes.setModel(new DefaultComboBoxModel(program.types.toArray()));
	}

	/**
	 * Reset the panel controls
	 */
	public void reset() {
		list.clearSelection();
		list.setSelectedIndex(0);
	}

	// Private methods

	/**
	 * Disable panel controls
	 */
	private void disableControls() {
		argsPane.clear();
		argsPane.setEnabled(false);

		arguments = null;

		argList.setModel(new DefaultListModel());

		funcName.setEnabled(false);
		retTypes.setEnabled(false);
		descArea.setEnabled(false);
		updateDescBtn.setEnabled(false);
	}

	/**
	 * Clear the panel controls
	 */
	private void clearControls() {
		funcName.setDocument(new PlainDocument());
		retTypes.setSelectedIndex(0);
		descArea.setText("");
	}

	/**
	 * Update the panel controls with a selected function
	 *
	 * @param f The selected function
	 */
	private void updateControls(Function f) {
		clearControls();
		disableControls();

		if (f == null) {
			return;
		}

		currentFunc = f;

		funcName.setDocument(f.nameDoc);

		descArea.setText(f.getDescription());

		f.nameDoc.addDocumentListener(new DocListener());

		arguments = f.arguments;
		argList.setModel(arguments);

		argsPane.clear();

		descArea.setEnabled(true);
		updateDescBtn.setEnabled(true);

		if (f.isMust()) {
			return;
		}

		funcName.setEnabled(true);
		retTypes.setEnabled(true);
	}

	/**
	 * Update the function description
	 */
	private void updateDescription() {
		if (currentFunc == null) {
			return;
		}

		try {
			currentFunc.descDoc.clear();

			int    beg;
			int    off;
			String line;

			for (int i = 0; i < descArea.getLineCount(); i++) {
				beg      = descArea.getLineStartOffset(i);
				off      = descArea.getLineEndOffset(i) - beg;
				line     = descArea.getText(beg, off);

				if (line.length() == 0) {
					continue;
				}

				if (! line.endsWith("\n")) {
					line = line + "\n";
				}

				currentFunc.descDoc.add(line);
			}
		} catch (BadLocationException ex) {
		}

		currentFunc.updateDoc();
		program.updateDoc();
	}

	//~ Inner Classes ----------------------------------------------------------

	/*
	 * Update current panel.
	 * Send this to children panels for them to update this
	 */
	public class Updater implements PanelUpdater {
		public void updatePanel() {
			currentFunc.updateDoc();
			program.updateDoc();
			list.repaint();
			argList.repaint();
		}
	}

	// Listeners
	// ActionListener for controls
	class FormActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				Object o = e.getSource();

				if (o instanceof JButton) {
					JButton btn = (JButton) o;

					if (btn == addButton) {
						// Add a default function
						Function newFunc =
							new Function(program, DEFAULT_FUNC_NAME);
						newFunc.returnType = DEFAULT_FUNC_TYPE;
						newFunc.updateDoc();

						int index = 1;

						while (functions.contains(newFunc)) {
							newFunc.nameDoc.setDoc(DEFAULT_FUNC_NAME + "_" +
							                       index);
							index++;
						}

						if (! functions.contains(newFunc)) {
							int place = list.getSelectedIndex();

							if (place == -1) {
								functions.add(0, newFunc);
								list.setSelectedIndex(0);
							} else if (place < (functions.getSize() - 1)) {
								functions.add(place + 1, newFunc);
								list.setSelectedIndex(place + 1);
							} else {
								functions.add(functions.getSize() - 1, newFunc);
								list.setSelectedIndex(functions.getSize() - 2);
							}

							((CodeWnd) ((EditWnd) editWindow).codeWnd).codeWndPanel.setProgramCode();

							updateControls(newFunc);

							funcName.requestFocus();
							funcName.selectAll();
						} else {
							new MessageBox(editWindow, "Error",
							               "Function " + DEFAULT_FUNC_NAME +
							               " already exists!");
						}
					}

					if (btn == delButton) {
						int selIndex = list.getSelectedIndex();

						if ((selIndex != -1) &&
							    ! ((Function) list.getSelectedValue()).isMust()) {
							functions.removeElement(list.getSelectedValue());

							if (selIndex < functions.getSize()) {
								list.setSelectedIndex(selIndex);
							} else if (functions.getSize() > 0) {
								list.setSelectedIndex(selIndex - 1);
							}
						}

						if (list.getSelectedIndex() == -1) {
							clearControls();
							disableControls();
						}
					}

					if (btn == updateDescBtn) {
						updateDescription();
					}

					if (btn == newArgBtn) {
						if ((arguments == null) ||
							    (list.getSelectedIndex() == -1)) {
							new MessageBox(editWindow, "Error",
							               "No function selected");

							return;
						}

						Variable newArg =
							new Variable(DEFAULT_ARGNAME, Type.INT, "",
							             Type.DEFAULT_INT_BITS);

						int      index = 1;

						while (arguments.contains(newArg)) {
							newArg.nameDoc.setDoc(DEFAULT_ARGNAME + "_" +
							                      index);
							index++;
						}

						if (! arguments.contains(newArg)) {
							arguments.addElement(newArg);
							currentFunc.updateDoc();
							program.updateDoc();
							argList.repaint();
							list.repaint();
							argList.setSelectedIndex(arguments.getSize() - 1);
						} else {
							new MessageBox(editWindow, "Error",
							               "Argument " + DEFAULT_ARGNAME +
							               " already exists");
						}
					}

					if (btn == delArgBtn) {
						if (arguments == null) {
							return;
						}

						int selIndex = argList.getSelectedIndex();

						if (selIndex != -1) {
							if (((Variable) argList.getSelectedValue()).must) {
								return;
							}

							arguments.removeElement(argList.getSelectedValue());

							if (selIndex < arguments.getSize()) {
								argList.setSelectedIndex(selIndex);
							} else if (arguments.getSize() > 0) {
								argList.setSelectedIndex(selIndex - 1);
							}

							currentFunc.updateDoc();
							program.updateDoc();
							argList.repaint();
							list.repaint();
						} else {
							new MessageBox(editWindow, "Error",
							               " Select function argument to remove ");
						}
					}
				}
			} catch (ClassCastException ex) {
			} catch (Exception ex) {
			}
		}
	}

	// ListListener for the types list
	class TypesListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			try {
				Object o = e.getSource();

				if (o instanceof JComboBox) {
					JComboBox btn = (JComboBox) o;

					if (list.getSelectedIndex() != -1) {
						// some func is selected
						Function f = (Function) list.getSelectedValue();

						if (f != null) {
							f.returnType = (Type) retTypes.getSelectedItem();
							program.updateDoc();
							argList.repaint();
							list.repaint();
						}
					}
				}
			} catch (ClassCastException ex) {
			}
		}
	}

	// ListListener for the function list
	class FuncListListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			try {
				JList    flist = (JList) e.getSource();

				Function f = (Function) flist.getSelectedValue();

				if (f != null) {
					updateControls(f);
				} else {
					clearControls();
					disableControls();
				}
			} catch (ClassCastException ex) {
			}
		}
	}

	// DocumentListener for the function name
	class DocListener implements DocumentListener {
		public void changedUpdate(DocumentEvent e) {
		}

		public void insertUpdate(DocumentEvent e) {
			program.updateDoc();
			argList.repaint();
			list.repaint();
		}

		public void removeUpdate(DocumentEvent e) {
			program.updateDoc();
			argList.repaint();
			list.repaint();
		}
	}

	// ListListener for the arguments list
	class ArgListListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			try {
				JList alist = (JList) e.getSource();

				if (currentFunc != null) {
					currentFunc.updateDoc();
				}

				program.updateDoc();
				alist.repaint();

				if (alist.getSelectedIndex() != -1) {
					argsPane.setEnabled(true);
					argsPane.setVar((Variable) alist.getSelectedValue());

					if (((Variable) alist.getSelectedValue()).must) {
						argsPane.setEnabled(false);
					}
				} else {
					argsPane.clear();
					argsPane.setEnabled(false);
				}
			} catch (ClassCastException ex) {
			}
		}
	}

	//~ Static fields/initializers ---------------------------------------------

	// Constants
	private static final String DEFAULT_FUNC_NAME = "_f";
	private static final String DEFAULT_ARGNAME   = "_a";
	private static final Type   DEFAULT_FUNC_TYPE = Type.INT;
}
