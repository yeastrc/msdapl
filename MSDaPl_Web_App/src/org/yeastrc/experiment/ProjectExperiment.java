/**
 * MsExperiment.java
 * @author Vagisha Sharma
 * Apr 2, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.ms.domain.general.MsInstrument;
import org.yeastrc.www.misc.TableCell;
import org.yeastrc.www.misc.TableHeader;
import org.yeastrc.www.misc.TableRow;
import org.yeastrc.www.misc.Tabular;
import org.yeastrc.www.project.SORT_CLASS;
import org.yeastrc.yates.YatesRun;


/**
 * 
 */
public class ProjectExperiment implements MsExperiment, Comparable<ProjectExperiment>, Tabular {

	private int projectId;

	private final MsExperiment experiment;
    private List<MsFile> ms1Files;
    private List<MsFile> ms2Files;
    private List<ExperimentSearch> searches;
    private List<SearchAnalysis> analyses;
    private YatesRun dtaSelect;
    private List<ExperimentProteinProphetRun> prophetRuns;
    private List<ExperimentProteinferRun> protInferRuns;
    
    private boolean hasFullInformation = false;
    private boolean hasProtinferRuns = false;
    
    private List<TableRow> rows;
    
    private boolean uploadSuccess = true;
    private int uploadJobId;
    
    
    private String precursorMassChartData;
    
    private String peakCountChartData;
    
    private String intensityCountChartData;
    
    public String getIntensityCountChartData() {
		return intensityCountChartData;
	}

	public void setIntensityCountChartData(String intensityCountChartData) {
		this.intensityCountChartData = intensityCountChartData;
	}

	public String getPeakCountChartData() {
		return peakCountChartData;
	}

	public void setPeakCountChartData(String peakCountChartData) {
		this.peakCountChartData = peakCountChartData;
	}

	public String getPrecursorMassChartData() {
		return precursorMassChartData;
	}

	public void setPrecursorMassChartData(String precursorMassChartData) {
		this.precursorMassChartData = precursorMassChartData;
	}

	public void setHasFullInformation(boolean full) {
    	this.hasFullInformation = full;
    }
    
    public boolean getHasFullInformation() {
    	return this.hasFullInformation;
    }
    
    public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
    
    public int getUploadJobId() {
        return uploadJobId;
    }

    public void setUploadJobId(int uploadJobId) {
        this.uploadJobId = uploadJobId;
    }

    public boolean isUploadSuccess() {
        return uploadSuccess;
    }

    public void setUploadSuccess(boolean uploadSuccess) {
        this.uploadSuccess = uploadSuccess;
    }

    public ProjectExperiment(MsExperiment experiment) {
        this.experiment = experiment;
    }

    @Override
    public int getId() {
        return experiment.getId();
    }

    @Override
    public Timestamp getLastUpdateDate() {
        return experiment.getLastUpdateDate();
    }

    @Override
    public String getServerAddress() {
        return experiment.getServerAddress();
    }

    @Override
    public String getServerDirectory() {
        return experiment.getServerDirectory();
    }

    public String getComments() {
        return experiment.getComments();
    }
    
    @Override
    public int getInstrumentId() {
        return this.experiment.getInstrumentId();
    }
    
    public MsInstrument getInstrument() {
        try {
            return DAOFactory.instance().getInstrumentDAO().load(experiment.getInstrumentId());
        } catch (Exception e) { return null; }
    }
    
    public String getInstrumentName() {
        MsInstrument instrument = getInstrument();
        if(instrument != null)
            return instrument.getName();
        else
            return "UNKNOWN";
    }

    public List<MsFile> getMs2Files() {
        return ms2Files;
    }

    public void setMs2Files(List<MsFile> ms2Files) {
        this.ms2Files = ms2Files;
    }
    
    public List<MsFile> getMs1Files() {
        return ms1Files;
    }

    public void setMs1Files(List<MsFile> ms1Files) {
        this.ms1Files = ms1Files;
    }
    
    public List<ExperimentSearch> getSearches() {
        return searches;
    }

    public void setSearches(List<ExperimentSearch> searches) {
        this.searches = searches;
    }

    public List<SearchAnalysis> getAnalyses() {
        return analyses;
    }
    
    public String getAnalysisProgramName() {
    	if(analyses != null && analyses.size() > 0)
    		return analyses.get(0).getAnalysisProgram().displayName();
    	return "";
    }

    public void setAnalyses(List<SearchAnalysis> analyses) {
        this.analyses = analyses;
    }

    public List<ExperimentProteinProphetRun> getProteinProphetRuns() {
    	if(prophetRuns == null)
    		return new ArrayList<ExperimentProteinProphetRun>();
    	else
    		return prophetRuns;
    }
    
    public void setProteinProphetRun(List<ExperimentProteinProphetRun> runs) {
        this.prophetRuns = runs;
    }
    
    public YatesRun getDtaSelect() {
        return dtaSelect;
    }

    public void setDtaSelect(YatesRun dtaSelect) {
        this.dtaSelect = dtaSelect;
    }

    @Override
    public Date getUploadDate() {
        return experiment.getUploadDate();
    }

    @Override
    public int compareTo(ProjectExperiment o) {
        if(o == null)
            return -1;
        return Integer.valueOf(experiment.getId()).compareTo(o.getId());
    }

