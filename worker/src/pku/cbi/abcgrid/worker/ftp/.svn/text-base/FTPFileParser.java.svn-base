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
 *    $Log: FTPFileParser.java,v $
 *    Revision 1.1.1.1  2004/11/21 01:54:37  suny
 *     gridc alpha version
 *
 *    Revision 1.2  2004/06/25 11:48:30  bruceb
 *    changed MAX_FIELDS to 20
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

import java.text.ParseException;

/**
 *  Root class of all file parsers
 *
 *  @author      Bruce Blackshaw
 *  @version     $Revision: 1.1.1.1 $
 */
abstract public class FTPFileParser {
    
    /**
     * Maximum number of fields in raw string
     */
    private final static int MAX_FIELDS = 20;
    
    /**
     * Parse server supplied string. Should be in
     * form 
     * 
     * 05-17-03  02:47PM                70776 ftp.jar
     * 08-28-03  10:08PM       <DIR>          EDTSSLTest
     * 
     * @param raw   raw string to parse
     */
    abstract public FTPFile parse(String raw) throws ParseException;
    
    /**
     * Splits string consisting of fields separated by
     * whitespace into an array of strings. Yes, we could
     * use String.split() but this would restrict us to 1.4+
     * 
     * @param str   string to split
     * @return array of fields
     */
    protected String[] split(String str) {
        String[] fields = new String[MAX_FIELDS];
        int pos = 0;
        StringBuffer field = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (!Character.isWhitespace(ch))
                field.append(ch);
            else {
                if (field.length()> 0) {
                    fields[pos++] = field.toString();
                    field.setLength(0);
                }
            }
        }
        // pick up last field
        if (field.length() > 0) {
            fields[pos++] = field.toString();
        }
        String[] result = new String[pos];
        System.arraycopy(fields, 0, result, 0, pos);
        return result;
    }
}
