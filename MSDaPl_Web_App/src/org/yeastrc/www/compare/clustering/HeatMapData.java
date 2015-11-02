/**
 * ClusteredGroupDataset.java
 * @author Vagisha Sharma
 * Apr 23, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare.clustering;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.www.compare.ComparisonProtein;
import org.yeastrc.www.compare.ProteinComparisonDataset;
import org.yeastrc.www.compare.ProteinGroupComparisonDataset;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.DatasetProteinInformation;
import org.yeastrc.www.compare.graph.ComparisonProteinGroup;

/**
 * 
 */
public class HeatMapData {

	private List<String> datasetLabels;
	private List<HeatMapRow> rows;
	
	public HeatMapData (ProteinGroupComparisonDataset grpComparison) {
		
		datasetLabels = new ArrayList<String>(grpComparison.getDatasetCount()+1);
		
		for(Dataset ds: grpComparison.getDatasets()) {
			if(ds.getDatasetName() != null && ds.getDatasetName().length() > 0) {
				datasetLabels.add((ds.getDatasetName()+"(ID"+ds.getDatasetId())+")");
			}
			else {
				datasetLabels.add(("ID_"+ds.getDatasetId()));
			}
		}
		
		rows = new ArrayList<HeatMapRow>(grpComparison.getTotalProteinGroupCount() + 1);
		
		int index = 0;
		for(ComparisonProteinGroup grp: grpComparison.getProteinsGroups()) {
			
			HeatMapRow row = new HeatMapRow();
			row.setIndexInList(index);
			index += grp.getProteins().size();
			rows.add(row);
			
			ComparisonProtein protein = grp.getProteins().get(0);
			
			List<HeatMapCell> cells = new ArrayList<HeatMapCell>();
			
			// add cell for molecular wt.
			String molWtCellColor = getColorForMolWt(protein.getMolecularWeight());
			HeatMapCell cell1 = new HeatMapCell();
			cell1.setLabel("");
			cell1.setHexColor(molWtCellColor);
			cells.add(cell1);
			
			row.setRowName(protein.getProteinListing().getFastaReferences().get(0).getShortAccession());
			
			List<String> allNames = new ArrayList<String>();
			for(ComparisonProtein prot: grp.getProteins()) {
				allNames.add(prot.getProteinListing().getFastaReferences().get(0).getShortAccession());
			}
			row.setRowAllNames(allNames);
			
			for(Dataset ds: grpComparison.getDatasets()) {
				
				HeatMapCell cell = new HeatMapCell();
				cells.add(cell);
				cell.setLabel("");
				DatasetProteinInformation dpi = protein.getDatasetProteinInformation(ds);
				if(dpi == null || !dpi.isPresent()) {
					cell.setHexColor("#FFFFFF");
				}
				else {
					float heatMapSpectrumCount = dpi.getHeatMapSpectrumCount();
					cell.setHexColor(grpComparison.getHeatMapColor(heatMapSpectrumCount));
				}
			}
			
			// http://chart.apis.google.com/chart?chs=320x200&cht=bvs&chd=t:1,19,27,53,61&chds=0,61&chco=FFCC33&chxt=x,y&chxr=1,0,61,10&chxl=0:|Jan|Feb|Mar|Apr|May
			String plotUrl = getPlotUrl(protein, grpComparison.getDatasets());
				
			row.setPlotUrl(plotUrl);
			
			row.setCells(cells);
		}
	}


