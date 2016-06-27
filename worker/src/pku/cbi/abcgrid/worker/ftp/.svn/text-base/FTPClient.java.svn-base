/**
 *
 *  edtFTPj
 *
 *  Copyright (C) 2000-2003  Enterprise Distributed Technologies Ltd
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
 *        $Log: FTPClient.java,v $
 *        Revision 1.1.1.1  2004/11/21 01:54:37  suny
 *         gridc alpha version
 *
 *        Revision 1.22  2004/06/25 11:47:46  bruceb
 *        made 1.1.x compatible
 *
 *        Revision 1.21  2004/06/11 10:20:35  bruceb
 *        permit 200 to be returned from various cmds
 *
 *        Revision 1.20  2004/05/22 16:52:57  bruceb
 *        message listener
 *
 *        Revision 1.19  2004/05/15 22:37:22  bruceb
 *        put debugResponses back in
 *
 *        Revision 1.18  2004/05/13 23:00:34  hans
 *        changed comment
 *
 *        Revision 1.17  2004/05/08 21:14:41  bruceb
 *        checkConnection stuff
 *
 *        Revision 1.14  2004/04/19 21:54:06  bruceb
 *        final tweaks to dirDetails() re caching
 *
 *        Revision 1.13  2004/04/18 11:16:44  bruceb
 *        made validateTransfer() public
 *
 *        Revision 1.12  2004/04/17 18:37:38  bruceb
 *        new parse functionality
 *
 *        Revision 1.11  2004/03/23 20:26:49  bruceb
 *        tweak to size(), catch exceptions on puts()
 *
 *        Revision 1.10  2003/11/15 11:23:55  bruceb
 *        changes required for ssl subclasses
 *
 *        Revision 1.6  2003/05/31 14:53:44  bruceb
 *        1.2.2 changes
 *
 *        Revision 1.5  2003/01/29 22:46:08  bruceb
 *        minor changes
 *
 *        Revision 1.4  2002/11/19 22:01:25  bruceb
 *        changes for 1.2
 *
 *        Revision 1.3  2001/10/09 20:53:46  bruceb
 *        Active mode changes
 *
 *        Revision 1.1  2001/10/05 14:42:03  bruceb
 *        moved from old project
 *
 */

package pku.cbi.abcgrid.worker.ftp;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;

import java.net.InetAddress;
import java.util.*;

/**
 *  Supports client-side FTP. Most common
 *  FTP operations are present in this class.
 *
 *  @author      Bruce Blackshaw
 *  @version     $Revision: 1.1.1.1 $
 *
 */
public class FTPClient
{
    /**
     *  A commons-logging logger
     */
    private static Log logger = LogFactory.getLog(FTPClient.class);
    /**
     *  Revision control id
     */
    private static String cvsId = "@(#)$Id: FTPClient.java,v 1.1.1.1 2004/11/21 01:54:37 suny Exp $";

    /**
     * Major version (substituted by ant)
     */
    private static String majorVersion = "1";
    /**
     * Middle version (substituted by ant)
     */
    private static String middleVersion = "0";
    /**
     * Middle version (substituted by ant)
     */
    private static String minorVersion = "0";
    /**
     * Full version
     */
    private static int[] version;
    /**
     * Timestamp of build
     */
    private static String buildTimestamp = "@date_time@";
    /**
     * Work out the version array
     */
    static {
        try {
            version = new int[3];
            version[0] = Integer.parseInt(majorVersion);
            version[1] = Integer.parseInt(middleVersion);
            version[2] = Integer.parseInt(minorVersion);
        }
        catch (NumberFormatException ex)
        {
            logger.error("Failed to calculate version: " + ex.getMessage());
        }
    }

    /**
     *  Format to interpret MTDM timestamp
     */
    private SimpleDateFormat tsFormat =
            new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * Logging object
     */
    /**
     *  Socket responsible for controlling
     *  the connection
     */
    protected FTPControlSocket control = null;

    /**
     *  Socket responsible for transferring the data
     */
    protected FTPDataSocket data = null;

    /**
     *  Socket timeout for both data and control. In milliseconds
     */
    private int timeout = 0;

    /**
     *  Can be used to cancel a transfer
     */
    private boolean cancelTransfer = false;

    /**
     * Bytes transferred in between monitor callbacks
     */
    private long monitorInterval = 4096;

    /**
     * Parses LIST output
     */
    private FTPFileFactory fileFactory = null;

    /**
     *  Progress monitor
     */
    private FTPProgressMonitor monitor = null;

    /**
     * Message listener
     */
    protected FTPMessageListener messageListener = null;

    /**
     *  Record of the transfer type - make the default ASCII
     */
    private FTPTransferType transferType = FTPTransferType.ASCII;

    /**
     *  Record of the connect mode - make the default PASV (as this was
     *  the original mode supported)
     */
    private FTPConnectMode connectMode = FTPConnectMode.PASV;

    /**
     *  Holds the last valid reply from the server on the control socket
     */
    protected FTPReply lastValidReply;

    /**
     *  Instance initializer. Sets formatter to GMT.
     */
    {
        tsFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }


    /**
     * Get the version of edtFTPj
     *
     * @return int array of {major,middle,minor} version numbers
     */
    public static int[] getVersion() {
        return version;
    }

    /**
     * Get the build timestamp
     *
     * @return d-MMM-yyyy HH:mm:ss z build timestamp
     */
    public static String getBuildTimestamp() {
        return buildTimestamp;
    }

    /**
     *  Constructor. Creates the control
     *  socket
     *
     *  @param   remoteHost  the remote hostname
     */
    public FTPClient(String remoteHost)
            throws IOException, FTPException {

        this(remoteHost, FTPControlSocket.CONTROL_PORT, 0);
    }

    /**
     *  Constructor. Creates the control
     *  socket
     *
     *  @param   remoteHost  the remote hostname
     *  @param   controlPort  port for control stream (-1 for default port)
     */
    public FTPClient(String remoteHost, int controlPort)
            throws IOException, FTPException {

        this(remoteHost, controlPort, 0);
    }


    /**
     *  Constructor. Creates the control
     *  socket
     *
     *  @param   remoteHost  the remote hostname
     *  @param   controlPort  port for control stream (use -1 for the default port)
     *  @param  timeout       the length of the timeout, in milliseconds
     *                        (pass in 0 for no timeout)
     */
    public FTPClient(String remoteHost, int controlPort, int timeout)
            throws IOException, FTPException {

        this(InetAddress.getByName(remoteHost), controlPort, timeout);
    }


    /**
     *  Constructor. Creates the control
     *  socket
     *
     *  @param   remoteAddr  the address of the
     *                       remote host
     */
    public FTPClient(InetAddress remoteAddr)
            throws IOException, FTPException {

        this(remoteAddr, FTPControlSocket.CONTROL_PORT, 0);
    }


    /**
     *  Constructor. Creates the control
     *  socket. Allows setting of control port (normally
     *  set by default to 21).
     *
     *  @param   remoteAddr  the address of the
     *                       remote host
     *  @param   controlPort  port for control stream
     */
    public FTPClient(InetAddress remoteAddr, int controlPort)
            throws IOException, FTPException {

        this(remoteAddr, controlPort, 0);
    }

