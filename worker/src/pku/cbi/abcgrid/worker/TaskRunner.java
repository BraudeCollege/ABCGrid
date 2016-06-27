package pku.cbi.abcgrid.worker;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;

import pku.cbi.abcgrid.master.Task;
import pku.cbi.abcgrid.master.TaskInput;
import pku.cbi.abcgrid.worker.conf.Config;

/**
 * For each task, a directory tree will be created:
 * <p/>
 * worker/
 * |---job/
 * |---$job_id/
 * |---$task_id/
 * |---input files ...
 * |---output/
 * |---output files ...
 * <p/>
 * where output path is also the working path.
 */
public class TaskRunner implements Callable<Result>
{
    Log logger;
    //File    job_path;
    File input_dir;
    File output_dir;
    byte[] stdout;
    byte[] stderr;
    Task task;
    ProcessManager pm;
    //#################################################################################
    String redirect_output;//the file name for redirected output
    String redirect_error;//the file name of redirected error
    String redirect_output_error;//the file name for redirected error and output
    //#################################################################################
    final String token_executable = "#";
    final String token_database = "@";
    final String token_current_path = "*";
    final String token_redirect_all = ">";
    final String token_redirect_output = "1>";
    final String token_redirect_error = "2>";

    public TaskRunner(Task t)
    {
        task = t;
        logger = LogFactory.getLog(TaskRunner.class);
        pm = ProcessManager.getInstance();
        input_dir = createTempInputDir();
        output_dir = createTempOutputDir();
        stdout = null;
        stderr = null;
    }

    private File createTempInputDir()
    {
        //create a temp dir to hold this job's input files.
        String sj = String.valueOf(task.getJobId());
        String tmpJobDir = FilenameUtils.concat(Config.getJobPath(), sj);
        //job_path = new File(tmpJobDir);
        String st = String.valueOf(task.getTaskId());
        String tmpTaskDir = FilenameUtils.concat(tmpJobDir, st);
        File task_path = new File(tmpTaskDir);

        if (!task_path.exists())
            task_path.mkdirs();
        return task_path;

    }

    private File createTempOutputDir()
    {
        //create a temp dir to hold this job's output files.
        String sj = String.valueOf(task.getJobId());
        String tmpJobDir = FilenameUtils.concat(Config.getJobPath(), sj);
        //File job_path = new File(tmpJobDir);
        String st = String.valueOf(task.getTaskId());
        String tmpTaskDir = FilenameUtils.concat(tmpJobDir, st);
        //File task_path = new File(tmpTaskDir);
        File out_path = new File(tmpTaskDir, "output");

        if (!out_path.exists())
            out_path.mkdirs();

        return out_path;
    }

    private Map<String, File> saveInputFile(Task t) throws IOException
    {
        Map<String, File> input = new HashMap<String, File>();
        List<TaskInput> inputs = t.getInput();
        //save InputStream of Task into local file.
        //A task MAY have no or many inputs.
        for (TaskInput ti : inputs)
        {
            String fname = ti.getName();
            byte[] data = ti.getData();
            File inp = new File(input_dir, fname);
            FileOutputStream fos = new FileOutputStream(inp);
            IOUtils.write(data, fos);
            fos.close();
            input.put(fname, inp);
        }
        return input;
    }

    /**
     * read all output files into a Map.
     *
     * @return Map<file name: file data>
     */
    private Map<String, byte[]> readOutputFile() throws IOException
    {
        Map<String, byte[]> output = new HashMap<String, byte[]>();
        //read redirected output or error files.
        if (redirect_output_error != null)
        {
            //">" is specified in command line.
            //output and error are redirected by using ">output_error_file"
            if (stdout != null)
            {
                output.put(redirect_output_error, stdout);
                stdout = null;
            }
            if (stderr != null)
            {
                output.put(redirect_output_error, stderr);
                stderr = null;
            }
        }
        else
        {
            if (redirect_output != null && stdout != null)//output is redirected by using "1>output_file"
            {
                output.put(redirect_output, stdout);
                stdout = null;
            }
            if (redirect_error != null && stderr != null)//error is redirected by using "2>error_file"
            {
                output.put(redirect_error, stderr);
                stderr = null;
            }
        }
        //read all output files
        File[] files = output_dir.listFiles();
        for (File f : files)
        {
            if (f.isFile())
            {
                byte[] data = FileUtils.readFileToByteArray(f);
                output.put(f.getName(), data);
            }
        }
        return output;
    }

