package org.yeastrc.ms.service.database.fasta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.yeastrc.ms.domain.general.EnzymeRule;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.nrseq.domain.NrDbProtein;

public class PeptideProteinMatchingServiceTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetPeptideProteinMatch4() throws PeptideProteinMatchingServiceException {
    	
    	String proteinSequence = "PNWVKTYIKFLQNSNLGGIIPTVNGKPVRQITDDELTFLYNTFQIFAPSQFLPTWVKDILSVDYTDIMKILSKSIEK*MQSDT*QEANDIVTLANLQYNGSTPADAFETKVTNIIDR";
        String peptide = "MQSDTQEANDIVTLANLQYNGSTPADAFETK";
        
        MsEnzyme enzyme = makeEnzyme();
            
        EnzymeRule rule = new EnzymeRule(enzyme);
        List<EnzymeRule> rules = new ArrayList<EnzymeRule>(1);
        rules.add(rule);
        int minEnzymaticTermini = 1;
        
        PeptideProteinMatchingService service = new PeptideProteinMatchingService();
        service.setEnzymeRules(rules);
        service.setNumEnzymaticTermini(minEnzymaticTermini);
        service.setDoItoLSubstitution(false);
        service.setRemoveAsterisks(false);
        
        NrDbProtein dbProt = new NrDbProtein();
        dbProt.setAccessionString("YKR094C");
        dbProt.setDatabaseId(194);
        dbProt.setProteinId(532712);
        
        PeptideProteinMatch match = null;
        // We are not removing asterisks. We should NOT find a match
        service.setRemoveAsterisks(false); 
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNull(match);
        
        // We are removing asterisks. We should find a match with two enzymatic termini
        service.setRemoveAsterisks(true);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNotNull(match);
        assertEquals(2, match.getNumEnzymaticTermini());
        
        // remove asterisk from the middle of the peptide match
        proteinSequence = "PNWVKTYIKFLQNSNLGGIIPTVNGKPVRQITDDELTFLYNTFQIFAPSQFLPTWVKDILSVDYTDIMKILSKSIEK*MQSDTQEANDIVTLANLQYNGSTPADAFETKVTNIIDR";
        service.setRemoveAsterisks(true); // all asterisks will be removed before finding a match
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNotNull(match);
        assertEquals(2, match.getNumEnzymaticTermini());
        
        
        // asterisks will NOT be removed before finding a match
        service.setRemoveAsterisks(false); 
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNotNull(match);
        assertEquals(2, match.getNumEnzymaticTermini()); // we should still get NET = 2 since the '*' will be treated a protein start.
        
        // change the peptide
        peptide = "MQSDTQEANDIVTLANLQYNGSTPADAFET";
        service.setRemoveAsterisks(false); 
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNotNull(match);
        assertEquals(1, match.getNumEnzymaticTermini()); // we should get NET = 1 since we've removed the cterminal 'K'.
        
        // replace 'K' before '*' with a 'T'
        proteinSequence = "PNWVKTYIKFLQNSNLGGIIPTVNGKPVRQITDDELTFLYNTFQIFAPSQFLPTWVKDILSVDYTDIMKILSKSIET*MQSDTQEANDIVTLANLQYNGSTPADAFETKVTNIIDR";
        service.setRemoveAsterisks(true); // all asterisks will be removed before finding a match
        service.setNumEnzymaticTermini(0);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNotNull(match);
        assertEquals(0, match.getNumEnzymaticTermini()); // we should get NET = 0
        
        service.setRemoveAsterisks(false); // all asterisks will NOT be removed before finding a match
        service.setNumEnzymaticTermini(0);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNotNull(match);
        assertEquals(1, match.getNumEnzymaticTermini()); // we should get NET = 1 due to '*' being treated as protein start
        
        // Add '*' after ...FET
        proteinSequence = "PNWVKTYIKFLQNSNLGGIIPTVNGKPVRQITDDELTFLYNTFQIFAPSQFLPTWVKDILSVDYTDIMKILSKSIET*MQSDTQEANDIVTLANLQYNGSTPADAFET*KVTNIIDR";
        service.setRemoveAsterisks(true); // all asterisks will be removed before finding a match
        service.setNumEnzymaticTermini(0);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNotNull(match);
        assertEquals(0, match.getNumEnzymaticTermini()); // we should get NET = 0
        
