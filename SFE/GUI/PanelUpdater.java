// PanelUpdater.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

/**
 * PanelUpdater interface
 *
 * It is used by a Panel, which may submit them to child
 * components allowing them to update the panel.
 *
 * The PanelUpdater should be an inner class of a panel.
 */
package SFE.GUI;

interface PanelUpdater {
	//~ Methods ----------------------------------------------------------------

	/**
	 * Update the panel
	 */
	public void updatePanel();
}
