package org.yeastrc.ms.parser.sqtFile;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.yeastrc.ms.parser.sqtFile.prolucid.ProlucidResultPeptideBuilderTest;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestResultPeptideBuilderTest;

public class SQTParserTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(
                "Test for org.yeastrc.ms.parser.sqtFile");
        //$JUnit-BEGIN$
        suite.addTestSuite(HeaderTest.class);
        suite.addTestSuite(SQTParserTest.class);
        suite.addTestSuite(SequestResultPeptideBuilderTest.class);
        suite.addTestSuite(ProlucidResultPeptideBuilderTest.class);
        suite.addTestSuite(HeaderStaticModificationTest.class);
        suite.addTestSuite(HeaderDynamicModificationTest.class);
        //$JUnit-END$
        return suite;
    }

}
