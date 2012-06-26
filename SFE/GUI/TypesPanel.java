// TypesPanel.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.PlainDocument;


/**
 * An Editor Panel.
 *
 * This panel is used in the Editor window
 * to edit type definitions.
 */
public class TypesPanel extends JPanel implements EditorPanel {
	//~ Instance fields --------------------------------------------------------

	// Class variables
	private Program program; // The program
	private JFrame  editWindow; // The editor window

	// Data model
	private DefaultListModel types; // The types
	private Type             selectedType = null; // Currently selected type

	// Graphic Components
	private JTextField   typeName;
	private JButton      newButton;
	private JButton      delButton;
	private JButton      updateButton;
	private JScrollPane  scroll;
	private JList        list;
	private ButtonGroup  radioButtons;
	private JRadioButton radioNone;
	private JRadioButton radioArray;
	private JRadioButton radioEnum;
	private JRadioButton radioStruct;
	private JComboBox    typeType;
	private JLabel       bitsLbl;
	private JLabel       arrayLbl;
	private JLabel       enumLbl;
	private JTextField   bits;
	private JTextField   array;
	private JTextField   enumData;
	private JScrollPane  membScroll;
	private JList        membList;
	private JButton      newMembBtn;
	private JButton      delMembBtn;
	private VarPane      membPane;
	private JPanel       structPane;
	private CardLayout   cards;

	//~ Constructors -----------------------------------------------------------

	// Constructors

