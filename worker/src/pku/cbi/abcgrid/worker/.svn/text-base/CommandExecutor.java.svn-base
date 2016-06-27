package pku.cbi.abcgrid.worker;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */


import java.io.*;
import java.util.List;
import java.util.ArrayList;
public class CommandExecutor
{
    /**
     * Execute this program, waiting for the program to return.  Standard input,
     * output, and error are accessed in separate threads.  The return value of
     * the program is returned.
     *
     * @param cmd List of command parameters
     * @param workDir Working directory of process
     * @return Exit value returned by process
     */
    public int exec(String cmd,StringBuffer stdout, StringBuffer stderr,File workDir)
            throws IOException, InterruptedException
    {
        List<String> cmds = new ArrayList<String>();
        cmds.add(cmd);
        return exec(cmds,stdout,stderr,workDir);
    }
    /**
     * Execute this program, waiting for the program to return.  Standard input,
     * output, and error are accessed in separate threads.  The return value of
     * the program is returned.
     *
     * @param cmds List of command parameters
     * @param stdout Standard output of process
     * @param stderr Error output of process
     * @param workDir Working directory of process
     * @return Exit value returned by process
     * @throws IOException, InterruptedException
     */
    public int exec(List<String> cmds,OutputStream stdout,OutputStream stderr,File workDir)
            throws IOException, InterruptedException
    {
        ProcessBuilder pb = new ProcessBuilder(cmds);
        if(workDir==null)
        {
            File appdir = new File(cmds.get(0));
            pb.directory(appdir.getParentFile());
        }
        else
        {
            pb.directory(workDir);
        }
        Process p = pb.start();
        p.getOutputStream().close();
        new IOThread(p.getInputStream(), stdout, false).start();
        new IOThread(p.getErrorStream(), stderr, false).start();
        p.waitFor();
        return p.exitValue();
    }
    public int exec(List<String> cmds,File workDir)
            throws IOException, InterruptedException
    {
        ProcessBuilder pb = new ProcessBuilder(cmds);
        if(workDir==null)
        {
            File appdir = new File(cmds.get(0));
            pb.directory(appdir.getParentFile());
        }
        else
        {
            pb.directory(workDir);
        }
        Process p = pb.start();
        //p.getOutputStream().close();
        //new IOThread(p.getInputStream(), stdout, false).start();
        //new IOThread(p.getErrorStream(), stderr, false).start();
        p.waitFor();
        return p.exitValue();
    }
    public int exec(List<String> cmds,StringBuffer stdout, StringBuffer stderr,File workdir)
            throws IOException, InterruptedException
    {
        ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream();
        ByteArrayOutputStream stderrStream = new ByteArrayOutputStream();
        int retValue = exec(cmds,stdoutStream, stderrStream,workdir);

        stdoutStream.close();
        stdout.append(new String(stdoutStream.toByteArray()));

        stderrStream.close();
        stderr.append(new String(stderrStream.toByteArray()));
        return retValue;
    }
    public int exec(List<String> cmds,InputStream stdin, OutputStream stdout,
                    OutputStream stderr,File workDir)
            throws IOException, InterruptedException
    {
        ProcessBuilder pb = new ProcessBuilder(cmds);
        if(workDir==null)
        {
            File appdir = new File(cmds.get(0));
            pb.directory(appdir.getParentFile());
        }
        else
        {
            pb.directory(workDir);
        }
        Process p = pb.start();
        // the data that will used as input to this program.
        if (stdin == null)
        {
            p.getOutputStream().close();
        }
        else
        {
            // create a thread to fill input data into this program.
            new IOThread(stdin, p.getOutputStream(), true).start();
        }
        new IOThread(p.getInputStream(), stdout, false).start();
        new IOThread(p.getErrorStream(), stderr, false).start();
        p.waitFor();
        return p.exitValue();
    }
    /**
     * Execute this program, waiting for the program to return.  Standard input,
     * output, and error are accessed in separate threads.  The return value of
     * the program is returned.
     */
    public int exec(List<String> cmds,String stdin, StringBuffer stdout,
                    StringBuffer stderr,File workDir)
            throws IOException, InterruptedException
    {
        ByteArrayInputStream stdinStream = null;
        ByteArrayOutputStream stdoutStream = null;
        ByteArrayOutputStream stderrStream = null;
        if (stdin != null) {
            stdinStream = new ByteArrayInputStream(stdin.getBytes());
        }
        if (stdout != null) {
            stdoutStream = new ByteArrayOutputStream();
        }
        if (stderr != null) {
            stderrStream = new ByteArrayOutputStream();
        }
        int retValue = exec(cmds,stdinStream, stdoutStream, stderrStream,workDir);
        if (stdinStream != null) {
            stdinStream.close();
        }
        if (stdoutStream != null) {
            stdoutStream.flush();
            stdoutStream.close();
            stdout.append(new String(stdoutStream.toByteArray()));
        }
        if (stderrStream != null) {
            stderrStream.flush();
            stderrStream.close();
            stderr.append(new String(stderrStream.toByteArray()));
        }
        return retValue;
    }

