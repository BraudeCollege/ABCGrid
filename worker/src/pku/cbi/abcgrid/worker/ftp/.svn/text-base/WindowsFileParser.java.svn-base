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
 *    $Log: WindowsFileParser.java,v $
 *    Revision 1.1.1.1  2004/11/21 01:54:37  suny
 *     gridc alpha version
 *
 *    Revision 1.3  2004/05/05 20:27:55  bruceb
 *    US locale for date formats
 *
 *    Revision 1.2  2004/05/01 11:44:21  bruceb
 *    modified for server returning "total 3943" as first line
 *
 *    Revision 1.1  2004/04/17 23:42:07  bruceb
 *    file parsing part II
 *
 *    Revision 1.1  2004/04/17 18:37:23  bruceb
 *    new parse functionality
 *
 */
package pku.cbi.abcgrid.worker.ftp;

import pku.cbi.abcgrid.worker.ftp.FTPFile;
import pku.cbi.abcgrid.worker.ftp.FTPFileParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *  Represents a remote Windows file parser
 *
 *  @author      Bruce Blackshaw
 *  @version     $Revision: 1.1.1.1 $
 */
public class WindowsFileParser extends FTPFileParser {

    /**
     *  Revision control id
     */
    private static String cvsId = "@(#)$Id: WindowsFileParser.java,v 1.1.1.1 2004/11/21 01:54:37 suny Exp $";
    
    /**
     * Date formatter
     */
    private final static SimpleDateFormat formatter =
        new SimpleDateFormat("MM-dd-yy hh:mma", Locale.US);
    
    /**
     * Directory field
     */
    private final static String DIR = "<DIR>";
    
    /**
     * Number of expected fields
     */
    private final static int MIN_EXPECTED_FIELD_COUNT = 4;

    /**
     * Default constructor
     */
    public WindowsFileParser() {}

    /**
     * Parse server supplied string. Should be in
     * form 
     * 
     * 05-17-03  02:47PM                70776 ftp.jar
     * 08-28-03  10:08PM       <DIR>          EDT SSLTest
     * 
     * @param raw   raw string to parse
     */
    public FTPFile parse(String raw) throws ParseException {
        String[] fields = split(raw);
        
        if (fields.length < MIN_EXPECTED_FIELD_COUNT)
            throw new ParseException("Unexpected number of fields: " + fields.length, 0);
        
        // first two fields are date time
        Date lastModified = formatter.parse(fields[0] + " " + fields[1]);
        
        // dir flag
        boolean isDir = false;
        long size = 0L;
        if (fields[2].equalsIgnoreCase(DIR))
            isDir = true;
        else {
            try {
                size = Long.parseLong(fields[2]);
            }
            catch (NumberFormatException ex) {
                throw new ParseException("Failed to parse size: " + fields[2], 0);
            }
        }
        
        // name of file or dir - use the raw string to get
        // spaces etc
        int pos = raw.indexOf(fields[3]);
        if (pos > 0) {
            String name = raw.substring(pos);
            return new FTPFile(FTPFile.WINDOWS, raw, name.toString(), size, isDir, lastModified);
        }
        throw new ParseException("Failed to retrieve name: " + raw, 0);        
    }
  
}