	/**
	 * Construct the types panel
	 *
	 * @param program The program
	 * @param wnd The Editor window
	 */
	public TypesPanel(Program program, JFrame wnd) {
		super();
		this.program     = program;
		editWindow       = wnd;

		types = program.types;
		types.addListDataListener(new TypesListener());

		Box mainBox = new Box(BoxLayout.Y_AXIS);

		Box box      = new Box(BoxLayout.X_AXIS);
		Box rightBox = new Box(BoxLayout.Y_AXIS);
		Box leftBox  = new Box(BoxLayout.Y_AXIS);

		setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED),
		                                           "Types"));

		// Components
		list       = new JList(types);
		scroll     = new JScrollPane(list);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setPreferredSize(new Dimension(160, 100));

		list.addListSelectionListener(new ListListener());

		typeName         = new JTextField(10);
		newButton        = new JButton("New Type");
		delButton        = new JButton("Remove");
		updateButton     = new JButton("Update");

		newMembBtn     = new JButton("New Member");
		delMembBtn     = new JButton("Remove");

		membPane     = new VarPane(program, new Updater());

		typeType = new JComboBox(types.toArray());
		typeType.setMaximumSize(new Dimension(100, 25));
		typeType.setPreferredSize(new Dimension(100, 25));

		bits         = new JTextField(3);
		array        = new JTextField(7);
		enumData     = new JTextField(16);

		bitsLbl      = new JLabel(" Bits:");
		arrayLbl     = new JLabel(" Size:");
		enumLbl      = new JLabel(" Elements:");

		radioButtons     = new ButtonGroup();
		radioNone        = new JRadioButton("", true);
		radioArray       = new JRadioButton("Array", false);
		radioEnum        = new JRadioButton("Enum", false);
		radioStruct      = new JRadioButton("Struct", false);

		radioButtons.add(radioNone);
		radioButtons.add(radioArray);
		radioButtons.add(radioEnum);
		radioButtons.add(radioStruct);

		radioNone.addActionListener(new FormActionListener());
		radioArray.addActionListener(new FormActionListener());
		radioEnum.addActionListener(new FormActionListener());
		radioStruct.addActionListener(new FormActionListener());

		// some listeners
		newButton.addActionListener(new FormActionListener());
		delButton.addActionListener(new FormActionListener());
		updateButton.addActionListener(new FormActionListener());
		typeType.addActionListener(new FormActionListener());

		newMembBtn.addActionListener(new FormActionListener());
		delMembBtn.addActionListener(new FormActionListener());

		// Controls
		// Name panel
		JPanel namePane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		namePane.add(new JLabel("Name: "));
		namePane.add(typeName);

		// Buttons panel
		JPanel btnPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		btnPane.add(newButton);
		btnPane.add(delButton);

		// Type form
		JPanel typePane = new JPanel();
		typePane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED),
		                                                    "Type Definition"));

		Box    typeBox = new Box(BoxLayout.Y_AXIS);

		JPanel nonePane  = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel arrayPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel enumPane  = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel strPane   = new JPanel(new FlowLayout(FlowLayout.LEFT));

		nonePane.add(radioNone);
		nonePane.add(typeType);
		nonePane.add(bitsLbl);
		nonePane.add(bits);
		nonePane.add(Box.createHorizontalStrut(30));
		nonePane.add(updateButton);

		arrayPane.add(radioArray);
		arrayPane.add(arrayLbl);
		arrayPane.add(array);

		enumPane.add(radioEnum);
		enumPane.add(enumLbl);
		enumPane.add(enumData);

		strPane.add(radioStruct);

		typeBox.add(nonePane);
		typeBox.add(arrayPane);
		typeBox.add(enumPane);
		typeBox.add(strPane);

		typePane.add(typeBox);

		// Structure members panel
		cards     = new CardLayout();

		structPane = new JPanel(cards);

		JPanel inStructPane = new JPanel(new GridLayout(1, 1));
		inStructPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED),
		                                                        "Structure Members"));

		membList       = new JList(new DefaultListModel());
		membScroll     = new JScrollPane(membList);

		membScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		membScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		membScroll.setPreferredSize(new Dimension(140, 80));

		membList.addListSelectionListener(new MembListListener());

		Box    membBox      = new Box(BoxLayout.X_AXIS);
		Box    rightMembBox = new Box(BoxLayout.Y_AXIS);

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		p1.add(newMembBtn);
		p1.add(delMembBtn);

		rightMembBox.add(p1);
		rightMembBox.add(membScroll);

		membBox.add(membPane);
		membBox.add(rightMembBox);

		inStructPane.add(membBox);

		structPane.add(new JPanel(), PANEL_NOSTRUCT);
		structPane.add(inStructPane, PANEL_STRUCT);

		leftBox.add(btnPane);
		leftBox.add(namePane);
		leftBox.add(Box.createVerticalStrut(10));
		leftBox.add(typePane);

		// Right list box
		rightBox.add(new JLabel("Types:"));
		rightBox.add(scroll);

		box.add(leftBox);
		box.add(rightBox);

		mainBox.add(box);
		mainBox.add(structPane);

		// disable controls
		disableControls();

		add(mainBox);
	}

	//~ Methods ----------------------------------------------------------------

	// Public methods

	/**
	 * Reset the panel controls
	 */
	public void reset() {
		list.clearSelection();
	}

	/**
	 * Reread the types definitions.
	 */
	public void updateTypes() {
		typeType.setModel(new DefaultComboBoxModel(types.toArray()));
		membPane.updateTypes();
	}

	// Private methods

	/**
	 * Enable panel controls
	 */
	private void enableControls() {
		updateButton.setEnabled(true);

		typeName.setEnabled(true);

		radioNone.setEnabled(true);
		radioArray.setEnabled(true);
		radioEnum.setEnabled(true);
		radioStruct.setEnabled(true);

		enableType(TYPE_NONE);
	}

	/**
	 * Enable panel controls
	 */
	private void enableType(int type) {
		bitsLbl.setEnabled(false);
		arrayLbl.setEnabled(false);
		enumLbl.setEnabled(false);

		typeType.setEnabled(false);
		array.setEnabled(false);
		enumData.setEnabled(false);

		switch (type) {
			case TYPE_DISABLE:
				break;

			case TYPE_ARRAY: {
				typeType.setEnabled(true);
				arrayLbl.setEnabled(true);
				array.setEnabled(true);
			}

			case TYPE_NONE: {
				typeType.setEnabled(true);

				if (typeType.getSelectedItem().equals(Type.INT)) {
					bitsLbl.setEnabled(true);
					bits.setEnabled(true);
				} else {
					bitsLbl.setEnabled(false);
					bits.setEnabled(false);
				}

				break;
			}

			case TYPE_ENUM: {
				enumData.setEnabled(true);
				enumLbl.setEnabled(true);

				break;
			}

			case TYPE_STRUCT:
				break;
		}
	}

	/**
	 * Disable panel controls
	 */
	private void disableControls() {
		updateButton.setEnabled(false);

		typeName.setEnabled(false);

		radioNone.setEnabled(false);
		radioArray.setEnabled(false);
		radioEnum.setEnabled(false);
		radioStruct.setEnabled(false);

		enableType(TYPE_DISABLE);

		disableMembs();
	}

	/**
	 * Hide struct members panel
	 */
	private void disableMembs() {
		cards.show(structPane, PANEL_NOSTRUCT);

		membList.setModel(new DefaultListModel());

		newMembBtn.setEnabled(false);
		delMembBtn.setEnabled(false);
		membList.setEnabled(false);
		membPane.clear();
		membPane.setEnabled(false);
	}

	/**
	 * Show struct members panel
	 */
	private void enableMembs() {
		newMembBtn.setEnabled(true);
		delMembBtn.setEnabled(true);
		membList.setEnabled(true);
		membPane.setEnabled(true);
	}

	/**
	 * Clear all panel controls
	 */
	private void clearControls() {
		selectedType = null;
		typeName.setDocument(new PlainDocument());
		enumData.setText("");
		array.setText("");
		bits.setText("");
		radioNone.setSelected(true);
	}

	/**
	 * Update all controls with a given (selected) type
	 *
	 * @param t The selected type
	 */
	private void updateControls(Type t) {
		clearControls();

		cards.show(structPane, PANEL_NOSTRUCT);

		if (t == null) {
			return;
		}

		selectedType = t;
		typeName.setDocument(t.nameDoc);

		if (t.isPrimitive()) {
			radioNone.setSelected(true);
			disableControls();

			return;
		}

		if (t.isMust()) {
			typeName.setEnabled(false);
		}

		if (t.isImmutable()) {
			disableControls();
		}

		t.nameDoc.addDocumentListener(new DocListener());

		if (t.type != null) {
			typeType.setSelectedIndex(types.indexOf(t.type));
		}

		if ((t.type != null) && t.type.equals(Type.INT) && (t.nBits > 0)) {
			bits.setText("" + t.nBits);
		} else if (t.isArray()) {
			radioArray.setSelected(true);
			enableType(TYPE_ARRAY);
			array.setText("" + t.nArray);
		} else if (t.isEnum()) {
			radioEnum.setSelected(true);
			enableType(TYPE_ENUM);
			enumData.setText(t.enumVals);
		} else if (t.isStruct()) {
			radioStruct.setSelected(true);
			enableType(TYPE_STRUCT);

			cards.show(structPane, PANEL_STRUCT);

			enableMembs();
			membList.setModel(t.structVars);
			membPane.clear();
			membPane.setEnabled(false);
		}
	}

	//~ Inner Classes ----------------------------------------------------------

	/*
	 * Update current panel.
	 * Send this to children panels for them to update this
	 */
	public class Updater implements PanelUpdater {
		public void updatePanel() {
			membList.repaint();
			list.repaint();
			program.updateDoc();
		}
	}

	// Listeners
	// DocumentListener for type name
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

	// ListListener for types list
	class ListListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			try {
				JList l = (JList) e.getSource();

				if (list.getSelectedIndex() != -1) {
					enableControls();

					Type t = (Type) list.getSelectedValue();
					updateControls(t);
				} else {
					clearControls();
					disableControls();
				}
			} catch (ClassCastException ex) {
			}
		}
	}

	// ListListener for members list
	class MembListListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			try {
				JList alist = (JList) e.getSource();

				program.updateDoc();
				alist.repaint();

				if (alist.getSelectedIndex() != -1) {
					membPane.setEnabled(true);
					membPane.setVar((Variable) alist.getSelectedValue());
				} else {
					membPane.clear();
					membPane.setEnabled(false);
				}
			} catch (ClassCastException ex) {
			}
		}
	}

	// DataListener for type list
	class TypesListener implements ListDataListener {
		public void contentsChanged(ListDataEvent e) {
			try {
				DefaultListModel m = (DefaultListModel) e.getSource();
				((EditWnd) editWindow).updateTypes();
			} catch (ClassCastException ex) {
			}
		}

		public void intervalAdded(ListDataEvent e) {
			try {
				DefaultListModel m = (DefaultListModel) e.getSource();
				((EditWnd) editWindow).updateTypes();
			} catch (ClassCastException ex) {
			}
		}

		public void intervalRemoved(ListDataEvent e) {
			try {
				DefaultListModel m = (DefaultListModel) e.getSource();
				((EditWnd) editWindow).updateTypes();
			} catch (ClassCastException ex) {
			}
		}
	}

	// Action listener for all controls
	class FormActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				Object o = e.getSource();

				if (o instanceof JButton) {
					JButton btn = (JButton) o;

					if (btn == newButton) {
						// Add new default type
						Type newType =
							new Type(DEFAULT_TYPE_NAME, Type.INT,
							         Type.DEFAULT_INT_BITS, "");

						int  index = 1;

						while (types.contains(newType)) {
							newType.nameDoc.setDoc(DEFAULT_TYPE_NAME + "_" +
							                       index);
							index++;
						}

						if (! types.contains(newType)) {
							types.addElement(newType);
							list.setSelectedIndex(types.getSize() - 1);
							enableControls();
							updateControls(newType);

							typeName.requestFocus();
							typeName.selectAll();
						} else {
							new MessageBox(editWindow, "Error",
							               "Type " + DEFAULT_TYPE_NAME +
							               " already exists!");
						}
					} else if (btn == delButton) {
						int selIndex = list.getSelectedIndex();

						if ((selIndex != -1) &&
							    ! ((Type) list.getSelectedValue()).isMust()) {
							Type t = (Type) list.getSelectedValue();

							types.removeElement(t);

							if (selIndex < types.getSize()) {
								list.setSelectedIndex(selIndex);
							} else if (types.getSize() > 0) {
								list.setSelectedIndex(selIndex - 1);
							}
						}

						// clear form
						if (list.getSelectedIndex() == -1) {
							clearControls();
							disableControls();
						}
					} else if (btn == updateButton) {
						if (selectedType == null) {
							return;
						}

						if (selectedType.isPrimitive()) {
							return;
						}

						// update type
						// get bits & array
						int b = 0;

						if (bits.isEnabled() && (bits.getText().length() > 0)) {
							try {
								b = Integer.parseInt(bits.getText());
							} catch (NumberFormatException ex) {
							}
						}

						String a = "";

						if (array.isEnabled() &&
							    (array.getText().length() > 0)) {
							a = array.getText();
						}

						if (radioNone.isSelected()) {
							if (typeType.getSelectedItem().equals(Type.INT) &&
								    (b > 0)) {
								selectedType.setBits(b);
							} else if (! selectedType.equals(typeType.getSelectedItem())) {
								selectedType.setType((Type) typeType.getSelectedItem());
							}
						} else if (radioArray.isSelected() && (a.length() > 0)) {
							if (! selectedType.equals(typeType.getSelectedItem())) {
								selectedType.setArray((Type) typeType.getSelectedItem(),
								                      b, a);
							}
						} else if (radioEnum.isSelected() &&
							           (enumData.getText().length() > 0)) {
							selectedType.setEnum(enumData.getText());
						} else if (radioStruct.isSelected()) {
							if (! selectedType.struct) {
								selectedType.setStruct(new DefaultListModel());
							}
						} else {
							// type not changed
						}

						updateControls(selectedType);
						program.updateDoc();
						list.repaint();
					} else if (btn == newMembBtn) {
						if (selectedType == null) {
							return;
						}

						if (! selectedType.struct) {
							return;
						}

						Variable newMemb =
							new Variable(DEFAULT_MEMBNAME, Type.INT, "",
							             Type.DEFAULT_INT_BITS);

						int      index = 1;

						while (selectedType.structVars.contains(newMemb)) {
							newMemb.nameDoc.setDoc(DEFAULT_MEMBNAME + "_" +
							                       index);
							index++;
						}

						if (! selectedType.structVars.contains(newMemb)) {
							selectedType.structVars.addElement(newMemb);
							program.updateDoc();
							membList.repaint();
							list.repaint();
							membList.setSelectedIndex(selectedType.structVars.getSize() -
							                          1);
						} else {
							new MessageBox(editWindow, "Error",
							               "Member " + DEFAULT_MEMBNAME +
							               " already exists");
						}
					} else if (btn == delMembBtn) {
						if (selectedType == null) {
							return;
						}

						if (! selectedType.struct) {
							return;
						}

						int selIndex = membList.getSelectedIndex();

						if (selIndex != -1) {
							if (((Variable) membList.getSelectedValue()).must) {
								return;
							}

							selectedType.structVars.removeElement(membList.getSelectedValue());

							if (selIndex < selectedType.structVars.getSize()) {
								membList.setSelectedIndex(selIndex);
							} else if (selectedType.structVars.getSize() > 0) {
								membList.setSelectedIndex(selIndex - 1);
							}

							program.updateDoc();
							membList.repaint();
							list.repaint();
						} else {
							new MessageBox(editWindow, "Error",
							               " Select struct member to remove ");
						}
					}
				}

				if (o instanceof JCheckBox) {
					JCheckBox cb = (JCheckBox) o;

					if (cb.isSelected()) {
						arrayLbl.setEnabled(true);
						array.setEnabled(true);
					} else {
						arrayLbl.setEnabled(false);
						array.setEnabled(false);
						array.setText("");
					}
				}

				if (o instanceof JComboBox) {
					JComboBox box = (JComboBox) o;

					if (box.getSelectedItem() == null) {
						return;
					}

					if (box.getSelectedItem().equals(Type.INT)) {
						bitsLbl.setEnabled(true);
						bits.setEnabled(true);
						bits.setText("" + Type.DEFAULT_INT_BITS);
					} else {
						bitsLbl.setEnabled(false);
						bits.setEnabled(false);
						bits.setText("");
					}
				}

				if (o instanceof JRadioButton) {
					// enable selected control
					JRadioButton btn = (JRadioButton) o;

					if (btn == radioNone) {
						enableType(TYPE_NONE);
					}

					if (btn == radioArray) {
						enableType(TYPE_ARRAY);
					}

					if (btn == radioEnum) {
						enableType(TYPE_ENUM);
					}

					if (btn == radioStruct) {
						enableType(TYPE_STRUCT);
					}
				}
			} catch (ClassCastException ex) {
			}
		}
	}

	//~ Static fields/initializers ---------------------------------------------

	// Constants
	private static final int    TYPE_DISABLE      = -1;
	private static final int    TYPE_NONE         = 0;
	private static final int    TYPE_ARRAY        = 1;
	private static final int    TYPE_ENUM         = 2;
	private static final int    TYPE_STRUCT       = 3;
	private static final String PANEL_NOSTRUCT    = "no";
	private static final String PANEL_STRUCT      = "yes";
	private static final String DEFAULT_TYPE_NAME = "_t";
	private static final String DEFAULT_MEMBNAME  = "_m";
}
