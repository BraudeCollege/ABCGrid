package pku.cbi.abcgrid.worker.ftp;

/**
 * Created by IntelliJ IDEA.
 * User: suny
 * Date: Jun 12, 2006
 * Time: 11:46:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleFTPProgressMonitor implements FTPProgressMonitor
{
    private long bytes_transferred = 0L;
    public void bytesTransferred(long count)
    {
        bytes_transferred = count;
    }
    public long getBytesTransfered()
    {
        return bytes_transferred; 
    }

}
