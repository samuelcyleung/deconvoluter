package ca.ubc.gpec.tma.deconvoluter.reader;

/**
 * exception thrown when something is wrong in reading score sheet
 * @author Samuel
 *
 */
public class ScoreSheetReaderException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ScoreSheetReaderException(String msg) {
		super(msg);
	}

}
