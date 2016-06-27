package pku.cbi.abcgrid.worker.tar;
import java.io.*;
import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.math.*;



/**
 * Created by IntelliJ IDEA.
 * User: suny
 * Date: 2006-5-30
 * Time: 10:05:03
 * To change this template use File | Settings | File Templates.
 */
public class Tar
{
    private boolean unixArchiveFormat;
    private int blockSize=0;
    private TarArchive archive = null;
    public Tar()
    {
        this.unixArchiveFormat = true;
        this.blockSize = TarBuffer.DEFAULT_BLKSIZE;
    }
    public void close()
    {
        if (archive != null)                        // CLOSE ARCHIVE
        {
            try {
                archive.closeArchive();
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }
    public void open(InputStream inStream, String mode) throws Exception {
        boolean compressed = false;
        if (mode.equals("r:gz"))
            compressed = true;
        else if (mode.equals("r"))
            compressed = false;
        else
            throw new Exception("mode is not correct");
        try
        {
            if (compressed)
                inStream = new GZIPInputStream(inStream);
            archive = new TarArchive(inStream, this.blockSize);
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    public void open(OutputStream outStream, String mode) throws Exception {
        boolean compressed = false;
        if (mode.equals("w:gz"))
            compressed = true;
        else if (mode.equals("w"))
            compressed = false;
        else
            throw new Exception("mode is not correct");
        try
        {
            if (compressed)
                outStream = new GZIPOutputStream(outStream);
            archive = new TarArchive(outStream, this.blockSize);
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void open(File tarname, String mode) throws Exception {
        int operation = 0;// 0---create, 1---extract
        boolean compressed = false;
        if (mode.equals("w:gz")) {
            operation = 0;
            compressed = true;
        } else if (mode.equals("r:gz")) {
            operation = 1;
            compressed = true;
        } else if (mode.equals("r")) {
            operation = 1;
            compressed = false;
        } else if (mode.equals("w")) {
            operation = 0;
            compressed = false;
        } else {
            throw new Exception("mode is not correct");
        }
        if (operation == 0) //create.
        {
            try {
                OutputStream outStream = new FileOutputStream(tarname);
                if (compressed)
                    outStream = new GZIPOutputStream(outStream);
                archive = new TarArchive(outStream, this.blockSize);
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
        if (operation == 1) //extract
        {
            try {
                InputStream inStream = new FileInputStream(tarname);
                if (compressed)
                    inStream = new GZIPInputStream(inStream);
                archive = new TarArchive(inStream, this.blockSize);
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }
    public void appendNormalFile(File file)
    {
        try
        {
            TarEntry entry = new TarEntry(file);
            if (this.unixArchiveFormat)
                entry.setUnixTarFormat();
            else
                entry.setUSTarFormat();
            archive.writeEntry(entry, true);
        }
        catch (IOException ex)
        {
            ex.printStackTrace(System.err);
        }
    }
    public void append(File file) {
            if(file.isDirectory())
            {
                File[] fs = file.listFiles();
                for(File f:fs)
                {
                    if(! f.isDirectory() )
                        appendNormalFile(f);
                    else
                        append(f);
                }
            }
            else
            {
                appendNormalFile(file);
            }
    }
    public void append(InputStream inStream)
    {
        try
        {
            byte[] data = new byte[inStream.available()];
            inStream.read(data);
            TarEntry entry = new TarEntry(data);
            if (this.unixArchiveFormat)
                entry.setUnixTarFormat();
            else
                entry.setUSTarFormat();
            archive.writeEntry(entry, true);
        }
        catch (IOException ex)
        {
            ex.printStackTrace(System.err);
        }
    }
    public void append(byte[] data)
    {
        try
        {
            TarEntry entry = new TarEntry(data);
            if (this.unixArchiveFormat)
                entry.setUnixTarFormat();
            else
                entry.setUSTarFormat();
            archive.writeEntry(entry, true);
        }
        catch (IOException ex)
        {
            ex.printStackTrace(System.err);
        }
    }

    public void extract(String basepath)
    {
        String userDir = System.getProperty("user.dir", null);
        String topath = basepath == null?userDir:basepath;
        //File destDir = new File(userDir);
        File destDir = new File(topath);
        //System.out.println("Extract to:"+destDir.toString());
        if (! destDir.exists()) {
            if (! destDir.mkdirs()) {
                destDir = null;
                Throwable ex = new Throwable
                        ("ERROR, mkdirs() on '" + destDir.getPath()
                                + "' returned false.");
                ex.printStackTrace(System.err);
            }
        }
        if (destDir != null) {
            try {
                archive.extractContents(destDir);
            }
            catch (InvalidHeaderException ex) {
                ex.printStackTrace(System.err);
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }
    public static void main(String argv[]) throws Exception
    {
        Tar app = new Tar();
        //app.open(new File("E:\\gridftp\\grid\\database\\pdb.tar.gz"),"w:gz");
        //app.append(new File("E:\\gridftp\\grid\\database\\pdb"));
        String source  = "/home/suny/blast-2.2.14-ia32-linux.tar.gz";
        String destDir  = "/home/suny/HELLO/WORLD";
        app.open(new File(source),"r:gz");
        app.extract(destDir);
        app.close();
    }
}
