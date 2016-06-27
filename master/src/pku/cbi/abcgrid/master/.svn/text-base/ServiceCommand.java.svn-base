package pku.cbi.abcgrid.master;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * print supported services.
 *
  */
public class ServiceCommand implements Command
{
    //the short command
    public static final String NAME = "s";
    JobScheduler js = JobScheduler.getInstance();
    public Object execute(String user,String[] cmd)
            throws Exception
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Supported bioinformatics services:\n");
        sb.append("**********************************\n");
        if(cmd.length==1)//list all services
        {
            String[] ss = js.getSupportedApps();
            List<String> ls = Arrays.asList(ss);
            Collections.sort(ls);
            for(String s:ls)
            {
                sb.append(s);
            }
        }
        else //list one service and its details
        {
        }
        return sb.toString();
    }
    public String usage()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("COMMAND: s\n");
        sb.append("DESCRIP: show supported bioinformatics services\n");
        sb.append("EXAMPLE: \n\n");
        sb.append(">>>s\n");
        return sb.toString();
    }
    public String toString()
    {
        return usage();
    }

}
