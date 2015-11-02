package org.yeastrc.www.compare.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.www.compare.ProteinComparisonDataset;
import org.yeastrc.www.compare.ProteinGroupComparisonDataset;
import org.yeastrc.www.compare.dataset.DatasetColor;

public class VennDiagramCreator {

    private static VennDiagramCreator instance;

    private VennDiagramCreator() {}

    public static VennDiagramCreator instance() {
        if(instance == null)
            instance = new VennDiagramCreator();
        return instance;
    }

    public String getChartUrl(ProteinComparisonDataset comparison) {

        if(comparison.getDatasetCount() == 2) {

            int ds1 = 0;
            int ds2 = 0;
            String label1 = null;
            String label2 = null;
            String[] colors = new String[3];
            
            if(comparison.getProteinCount(0) > comparison.getProteinCount(1)) {
                ds1 = comparison.getProteinCount(0);
                ds2 = comparison.getProteinCount(1);
                label1 = "ID"+comparison.getDatasets().get(0).getDatasetId();
                label2 = "ID"+comparison.getDatasets().get(1).getDatasetId();
                colors[0] = DatasetColor.get(0).hexValue();
                colors[1] = DatasetColor.get(1).hexValue();
                colors[2] = DatasetColor.get(2).hexValue();
            }
            else {
                ds1 = comparison.getProteinCount(1);
                ds2 = comparison.getProteinCount(0);
                label1 = "ID"+comparison.getDatasets().get(1).getDatasetId();
                label2 = "ID"+comparison.getDatasets().get(0).getDatasetId();
                colors[0] = DatasetColor.get(1).hexValue();
                colors[1] = DatasetColor.get(0).hexValue();
                colors[2] = DatasetColor.get(2).hexValue();
            }
            
            int common1_2 = comparison.getCommonProteinCount(0, 1);
            
            
            StringBuilder googleChartUrl = createChartUrl(ds1, ds2,
                    common1_2, new String[]{label1, label2}, colors);
            return googleChartUrl.toString();
        }
        else if(comparison.getDatasetCount() == 3) {

            List<DatasetProteinCount> protCountRanks = new ArrayList<DatasetProteinCount>(3);
            for(int index = 0; index < comparison.getDatasetCount(); index++) {
                protCountRanks.add(new DatasetProteinCount(index, comparison.getProteinCount(index)));
            }
            Collections.sort(protCountRanks, new Comparator<DatasetProteinCount>() {
                public int compare(DatasetProteinCount o1,DatasetProteinCount o2) {
                    return Integer.valueOf(o2.proteinCount).compareTo(o1.proteinCount);
                }});
            
            int ds1 = comparison.getProteinCount(protCountRanks.get(0).datasetIndex);
            int ds2 = comparison.getProteinCount(protCountRanks.get(1).datasetIndex);
            int ds3 = comparison.getProteinCount(protCountRanks.get(2).datasetIndex);

            int common1_2 = comparison.getCommonProteinCount(protCountRanks.get(0).datasetIndex, protCountRanks.get(1).datasetIndex);
            int common1_3 = comparison.getCommonProteinCount(protCountRanks.get(0).datasetIndex, protCountRanks.get(2).datasetIndex);
            int common2_3 = comparison.getCommonProteinCount(protCountRanks.get(1).datasetIndex, protCountRanks.get(2).datasetIndex);
            int common1_2_3 = 0; // commonIds(nrseqIds1, nrseqIds2, nrseqIds3);

            String[] colors = new String[3];
            colors[0] = DatasetColor.get(protCountRanks.get(0).datasetIndex).hexValue();
            colors[1] = DatasetColor.get(protCountRanks.get(1).datasetIndex).hexValue();
            colors[2] = DatasetColor.get(protCountRanks.get(2).datasetIndex).hexValue();
            
            StringBuilder googleChartUrl = createChartUrl(ds1, ds2, ds3,
                    common1_2, common1_3, common2_3, common1_2_3,
                    new String[]{"ID"+comparison.getDatasets().get(protCountRanks.get(0).datasetIndex).getDatasetId(), 
                    "ID"+comparison.getDatasets().get(protCountRanks.get(1).datasetIndex).getDatasetId(), 
                    "ID"+comparison.getDatasets().get(protCountRanks.get(2).datasetIndex).getDatasetId()},
                    colors);

            return googleChartUrl.toString();
        }
        return null;
    }
    
