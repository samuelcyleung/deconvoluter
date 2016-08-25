package ca.ubc.gpec.tma.deconvoluter.model;

/**
 * CaseId - this class is needed to deal with different type a case id can be
 * e.g. String, Integer
 * 
 * @author Samuel
 * 
 */
public class CaseId implements Comparable<CaseId> {
	private static final int HASH_CODE_STRING_LENGTH = 4;
	
	private String caseId;
	private boolean caseSensitive;

	/**
	 * constructor
	 * 
	 * @param caseId
	 * @param caseSensitive
	 */
	public CaseId(String caseId, boolean caseSensitive) {
		this.caseId = caseId;
		this.caseSensitive = caseSensitive;
	}

	/**
	 * override toString() - print the caseId nicely i.e. if interger 2.0 => "2"
	 * 
	 * @return
	 */
	public String toString() {
		// check if its integer
		int integerValue;
		try {
			if (caseId.trim().endsWith("D") | 
					caseId.trim().endsWith("d") | 
					caseId.trim().endsWith("F") | 
					caseId.trim().endsWith("f") ) {
				// "1345D" do NOT want "1345"
				throw new NumberFormatException("");
			}
			
			Double doubleValue = Double.parseDouble(caseId);
			integerValue = (int) Math.round(doubleValue);
			if (integerValue != doubleValue) {
				return caseId; // just return it as a String
			}
		} catch (NumberFormatException nfe) {
			return caseId; // just return it as a String
		}
		return integerValue + "";
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CaseId)) {
			return false;
		}
		
		CaseId otherCaseId = (CaseId) obj;
		
		if (this.caseSensitive != otherCaseId.caseSensitive) {
			throw new UnsupportedOperationException("cannot compare two caseId's where one is case sensitive and the other is not");
		}
		
		return MiscUtil.checkEqual(otherCaseId.toString(), this.toString(),
				caseSensitive);

	}

	public int hashCode() {
		return caseId.substring(0, Math.min(caseId.length(), HASH_CODE_STRING_LENGTH)).hashCode();
	}

	public int compareTo(CaseId obj) {
		return this.toString().compareTo(obj.toString());
	}

}
