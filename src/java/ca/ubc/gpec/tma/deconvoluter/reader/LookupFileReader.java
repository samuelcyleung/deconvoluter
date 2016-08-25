package ca.ubc.gpec.tma.deconvoluter.reader;

import java.util.Set;

import ca.ubc.gpec.tma.deconvoluter.model.CaseId;

/**
 * read lookup file
 * @author Samuel
 *
 */

public interface LookupFileReader {

	/**
	 * see if this case id is mentioned in lookup file
	 * @param caseId
	 * @return
	 */
	public boolean isValidCaseId(String caseId);
	
	/**
	 * return an iterator of the case id's found in this lookup file
	 * @return
	 */
	public Set<CaseId> getCaseIds();
	
	/**
	 * return the numher of unique cases ID's in this lookup file 
	 * @return
	 */
	public int getNumCaseIds();
	
	/**
	 * get case id name e.g. bseries_id ... this is the first line of lookup file
	 * @return
	 */
	public String getCaseIdName();
	
}
