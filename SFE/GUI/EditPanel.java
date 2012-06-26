// EditPanel.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * An Editor Panel.
 *
 * This panel is used in the Editor window
 * to edit the functions code
 */
public class EditPanel extends JPanel implements EditorPanel {
	//~ Instance fields --------------------------------------------------------

	// Class variables
	private Program          program; // The program
	private JFrame           mainWindow; // The Editor Window
	private Function         currentFunc = null; // The current function
	private DefaultListModel body; // The function body
	private boolean          insertMode  = true; // Insert/Edit mode

	// Graphic Components
	private CodeWndPanel codePanel;
	private JList        funcList;
	private JScrollPane  funcScroll;
	private JScrollPane  codeScroll;
	private JList        codeList;
	private JButton      delCodeButton;
	private JButton      addCommentBtn;
	private JButton      ifBtn;
	private JButton      ifelseBtn;
	private JButton      forBtn;
	private ButtonGroup  modeButtons;
	private JRadioButton modeInsert;
	private JRadioButton modeEdit;
	private JButton      clearBtn;
	private JTextField   editTxt;

	//~ Constructors -----------------------------------------------------------

	// Constructors

	/**
	 * Construct a new EditPanel
	 *
	 * @param program The Program
	 * @param codePanel The code-panel (in the main window)
	 * @param wnd The Editor Window
	 */
	public EditPanel(Program program, CodeWndPanel codePanel, JFrame wnd) {
		super();
		this.program       = program;
		mainWindow         = wnd;
		this.codePanel     = codePanel;

		Box box            = new Box(BoxLayout.Y_AXIS);

		setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED),
		                                           "Edit Function Code"));

		// Components
		funcList       = new JList(program.functions);
		funcScroll     = new JScrollPane(funcList);

		funcScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		funcScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		funcScroll.setPreferredSize(new Dimension(500, 50));
		funcScroll.setMaximumSize(new Dimension(500, 50));

		funcList.addListSelectionListener(new FuncListListener());

		// stam a label panel
		JPanel labelPane1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelPane1.add(new JLabel("Choose function to edit:"));

		// Body editing panel 
		Box bodyBox      = new Box(BoxLayout.X_AXIS);
		Box leftBodyBox  = new Box(BoxLayout.Y_AXIS);
		Box rightBodyBox = new Box(BoxLayout.Y_AXIS);
		Box editBox      = new Box(BoxLayout.Y_AXIS);

		// Left side
		// label panel
		JPanel labelPane2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelPane2.add(new JLabel("Function Body:"));

		JPanel scrollPane = new JPanel(new FlowLayout(FlowLayout.LEFT));

		codeList       = new JList();
		codeScroll     = new JScrollPane(codeList);
		codeScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		codeScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		codeScroll.setPreferredSize(new Dimension(300, 170));
		codeScroll.setMaximumSize(new Dimension(300, 170));

		scrollPane.add(codeScroll);

		codeList.addListSelectionListener(new BodyListListener());

		// Right side
		GridBagLayout      cmdLayout = new GridBagLayout();
		JPanel             cmdPane = new JPanel(cmdLayout);
		GridBagConstraints c       = new GridBagConstraints();

		JPanel             delPane = new JPanel(new GridLayout(2, 1));

		cmdPane.setPreferredSize(new Dimension(180, 130));
		cmdPane.setMaximumSize(new Dimension(180, 130));

		delPane.setPreferredSize(new Dimension(180, 45));
		delPane.setMaximumSize(new Dimension(180, 45));

		ifBtn             = new JButton("if");
		ifelseBtn         = new JButton("if-else");
		forBtn            = new JButton("for");
		delCodeButton     = new JButton(" Delete Line ");
		addCommentBtn     = new JButton("Add Comment");
		editTxt           = new JTextField(35);
		clearBtn          = new JButton("Clear");

		clearBtn.setFont(clearBtn.getFont().deriveFont(Font.PLAIN, 11));
		clearBtn.setPreferredSize(new Dimension(76, 23));
		clearBtn.setMaximumSize(new Dimension(76, 23));

		// Edit mode
		modeButtons     = new ButtonGroup();
		modeInsert      = new JRadioButton("Insert", true);
		modeEdit        = new JRadioButton("Edit", false);

		modeButtons.add(modeInsert);
		modeButtons.add(modeEdit);

		modeInsert.addActionListener(new FormActionListener());
		modeEdit.addActionListener(new FormActionListener());

		// set listeners
		ifBtn.addActionListener(new FormActionListener());
		ifelseBtn.addActionListener(new FormActionListener());
		forBtn.addActionListener(new FormActionListener());
		delCodeButton.addActionListener(new FormActionListener());
		addCommentBtn.addActionListener(new FormActionListener());
		editTxt.addActionListener(new FormActionListener());
		clearBtn.addActionListener(new FormActionListener());

		// add right buttons
		c.fill        = GridBagConstraints.BOTH;
		c.weightx     = 1.0;

		JLabel modeLbl = new JLabel("Editing mode:");
		cmdPane.add(modeLbl);
		c.gridwidth = GridBagConstraints.REMAINDER;
		cmdLayout.setConstraints(modeLbl, c);

		cmdPane.add(modeInsert);
		c.gridwidth = GridBagConstraints.REMAINDER;
		cmdLayout.setConstraints(modeInsert, c);

		cmdPane.add(modeEdit);
		c.gridwidth = 1;
		cmdLayout.setConstraints(modeEdit, c);

		JPanel p1 = new JPanel();
		cmdPane.add(p1);
		c.gridwidth = GridBagConstraints.REMAINDER;
		cmdLayout.setConstraints(p1, c);

		c.gridwidth = 1;
		cmdPane.add(ifBtn);
		cmdLayout.setConstraints(ifBtn, c);

		cmdPane.add(ifelseBtn);
		c.gridwidth = GridBagConstraints.REMAINDER;
		cmdLayout.setConstraints(ifelseBtn, c);

		cmdPane.add(forBtn);
		c.gridwidth = GridBagConstraints.REMAINDER;
		cmdLayout.setConstraints(forBtn, c);

		delPane.add(addCommentBtn);
		delPane.add(delCodeButton);

		// Bottom side - edit box
		GridBagLayout editLayout = new GridBagLayout();
		JPanel        editPane = new JPanel(editLayout);
		editPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		editPane.add(editTxt);
		editPane.add(clearBtn);

		c.fill          = GridBagConstraints.HORIZONTAL;
		c.gridwidth     = 1;
		c.fill          = GridBagConstraints.HORIZONTAL;
		editLayout.setConstraints(editTxt, c);
		c.gridwidth     = GridBagConstraints.REMAINDER;
		c.fill          = GridBagConstraints.NONE;
		editLayout.setConstraints(clearBtn, c);

		// operators buttons
		JPanel  opPane = new JPanel(new GridLayout(2, 8, 0, 3));

		JButton opEq    = new JButton(OP_EQ);
		JButton opPlus  = new JButton(OP_PLUS);
		JButton opMinus = new JButton(OP_MINUS);
		JButton opAnd   = new JButton(OP_AND);
		JButton opOr    = new JButton(OP_OR);
		JButton opNot   = new JButton(OP_NOT);
		JButton opXor   = new JButton(OP_XOR);
		JButton opAsgn  = new JButton(OP_ASGN);
		JButton opLt    = new JButton(OP_LT);
		JButton opGt    = new JButton(OP_GT);
		JButton opLet   = new JButton(OP_LET);
		JButton opGet   = new JButton(OP_GET);

		// Line 1
		opPane.add(new JPanel());
		opPane.add(opAsgn);
		opPane.add(opLt);
		opPane.add(opGt);
		opPane.add(opEq);
		opPane.add(opLet);
		opPane.add(opGet);
		opPane.add(new JPanel());

		// Line 2
		opPane.add(new JPanel());
		opPane.add(opPlus);
		opPane.add(opMinus);
		opPane.add(opAnd);
		opPane.add(opOr);
		opPane.add(opNot);
		opPane.add(opXor);
		opPane.add(new JPanel());

		opEq.addActionListener(new FormActionListener());
		opPlus.addActionListener(new FormActionListener());
		opMinus.addActionListener(new FormActionListener());
		opAnd.addActionListener(new FormActionListener());
		opOr.addActionListener(new FormActionListener());
		opNot.addActionListener(new FormActionListener());
		opXor.addActionListener(new FormActionListener());
		opAsgn.addActionListener(new FormActionListener());
		opLt.addActionListener(new FormActionListener());
		opGt.addActionListener(new FormActionListener());
		opLet.addActionListener(new FormActionListener());
		opGet.addActionListener(new FormActionListener());

		// Disable controls
		disableControls();

		// Add all panels
		box.add(labelPane1);
		box.add(funcScroll);
		box.add(Box.createVerticalStrut(7));

		leftBodyBox.add(labelPane2);
		leftBodyBox.add(scrollPane);

		rightBodyBox.add(Box.createVerticalStrut(1));
		rightBodyBox.add(cmdPane);
		rightBodyBox.add(Box.createVerticalStrut(5));
		rightBodyBox.add(delPane);

		bodyBox.add(leftBodyBox);
		bodyBox.add(rightBodyBox);

		editBox.add(editPane);
		editBox.add(Box.createVerticalStrut(10));
		editBox.add(opPane);

		box.add(bodyBox);
		box.add(editBox);

		add(box);

		funcList.clearSelection();
		updateFunc();
		disableControls();
	}

	//~ Methods ----------------------------------------------------------------

	// Public methods

	/**
	 * Reset panel controls
	 */
	public void reset() {
		funcList.clearSelection();
		funcList.setSelectedIndex(0);
	}

	// Private methods

	/**
	 * Disable panel controls
	 */
	private void disableControls() {
		editTxt.setEnabled(false);
		ifBtn.setEnabled(false);
		ifelseBtn.setEnabled(false);
		forBtn.setEnabled(false);
		delCodeButton.setEnabled(false);
		addCommentBtn.setEnabled(false);
		modeInsert.setEnabled(false);
		modeEdit.setEnabled(false);
	}

	/**
	 * Enable panel contols
	 */
	private void enableControls() {
		editTxt.setEnabled(true);
		ifBtn.setEnabled(true);
		ifelseBtn.setEnabled(true);
		forBtn.setEnabled(true);
		delCodeButton.setEnabled(true);
		addCommentBtn.setEnabled(true);
		modeInsert.setEnabled(true);
		modeEdit.setEnabled(true);
	}

	/**
	 * Set Edit-Mode (overwrite)
	 */
	private void setEditMode() {
		insertMode = false;

		// edit selected line
		if (codeList.getSelectedIndex() != -1) {
			editTxt.setText(((Statement) codeList.getSelectedValue()).code);
		}

		modeEdit.setSelected(true);
	}

	/**
	 * Set Insert-Mode
	 */
	private void setInsertMode() {
		insertMode = true;
		editTxt.setText("");

		modeInsert.setSelected(true);
	}

	/**
	 * Update the panel controls with the selected function
	 */
	private void updateFunc() {
		if (funcList.getSelectedIndex() != -1) {
			currentFunc = (Function) funcList.getSelectedValue();

			currentFunc.updateDoc();

			codeList.setModel(currentFunc.body);
			body = currentFunc.body;
			body.addListDataListener(new FuncBodyListener());

			setInsertMode();
		} else {
			codeList.setModel(new DefaultListModel());
			body = null;
		}
	}

	//~ Inner Classes ----------------------------------------------------------

	// Listeners
	// DataListener for the function body
	class FuncBodyListener implements ListDataListener {
		public void contentsChanged(ListDataEvent e) {
			currentFunc.updateDoc();
		}

		public void intervalAdded(ListDataEvent e) {
			currentFunc.updateDoc();
		}

		public void intervalRemoved(ListDataEvent e) {
			currentFunc.updateDoc();
		}
	}

	// ActionListener for panel controls
	class FormActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				Object o = e.getSource();

				if (body == null) {
					String[] msg =
					{ "No function selected", "Select a function before starting to edit" };
					new MessageBox(mainWindow, "Error", msg);

					return;
				}

				// Editing mode radio buttons
				if (o instanceof JRadioButton) {
					JRadioButton rb = (JRadioButton) o;

					if (rb == modeEdit) {
						setEditMode();
					}

					if (rb == modeInsert) {
						setInsertMode();
					}
				}

				// Get Next Line & level
				int nextLine = codeList.getSelectedIndex() + 1;
				int level = 0;

				if (nextLine > 0) {
					level =
						((Statement) codeList.getSelectedValue()).getNextLevel();
				}

				// Editing line
				if (o instanceof JTextField) {
					JTextField txtFld = (JTextField) o;

					if (txtFld == editTxt) {
						if (insertMode) {
							if (editTxt.getText().lastIndexOf(';') == -1) {
								editTxt.setText(editTxt.getText() + ";");
							}

							body.insertElementAt(new Statement(level, level,
							                                   editTxt.getText()),
							                     nextLine);
							codeList.setSelectedIndex(nextLine);

							editTxt.setText("");
						} else {
							if (codeList.getSelectedIndex() != -1) {
								// update edited line
								((Statement) codeList.getSelectedValue()).code =
									editTxt.getText();

								codeList.repaint();
								currentFunc.updateDoc();
							}
						}
					}
				}

				// Buttons
				if (o instanceof JButton) {
					JButton btn = (JButton) o;

					if (btn == ifBtn) {
						// insert IF block
						body.insertElementAt(new Statement(level, level + 1,
						                                   "if(){"), nextLine);
						body.insertElementAt(new Statement(level, level, "}"),
						                     nextLine + 1);
						codeList.setSelectedIndex(nextLine); // select if statement

						setEditMode();

						editTxt.requestFocus();
						editTxt.setCaretPosition(3);
					} else if (btn == forBtn) {
						// insert FOR block
						body.insertElementAt(new Statement(level, level + 1,
						                                   "for() {"), nextLine);
						body.insertElementAt(new Statement(level, level, "}"),
						                     nextLine + 1);
						codeList.setSelectedIndex(nextLine); // select for statement

						setEditMode();

						editTxt.requestFocus();
						editTxt.setCaretPosition(4);
					} else if (btn == ifelseBtn) {
						// insert IF-ELSE block
						body.insertElementAt(new Statement(level, level + 1,
						                                   "if(){"), nextLine);
						body.insertElementAt(new Statement(level, level + 1,
						                                   "} else {"),
						                     nextLine + 1);
						body.insertElementAt(new Statement(level, level, "}"),
						                     nextLine + 2);
						codeList.setSelectedIndex(nextLine); // select if statement

						setEditMode();

						editTxt.requestFocus();
						editTxt.setCaretPosition(3);
					} else if (btn == delCodeButton) {
						// Next Line
						nextLine = codeList.getSelectedIndex() + 1;

						if (nextLine == 0) {
							return;
						}

						body.removeElement(codeList.getSelectedValue());

						if (body.size() < nextLine) {
							codeList.setSelectedIndex(body.size() - 1);
						} else {
							codeList.setSelectedIndex(nextLine - 1);
						}

						return;
					} else if (btn == addCommentBtn) {
						body.insertElementAt(new Statement(level, level, "// "),
						                     nextLine);
						codeList.setSelectedIndex(nextLine); // select if statement

						setEditMode();
					} else if (btn == clearBtn) {
						editTxt.setText("");
					} else {
						String l = btn.getText();

						if (l.equals(OP_EQ) || l.equals(OP_PLUS) ||
							    l.equals(OP_MINUS) || l.equals(OP_AND) ||
							    l.equals(OP_OR) || l.equals(OP_NOT) ||
							    l.equals(OP_ASGN) || l.equals(OP_LT) ||
							    l.equals(OP_GT) || l.equals(OP_LET) ||
							    l.equals(OP_GET) || l.equals(OP_XOR)) {
							editTxt.setText(editTxt.getText() + l);
						}
					}
				}

				editTxt.requestFocus();
			} catch (ClassCastException ex) {
			}
		}
	}

	// ListListener for the Function list
	class FuncListListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			try {
				JList list = (JList) e.getSource();

				if (list.getSelectedIndex() != -1) {
					enableControls();
					codePanel.setFunctionCode((Function) list.getSelectedValue());
					updateFunc();
				} else {
					updateFunc();
					disableControls();
				}

				editTxt.requestFocus();
			} catch (ClassCastException ex) {
			}
		}
	}

	// ListListener for the function body 
	class BodyListListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			try {
				JList list = (JList) e.getSource();

				if (list.getSelectedIndex() != -1) {
					if (! insertMode) {
						// Edit selected line
						editTxt.setText(((Statement) list.getSelectedValue()).code);
					}
				} else {
					if (! insertMode) {
						// if editing, clear line
						editTxt.setText("");
					}
				}

				editTxt.requestFocus();
			} catch (ClassCastException ex) {
			}
		}
	}

	//~ Static fields/initializers ---------------------------------------------

	// Constants
	private static final String OP_ASGN  = "=";
	private static final String OP_PLUS  = "+";
	private static final String OP_MINUS = "-";
	private static final String OP_AND   = "&";
	private static final String OP_OR    = "|";
	private static final String OP_NOT   = "~";
	private static final String OP_XOR   = "^";
	private static final String OP_LT    = "<";
	private static final String OP_GT    = ">";
	private static final String OP_LET   = "<=";
	private static final String OP_GET   = ">=";
	private static final String OP_EQ    = "==";
}
