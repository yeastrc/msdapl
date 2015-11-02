/**
 * GOEnrichmentTabular.java
 * @author Vagisha Sharma
 * Jun 11, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.DatasetGoInformation;
import org.yeastrc.www.go.EnrichedGOTerm;
import org.yeastrc.www.misc.TableCell;
import org.yeastrc.www.misc.TableHeader;
import org.yeastrc.www.misc.TableRow;
import org.yeastrc.www.misc.Tabular;
import org.yeastrc.www.project.SORT_CLASS;

/**
 * 
 */
public class GOEnrichmentTabular implements Tabular {

    private String title;
    private int numProteinsInSet;
    private int numProteinsInUniverse;
    
    private List<EnrichedGOTerm> enrichedTerms;
    private Map<EnrichedGOTerm, List<DatasetGoInformation>> datasetInformation;
    private List<Dataset> datasets;
    
    private List<TableHeader> headers;
    
    public GOEnrichmentTabular() {
        
        headers = new ArrayList<TableHeader>();
        
        TableHeader header = null;
        
        header = new TableHeader("GO Term");
        header.setSortable(true);
        header.setSortClass(SORT_CLASS.SORT_ALPHA);
        headers.add(header);
        
        header = new TableHeader("P-Value");
        header.setSortable(true);
        header.setSortClass(SORT_CLASS.SORT_FLOAT);
        headers.add(header);
        
        header = new TableHeader("Num. Annotated (in set)");
        header.setSortable(true);
        header.setSortClass(SORT_CLASS.SORT_INT);
        headers.add(header);
        
        header = new TableHeader("Total (in set)");
        header.setSortable(true);
        header.setSortClass(SORT_CLASS.SORT_INT);
        headers.add(header);
        
        header = new TableHeader("Num. Annotated (All)");
        header.setSortable(true);
        header.setSortClass(SORT_CLASS.SORT_INT);
        headers.add(header);
        
        header = new TableHeader("Total (All)");
        header.setSortable(true);
        header.setSortClass(SORT_CLASS.SORT_INT);
        headers.add(header);
        
//        for(Dataset dataset: datasets) {
//            header = new TableHeader(String.valueOf(dataset.getDatasetId()));
//            header.setWidth(2);
//            header.setSortable(false);
//            headers.add(header);
//        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumProteinsInSet() {
        return numProteinsInSet;
    }

    public void setNumProteinsInSet(int numProteinsInSet) {
        this.numProteinsInSet = numProteinsInSet;
    }

    public int getNumProteinsInUniverse() {
        return numProteinsInUniverse;
    }

    public void setNumProteinsInUniverse(int numProteinsInUniverse) {
        this.numProteinsInUniverse = numProteinsInUniverse;
    }

    public List<EnrichedGOTerm> getEnrichedTerms() {
        return enrichedTerms;
    }

    public void setEnrichedTerms(List<EnrichedGOTerm> enrichedTerms) {
        this.enrichedTerms = enrichedTerms;
    }

    public int getEnrichedTermCount() {
        return this.enrichedTerms.size();
    }
    
    @Override
    public int columnCount() {
        return headers.size();
    }

    @Override
    public TableRow getRow(int index) {
        EnrichedGOTerm term = enrichedTerms.get(index);
        TableRow row = new TableRow();
        
        String pdrUrl = "http://www.yeastrc.org/pdr/viewGONode.do?acc="+term.getGoNode().getAccession();
        TableCell cell = new TableCell(term.getGoNode().getName(), pdrUrl, true, true); // absoulte url; opens in a new window
        cell.setClassName("left_align");
        row.addCell(cell);
        
        cell = new TableCell(term.getPvalueString());
        row.addCell(cell);
        
        cell = new TableCell(""+term.getProteins().size());
        row.addCell(cell);

        cell = new TableCell(""+numProteinsInSet);
        row.addCell(cell);
        
        cell = new TableCell(""+term.getTotalAnnotatedProteins());
        row.addCell(cell);
        
        cell = new TableCell(""+numProteinsInUniverse);
        row.addCell(cell);
        
        // Present / not present in each dataset
//        int dsIndex = 0;
//        List<DatasetGoInformation> dgiList = datasetInformation.get(term);
//        for(DatasetGoInformation dgi: dgiList) {
//            cell = new TableCell();
//            cell.setId(String.valueOf(dsIndex));
//            dsIndex++;
//            
//            if(dgi == null || !dgi.isPresent()) { // dataset does not contain this protein
//                cell.setClassName("prot-not-found");
//            }
//            else {
//                String className = "prot-found";
//                cell.setClassName(className);
//            }
//            row.addCell(cell);
//        }
        
        return row;
    }

    @Override
    public int rowCount() {
        return enrichedTerms.size();
    }

    @Override
    public List<TableHeader> tableHeaders() {
        return headers;
    }

    @Override
    public void tabulate() {
        // nothing to do here
    }
    
}
