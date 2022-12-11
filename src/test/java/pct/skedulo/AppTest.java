package pct.skedulo;

import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import pct.skedulo.factory.JsonMapperFactory;
import pct.skedulo.model.Show;
import pct.skedulo.process.DataProcessor;

import java.io.IOException;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    static final String[] TEST_FILES = new String[] {"skedulo-verify/example.json", "skedulo-verify/time-priority.json", "skedulo-verify/timezone.json", "skedulo-verify/overlapping.json"};

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested5
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws IOException {
        for (int i = 0 ;i <TEST_FILES.length; i++) {
            String oriFilePath = TEST_FILES[i];
            App.main(new String[]{oriFilePath});

            DataProcessor dataProcessor = new DataProcessor();
            ObjectMapper mapper = JsonMapperFactory.createObjectMapper(dataProcessor.handleTimeZone(oriFilePath));

            String filePathWithoutExtension = dataProcessor.getFilePathWithoutExtension(oriFilePath);
            List<Show> actualShows = dataProcessor.loadShowsFromFile(filePathWithoutExtension + ".optimal.json", "application/json");
            List<Show> expectedShows = dataProcessor.loadShowsFromFile(filePathWithoutExtension + ".optimal.json.expected", "application/json");

            String actualStr = mapper.writeValueAsString(actualShows);
            String expectedStr = mapper.writeValueAsString(expectedShows);
            assertEquals(expectedStr, actualStr);
        }
    }
}
