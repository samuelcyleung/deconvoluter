package ca.ubc.gpec.tma.deconvoluter.reader;
/**
 * exception encountered when reading lookup file
 * @author Samuel
 *
 */
public class LookupFileReaderException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LookupFileReaderException (String msg) {
		super(msg);
	}
}
