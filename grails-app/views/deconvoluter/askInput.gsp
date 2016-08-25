<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="ca.ubc.gpec.tma.deconvoluter.Deconvoluter" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title>Upload TMA score sheets for deconvolute</title>
<g:javascript library="prototype"/>
</head>
<body>
	<div class="body">

		<h1>Upload TMA score sheets for deconvolute</h1>
		<i>Please note: score sheets and lookup files may be stored in the server.</i>
		
		<p>
		<h2>Instructions</h2>
		STEP1: select Excel file(s) containing sector map(s) and score sheet(s).  The following are requirements for the Excel files:
		<ul>
			<li>Files must be in Microsoft Excel format.
			<li>One block per Excel file.
			<li>First worksheet in each Excel file must be the sector map.  Name of sector map worksheet does not matter.
			<li>Core id's on the sector map ...  Be careful!!! Some character may be mistakened to be a core id by the deconvoluter. e.g. the row number '5' on the sector map is the same as the core id '5'. The deconvoluter will try to guess which are the row/column numbers by scanning the rows/columns for the word 'sector'. To be safe, please ensure that no characters (e.g. any comments on the score sheet) appears outside the sectors - it is fine to keep the sector row/column numbers.
			<li>Subsequent worksheet(s) on each Excel file contain scores for biomarker(s).  Worksheet name must be the name of the biomarker.
			<li>Position of biomarker worksheet (i.e. not sector map) on Excel file does not matter.
			<li>Name of the biomarker worksheet for the same biomarker must be the same across multiple Excel files (representing scores on different blocks or the array).
			<li>Position of biomarker scores of the cores on each Excel worksheet must match the core position on the sector map.
			<li>Deconvoluter does not look at any sector markings on the score sheet.  It assumes biomarker scores of the cores are on the same (or transposed, if specified) position as the sector map based on Excel worksheet coordinates only.
			<li>Example score sheets / sector map: 
				<a href='http://www.gpec.ubc.ca/software/deconvoluter/example/blockA.xls'>blockA</a>,
				<a href='http://www.gpec.ubc.ca/software/deconvoluter/example/blockB.xls'>blockB</a>,
				<a href='http://www.gpec.ubc.ca/software/deconvoluter/example/blockC.xls'>blockC</a>
		</ul>
		STEP 2: select an Excel file that contain the core id's on the sector map (i.e. lookup file).  The following are requirements for this Excel file:
		<ul>
			<li>Assume core id's are on the first non-empty column.  All other non-empty columns are ignored.
			<li>No requirements for core id's format.  It can be a number or text of any length.  Be careful, if you select tab-delimited text as your output format, you should not have tab character in core id's.
			<li>Core id's do not have to be in sorted order.
			<li>Core id's must be unique within the lookup file.  If core id's appear more than once on the lookup file, deconvoluter will return an error.
			<li>Example lookup file: <a href='http://www.gpec.ubc.ca/software/deconvoluter/example/lookup.xls'>lookup file</a>
		</ul>
		STEP 3: select output file format.  Default is tab-delimited text.<br>
		</p>
		
		<g:form action="uploadFiles" method="post"
			enctype="multipart/form-data">
			<div class="dialog">
				<table>
					<tbody>
						<tr class="prop">
							<td valign="top" class="name"><label>(STEP 1) TMA score
									sheets</label></td>
							<td valign="top">
								<table id="fileSelectTable">
									<tr>
										<td><input type="file" 
											name="scoreSheet.1"
											id="scoreSheet_1"
											title="please select score sheet" 
											onchange="addFileSelect(1)" />
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr class="prop">
							<td colspan=2>
								<input type="checkbox" name="caseIdCaseSensitive", value="yes" />
								Case/core ID is case sensitive?
							</td>
						</tr>
						<tr class="prop">
							<td colspan=2>
								<select name="rowTranspose">
									<g:each in="${0..20}">
										<option value="${it}">${it}</option>
									</g:each>
								</select>
								Row transpose (i.e. score sheet row number shifted from sector map)
							</td>
						</tr>
						<tr class="prop">
							<td valign="top" class="name"><label>(STEP 2) Lookup file</label></td>
							<td><input type="file" name="lookupFile"
								title="please select lookup file" onchange="lookupFileHasBeenSelected()" />
							</td>
						</tr>
						<tr class="prop">
							<td colspan=2>
								<input type="checkbox" name="headerRowInLookupFile", value="yes" checked="yes" />
								Header row in lookup file?
							</td>
						</tr>
						<tr class="prop">
							<td colspan=2>
								<select name="outputFileFormat">
									<g:each in="${Deconvoluter.getAvailableOutputFileFormats()}">
										<option value="${it}">${it}</option>
									</g:each>
								</select>
								(STEP 3) Output file format
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="buttons">
				<span class="button">
					<g:submitButton id="uploadButton"
						name="upload" class="upload" value="Please select score sheet(s) & lookup file." disabled="true"/> 
				</span>
				<input type="reset" value="Reset" align="right" onclick="resetFileSelect()">	
			</div>
		</g:form>

	</div>

	<g:javascript>
	
		var atLeastOneScoreSheetSelected = false;
		var lookupFileSelected = false;
		
		function checkOkToDeconvolute() {
			if (atLeastOneScoreSheetSelected && lookupFileSelected) {
				document.getElementById("uploadButton").value="Great! Click here to deconvolute.";
				document.getElementById("uploadButton").disabled=false;
			}
		}
		
		function addFileSelect(currRow) {
			var tableRef=document.getElementById("fileSelectTable");
			var lastRow = tableRef.rows.length;

			if (currRow == lastRow) {
				var newRow = tableRef.insertRow(lastRow);
				var newFileSelect = document.createElement("input");
				newFileSelect.setAttribute("type", "file");
				newFileSelect.setAttribute("name", "scoreSheet."+(1+lastRow));
				newFileSelect.setAttribute("title", "(optional) please select additional score sheet");
				newFileSelect.setAttribute("onchange","addFileSelect("+(1+lastRow)+")");
						
				var newCell = newRow.insertCell(0);
				newCell.appendChild(newFileSelect);
			
				// one score sheet must have been selected
				atLeastOneScoreSheetSelected = true;
			
				checkOkToDeconvolute();
			}
		}
				
		function lookupFileHasBeenSelected() {
			lookupFileSelected = true;
			checkOkToDeconvolute();
		}
		
		function resetFileSelect() {
			var tableRef=document.getElementById("fileSelectTable");
			var lastRow = tableRef.rows.length;

			while (tableRef.rows.length != 1) {
				tableRef.deleteRow(1);
			}
			
			atLeastOneScoreSheetSelected = false;
			lookupFileSelected = false;
			document.getElementById("uploadButton").value="Please select score sheet(s) & lookup file.";
			document.getElementById("uploadButton").disabled=true;
		}
			
	</g:javascript>
</body>
</html>