package ca.ubc.gpec.tma.deconvoluter.reader;

import java.util.Set;

import ca.ubc.gpec.tma.deconvoluter.model.Case;
import ca.ubc.gpec.tma.deconvoluter.model.CorePosition;

/**
 * API for reading score sheets
 * @author Samuel
 *
 */
public interface ScoreSheetReader {

	/** 
	 * get file name this reader is reading
	 * @return
	 */
	public String getFilename();

	/**
	 * read sector map
	 * put core into caseIds
	 * @param cases
	 * @param caseSensitive
	 * @param lookupFileReader
	 */
	public void readSectorMap(Set<Case> cases, boolean caseSensitive) throws ScoreSheetReaderException;

	/**
	 * get available biomarkers in this score sheet
	 * @return
	 */
	Set<String> getAvailableBiomarkerNames();
	
	/**
	 * get the score of the core corresponding to marker
	 * @param markerName
	 * @param row
	 * @param column
	 * @return
	 */
	public String getScore(String markerName, CorePosition position) throws ScoreSheetReaderException;
	
	/**
	 * get the score of the core corresponding to the marker transposing the row number
	 * @param markerName
	 * @param position
	 * @param transposeRow
	 * @return
	 */
	public String getScore(String markerName, CorePosition position, int transposeRow) throws ScoreSheetReaderException;

}
