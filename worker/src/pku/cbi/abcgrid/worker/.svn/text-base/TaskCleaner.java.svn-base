package pku.cbi.abcgrid.worker;

import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import pku.cbi.abcgrid.worker.conf.Config;

public class TaskCleaner
{
    private final Timer timer;
    private final int minutes;

    public TaskCleaner(int m)
    {
        timer = new Timer();
        minutes = m;
    }

    public void start()
    {
        timer.scheduleAtFixedRate(new DeleteTempJobDir(), minutes * 60 * 1000, minutes * 10 * 1000);
    }

    class DeleteTempJobDir extends TimerTask
    {
        private File job_dir;

        public DeleteTempJobDir()
        {
            job_dir = new File(Config.getJobPath());
        }

        public void run()
        {
            //delete files older than one hour
            deleteDir();
        }

        private void deleteDir()
        {
            long cutoff = System.currentTimeMillis() - (minutes * 60 * 1000);
            try
            {
                String[] files = job_dir.list(new AgeFileFilter(cutoff));
                for (String fs : files)
                {
                    File fd = new File(job_dir, fs);
                    String[] il = fd.list();
                    if (il.length == 0)//empty dir
                    {
                        FileUtils.forceDelete(fd);
                    }
                }

            }
            catch (Exception e)
            {

            }

        }
    }
}
