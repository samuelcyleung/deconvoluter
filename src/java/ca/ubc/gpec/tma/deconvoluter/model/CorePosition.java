package ca.ubc.gpec.tma.deconvoluter.model;

/**
 * represents a core position with in a single score sheet
 * @author Samuel
 *
 */
public class CorePosition {
	private int row;
	private int column;
	
	/**
	 * constructor
	 * @param row
	 * @param column
	 */
	public CorePosition(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {return true;}
		if (this.getClass() != obj.getClass()) {return false;}
		return (
			this.row == ((CorePosition)obj).row &
			this.column == ((CorePosition)obj).column
		);
	}
	
	public int hashCode() {
		return row ^ column;
	}
	
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	
	public String toString() {
		return "row:"+row+"; col:"+column;
	}

}
