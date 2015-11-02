/**
 * PeptideComparisonDataset.java
 * @author Vagisha Sharma
 * Apr 15, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.DatasetPeptideInformation;
import org.yeastrc.www.misc.TableCell;
import org.yeastrc.www.misc.TableHeader;
import org.yeastrc.www.misc.TableRow;
import org.yeastrc.www.misc.Tabular;
import org.yeastrc.www.project.SORT_CLASS;

/**
 * 
 */
public class PeptideComparisonDataset implements Tabular {

    
    private List<Dataset> datasets;
    private List<ComparisonPeptide> peptides = new ArrayList<ComparisonPeptide>();
    private final int nrseqProteinId;
    
    public PeptideComparisonDataset(int nrseqProteinId) {
        this.nrseqProteinId = nrseqProteinId;
        datasets = new ArrayList<Dataset>(0);
        peptides = new ArrayList<ComparisonPeptide>(0);
    }
    
    public List<ComparisonPeptide> getPeptides() {
        return peptides;
    }

    public void setPeptides(List<ComparisonPeptide> peptides) {
        this.peptides = peptides;
    }
    
    public List<Dataset> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<Dataset> datasets) {
        this.datasets = datasets;
    }
    
    public int getDatasetCount() {
        return datasets.size();
    }

    @Override
    public int columnCount() {
        return datasets.size() + 1 + datasets.size();
    }

    @Override
    public TableRow getRow(int index) {
        
        ComparisonPeptide peptide = peptides.get(index);
        TableRow row = new TableRow();
        
        
        int dsIndex = 0;
        for(Dataset ds: datasets) {
            TableCell cell = new TableCell();
            cell.setId(String.valueOf(dsIndex));
            dsIndex++;
            
            DatasetPeptideInformation dpi = peptide.getDatasetPeptideInformation(ds);
            if(dpi == null || !dpi.isPresent()) { // dataset does not contain this protein
                cell.setClassName("pept-not-found");
            }
            else {
                cell.setClassName("pept-found");
                if(dpi.isUnique()) {
                    cell.setData("*");
                    cell.setClassName("pept-found pept-unique centered");
                }
                else {
                    cell.setData("-");
                }
            }
            row.addCell(cell);
        }
        // sequence
        TableCell peptCell = new TableCell(peptide.getSequence());
        peptCell.setClassName("left_align");
        row.addCell(peptCell);
        
        // charge
        TableCell chargeCell = new TableCell(peptide.getCharge()+"");
        chargeCell.setClassName("left_align");
        row.addCell(chargeCell);
        
        // Spectrum counts
        for(Dataset ds: datasets) {
            TableCell cell = new TableCell();
            
            DatasetPeptideInformation dpi = peptide.getDatasetPeptideInformation(ds);
            if(dpi == null || !dpi.isPresent()) { // dataset does not contain this protein
                cell.setClassName("pept-not-found");
            }
            else {
            	cell.setData(dpi.getSpectrumCount()+"");
            }
            row.addCell(cell);
        }
        
        return row;
    }

    @Override
    public int rowCount() {
        return peptides.size();
    }

    @Override
    public List<TableHeader> tableHeaders() {
        List<TableHeader> headers = new ArrayList<TableHeader>(columnCount());
        TableHeader header;
        for(Dataset ds: datasets) {
            header = new TableHeader(String.valueOf(ds.getDatasetId()));
            header.setWidth(2);
            header.setSortClass(SORT_CLASS.SORT_ALPHA);
            headers.add(header);
        }
        header = new TableHeader("Sequence");
        header.setSortClass(SORT_CLASS.SORT_ALPHA);
        headers.add(header);
        header = new TableHeader("Charge");
        header.setSortClass(SORT_CLASS.SORT_INT);
        headers.add(header);
        for(Dataset ds: datasets) {
            header = new TableHeader(String.valueOf(ds.getDatasetId())+"<br>SC");
            header.setSortClass(SORT_CLASS.SORT_INT);
            headers.add(header);
        }
        return headers;
    }

    @Override
    public void tabulate() {
        // nothing to do here
    }

    public int getNrseqProteinId() {
        return nrseqProteinId;
    }

}
