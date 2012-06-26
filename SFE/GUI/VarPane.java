// VarPane.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * A panel to edit a variable.
 * Variables may be of any other defined type,
 * and may include Int with arbitrary number of bits
 * as well as arrays.
 *
 * It is used as a sub-panel by several
 * panels in the editor.
 * It is initialized with a PanelUpdater in order
 * to update its parent.
 */
public class VarPane extends JPanel {
	//~ Instance fields --------------------------------------------------------

	// Data Model
	private Program program; // The program
	public Variable curVar = null; // The current edited variable

	// Parent updater
	PanelUpdater updater;

	// Graphic Components
	private JTextField varName;
	private JLabel     bitsLbl;
	private JTextField bitsFld;
	private JTextField arrayFld;
	private JComboBox  varType;
	private JCheckBox  arrayChk;
	private JButton    updateBtn;
	private JTextField comment;

	//~ Constructors -----------------------------------------------------------

	// Constructors

	/**
	 * Construct the VarPane
	 *
	 * @param prog The program
	 * @param updater The parent updater, or NULL.
	 */
	public VarPane(Program prog, PanelUpdater updater) {
		super();
		program          = prog;
		this.updater     = updater;

		// Components
		varName       = new JTextField(10);
		varType       = new JComboBox(program.types.toArray());
		bitsLbl       = new JLabel("Bits:");
		bitsFld       = new JTextField(3);
		arrayFld      = new JTextField(7);
		updateBtn     = new JButton("Update");
		arrayChk      = new JCheckBox("Array:", false);

		comment = new JTextField(20);

		varType.setMaximumSize(new Dimension(100, 23));
		varType.setPreferredSize(new Dimension(100, 23));

		updateBtn.setFont(updateBtn.getFont().deriveFont(Font.PLAIN, 11));
		updateBtn.setPreferredSize(new Dimension(76, 23));
		updateBtn.setMaximumSize(new Dimension(76, 23));

		varName.addActionListener(new UpdateListener());
		bitsFld.addActionListener(new UpdateListener());
		arrayFld.addActionListener(new UpdateListener());
		updateBtn.addActionListener(new UpdateListener());
		arrayChk.addChangeListener(new ArrayChkListener());
		varType.addActionListener(new UpdateListener());
		comment.addActionListener(new UpdateListener());

		// Layout
		Box    box = new Box(BoxLayout.Y_AXIS);

		JPanel pane1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pane2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pane3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pane4 = new JPanel(new FlowLayout(FlowLayout.LEFT));

		pane1.add(new JLabel("Name:"));
		pane1.add(varName);
		pane1.add(updateBtn);

		pane2.add(new JLabel("Type:"));
		pane2.add(varType);
		pane2.add(bitsLbl);
		pane2.add(bitsFld);

		pane3.add(arrayChk);
		pane3.add(arrayFld);

		pane4.add(new JLabel("Description:"));
		pane4.add(comment);

		box.add(pane1);
		box.add(pane2);
		box.add(pane3);
		box.add(pane4);

		setLayout(new GridLayout(1, 1));
		add(box);
	}

	//~ Methods ----------------------------------------------------------------

	/**
	 * Set the variable to edit
	 *
	 * @param var The variable to edit
	 */
	public void setVar(Variable var) {
		clear();

		curVar = var;

		varName.setText(var.nameDoc.getDoc());
		comment.setText(var.commentDoc.getDoc());
		varType.setSelectedIndex(program.types.indexOf(var.type));

		if (var.nArray.length() > 0) {
			arrayChk.setSelected(true);
			arrayFld.setText(var.nArray);
		}

		if (var.type == Type.INT) {
			bitsLbl.setEnabled(true);
			bitsFld.setEnabled(true);
			bitsFld.setText("" + var.nBits);
		}

		varName.requestFocus();
		varName.selectAll();
	}

	/**
	 * Clear the panel controls
	 */
	public void clear() {
		curVar = null;
		varName.setText("");
		comment.setText("");
		varType.setSelectedIndex(0);
		arrayChk.setSelected(false);
		arrayFld.setText("");
		bitsFld.setEnabled(false);
		bitsFld.setText("");
	}

	/**
	 * Enable/Disable the panel
	 *
	 * @param enabled true or false
	 */
	public void setEnabled(boolean enabled) {
		varName.setEnabled(enabled);
		comment.setEnabled(enabled);
		arrayFld.setEnabled(enabled);
		varType.setEnabled(enabled);
		arrayChk.setEnabled(enabled);
		updateBtn.setEnabled(enabled);

		bitsLbl.setEnabled(false);
		bitsFld.setEnabled(false);

		if (enabled && ((curVar != null) && (curVar.type == Type.INT))) {
			bitsLbl.setEnabled(true);
			bitsFld.setEnabled(true);
			bitsFld.setText(DEFAULT_BITS);
		}
	}

	/**
	 * Reread the type definitions from the program
	 */
	public void updateTypes() {
		varType.setModel(new DefaultComboBoxModel(program.types.toArray()));
	}

	//~ Inner Classes ----------------------------------------------------------

	// Listeners
	// Listener for the "Update" button
	class UpdateListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				Object o = e.getSource();

				if (o instanceof JComboBox) {
					JComboBox box = (JComboBox) o;

					if (box.getSelectedItem() == null) {
						return;
					}

					if (box.getSelectedItem().equals(Type.INT)) {
						bitsLbl.setEnabled(true);
						bitsFld.setEnabled(true);
					} else {
						bitsLbl.setEnabled(false);
						bitsFld.setEnabled(false);
						bitsFld.setText(DEFAULT_BITS);
					}
				} else {
					if (curVar == null) {
						return;
					}

					if (varName.getText().length() == 0) {
						return;
					}

					curVar.nameDoc.setDoc(varName.getText());
					curVar.commentDoc.setDoc(comment.getText());

					curVar.type = (Type) varType.getSelectedItem();

					if (arrayChk.isSelected() &&
						    (arrayFld.getText().length() > 0)) {
						curVar.nArray = arrayFld.getText();
					} else {
						curVar.nArray = "";
					}

					if (bitsFld.isEnabled() &&
						    (bitsFld.getText().length() > 0)) {
						curVar.nBits = Integer.parseInt(bitsFld.getText());
					} else {
						curVar.nBits = 0;
					}

					if (updater != null) {
						updater.updatePanel();
					}
				}
			} catch (ClassCastException ex) {
			} catch (NumberFormatException ex) {
			}
		}
	}

	// Listener for the array check-box
	class ArrayChkListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			try {
				JCheckBox c = (JCheckBox) e.getSource();

				if (c.isSelected()) {
					arrayFld.setEnabled(true);
				} else {
					arrayFld.setEnabled(false);
				}
			} catch (ClassCastException ex) {
			}
		}
	}

	//~ Static fields/initializers ---------------------------------------------

	// Constants
	private static final String DEFAULT_BITS = "8";
}
