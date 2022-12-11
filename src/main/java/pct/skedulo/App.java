package pct.skedulo;

import pct.skedulo.model.Show;
import pct.skedulo.process.DataProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class App
{
    public static void main( String[] args ) throws IOException {
        String filePath = args[0];
        System.out.println( "File Path: " + filePath);
        if (! new File(filePath).exists()) {
            System.out.println(filePath + " is not exist!");
            throw new FileNotFoundException(filePath + " is not exist!");
        }

        DataProcessor dataProcessor = new DataProcessor();
        List<Show> shows = dataProcessor.loadShowsFromFile(filePath, "application/json");

        List<Show> timeOptimalScheduledShows = new ShowScheduler().optimalShows(shows);

        dataProcessor.writeShowsToFile(timeOptimalScheduledShows, filePath, "application/json");

        System.out.println( "End of Task");
    }
}
