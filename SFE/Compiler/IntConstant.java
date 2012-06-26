// IntConstant.java.
// Copyright (C) 2004 Naom Nisan, Ziv Balshai, Amir Levy.
// See full copyright license terms in file ../GPL.txt

package SFE.Compiler;

import java.math.BigInteger;


/**
 * The IntConstant class represents integer consts expressions that can
 * appear in the program.
 */
public class IntConstant extends ConstExpression {
	//~ Instance fields --------------------------------------------------------

	// data members

	/*
	 * Holds the integer constant of this IntConstant
	 */
	private BigInteger intConst;

	/*
	 * Holds the number of bit needed to store this intConst (size)
	 */
	private int size;

	//~ Constructors -----------------------------------------------------------

	/**
	 * Constructs a new IntConstant from a given integer const
	 * @param intConst the given integer constant
	 */
	public IntConstant(BigInteger intConst) {
		this.intConst = intConst;

		size = intConst.bitLength() + 1;
	}

	//~ Methods ----------------------------------------------------------------

	/**
	 * Returns the number of bits needed to represent this expression.
	 * @return the number of bits needed to represent this expression.
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns a string representation of the object.
	 * @return a string representation of the object.
	 */
	public String toString() {
		return intConst.toString();
	}

	/**
	 * Returns the value stored in this IntConstant
	 * @return the value stored in this IntConstant
	 */
	public BigInteger value() {
		return intConst;
	}

	/**
	 * Returns Expression that represents the bit at place i of this Expression
	 * @return Expression that represents the bit at place i of this Expression
	 */
	public Expression bitAt(int i) {
		boolean val = intConst.testBit(i);

		return new BooleanConstant(val);
	}
}
