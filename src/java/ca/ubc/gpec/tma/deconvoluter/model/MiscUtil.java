package ca.ubc.gpec.tma.deconvoluter.model;

/**
 * some misc helper functions
 * @author Samuel
 *
 */
public class MiscUtil {

	/**
	 * compare two cell values
	 * - if String, do equals
	 * - if numeric, convert to number and compare
	 * @param cellValue1
	 * @param cellValue2
	 * @return
	 */
	public static boolean checkEqual(String cellValue1, String cellValue2, boolean caseSensitive) {
		// see if they are numeric by casting them to double
		boolean isNumeric = false;
		double cellDoubleValue1 = 0;
		double cellDoubleValue2 = 0;
		boolean result = false;
		try {
			// the following will be parsed to a double but should not be 
			// left AS IS in this application because
			// e.g. "1345D" should be left as "1345D" NOT "1345"
			if (cellValue1.trim().endsWith("D") |
					cellValue1.trim().endsWith("d") |
					cellValue1.trim().endsWith("F") |
					cellValue1.trim().endsWith("f") |
					cellValue2.trim().endsWith("D") |
					cellValue2.trim().endsWith("d") |
					cellValue2.trim().endsWith("F") |
					cellValue2.trim().endsWith("f")
			) {
				throw new NumberFormatException("");
			}
			cellDoubleValue1 = Double.parseDouble(cellValue1);
			cellDoubleValue2 = Double.parseDouble(cellValue2);
			isNumeric = true;
		} catch (NumberFormatException nfe) {
			// no need to do anything
		}
		if (isNumeric) {
			result = (cellDoubleValue1 == cellDoubleValue2);
		} else {
			if (caseSensitive) {
				result = cellValue1.equals(cellValue2);
			} else {
				result = cellValue1.equalsIgnoreCase(cellValue2);
			}
		}
		return result;
	}
	
}