    /**
     *  Constructor. Creates the control
     *  socket. Allows setting of control port (normally
     *  set by default to 21).
     *
     *  @param   remoteAddr    the address of the
     *                          remote host
     *  @param   controlPort   port for control stream (-1 for default port)
     *  @param  timeout        the length of the timeout, in milliseconds
     *                         (pass in 0 for no timeout)
     */
    public FTPClient(InetAddress remoteAddr, int controlPort, int timeout)
            throws IOException, FTPException {
        if (controlPort < 0)
            controlPort = FTPControlSocket.CONTROL_PORT;
        initialize(new FTPControlSocket(remoteAddr, controlPort, timeout, null));
    }

    /**
     *  Default constructor for use by subclasses
     */
    protected FTPClient() {
    }

    /**
     * Checks if the client has connected to the server and throws an exception if it hasn't.
     * This is only intended to be used by subclasses
     *
     * @throws FTPException Thrown if the client has not connected to the server.
     */
    protected void checkConnection(boolean shouldBeConnected) throws FTPException {
        if (shouldBeConnected && control==null)
            throw new FTPException("The FTP client has not yet connected to the server.  "
                    + "The requested action cannot be performed until after a connection has been established.");
        else if (!shouldBeConnected && control!=null)
            throw new FTPException("The FTP client has already been connected to the server.  "
                    +"The requested action must be performed before a connection is established.");
    }

    /**
     * Set the control socket explicitly
     *
     * @param control   control socket reference
     */
    protected void initialize(FTPControlSocket control) {
        this.control = control;
        control.setMessageListener(messageListener);
    }
    /**
     *   Set the TCP timeout on the underlying socket.
     *
     *   If a timeout is set, then any operation which
     *   takes longer than the timeout value will be
     *   killed with a java.io.InterruptedException. We
     *   set both the control and data connections
     *
     *   @param millis The length of the timeout, in milliseconds
     */
    public void setTimeout(int millis)
            throws IOException {

        this.timeout = millis;
        control.setTimeout(millis);
    }

    /**
     *  Set the connect mode
     *
     *  @param  mode  ACTIVE or PASV mode
     */
    public void setConnectMode(FTPConnectMode mode) {
        connectMode = mode;
    }

    /**
     * Set a listener that handles all FTP messages
     *
     * @param listener  message listener
     */
    public void setMessageListener(FTPMessageListener listener) {
        this.messageListener = listener;
        if (control != null)
            control.setMessageListener(listener);
    }

    /**
     *  Set a progress monitor for callbacks. The bytes transferred in
     *  between callbacks is only indicative. In many cases, the data is
     *  read in chunks, and if the interval is set to be smaller than the
     *  chunk size, the callback will occur after after chunk transfer rather
     *  than the interval.
     *
     *  @param  monitor   the monitor object
     *  @param  interval  bytes transferred in between callbacks
     */
    public void setProgressMonitor(FTPProgressMonitor monitor, long interval) {
        this.monitor = monitor;
        this.monitorInterval = interval;
    }

    /**
     *  Set a progress monitor for callbacks. Uses default callback
     *  interval
     *
     *  @param  monitor   the monitor object
     */
    public void setProgressMonitor(FTPProgressMonitor monitor) {
        this.monitor = monitor;
    }

    /**
     *  Get the bytes transferred between each callback on the
     *  progress monitor
     *
     * @return long     bytes to be transferred before a callback
     */
    public long getMonitorInterval() {
        return monitorInterval;
    }

    /**
     *  Cancels the current transfer. Must be called from a separate
     *  thread. Note that this may leave partially written files on the
     *  server or on local disk, and should not be used unless absolutely
     *  necessary
     */
    public void cancelTransfer() {
        cancelTransfer = true;
    }

    /**
     *  Login into an account on the FTP server. This
     *  call completes the entire login process
     *
     *  @param   user       user name
     *  @param   password   user's password
     */
    public void login(String user, String password)
            throws IOException, FTPException {

        checkConnection(true);

        String reply = control.sendCommand("USER " + user);

        // we allow for a site with no password - 230 response
        String[] validCodes = {"230", "331"};
        lastValidReply = control.validateReply(reply, validCodes);
        if (lastValidReply.getReplyCode().equals("230"))
            return;
        else {
            password(password);
        }
    }

    /**
     *  Supply the user name to log into an account
     *  on the FTP server. Must be followed by the
     *  password() method - but we allow for
     *
     *  @param   user       user name
     */
    public void user(String user)
            throws IOException, FTPException {

        checkConnection(true);

        String reply = control.sendCommand("USER " + user);

        // we allow for a site with no password - 230 response
        String[] validCodes = {"230", "331"};
        lastValidReply = control.validateReply(reply, validCodes);
    }


    /**
     *  Supplies the password for a previously supplied
     *  username to log into the FTP server. Must be
     *  preceeded by the user() method
     *
     *  @param   password       The password.
     */
    public void password(String password)
            throws IOException, FTPException {

        checkConnection(true);

        String reply = control.sendCommand("PASS " + password);

        // we allow for a site with no passwords (202)
        String[] validCodes = {"230", "202"};
        lastValidReply = control.validateReply(reply, validCodes);
    }

    /**
     *  Set up SOCKS v4/v5 proxy settings. This can be used if there
     *  is a SOCKS proxy server in place that must be connected thru.
     *  Note that setting these properties directs <b>all</b> TCP
     *  sockets in this JVM to the SOCKS proxy
     *
     *  @param  port  SOCKS proxy port
     *  @param  host  SOCKS proxy hostname
     */
    public static void initSOCKS(String port, String host) {
        Properties props = System.getProperties();
        props.put("socksProxyPort", port);
        props.put("socksProxyHost", host);
        System.setProperties(props);
    }

    /**
     *  Set up SOCKS username and password for SOCKS username/password
     *  authentication. Often, no authentication will be required
     *  but the SOCKS server may be configured to request these.
     *
     *  @param  username   the SOCKS username
     *  @param  password   the SOCKS password
     */
    public static void initSOCKSAuthentication(String username,
                                               String password) {
        Properties props = System.getProperties();
        props.put("java.net.socks.username", username);
        props.put("java.net.socks.password", password);
        System.setProperties(props);
    }

    /**
     *  Get the name of the remote host
     *
     *  @return  remote host name
     */
    String getRemoteHostName() {
        return control.getRemoteHostName();
    }

    /**
     *  Issue arbitrary ftp commands to the FTP server.
     *
     *  @param command     ftp command to be sent to server
     *  @param validCodes  valid return codes for this command
     *
     *  @return  the text returned by the FTP server
     */
    public String quote(String command, String[] validCodes)
            throws IOException, FTPException {

        checkConnection(true);

        String reply = control.sendCommand(command);

        // allow for no validation to be supplied
        if (validCodes != null && validCodes.length > 0) {
            lastValidReply = control.validateReply(reply, validCodes);
            return lastValidReply.getReplyText();
        }
        else {
            throw new FTPException("Valid reply code must be supplied");
        }
    }