    private List<String> Task2Command(Task t) throws Exception
    {
        Map<String, File> input_files = saveInputFile(t);
        String[] cmds = t.getCommand();
        //the command list
        List<String> local_command = new ArrayList<String>();
        //#blastall -p blastp -d @database -m 9
        String app_path = null;
        for (int i = 0; i < cmds.length; i++)
        {
            String param = cmds[i];
            if (param.startsWith(token_executable))//executable application. e.g. "blastall"
            {
                String pa = param.substring(1, param.length()); //remove the leading "#"
                //get the path to the executable application.
                String pb = Config.getComponentPath(t.getServiceName(), "executable", pa);
                if (pb == null)
                    throw new Exception(String.format("application [%s] is not supported", pa));
                local_command.add(pb);
                app_path = pb;
                //working_path = new File(pb).getParentFile();
            }
            else if (param.startsWith(token_database))//local database. e.g. "NR","Pfam_ls"
            {
                String pa = param.substring(1, param.length()); //remove the leading "@"
                String pb = Config.getComponentPath(t.getServiceName(), "data", pa);
                if (pb == null)
                    throw new Exception(String.format("database [%s] is not supported", pa));
                local_command.add(pb);
            }
            else if (param.startsWith(token_redirect_all))
            {
                //redirect output and error, previous 2> and 1> will be skiped.
                //remove the leading "1>"
                redirect_output_error = param.substring(1).trim();
            }
            else if (param.startsWith(token_redirect_output))
            {
                //remove the leading "1>"
                redirect_output = param.substring(2).trim();
            }
            else if (param.startsWith(token_redirect_error))//redirect error
            {
                //remove the leading "2>"
                redirect_error = param.substring(2).trim();
            }
            else if (param.startsWith(token_current_path))//must be mapped to current path.
            {
                String pa = param.substring(1, param.length()); //remove the leading "*"
                File parentPath = new File(app_path).getParentFile();
                if (!parentPath.exists() || !parentPath.isDirectory())
                {

                }
                else
                {
                    String pb = FilenameUtils.concat(parentPath.getAbsolutePath(), pa);
                    local_command.add(pb);
                }
            }
            else if (input_files.containsKey(param))
            //replace the path of input file. "demo.fasta" ---> "/PATH/TO/LOCAL/demo.fasta"
            {
                String pa = input_files.get(param).getAbsolutePath();
                local_command.add(pa);
            }
            else
            {
                local_command.add(param);
            }
        }
        //System.out.println("COMMAND:"+local_command);
        return local_command;
    }

    private void execute(Task t) throws Exception
    {
        //convert a Task into a local executable command.
        List<String> cmds = Task2Command(t);
        ProcessBuilder pb = new ProcessBuilder(cmds);
        pb.directory(output_dir);
        Process process = pb.start();
        //add the process to a list which could be killed.
        pm.add(t.getJobId(), t.getTaskId(), process);
        byte[] bout = IOUtils.toByteArray(process.getInputStream());
        byte[] berr = IOUtils.toByteArray(process.getErrorStream());
        stdout = bout.length > 0 ? bout : null;
        stderr = berr.length > 0 ? berr : null;
    }

    public Result call()
    {
        try
        {
            execute(task);
        }
        catch (IOException e)
        {
            //the process is killed.
        }
        catch (Exception e)
        {
            long worker_id = Config.getWorkerNodeId();
            MasterProxy.putStderr(task.getJobId(),
                    task.getTaskId(),
                    Util.join(task.getCommand(), " "),
                    e.getMessage().getBytes());
            if (Config.isVerbose())
            {
                logger.error(e.toString());
            }
        }
        Map<String, byte[]> output_streams = null;
        try
        {
            output_streams = readOutputFile();
        }
        catch(IOException e)
        {
            if (Config.isVerbose())
            {
                logger.error(e.toString());
            }
        }
        finally
        {
            try
            {
                //delete I/O files.
                FileUtils.deleteDirectory(input_dir);
            }
            catch (Exception e)
            {

            }
        }
        return new Result(task.getJobId(),
                task.getTaskId(),
                task.getCommand(),
                output_streams,
                stdout, stderr);
    }

}