	private String getPlotUrl(ComparisonProtein protein, List<? extends Dataset> datasets) {
		
		String plotUrl = "http://chart.apis.google.com/chart?cht=bhg&chxt=x,y";
		String data1 = "";
		String data2 = "";
		//String colors = "";
		String xrange = "";
		String scale = "";
		String xlabel = "";
		String chartSize = "chs=";
		
		// chart title
		//String title = "&chtt="+protein.getProteinListing().getFastaReferences().get(0).getShortAccession();
		//plotUrl += title;
		
		int idx = 0;
		int maxSc = 0;
		int maxLabel = 0;
		
		boolean allDatasetsHaveNames = true;
		for(Dataset ds: datasets) {
			if(ds.getDatasetName() == null || ds.getDatasetName().length() == 0) {
				allDatasetsHaveNames = false;
				break;
			}
		}
		
		for(Dataset ds: datasets) {
			
			DatasetProteinInformation dpi = protein.getDatasetProteinInformation(ds);
			
			if(allDatasetsHaveNames) {
				xlabel = "|"+ds.getDatasetName() + xlabel;
			}
			else {
				xlabel = "|"+ds.getDatasetId() + xlabel;
			}
			
			//colors += "|"+DatasetColor.get(idx).hexValue();
			idx++;
			
			maxLabel = (int)Math.max(maxLabel, (""+ds.getDatasetId()).length());
			
			if(dpi == null || !dpi.isPresent()) {
				data1+=",0";
				data2 += ",0";
			}
			else {
				data1 += ","+dpi.getNormalizedSpectrumCountRounded();
				data2 += ","+dpi.getSpectrumCount();
				maxSc = (int) Math.max(maxSc, dpi.getNormalizedSpectrumCount());
			}
			
		}
		int barWidth = (maxLabel*6)/2;
		if(barWidth < 12)	barWidth = 12;
		String barSpacing = barWidth+",1,10";
		int chartHeight = ((barWidth*2 + 1 + 10)*datasets.size())+50;
		chartSize += "250x"+chartHeight;
		plotUrl += "&"+chartSize;
		plotUrl += "&chbh="+barSpacing;
		
		//colors = colors.substring(1);
		plotUrl += "&chco=C6D9FD,4D89F9";
		
		data1 = data1.substring(1);
		data1 = "t:"+data1;
		data2 = data2.substring(1);
		data2 = "|"+data2;
		plotUrl += "&chd="+data1+data2;
		
		
		int div = (int)Math.ceil((double)maxSc / 10.0);
		if(div > 5) {
			div = Math.round((float)div / 5.0f) * 5;
		}
		xrange = "0,0,"+maxSc+","+div;
		plotUrl += "&chxr="+xrange;
		
		scale = "0,"+maxSc;
		plotUrl += "&chds="+scale;
		
		// x-axis labels
		plotUrl += "&chxl=1:"+xlabel+"|";
		
		
		// bar labels
		plotUrl += "&chm=N,000000,0,,10|N,000000,1,,10";
		
		// legend
		plotUrl += "&chdl=Norm.+Spectrum+Count|Spectrum+Count&chdlp=b";
		return plotUrl;
	}
	
	
	public HeatMapData (ProteinComparisonDataset comparison) {
		
		datasetLabels = new ArrayList<String>(comparison.getDatasetCount()+1);
		
		for(Dataset ds: comparison.getDatasets()) {
			datasetLabels.add(("ID_"+ds.getDatasetId()));
		}
		
		rows = new ArrayList<HeatMapRow>(comparison.getTotalProteinCount() + 1);
		
		int index = 0;
		for(ComparisonProtein protein: comparison.getProteins()) {
			
			HeatMapRow row = new HeatMapRow();
			row.setIndexInList(index++);
			rows.add(row);
			
			
			List<HeatMapCell> cells = new ArrayList<HeatMapCell>();
			
			row.setRowName(protein.getProteinListing().getFastaReferences().get(0).getShortAccession());
			List<String> allNames = new ArrayList<String>(1);
			allNames.add(row.getRowName());
			row.setRowAllNames(allNames);
			
			// add cell for molecular wt.
			String molWtCellColor = getColorForMolWt(protein.getMolecularWeight());
			HeatMapCell cell1 = new HeatMapCell();
			cell1.setLabel("");
			cell1.setHexColor(molWtCellColor);
			cells.add(cell1);
			
			for(Dataset ds: comparison.getDatasets()) {
				
				HeatMapCell cell = new HeatMapCell();
				cells.add(cell);
				cell.setLabel("");
				DatasetProteinInformation dpi = protein.getDatasetProteinInformation(ds);
				if(dpi == null || !dpi.isPresent()) {
					cell.setHexColor("#FFFFFF");
				}
				else {
					float heatMapSpectrumCount = dpi.getHeatMapSpectrumCount();
					cell.setHexColor(comparison.getHeatMapColor(heatMapSpectrumCount));
				}
				
				// http://chart.apis.google.com/chart?chs=320x200&cht=bvs&chd=t:1,19,27,53,61&chds=0,61&chco=FFCC33&chxt=x,y&chxr=1,0,61,10&chxl=0:|Jan|Feb|Mar|Apr|May
				String plotUrl = getPlotUrl(protein, comparison.getDatasets());
					
				row.setPlotUrl(plotUrl);
			}
			row.setCells(cells);
		}
	}

