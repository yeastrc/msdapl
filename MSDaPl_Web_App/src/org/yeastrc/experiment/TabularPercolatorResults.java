/**
 * TabularPercolatorResults.java
 * @author Vagisha Sharma
 * Apr 5, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SORT_BY;
import org.yeastrc.ms.domain.search.SORT_ORDER;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.utils.RoundingUtilsMSLIBRARY;
import org.yeastrc.www.misc.HyperlinkedData;
import org.yeastrc.www.misc.Pageable;
import org.yeastrc.www.misc.TableCell;
import org.yeastrc.www.misc.TableHeader;
import org.yeastrc.www.misc.TableRow;
import org.yeastrc.www.misc.Tabular;
import org.yeastrc.www.util.RoundingUtils;

public class TabularPercolatorResults implements Tabular, Pageable {

	private List<SORT_BY> columns = new ArrayList<SORT_BY>();

    private SORT_BY sortColumn;
    private SORT_ORDER sortOrder = SORT_ORDER.ASC;

    private boolean hasPEP = true;

    private List<PercolatorResultPlus> results;
    private boolean hasBullsEyeArea = false;
    private boolean hasPeptideResults = false;
    
    private boolean hasSequestData = false;
    private boolean hasProlucidData = false;

    private int currentPage;
    private int numPerPage;
    private int lastPage = currentPage;
    private List<Integer> displayPageNumbers;

    private RoundingUtils rounder;

    RoundingUtilsMSLIBRARY roundingUtilsMSLIBRARY;

    public TabularPercolatorResults(List<PercolatorResultPlus> results, int searchAnalysisId,
    		boolean hasPEP, boolean hasBullsEyeArea,
    		boolean hasPeptideResults) {
    	
    	hasSequestData = true;
    	hasProlucidData = false;
    	
    	
    	boolean foundSequestData = false;
    	boolean foundProlucidData = false;

    	if ( ! results.isEmpty() ) {
    		
    		for ( PercolatorResultPlus result : results ) {

    			if ( result.getSequestData() != null ) {
    				foundSequestData = true;
    			}
    			if ( result.getProlucidData() != null ) {
    				foundProlucidData = true;
    			}
    		}
        	
        	if ( foundSequestData ) {
        		hasSequestData = true;
        	} else {
        		hasSequestData = false;
        	}
        	if ( foundProlucidData ) {
        		hasProlucidData = true;
        	} else {
        		hasProlucidData = false;
        	}
    	
    	} else {

    		//  No results to go by so query to get the search program used

            MsSearchDAO msSearchDAO = DAOFactory.instance().getMsSearchDAO();
            List<String> msSearchAnalysisProgramNamesForSearchAnalysisID =  msSearchDAO.getAnalysisProgramNamesForSearchAnalysisID( searchAnalysisId );
            
            for ( String msSearchAnalysisProgramName : msSearchAnalysisProgramNamesForSearchAnalysisID ) {
            	
            	Program msSearchAnalysisProgram = Program.instance( msSearchAnalysisProgramName );
            	
            	if ( Program.isSequest( msSearchAnalysisProgram ) ) {
            		
            		hasSequestData = true;
            	}
            	
            	if ( msSearchAnalysisProgram.equals( Program.PROLUCID ) ) {
            		
            		 hasProlucidData = true;
            	}
            }
            
            if ( ! hasProlucidData ) {
            	
            	hasSequestData = true; // Default to Sequest if not Prolucid.  Handles COMET and others
            }
            
    	}

    	
    	
    	
        this.results = results;
        displayPageNumbers = new ArrayList<Integer>();
        displayPageNumbers.add(currentPage);

        this.hasPEP = hasPEP;
        this.hasBullsEyeArea = hasBullsEyeArea;
        this.hasPeptideResults = hasPeptideResults;

        columns.add(SORT_BY.FILE_ANALYSIS);
        columns.add(SORT_BY.SCAN);
        columns.add(SORT_BY.CHARGE);
        columns.add(SORT_BY.MASS);
        columns.add(SORT_BY.RT);
        if(this.hasBullsEyeArea) {
        	columns.add(SORT_BY.AREA);
        }
        columns.add(SORT_BY.QVAL);
        if(hasPEP)
        	columns.add(SORT_BY.PEP);
        else
        	columns.add(SORT_BY.DS); // Report Percolator Discriminant Score instead of PEP

        if(this.hasPeptideResults) {
        	columns.add(SORT_BY.QVAL_PEPT);
        	columns.add(SORT_BY.PEP_PEPT);
        }
        
        if ( hasSequestData ) {
        	columns.add(SORT_BY.XCORR_RANK);
        	columns.add(SORT_BY.XCORR);
        }        
        if ( hasProlucidData ) {
        	columns.add(SORT_BY.PRIMARY_SCORE_RANK);
        	columns.add(SORT_BY.PRIMARY_SCORE);
        }
        
        columns.add(SORT_BY.PEPTIDE);
        columns.add(SORT_BY.PROTEIN);

        this.rounder = RoundingUtils.getInstance();

        roundingUtilsMSLIBRARY = RoundingUtilsMSLIBRARY.getInstance();
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

            if(col == SORT_BY.XCORR_RANK || col == SORT_BY.XCORR || col == SORT_BY.DELTACN)
                header.setSortable(false);
            if(col == SORT_BY.QVAL_PEPT || col == SORT_BY.PEP_PEPT)
            	header.setSortable(false);
            if(col == SORT_BY.PROTEIN || col == SORT_BY.AREA)
                header.setSortable(false);

            if(col == sortColumn) {
                header.setSorted(true);
                header.setSortOrder(sortOrder);
            }

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
        PercolatorResultPlus result = results.get(index);
        TableRow row = new TableRow();

        // row.addCell(new TableCell(String.valueOf(result.getPercolatorResultId())));
        row.addCell(new TableCell(result.getFilename()));
        row.addCell(makeRightAlignCell(String.valueOf(result.getScanNumber())));
        row.addCell(makeRightAlignCell(String.valueOf(result.getCharge())));
        row.addCell(makeRightAlignCell(rounder.roundFourFormat(result.getObservedMass())));

        // Retention time
        BigDecimal temp = result.getRetentionTime();
        if(temp == null) {
            row.addCell(new TableCell(""));
        }
        else
            row.addCell(makeRightAlignCell(rounder.roundTwoFormat(temp)));

        // Area of the precursor ion
        if(hasBullsEyeArea) {
            row.addCell(makeRightAlignCell(rounder.roundTwoFormat(result.getArea())));
        }

        row.addCell(makeRightAlignCell( result.getQvalueRounded3SignificantDigits() ));
        if(this.hasPEP)
            row.addCell(makeRightAlignCell( result.getPosteriorErrorProbabilityRounded3SignificantDigits() ));
        else
            row.addCell(makeRightAlignCell( result.getDiscriminantScoreRounded3SignificantDigits() ));

        if(hasPeptideResults) {
        	row.addCell(makeRightAlignCell( roundingUtilsMSLIBRARY.roundThreeSignificantDigits( result.getPeptideQvalue())));
        	row.addCell(makeRightAlignCell(roundingUtilsMSLIBRARY.roundThreeSignificantDigits(result.getPeptidePosteriorErrorProbability())));
        }
        
        
        
        if ( hasSequestData ) {
    		if ( result.getSequestData() != null ) {

				// Sequest data
				row.addCell(makeRightAlignCell(String.valueOf(result.getSequestData().getxCorrRank())));
				row.addCell(makeRightAlignCell((rounder.roundFourFormat(result.getSequestData().getxCorr()))));
//    	        row.addCell(new TableCell(String.valueOf(round(result.getSequestData().getDeltaCN()))));
				
    		} else {
				// No Sequest data this row
				row.addCell(makeRightAlignCell(""));
				row.addCell(makeRightAlignCell(""));
    		}
        }        
        if ( hasProlucidData ) {

        	if ( result.getProlucidData() != null ) {

        		// Prolucid data
        		row.addCell(makeRightAlignCell(String.valueOf(result.getProlucidData().getPrimaryScoreRank())));
        		row.addCell(makeRightAlignCell((rounder.roundFourFormat(result.getProlucidData().getPrimaryScore()))));
    		} else {
				// No Prolucid data this row
				row.addCell(makeRightAlignCell(""));
				row.addCell(makeRightAlignCell(""));
        	}
        }

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
        	+"&runSearchAnalysisID="+result.getRunSearchAnalysisId()
        	+"&java=true";
        	javaLink.setHyperlink(url, true);
        	javaLink.setTargetName("spec_view_java");
        	cell.addData(javaLink);

        	HyperlinkedData jsLink = new HyperlinkedData("<span style='font-size:8pt;' title='JavaScript Spectrum Viewer'>(JS)</span>");
        	url = "viewSpectrum.do?scanID="+result.getScanId()+"&runSearchResultID="+result.getId()
        	+"&runSearchAnalysisID="+result.getRunSearchAnalysisId();
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
            "onClick=javascript:toggleProteins("+result.getPercolatorResultId()+") "+
            ">("+result.getProteinCount()+")</span>";
            cellContents += " \n<div style=\"display: none;\" id=\"proteins_for_"
            			 +result.getPercolatorResultId()+"\">"+result.getOtherProteinsShortHtml()+"</div>";
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
