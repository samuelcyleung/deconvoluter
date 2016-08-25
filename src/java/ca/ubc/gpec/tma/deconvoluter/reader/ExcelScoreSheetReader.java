package ca.ubc.gpec.tma.deconvoluter.reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;

import ca.ubc.gpec.tma.deconvoluter.model.Case;
import ca.ubc.gpec.tma.deconvoluter.model.CaseId;
import ca.ubc.gpec.tma.deconvoluter.model.Core;
import ca.ubc.gpec.tma.deconvoluter.model.CorePosition;

/**
 * read excel score sheet
 * 
 * @author Samuel
 * 
 */
public class ExcelScoreSheetReader implements ScoreSheetReader {
	private static final int SECTOR_MAP_WORKSHEET_INDEX = 0;
	private static final String SECTOR_HEADER_SEARCH_STRING = ".*(S|s)(E|e)(C|c)(T|t)(O|o)(R|r).*";

	private String filename;
	private Sheet sectorMapWorksheet;
	private Workbook scoreSheetWorkbook;
	private ArrayList<Integer> sectorHeaderRowNums;
	private ArrayList<Integer> sectorHeaderColNums;

	public ExcelScoreSheetReader(String filename)
			throws ExcelScoreSheetReaderException {

		try {
			ExcelScoreSheetReader essr = new ExcelScoreSheetReader(
					new FileInputStream(filename), filename);
			this.filename = essr.filename;
			this.sectorMapWorksheet = essr.sectorMapWorksheet;
			this.scoreSheetWorkbook = essr.scoreSheetWorkbook;
			this.sectorHeaderRowNums = essr.sectorHeaderRowNums;
			this.sectorHeaderColNums = essr.sectorHeaderColNums;

		} catch (FileNotFoundException fnfe) {
			throw new ExcelScoreSheetReaderException(
					"Exception encountered when trying to read " + filename
							+ ": " + fnfe);
		}
	}

	/**
	 * constructor
	 * 
	 * @param inp
	 * @param filename
	 * @throws ExcelScoreSheetReaderException
	 */
	public ExcelScoreSheetReader(InputStream inp, String filename)
			throws ExcelScoreSheetReaderException {

		this.filename = filename;
		this.sectorHeaderRowNums = new ArrayList<Integer>();
		this.sectorHeaderColNums = new ArrayList<Integer>();

		try {
			scoreSheetWorkbook = WorkbookFactory.create(inp);

			// check number of worksheet
			int numberOfSheets = scoreSheetWorkbook.getNumberOfSheets();
			if (numberOfSheets < 1) {
				throw new ExcelScoreSheetReaderException(
						"Cannot deconvolution because there is "
								+ numberOfSheets + " worksheet in file: "
								+ filename);
			}

			// get sectorMapWorksheet
			this.sectorMapWorksheet = scoreSheetWorkbook
					.getSheetAt(SECTOR_MAP_WORKSHEET_INDEX);

			// get sector header cell positions (if any)
			getSectorHeaderPositions();

		} catch (InvalidFormatException ife) {
			throw new ExcelScoreSheetReaderException(
					"Exception encountered when trying to read " + filename
							+ ": " + ife);
		} catch (IOException ioe) {
			throw new ExcelScoreSheetReaderException(
					"Exception encountered when trying to read " + filename
							+ ": " + ioe);
		}
	}

	/**
	 * check to see if this cell is a comment on the sector map instead of a
	 * case id
	 * 
	 * ASSUME all columns/rows of the header cell is considered a header
	 * column/row e.g. if "sector 1" is on cell C5, - all cells on column "C"
	 * and all cells on row "5" are considered header column/row
	 * 
	 * @return
	 */
	private boolean isHeader(int rowNum, int colNum) {
		return (sectorHeaderRowNums.contains(rowNum) | sectorHeaderColNums
				.contains(colNum));
	}

