<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<head>
 <title>MSDaPl Docs: Data Upload</title>
 <link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='/css/global.css' />">
</head>
<yrcwww:contentbox centered="true" width="90" widthRel="true" title="Uploading Data">
	
	<div style="border: 1px dotted; margin:10px; padding:10px;">
	
	The fasta file used for peptide search has to be uploaded to our protein database BEFORE you upload your search results.
	You can check the fasta files available for your lab by clicking on the "<html:link action="availableFasta.do">Available FASTA</html:link>" link in the menu.
	If you do not see your file in the list please contact the administrator for uploading your fasta file. 
	<br/><br/>
	
	
	MSDaPl supports data from two proteomic pipelines:
	<li><b>MacCoss Lab's pipeline</b></li>
	<li><b>Trans-Proteomic Pipeline (TPP)</b></li>
	<br/><br/>
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">Requirements for data from the MacCoss Lab's pipeline<br/></div>
	<br/>
	There are two options for required directory structure:
	<br/><br/>
	Option 1:
	<pre style="font-size:8pt;">
	Experiment directory
	|
	|---- pipeline/sequest (contains Sequest .sqt files, sequest.params and ms2 or cms2 files)
	|
	|---- pipeline/percolator (contains Percolator's .sqt files)
	|
	|---- pipeline/dtaselect/sequest (contains DTASelect-filter.txt)
	</pre>
	<br/>
	Option 2:
	<pre style="font-size:8pt;">
	Experiment directory
	|
	|---- sequest (contains Sequest .sqt files, sequest.params and ms2 or cms2 files)
	|
	|---- percolator (contains Percolator's .sqt files)
	|
	|---- dtaselect/sequest (contains DTASelect-filter.txt)
	</pre>
	<br/>
	<br/>
	
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">Requirements for data from the TPP<br/></div>
	<br/>
	The following files should be available in the experiment directory:
	<li>mzXML files</li>
	<li>pepXML files with Sequest search results. There should be one corresponding to each mzXML file.</li>
	<li>sequest.params file used for database search</li>
	<li>interact.pep.xml file with PeptideProphet results</li>
	<li>interact.prot.xml file with ProteinProphet results</li>
	
	<br/><br/>
	
	</div>
	

	<div style="border: 1px dotted; margin:10px; padding:10px;">
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">Adding jobs to the upload queue using Web Services<br/></div>
	<br>
	
	MSDaPl provides REST-based web services to submit upload requests without having to use the upload form in the web interface.
	<br/>
	 <br/>
	In the examples below replace &lt;server&gt; with repoman.gs.washington.edu for MSDaPl deployed on repoman.  
	Use flint.gs.washington.edu for the application deployed on flint.

	<br/><br/>
	The service provides the following REST methods:

	

	<ol>
		<li style="padding-bottom:25px;">
			<b>Get the details of a job already in the queue</b>
			<table style="border:1px dotted; gray;background:#F0FFFF;margin:5px;" cellpadding="2" cellspacing="2" >
				<tbody>
					<tr>
						<td style="border:1px dotted; gray;">URL</td>
						<td style="border:1px dotted; gray;">http://&lt;server&gt;/msdapl_queue/services/msjob/&lt;jobId&gt;</td>
					</tr>
					<tr>
						<td style="border:1px dotted; gray;">HTTP METHOD</td>
						<td style="border:1px dotted; gray;">GET</td>
					</tr>
					<tr>
						<td style="border:1px dotted; gray;">AUTHENTICATION</td>
						<td style="border:1px dotted; gray;">not required</td>
					</tr>
					<tr>
						<td style="border:1px dotted; gray;">PATH PRAMETER</td>
						<td style="border:1px dotted; gray;">jobId</td>
					</tr>
					<tr>
						<td style="border:1px dotted; gray;">PRODUCES</td>
						<td style="border:1px dotted; gray;">text, xml, json</td>
					</tr>
				</tbody>
			</table>
		
			<b>Examples using cURL</b>
			<br/>
			<ul>
				<li>TEXT OUTPUT: <span style="color:#D74D2D;">curl http://&lt;server&gt;/msdapl_queue/services/msjob/&lt;jobId&gt;</span></li>
				<li>XML OUTPUT : <span style="color:#D74D2D;">curl -H "Accept:application/xml" http://&lt;server&gt;/msdapl_queue/services/msjob/&lt;jobId&gt;</span></li>
	 			<li>JSON OUTPUT: <span style="color:#D74D2D;">curl -H "Accept:application/json" http://&lt;server&gt;/msdapl_queue/services/msjob/&lt;jobId&gt;</span></li>
			</ul>
			
		</li>
	
	
		<li style="padding-bottom:25px;">
			<b>Get the status of a job already in the queue</b>
			<table style="border:1px dotted; gray;background:#F0FFFF;margin:5px;" cellpadding="2" cellspacing="2" >
				<tbody>
					<tr>
						<td style="border:1px dotted; gray;">URL</td>
						<td style="border:1px dotted; gray;">http://&lt;server&gt;/msdapl_queue/services/msjob/status/&lt;jobId&gt;</td>
					</tr>
					<tr>
						<td style="border:1px dotted; gray;">HTTP METHOD</td>
						<td style="border:1px dotted; gray;">GET</td>
					</tr>
					<tr>
						<td style="border:1px dotted; gray;">AUTHENTICATION</td>
						<td style="border:1px dotted; gray;">not required</td>
					</tr>
					<tr>
						<td style="border:1px dotted; gray;">PATH PRAMETER</td>
						<td style="border:1px dotted; gray;">jobId</td>
					</tr>
				</tbody>
			</table>
			<b>Example using cURL</b>
			<br/>
			<ul>
				<li>TEXT OUTPUT: <span style="color:#D74D2D;">curl http://&lt;server&gt;/msdapl_queue/services/msjob/status/&lt;jobId&gt;</span></li>
			</ul>
		</li>
		
		
		<li style="padding-bottom:25px;">
			<b>Delete a job already in the database</b>
			<table style="border:1px dotted; gray;background:#F0FFFF;margin:5px;" cellpadding="2" cellspacing="2" >
				<tbody>
					<tr>
						<td style="border:1px dotted; gray;">URL</td>
						<td style="border:1px dotted; gray;">http://&lt;server&gt;/msdapl_queue/services/msjob/delete/&lt;jobId&gt;</td>
					</tr>
					<tr>
						<td style="border:1px dotted; gray;">HTTP METHOD</td>
						<td style="border:1px dotted; gray;">DELETE</td>
					</tr>
					<tr>
						<td style="border:1px dotted; gray;">AUTHENTICATION</td>
						<td style="border:1px dotted; gray;">required</td>
					</tr>
					<tr>
						<td style="border:1px dotted; gray;">PATH PRAMETER</td>
						<td style="border:1px dotted; gray;">jobId</td>
					</tr>
				</tbody>
			</table>
			<b>Example using cURL</b>
			<br/>
			<ul>
				<li><span style="color:#D74D2D;">curl -u &lt;username&gt;:&lt;password&gt;  -X DELETE http://&lt;server&gt;/msdapl_queue/services/msjob/delete/&lt;jobId&gt;</span></li>
			</ul>
		</li>
		
		
		<li style="padding-bottom:25px;">
			<b>Submit a job to the queue</b>
			<table style="border:1px dotted; gray;background:#F0FFFF;margin:5px;" cellpadding="2" cellspacing="2" >
				<tbody>
					<tr>
						<td style="border:1px dotted; gray;">URL</td>
						<td style="border:1px dotted; gray;">http://&lt;server&gt;/msdapl_queue/services/msjob/add</td>
					</tr>
					<tr>
						<td style="border:1px dotted; gray;">HTTP METHOD</td>
						<td style="border:1px dotted; gray;">POST</td>
					</tr>
					<tr>
						<td style="border:1px dotted; gray;">AUTHENTICATION</td>
						<td style="border:1px dotted; gray;">required</td>
					</tr>
					<tr>
						<td style="border:1px dotted; gray;">CONSUMES</td>
						<td style="border:1px dotted; gray;">text, xml, json</td>
					</tr>
					<tr>
						<td style="border:1px dotted; gray;">PRODUCES</td>
						<td style="border:1px dotted; gray;">text<br/>Returns the database ID of the newly queued job</td>
					</tr>
				</tbody>
			</table>
			<b>Example using cURL</b>
			<br/>
			<ul>
				<li>JSON INPUT: <span style="color:#D74D2D;">curl -u &lt;username&gt;:&lt;password&gt; -X POST  -H 'Content-Type: application/json' -d '{"projectId":"24", "dataDirectory":"/test/data", "pipeline":"MACCOSS", "date":"2010-03-29", "comments":"upload test"}' http://&lt;server&gt;/msdapl_queue/services/msjob/add</span></li>
			</ul>
		
		</li>
		
		<li>
			<b>Submit a job to the queue (using query parameters)</b>
			<table style="border:1px dotted; gray;background:#F0FFFF;margin:5px;" cellpadding="2" cellspacing="2" >
				<tbody>
					<tr>
						<td style="border:1px dotted; gray;">URL</td>
						<td style="border:1px dotted; gray;">http://&lt;server&gt;/msdapl_queue/services/msjob/add</td>
					</tr>
					<tr>
						<td style="border:1px dotted; gray;">HTTP METHOD</td>
						<td style="border:1px dotted; gray;">POST</td>
					</tr>
					<tr>
						<td style="border:1px dotted; gray;">AUTHENTICATION</td>
						<td style="border:1px dotted; gray;">required</td>
					</tr>
					<tr>
						<td style="border:1px dotted; gray;">QUERY PRAMETERs</td>
						<td style="border:1px dotted; gray;">
							<table style="font-size:8pt;">
								<tbody>
									<tr><td style="font-size:8pt;font-weight:bold;">projectId</td><td style="font-size:8pt;">Required.  ID of the parent project</td></tr>
									<tr><td style="font-size:8pt;font-weight:bold;">dataDirectory</td><td style="font-size:8pt;">Required.  path to the data directory</td></tr>
									<tr><td style="font-size:8pt;font-weight:bold;">remoteServer</td><td style="font-size:8pt;">Optional.  ID of remote server</td></tr>
									<tr><td style="font-size:8pt;font-weight:bold;">pipeline</td><td style="font-size:8pt;">Required.  Either TPP or MACCOSS</td></tr>
									<tr><td style="font-size:8pt;font-weight:bold;">date</td><td style="font-size:8pt;">Required.  Date the data was generated (Accepted format example: 09/24/10)</td></tr>
									<tr><td style="font-size:8pt;font-weight:bold;">instrument</td><td style="font-size:8pt;">Optional.  Name of the instrument use to acquire data. This should match the instruments available in MSDaPl</td></tr>
									<tr><td style="font-size:8pt;font-weight:bold;">targetSpecies</td><td style="font-size:8pt;">Optional.  Taxonomy ID of the target species</td></tr>
									<tr><td style="font-size:8pt;font-weight:bold;">comments</td><td style="font-size:8pt;">Optional</td></tr>
								</tbody>
							</table>
						</td>
					</tr>
					<tr>
						<td style="border:1px dotted; gray;">PRODUCES</td>
						<td style="border:1px dotted; gray;">text<br/>Returns the database ID of the newly queued job</td>
					</tr>
				</tbody>
			</table>
			<b>Example using cURL</b>
			<br/>
			<ul>
				<li><span style="color:#D74D2D;">curl -u &lt;username&gt;:&lt;password&gt;  -X POST "http://&lt;server&gt;/msdapl_queue/services/msjob/add?projectId=24&amp;dataDirectory=/data/test&amp;pipeline=MACCOSS&amp;date=09/24/10&amp;instrument=LTQ&amp;taxId=9606&amp;comments=some%20comments"</span></li>
			</ul>
		
		</li>
	</ol>
	
	</div>
</yrcwww:contentbox>