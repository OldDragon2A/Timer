package me.olddragon2a;

import java.io.Serializable;

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

  public DateTime getStart() {
    return start;
  }

  public void setStart(DateTime start) {
    this.start = start;
  }

  public DateTime getStop() {
    return stop;
  }

  public void setStop(DateTime stop) {
    this.stop = stop;
  }
  
  public Period getPeriod() {
    return this.period;
  }
}