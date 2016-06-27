package pku.cbi.abcgrid.user;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */
import java.net.InetAddress;
import java.net.Socket;
import java.io.*;
/**
 * user client
 * */
public class UserMain
{

    private Socket smtpSocket = null;
    private PrintWriter writer = null;
    private BufferedReader reader = null;
    public void connectMaster(InetAddress address, int port)
            throws IOException
    {
        smtpSocket = new Socket(address, port);
        writer = new PrintWriter(smtpSocket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
    }

    public void connectMaster(String address, int port)
            throws IOException
    {
        smtpSocket = new Socket(address, port);
        writer = new PrintWriter(smtpSocket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
    }

    public void sendCommand(String user, String password, String cmd)
    {
        writer.write(String.format("%s:%s\n", user, password));
        writer.write(cmd+ "\n");
        writer.write("//" + "\n");//end of the command
        writer.flush();
    }

    public String recvResponse() throws IOException
    {
        StringBuffer buffer = new StringBuffer();
        String responseLine;
        while ((responseLine = reader.readLine()) != null)
        {
            buffer.append(responseLine);
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public void close()
    {
        try
        {
            writer.close();
            reader.close();
            smtpSocket.close();
        }
        catch(Exception e)
        {
        }
    }

    public static void main(String[] args)
    {
        UserMain user = new UserMain();
        Config config = new Config();
        try
        {
            //read configuration from user.conf
            config.init();
            user.connectMaster(config.getMasterAddress(), config.getMasterPort());
            if(args.length==0)
            {
                usage();
            }
            else
            {
                user.sendCommand(config.getUser(), config.getPassword(), Util.join(args, " "));
                System.out.println(user.recvResponse());
            }
        }
        catch (IOException e)
        {
            System.err.println("Error: "+e);
        }
        finally
        {
            user.close();
        }
    }

    private static void usage()
    {
        StringBuffer u = new StringBuffer();
        u.append("Usage:\n\n");
        u.append("java -jar user.jar [COMMAND]\n\n");
        u.append("---------------------------------------------------\n");
        u.append(runUsage());
        u.append("---------------------------------------------------\n");
        u.append(jobUsage());
        u.append("---------------------------------------------------\n");
        u.append(serviceUsage());
        u.append("---------------------------------------------------\n");
        u.append(killUsage());
        u.append("---------------------------------------------------\n");
        u.append(workerUsage());

        System.out.println(u.toString());
    }

    private static String jobUsage()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("COMMAND: j [job_id]\n");
        sb.append("DESCRIPTION: query a job\n");
        sb.append("EXAMPLE:\n>java -jar user.jar j 12\n");
        sb.append("#query the status of job with id=12.\n");
        return sb.toString();
    }

    private static String killUsage()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("COMMAND: k [job_id]\n");
        sb.append("DESCRIPTION: kill a job\n");
        sb.append("EXAMPLE:\n>java -jar user.jar k 12\n");
        sb.append("#kill job with id=12.\n");
        return sb.toString();
    }

    private static String runUsage()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("COMMAND: r [params...]\n");
        sb.append("DESCRIPTION: run a job\n");
        sb.append("EXAMPLE:\n>java -jar user.jar r blastall -p blastp -i pdb_10.fasta -d nr -o pdb_10.blastp\n");
        sb.append("#run a blastp search again nr database.\n");
        return sb.toString();
    }

    private static String serviceUsage()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("COMMAND: s\n");
        sb.append("DESCRIPTION: show all supported services\n");
        sb.append("EXAMPLE:\n>java -jar user.jar s \n");
        return sb.toString();
    }

    private static String workerUsage()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("COMMAND: w\n");
        sb.append("DESCRIPTION: show all connected workers\n");
        sb.append("EXAMPLE:\n>java -jar user.jar w \n");
        return sb.toString();
    }


}