        service.setRemoveAsterisks(false); // all asterisks will NOT be removed before finding a match
        service.setNumEnzymaticTermini(0);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNotNull(match);
        assertEquals(2, match.getNumEnzymaticTermini()); // we should get NET = 1 due to the two '*' being treated as protein start end
        
    }
    
    
    public void testGetPeptideProteinMatch2() throws PeptideProteinMatchingServiceException {
    	
		String proteinSequence = "MESQQLSNYPNISHGSACASVTSKEVHTNQDPLDVSASKIQEYDKASTKANSQQTTTPASSAVPENLHHASPQPASVPPPQNGPYPQQCMMTQNQANPSGWSFYGHPSMIPYTPYQMSPMYFPPGPQSQFPQYPSSVGTPLSTPSPESGNTFTDSSSADSDMTSTKKYVRPPPMLTSPNDFPNWVKTYIKFLQNSNLGGIIPTVNGKPVRQITDDELTFLYNTFQIFAPSQFLPTWVKDILSVDYTDIMKILSKSIEKMQSDTQEANDIVTLANLQYNGSTPADAFETKVTNIIDRLNNNGIHINNKVACQLIMRGLSGEYKFLRYTRHRHLNMTVAELFLDIHAIYEEQQGSRNSKPNYRRNPSDEKNDSRSYTNTTKPKVIARNPQKTNNSKSKTARAHNVSTSNNSPSTDNDSISKSTTEPIQLNNKHDLHLGQKLTESTVNHTNHSDDELPGHLLLDSGASRTLIRSAHHIHSASSNPDINVVDAQKRNIPINAIGDLQFHFQDNTKTSIKVLHTPNIAYDLLSLNELAAVDITACFTKNVLERSDGTVLAPIVKYGDFYWVSKKYLLPSNISVPTINNVHTSESTRKYPYPFIHRMLAHANAQTIRYSLKNNTITYFNESDVDWSSAIDYQCPDCLIGKSTKHRHIKGSRLKYQNSYEPFQYLHTDIFGPVHNLPKSAPSYFISFTDETTKFRWVYPLHDRREDSILDVFTTILAFIKNQFQASVLVIQMDRGSEYTNRTLHKFLEKNGITPCYTTTADSRAHGVAERLNRTLLDDCRTQLQCSGLPNHLWFSAIEFSTIVRNSLASPKSKKSARQHAGLAGLDISTLLPFGQPVIVNDHNPNSKIHPRGIPGYALHPSRNSYGYIIYLPSLKKTVDTTNYVILQGKESRLDQFNYDALTFDEDLNRLTASYQSFIASNEIQQSDDLNIESDHDFQSDIELHPEQPRNVLSKAVSPTDSTPPSTHTEDSKRVSKTNIRAPREVDPNISESNILPSKKRSSTPQISNIESTGSGGMHKLNVPLLAPMSQSNTHESSHASKSKDFRHSDSYSENETNHTNVPISSTGGTNNKTVPQISDQETEKRIIHRSPSIDASPPENNSSHNIVPIKTPTTVSEQNTEESIIADLPLPDLPPESPTEFPDPFKELPPINSHQTNSSLGGIGDSNAYTTINSKKRSLEDNETEIKVSRDTWNTKNMRSLEPPRSKKRIHLIAAVKAVKSIKPIRTTLRYDEAITYNKDIKEKEKYIEAYHKEVNQLLKMNTWDTDKYYDRKEIDPKRVINSMFIFNRKRDGTHKARFVARGDIQHPDTYDSGMQSNTVHHYALMTSLSLALDNNYYITQLDISSAYLYADIKEELYIRPPPHLGMNDKLIRLKKSLYGLKQSGANWYETIKSYLIKQCGMEEVRGWSCVFKNSQVTICLFVDDMILFSKDLNANKKIITTLKKQYDTKIINLGESDNEIQYDILGLEIKYQRGKYMKLGMENSLTEKIPKLNVPLNPKGRKLSAPGQPGLYIDQDELEIDEDEYKEKVHEMQKLIGLASYVGYKFRFDLLYYINTLAQHILFPSRQVLDMTYELIQFMWDTRDKQLIWHKNKPTEPDNKLVAISDASYGNQPYYKSQIGNIYLLNGKVIGGKSTKASLTCTSTTEAEIHAISESVPLLNNLSHLVQELNKKPITKGLLTDSKSTISIIISNNEEKFRNRFFGTKAMRLRDEVSGNHLHVCYIETKKNIADVMTKPLPIKTFKLLTNKWIH";
        int minEnzymaticTermini = 1;
        String peptide = "MQSDTQEANDIVTLANLQYNGSTPADAFETK";
        
        MsEnzyme enzyme = makeEnzyme();
            
        EnzymeRule rule = new EnzymeRule(enzyme);
        List<EnzymeRule> rules = new ArrayList<EnzymeRule>(1);
        rules.add(rule);
        
        PeptideProteinMatchingService service = new PeptideProteinMatchingService(194);
        service.setEnzymeRules(rules);
        service.setNumEnzymaticTermini(minEnzymaticTermini);
        
        List<PeptideProteinMatch> matches = service.getMatchingProteins(peptide);
        Set<String> accessions = new HashSet<String>();
        for(PeptideProteinMatch match: matches) {
        	accessions.add(match.getProtein().getAccessionString());
        }
        
        //System.out.println(matches.size());
        assertEquals(53, matches.size());
        assertTrue(accessions.contains("YML045W-A"));
        
        NrDbProtein dbProt = new NrDbProtein();
        dbProt.setAccessionString("YKR094C");
        dbProt.setDatabaseId(194);
        dbProt.setProteinId(532712);
        
        PeptideProteinMatch match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        
        assertNotNull(match);
        assertEquals(2, match.getNumEnzymaticTermini());
        
        peptide = "MQSDTQEANDLVTIANLQYNGSTPADAFETK"; // switch L and I
        
        service.setDoItoLSubstitution(false);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNull(match);
        
        service.setDoItoLSubstitution(true);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNotNull(match);
        assertEquals(2, match.getNumEnzymaticTermini());
        
	}
    
    public void testGetPeptideProteinMatch_clipntermM1() throws PeptideProteinMatchingServiceException {
    	
		String proteinSequence = "MEEEIAALVVDNGSGMCKAGFAGDDAPRAVFPSIVGRPRHQGVMVGMGQKDSYVGD";
        int minEnzymaticTermini = 1;
        String peptide = "EEEIAALVVDNGSGM";
        
        MsEnzyme enzyme = makeEnzyme();
            
        EnzymeRule rule = new EnzymeRule(enzyme);
        List<EnzymeRule> rules = new ArrayList<EnzymeRule>(1);
        rules.add(rule);
        
        PeptideProteinMatchingService service = new PeptideProteinMatchingService();
        service.setEnzymeRules(rules);
        service.setNumEnzymaticTermini(minEnzymaticTermini);
        service.setDoItoLSubstitution(false);
        service.setRemoveAsterisks(false);
        
        NrDbProtein dbProt = new NrDbProtein();
        dbProt.setAccessionString("Dummy");
        dbProt.setDatabaseId(0);
        dbProt.setProteinId(0);
        
        PeptideProteinMatch match = null;
        
        // We are not clipping nterm 'M' we should NOT find a match
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNull(match);
        
        // We are clipping nterm 'M' we should fina a match
        service.setClipNtermMet(true);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNotNull(match);
	}
    
    public void testGetPeptideProteinMatch_clipntermM2() throws PeptideProteinMatchingServiceException {
    	
		String proteinSequence = "MEEEIAALVVDNGSGMCKAGFAGDDAPRAVFPSIVGRPRHQGVMVGMGEEEIAALVVDNGSGMQKDSYVGD";
        String peptide = "EEEIAALVVDNGSGM";
        
        MsEnzyme enzyme = makeEnzyme();
            
        EnzymeRule rule = new EnzymeRule(enzyme);
        List<EnzymeRule> rules = new ArrayList<EnzymeRule>(1);
        rules.add(rule);
        
        PeptideProteinMatchingService service = new PeptideProteinMatchingService();
        service.setEnzymeRules(rules);
        service.setDoItoLSubstitution(false);
        service.setRemoveAsterisks(false);
        
        NrDbProtein dbProt = new NrDbProtein();
        dbProt.setAccessionString("Dummy");
        dbProt.setDatabaseId(0);
        dbProt.setProteinId(0);
        
        PeptideProteinMatch match = null;
        
        // Peptide is present twice in the protein sequence, once after the nterm Met, and once in the 
        // middle of the sequence (non-tryptic)
        
        // Look for non-tryptic matches; we should find one
        service.setNumEnzymaticTermini(0);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNotNull(match);
        
        // Look for semi-tryptic matches; We are not clipping nterm 'M'
        // we should NOT find any matches
        service.setNumEnzymaticTermini(1);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNull(match);
        
        // Look for semi-tryptic matches; 
        // We are clipping nterm 'M' we should find a match
        service.setClipNtermMet(true);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNotNull(match);
        
        // Look for fully tryptic matches;
        // We are clipping nterm 'M'; we should NOT find any matches
        service.setNumEnzymaticTermini(2);
        service.setClipNtermMet(true);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNull(match);
	}
    
    public void testGetPeptideProteinMatch_clipntermM3() throws PeptideProteinMatchingServiceException {
    	
		String proteinSequence = "MEEEI*AALVVDNGSGM*CKAGFAGDDAPRAVFPSIVGRPRHQGVMVGMGEEEIAALVVDNGSGM*QKDSYVGD";
        String peptide = "EEEIAALVVDNGSGM";
        
        MsEnzyme enzyme = makeEnzyme();
            
        EnzymeRule rule = new EnzymeRule(enzyme);
        List<EnzymeRule> rules = new ArrayList<EnzymeRule>(1);
        rules.add(rule);
        
        PeptideProteinMatchingService service = new PeptideProteinMatchingService();
        service.setEnzymeRules(rules);
        service.setDoItoLSubstitution(false);
        service.setRemoveAsterisks(false);
        
        NrDbProtein dbProt = new NrDbProtein();
        dbProt.setAccessionString("Dummy");
        dbProt.setDatabaseId(0);
        dbProt.setProteinId(0);
        
        PeptideProteinMatch match = null;
        
        // Peptide is present twice in the protein sequence, once after the nterm Met, and once in the 
        // middle of the sequence (non-tryptic)
        
        // Look for non-tryptic matches; we should find one
        service.setNumEnzymaticTermini(0);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNotNull(match);
        
        // Look for semi-tryptic matches; We are not clipping nterm 'M'
        // we should find a match (the one in the middle of the protein sequence since
        // the residue at cterm is a '*' it will be treated as the end of the protein.  
        service.setNumEnzymaticTermini(1);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNotNull(match);
        
        
        // Look for semi-tryptic matches; We are not clipping nterm 'M'; We are removing '*'
        // we should NOT find a match as neither match is semi-tryptic  
        service.setNumEnzymaticTermini(1);
        service.setRemoveAsterisks(true);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNull(match);
        
        // Look for semi-tryptic matches; We are clipping nterm 'M'; We are removing '*';
        // we should  find a match  
        service.setNumEnzymaticTermini(1);
        service.setRemoveAsterisks(true);
        service.setClipNtermMet(true);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNotNull(match);
        
	}
    
    public void testGetPeptideProteinMatch_clipntermM4() throws PeptideProteinMatchingServiceException {
    	
		String proteinSequence = "MEEEI*AALVVDNGSGR*CKAGFAGDDAPRAVFPSIVGRPRHQGVMVGMGEEEIAALVVDNGSGR*QKDSYVGD";
        String peptide = "EEEIAALVVDNGSGR";
        
        MsEnzyme enzyme = makeEnzyme();
            
        EnzymeRule rule = new EnzymeRule(enzyme);
        List<EnzymeRule> rules = new ArrayList<EnzymeRule>(1);
        rules.add(rule);
        
        PeptideProteinMatchingService service = new PeptideProteinMatchingService();
        service.setEnzymeRules(rules);
        service.setDoItoLSubstitution(false);
        service.setRemoveAsterisks(false);
        
        NrDbProtein dbProt = new NrDbProtein();
        dbProt.setAccessionString("Dummy");
        dbProt.setDatabaseId(0);
        dbProt.setProteinId(0);
        
        PeptideProteinMatch match = null;
        
        // Peptide is present twice in the protein sequence, once after the nterm Met, and once in the 
        // middle of the sequence (non-tryptic)
        
        // Look for non-tryptic matches; we should find one
        service.setNumEnzymaticTermini(0);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNotNull(match);
        
        // Look for semi-tryptic matches; We are not clipping nterm 'M'
        // we should find a match (the one in the middle of the protein sequence 
        service.setNumEnzymaticTermini(1);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNotNull(match);
        
        
        // Look for semi-tryptic matches; We are not clipping nterm 'M'; We are removing '*'
        // we should find a match (the first one)  
        service.setNumEnzymaticTermini(1);
        service.setRemoveAsterisks(true);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNotNull(match);
        
        // Look for fully-tryptic matches; We are not clipping nterm 'M'; We are not removing '*';
        // we should NOT find a match  
        service.setNumEnzymaticTermini(2);
        service.setRemoveAsterisks(false);
        service.setClipNtermMet(false);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNull(match);
        
        // Look for fully-tryptic matches; We are clipping nterm 'M'; We are not removing '*';
        // we should NOT find a match  
        service.setNumEnzymaticTermini(2);
        service.setRemoveAsterisks(false);
        service.setClipNtermMet(true);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNull(match);
        
        // Look for fully-tryptic matches; We are clipping nterm 'M'; We are not removing '*';
        // we should  find a match  
        service.setNumEnzymaticTermini(2);
        service.setRemoveAsterisks(true);
        service.setClipNtermMet(true);
        match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
        assertNotNull(match);
        
	}


	private MsEnzyme makeEnzyme() {
		MsEnzyme enzyme = new MsEnzyme() {
            @Override
            public int getId() {
                return 0;
            }
            @Override
            public String getCut() {
                return "KR";
            }
            @Override
            public String getDescription() {
                return null;
            }
            @Override
            public String getName() {
                return "Trypsin_K";
            }
            @Override
            public String getNocut() {
                return "P";
            }

            @Override
            public Sense getSense() {
                return Sense.CTERM;
            }
			};
		return enzyme;
	}

    // NOTE: COMMENTED OUT BECAUSE CREATING SUFFIX MAP TAKES A LONG TIME
