// FormatterError.java - Format error must be defined in a separate file.
// Copyright (C) 2004 Dahlia Malkhi, Yaron Sella. 
// See full copyright license terms in file ../GPL.txt

package SFE.BOAL;

public class FormatterError extends Exception {
	private static final long serialVersionUID = 3983733225921090467L;

	public FormatterError(String s) {
        super(s);

        // logger.error (s) ;
    }
}