	private String getColorForMolWt(float molecularWeight) {
		
//		int r = 255;
//		int g = 255;
//		int b = 0;
//		
//		int bin = (int) (molecularWeight / 10000);
//		//r += 25 * bin;
//		g -= 25 * bin;
//		//b -= 25 * bin;
//		r = Math.min(255, r);
//		g = Math.max(0, g);
//		//b = Math.max(0, bin);
		if(molecularWeight > 0 && molecularWeight <= 12000)
			return ProteinGroupComparisonDataset.hexValue(255, 255,0);  //  0 to 12000; yellow
		else if(molecularWeight > 12000 && molecularWeight <= 18000)
			return ProteinGroupComparisonDataset.hexValue(255, 180,0);  // 12000 to 18000;  orange
		else if(molecularWeight > 18000 && molecularWeight <= 22000)
			return ProteinGroupComparisonDataset.hexValue(255, 0,0);	// 18000 to 22000; red
		else if(molecularWeight > 22000 && molecularWeight <= 25000)
			return ProteinGroupComparisonDataset.hexValue(255,0,180);	// 22000 to 25000; 
		else if(molecularWeight > 25000 && molecularWeight <= 35000)
			return ProteinGroupComparisonDataset.hexValue(255,0,255);	// 25000 to 35000; magenta
		else if(molecularWeight > 35000 && molecularWeight <= 40000)
			return ProteinGroupComparisonDataset.hexValue(180,0,255);	// 35000 to 40000
		else if(molecularWeight > 40000 && molecularWeight <= 50000)
			return ProteinGroupComparisonDataset.hexValue(0,0,255);		// 40000 to 50000; blue
		else if(molecularWeight > 50000 && molecularWeight <= 60000)
			return ProteinGroupComparisonDataset.hexValue(0,180,255);	// 50000 to 60000; 
		else if(molecularWeight > 60000 && molecularWeight <= 70000)
			return ProteinGroupComparisonDataset.hexValue(0,255,255);	// 60000 to 70000; cyan
		else if(molecularWeight > 70000 && molecularWeight <= 80000)
			return ProteinGroupComparisonDataset.hexValue(0,255,180);	// 70000 to 80000; 
		else if(molecularWeight > 80000 && molecularWeight <= 90000)
			return ProteinGroupComparisonDataset.hexValue(0,255,0);		// 80000 to 90000; green
		else
			return ProteinGroupComparisonDataset.hexValue(0,0,0);		// 90000 to 100000; black
		//return ProteinGroupComparisonDataset.hexValue(r, g, b);
	}


	public List<String> getDatasetLabels() {
		return datasetLabels;
	}
	
	public List<HeatMapRow> getRows() {
		return this.rows;
	}
	
	public static final class HeatMapRow {
		
		private String rowName;
		private List<String> rowAllNames;
		private List<HeatMapCell> cells;
		private int indexInList;
		private String plotUrl = "NULL";
		
		public String getRowName() {
			return rowName;
		}
		public void setRowName(String rowName) {
			this.rowName = rowName;
		}
		public List<String> getRowAllNames() {
			return rowAllNames;
		}
		public void setRowAllNames(List<String> rowAllNames) {
			this.rowAllNames = rowAllNames;
		}
		public List<HeatMapCell> getCells() {
			return cells;
		}
		public void setCells(List<HeatMapCell> cells) {
			this.cells = cells;
		}
		public int getIndexInList() {
			return indexInList;
		}
		public void setIndexInList(int indexInList) {
			this.indexInList = indexInList;
		}
		public String getPlotUrl() {
			return plotUrl;
		}
		public void setPlotUrl(String url) {
			this.plotUrl = url;
		}
	}
	
	public static final class HeatMapCell {
		private String hexColor;
		private String label;
		
		public String getHexColor() {
			return hexColor;
		}
		public void setHexColor(String hexColor) {
			this.hexColor = hexColor;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
	}
	
}
