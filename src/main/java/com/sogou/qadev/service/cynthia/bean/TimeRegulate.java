/**
 * 
 */
package com.sogou.qadev.service.cynthia.bean;

import java.io.PrintStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;

public class TimeRegulate
{
  public static final int[] monthDays = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

  public static final int[] leepMonthDays = { 0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

  private Timestamp startTime = null;

  private Timestamp currentScheduleTime = null;

  private Timestamp nextScheduleTime = null;

  private TreeSet<Integer> monthSet = null;

  private TreeSet<Integer> dateSet = null;

  private TreeSet<Integer> daySet = null;

  private TreeSet<Integer> hourSet = null;

  private TreeSet<Integer> minuteSet = null;

  private TreeSet<Integer> secondSet = null;

  public TimeRegulate()
  {
    this.monthSet = new TreeSet();
    this.monthSet.add(Integer.valueOf(1));

    this.dateSet = new TreeSet();
    this.dateSet.add(Integer.valueOf(1));

    this.daySet = new TreeSet();
    this.daySet.add(Integer.valueOf(1));

    this.hourSet = new TreeSet();
    this.hourSet.add(Integer.valueOf(0));

    this.minuteSet = new TreeSet();
    this.minuteSet.add(Integer.valueOf(0));

    this.secondSet = new TreeSet();
    this.secondSet.add(Integer.valueOf(0));
  }

  public TimeRegulate copyInstance()
  {
    TimeRegulate temp = new TimeRegulate();
    temp.startTime = this.startTime;
    temp.currentScheduleTime = this.currentScheduleTime;
    temp.nextScheduleTime = this.nextScheduleTime;
    temp.monthSet.addAll(this.monthSet);
    temp.dateSet.addAll(this.dateSet);
    temp.daySet.addAll(this.daySet);
    temp.hourSet.addAll(this.hourSet);
    temp.minuteSet.addAll(this.minuteSet);
    temp.secondSet.addAll(this.secondSet);
    return temp;
  }

  public Timestamp accountNextScheduleTime()
  {
    return accountNextScheduleTime(new Date());
  }

  public Timestamp accountNextScheduleTime(Date dateUse)
  {
    Calendar current = Calendar.getInstance();
    current.setTime(dateUse);
    TimeData timedata = new TimeData(current);

    timedata.minute = Integer.valueOf(timedata.minute.intValue() + 1);

    if (!this.minuteSet.contains(timedata.minute))
    {
      timedata.second = ((Integer)this.secondSet.first());

      Object[] obj = this.minuteSet.toArray();
      for (int ih = 0; ih < obj.length; ih++)
      {
        if (timedata.minute.intValue() < ((Integer)obj[ih]).intValue())
        {
          timedata.minute = ((Integer)obj[ih]);
          break;
        }
        if (ih != obj.length - 1)
          continue;
        timedata.minute = ((Integer)obj[0]);
        timedata.hour = new Integer(timedata.hour.intValue() + 1);
        break;
      }

    }

    if (!this.hourSet.contains(timedata.hour))
    {
      timedata.second = ((Integer)this.secondSet.first());
      timedata.minute = ((Integer)this.minuteSet.first());

      Object[] obj = this.hourSet.toArray();
      for (int ih = 0; ih < obj.length; ih++)
      {
        if (timedata.hour.intValue() < ((Integer)obj[ih]).intValue())
        {
          timedata.hour = ((Integer)obj[ih]);
          break;
        }
        if (ih != obj.length - 1)
          continue;
        timedata.hour = ((Integer)obj[0]);
        timedata.date = new Integer(timedata.date.intValue() + 1);
        timedata.day = new Integer(timedata.day.intValue() + 1);
        break;
      }

    }

    timedata.isCorrect = false;

    while (!timedata.isCorrect)
    {
      if ((this.monthSet.contains(timedata.month)) && (this.dateSet.contains(timedata.date)) && (this.daySet.contains(timedata.day)))
      {
        timedata.isCorrect = true;
        break;
      }

      timedata.second = ((Integer)this.secondSet.first());
      timedata.minute = ((Integer)this.minuteSet.toArray()[0]);
      timedata.hour = ((Integer)this.hourSet.toArray()[0]);

      timedata = setMonthAndDay(timedata);
    }

    DecimalFormat format = new DecimalFormat();
    format.setMinimumIntegerDigits(2);

    String tempString = timedata.year + "-" + format.format(timedata.month) + "-" + format.format(timedata.date) + " " + format.format(timedata.hour) + ":" + 
      format.format(timedata.minute) + ":" + format.format(timedata.second);
    try
    {
      this.nextScheduleTime = Timestamp.valueOf(tempString);
    }
    catch (Exception e)
    {
      System.err.println("next time: " + tempString);
      e.printStackTrace();
      return null;
    }
    return this.nextScheduleTime;
  }

  public TimeData setMonthAndDay(TimeData timedata)
  {
    while (true)
    {
      if ((this.dateSet.contains(timedata.date)) && (this.daySet.contains(timedata.day)) && (this.monthSet.contains(timedata.month))) {
        return timedata;
      }

      int tempdate = timedata.date.intValue() + 1;
      int day = timedata.day.intValue() + 1;
      int tempmonth = timedata.month.intValue();

      if (((timedata.year.intValue() % 4 == 0) && (timedata.year.intValue() % 100 != 0)) || 
        (timedata.year.intValue() % 400 == 0))
      {
        if (tempdate > leepMonthDays[tempmonth])
        {
          tempdate = 1;
          tempmonth++;
        }

      }
      else if (tempdate > monthDays[tempmonth])
      {
        tempdate = 1;
        tempmonth++;
      }

      if (tempmonth > 12)
      {
        timedata.year = new Integer(timedata.year.intValue() + 1);
        tempdate = 1;
        tempmonth = 1;
      }
      timedata.month = new Integer(tempmonth);
      timedata.date = new Integer(tempdate);
      if (day <= 7) {
        timedata.day = new Integer(day); continue;
      }
      timedata.day = new Integer(day % 7);
    }
  }

  public Timestamp getNextScheduleTime()
  {
    return accountNextScheduleTime();
  }

  public static void main(String[] args)
  {
    TimeRegulate time = new TimeRegulate();
    time.accountNextScheduleTime();
  }

  public void setDateSet(TreeSet<Integer> dateSet)
  {
    this.dateSet = dateSet;
  }

  public void setDaySet(TreeSet<Integer> daySet)
  {
    this.daySet = daySet;
  }

  public void setHourSet(TreeSet<Integer> hourSet)
  {
    this.hourSet = hourSet;
  }

  public void setMinuteSet(TreeSet<Integer> minuteSet)
  {
    this.minuteSet = minuteSet;
  }

  public void setMonthSet(TreeSet<Integer> monthSet)
  {
    this.monthSet = monthSet;
  }

  public void setSecondSet(TreeSet<Integer> secondSet)
  {
    this.secondSet = secondSet;
  }

  public TreeSet<Integer> getDateSet()
  {
    return this.dateSet;
  }

  public TreeSet<Integer> getDaySet()
  {
    return this.daySet;
  }

  public TreeSet<Integer> getHourSet()
  {
    return this.hourSet;
  }

  public TreeSet<Integer> getMinuteSet()
  {
    return this.minuteSet;
  }

  public TreeSet<Integer> getMonthSet()
  {
    return this.monthSet;
  }

  public Timestamp getCurrentScheduleTime() {
    return this.currentScheduleTime;
  }

  public TreeSet<Integer> getSecondSet() {
    return this.secondSet;
  }

  public class TimeData
  {
    Integer year = null;

    Integer month = null;

    Integer date = null;

    Integer hour = null;

    Integer minute = null;

    Integer second = null;

    Integer week = null;

    Integer day = null;

    boolean isCorrect = true;

    public TimeData(Calendar current)
    {
      this.year = new Integer(current.get(1));
      this.month = new Integer(current.get(2) + 1);
      this.date = new Integer(current.get(5));
      this.hour = new Integer(current.get(11));
      this.minute = new Integer(current.get(12));
      this.second = new Integer(current.get(13));
      this.week = new Integer(current.get(4));
      this.day = new Integer(current.get(7) - 1);
      if (this.day.intValue() == 0)
        this.day = new Integer(7);
    }
  }
}