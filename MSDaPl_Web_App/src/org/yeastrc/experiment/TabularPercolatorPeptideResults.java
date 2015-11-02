/**
 * TabularPercolatorPeptideResults.java
 * @author Vagisha Sharma
 * Sep 20, 2010
 */
package org.yeastrc.experiment;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorPeptideResult;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.SORT_BY;
import org.yeastrc.ms.domain.search.SORT_ORDER;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.www.misc.Pageable;
import org.yeastrc.www.misc.TableCell;
import org.yeastrc.www.misc.TableHeader;
import org.yeastrc.www.misc.TableRow;
import org.yeastrc.www.misc.Tabular;

/**
 *
 */
public class TabularPercolatorPeptideResults implements Tabular, Pageable {

	private List<SORT_BY> columns = new ArrayList<SORT_BY>();

    private SORT_BY sortColumn;
    private SORT_ORDER sortOrder = SORT_ORDER.ASC;


    private List<PercolatorPeptideResult> results;

    private int currentPage;
    private int numPerPage;
    private int lastPage = currentPage;
    private List<Integer> displayPageNumbers;


    public TabularPercolatorPeptideResults(List<PercolatorPeptideResult> results) {
        this.results = results;
        displayPageNumbers = new ArrayList<Integer>();
        displayPageNumbers.add(currentPage);


        columns.add(SORT_BY.PEPTIDE);
        columns.add(SORT_BY.PROTEIN);
        columns.add(SORT_BY.QVAL);
        columns.add(SORT_BY.PEP);
        columns.add(SORT_BY.DS);
        columns.add(SORT_BY.PVAL);
        columns.add(SORT_BY.NUM_PSM);

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

            if(col == SORT_BY.PROTEIN || col == SORT_BY.NUM_PSM)
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
        PercolatorPeptideResult result = results.get(index);
        TableRow row = new TableRow();


        TableCell cell;
        String modifiedSequence = null;
        try {
        	// get modified peptide of the form: K.PEP[+80]TIDE.L
        	modifiedSequence = result.getResultPeptide().getFullModifiedPeptide(true);
        }
        catch (ModifiedSequenceBuilderException e) {
            modifiedSequence = "Error building peptide sequence";
        }
        cell = new TableCell();
        if(result.getResultPeptide().hasDynamicModification())
    		cell.addData("<span class=\"peptide\">"+modifiedSequence+"</span>");
    	else
    		cell.addData(modifiedSequence);
        row.addCell(cell);


        String cellContents = getOneProteinShort(result);
        if(result.getProteinMatchList().size() > 1) {
            cellContents += " <span class=\"underline clickable\" "+
            "onClick=javascript:toggleProteins("+result.getId()+") "+
            ">("+result.getProteinMatchList().size()+")</span>";
            cellContents += " \n<div style=\"display: none;\" id=\"proteins_for_"
            			 +result.getId()+"\">"+getOtherProteinsShortHtml(result)+"</div>";
        }
        cell = new TableCell(cellContents);
        row.addCell(cell);


        row.addCell(makeRightAlignCell( result.getQvalueRounded3SignificantDigits() ));
        row.addCell(makeRightAlignCell( result.getPosteriorErrorProbabilityRounded3SignificantDigits() ) );
        if(result.getDiscriminantScore() != null)
        	row.addCell(makeRightAlignCell( result.getDiscriminantScoreRounded3SignificantDigits() ) );
        else
        	row.addCell(new TableCell(""));
        if(result.getPvalue() != -1.0)
        	row.addCell( makeRightAlignCell( result.getPvalueRounded3SignificantDigits() ) );
        else
        	row.addCell(new TableCell(""));

        // link to matching PSMs
        cellContents = "<span class=\"underline clickable\" id=\"psm_"+result.getId()+"\""+
        "onClick=javascript:viewPsms("+result.getSearchAnalysisId()+","+result.getId()+") "+
        ">"+result.getPsmIdList().size()+"</span>";
        row.addCell(makeRightAlignCell(cellContents));

        return row;
    }

    public String getOneProteinShort(PercolatorPeptideResult result) {
        if(result.getProteinMatchList() == null)
            return null;
        else {
        	return makeShort(result.getProteinMatchList().get(0).getAccession());
        }
    }

    private String makeShort(String string) {
    	if(string.length() > 23)
    		return string.substring(0, 20)+"...";
    	else
    		return string;
    }

    public String getOtherProteinsShortHtml(PercolatorPeptideResult result) {
        if(result.getProteinMatchList() == null)
            return null;
        else {
            StringBuilder buf = new StringBuilder();
            int i = 0;
            for(MsSearchResultProtein protein: result.getProteinMatchList()) {
                if(i == 0) {
                    i++;
                    continue;
                }
                buf.append("<br>"+makeShort(protein.getAccession()));
            }
            if(buf.length() > 0)
                buf.delete(0, "<br>".length());
            return buf.toString();
        }
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