//    public void testGetPeptideProteinMatch3() throws PeptideProteinMatchingServiceException {
//    	
//		String proteinSequence = "MQIFVKTLTGKTITLEVESSDTIDNVKSKIQDKEGIPPDQQRLIFAGKQLEDGRTLSDYNIQKESTLHLVLRLRGGIIEPSLKALASKYNCDKSVCRKCYARLPPRATNCRKRKCGHTNQLRPKKKLK";
//        int minEnzymaticTermini = 1;
//        String peptide = "IQDKEGIPPDQQR";
//        
//        MsEnzyme enzyme = new MsEnzyme() {
//            @Override
//            public int getId() {
//                return 0;
//            }
//            @Override
//            public String getCut() {
//                return "KR";
//            }
//            @Override
//            public String getDescription() {
//                return null;
//            }
//            @Override
//            public String getName() {
//                return "Trypsin_K";
//            }
//            @Override
//            public String getNocut() {
//                return "P";
//            }
//
//            @Override
//            public Sense getSense() {
//                return Sense.CTERM;
//            }};
//            
//        EnzymeRule rule = new EnzymeRule(enzyme);
//        List<EnzymeRule> rules = new ArrayList<EnzymeRule>(1);
//        rules.add(rule);
//        
//        NrDbProtein dbProt = new NrDbProtein();
//        dbProt.setAccessionString("YKR094C");
//        dbProt.setDatabaseId(194);
//        dbProt.setProteinId(531326);
//        
//        PeptideProteinMatchingService service = new PeptideProteinMatchingService(194);
//        service.setEnzymeRules(rules);
//        service.setNumEnzymaticTermini(minEnzymaticTermini);
//        
//        List<PeptideProteinMatch> matches = service.getMatchingProteins(peptide);
//        Set<String> accessions = new HashSet<String>();
//        for(PeptideProteinMatch match: matches) {
//        	accessions.add(match.getProtein().getAccessionString());
//        }
//        assertTrue(accessions.contains("YKR094C"));
//        assertTrue(accessions.contains("YLL039C"));
//        assertTrue(accessions.contains("YIL148W"));
//        assertTrue(accessions.contains("YLR167W"));
//        
//        PeptideProteinMatch match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
//        
//        assertNotNull(match);
//        assertEquals(2, match.getNumEnzymaticTermini());
//	}
    
    // NOTE: COMMENTED OUT BECAUSE CREATING SUFFIX MAP TAKES A LONG TIME
