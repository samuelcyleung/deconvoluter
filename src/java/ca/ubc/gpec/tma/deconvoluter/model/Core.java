package ca.ubc.gpec.tma.deconvoluter.model;

import ca.ubc.gpec.tma.deconvoluter.reader.ScoreSheetReader;
import ca.ubc.gpec.tma.deconvoluter.reader.ScoreSheetReaderException;

/**
 * represents a core
 * @author Samuel
 *
 */
public class Core implements Comparable<Core> {

	private CorePosition scoreSheetPosition; // i.e. coordinates in excel file
	private CorePosition logicalPosition; // i.e. row / col # 
	private CaseId caseId;
	private ScoreSheetReader scoreSheetReader;
	
	/**
	 * constructor
	 * @param row
	 * @param column
	 */
	public Core (
			CorePosition scoreSheetPosition,
			CorePosition logicalPosition,
			CaseId caseId, 
			ScoreSheetReader scoreSheetReader) {
		this.scoreSheetPosition = scoreSheetPosition;
		this.logicalPosition = logicalPosition;
		this.caseId = caseId;
		this.scoreSheetReader = scoreSheetReader;
	}
	
	/**
	 * set scoreSheetPosition
	 * @param scoreSheetPosition
	 */
	public void setPosition(CorePosition scoreSheetPosition) {this.scoreSheetPosition = scoreSheetPosition;}
	
	/**
	 * set logical core position
	 * @param logicalPosition
	 */
	public void setLogicalPosition(CorePosition logicalPosition) {this.logicalPosition = logicalPosition;}
	
	/**
	 * set case ID
	 * @param caseId
	 */
	public void setCaseId(CaseId caseId) {this.caseId = caseId;}
	
	/**
	 * set score sheet reader
	 * @param scoreSheetReader
	 */
	public void setScoreSheetReader(ScoreSheetReader scoreSheetReader) {this.scoreSheetReader = scoreSheetReader;}
	
	/**
	 * get score sheet position
	 * @return
	 */
	public CorePosition getScoreSheetPosition() {return scoreSheetPosition;}
	
	/**
	 * get logical position
	 * @return
	 */
	public CorePosition getLogicalPosition() {return logicalPosition;}
	
	/**
	 * get Case Id
	 * @return
	 */
	public CaseId getCaseId() {return caseId;}

	/**
	 * get score sheet reader
	 * @return
	 */
	public ScoreSheetReader getScoreSheetReader() {return scoreSheetReader;}
	
	public String getScore(String markerName) throws ScoreSheetReaderException {
		return getScore(markerName, 0);
	}

	public String getScore(String markerName, int transposeRow) throws ScoreSheetReaderException {
		//System.out.print("getting score for "+caseId);
		//System.out.println(" at position "+scoreSheetPosition);
		return scoreSheetReader.getScore(markerName, scoreSheetPosition, transposeRow);
	}
	/**
	 * override equals for hashtable
	 */
	public boolean equals(Object obj) {
		if (this == obj) {return true;}
		if ( !(obj.getClass() == this.getClass())) {return false;}
		Core testCore = (Core)obj;
		return (
			this.caseId == testCore.getCaseId() &
			this.getScoreSheetPosition().equals(testCore.getScoreSheetPosition()) &
			this.scoreSheetReader.getFilename().equals(testCore.getScoreSheetReader().getFilename())
		);
	}
	
	/**
	 * override hashcode
	 * @return
	 */
	public int hasCode() {
		return scoreSheetPosition.getRow() ^ scoreSheetPosition.getColumn();
	}
	
	/**
	 * compare row, then column
	 * @param core
	 * @return
	 */
	public int compareTo(Core core) {
		int coreRow = core.getScoreSheetPosition().getRow();
		int coreColumn = core.getScoreSheetPosition().getColumn();
		
		int selfRow = scoreSheetPosition.getRow();
		int selfColumn = scoreSheetPosition.getColumn();
		
		if (selfRow != coreRow) {
			return selfRow - coreRow;
		} else if (selfColumn != coreColumn) {
			return selfColumn - coreColumn;
		} else {
			return this.scoreSheetReader.getFilename().compareTo(core.getScoreSheetReader().getFilename());
		}
	}
	
}
