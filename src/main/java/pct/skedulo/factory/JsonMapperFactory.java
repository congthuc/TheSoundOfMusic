package pct.skedulo.factory;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class JsonMapperFactory {
    static final SimpleDateFormat DATE_FORMAT_WITHOUT_TIMEZONE  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    static final SimpleDateFormat DATE_FORMAT_WITH_TIMEZONE  =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    static final TimeZone AET_TIMEZONE  = TimeZone.getTimeZone("AET");

    public static ObjectMapper createObjectMapper(boolean handledTimeZone) {
        ObjectMapper mapper = new ObjectMapper();
        if (handledTimeZone) {
            mapper.setDateFormat(DATE_FORMAT_WITH_TIMEZONE);
        } else {
            mapper.setDateFormat(DATE_FORMAT_WITHOUT_TIMEZONE);
        }
        return mapper;
    }

    public static ObjectWriter createObjectWriter(boolean handledTimeZone) {
        ObjectMapper mapper = new ObjectMapper();
        if (handledTimeZone) {
            SimpleDateFormat df = DATE_FORMAT_WITH_TIMEZONE;
            df.setTimeZone(AET_TIMEZONE);
            mapper.setDateFormat(df);
        } else {
            mapper.setDateFormat(DATE_FORMAT_WITHOUT_TIMEZONE);
        }
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

        return mapper.writer(prettyPrinter);
    }
}
