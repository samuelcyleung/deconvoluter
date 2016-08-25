package ca.ubc.gpec.tma.deconvoluter.model;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * a case can have multiple core e.g. duplicate core
 * @author Samuel
 *
 */
public class Case implements Comparable<Case>{

	private CaseId caseId;
	private TreeSet<Core> cores;
	
	/**
	 * constructor
	 * @param caseId
	 */
	public Case (CaseId caseId) {
		this.caseId = caseId;
		cores = new TreeSet<Core>();
	}
	
	/**
	 * get case ID
	 * @return
	 */
	public CaseId getCaseId(){return caseId;}
	
	public void addCore(Core core) {
		cores.add(core);
	}
	
	public Iterator<Core> getCores() {
		return cores.iterator();
	}
	
	public int hashCode() {
		return caseId.hashCode();
	}

	public int compareTo(Case obj) {
		return caseId.compareTo(obj.getCaseId());
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Case)) {
			return false;
		}
		
		Case otherCase = (Case)obj;
		
		return this.caseId.equals(otherCase.getCaseId());
	}
}
