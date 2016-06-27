package pku.cbi.abcgrid.worker.update;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;

import java.io.StringReader;

import pku.cbi.abcgrid.worker.conf.*;
import pku.cbi.abcgrid.worker.MasterProxy;

/**
 * Created by IntelliJ IDEA.
 * User: suny
 * Date: 2006-6-12
 * Time: 22:42:19
 * To change this template use File | Settings | File Templates.
 */
public class UpdateConfParser
{
    private Digester makeDigester()
    {
        Digester dg = new Digester();
        dg.setValidating(true);
        dg.addObjectCreate("grid", Entry.class);
        dg.addObjectCreate("grid/ftplist/ftp", FTP.class);
        dg.addSetProperties("grid/ftplist/ftp", "name", "name");
        dg.addBeanPropertySetter("grid/ftplist/ftp/ip", "ip");
        dg.addBeanPropertySetter("grid/ftplist/ftp/port", "port");
        dg.addBeanPropertySetter("grid/ftplist/ftp/user", "user");
        dg.addBeanPropertySetter("grid/ftplist/ftp/password", "password");
        dg.addBeanPropertySetter("grid/ftplist/ftp/basepath", "basepath");
        //dg.addBeanPropertySetter("grid/ftplist/ftp/timeout", "timeout");
        dg.addBeanPropertySetter("grid/ftplist/ftp/pasv", "pasv");
        dg.addSetNext("grid/ftplist/ftp", "addFtp"); //define in Entry

        dg.addObjectCreate("grid/services", Services.class);
        dg.addSetProperties("grid/services", "os", "os");
        dg.addSetProperties("grid/services", "arch", "type");

        dg.addObjectCreate("grid/services/service", Service.class);
        dg.addSetProperties("grid/services/service", "name", "name");

        dg.addObjectCreate("grid/services/service/component", Component.class);
        dg.addSetProperties("grid/services/service/component", "type", "type");
        dg.addBeanPropertySetter("grid/services/service/component/name", "name");
        dg.addBeanPropertySetter("grid/services/service/component/path", "path");

        dg.addSetNext("grid/services/service/component","addComponent");
        dg.addSetNext("grid/services/service","addService"); //define in Services
        dg.addSetNext("grid/services", "addServices"); //define in Entry
        return dg;
    }

    public Entry parse(String update_info)
    {
        Digester digester = makeDigester();
        digester.setValidating(false);
        try
        {
            return (Entry) digester.parse(new StringReader(update_info));
        }
        catch (SAXParseException e)
        {
            String errmsg = "Failed to parse update information, "+e.toString();
            MasterProxy.putStderr(0,0,null,errmsg.getBytes());
            System.err.println(e);
            //logger.error(e);
        }
        catch (SAXException e)
        {
            String errmsg = "Failed to parse update information, error:"+e.toString();
            MasterProxy.putStderr(0,0,null,errmsg.getBytes());
            System.err.println(e);
            //logger.error(e);
        }
        catch (Exception e)
        {
            String errmsg = "Failed to parse update information, error:"+e.toString();
            MasterProxy.putStderr(0,0,null,errmsg.getBytes());
            System.err.println("Failed to parse update information: " + e);
        }
        return null;
    }
}