    public String getChartUrl(ProteinGroupComparisonDataset comparison) {

        if(comparison.getDatasetCount() == 2) {

            int ds1 = 0;
            int ds2 = 0;
            String label1 = null;
            String label2 = null;
            String[] colors = new String[3];
            
            if(comparison.getProteinCount(0) > comparison.getProteinCount(1)) {
                ds1 = comparison.getProteinGroupCount(0);
                ds2 = comparison.getProteinGroupCount(1);
                label1 = "ID"+comparison.getDatasets().get(0).getDatasetId();
                label2 = "ID"+comparison.getDatasets().get(1).getDatasetId();
                colors[0] = DatasetColor.get(0).hexValue();
                colors[1] = DatasetColor.get(1).hexValue();
                colors[2] = DatasetColor.get(2).hexValue();
            }
            else {
                ds1 = comparison.getProteinGroupCount(1);
                ds2 = comparison.getProteinGroupCount(0);
                label1 = "ID"+comparison.getDatasets().get(1).getDatasetId();
                label2 = "ID"+comparison.getDatasets().get(0).getDatasetId();
                colors[0] = DatasetColor.get(1).hexValue();
                colors[1] = DatasetColor.get(0).hexValue();
                colors[2] = DatasetColor.get(2).hexValue();
            }
            
            int common1_2 = comparison.getCommonProteinGroupCount(0, 1);
            
            
            StringBuilder googleChartUrl = createChartUrl(ds1, ds2,
                    common1_2, new String[]{label1, label2}, colors);
            return googleChartUrl.toString();
        }
        else if(comparison.getDatasetCount() == 3) {

            List<DatasetProteinCount> protCountRanks = new ArrayList<DatasetProteinCount>(3);
            for(int index = 0; index < comparison.getDatasetCount(); index++) {
                protCountRanks.add(new DatasetProteinCount(index, comparison.getProteinCount(index)));
            }
            Collections.sort(protCountRanks, new Comparator<DatasetProteinCount>() {
                public int compare(DatasetProteinCount o1,DatasetProteinCount o2) {
                    return Integer.valueOf(o2.proteinCount).compareTo(o1.proteinCount);
                }});
            
            int ds1 = comparison.getProteinGroupCount(protCountRanks.get(0).datasetIndex);
            int ds2 = comparison.getProteinGroupCount(protCountRanks.get(1).datasetIndex);
            int ds3 = comparison.getProteinGroupCount(protCountRanks.get(2).datasetIndex);

            int common1_2 = comparison.getCommonProteinGroupCount(protCountRanks.get(0).datasetIndex, protCountRanks.get(1).datasetIndex);
            int common1_3 = comparison.getCommonProteinGroupCount(protCountRanks.get(0).datasetIndex, protCountRanks.get(2).datasetIndex);
            int common2_3 = comparison.getCommonProteinGroupCount(protCountRanks.get(1).datasetIndex, protCountRanks.get(2).datasetIndex);
            int common1_2_3 = 0; // commonIds(nrseqIds1, nrseqIds2, nrseqIds3);

            String[] colors = new String[3];
            colors[0] = DatasetColor.get(protCountRanks.get(0).datasetIndex).hexValue();
            colors[1] = DatasetColor.get(protCountRanks.get(1).datasetIndex).hexValue();
            colors[2] = DatasetColor.get(protCountRanks.get(2).datasetIndex).hexValue();
            
            StringBuilder googleChartUrl = createChartUrl(ds1, ds2, ds3,
                    common1_2, common1_3, common2_3, common1_2_3,
                    new String[]{"ID"+comparison.getDatasets().get(protCountRanks.get(0).datasetIndex).getDatasetId(), 
                    "ID"+comparison.getDatasets().get(protCountRanks.get(1).datasetIndex).getDatasetId(), 
                    "ID"+comparison.getDatasets().get(protCountRanks.get(2).datasetIndex).getDatasetId()},
                    colors);

            return googleChartUrl.toString();
        }
        return null;
    }
    