//	public void testGetPeptideProteinMatch1() throws PeptideProteinMatchingServiceException {
//		String proteinSequence = "MLPSWKAFKAHNILRILTRFQSTKIPDAVIGIDLGTTNSAVAIMEGKVPRIIENAEGSRTTPSVVAFTKDGERLVGEPAKRQSVINSENTLFATKRLIGRRFEDAEVQRDINQVPFKIVKHSNGDAWVEARNRTYSPAQIGGFILNKMKETAEAYLAKSVKNAVVTVPAYFNDAQRQATKDAGQIIGLNVLRVVNEPTAAALAYGLDKSEPKVIAVFDLGGGTFDISILDIDNGIFEVKSTNGDTHLGGEDFDIYLLQEIISHFKKETGIDLSNDRMAVQRIREAAEKAKIELSSTLSTEINLPFITADAAGPKHIRMPFSRVQLENITAPLIDRTVDPVKKALKDARITASDISDVLLVGGMSRMPKVADTVKKLFGKDASKAVNPDEAVALGAAIQAAVLSGEVTDVLLLDVTPLSLGIETLGGVFTKLIPRNSTIPNKKSQIFSTAASGQTSVEVKVFQGERELVKDNKLIGNFTLAGIPPAPKGTPQIEVTFDIDANGIINVSAKDLASHKDSSITVAGASGLSDTEIDRMVNEAERYKNQDRARRNAIETANKADQLANDTENSIKEFEGKLDKTDSQRLKDQISSLRELVSRSQAGDEVNDDDVGTKIDNLRTSSMKLFEQLYKNSDNPETKNGRENK";
////        String proteinSequence = "WFPCWGIYHQCLNEVSTAQQILKDVQEPVDLDNFRRIDNGTLKDTIRKLVLMARANRIANKHENEVRQVEEETIAGNSLLENANMVPLQIGTEEEIKKTPLDFGWNILPDFAFAELIAMLSGKNDRLVKMVNECTIRFSGEIGSVEMAYTLMRTLRFPVKEPFKERLIAAEFCDGFDIHIVKGTIRDLMLNSPHRDGLGLIYGTMSMVALSRTYTTRRELWTESSRSKLWLVKYLDQGETNNLAYTFVEVKQLLTLNDYDPAMQLMVWHEINLPIKKAERHERILVHFTDSNPVWGLLGSKPSLPIAPYQQIDLHRRFCEADNQLLTNVLGFLQMVLSDQRIDEHGKLVYKYDKGDSGKICFKRPRQKSSIVSFVPEFKSIKVIPKGGSARTGPVALELDHASLLKPSVHQLELTQLQPLQKGIKRFVNYYIDWAQNLNSVDKSKKYNMLWEYADNLDRGFSNQFSIERLTEPGRKLMEYLPELAAFMKETNHEGFFQRSADDLGEYWQEHWLVAMRILEHSVLEAQDVLVPSHIRMKEIISLAAKQRSLSESKIAVMLPYVLAQPHAKGLDSLLSLLSRSVIQNPQHIRSILQPLVELWTGIQILNFGEHMAQTAEPIGGFTFWLTLLRLADQLSSSESLSISHFFGKIAPIVHRHILNSSYHVEKADFTNVGIMGNDFENIDTVSSADSGEQKKKSVSTLMSIVEFNALAWNHWAKYWTNDFHTALLYSGLISDPNSLRWKPQLCVRWEGQKLFCRALLKTYDEVHRPVRKSQQPVSQAIMNNPDLGLDHAMRSTFNILQKLAEDQLGTAWLYKLQAYVVPPSAKATNPHDPDDTEELLTNLVKKALAMRGSKRCLNAFKIRVQADEKPKIVLSRVRLIRQWVDINKQCGLLRTNWTERMTLRKDSNQPLKKYKIIEELEAIIQARVVVNYARNYSENVLASLETVLLDRANFIHVEAKKFNNRHLCLIADYFEKDPSQSKMVSTYQAIEDWQELGWAAGAALPAMAKKVEPKATGWKESALKSLEEWEGLAYLSRLKGMMVEVSDEGAAEKENYAALADEWRQLKEYWTEKLQLENHQQAHKLIGIASDTQHLQNNISILAEITSNKPEELFEVEKYHLAKAFAHCKQAYKGLTHIPIPLPKDDHEMFEVLNLLMQYIEPPNESSSLAKCLAQILDEQYSTQLEVWCSSFSANFLERALPYYVSVLSSCSRLCASPSEKLLQISLRRIWEQWDEKTKQQSCYWANKLINQNVPLKTVQMEDEYNKREPVENEKDFIINTPLCENNLLKNVLQDYVSHQIRNRLLAKNIVPVFVVFDTGLQLLLLSLTNMTAKTLERDGNNLIRVLAQVIRSSMESLNINKALRGLTIISIKKLSGASYETMRVVIPMILHSYDELNPGFTVLSKLIRIPVIRKNSQDNELIDLFFTLTEPVFRKFEGELAKSISEIVSIITIQLKIIPFFERIVGYIKEVHPRIHQKVISILSGLQQFYFDLQSPPCSRMVLIIGPIIQDLFSVCRLGLNQFIHMIAQIAATHHISLSPDNLIKMLNHIVVTPYYEDNSPSVGQMLLAIDISPANQEVSSKSNSTVEIERHKYPDLAGLIGILRVTGRRIHPNNETKLINILIGLLEPYDLLPGVVYGSSAALQGLTTLAADRKFSNSQDQFTNIILPMLEKLYRTMEKGGVVSLEGLVKLATSAVASSADQCKPLIVDLIPDIYPKAVEDSSNILTCLLTASEEKKKPMNSFKLQTLLELLTKRLSPVVYAPNVSSLRGIIKIAELQIGFIEDNLAMFLLRLNDPQALQPDFNSGLHQLIELRIEAVPDTIAIMLLKSLVESVSHLAHVSTQKCIDDKIFLDCSTLAALKRVSSDEHEIYSITILRVFETLSYQHHILQLMKFCQILIQADTIDDNSEGTKKMFSQNRSKRAKEISFQNNFDYQNSQIFKEGSLSISLLNLIRSNVTSELSPIKENLIMLTEQMHDSMPCNLMLNLLDKNLHKAFAPGLACALKGICYFLDKEFQKRVKFKTRLGERINDLILTMYPSISSGVEFAIDGISVLIFPKDSNNAANMDINKLYRLYHVMIRDLYKKTFIAPDFAALLPLIAYVERRIVDFKYEKYKMTSKYIDDYKDRLYPAKLSLLERFVLLTAHVSDNTNLSLGHTCGQFLRQFWQKGLAPDRDQIITLCKGLAVAADLRIILKADRLPVWINDLISNVYPYLLYPSNDALAKIILLAAHRRYELKSSSSNNDATLTLWDICTRVEFEVFDSTLTGGPVTLRGLTNAALRMVEIDSSPILVRLYNALRSTQNPLEETSLYFSILTDVALIGGIKESSTFGHILEFIKNNLSNSFRQFQEASVERALSTLTTSLENAGSAREQPVDSKLKDFILNLTSFTTDLESDVFSIKGIHGASGTIVRGSDNPGNHNSDTNSTTSMEDDHSHSKHTLKKRTRHKGEARQRLSLLNPPTTYKNIYKNM";
//        int minEnzymaticTermini = 1;
//        String peptide = "IIENAEGSRTTPS";
////        String peptide = "LLTNVLGFLQMVLSDQRIDEHG";
//        MsEnzyme enzyme = new MsEnzyme() {
//            @Override
//            public int getId() {
//                return 0;
//            }
//            @Override
//            public String getCut() {
//                return "KR";
//            }
//            @Override
//            public String getDescription() {
//                return null;
//            }
//            @Override
//            public String getName() {
//                return "Trypsin_K";
//            }
//            @Override
//            public String getNocut() {
//                return "P";
//            }
//
//            @Override
//            public Sense getSense() {
//                return Sense.CTERM;
//            }};
//        EnzymeRule rule = new EnzymeRule(enzyme);
//        List<EnzymeRule> rules = new ArrayList<EnzymeRule>(1);
//        rules.add(rule);
//        
//        NrDbProtein dbProt = new NrDbProtein();
//        dbProt.setAccessionString("YEL030W");
//        dbProt.setDatabaseId(178);
//        dbProt.setProteinId(529942);
//        
//        PeptideProteinMatchingService service = new PeptideProteinMatchingService(178);
//        
//        PeptideProteinMatch match = service.getPeptideProteinMatch(dbProt, peptide, proteinSequence);
//        
//        assertNotNull(match);
//        assertEquals(1, match.getNumEnzymaticTermini());
//	}

}
