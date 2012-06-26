// OutputWriter originally had a static Vector inputFormat = new Vector();
// which had two strings in it and several InputStatement
// Having such a hybrid Vector is ugly, so this class explicitly materializes that layout.
//
// Csaba Toth
package SFE.Compiler;

import java.util.Vector;

public class InputFormat {
	private String partyName;
	private String inputIntegerStr;
	private Vector<InputStatement> inputStatements;

	public InputFormat() {
		inputStatements = new Vector<InputStatement>();
		// TODO Auto-generated constructor stub
	}

	public String getPartyName() {
		return partyName;
	}

	public void setPartyName(String inputPartyName) {
		this.partyName = inputPartyName;
	}

	public String getIntegerStr() {
		return inputIntegerStr;
	}

	public void setIntegerStr(String inputIntegerStr) {
		this.inputIntegerStr = inputIntegerStr;
	}

	public Vector<InputStatement> getInputStatements() {
		return inputStatements;
	}

	public void setInputStatements(Vector<InputStatement> inputStatements) {
		this.inputStatements = inputStatements;
	}

	public void addInputStatement(InputStatement inputStatement) {
		inputStatements.add(inputStatement);
	}

}
