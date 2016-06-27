package pku.cbi.abcgrid.master;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import java.util.Date;


public class TaskInfo
{
    private long worker;
    private Date time_start;
    private Date time_stop;
    private TaskState state;
    private Task task;
    public TaskInfo(Task t)
    {
        task = t;
        worker = 0L;
        state = TaskState.idle;
    }
    public Task getTask()
    {
        return task;
    }
    public long getTaskId()
    {
        return task.getTaskId();
    }
    public void setWorker(long wid)
    {
        worker = wid;
    }
    public long getWorker()
    {
        return worker;
    }
    public TaskState getState()
    {
        return state;
    }
    public void setState(TaskState s)
    {
        state = s;
    }
    public Date getStartTime()
    {
        return time_start;
    }
    public void startTiming()
    {
        time_start = new Date();
    }
    public void stopTiming()
    {
        if(time_stop==null)
            time_stop = new Date();
    }
    public int getElapsedSeconds()
    {
        int t = 0;
        if(state!=TaskState.complete)
        {
            Date now = new Date();
            t =  (int)(now.getTime()-time_start.getTime())/1000;
        }
        else
        {
            t = (int)(time_stop.getTime()-time_start.getTime())/1000;
        }
        return t;
    }
    private String formatTime(int seconds)
    {
        int hour = seconds/3600;
        int minute = (seconds-hour*3600)/60;
        int second = (seconds-hour*3600-minute*60);
        if(hour>0)
            return String.format("%dh%02dm%02ds",hour,minute,second);
        else if(minute>0)
            return String.format("%02dm%02ds",minute,second);
        else
            return String.format("%02ds",second);
    }
    public String toString()
    {
        return String.format("%d,%d %s %s",task.getJobId(),task.getTaskId(),
                state,formatTime(getElapsedSeconds()));
    }
}
