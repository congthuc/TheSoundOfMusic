package pct.skedulo.process;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.io.FileUtils;
import pct.skedulo.factory.JsonMapperFactory;
import pct.skedulo.model.Show;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DataProcessor {

    static final String JSON_MIME_TYPE = "application/json";

    public List<Show> loadShowsFromFile(String filePath, String type) throws IOException {
        String showDataStr = readFileToString(filePath);
        System.out.println("Original data");
        System.out.println(showDataStr);
        List<Show> shows;
        if (JSON_MIME_TYPE.equals(type)) {
            ObjectMapper mapper = JsonMapperFactory.createObjectMapper(handleTimeZone(filePath));
            shows = mapper.readValue(showDataStr, new TypeReference<ArrayList<Show>>() {});
        } else {
            throw new IOException("Not support this file type: " + type);
        }

        return shows;
    }

    public void writeShowsToFile(LinkedList<Show> shows, String filePath, String type) throws IOException {

        if (JSON_MIME_TYPE.equals(type)) {
            try {
                ObjectWriter writer = JsonMapperFactory.createObjectWriter(handleTimeZone(filePath));
                String showAsString = writer.writeValueAsString(shows);
                System.out.println("===========Optimal data=========");
                System.out.println(showAsString);
                Path path = Paths.get(getFilePathWithoutExtension(filePath) + ".optimal.json");
                byte[] bytes = showAsString.getBytes();
                Files.write(path, bytes);
            } catch (IOException ex) {
                throw new IOException("Could not write to file " + ex.getMessage());
            }

        } else {
            throw new IOException("Not support this file type: " + type);
        }
    }

    public String readFileToString(String filePath) {
        try {
            return FileUtils.readFileToString(new File(filePath), Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean handleTimeZone(String filePath) {
        return filePath.contains("timezone");
    }

    public static String getFilePathWithoutExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        return (dotIndex == -1) ? filePath : filePath.substring(0, dotIndex);
    }

}
