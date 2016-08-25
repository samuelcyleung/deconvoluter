package ca.ubc.gpec.tma.deconvoluter.reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import ca.ubc.gpec.tma.deconvoluter.model.CaseId;

public class ExcelLookupFileReader implements LookupFileReader {

	private static final String DEFAULT_CASE_ID_NAME = "case.id";

	private String caseIdName;
	private boolean caseIdCaseSensitive;
	TreeSet<CaseId> caseIds;

	public ExcelLookupFileReader(String filename, boolean hasHeader,
			boolean caseIdCaseSensitive) throws LookupFileReaderException,
			ExcelLookupFileReaderException, ExcelScoreSheetReaderException {

		try {
			ExcelLookupFileReader elfr = new ExcelLookupFileReader(
					new FileInputStream(filename), filename, hasHeader,
					caseIdCaseSensitive);
			this.caseIdName = elfr.caseIdName;
			this.caseIdCaseSensitive = elfr.caseIdCaseSensitive;
			this.caseIds = elfr.caseIds;

		} catch (FileNotFoundException fnfe) {
			throw new ExcelLookupFileReaderException(
					"Exception encountered when trying to read " + filename
							+ ": " + fnfe);
		}
	}

	/**
	 * file lookup
	 * 
	 * @param file
	 * @param hasHeader
	 * @param caseIdCaseSensitive
	 *            - are the caseId's case sensitive
	 * @throws ExcelLookupFileReaderException
	 */
	public ExcelLookupFileReader(InputStream inp, String filename,
			boolean hasHeader, boolean caseIdCaseSensitive)
			throws LookupFileReaderException, ExcelLookupFileReaderException,
			ExcelScoreSheetReaderException {

		this.caseIdCaseSensitive = caseIdCaseSensitive;

		try {
			Workbook wb = WorkbookFactory.create(inp);

			int numberOfSheets = wb.getNumberOfSheets();
			if (numberOfSheets != 1) {
				// see if there is only one non-empty worksheet
				int numNonEmptySheets = 0;
				for (int i=0; i<numberOfSheets; i++) {
					Sheet sheet = wb.getSheetAt(i);
					if (sheet.getLastRowNum() > 0) {
						numNonEmptySheets++;
					}
				}
				if (numNonEmptySheets > 1) {
					throw new ExcelLookupFileReaderException("Lookup file ("
						+ filename + ") must have only 1 non-empty worksheet but "
						+ numNonEmptySheets + " worksheet(s) found");
				}
			}

			Sheet sheet = wb.getSheetAt(0);

			int columnIndex = -1; // column index
			caseIdName = null;
			caseIds = new TreeSet<CaseId>();

			Iterator<Row> rowItr = sheet.rowIterator();
			while (rowItr.hasNext()) {
				Row row = rowItr.next();

				if (columnIndex == -1) {
					// find column index and caseIdName

					Iterator<Cell> cellItr = row.cellIterator();

					while (cellItr.hasNext()) {
						Cell cell = cellItr.next();
						String cellValue = ExcelReaderUtil
								.getCellStringValue(cell);
						if (cellValue != null) {
							cellValue = cellValue.trim();
							// non null cell found!!!
							if (caseIdName == null) {
								// caseIdName not set ... set it now
								if (hasHeader) {
									caseIdName = cellValue;
								} else {
									caseIdName = DEFAULT_CASE_ID_NAME;
									// this is a case id
									// no need to check if its exist already because
									// this is the first case id!!!
									caseIds.add(new CaseId(cellValue.trim(), caseIdCaseSensitive));
								}
								columnIndex = cell.getColumnIndex();
							}
						}
					}
				} else {
					// just read value at columnIndex and ignore everything
					// else!
					Cell cell = row.getCell(columnIndex);
					String cellValue = ExcelReaderUtil.getCellStringValue(cell);
					if (cellValue != null) {
						// non null cell found!!!
						CaseId caseId = new CaseId(cellValue.trim(),
								caseIdCaseSensitive);
						// System.out.println("adding ..."+caseId);
						if (!caseIds.contains(caseId)) {
							// DO NOT import duplicate case ... just unique case
							// System.out.println("adding case #: "+caseId);
							caseIds.add(caseId);
						} else {
							// Iterator<CaseId> caseIdItr = caseIds.iterator();
							// while(caseIdItr.hasNext()) {
							// System.out.println(caseIdItr.next()+"\n");
							// }
							throw new LookupFileReaderException(
									"please remove duplicate case id in lookup file: "
											+ caseId);
						}
					}
				}
			}

		} catch (InvalidFormatException ife) {
			throw new ExcelLookupFileReaderException(
					"Exception encountered when trying to read " + filename
							+ ": " + ife);
		} catch (IOException ioe) {
			throw new ExcelLookupFileReaderException(
					"Exception encountered when trying to read " + filename
							+ ": " + ioe);
		}
	}

	/**
	 * check to see if this is a valid caseId
	 */
	public boolean isValidCaseId(String caseId) {
		return caseIds.contains(new CaseId(caseId, caseIdCaseSensitive));
	}

	/**
	 * returns iterator of case id in lookup file
	 */
	public Set<CaseId> getCaseIds() {
		return caseIds;
	}

	public int getNumCaseIds() {
		return caseIds.size();
	}

	/**
	 * return caseIdName
	 */
	public String getCaseIdName() {
		return caseIdName;
	}

	/**
	 * main method for process testing
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ExcelLookupFileReader elfr = new ExcelLookupFileReader(
					"/Users/Samuel/Desktop/cd163/lookup.xls", true, false);

			System.out.println(elfr.isValidCaseId("12"));
			System.out.println(elfr.isValidCaseId("asdfasdf"));

			System.out.println((new CaseId("123D", false))
					.compareTo(new CaseId("123", false)));
			System.out.println("ok");
		} catch (LookupFileReaderException elfre) {
			System.err.println(elfre.toString());
		} catch (ExcelScoreSheetReaderException essre) {
			System.err.println(essre);
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