    /**
     *  Get the size of a remote file. This is not a standard FTP command, it
     *  is defined in "Extensions to FTP", a draft RFC
     *  (draft-ietf-ftpext-mlst-16.txt)
     *
     *  @param  remoteFile  name or path of remote file in current directory
     *  @return size of file in bytes
     */
    public long size(String remoteFile)
            throws IOException, FTPException {

        checkConnection(true);

        String reply = control.sendCommand("SIZE " + remoteFile);
        lastValidReply = control.validateReply(reply, "213");

        // parse the reply string .
        String replyText = lastValidReply.getReplyText();

        // trim off any trailing characters after a space, e.g. webstar
        // responds to SIZE with 213 55564 bytes
        int spacePos = replyText.indexOf(' ');
        if (spacePos >= 0)
            replyText = replyText.substring(0, spacePos);

        // parse the reply
        try {
            return Long.parseLong(replyText);
        }
        catch (NumberFormatException ex) {
            throw new FTPException("Failed to parse reply: " + replyText);
        }
    }


    /**
     *  Put a local file onto the FTP server. It
     *  is placed in the current directory.
     *
     *  @param  localPath   path of the local file
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
    public void put(String localPath, String remoteFile)
            throws IOException, FTPException {

        put(localPath, remoteFile, false);
    }

    /**
     *  Put a stream of data onto the FTP server. It
     *  is placed in the current directory.
     *
     *  @param  srcStream   input stream of data to put
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
    public void put(InputStream srcStream, String remoteFile)
            throws IOException, FTPException {

        put(srcStream, remoteFile, false);
    }


    /**
     *  Put a local file onto the FTP server. It
     *  is placed in the current directory. Allows appending
     *  if current file exists
     *
     *  @param  localPath   path of the local file
     *  @param  remoteFile  name of remote file in
     *                      current directory
     *  @param  append      true if appending, false otherwise
     */
    public void put(String localPath, String remoteFile,
                    boolean append)
            throws IOException, FTPException {

        // get according to set type
        if (getType() == FTPTransferType.ASCII) {
            putASCII(localPath, remoteFile, append);
        }
        else {
            putBinary(localPath, remoteFile, append);
        }
        validateTransfer();
    }

    /**
     *  Put a stream of data onto the FTP server. It
     *  is placed in the current directory. Allows appending
     *  if current file exists
     *
     *  @param  srcStream   input stream of data to put
     *  @param  remoteFile  name of remote file in
     *                      current directory
     *  @param  append      true if appending, false otherwise
     */
    public void put(InputStream srcStream, String remoteFile,
                    boolean append)
            throws IOException, FTPException {

        // get according to set type
        if (getType() == FTPTransferType.ASCII) {
            putASCII(srcStream, remoteFile, append);
        }
        else {
            putBinary(srcStream, remoteFile, append);
        }
        validateTransfer();
    }

    /**
     * Validate that the put() or get() was successful.  This method is not
     * for general use.
     */
    public void validateTransfer()
            throws IOException, FTPException {

        checkConnection(true);

        // check the control response
        String[] validCodes = {"226", "250"};
        String reply = control.readReply();
        lastValidReply = control.validateReply(reply, validCodes);
    }

    /**
     *  Request the server to set up the put
     *
     *  @param  remoteFile  name of remote file in
     *                      current directory
     *  @param  append      true if appending, false otherwise
     */
    private void initPut(String remoteFile, boolean append)
            throws IOException, FTPException {

        checkConnection(true);

        // reset the cancel flag
        cancelTransfer = false;

        boolean close = false;
        try {
            // set up data channel
            data = control.createDataSocket(connectMode);
            data.setTimeout(timeout);

            // send the command to store
            String cmd = append ? "APPE " : "STOR ";
            String reply = control.sendCommand(cmd + remoteFile);

            // Can get a 125 or a 150
            String[] validCodes = {"125", "150"};
            lastValidReply = control.validateReply(reply, validCodes);
        }
        catch (IOException ex) {
            close = true;
            throw ex;
        }
        catch (FTPException ex) {
            close = true;
            throw ex;
        }
        finally {
            if (close) {
                try {
                    data.close();
                }
                catch (IOException ignore) {}
            }
        }
    }


    /**
     *  Put as ASCII, i.e. read a line at a time and write
     *  inserting the correct FTP separator
     *
     *  @param localPath   full path of local file to read from
     *  @param remoteFile  name of remote file we are writing to
     *  @param  append      true if appending, false otherwise
     */
    private void putASCII(String localPath, String remoteFile, boolean append)
            throws IOException, FTPException {

        // create an inputstream & pass to common method
        InputStream srcStream = new FileInputStream(localPath);
        putASCII(srcStream, remoteFile, append);
    }

    /**
     *  Put as ASCII, i.e. read a line at a time and write
     *  inserting the correct FTP separator
     *
     *  @param  srcStream   input stream of data to put
     *  @param  remoteFile  name of remote file we are writing to
     *  @param  append      true if appending, false otherwise
     */
    private void putASCII(InputStream srcStream, String remoteFile,
                          boolean append)
            throws IOException, FTPException {

        // need to read line by line ...
        LineNumberReader in = null;
        BufferedWriter out = null;
        try {
            in = new LineNumberReader(new InputStreamReader(srcStream));

            initPut(remoteFile, append);

            // get an character output stream to write to ... AFTER we
            // have the ok to go ahead AND AFTER we've successfully opened a
            // stream for the local file
            out = new BufferedWriter(
                    new OutputStreamWriter(data.getOutputStream()));

            // write \r\n as required by RFC959 after each line
            long size = 0;
            long monitorCount = 0;
            int ch = -1;
            while ((ch = in.read()) != -1 && !cancelTransfer) {
                size++;
                monitorCount++;
                if (ch == '\n')
                    out.write(FTPControlSocket.EOL);
                else
                    out.write(ch);

                if (monitor != null && monitorCount > monitorInterval) {
                    monitor.bytesTransferred(size);
                    monitorCount = 0;
                }
            }
        }
        finally {
            try {
                if (in != null)
                    in.close();
            }
            catch (IOException ignore) {}
            try {
                if (out != null) {
                    out.flush();
                    //out.close(); // Hans
                }
            }
            catch (IOException ignore) {}

            // and close the data socket
            try {
                data.close();
            }
            catch (IOException ignore) {}
        }
    }


    /**
     *  Put as binary, i.e. read and write raw bytes
     *
     *  @param localPath   full path of local file to read from
     *  @param remoteFile  name of remote file we are writing to
     *  @param  append      true if appending, false otherwise
     */
    private void putBinary(String localPath, String remoteFile,
                           boolean append)
            throws IOException, FTPException {

        // open input stream to read source file ... do this
        // BEFORE opening output stream to server, so if file not
        // found, an exception is thrown
        InputStream srcStream = new FileInputStream(localPath);
        putBinary(srcStream, remoteFile, append);
    }

