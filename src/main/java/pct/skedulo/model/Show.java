package pct.skedulo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Show {

    @JsonProperty("band")
    String band;
    @JsonProperty("start")
    Date start;
    @JsonProperty("finish")
    Date finish;
    @JsonProperty("priority")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer priority;

    public Show() {
    }

    public Show(String band, Date start, Date finish) {
        this.band = band;
        this.start = start;
        this.finish = finish;
    }

    public String getBand() {
        return band;
    }

    public Date getStart() {
        return start;
    }

    public Integer getPriority() {
        return priority;
    }

    public Date getFinish() {
        return finish;
    }

    @Override
    public String toString() {
        return "Brand: " + band + ", start:" + start.toString() + ", end: " + finish.toString() +", priority: " + priority;
    }
}
