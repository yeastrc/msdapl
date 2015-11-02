package org.yeastrc.www.go;

import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.yeastrc.bio.go.GOUtils;

public class GOEnrichmentCalculatorTest extends TestCase {

    private List<Integer> proteinIds;
    private int speciesId = 4932; // yeast (S.cerevisiae)
    
    private static final Logger log = Logger.getLogger(GOEnrichmentCalculatorTest.class);
    
    static {
        
        // Had to include the following jars from tomcat installation to get this to work
        // lib/catalina.jar     --  for org.apache.naming.java.javaURLContextFactory
        // bin/tomcat-juli.jar  --  for org/apache/juli/logging/LogFactory
        // MyEclipse -> Web -> Deployment -- uncheck "Jars on web project build path"
        // so that these two jars are not included in the exported war file.
        try {
            // Create initial context
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.naming.java.javaURLContextFactory");
            System.setProperty(Context.URL_PKG_PREFIXES, 
                "org.apache.naming");            
            InitialContext ic = new InitialContext();

            ic.createSubcontext("java:");
            ic.createSubcontext("java:comp");
            ic.createSubcontext("java:comp/env");
            ic.createSubcontext("java:comp/env/jdbc");
           
            // Construct YRC_NRSEQ DataSource
            BasicDataSource ds = new BasicDataSource();
            ds.setUrl("jdbc:mysql://localhost/YRC_NRSEQ");
            ds.setUsername("root");
            ds.setPassword("");
            ic.bind("java:comp/env/jdbc/nrseq", ds);
            
            // Construct mainDb DataSource
            ds = new BasicDataSource();
            ds.setUrl("jdbc:mysql://localhost/mainDb");
            ds.setUsername("root");
            ds.setPassword("");
            ic.bind("java:comp/env/jdbc/yrc", ds);
            
            // Construct GO DataSource
            ds = new BasicDataSource();
            ds.setUrl("jdbc:mysql://localhost/mygo_201005");
            ds.setUsername("root");
            ds.setPassword("");
            ic.bind("java:comp/env/jdbc/go", ds);
            
            // Construct wormbase DataSource
            ds = new BasicDataSource();
            ds.setUrl("jdbc:mysql://localhost/wormbase");
            ds.setUsername("root");
            ds.setPassword("");
            ic.bind("java:comp/env/jdbc/wormbase", ds);
            
            // Construct sgd DataSource
            ds = new BasicDataSource();
            ds.setUrl("jdbc:mysql://localhost/sgd_static_201005");
            ds.setUsername("root");
            ds.setPassword("");
            ic.bind("java:comp/env/jdbc/sgd", ds);
            
            
        } catch (NamingException ex) {
            Logger.getLogger(GOEnrichmentCalculatorTest.class.getName()).log(Level.FATAL, null, ex);
        }
    }
    
    
    protected void setUp() throws Exception {
        super.setUp();
        proteinIds = new ArrayList<Integer>(18);
        proteinIds.add(529654); // BFR2
        proteinIds.add(531714); // BIR1
        proteinIds.add(532424); // BNA5
        proteinIds.add(529101); // CDC7
        proteinIds.add(529942); // ECM10
        proteinIds.add(532528); // JIP3
        proteinIds.add(530480); // NUT1
        proteinIds.add(532329); // PDC5
        proteinIds.add(530684); // PDC6
        proteinIds.add(529370); // PST2
        proteinIds.add(533267); // RPC19
        proteinIds.add(534365); // SAR1
        proteinIds.add(530466); // SEC27
        proteinIds.add(533772); // STI1
        proteinIds.add(529165); // THI3
        proteinIds.add(528730); // TPS1
        proteinIds.add(532165); // UBI4
        proteinIds.add(532584); // VAC14
        
//             528749 |     0 | 
//        |    529171 |     0 | 
//        |    529424 |     0 | 
//        |    532238 |     0 | 
//        |    532329 |     0 | 
//        |    532960 |     0 | 
//        |    533660 |     0 | 
//        |    530684 |     1 | 
//        |    532444 |     1 | 
//        |    533127 |     1 | 
//        |    534125 |     1 
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetAnnotatedTerms() {
        
        
    }
    
    public void testCalculate() {
        assertEquals(18, proteinIds.size());
        GOEnrichmentInput input = new GOEnrichmentInput(speciesId);
        input.setProteinIds(proteinIds);
        input.setPValCutoff(1.0);
        input.setGoAspect(GOUtils.BIOLOGICAL_PROCESS);
        
        GOEnrichmentOutput output = null;
        try {
            output = GOEnrichmentCalculator.calculate(input);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Something went wrong");
        }
        assertNotNull(output);
        
        List<EnrichedGOTerm> bpEnriched = output.getEnrichedTerms();
        assertTrue(bpEnriched.size() == 219);
        
//        for(EnrichedGOTerm term: bpEnriched) {
//            log.info(term);
//        }
        
        
//        HashMap<String, String> mapForBingoCalc = new HashMap<String, String>();
//        HashMap<String, EnrichedGOTerm> myCalculations = new HashMap<String, EnrichedGOTerm>();
//        
//        
//        for(EnrichedGOTerm term: bpEnriched) {
//        	String pval = term.getPvalueString();
//        	//log.info(term);
//        	String name = term.getGoNode().getAccession();
//        	mapForBingoCalc.put(name, pval);
//        	
//        	myCalculations.put(name, term);
//        }
//        
//        BenjaminiHochbergFDR bingoCalc = new BenjaminiHochbergFDR(mapForBingoCalc, "0.05");
//        bingoCalc.calculate();
//        
//        HashMap<String, String> correctedBingoMap = bingoCalc.getCorrectionMap();
//        
//        for(String name: correctedBingoMap.keySet()) {
//        	double bingoPval = Double.valueOf(correctedBingoMap.get(name));
//        	
//        	double myPval = myCalculations.get(name).getCorrectedPvalue();
//        	assertEquals(myPval, bingoPval, 0.00005);
//        }
    }

    public void testGetEnrichedTerms() {
        assertEquals(18, proteinIds.size());
    }

}
