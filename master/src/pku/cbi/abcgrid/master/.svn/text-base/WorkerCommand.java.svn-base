package pku.cbi.abcgrid.master;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import java.util.List;

/**
 *
 * show connected worker nodes. Trigered by command "w"
 * >>>w
 */
public class WorkerCommand implements Command
{
    public static final String NAME = "w";
    private WorkerManager wm = WorkerManager.getInstance();
    public Object execute(String user,String[] cmd)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Connected worker nodes:\n");
        sb.append("**********************************\n");
        if(cmd.length>1)
        {
            for(int i=1;i<cmd.length;i++)
            {
                try
                {
                    long workerid = Long.valueOf(cmd[i]);
                    WorkerInfo wi = wm.getWorkerInfo(workerid);
                    if(wi!=null)
                    {
                        sb.append(wi);
                        sb.append("\n");
                    }

                }
                catch(Exception e)
                {
                    //sb.append(e);
                }
            }
        }
        else
        {
            List<WorkerInfo> info = wm.getWorkerInfo();
            int index = 0;
            for(WorkerInfo i:info)
            {
                index += 1;
                sb.append(String.format("[%d]  %s\n",index,i));
            }
        }
        return sb.toString();
    }
    public String usage()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("show connected worker nodes:\n");
        sb.append("COMMAND: w\n");
        sb.append("EXAMPLE: \n\n");
        sb.append(">>>w\n");
        sb.append("show all connected worker nodes\n");
        return sb.toString();
    }
    public String toString()
    {
        return usage(); 
    }

}
