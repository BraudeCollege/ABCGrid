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

public class Component
{
    private String type; //executable,data, etc.
    private String name;//blast-2.2.14-ia32-linux.tar.gz
    private String path; //$ABCGRID_WORKER_HOME/service/$service_name/$path
    //only used in master side configuration to indicate the
    //ftp address which contain the component's data.
    //private String from_path;
    //private String to_path;
    private String ftp;
    public void setType(String val)
    {
        type = val;
    }
    public String getType()
    {
        return type;
    }
    public void setName(String val)
    {
        name = val;
    }
    public String getName()
    {
        return name;
    }
    public void setPath(String val)
    {
        path = val;
    }
    public String getPath()
    {
        int prefix = FilenameUtils.getPrefixLength(path);
        if(prefix>0)
        {
            path = path.substring(prefix);
        }
        return path;
        //path = FilenameUtils.getFullPathNoEndSeparator(path)+"/";
        //return FilenameUtils.normalize(path);
    }
    public void setFtp(String val)
    {
        ftp = val;
    }
    public String getFtp()
    {
        return ftp;
    }

    public boolean equals(Component comp)
    {
        return comp.getType().equals(this.getType())&& comp.getName().equals(this.getName());
    }
    public boolean isSameType(Component other)
    {
        String an = this.getName();
        String bn = other.getName();
        String aa = this.getType();
        String ba = other.getType();
        return an.equalsIgnoreCase(bn) && aa.equalsIgnoreCase(ba);
    }
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Type:");
        buffer.append(type);
        buffer.append("\n");

        buffer.append("Name:");
        buffer.append(name);
        buffer.append("\n");

        buffer.append("Path:");
        buffer.append(path);
        buffer.append("\n");

        buffer.append("From:");
        buffer.append(ftp);
        buffer.append("\n");
        return buffer.toString();
    }

}
