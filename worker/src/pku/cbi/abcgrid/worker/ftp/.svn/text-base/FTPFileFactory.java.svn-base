/**
 *
 *  edtFTPj
 * 
 *  Copyright (C) 2000-2004 Enterprise Distributed Technologies Ltd
 *
 *  www.enterprisedt.com
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Bug fixes, suggestions and comments should be sent to bruce@enterprisedt.com
 *
 *  Change Log:
 *
 *    $Log: FTPFileFactory.java,v $
 *    Revision 1.1.1.1  2004/11/21 01:54:37  suny
 *     gridc alpha version
 *
 *    Revision 1.3  2004/05/01 11:44:21  bruceb
 *    modified for server returning "total 3943" as first line
 *
 *    Revision 1.2  2004/04/17 23:42:07  bruceb
 *    file parsing part II
 *
 *    Revision 1.1  2004/04/17 18:37:23  bruceb
 *    new parse functionality
 *
 */

package pku.cbi.abcgrid.worker.ftp;

import pku.cbi.abcgrid.worker.ftp.FTPException;
import pku.cbi.abcgrid.worker.ftp.FTPFile;

import java.text.ParseException;
import java.util.Calendar;

/**
 *  Factory for creating FTPFile objects
 *
 *  @author      Bruce Blackshaw
 *  @version     $Revision: 1.1.1.1 $
 */
public class FTPFileFactory {
    
    /**
     *  Revision control id
     */
    private static String cvsId = "@(#)$Id: FTPFileFactory.java,v 1.1.1.1 2004/11/21 01:54:37 suny Exp $";

    /**
     * Windows server comparison string
     */
    private final static String WINDOWS_STR = "WINDOWS";
                  
    /**
     * UNIX server comparison string
     */
    private final static String UNIX_STR = "UNIX";
    
    /**
     * Calendar
     */
    private Calendar cal = Calendar.getInstance();
    
    /**
     * SYST string
     */
    private String system;
    
    /**
     * Does the parsing work
     */
    private FTPFileParser parser = null;
    
    /**
     * Constructor
     * 
     * @param system    SYST string
     */
    FTPFileFactory(String system) throws FTPException {
        setParser(system);
    }
    
    /**
     * Set the remote server type
     * 
     * @param system    SYST string
     */
    private void setParser(String system) throws FTPException {
        this.system = system;
        if (system.toUpperCase().startsWith(WINDOWS_STR))
            parser = new WindowsFileParser();
        else if (system.toUpperCase().startsWith(UNIX_STR))
            parser = new UnixFileParser();
        else
            throw new FTPException("Unknown SYST: " + system);
    }
    
    
    /**
     * Parse an array of raw file information returned from the
     * FTP server
     * 
     * @param files     array of strings
     * @return array of FTPFile objects
     */
    FTPFile[] parse(String[] files) throws ParseException {
        FTPFile[] temp = new FTPFile[files.length];
        int count = 0;
        for (int i = 0; i < files.length; i++) {
            FTPFile file = parser.parse(files[i]);
            // we skip null returns - these are duff lines we know about and don't
            // really want to throw an exception
            if (file != null) {
                temp[count++] = file;
	    }
        }
        FTPFile[] result = new FTPFile[count];
        System.arraycopy(temp, 0, result, 0, count);
        return result;
    }
}
