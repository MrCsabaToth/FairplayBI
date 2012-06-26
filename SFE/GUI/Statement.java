// Statement.java.
// Copyright (C) 2004 Dahlia Malkhi, Dudi Einey.
// See full copyright license terms in file ../GPL.txt

package SFE.GUI;


/**
 * A statement in a function code.
 *
 * The statement is a line of code,
 * with information about indentation level.
 */
public class Statement {
	//~ Instance fields --------------------------------------------------------

	// Class state variables
	public int      level     = 0; // Current indentation level
	public int      nextLevel = 0; // Indentation of next line
	public String   code; // The line of code
	public int      ident     = 4; // Indentation size
	private boolean output    = true; // output this statement in code

	//~ Constructors -----------------------------------------------------------

	public Statement(String code) {
		this.code = code;
	}

	/**
	 * Build a new statement
	 *
	 * @param level Indentation level
	 * @param code The code line
	 */
	public Statement(int level, String code) {
		if (level >= 0) {
			this.level     = level;
			nextLevel      = level;
		}

		this.code = code;
	}

	/**
	 * Build a new statement
	 *
	 * @param level Indentation level
	 * @param code The code line
	 */
	public Statement(int level, int nextLevel, String code) {
		if (nextLevel >= 0) {
			this.nextLevel = nextLevel;
		}

		if (level >= 0) {
			this.level = level;
		}

		this.code = code;
	}

	/**
	 * Build a new statemenet
	 *
	 * @param level Indentation level
	 * @param nextLevel The level of the following line
	 * @param code The code line
	 * @param output Wether to output this line in the code
	 */
	public Statement(int level, int nextLevel, String code, boolean output) {
		if (nextLevel >= 0) {
			this.nextLevel = nextLevel;
		}

		if (level >= 0) {
			this.level = level;
		}

		this.code       = code;
		this.output     = output;
	}

	//~ Methods ----------------------------------------------------------------

	// Public methods

	/**
	 * Set indentation level
	 *
	 * @param ident indentation level
	 */
	public void setIdent(int ident) {
		this.ident = ident;
	}

	/**
	 * Get indentation level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Get next-line indentation level
	 */
	public int getNextLevel() {
		return nextLevel;
	}

	/**
	 * Get the string representation of the statement
	 */
	public String toString() {
		String res = "";

		for (int l = 0; l < level; l++)
			for (int i = 0; i < ident; i++)
				res = res + " ";

		res += code;

		return res;
	}
}
;
