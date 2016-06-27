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
 *    $Log: UnixFileParser.java,v $
 *    Revision 1.1.1.1  2004/11/21 01:54:37  suny
 *     gridc alpha version
 *
 *    Revision 1.5  2004/06/11 10:19:59  bruceb
 *    fixed bug re filename same as user
 *
 *    Revision 1.4  2004/05/20 19:47:00  bruceb
 *    blanks in names fix
 *
 *    Revision 1.3  2004/05/05 20:27:41  bruceb
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *  Represents a remote Unix file parser
 *
 *  @author      Bruce Blackshaw
 *  @version     $Revision: 1.1.1.1 $
 */
public class UnixFileParser extends FTPFileParser {

    /**
     *  Revision control id
     */
    private static String cvsId = "@(#)$Id: UnixFileParser.java,v 1.1.1.1 2004/11/21 01:54:37 suny Exp $";

    /**
     * Duff line start word
     */
    private final static String TOTAL = "total";
    
    /**
     * Symbolic link symbol
     */
    private final static String SYMLINK_ARROW = "->";
    
    /**
     * Date formatter type 1
     */
    private final static SimpleDateFormat formatter1 =
        new SimpleDateFormat("MMM-dd-yyyy", Locale.US);
    
    /**
     * Date formatter type 2
     */
    private final static SimpleDateFormat formatter2 =
        new SimpleDateFormat("MMM-dd-yyyy-HH:mm", Locale.US);
    
    /**
     * Minimum number of expected fields
     */
    private final static int MIN_FIELD_COUNT = 9;

    /**
     * Current year
     */
    private int year;

    /**
     * Constructor
     */
    public UnixFileParser() {
        this.year = Calendar.getInstance().get(Calendar.YEAR);
    }
    
    /**
     * Parse server supplied string, e.g.:
     * 
     * lrwxrwxrwx   1 wuftpd   wuftpd         14 Jul 22  2002 MIRRORS -> README-MIRRORS
     * -rw-r--r--   1 b173771  users         431 Mar 31 20:04 .htaccess
     * 
     * @param raw   raw string to parse
     */
    public FTPFile parse(String raw) throws ParseException {
        String[] fields = split(raw);
 
        // duff line starting with "total 342522"
        if (fields.length == 2 && TOTAL.equalsIgnoreCase(fields[0])) 
            return null;
        
        if (fields.length < MIN_FIELD_COUNT)
            throw new ParseException("Unexpected number of fields: " + fields.length, 0);
        
        // first field is perms
        String permissions = fields[0];
        char ch = permissions.charAt(0);
        boolean isDir = false;
        boolean isLink = false;
        if (ch == 'd')
            isDir = true;
        else if (ch == 'l')
            isLink = true;
        
        // owner and group
        String owner = fields[2];
        String group = fields[3];
        
        // size
        long size = 0L;
        String sizeStr = fields[4];
        try {
            size = Long.parseLong(sizeStr);
        }
        catch (NumberFormatException ex) {
            throw new ParseException("Failed to parse size: " + sizeStr, 0);
        }
        
        // 5,6,7 are the date time
        Date lastModified = null;
        StringBuffer stamp = new StringBuffer(fields[5]);
        stamp.append('-').append(fields[6]).append('-');
        if (fields[7].indexOf(':') < 0) {
            stamp.append(fields[7]); // year
            lastModified = formatter1.parse(stamp.toString());
        }
        else { // add the year ourselves as not present
            stamp.append(year).append('-').append(fields[7]);
            lastModified = formatter2.parse(stamp.toString());
        }
            
        // name of file or dir. Extract symlink if possible
        String name = null;
        String linkedname = null;
        
        // we've got to find the starting point of the name. We
        // do this by finding the pos of all the date/time fields, then
        // the name - to ensure we don't get tricked up by a userid the
        // same as the filename,for example
        int pos = 0;
        boolean ok = true;
        for (int i = 5; i < 9; i++) {
            pos = raw.indexOf(fields[i], pos);
            if (pos < 0) {
                ok = false;
                break;
            }
        }
        if (ok) {
            String remainder = raw.substring(pos);
            if (!isLink) 
                name = remainder;
            else { // symlink, try to extract it
                pos = remainder.indexOf(SYMLINK_ARROW);
                if (pos <= 0) { // couldn't find symlink, give up & just assign as name
                    name = remainder;
                }
                else { 
                    int len = SYMLINK_ARROW.length();
                    name = remainder.substring(0, pos).trim();
                    if (pos+len < remainder.length())
                        linkedname = remainder.substring(pos+len);
                }
            }
        }
        else {
            throw new ParseException("Failed to retrieve name: " + raw, 0);  
        }
        
        FTPFile file = new FTPFile(FTPFile.UNIX, raw, name, size, isDir, lastModified);
        file.setGroup(group);
        file.setOwner(owner);
        file.setLink(isLink);
        file.setLinkedName(linkedname);
        return file;
    }
}
