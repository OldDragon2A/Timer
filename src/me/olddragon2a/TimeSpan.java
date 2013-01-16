package me.olddragon2a;

import java.io.Serializable;

import org.jdom2.Element;
import org.joda.time.DateTime;
import org.joda.time.Period;

/**
 * @author OldDragon2A
 * 
 */
public class TimeSpan implements Serializable {
  private static final long serialVersionUID = -7619046996783076689L;
  private DateTime start;
  private DateTime stop;
  private Period period;

  public TimeSpan(DateTime start, DateTime stop) {
    this.start = start;
    this.stop = stop;
    this.period = new Period(start, stop);
  }
  
  public TimeSpan(Element timespan) {
    this.start = DateTime.parse(timespan.getChildTextNormalize("start"));
    this.stop = DateTime.parse(timespan.getChildTextNormalize("stop"));
    this.period = new Period(start, stop);
  }

  public DateTime getStart() { return start; }
  public void setStart(DateTime start) { this.start = start; this.period = new Period(start, stop);}

  public DateTime getStop() { return stop; }
  public void setStop(DateTime stop) { this.stop = stop; this.period = new Period(start, stop); }
  
  public Period getPeriod() { return this.period; }
  
  public Element toXML() {
    Element result = new Element("timespan");
    XMLUtil.createElement("start", start.toString(), result);
    XMLUtil.createElement("stop", stop.toString(), result);
    XMLUtil.createElement("period", period.toString(), result);
    return result;
  }
}