    public void test_no_output()
    {
        String executable = "f:\\ABCGrid2\\worker\\service\\NCBI_BLAST\\executable\\bin\\blastall";
        String database = "f:\\ABCGrid2\\worker\\service\\NCBI_BLAST\\data\\pataa";
        String finput = "f:\\ABCGrid2\\worker\\job\\1\\1_pdb_10.fasta";
        File working_dir = new File("f:\\ABCGrid2\\worker\\job\\1\\output");
        String foutput = "abc.blastp";
        List<String> cmds = new ArrayList<String>();
        cmds.add(executable);
        /*
        cmds.add("-p");
        cmds.add("blastp");
        cmds.add("-d");
        cmds.add(database);
        cmds.add("-i");
        cmds.add(finput);
        cmds.add("-o");
        cmds.add(foutput);
        cmds.add("-m");
        cmds.add("9");
        cmds.add("-T");
        cmds.add("T");
        cmds.add("-O");
        cmds.add("abc.align");
        */
        try
        {
            StringBuffer output = new StringBuffer();
            StringBuffer error = new StringBuffer();
            int val = exec(cmds,output,error,working_dir);
            System.out.println("RET:"+val);
            System.out.println(output.toString());
            System.out.println(error.toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
    public void test_hmmpfam()
    {
        String executable = "/rd1/user/suny/worker/dist/service/HMMER/executable/bin/hmmpfam";
        String database = "/rd1/user/suny/worker/dist/service/HMMER/data/Pfam_ls";
        String finput = "/rd1/user/suny/worker/dist/job/1/1/pdb_10.fasta";
        File working_dir = new File("/rd1/user/suny/worker/dist/bin");
        List<String> cmds = new ArrayList<String>();
        cmds.add(executable);
        cmds.add(database);
        cmds.add(finput);
        //cmds.add(">");
        //cmds.add("hello.dat");
        try
        {
            StringBuffer output = new StringBuffer();
            StringBuffer error = new StringBuffer();
            int val = exec(cmds,output,error,working_dir);
            System.out.println("RET:"+val);
            System.out.println(output.toString());
            System.out.println(error.toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
    public void test_redirect()
    {
        List<String> cmds = new ArrayList<String>();
        cmds.add("ls");
        cmds.add(">");
        cmds.add("hello.dat");
        File workdir = new File("/rd1/user/suny/");
        try
        {

            System.out.println("CMD:"+cmds);
            int val = exec(cmds,workdir);
            System.out.println("RET:"+val);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
    public static void main(String[] args)
    {
        CommandExecutor ce = new CommandExecutor();
        //ce.test_hmmpfam();
        ce.test_redirect();
    }
}
