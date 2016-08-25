package ca.ubc.gpec.tma.deconvoluter.reader;
/**
 * exception encountered when reading lookup file in excel format
 * @author Samuel
 *
 */
public class ExcelLookupFileReaderException extends LookupFileReaderException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExcelLookupFileReaderException (String msg) {
		super(msg);
	}
}
