// EditWndPanel.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * This is the main panel for the Editor Window
 *
 * It uses different panels using cards-layout
 * to show the different edit windows to the user.
 */
public class EditWndPanel extends JPanel {
	//~ Instance fields --------------------------------------------------------

	// Class Variables
	private Program      program; // The program
	private JFrame       editWnd; // The Editor window
	private CodeWndPanel codePanel    = null; // The panel in the main window
	public ProgPanel     progPanel;
	public ConstPanel    constPanel;
	public TypesPanel    typesPanel;
	public FuncPanel     funcPanel;
	public EditVarPanel  editVarPanel;
	public EditPanel     editPanel;
	private JPanel       bottomPane;
	private CardLayout   cards;
	private HashMap      editPanels;

	//~ Constructors -----------------------------------------------------------

	/**
	 * Construct a new EditWndPanel
	 *
	 * @param program The Program
	 * @param editWnd The Editor Window
	 */
	public EditWndPanel(Program program, JFrame editWnd) {
		super();

		this.editWnd     = editWnd;
		this.program     = program;
		codePanel        = ((CodeWnd) ((EditWnd) editWnd).codeWnd).codeWndPanel;

		setLayout(new GridLayout(1, 1));

		// Layout
		bottomPane     = new JPanel();
		cards          = new CardLayout(10, 10);
		bottomPane.setLayout(cards);

		bottomPane.setMaximumSize(new Dimension(800, 800));

		// Panels
		progPanel        = new ProgPanel(program, editWnd);
		typesPanel       = new TypesPanel(program, editWnd);
		constPanel       = new ConstPanel(program, editWnd);
		funcPanel        = new FuncPanel(program, editWnd);
		editVarPanel     = new EditVarPanel(program, codePanel, editWnd);
		editPanel        = new EditPanel(program, codePanel, editWnd);

		bottomPane.add(progPanel, PROG_PANEL);
		bottomPane.add(typesPanel, TYPES_PANEL);
		bottomPane.add(constPanel, CONST_PANEL);
		bottomPane.add(funcPanel, FUNC_PANEL);
		bottomPane.add(editVarPanel, EDITVAR_PANEL);
		bottomPane.add(editPanel, EDIT_PANEL);

		editPanels = new HashMap();
		editPanels.put(PROG_PANEL, progPanel);
		editPanels.put(TYPES_PANEL, typesPanel);
		editPanels.put(CONST_PANEL, constPanel);
		editPanels.put(FUNC_PANEL, funcPanel);
		editPanels.put(EDITVAR_PANEL, editVarPanel);
		editPanels.put(EDIT_PANEL, editPanel);

		add(bottomPane);
	}

	//~ Methods ----------------------------------------------------------------

	// public methods

	/**
	 * Show the selected edit panel
	 *
	 * @param panel The name of the panel to show
	 */
	public void show(String panel) {
		Object o = editPanels.get(panel);

		if ((o != null) && o instanceof EditorPanel) {
			((EditorPanel) o).reset();
			cards.show(bottomPane, panel);
		}
	}

	//~ Static fields/initializers ---------------------------------------------

	// Constants
	public static final String PROG_PANEL    = "prog";
	public static final String TYPES_PANEL   = "types";
	public static final String CONST_PANEL   = "const";
	public static final String FUNC_PANEL    = "func";
	public static final String EDITVAR_PANEL = "editvar";
	public static final String EDIT_PANEL    = "edit";
	public static final int    N_EDIT_PANELS = 6;
}
