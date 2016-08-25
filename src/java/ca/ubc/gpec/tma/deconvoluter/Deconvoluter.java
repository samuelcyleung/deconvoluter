package ca.ubc.gpec.tma.deconvoluter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeSet;

import ca.ubc.gpec.tma.deconvoluter.model.Case;
import ca.ubc.gpec.tma.deconvoluter.model.CaseId;
import ca.ubc.gpec.tma.deconvoluter.model.Core;
import ca.ubc.gpec.tma.deconvoluter.reader.ExcelLookupFileReader;
import ca.ubc.gpec.tma.deconvoluter.reader.ExcelScoreSheetReader;
import ca.ubc.gpec.tma.deconvoluter.reader.ExcelScoreSheetReaderException;
import ca.ubc.gpec.tma.deconvoluter.reader.LookupFileReader;
import ca.ubc.gpec.tma.deconvoluter.reader.LookupFileReaderException;
import ca.ubc.gpec.tma.deconvoluter.reader.ScoreSheetReader;
import ca.ubc.gpec.tma.deconvoluter.reader.ScoreSheetReaderException;

/**
 * Call the different classes in the model package to do deconvolution
 * @author Samuel
 *
 */
public class Deconvoluter {
	// some constants ...
	public static final String OUTPUT_FILE_FORMAT_TAB_DELIMITED = "text (tab delimited)";
	public static final String OUTPUT_FILE_FORMAT_CSV           = "CSV (comma delimited)";
	
	private TreeSet<Case> cases;
	private String caseIdName;
	
	public static String[] getAvailableOutputFileFormats() {
		return new String[] {OUTPUT_FILE_FORMAT_TAB_DELIMITED, OUTPUT_FILE_FORMAT_CSV};
	}
	
	/**
	 * find out how number of multiplicate cores
	 * e.g. duplicate core TMA - returns 2
	 * @return
	 */
	private int getMultiCoreNum() {
		int n = 0;
		// get the cases mentioned in lookup file ...
		Iterator<Case> caseItr = cases.iterator();
		while(caseItr.hasNext()) {
			Case c = caseItr.next();
			Iterator<Core> coreItr = c.getCores();
			int temp = 0;
			while(coreItr.hasNext()) {
				temp++;
				coreItr.next();
			}
			if (temp > n) {
				n=temp;
			}
		}
		return n; 
	}
	
	/**
	 * constructor
	 * @param lookupFileReader
	 * @param scoreSheetReaders
	 * @param caseSensitive
	 */
	public Deconvoluter(
			LookupFileReader lookupFileReader, 
			ScoreSheetReader[] scoreSheetReaders,
			boolean caseSensitive) 
		throws ScoreSheetReaderException {
		
		cases = new TreeSet<Case>();
		caseIdName = lookupFileReader.getCaseIdName();
		
		// get the cases mentioned in lookup file ...
		Iterator<CaseId> caseIdItr = lookupFileReader.getCaseIds().iterator();
		
		System.out.println("number of cases found in lookup file: "+lookupFileReader.getCaseIds().size());
		while (caseIdItr.hasNext()) {
			cases.add(new Case(caseIdItr.next()));
		}
		// read sector maps ...
		for (ScoreSheetReader scoreSheetReader : scoreSheetReaders) {
			scoreSheetReader.readSectorMap(cases, caseSensitive);
		}
	}
	
