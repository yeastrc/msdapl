/**
 * TabularResults.java
 * @author Vagisha Sharma
 * Apr 5, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.SORT_BY;
import org.yeastrc.ms.domain.search.SORT_ORDER;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.www.misc.HyperlinkedData;
import org.yeastrc.www.misc.Pageable;
import org.yeastrc.www.misc.TableCell;
import org.yeastrc.www.misc.TableHeader;
import org.yeastrc.www.misc.TableRow;
import org.yeastrc.www.misc.Tabular;
import org.yeastrc.www.util.RoundingUtils;

public class TabularSequestResults implements Tabular, Pageable {

    
	private List<SORT_BY> columns = new ArrayList<SORT_BY>();
    
    private SORT_BY sortColumn;
    private SORT_ORDER sortOrder = SORT_ORDER.ASC;
    
    private boolean useEvalue;
    private boolean hasBullsEyeArea = false;
    
    private List<SequestResultPlus> results;
    
    private int currentPage;
    private int numPerPage;
    private int lastPage = currentPage;
    private List<Integer> displayPageNumbers;
    
    private RoundingUtils rounder;
    
    public TabularSequestResults(List<SequestResultPlus> results, boolean useEvalue, boolean hasBullsEyeArea) {
        this.results = results;
        displayPageNumbers = new ArrayList<Integer>();
        displayPageNumbers.add(currentPage);
        
        this.useEvalue = useEvalue;
        this.hasBullsEyeArea = hasBullsEyeArea;
        
        columns.add(SORT_BY.FILE_SEARCH);
        columns.add(SORT_BY.SCAN);
        columns.add(SORT_BY.CHARGE);
        columns.add(SORT_BY.MASS);
        columns.add(SORT_BY.RT);
        if(this.hasBullsEyeArea) {
        	columns.add(SORT_BY.AREA);
        }
        columns.add(SORT_BY.XCORR_RANK);
        columns.add(SORT_BY.XCORR);
        columns.add(SORT_BY.DELTACN);
        if(useEvalue) {
        	columns.add(SORT_BY.EVAL);
        }
        else {
        	columns.add(SORT_BY.SP);
        }
        columns.add(SORT_BY.PEPTIDE);
        columns.add(SORT_BY.PROTEIN);
        
        rounder = RoundingUtils.getInstance();
    }
    
    
    @Override
    public int columnCount() {
        return columns.size();
    }

    public SORT_BY getSortedColumn() {
        return sortColumn;
    }
    
    public void setSortedColumn(SORT_BY column) {
        this.sortColumn = column;
    }
    
    public SORT_ORDER getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SORT_ORDER sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    @Override
    public List<TableHeader> tableHeaders() {
        List<TableHeader> headers = new ArrayList<TableHeader>(columnCount());
        for(SORT_BY col: columns) {
            TableHeader header = new TableHeader(col.getDisplayName(), col.name());
            if(col == sortColumn) {
                header.setSorted(true);
                header.setSortOrder(sortOrder);
            }
            if(col == SORT_BY.PROTEIN || col == SORT_BY.AREA)
                header.setSortable(false);
            
            if(col.getTooltip() != null)
            	header.setTitle(col.getTooltip());
            headers.add(header);
        }
        return headers;
    }

    @Override
    public TableRow getRow(int index) {
        if(index >= results.size())
            return null;
        SequestResultPlus result = results.get(index);
        TableRow row = new TableRow();
        
        // row.addCell(new TableCell(String.valueOf(result.getId())));
        row.addCell(new TableCell(result.getFilename()));
        row.addCell(makeRightAlignCell(String.valueOf(result.getScanNumber())));
        row.addCell(makeRightAlignCell(String.valueOf(result.getCharge())));
        row.addCell(makeRightAlignCell(rounder.roundFourFormat(result.getObservedMass())));
        
        // Retention time
        BigDecimal temp = result.getRetentionTime();
        if(temp == null) {
            row.addCell(new TableCell(""));
        }
        else {
            row.addCell(makeRightAlignCell(rounder.roundTwoFormat(temp)));
        }
        
        // Area of the precursor ion
        if(this.hasBullsEyeArea) {
            row.addCell(makeRightAlignCell(rounder.roundTwoFormat(result.getArea())));
        }
        
        row.addCell(makeRightAlignCell(String.valueOf(result.getSequestResultData().getxCorrRank())));
        row.addCell(makeRightAlignCell(rounder.roundFourFormat(result.getSequestResultData().getxCorr())));
        row.addCell(makeRightAlignCell(String.valueOf(result.getSequestResultData().getDeltaCN())));
        if(useEvalue)
            row.addCell(makeRightAlignCell(String.valueOf(result.getSequestResultData().getEvalue())));
        else
            row.addCell(makeRightAlignCell(String.valueOf(result.getSequestResultData().getSp())));
        
        
        TableCell cell = null;
        String modifiedSequence = null;
        try {
        	// get modified peptide of the form: K.PEP[+80]TIDE.L
        	modifiedSequence = result.getResultPeptide().getFullModifiedPeptide(true); 
        }
        catch (ModifiedSequenceBuilderException e) {
            cell = new TableCell("Error building peptide sequence");
            modifiedSequence = null;
        }
        if(modifiedSequence != null) {
        	cell = new TableCell();
        	// link to Java spectrum viewer
        	HyperlinkedData javaLink = new HyperlinkedData("<span style='font-size:8pt;' title='Java Spectrum Viewer'>(J)</span>");
        	String url = "viewSpectrum.do?scanID="+result.getScanId()+"&runSearchResultID="+result.getId()
        	+"&java=true";
        	javaLink.setHyperlink(url, true);
        	javaLink.setTargetName("spec_view_java");
        	cell.addData(javaLink);
        	
        	// link to JavaScript spectrum viewer
        	HyperlinkedData jsLink = new HyperlinkedData("<span style='font-size:8pt;' title='JavaScript Spectrum Viewer'>(JS)</span>");
        	url = "viewSpectrum.do?scanID="+result.getScanId()+"&runSearchResultID="+result.getId();
        	jsLink.setHyperlink(url, true);
        	jsLink.setTargetName("spec_view_js");
        	cell.addData(jsLink);
        	
        	if(result.getResultPeptide().hasDynamicModification())
        		cell.addData("<span class=\"peptide\">"+modifiedSequence+"</span>");
        	else
        		cell.addData(modifiedSequence);
        }
        row.addCell(cell);
        
        String cellContents = result.getOneProteinShort();
        if(result.getProteinCount() > 1) {
            cellContents += " <span class=\"underline clickable\" "+
            "onClick=javascript:toggleProteins("+result.getId()+") "+
            ">("+result.getProteinCount()+")</span>";
            cellContents += " \n<div style=\"display: none;\" id=\"proteins_for_"+result.getId()+"\">"+result.getOtherProteinsShortHtml()+"</div>";
        }
        cell = new TableCell(cellContents);
        row.addCell(cell);
        return row;
    }
    
    private TableCell makeRightAlignCell(String content) {
    	TableCell cell = new TableCell(content);
    	cell.setClassName("right_align");
    	return cell;
    }
    
    @Override
    public int rowCount() {
        return results.size();
    }

    @Override
    public void tabulate() {
        // nothing to do here?
    }


    @Override
    public int getCurrentPage() {
        return currentPage;
    }
    
    public void setCurrentPage(int pageNum) {
        this.currentPage = pageNum;
    }

    @Override
    public List<Integer> getDisplayPageNumbers() {
        return this.displayPageNumbers;
    }
    
    public void setDisplayPageNumbers(List<Integer> pageNums) {
        this.displayPageNumbers = pageNums;
    }

    @Override
    public int getLastPage() {
        return this.lastPage;
    }
    
    public void setLastPage(int pageNum) {
        this.lastPage = pageNum;
    }

    @Override
    public int getPageCount() {
        return lastPage;
    }
    
    @Override
	public int getNumPerPage() {
		return numPerPage;
	}

	@Override
	public void setNumPerPage(int num) {
		this.numPerPage = num;
	}
}