    @Override
    public void tabulate() {
        
        rows = new ArrayList<TableRow>(ms2Files.size());
        int colCount = columnCount();
        
        FileComparator comparator = new FileComparator();
        Collections.sort(ms2Files, comparator);
        
        for(MsFile file: ms2Files) {
            TableRow row = new TableRow();
            TableCell cell = new TableCell(file.getFileName(), null);
            cell.setClassName("left_align");
            row.addCell(cell);
            rows.add(row);
        }
        
        
        if(ms1Files != null) {
            Collections.sort(ms1Files,comparator);
            int r = 0;
            for(MsFile file: ms1Files) {
                TableRow row = rows.get(r);
                row.addCell(new TableCell(String.valueOf(file.getScanCount()), null));
                r++;
            }
        }
        
        int r = 0;
        for(MsFile file: ms2Files) {
            TableRow row = rows.get(r);
            row.addCell(new TableCell(String.valueOf(file.getScanCount()), null));
            r++;
        }
        
        // iterate over the searches
        for(ExperimentSearch search: searches) {
            List<SearchFile> files = search.getFiles();
            Collections.sort(files, comparator);
            
            String action = null;
            //if(search.getSearchProgram() == Program.SEQUEST)
                //action = "viewSequestResults.do";
            
            int j = 0;
            for(r = 0; r < rows.size(); r++) {
                TableRow row = rows.get(r);
                SearchFile file = files.get(j);
                if(file.getFileName().equals(row.getCells().get(0).getDataList().get(0).getData())) {
                    String url = null;
                    if(action != null)
                        url = action+"?ID="+file.getId();
                    TableCell cell = new TableCell(String.valueOf(file.getNumResults()),
                            url);
                    row.addCell(cell);
                    j++;
                }
            }
        }
        
        // iterate over the analyses
        for(SearchAnalysis analysis: analyses) {
            List<AnalysisFile> files = analysis.getFiles();
            Collections.sort(files, comparator);
            
            String action = null;
            //if(analysis.getAnalysisProgram() == Program.PERCOLATOR)
             //   action = "viewPercolatorResults.do";
            
            int j = 0;
            for(r = 0; r < rows.size(); r++) {
                TableRow row = rows.get(r);
                if(j >= files.size()) {
                    row.addCell(new TableCell("-"));
                    continue;
                }
                AnalysisFile file = files.get(j);
                // match the file names
                if(file.getFileName().equals(row.getCells().get(0).getDataList().get(0).getData())) {
                    String url = null;
                    if(action != null)
                        url  = action + "?ID="+file.getId();
                    TableCell cell = new TableCell(String.valueOf(file.getNumResults()),
                            url);
                    row.addCell(cell);
                    j++;
                }
                else {
                    row.addCell(new TableCell("-"));
                }
            }
        }
    }
    
    private static class FileComparator implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            if(o1 == o2)    return 0;
            if(o1 == null)  return 1;
            if(o2 == null)  return -1;
            return o1.getFileName().compareTo(o2.getFileName());
        }
    }
    
    @Override
    public int columnCount() {
        // first column is filename
        // second column is # ms2 spectra
        int count = searches.size()+analyses.size();
        count++;  // filename
        count++;  // ms2 file
        if(ms1Files != null)
            count++;
        return count;
    }

    @Override
    public int rowCount() {
        return ms2Files.size();
    }
    
    @Override
    public TableRow getRow(int row) {
        return rows.get(row);
    }

    @Override
    public List<TableHeader> tableHeaders() {
        List<TableHeader> headers = new ArrayList<TableHeader>(columnCount());
        
        TableHeader header = new TableHeader("File");
        header.setSortClass(SORT_CLASS.SORT_ALPHA);
        headers.add(header);
        
        if(ms1Files != null) {
            header = new TableHeader("# MS1 Scans");
            header.setSortClass(SORT_CLASS.SORT_INT);
            headers.add(header);
        }
        
        header = new TableHeader("# MS2 Scans");
        header.setSortClass(SORT_CLASS.SORT_INT);
        headers.add(header);
        
        for(ExperimentSearch search: searches){
            header = new TableHeader(search.getSearchProgram().displayName());
            header.setSortClass(SORT_CLASS.SORT_INT);
            headers.add(header);
        }
        for(SearchAnalysis analysis: analyses) {
            String text = analysis.getAnalysisProgram().displayName();
            if(analysis.getFilename() != null && analysis.getFilename().length() > 0) {
                text += "<br><NOBR>"+analysis.getFilename()+"</NOBR></br>";
            }
            text += " "+analysis.getId();
            header = new TableHeader(text);
            header.setSortClass(SORT_CLASS.SORT_INT);
            headers.add(header);
        }
        return headers;
    }

    public List<ExperimentProteinferRun> getProtInferRuns() {
    	if(protInferRuns == null)
    		return new ArrayList<ExperimentProteinferRun>();
    	else
    		return protInferRuns;
    }

    public void setProtInferRuns(List<ExperimentProteinferRun> protInferRuns) {
        this.protInferRuns = protInferRuns;
    }
    
    public boolean getHasProtInferResults() {
        return this.hasProtinferRuns;
    }

    public void setHasProtinferRuns(boolean hasProtinferRuns) {
		this.hasProtinferRuns = hasProtinferRuns;
	}

	@Override
    public void setComments(String comments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setId(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setInstrumentId(int instrumentId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setServerAddress(String serverAddress) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setServerDirectory(String serverDirectory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUploadDate(Date uploadDate) {
        throw new UnsupportedOperationException();
    }
}