	/**
	 * deconvolute ... output to String representation of the output file
	 * @return
	 */
	public void deconvoluteToDelimited(BufferedWriter bw, String[] markerNames, int transposeRow, String delimiter) 
		throws ScoreSheetReaderException, IOException {
		
		int multiCoreNum = getMultiCoreNum();
		
		// writer header line ...
		bw.write(caseIdName);
		for (String markerName : markerNames) {
			for (int i=0; i<multiCoreNum; i++) {
				bw.write(delimiter+markerName);
				if (multiCoreNum > 1) {
					bw.write(".c"+(i+1));
				}
			}
		}
		bw.write("\n");
		
		
		Iterator<Case> caseItr = cases.iterator();
		while(caseItr.hasNext()) {
			Case c = caseItr.next();
			bw.write(""+c.getCaseId());
			
			for (String markerName : markerNames) {
				bw.write(delimiter);
				
				Iterator<Core> coreItr = c.getCores();
				int coreCount = 0;
				
				while(coreItr.hasNext()) {
					Core core = coreItr.next();
					//bw.write("r"+core.getScoreSheetPosition().getRow()+"c"+core.getScoreSheetPosition().getColumn()+" "+core.getScore(markerName, transposeRow) + delimiter);
					bw.write(""+core.getScore(markerName, transposeRow));	

					// whether to write a delimiter or not
					if (coreItr.hasNext()) {bw.write(delimiter);} 
				
					coreCount++;
				}
				while (coreCount < multiCoreNum) {
					bw.write(delimiter); // add some column in case some cases do not have same multiCoreNum
					coreCount++;
				}
			}
			
			bw.write("\n"); // newline character
		}
	}
	
	public void deconvolute(BufferedWriter bw, String[] markerNames, int transposeRow, String outputFileFormat) 
		throws ScoreSheetReaderException, IOException {
		if (outputFileFormat.equals(OUTPUT_FILE_FORMAT_TAB_DELIMITED)) {
			deconvoluteToDelimited(bw, markerNames, transposeRow, "\t");
		} else if (outputFileFormat.equals(OUTPUT_FILE_FORMAT_CSV)) {
			deconvoluteToDelimited(bw, markerNames, transposeRow, ",");
		}
	}
	
	/**
	 * for process test
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			boolean caseSensitive = false;

			Deconvoluter d = new Deconvoluter(
					//new ExcelLookupFileReader("/home/samuelc/Desktop/cd163/lookup.xls", true, caseSensitive), 
					//new ScoreSheetReader[] {
					//		new ExcelScoreSheetReader("/home/samuelc/Desktop/cd163/blockA.xls"),
					//		new ExcelScoreSheetReader("/home/samuelc/Desktop/cd163/blockB.xls"),
					//		new ExcelScoreSheetReader("/home/samuelc/Desktop/cd163/blockC.xls"),
					//		new ExcelScoreSheetReader("/home/samuelc/Desktop/cd163/blockD.xls"),
					//		new ExcelScoreSheetReader("/home/samuelc/Desktop/cd163/blockE.xls"),
					//		new ExcelScoreSheetReader("/home/samuelc/Desktop/cd163/blockF.xls")
					//}, caseSensitive);
					new ExcelLookupFileReader("/media/data/TEMP/testDeconvolute/vgh/lookup.xls", true, caseSensitive), 
					new ScoreSheetReader[] {
							//new ExcelScoreSheetReader("/media/data/TEMP/testDeconvolute/vgh/blockA.xls"),
							new ExcelScoreSheetReader("/media/data/TEMP/testDeconvolute/vgh/blockB.xls"),
							new ExcelScoreSheetReader("/media/data/TEMP/testDeconvolute/vgh/blockB2.xls")//,
							//new ExcelScoreSheetReader("/media/data/TEMP/testDeconvolute/vgh/blockC.xls")
					}, caseSensitive);
			
			BufferedWriter bw = new BufferedWriter(new FileWriter("/media/data/TEMP/testDeconvolute/vgh/vgh_deconvoluted.txt"));
			
			d.deconvolute(bw, new String[]{"Galectin4"}, 0, Deconvoluter.OUTPUT_FILE_FORMAT_TAB_DELIMITED);
			
			bw.close();
			System.out.println("ok");
		} catch (ExcelScoreSheetReaderException essre) {
			System.err.println(essre);
		} catch (LookupFileReaderException elfre) {
			System.err.println(elfre.toString());
		} catch (ScoreSheetReaderException ssre) {
			System.err.println(ssre.toString());
		} catch (IOException ioe) {
			System.err.println(ioe.toString());
		}
	}
}
