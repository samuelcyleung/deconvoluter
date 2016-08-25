package ca.ubc.gpec.tma.deconvoluter.webapp

import java.io.IOException;
import org.springframework.web.multipart.commons.CommonsMultipartFile

import ca.ubc.gpec.tma.deconvoluter.Deconvoluter;
import ca.ubc.gpec.tma.deconvoluter.reader.ExcelLookupFileReader;
import ca.ubc.gpec.tma.deconvoluter.reader.ExcelScoreSheetReader;
import ca.ubc.gpec.tma.deconvoluter.reader.ExcelScoreSheetReaderException;
import ca.ubc.gpec.tma.deconvoluter.reader.LookupFileReaderException;
import ca.ubc.gpec.tma.deconvoluter.reader.LookupFileReader;
import ca.ubc.gpec.tma.deconvoluter.reader.ScoreSheetReader;
import ca.ubc.gpec.tma.deconvoluter.reader.ScoreSheetReaderException;

class DeconvoluterController {

	def index = {
		redirect(action: "askInput", params: params)
	}

	def askInput = { render(view: "askInput") }

	def uploadFiles = {
		boolean caseIdCaseSensitive = false;
		boolean headerRowInLookupFile = false;
		int rowTranspose = Integer.parseInt(params.rowTranspose);

		// determine if case/core ids are case sensitive
		if (params.caseIdCaseSensitive != null) {
			if (params.caseIdCaseSensitive.equals("yes")) {
				caseIdCaseSensitive = true;
			}
		}

		// determine if lookup file has header row
		if (params.headerRowInLookupFile != null) {
			if (params.headerRowInLookupFile.equals("yes")) {
				headerRowInLookupFile = true;
			}
		}

		try {

			ArrayList<ScoreSheetReader> ssrArr = new ArrayList<ScoreSheetReader>();
			params.scoreSheet.each {
				if (it.value.getOriginalFilename().length() > 0) {
					// make sure file exists
					ssrArr.add(new ExcelScoreSheetReader(
							new ByteArrayInputStream(it.value.getBytes()),
							it.value.getOriginalFilename()));
				}
			}
			ScoreSheetReader[] scoreSheetReaders = new ExcelScoreSheetReader[ssrArr.size()];
			for (int i=0; i<ssrArr.size(); i++) {
				scoreSheetReaders[i] = ssrArr.get(i);
			}


			CommonsMultipartFile lookupFile = params.lookupFile;
			LookupFileReader lookupFileReader = new ExcelLookupFileReader(
					new ByteArrayInputStream(lookupFile.getBytes()),
					lookupFile.getOriginalFilename(),
					headerRowInLookupFile,
					caseIdCaseSensitive);


			// find available biomarkers
			// assume first excel file contains all biomarkers
			Set<String> biomarkerNamesSet = scoreSheetReaders[0].getAvailableBiomarkerNames();

			// deconvolute !!!
			Deconvoluter d = new Deconvoluter(
					lookupFileReader,
					scoreSheetReaders,
					caseIdCaseSensitive);

			String[] biomarkerNames = new String[biomarkerNamesSet.size()];
			Iterator<String> biomarkerNamesItr = biomarkerNamesSet.iterator();
			int i=0;
			while (biomarkerNamesItr.hasNext()) {
				biomarkerNames[i] = biomarkerNamesItr.next();
				i++;
			}
				
			// write response to http response ...
			response.setContentType(getOuputFileContentType(params.outputFileFormat));
			response.setHeader(
				"Content-disposition", 
				"attachment; filename=deconvoluted."+getOutputFileExtension(params.outputFileFormat))
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
			
			d.deconvolute(bw, biomarkerNames, rowTranspose, params.outputFileFormat);
			bw.flush();
			bw.close();
			response.flushBuffer();

		} catch (ExcelScoreSheetReaderException essre) {
			render essre.toString();
		} catch (LookupFileReaderException elfre) {
			render elfre.toString();
		} catch (ScoreSheetReaderException ssre) {
			render ssre.toString();
		} catch (IOException ioe) {
			render ioe.toString();
		}
	}

	/**
	 * get the appropriate http response contentType given the outputFileFormat
	 * @param outputFileFormat
	 * @return
	 */
	private String getOuputFileContentType(String outputFileFormat) {
		String contentType = "";
		if (outputFileFormat.equals(Deconvoluter.OUTPUT_FILE_FORMAT_TAB_DELIMITED)) {
			contentType = "text/plain";
		} else if (outputFileFormat.equals(Deconvoluter.OUTPUT_FILE_FORMAT_CSV)) {
			contentType = "text/csv";
		}
		return contentType;
	}
	
	private String getOutputFileExtension(String outputFileFormat) {
		String extension = "";
		if (outputFileFormat.equals(Deconvoluter.OUTPUT_FILE_FORMAT_TAB_DELIMITED) |
			outputFileFormat.equals(Deconvoluter.OUTPUT_FILE_FORMAT_CSV)) {
			extension = "txt";
		} 
		return extension;
	}

}
