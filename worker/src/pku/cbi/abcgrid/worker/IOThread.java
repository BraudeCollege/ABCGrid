package pku.cbi.abcgrid.worker;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * A thread that copies IO.
 */
public final class IOThread extends Thread
{
    /**
     * An error produced by copying the IO
     */
    protected Throwable t;
    /**
     * The input source
     */
    protected InputStream is;
    /**
     * The output target
     */
    protected OutputStream os;
    protected boolean closeOutputWhenDone = true;
    protected StringBuffer sbuf;
    protected ByteBuffer bbuf;
    protected File file;

    public IOThread(InputStream is, OutputStream out, boolean closeOutputWhenDone)
    {
        super();
        this.is = is;
        this.os = out;
        this.closeOutputWhenDone = closeOutputWhenDone;
    }

    /**
     *
     */
    public IOThread(InputStream is, StringBuffer out)
    {
        this(is,new ByteArrayOutputStream(), true);
        this.sbuf = out;
        if (out == null) this.os = null;
    }
    /**
     * Copies is to a ByteArrayOutputStream, and, when complete, copies the
     * bytes to the ByteBuffer.
     */
    public IOThread(InputStream is, ByteBuffer out)
    {
        this(is, new ByteArrayOutputStream(), true);
        this.bbuf = out;
        if (out == null) this.os = null;
    }

    /**
     * Copies is to a the file.
     */
    public IOThread(InputStream is, File out)throws IOException
    {
        this(is, new BufferedOutputStream(new FileOutputStream(out)), true);
        this.file = out;
        if (out == null) this.os = null;
    }
    /**
     * Perform the copy, cleaning up afterward
     */
    public void run()
    {
        try
        {
            byte[] buf = new byte[10240];
            int count = 0;
            while ((count = is.read(buf)) != -1)
            {
                if (os != null) os.write(buf, 0, count);
            }
        }
        catch (Throwable t)
        {
            this.t = t;
        }
        finally
        {
            if (os != null)
            {
                try
                {
                    os.flush();
                    //ByteArrayOutputStream baos = new ByteArrayOutputStream()
                    //System.out.println(os.);
                    if (sbuf != null)
                    {
                        sbuf.append(new String(((ByteArrayOutputStream) os).toByteArray()));
                    }
                    else if (bbuf != null)
                    {
                        bbuf.put(((ByteArrayOutputStream) os).toByteArray());
                    }
                    else if(file!=null)
                    {
                        FileUtils.writeByteArrayToFile(file,((ByteArrayOutputStream) os).toByteArray());
                    }
                    else
                    {

                    }
                    if (closeOutputWhenDone)
                    {
                        os.close();
                    }
                }
                catch (Throwable t)
                {
                    if (this.t == null)
                        this.t = t;
                }
            }
        }
    }
}
