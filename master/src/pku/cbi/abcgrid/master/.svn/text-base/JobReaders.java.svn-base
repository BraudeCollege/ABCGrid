package pku.cbi.abcgrid.master;

import pku.cbi.abcgrid.master.service.App;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class that find and create instance of all classes
 * which implementated jobreader.pku.cbi.abcgrid.master.JobReader.
 * Not used at now.
 * @deprecated
 * */
public class JobReaders
{
    /**
     * find all classes(and create object if found) implementated JobReader.
     * @deprecated
     * */
    public static List<App> find(String pckgname)
    {
        List<App> jrs = new ArrayList<App>();
        //change package name to absolute path.
        String name = new String(pckgname);
        if (!name.startsWith("/")) {
            name = "/" + name;
        }
        name = name.replace('.', '/');
        // get a file object
        URL url = JobReaders.class.getResource(name);
        //System.out.println(url);
        File directory = new File(url.getFile());
        if (directory.exists())
        {
            //get the list in this directory
            String [] files = directory.list();
            for (int i = 0; i<files.length;i++)
            {
                if (files[i].endsWith(".class"))
                {
                    // tail ".class"
                    String classname = files[i].substring(0, files[i].length() - 6);
                    try
                    {
                        //try to create a instance
                        Object o = Class.forName(pckgname + "." + classname).newInstance();
                        if (o instanceof App)
                        {
                            jrs.add((App)o);
                        }
                    }
                    catch (ClassNotFoundException cnfex)
                    {
                        System.err.println(cnfex);
                    }
                    catch (InstantiationException iex)
                    {
                        // try to instanciate a interface or a class without default constructor.
                    }
                    catch (IllegalAccessException iaex)
                    {
                        //Not a public class
                    }
                }
            }
        }
        return jrs;
    }
    /**
     * for test
     * */
    public static void main(String[] args) {
        try
        {
            List<App> jrs  =find("service.pku.cbi.abcgrid.master");
            for(App j:jrs)
            {
                //System.out.println(j.name());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
}
