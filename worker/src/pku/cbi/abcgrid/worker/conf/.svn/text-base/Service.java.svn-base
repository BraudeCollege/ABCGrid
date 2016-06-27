package pku.cbi.abcgrid.worker.conf;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import org.apache.commons.io.FilenameUtils;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import pku.cbi.abcgrid.worker.Util;

/**
 *
 * a "<service></service>" element defined in worker.conf
 *
 * A Service consists of some "components" which is defned by class Component.
 *
 */
public class Service
{
    private String name="";
    private List<Component> components = new ArrayList<Component>();
    public void setName(String val)
    {
        name = val;
    }
    public String getName()
    {
        return name;
    }
    public void addComponent(Component c)
    {
        components.add(c);
    }
    public Component[] getComponent()
    {
        Component[] cps = new Component[components.size()];
        components.toArray(cps);
        return cps;
    }
    public String getComponentPath(String type,String name)
    {

        for(Component c:components)
        {
            if(type.equalsIgnoreCase("executable")) //executable
            {
                String parent = FilenameUtils.concat("executable",c.getPath());
                return FilenameUtils.concat(parent,name);
            }
            else if(type.equalsIgnoreCase("data")) //data
            {
                if(c.getName().equalsIgnoreCase(name))
                {
                    String parent = FilenameUtils.concat("data",c.getPath());
                    return FilenameUtils.concat(parent,name);
                }
            }
            else// what is else? reserved for future.
            {

            }
        }
        return null;
    }
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        for(Component c:components)
        {
            sb.append("####################################\n");
            sb.append(String.format("%s",c.toString()));
        }
        return sb.toString();
    }
}