    /**
     *  Put as binary, i.e. read and write raw bytes
     *
     *  @param  srcStream   input stream of data to put
     *  @param  remoteFile  name of remote file we are writing to
     *  @param  append      true if appending, false otherwise
     */
    private void putBinary(InputStream srcStream, String remoteFile,
                           boolean append)
            throws IOException, FTPException {

        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(srcStream);

            initPut(remoteFile, append);

            // get an output stream
            out = new BufferedOutputStream(
                    new DataOutputStream(data.getOutputStream()));

            byte[] buf = new byte[512];

            // read a chunk at a time and write to the data socket
            long size = 0;
            long monitorCount = 0;
            int count = 0;
            while ((count = in.read(buf)) > 0 && !cancelTransfer) {
                out.write(buf, 0, count);
                size += count;
                monitorCount += count;
                if (monitor != null && monitorCount > monitorInterval) {
                    monitor.bytesTransferred(size);
                    monitorCount = 0;
                }
            }
            // log bytes transferred
            //logger.info("Transferred " + size + " bytes to remote host");
        }
        finally {
            try {
                if (in != null)
                    in.close();
            }
            catch (IOException ignore) {}

            // flush and clean up
            try {
                if (out != null) {
                    out.flush();
                    out.close(); // Hans XXXXXXX
                }
            }
            catch (IOException ignore) {}

            // and close the data socket
            try {
                data.close();
            }
            catch (IOException ignore) {}
        }
    }


    /**
     *  Put data onto the FTP server. It
     *  is placed in the current directory.
     *
     *  @param  bytes        array of bytes
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
    public void put(byte[] bytes, String remoteFile)
            throws IOException, FTPException {

        put(bytes, remoteFile, false);
    }

    /**
     *  Put data onto the FTP server. It
     *  is placed in the current directory. Allows
     *  appending if current file exists
     *
     *  @param  bytes        array of bytes
     *  @param  remoteFile  name of remote file in
     *                      current directory
     *  @param  append      true if appending, false otherwise
     */
    public void put(byte[] bytes, String remoteFile, boolean append)
            throws IOException, FTPException {

        initPut(remoteFile, append);

        // get an output stream
        BufferedOutputStream out =
                new BufferedOutputStream(
                        new DataOutputStream(data.getOutputStream()));

        try
        {
            // write array
            out.write(bytes, 0, bytes.length);
        }
        finally
        {

            // flush and clean up
            try
            {
                out.flush();
                //out.close(); // Hans
            }
            catch (IOException ignore)
            {

            }
            // and close the data socket
            try
            {
                data.close();
            }
            catch (IOException ignore) {}
        }

        validateTransfer();
    }


    /**
     *  Get data from the FTP server. Uses the currently
     *  set transfer mode.
     *
     *  @param  localPath   local file to put data in
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
    public void get(String localPath, String remoteFile)
            throws IOException, FTPException {

        // get according to set type
        if (getType() == FTPTransferType.ASCII) {
            getASCII(localPath, remoteFile);
        }
        else {
            getBinary(localPath, remoteFile);
        }
        validateTransfer();
    }
    /**
     *  Get data from the FTP server. Uses the currently
     *  set transfer mode. This method only process normal file .
     * Added by Ying Sun(suny[at]mail.cbi.pku.edu.cn).
     *  @param  outStream
     *  @param  remoteFile  name of remote file in current directory
     */
    public void  getEx(OutputStream outStream, FTPFile remoteFile)
            throws IOException, FTPException
    {

        long sizeLocal = -1;
        long  dateLocal = -1;
        long sizeRemote = remoteFile.size();
        long  dateRemote = remoteFile.lastModified().getTime();
        long sizeTransfered = getBinary(outStream,remoteFile.getName());
        if(sizeTransfered!=sizeRemote)
            throw new IOException("Transfered size does not equal to original file size");
        validateTransfer();
    }

    /**
     *  Get data from the FTP server. Uses the currently
     *  set transfer mode. This method only process normal file .
     * Added by Ying Sun(suny[at]mail.cbi.pku.edu.cn).
     *  @param  localFile   local file to put data in
     *  @param  remoteFile  name of remote file in current directory
     *  @param overwrite flag to indicate the transfer position,
     *                          true-Overwrite, false-Append
     */
    public void  getEx(String localFile, FTPFile remoteFile,boolean overwrite)
            throws IOException, FTPException
    {

        long sizeLocal = -1;
        long  dateLocal = -1;
        long sizeRemote = remoteFile.size();;
        long  dateRemote = remoteFile.lastModified().getTime();
        long sizeTransfered = 0;
        File fLocal = new File(localFile);
        if(overwrite)
        {
            //force to overwrite local file
            sizeTransfered = getBinary(localFile,remoteFile.getName());
            validateTransfer();
        }
        else
        {
            if(fLocal.exists())
            {
                //the size of local file
                sizeLocal = fLocal.length();
                //the last modified time of local file
                dateLocal = fLocal.lastModified();
                //the size of remote file
                if(sizeRemote>sizeLocal)
                {
                    sizeTransfered = getBinary(localFile, remoteFile.getName(),sizeLocal);
                    validateTransfer();
                }
            }
            else
            {
                //local file is NOT exists , overwrite
                sizeTransfered = getBinary(localFile, remoteFile.getName());
                validateTransfer();
            }
        }
        fLocal.setLastModified(dateRemote);
        if(sizeTransfered!=sizeRemote)
            throw new IOException("Transfered size does not equal to original file size");

    }
    private String workingDir = null;
    //private long sizeTransfered = 0;
    /**
     * get a file or a directory.
     *
     * @param outStream local directory to store downloaded file(directory)
     * @param remoteFile a regular file or a directory.
     * */
    public void getFile(OutputStream outStream,String remoteFile)
            throws IOException, FTPException,ParseException
    {
        //System.out.println("getFile:"+remoteFile);
        String parent = FilenameUtils.getFullPath(remoteFile);
        String son = FilenameUtils.getName(remoteFile);
        FTPFile[] remoteFiles = dirDetails(parent);
        boolean isdir = false;
        for(int i=0;i<remoteFiles.length;i++)
        {
            FTPFile rf = remoteFiles[i];
            //System.out.println(rf.getName());
            if(rf.getName().equals(son))
            {
                if(rf.isDir())
                {
                    isdir = true;
                }
                break;
            }
        }
        if(isdir)
        {
            //System.out.println("getDirectory:"+remoteFile);
            getDirectory(outStream,remoteFile,true);
        }
        else
        {
            //System.out.println("get:"+remoteFile);
            get(outStream,remoteFile);
        }
    }

    /**
     * get a file or a directory.
     *
     * @param localDir local directory to store downloaded file(directory)
     * @param remoteFile a regular file or a directory.
     * */
    public void getFile(String localDir,String remoteFile)
            throws IOException, FTPException,ParseException
    {
        //String parent = FilenameUtils.getFullPath(remoteFile);
        //System.out.println(parent);
        //String son = FilenameUtils.getName(remoteFile);
        String son = remoteFile;
        //System.out.println(son);
        //FTPFile[] remoteFiles = dirDetails(parent);
        FTPFile[] remoteFiles = dirDetails(".");
        boolean isdir = false;
        for(int i=0;i<remoteFiles.length;i++)
        {
            FTPFile rf = remoteFiles[i];
            //System.out.println(rf.getName());
            //modtime(rf.getName());
            if(rf.getName().equals(son))
            {
                if(rf.isDir())
                {
                    isdir = true;
                }
                break;
            }
        }
        if(isdir)
        {
            //System.out.println("getDirectory:"+remoteFile);
            getDirectory(localDir,remoteFile,true);
        }
        else
        {
            //System.out.println("get:"+remoteFile);
            //String localFullPath = localDir+"/"+parent+remoteFiles[i].getName();

            //File localDirToMk = new File(localDir+"/"+parent);
            //if(!localDirToMk.exists())
            //{
            //localDirToMk.mkdirs();
            //}
              //get(localDir+"\\"+remoteFile,remoteFile);
            String localFile = FilenameUtils.concat(localDir,remoteFile);
            get(localFile,remoteFile);
        }
    }

    /**
     *  Get data from the FTP server. Uses the currently
     *  set transfer mode.
     *
     *  @param  outStream  output stream to put file in
     *  @param  remoteDir name of remote Directory
     *  @param  overwrite overwrite or append existing files true---overwrite false---append
     */
    public void getDirectory(OutputStream outStream, String remoteDir,boolean overwrite)
        throws IOException, FTPException,ParseException {
    {
            if(workingDir==null)
                workingDir = remoteDir+"/";
            else
                workingDir +=remoteDir+"/";
            chdir(remoteDir);
            FTPFile currentDir = null;
            FTPFile[] remoteFiles = dirDetails(".");
            for(int i=0;i<remoteFiles.length;i++)
            {
                if(remoteFiles[i].isDir())
                {
                    if(!remoteFiles[i].getName().equalsIgnoreCase(".")
                            &&!remoteFiles[i].getName().equalsIgnoreCase(".."))
                    {
                        currentDir = remoteFiles[i];
                        getDirectory(outStream,currentDir.getName(),overwrite);
                    }
                }
                else//remote file is a normal file
                {
                    getEx(outStream,remoteFiles[i]);
                    logger.info("Transfering " + workingDir+remoteFiles[i].getName()+" finished");
                }
            }
            chdir("..");
            int index = workingDir.lastIndexOf("/",workingDir.length()-2);
            if(index!=-1)
            {
                workingDir = workingDir.substring(0,index+1);
            }
        }
    }
    //private boolean isRemoteFileDir
    /**
     *  Get data from the FTP server. Uses the currently
     *  set transfer mode.
     *
     *  @param  localDir  local Directory to put file in
     *  @param  remoteDir name of remote Directory
     *  @param  overwrite overwrite or append existing files true---overwrite false---append
     */
    public void getDirectory(String localDir, String remoteDir,boolean overwrite)
        throws IOException, FTPException,ParseException {
    {
            if(workingDir==null)
                workingDir = remoteDir+"/";
            else
                workingDir +=remoteDir+"/";
            chdir(remoteDir);
            FTPFile currentDir = null;
            FTPFile[] remoteFiles = dirDetails(".");
            for(int i=0;i<remoteFiles.length;i++)
            {
                if(remoteFiles[i].isDir())
                {
                    if(!remoteFiles[i].getName().equalsIgnoreCase(".")
                            &&!remoteFiles[i].getName().equalsIgnoreCase(".."))
                    {
                        currentDir = remoteFiles[i];
                        getDirectory(localDir,currentDir.getName(),overwrite);
                    }
                }
                else//remote file is a normal file
                {
                    //the full path of local file ,i.e. /home/suny/data/somefiles1.txt
                    String localFullPath = localDir+"/"+workingDir+remoteFiles[i].getName();
                    File localDirToMk = new File(localDir+"/"+workingDir);
                    if(!localDirToMk.exists())
                    {
                        localDirToMk.mkdirs();
                    }
                    //the name of remote file ,i.e.  somefiles1.txt
//                    String remoteFullPath = remoteFiles[i].getName();
                    getEx(localFullPath,remoteFiles[i],overwrite);
                    logger.info("Transfering " + workingDir+remoteFiles[i].getName()+" finished");
                }
            }
            chdir("..");
            int index = workingDir.lastIndexOf("/",workingDir.length()-2);
            if(index!=-1)
            {
                workingDir = workingDir.substring(0,index+1);
            }
        }
    }
    /**
     *  Get data from the FTP server. Uses the currently
     *  set transfer mode.
     *
     *  @param  destStream  data stream to write data to
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
    public void get(OutputStream destStream, String remoteFile)
            throws IOException, FTPException
    {

        // get according to set type
        if (getType() == FTPTransferType.ASCII)
        {
            getASCII(destStream, remoteFile);
        }
        else
        {
            getBinary(destStream, remoteFile);
        }
        validateTransfer();
    }


    /**
     *  Request to the server that the get is set up
     * Support transfering from the point of last interruption.
     *  Revised by Ying Sun.(suny[at]mail.cbi.pku.edu.cn)
     *  @param  remoteFile  name of remote file
     */
    private void initGetEx(String remoteFile,long  StartFrom)
            throws IOException, FTPException {

        checkConnection(true);
        // reset the cancel flag
        cancelTransfer = false;
        boolean close = false;
        String reply;
        try {
            // set up data channel
            data = control.createDataSocket(connectMode);
            data.setTimeout(timeout);
            reply = control.sendCommand("REST " + StartFrom);
            String[] validCodes1 = {"350"};
            lastValidReply = control.validateReply(reply, validCodes1);
            // send the retrieve command
            reply = control.sendCommand("RETR " + remoteFile);
            // Can get a 125(ASCII mode) or a 150 (binary mode)
            String[] validCodes2 = {"125", "150"};
            lastValidReply = control.validateReply(reply, validCodes2);
        }
        catch (IOException ex)
        {
            close = true;
            throw ex;
        }
        catch (FTPException ex)
        {
            close = true;
            throw ex;
        }
        finally
        {
            if (close)
            {
                try
                {
                    data.close();
                }
                catch (IOException ignore) {}
            }
        }
    }

    private void initGet(String remoteFile)
            throws IOException, FTPException {

        checkConnection(true);

        // reset the cancel flag
        cancelTransfer = false;

        boolean close = false;
        try {
            // set up data channel
            data = control.createDataSocket(connectMode);
            data.setTimeout(timeout);
            // send the retrieve command
            String reply = control.sendCommand("RETR " + remoteFile);
            //System.out.println(reply);
            // Can get a 125 or a 150
            String[] validCodes1 = {"125", "150"};
            lastValidReply = control.validateReply(reply, validCodes1);
        }
        catch (IOException ex) {
            close = true;
            throw ex;
        }
        catch (FTPException ex) {
            close = true;
            throw ex;
        }
        finally {
            if (close) {
                try {
                    data.close();
                }
                catch (IOException ignore) {}
            }
        }
    }


    /**
     *  Get as ASCII, i.e. read a line at a time and write
     *  using the correct newline separator for the OS
     *
     *  @param localPath   full path of local file to write to
     *  @param remoteFile  name of remote file
     */
    private void getASCII(String localPath, String remoteFile)
            throws IOException, FTPException {

        // B.McKeown:
        // Call initGet() before creating the FileOutputStream.
        // This will prevent being left with an empty file if a FTPException
        // is thrown by initGet().
        initGet(remoteFile);

        // B. McKeown: Need to store the local file name so the file can be
        // deleted if necessary.
        File localFile = new File(localPath);

        // create the buffered stream for writing
        BufferedWriter out =
                new BufferedWriter(
                        new FileWriter(localPath));

        // get an character input stream to read data from ... AFTER we
        // have the ok to go ahead AND AFTER we've successfully opened a
        // stream for the local file
        LineNumberReader in =
                new LineNumberReader(
                        new BufferedReader(
                                new InputStreamReader(data.getInputStream())));

        // B. McKeown:
        // If we are in active mode we have to set the timeout of the passive
        // socket. We can achieve this by calling setTimeout() again.
        // If we are in passive mode then we are merely setting the value twice
        // which does no harm anyway. Doing this simplifies any logic changes.
        data.setTimeout(timeout);

        // output a new line after each received newline
        IOException storedEx = null;
        long size = 0;
        long monitorCount = 0;
        try {
            int ch = -1;
            while ((ch = readChar(in)) != -1 && !cancelTransfer) {
                size++;
                monitorCount++;
                if (ch == '\n')
                    out.newLine();
                else
                    out.write(ch);

                if (monitor != null && monitorCount > monitorInterval) {
                    monitor.bytesTransferred(size);
                    monitorCount = 0;
                }
            }
        }
        catch (IOException ex) {
            storedEx = ex;
            localFile.delete();
        }
        finally {
            out.close();
        }

        try {
            //in.close(); // Hans
            data.close();
        }
        catch (IOException ignore) {}

        // if we failed to write the file, rethrow the exception
        if (storedEx != null)
            throw storedEx;
    }

    /**
     *  Get as ASCII, i.e. read a line at a time and write
     *  using the correct newline separator for the OS
     *
     *  @param destStream  data stream to write data to
     *  @param remoteFile  name of remote file
     */
    private void getASCII(OutputStream destStream, String remoteFile)
            throws IOException, FTPException {

        initGet(remoteFile);

        // create the buffered stream for writing
        BufferedWriter out =
                new BufferedWriter(
                        new OutputStreamWriter(destStream));

        // get an character input stream to read data from ... AFTER we
        // have the ok to go ahead
        LineNumberReader in =
                new LineNumberReader(
                        new BufferedReader(
                                new InputStreamReader(data.getInputStream())));

        // B. McKeown:
        // If we are in active mode we have to set the timeout of the passive
        // socket. We can achieve this by calling setTimeout() again.
        // If we are in passive mode then we are merely setting the value twice
        // which does no harm anyway. Doing this simplifies any logic changes.
        data.setTimeout(timeout);

        // output a new line after each received newline
        IOException storedEx = null;
        long size = 0;
        long monitorCount = 0;
        try {
            int ch = -1;
            while ((ch = readChar(in)) != -1 && !cancelTransfer) {
                size++;
                monitorCount++;
                if (ch == '\n')
                    out.newLine();
                else
                    out.write(ch);

                if (monitor != null && monitorCount > monitorInterval) {
                    monitor.bytesTransferred(size);
                    monitorCount = 0;
                }
            }
        }
        catch (IOException ex) {
            storedEx = ex;
        }
        finally {
            out.close();
        }

        try {
            //in.close(); // Hans
            data.close();
        }
        catch (IOException ignore) {}

        // if we failed to write the file, rethrow the exception
        if (storedEx != null)
            throw storedEx;
    }


    /**
     *  Get as binary file, i.e. straight transfer of data
     *
     *  @param localPath   full path of local file to write to
     *  @param remoteFile  name of remote file
     */
    private long  getBinary(String localPath, String remoteFile)
            throws IOException, FTPException {

        // B.McKeown:
        // Call initGet() before creating the FileOutputStream.
        // This will prevent being left with an empty file if a FTPException
        // is thrown by initGet().
        initGet(remoteFile);
        // B. McKeown: Need to store the local file name so the file can be
        // deleted if necessary.
        File localFile = new File(localPath);

        // create the buffered output stream for writing the file
        BufferedOutputStream out =
                new BufferedOutputStream(
                        new FileOutputStream(localPath, false));

        // get an input stream to read data from ... AFTER we have
        // the ok to go ahead AND AFTER we've successfully opened a
        // stream for the local file
        BufferedInputStream in =
                new BufferedInputStream(
                        new DataInputStream(data.getInputStream()));

        // B. McKeown:
        // If we are in active mode we have to set the timeout of the passive
        // socket. We can achieve this by calling setTimeout() again.
        // If we are in passive mode then we are merely setting the value twice
        // which does no harm anyway. Doing this simplifies any logic changes.
        data.setTimeout(timeout);

        // do the retrieving
        long size = 0;
        long monitorCount = 0;
        int chunksize = 4096;
        byte [] chunk = new byte[chunksize];
        int count;
        IOException storedEx = null;

        // read from socket & write to file in chunks
        try {
            while ((count = readChunk(in, chunk, chunksize)) >= 0 && !cancelTransfer) {
                out.write(chunk, 0, count);
                size += count;
                monitorCount += count;

                if (monitor != null && monitorCount > monitorInterval) {
                    monitor.bytesTransferred(size);
                    monitorCount = 0;
                }
            }
        }
        catch (IOException ex) {
            storedEx = ex;
            localFile.delete();
        }
        finally {
            out.close();
        }

        // close streams
        try {
            //in.close(); // Hans
            data.close();
        }
        catch (IOException ignore) {}

        // if we failed to write the file, rethrow the exception
        if (storedEx != null)
            throw storedEx;

        // log bytes transferred
        //logger.info("Transferred " + size + " bytes from remote host");
        return size;
    }
    /**
     *  Get as binary file, i.e. straight transfer of data,but different
     * with getBinary, support append to local file.
     * revised by Ying Sun. (suny@mail.cbi.pku.edu.cn)
     *
     *  @param localFile   full path of local file to write to
     *  @param remoteFile  name of remote file
     */
    private long   getBinary(String localFile, String remoteFile,long offset)
            throws IOException, FTPException
    {

        if(offset<=0)
        {
            return getBinary(localFile, remoteFile);
        }
        // B.McKeown:
        // Call initGet() before creating the FileOutputStream.
        // This will prevent being left with an empty file if a FTPException
        // is thrown by initGet().

//     System.out.println("From offset = " + offset);

        initGetEx(remoteFile,offset);
        // B. McKeown: Need to store the local file name so the file can be
        // deleted if necessary.
        File local = new File(localFile);

        // create the buffered output stream for writing the file
        BufferedOutputStream out =
                new BufferedOutputStream(
                        new FileOutputStream(local, true));

        // get an input stream to read data from ... AFTER we have
        // the ok to go ahead AND AFTER we've successfully opened a
        // stream for the local file
        BufferedInputStream in =
                new BufferedInputStream(
                        new DataInputStream(data.getInputStream()));

        // B. McKeown:
        // If we are in active mode we have to set the timeout of the passive
        // socket. We can achieve this by calling setTimeout() again.
        // If we are in passive mode then we are merely setting the value twice
        // which does no harm anyway. Doing this simplifies any logic changes.
        data.setTimeout(timeout);

        // do the retrieving
        long size = 0;
        long monitorCount = 0;
        int chunksize = 4096;
        byte [] chunk = new byte[chunksize];
        int count;
        IOException storedEx = null;

        // read from socket & write to file in chunks
        try
        {
            while ((count = readChunk(in, chunk, chunksize)) >= 0 && !cancelTransfer)
            {
                out.write(chunk, 0, count);
                size += count;
                monitorCount += count;

                if (monitor != null && monitorCount > monitorInterval)
                {
                    monitor.bytesTransferred(size);
                    monitorCount = 0;
                }
            }
        }
        catch (IOException ex)
        {
            storedEx = ex;
            local.delete();
        }
        finally
        {
            out.close();
        }

        // close streams
        try
        {
            //in.close(); // Hans
            data.close();
        }
        catch (IOException ignore) {}

        // if we failed to write the file, rethrow the exception
        if (storedEx != null)
            throw storedEx;

        // log bytes transferred

        //logger.info("Transferred " + size + " bytes from remote host");
        return size;
    }

    /**
     *  Get as binary file, i.e. straight transfer of data
     *
     *  @param destStream  stream to write to
     *  @param remoteFile  name of remote file
     */
    private long getBinary(OutputStream destStream, String remoteFile)
            throws IOException, FTPException {

        initGet(remoteFile);

        // create the buffered output stream for writing the file
        BufferedOutputStream out =
                new BufferedOutputStream(destStream);

        // get an input stream to read data from ... AFTER we have
        // the ok to go ahead AND AFTER we've successfully opened a
        // stream for the local file
        BufferedInputStream in =
                new BufferedInputStream(
                        new DataInputStream(data.getInputStream()));

        // B. McKeown:
        // If we are in active mode we have to set the timeout of the passive
        // socket. We can achieve this by calling setTimeout() again.
        // If we are in passive mode then we are merely setting the value twice
        // which does no harm anyway. Doing this simplifies any logic changes.
        data.setTimeout(timeout);

        // do the retrieving
        long size = 0;
        long monitorCount = 0;
        int chunksize = 4096;
        byte [] chunk = new byte[chunksize];
        int count;
        IOException storedEx = null;

        // read from socket & write to file in chunks
        try {
            while ((count = readChunk(in, chunk, chunksize)) >= 0 && !cancelTransfer) {
                out.write(chunk, 0, count);
                size += count;
                monitorCount += count;

                if (monitor != null && monitorCount > monitorInterval) {
                    monitor.bytesTransferred(size);
                    monitorCount = 0;
                }
            }
        }
        catch (IOException ex) {
            storedEx = ex;
        }
        finally {
            out.close();
        }

        // close streams
        try {
            //in.close(); // Hans
            data.close();
        }
        catch (IOException ignore) {}

        // if we failed to write to the stream, rethrow the exception
        if (storedEx != null)
            throw storedEx;

        // log bytes transferred
        //logger.info("Transferred " + size + " bytes from remote host");
        return size;
    }

    /**
     *  Get data from the FTP server. Transfers in
     *  whatever mode we are in. Retrieve as a byte array. Note
     *  that we may experience memory limitations as the
     *  entire file must be held in memory at one time.
     *
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
    public byte[] get(String remoteFile)
            throws IOException, FTPException {

        initGet(remoteFile);

        // get an input stream to read data from
        BufferedInputStream in =
                new BufferedInputStream(
                        new DataInputStream(data.getInputStream()));

        // B. McKeown:
        // If we are in active mode we have to set the timeout of the passive
        // socket. We can achieve this by calling setTimeout() again.
        // If we are in passive mode then we are merely setting the value twice
        // which does no harm anyway. Doing this simplifies any logic changes.
        data.setTimeout(timeout);

        // do the retrieving
        long size = 0;
        long monitorCount = 0;
        int chunksize = 4096;
        byte [] chunk = new byte[chunksize];  // read chunks into
        byte [] resultBuf = null; // where we place result
        ByteArrayOutputStream temp =
                new ByteArrayOutputStream(chunksize); // temp swap buffer
        int count;  // size of chunk read

        // read from socket & write to file
        while ((count = readChunk(in, chunk, chunksize)) >= 0 && !cancelTransfer) {
            temp.write(chunk, 0, count);
            size += count;
            monitorCount += count;

            if (monitor != null && monitorCount > monitorInterval) {
                monitor.bytesTransferred(size);
                monitorCount = 0;
            }

        }
        temp.close();

        // get the bytes from the temp buffer
        resultBuf = temp.toByteArray();

        // close streams
        try {
//            in.close();
            data.close();
        }
        catch (IOException ignore) {}

        validateTransfer();

        return resultBuf;
    }


    /**
     *  Run a site-specific command on the
     *  server. Support for commands is dependent
     *  on the server
     *
     *  @param  command   the site command to run
     *  @return true if command ok, false if
     *          command not implemented
     */
    public boolean site(String command)
            throws IOException, FTPException {

        checkConnection(true);

        // send the retrieve command
        String reply = control.sendCommand("SITE " + command);

        // Can get a 200 (ok) or 202 (not impl). Some
        // FTP servers return 502 (not impl)
        String[] validCodes = {"200", "202", "502"};
        lastValidReply = control.validateReply(reply, validCodes);

        // return true or false? 200 is ok, 202/502 not
        // implemented
        if (reply.substring(0, 3).equals("200"))
            return true;
        else
            return false;
    }
    /**
     *  List a directory's contents as an array of strings of filenames.
     *  Should work for Windows and most Unix FTP servers - let us know
     *  about unusual formats (support@enterprisedt.com)
     *
     *  @param   dirname  name of directory(<b>not</b> a file mask)
     *  @return  an array of FTPFile objects
     */
    public FTPFile[] dirDetails(String dirname)
            throws IOException, FTPException, ParseException {

        // create the factory
        if (fileFactory == null)
            fileFactory = new FTPFileFactory(system());

        // get the details and parse
        return fileFactory.parse(dir(dirname, true));
    }

    /**
     *  List current directory's contents as an array of strings of
     *  filenames.
     *
     *  @return  an array of current directory listing strings
     */
    public String[] dir()
            throws IOException, FTPException {

        return dir(null, false);
    }

    /**
     *  List a directory's contents as an array of strings of filenames.
     *
     *  @param   dirname  name of directory(<b>not</b> a file mask)
     *  @return  an array of directory listing strings
     */
    public String[] dir(String dirname)
            throws IOException, FTPException {

        return dir(dirname, false);
    }


    /**
     *  List a directory's contents as an array of strings. A detailed
     *  listing is available, otherwise just filenames are provided.
     *  The detailed listing varies in details depending on OS and
     *  FTP server. Note that a full listing can be used on a file
     *  name to obtain information about a file
     *
     *  @param  dirname  name of directory (<b>not</b> a file mask)
     *  @param  full     true if detailed listing required
     *                   false otherwise
     *  @return  an array of directory listing strings
     */
    public String[] dir(String dirname, boolean full)
            throws IOException, FTPException {

        checkConnection(true);
        // set up data channel
        data = control.createDataSocket(connectMode);
        data.setTimeout(timeout);

        // send the retrieve command
        String command = full ? "LIST ":"NLST ";
        if (dirname != null)
            command += dirname;

        // some FTP servers bomb out if NLST has whitespace appended
        command = command.trim();
        String reply = control.sendCommand(command);
        // check the control response. wu-ftp returns 550 if the
        // directory is empty, so we handle 550 appropriately. Similarly
        // proFTPD returns 450
        String[] validCodes1 = {"125", "150", "450", "550"};
        lastValidReply = control.validateReply(reply, validCodes1);

        // an empty array of files for 450/550
        String[] result = new String[0];

        // a normal reply ... extract the file list
        String replyCode = lastValidReply.getReplyCode();
        if (!replyCode.equals("450") && !replyCode.equals("550")) {
            // get a character input stream to read data from .
            LineNumberReader in =
                    new LineNumberReader(
                            new InputStreamReader(data.getInputStream()));

            // read a line at a time
            Vector<String> lines = new Vector<String>();
            String line = null;
            while ((line = readLine(in)) != null) {
                lines.addElement(line);
            }
            try {
//				in.close();
                data.close();
            }
            catch (IOException ignore) {}

            // check the control response
            String[] validCodes2 = {"226", "250"};
            reply = control.readReply();
            lastValidReply = control.validateReply(reply, validCodes2);

            // empty array is default
            if (!lines.isEmpty()) {
                result = new String[lines.size()];
                try {
                    result = (String[])lines.toArray(result);
                }
                catch (NoSuchMethodError ex) { // for 1.1.x JVMs
                    for (int i = 0; i < lines.size(); i++) {
                        result[i] = (String)lines.elementAt(i);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Attempts to read a specified number of bytes from the given 
     * <code>InputStream</code> and place it in the given byte-array.
     * The purpose of this method is to permit subclasses to execute
     * any additional code necessary when performing this operation. 
     * @param in The <code>InputStream</code> to read from.
     * @param chunk The byte-array to place read bytes in.
     * @param chunksize Number of bytes to read.
     * @return Number of bytes actually read.
     * @throws IOException Thrown if there was an error while reading.
     */
    protected int readChunk(BufferedInputStream in, byte[] chunk, int chunksize)
            throws IOException {

        return in.read(chunk, 0, chunksize);
    }

    /**
     * Attempts to read a single character from the given <code>InputStream</code>. 
     * The purpose of this method is to permit subclasses to execute
     * any additional code necessary when performing this operation. 
     * @param in The <code>LineNumberReader</code> to read from.
     * @return The character read.
     * @throws IOException Thrown if there was an error while reading.
     */
    protected int readChar(LineNumberReader in)
            throws IOException {

        return in.read();
    }

    /**
     * Attempts to read a single line from the given <code>InputStream</code>. 
     * The purpose of this method is to permit subclasses to execute
     * any additional code necessary when performing this operation. 
     * @param in The <code>LineNumberReader</code> to read from.
     * @return The string read.
     * @throws IOException Thrown if there was an error while reading.
     */
    protected String readLine(LineNumberReader in)
            throws IOException {

        return in.readLine();
    }

    /**
     *  Gets the latest valid reply from the server
     *
     *  @return  reply object encapsulating last valid server response
     */
    public FTPReply getLastValidReply() {
        return lastValidReply;
    }


    /**
     *  Get the current transfer type
     *
     *  @return  the current type of the transfer,
     *           i.e. BINARY or ASCII
     */
    public FTPTransferType getType() {
        return transferType;
    }

    /**
     *  Set the transfer type
     *
     *  @param  type  the transfer type to
     *                set the server to
     */
    public void setType(FTPTransferType type)
            throws IOException, FTPException {

        checkConnection(true);

        // determine the character to send
        String typeStr = FTPTransferType.ASCII_CHAR;
        if (type.equals(FTPTransferType.BINARY))
            typeStr = FTPTransferType.BINARY_CHAR;

        // send the command
        String reply = control.sendCommand("TYPE " + typeStr);
        lastValidReply = control.validateReply(reply, "200");

        // record the type
        transferType = type;
    }


    /**
     *  Delete the specified remote file
     *
     *  @param  remoteFile  name of remote file to
     *                      delete
     */
    public void delete(String remoteFile)
            throws IOException, FTPException {

        checkConnection(true);
        String[] validCodes = {"200", "250"};
        String reply = control.sendCommand("DELE " + remoteFile);
        lastValidReply = control.validateReply(reply, validCodes);
    }


    /**
     *  Rename a file or directory
     *
     * @param from  name of file or directory to rename
     * @param to    intended name
     */
    public void rename(String from, String to)
            throws IOException, FTPException {

        checkConnection(true);

        String reply = control.sendCommand("RNFR " + from);
        lastValidReply = control.validateReply(reply, "350");

        reply = control.sendCommand("RNTO " + to);
        lastValidReply = control.validateReply(reply, "250");
    }


    /**
     *  Delete the specified remote working directory
     *
     *  @param  dir  name of remote directory to
     *               delete
     */
    public void rmdir(String dir)
            throws IOException, FTPException {

        checkConnection(true);

        String reply = control.sendCommand("RMD " + dir);

        // some servers return 200,257, technically incorrect but
        // we cater for it ...
        String[] validCodes = {"200", "250", "257"};
        lastValidReply = control.validateReply(reply, validCodes);
    }


    /**
     *  Create the specified remote working directory
     *
     *  @param  dir  name of remote directory to
     *               create
     */
    public void mkdir(String dir)
            throws IOException, FTPException {

        checkConnection(true);

        String reply = control.sendCommand("MKD " + dir);

        // some servers return 200,257, technically incorrect but
        // we cater for it ...
        String[] validCodes = {"200", "250", "257"};
        lastValidReply = control.validateReply(reply, validCodes);
    }


    /**
     *  Change the remote working directory to
     *  that supplied
     *
     *  @param  dir  name of remote directory to
     *               change to
     */
    public void chdir(String dir)
            throws IOException, FTPException {

        checkConnection(true);

        String reply = control.sendCommand("CWD " + dir);
        lastValidReply = control.validateReply(reply, "250");
    }

    /**
     *  Get modification time for a remote file
     *
     *  @param    remoteFile   name of remote file
     *  @return   modification time of file as a date
     */
    public Date modtime(String remoteFile)
            throws IOException, FTPException {

        checkConnection(true);

        String reply = control.sendCommand("MDTM " + remoteFile);
        lastValidReply = control.validateReply(reply, "213");

        // parse the reply string ...
        Date ts = tsFormat.parse(lastValidReply.getReplyText(),
                new ParsePosition(0));
        return ts;
    }

    /**
     *  Get the current remote working directory
     *
     *  @return   the current working directory
     */
    public String pwd()
            throws IOException, FTPException {

        checkConnection(true);

        String reply = control.sendCommand("PWD");
        lastValidReply = control.validateReply(reply, "257");

        // get the reply text and extract the dir
        // listed in quotes, if we can find it. Otherwise
        // just return the whole reply string
        String text = lastValidReply.getReplyText();
        int start = text.indexOf('"');
        int end = text.lastIndexOf('"');
        if (start >= 0 && end > start)
            return text.substring(start+1, end);
        else
            return text;
    }

    /**
     *  Get the type of the OS at the server
     *
     *  @return   the type of server OS
     */
    public String system()
            throws IOException, FTPException {

        checkConnection(true);

        String reply = control.sendCommand("SYST");
        lastValidReply = control.validateReply(reply, "215");
        return lastValidReply.getReplyText();
    }

    /**
     *  Get the help text for the specified command
     *
     *  @param  command  name of the command to get help on
     *  @return help text from the server for the supplied command
     */
    public String help(String command)
            throws IOException, FTPException {

        checkConnection(true);

        String reply = control.sendCommand("HELP " + command);
        String[] validCodes = {"211", "214"};
        lastValidReply = control.validateReply(reply, validCodes);
        return lastValidReply.getReplyText();
    }

    /**
     *  Quit the FTP session
     *
     */
    public void quit()
            throws IOException, FTPException {

        checkConnection(true);

        fileFactory = null;
        try {
            String reply = control.sendCommand("QUIT");
            String[] validCodes = {"221", "226"};
            lastValidReply = control.validateReply(reply, validCodes);
        }
        finally { // ensure we clean up the connection
            control.logout();
            control = null;
        }
    }
}



