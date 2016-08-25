package ca.ubc.gpec.tma.deconvoluter.reader;

import org.apache.poi.ss.usermodel.Cell;

/**
 * some helper functions ...
 * @author Samuel
 *
 */
public class ExcelReaderUtil {

	/**
	 * return value cast to String, returns null if blank
	 * @param cell
	 * @return
	 * @throws ExcelScoreSheetException if cell type is not numeric or string
	 */
	public static String getCellStringValue(Cell cell) throws ExcelScoreSheetReaderException {
		String result = null;
		String cellTypeName = "";
		int cellType = cell.getCellType();
		if (cellType == Cell.CELL_TYPE_NUMERIC) {
			cellTypeName = "CELL_TYPE_NUMERIC";
			Double cellNumericValue = cell.getNumericCellValue();
			if ((cellNumericValue - Math.round(cellNumericValue)) == 0) {
				result = ""+Math.round(cellNumericValue);
			} else {
				result = ""+cell.getNumericCellValue();
			}
		} else if (cellType == Cell.CELL_TYPE_STRING) {
			cellTypeName = "CELL_TYPE_STRING";
			//result = cell.getStringCellValue();
			result = cell.toString(); // this works better than getStringCellValue() 
			                          // e.g. "1345D" would give "1345" if using getStringCellValue()
			                          //      toString() would give "1345D"
		} else if (cellType == Cell.CELL_TYPE_BLANK) {
			cellTypeName = "CELL_TYPE_BLANK";
			result = "";
		} else {
			// this is not string or numeric; throw exception
			// find out the cell type:
			if (cellType == Cell.CELL_TYPE_BOOLEAN) {
				cellTypeName = "CELL_TYPE_BOOLEAN";
			} else if (cellType == Cell.CELL_TYPE_ERROR) {
				cellTypeName = "CELL_TYPE_ERROR";
			} else if (cellType == Cell.CELL_TYPE_FORMULA) {
				cellTypeName = "CELL_TYPE_FORMULA";
			}
			throw new ExcelScoreSheetReaderException("This cell type ("+cellTypeName+") is not supported - only string and numeric is supported");
		}
		//System.out.println(cellTypeName+" "+cell);
		return result;
	}
	
	/**
	 * force change excel cell type to string and get string value
	 * @param cell
	 * @return
	 * @throws ExcelScoreSheetReaderException
	 */
	public static String getCellForceStringValue(Cell cell) throws ExcelScoreSheetReaderException {
		cell.setCellType(Cell.CELL_TYPE_STRING);
		return (cell.getStringCellValue());
	}
	
}
