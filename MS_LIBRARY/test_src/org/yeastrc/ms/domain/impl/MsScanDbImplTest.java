package org.yeastrc.ms.domain.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.yeastrc.ms.domain.run.Peak;
import org.yeastrc.ms.domain.run.PeakStorageType;
import org.yeastrc.ms.domain.run.impl.ScanDb;
import org.yeastrc.ms.util.PeakStringBuilder;

public class MsScanDbImplTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testParsePeaksAsString() throws IOException, ClassNotFoundException {
        ScanDb scanDb = new ScanDb();
        scanDb.setPeakStorageType(PeakStorageType.STRING);
        PeakStringBuilder builder = new PeakStringBuilder();
        List<String[]> peaks = new ArrayList<String[]>(10);
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            String[] peak = new String[2];
            peak[0] = Double.toString(r.nextDouble());
            peak[1] = Float.toString(r.nextFloat());
            peaks.add(peak);
            builder.addPeak(peak[0], peak[1]);
        }
        scanDb.setPeakData(builder.getPeaksAsString().getBytes());
        List<Peak> peakList = scanDb.getPeaks();
        int i = 0;
        for (Peak peak: peakList) {
            String[] peakStr = peaks.get(i);
            assertEquals(peak.getMz(), Double.parseDouble(peakStr[0]));
            assertEquals(peak.getIntensity(), Float.parseFloat(peakStr[1]));
            i++;
        }
        assertEquals(10, i);
    }

}