    private static class DatasetProteinCount {
        private int datasetIndex;
        private int proteinCount;
        public DatasetProteinCount(int datasetIndex, int proteinCount) {
            this.datasetIndex = datasetIndex;
            this.proteinCount = proteinCount;
        }
    }

    private StringBuilder createChartUrl(int A, int B, int AB, String[] legends, String[] colors) {
        return createChartUrl(A, B, 0, AB, 0, 0, 0, legends, colors);
    }
    
    private StringBuilder createChartUrl(int num1, int num2, int num3, 
            int common1_2, int common1_3, int common2_3, int common1_2_3, String[] legends, String[] colors) {

        int maxNum = Math.max(num1, num2);
        maxNum = Math.max(maxNum, num3);

        int A = calcPercentage(num1, maxNum);
        int B = calcPercentage(num2, maxNum);
        int C = calcPercentage(num3, maxNum);
        int AB = calcPercentage(common1_2, maxNum);
        int AC = calcPercentage(common1_3, maxNum);
        int BC = calcPercentage(common2_3, maxNum);
        int ABC = calcPercentage(common1_2_3, maxNum);


//      http://chart.apis.google.com/chart?cht=v&chs=200x100&chd=t:100,80,0,30,0,0,0
        StringBuilder url = new StringBuilder();
        url.append("http://chart.apis.google.com/chart?cht=v");
        url.append("&chs=170x100");
        url.append("&chd=t:");
        url.append(A+",");      // A
        url.append(B+",");      // B
        url.append(C+",");      // C
        url.append(AB+",");     // A & B
        url.append(AC+",");     // A & C
        url.append(BC+",");     // B & C
        url.append(ABC);        // A & B & C

//      chart colors
//        url.append("&chco="+DatasetColor.get(0).hexValue()+","+DatasetColor.get(1).hexValue()+","+DatasetColor.get(2).hexValue());
        url.append("&chco="+colors[0]+","+colors[1]+","+colors[2]);

//      Chart legend
//      chdl=First|Second|Third
//      chco=ff0000,00ff00,0000ff
//      chdlp=t
//      url.append("&chdpl=t");
//      url.append("&chdl=");

//      if(legends.length == 2) {
//      url.append(num1+": "+legends[0]);
//      url.append("|"+num2+": "+legends[1]);
//      url.append("|"+common1_2+": "+legends[0]+" AND "+legends[1]);

//      url.append("&chco=ff0000,00ff00,AAAA00");
//      }

//      if(legends.length == 3) {
//      url.append(num1+": "+legends[0]);
//      url.append("|"+num2+": "+legends[1]);
//      url.append("|"+num3+": "+legends[2]);
//      url.append("|"+common1_2+": "+legends[0]+" AND "+legends[1]);
//      url.append("|"+common1_3+": "+legends[0]+" AND "+legends[2]);
//      url.append("|"+common2_3+": "+legends[1]+" AND "+legends[2]);
//      url.append("|"+common1_2_3+": "+legends[0]+" AND "+legends[1]+" AND "+legends[2]);

//      url.append("&chco=ff0000,00ff00,0000ff,AAAA00,AA00AA,00AAAA,AAAAFF");
//      }
//      url.append("&chf=bg,s,F2F2F2");
//      return url.toString();
        return url;
    }

    private int calcPercentage(int num1, int num2) {
        return (int)((num1*100.0)/num2);
    }
    
//    public static void main(String[] args) {
//        
//        int A = 100;
//        int B = 80;
//        int C = 60;
//        
//        int AB = 30;
//        int AC = 25;
//        int BC = 20;
//        int ABC = 10;
//        
//        String[] legends = new String[]{"ID1", "ID2", "ID3"};
//        
//        String url = VennDiagramCreator.instance().createChartUrl(A, B, C, AB, AC, BC, ABC, legends).toString();
//        System.out.println(url);
//        
//        C = 0;
//        AC = 0;
//        BC = 0;
//        ABC = 0;
//        legends = new String[]{"ID1", "ID2"};
//        url = VennDiagramCreator.instance().createChartUrl(A, B, C, AB, AC, BC, ABC, legends).toString();
//        System.out.println(url);
//    }
}
