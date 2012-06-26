// CodeWndPanel.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * The Panel of the main window (CodeWnd)
 */
public class CodeWndPanel extends JPanel {
	//~ Instance fields --------------------------------------------------------

	// Variables
	private Program program; // The program
	private JFrame  codeWnd; // The main window
	private JFrame  editWnd; // The editor window
	boolean         editingFunction = false; // Is editing a function

	// Graphic Components
	public JScrollPane progScroll;
	public JTextArea   progText;
	private JButton    progBtn;
	private JButton    typesBtn;
	private JButton    constBtn;
	private JButton    funcBtn;
	private JButton    editVarBtn;
	private JButton    editBtn;
	private JCheckBox  showFunc;
	private JButton    abortBtn;
	private JButton    saveBtn;
	private JButton    runBtn;

	//~ Constructors -----------------------------------------------------------

	/**
	 * Construct the panel
	 *
	 * @param program The program
	 * @param codeWnd The main (containing) window
	 */
	public CodeWndPanel(Program program, JFrame codeWnd) {
		super();

		this.codeWnd     = codeWnd;
		this.program     = program;

		program.setCodePanel(this);

		setLayout(new GridLayout(1, 1));

		Box box = new Box(BoxLayout.Y_AXIS);

		// Top half
		JPanel topPane = new JPanel();
		topPane.setLayout(new BorderLayout(5, 5));
		topPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		// Main Text Area - Program/Function editor
		progText = new JTextArea(program.programDoc);

		progText.setEditable(false);
		progScroll = new JScrollPane(progText);
		progScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		progScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		progScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
		                                                      PROGRAM_TITLE));
		progScroll.setMinimumSize(new Dimension(400, 200));
		progScroll.setPreferredSize(new Dimension(450, 500));

		// Buttons
		progBtn      = new JButton(" Program  ");
		typesBtn     = new JButton("Type Defs");
		constBtn     = new JButton("Constants");

		funcBtn        = new JButton("Add Func");
		editVarBtn     = new JButton("Variables");
		editBtn        = new JButton("Edit Code");

		abortBtn     = new JButton("   Abort  ");
		saveBtn      = new JButton("    Save   ");
		runBtn       = new JButton("    Run    ");

		showFunc = new JCheckBox("Zoom on edited functions", true);

		progBtn.addActionListener(new ButtonsListener());
		typesBtn.addActionListener(new ButtonsListener());
		constBtn.addActionListener(new ButtonsListener());
		funcBtn.addActionListener(new ButtonsListener());
		editVarBtn.addActionListener(new ButtonsListener());
		editBtn.addActionListener(new ButtonsListener());
		abortBtn.addActionListener(new ButtonsListener());
		saveBtn.addActionListener(new ButtonsListener());
		runBtn.addActionListener(new ButtonsListener());
		showFunc.addChangeListener(new CheckboxListener());

		Box buttonsBox = new Box(BoxLayout.Y_AXIS);
		buttonsBox.add(Box.createVerticalStrut(30));
		buttonsBox.add(new JLabel("Global:"));
		buttonsBox.add(Box.createVerticalStrut(10));
		buttonsBox.add(progBtn);
		buttonsBox.add(Box.createVerticalStrut(5));
		buttonsBox.add(constBtn);
		buttonsBox.add(Box.createVerticalStrut(5));
		buttonsBox.add(typesBtn);
		buttonsBox.add(Box.createVerticalStrut(30));
		buttonsBox.add(new JLabel("Functions:"));
		buttonsBox.add(Box.createVerticalStrut(10));
		buttonsBox.add(funcBtn);
		buttonsBox.add(Box.createVerticalStrut(5));
		buttonsBox.add(editVarBtn);
		buttonsBox.add(Box.createVerticalStrut(5));
		buttonsBox.add(editBtn);

		buttonsBox.add(Box.createVerticalStrut(60));
		buttonsBox.add(abortBtn);
		buttonsBox.add(saveBtn);
		buttonsBox.add(runBtn);

		topPane.add(Box.createVerticalStrut(7), BorderLayout.NORTH);
		topPane.add(progScroll, BorderLayout.CENTER);
		topPane.add(buttonsBox, BorderLayout.EAST);
		topPane.add(showFunc, BorderLayout.SOUTH);

		box.add(topPane);
		add(box);
	}

	//~ Methods ----------------------------------------------------------------

	// Public methods

	/**
	 * Set the Editor window handle
	 *
	 * @param editWnd the editor window
	 */
	public void setEditWnd(JFrame editWnd) {
		this.editWnd = editWnd;
	}

	/**
	 * View the whole program in the window
	 */
	public void setProgramCode() {
		if (editingFunction) {
			progText.setDocument(program.programDoc);
			progScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
			                                                      PROGRAM_TITLE));
			editingFunction = false;
		}
	}

	/**
	 * View a selected function in the window
	 *
	 * @param f The function to show
	 */
	public void setFunctionCode(Function f) {
		if (showFunc.isSelected()) {
			f.updateDoc();
			progText.setDocument(f.functionDoc);
			progScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
			                                                      "Editing Function " +
			                                                      f.nameDoc.getDoc()));
			editingFunction = true;
		}
	}

	//~ Inner Classes ----------------------------------------------------------

	// Listeners
	// ActionListener for panel controls
	class ButtonsListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				JButton btn = (JButton) e.getSource();

				if (editWnd == null) {
					return;
				}

				if (! editWnd.isVisible()) {
					editWnd.setVisible(true);
				}

				editWnd.toFront();
				editWnd.setState(Frame.NORMAL);

				EditWndPanel editor = ((EditWnd) editWnd).editWndPanel;

				if (btn == progBtn) {
					editor.show(EditWndPanel.PROG_PANEL);
					setProgramCode();
				} else if (btn == typesBtn) {
					editor.show(EditWndPanel.TYPES_PANEL);
					setProgramCode();
				} else if (btn == constBtn) {
					editor.show(EditWndPanel.CONST_PANEL);
					setProgramCode();
				} else if (btn == funcBtn) {
					editor.show(EditWndPanel.FUNC_PANEL);
					setProgramCode();
				} else if (btn == editVarBtn) {
					editor.show(EditWndPanel.EDITVAR_PANEL);
				} else if (btn == editBtn) {
					editor.show(EditWndPanel.EDIT_PANEL);
				} else if (btn == abortBtn) {
					System.exit(0);
				} else if (btn == saveBtn) {
					String      str  = program.generateCode();
					PrintWriter file =
						new PrintWriter(new FileWriter(program.outputFilename));
					file.print(str);
					file.close();
				} else if (btn == runBtn) {
					String      str  = program.generateCode();
					PrintWriter file =
						new PrintWriter(new FileWriter(program.outputFilename));
					file.print(str);
					file.close();

					codeWnd.dispose();
					editWnd.dispose();

					// Signal to GUImain to continue
					synchronized (program.globalLock) {
						program.globalLock.notify();
					}
				}
			} catch (ClassCastException ex) {
			} catch (IOException ioex) {
			}
		}
	}

	// CheckboxListener for "Zoom" checkbox
	class CheckboxListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			try {
				JCheckBox c = (JCheckBox) e.getSource();

				if ((c == showFunc) && (! c.isSelected())) {
					setProgramCode();
				}
			} catch (ClassCastException ex) {
			}
		}
	}

	//~ Static fields/initializers ---------------------------------------------

	// Constants
	public static final String PROGRAM_TITLE = "Program Code";
}