	/**
	 * get filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * get sector header positions need to traverse through the whole sheet once
	 */
	private void getSectorHeaderPositions() {
		for (int rowNum = 0; rowNum <= sectorMapWorksheet.getLastRowNum(); rowNum++) {
			// (sheet) getLastRowNum() is 0-based while (Row) getLastCellNum() is 1-based !!!
			Row row = sectorMapWorksheet.getRow(rowNum);
			if (row != null) {
				for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
					Cell cell = row.getCell(colNum);
					if (cell != null) {
						if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
							// only possible if it is string
							if (cell.getStringCellValue().matches(
									SECTOR_HEADER_SEARCH_STRING)) {
								// this must be a sector header cell
								sectorHeaderRowNums.add(rowNum);
								sectorHeaderColNums.add(colNum);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * read sector map put core into cases
	 * 
	 * @param cases
	 * @param caseSensitive
	 */
	public void readSectorMap(Set<Case> cases, boolean caseSensitive)
			throws ScoreSheetReaderException, ExcelScoreSheetReaderException {
		// construct a tree set of case ids for searching
		TreeSet<CaseId> caseIds = new TreeSet<CaseId>();
		Hashtable<CaseId, Case> casesHashtable = new Hashtable<CaseId, Case>();

		Iterator<Case> casesItr = cases.iterator();

		while (casesItr.hasNext()) {
			Case c = casesItr.next();
			CaseId caseId = c.getCaseId();
			// System.out.println(caseId);
			if (caseIds.contains(caseId)) {
				throw new ScoreSheetReaderException(
						"duplicate case found in input case array to readSectoMap");
			}
			caseIds.add(caseId);
			casesHashtable.put(caseId, c);
		}

		// iterate throw ALL cells in the sector map and see which one
		// corresponds to a case id
		for (int rowNum = 0; rowNum <= sectorMapWorksheet.getLastRowNum(); rowNum++) {
			// (sheet) getLastRowNum() is 0-based while (Row) getLastCellNum() is 1-based !!!
			Row row = sectorMapWorksheet.getRow(rowNum);
			if (row != null) {
				for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
					if (!isHeader(rowNum, colNum)) {
						// this is not a header row/column
						Cell cell = row.getCell(colNum);
						if (cell != null) {
							// check to see if this cell corresponds to a case
							// id:
							CaseId caseId = new CaseId(
									ExcelReaderUtil.getCellStringValue(cell),
									caseSensitive);
							if (caseIds.contains(caseId)) {
								// case id found!!!
								(casesHashtable.get(caseId)).addCore(new Core(
										new CorePosition(rowNum, colNum), // core
																			// position
																			// in
																			// excel
																			// coordinates
										new CorePosition(rowNum, colNum), // logical
																			// position
																			// TODO:
																			// need
																			// implement
										caseId, this));
							}
						}
					}
				}
			}
		}

	}

	/**
	 * read and return score from scoresheet
	 * 
	 * @param markerName
	 *            , position
	 */
	public String getScore(String markerName, CorePosition position)
			throws ScoreSheetReaderException {
		return getScore(markerName, position, 0);
	}

	public String getScore(String markerName, CorePosition position,
			int transposeRow) throws ScoreSheetReaderException {
		// get worksheet
		Sheet markerWorksheet = scoreSheetWorkbook.getSheet(markerName);

		if (markerWorksheet == null) {
			throw new ScoreSheetReaderException("score sheet for marker ("
					+ markerName + ") not found");
		}

		Cell cell = markerWorksheet.getRow(position.getRow() + transposeRow)
				.getCell(position.getColumn());

		if (cell == null) {
			return "";
		}

		return ExcelReaderUtil.getCellStringValue(cell);
	}

	/**
	 * return available biomarker names i.e. excel worksheet names
	 * @return
	 */
	public TreeSet<String> getAvailableBiomarkerNames() {
		TreeSet<String> sheetNames = new TreeSet<String>();
		
		if (scoreSheetWorkbook.getNumberOfSheets() == 1) {
			// if only one sheet in scoresheet, assume both sector map
			// and score sheet in the same worksheet ... i.e. there
			// must be a rowTranspose
			sheetNames.add(scoreSheetWorkbook.getSheetName(0));
		}
		
		for (int i=1; i<scoreSheetWorkbook.getNumberOfSheets(); i++) {
			// skip the first sheet - assume this must be a sector map
			Sheet sheet = scoreSheetWorkbook.getSheetAt(i);
			if(sheet.getLastRowNum() > 0) {
				// assume sheet with only one row is NOT a score sheet
				String sheetName = scoreSheetWorkbook.getSheetName(i);
				if (!sheetName.matches(SECTOR_HEADER_SEARCH_STRING)) {
					// make sure its not a sector map 
					// i.e. if sheet name matches *sector*
					//      assume this is a sector map
					sheetNames.add(sheetName);
				}
			}
		}
		return sheetNames;
	}
	/**
	 * main method for process test
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		/**
		try {
			ExcelLookupFileReader elfr = new ExcelLookupFileReader(
					"/Users/Samuel/Desktop/lookup.xls", true, false);

			ExcelScoreSheetReader essr = new ExcelScoreSheetReader(
					"/Users/Samuel/Desktop/blockA.xls");

			System.out.println("SECTOR 1"
					.matches(".*(S|s)(E|e)(C|c)(T|t)(O|o)(R|r).*"));

			System.out.println("SECTOR"
					.matches(".*(S|s)(E|e)(C|c)(T|t)(O|o)(R|r).*"));
			
			System.out.println("ok");
		} catch (ExcelScoreSheetReaderException essre) {
			System.err.println(essre);
		} catch (LookupFileReaderException elfre) {
			System.err.println(elfre.toString());
		}
		**/
		System.out.println("SECTOR 1"
				.matches(".*(S|s)(E|e)(C|c)(T|t)(O|o)(R|r).*"));

		System.out.println("Sector"
				.matches(".*(S|s)(E|e)(C|c)(T|t)(O|o)(R|r).*"));
		
		System.out.println("08-011 - Lung Carcinoma Rebuild - Sector Map Block A"
				.matches(".*(S|s)(E|e)(C|c)(T|t)(O|o)(R|r).*"));
		
	}
}
