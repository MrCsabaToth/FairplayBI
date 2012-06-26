// EditVarPanel.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * An Editor Panel.
 *
 * This panel is used in the Editor window
 * to edit the function local variables
 */
public class EditVarPanel extends JPanel implements EditorPanel {
	//~ Instance fields --------------------------------------------------------

	// Class variables
	private Program  program; // The program
	private JFrame   editWindow; // The editor window
	private Function currentFunc = null; // The current edited function

	// Graphic Components
	private CodeWndPanel codePanel;
	private JList        funcList;
	private JList        varList;
	private JScrollPane  funcScroll;
	private JScrollPane  varScroll;
	private JButton      newVarBtn;
	private JButton      delVarBtn;
	private VarPane      varsPane;

	//~ Constructors -----------------------------------------------------------

	// Constructors

	/**
	 * Construct a new EditVarPanel
	 *
	 * @param program The program
	 * @param codePanel The panel where function code is shown (in the
	 *                  main window)
	 * @param wnd The editor window
	 */
	public EditVarPanel(Program program, CodeWndPanel codePanel, JFrame wnd) {
		super();
		this.program       = program;
		editWindow         = wnd;
		this.codePanel     = codePanel;

		Box box            = new Box(BoxLayout.Y_AXIS);

		setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED),
		                                           "Edit Function Variables"));

		// Components
		funcList       = new JList(program.functions);
		funcScroll     = new JScrollPane(funcList);

		funcScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		funcScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		funcScroll.setPreferredSize(new Dimension(500, 70));
		funcScroll.setMaximumSize(new Dimension(500, 70));

		funcList.addListSelectionListener(new FuncListListener());

		//	addVarButton = new JButton("Add Variable");
		//	delVarButton = new JButton("Remove");
		newVarBtn     = new JButton("New Var");
		delVarBtn     = new JButton("Remove");

		newVarBtn.addActionListener(new FormActionListener());
		delVarBtn.addActionListener(new FormActionListener());

		varsPane = new VarPane(program, new Updater());

		// stam a label panel
		JPanel labelPane1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelPane1.add(new JLabel("Choose function to edit:"));

		// Variable list panel
		JPanel varPane = new JPanel(new GridLayout(1, 1));
		varPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		varPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED),
		                                                   "Local Variables"));

		Box varBox = new Box(BoxLayout.X_AXIS);

		Box rightVarBox = new Box(BoxLayout.Y_AXIS);

		varList       = new JList(new DefaultListModel());
		varScroll     = new JScrollPane(varList);

		varScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		varScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		varScroll.setPreferredSize(new Dimension(140, 80));

		varList.addListSelectionListener(new VarsListListener());

		// Disable controls
		disableControls();

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		p1.add(newVarBtn);
		p1.add(delVarBtn);

		rightVarBox.add(p1);
		rightVarBox.add(varScroll);

		varBox.add(varsPane);
		varBox.add(rightVarBox);

		varPane.add(varBox);

		box.add(Box.createVerticalStrut(5));
		box.add(labelPane1);
		box.add(funcScroll);
		box.add(Box.createVerticalStrut(40));
		box.add(varPane);

		add(box);

		funcList.clearSelection();
		updateFunc();
	}

	//~ Methods ----------------------------------------------------------------

	// Public methods

	/**
	 * Reset panel controls
	 */
	public void reset() {
		// select the first function
		funcList.setSelectedIndex(0);
		updateFunc();
	}

	/**
	 * Reread the type definitions from the program
	 */
	public void updateTypes() {
		varsPane.updateTypes();
	}

	// Private methods

	/**
	 * Disable panel controls
	 */
	private void disableControls() {
		varsPane.setEnabled(false);
	}

	/**
	 * Update the form controls with current function
	 */
	private void updateFunc() {
		if (funcList.getSelectedIndex() != -1) {
			currentFunc = (Function) funcList.getSelectedValue();
			currentFunc.updateDoc();

			varList.setModel(currentFunc.variables);
		} else {
			currentFunc = null;
			varList.setModel(new DefaultListModel());
		}

		varsPane.clear();
		varsPane.setEnabled(false);
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
			varList.repaint();
		}
	}

	// Listeners
	// ActionListener for panel controls
	class FormActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				Object o = e.getSource();

				if (o instanceof JButton) {
					JButton btn = (JButton) o;

					if (btn == newVarBtn) {
						if (funcList.getSelectedIndex() == -1) {
							new MessageBox(editWindow, "Error",
							               "No function selected");

							return;
						}

						Variable newVar =
							new Variable(DEFAULT_VARNAME, Type.INT, "",
							             Type.DEFAULT_INT_BITS);

						if ((currentFunc != null) &&
							    currentFunc instanceof Function) {
							DefaultListModel vars  = currentFunc.variables;
							int              index = 1;

							while (vars.contains(newVar)) {
								newVar.nameDoc.setDoc(DEFAULT_VARNAME + "_" +
								                      index);
								index++;
							}

							if (! vars.contains(newVar)) {
								vars.addElement(newVar);
								currentFunc.updateDoc();
								program.updateDoc();
								varList.repaint();
								varList.setSelectedIndex(vars.getSize() - 1);
							} else {
								new MessageBox(editWindow, "Error",
								               "Variable " + DEFAULT_VARNAME +
								               " already exists!");
							}
						}

						return;
					}

					if (btn == delVarBtn) {
						if ((currentFunc == null) ||
							    (currentFunc.variables == null)) {
							return;
						}

						int selIndex = varList.getSelectedIndex();

						if (selIndex != -1) {
							currentFunc.variables.removeElement(varList.getSelectedValue());

							if (selIndex < currentFunc.variables.getSize()) {
								varList.setSelectedIndex(selIndex);
							} else if (currentFunc.variables.getSize() > 0) {
								varList.setSelectedIndex(selIndex - 1);
							} else {
								varsPane.clear();
								varsPane.setEnabled(false);
							}

							currentFunc.updateDoc();
							program.updateDoc();
							varList.repaint();
						}

						return;
					}
				}
			} catch (ClassCastException ex) {
			}
		}
	}

	// ListListener for function list
	class FuncListListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			try {
				JList list = (JList) e.getSource();

				if (list.getSelectedIndex() != -1) {
					codePanel.setFunctionCode((Function) list.getSelectedValue());
					updateFunc();
				}
			} catch (ClassCastException ex) {
			}
		}
	}

	// ListListener for variables list
	class VarsListListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			try {
				JList list = (JList) e.getSource();

				if (currentFunc != null) {
					currentFunc.updateDoc();
				}

				program.updateDoc();
				list.repaint();

				if (list.getSelectedIndex() != -1) {
					varsPane.setEnabled(true);
					varsPane.setVar((Variable) list.getSelectedValue());
				} else {
					varsPane.clear();
					varsPane.setEnabled(false);
				}
			} catch (ClassCastException ex) {
			}
		}
	}

	//~ Static fields/initializers ---------------------------------------------

	// Constants
	private static final String DEFAULT_VARNAME = "_v";
}
