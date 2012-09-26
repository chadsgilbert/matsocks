package ca.csg.matsocks;

import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;
import ca.csg.matsocks.MSocket;

/** A class for communicating with a MATLAB process over a sockets connection. 
 *
 * @author Chad S Gilbert
 * @version 26 Sep 2012
 */
public class MSocketClient extends MSocket {
	
	/** The IP address of the socket server. */
	protected String addr;
	
	/** Default constructor for MSocketClient. */
	public MSocketClient() { }
	
	/** Creates the MSocketClient object.
	 * 
	 * @param addr the ip address of the server to connect to
	 * @param port the port number to connect on
	 * @throws Exception if the Host in not found, or if the socket cannot be 
	 * created or closed
	 */
	public MSocketClient(String addr, int port) throws Exception {
		initialize(addr, port);
	}
	
	/** Initializes the MSocketClient.
	 *
	 * @param addr the ip address of the MATLAB socket server
	 * @param port the port number to connect on
	 */
	public void initialize(String addr, int port) throws Exception {
		this.addr = addr;
		this.port = port;
		this.cSock = new Socket(addr, port);
		this.pw = new PrintWriter(cSock.getOutputStream(), true);
		InputStreamReader isr = new InputStreamReader(cSock.getInputStream());
		this.br = new BufferedReader(isr);
	}
	
	/** Executes a MATLAB command in MATLAB over the sockets interface.
	 * The command is intended to behave exactly the same with as it would in 
	 * the MATLAB window; results are echoed to the screen if the command isn't 
	 * followed by a semi-colon ";".
	 *
	 * One known difference in behaviour is that plotting commands (e.g. "plot")
	 * do not immediately draw. This can be solved by calling "drawnow"
	 * immediately after the plot command. E.g.
	 * <pre>
	 * >> plot([1 2 3], [4 5 6]); drawnow; </pre>
	 * will draw the plot.
	 *
	 * @param cmd a command to be executed in MATLAB.
	 */
	public String exec(String cmd) throws Exception {
		this.write(cmd);
		String rslt = this.read();
		if ( cmd.equals("exit") || cmd.equals("quit") )	{
			this.close();
		}
		return rslt;
	}
	
	/** Runs the MATLAB client in a terminal window.
	 * To run this client, start the program with:
	 * <pre>$ java -jar matsocks.jar hostname port </pre>
	 * where <code>hostname</code> is the host name of the machine where the 
	 * jSocketServer script is running and <code>port</code> is the port it is
	 * listening on.
	 *
	 * This program presents the used with a MATLAB-like prompt, e.g.:
	 * <pre> 127.0.0.1:1235 >> </pre>
	 * where any MATLAB command can be entered. That command will be evaluated
	 * in the remote MATLAB instance and its result (if any) echoed back to the 
	 * terminal. E.g.
	 * <pre> 127.0.0.1:1235 >> whos
	 *     Name       Size            Bytes  Class      Attributes
	 *
	 *     addr       1x9                18  char                                       
	 *     cmd        1x4                 8  char </pre>
     *
     * There are two special commands: <code>quit</code> and <code>exit</code>.
	 * <code>exit</exit> quits the client program while leaving the server up, 
	 * and <code>quit</code> closes both the client and the server.
	 * 
	 * @param args the ip address of the server and the port number
	 * @throws Exception if an IO or networking error occurs.
	 */
	public static void main(String[] args) throws Exception {
	
		// Variables.
		MSocketClient mSock;// The MSocketClient for sending MATLAB commands.
		BufferedReader cin;	// The command input stream.
		String cmd = "";	// The next command to execute.
		String addr;		// The IP address of the socket server.
		int port;			// The port.

		// Get the connection address as an input argument.
		switch ( args.length ) {
			case 0: 
				addr = "127.0.0.1";
				port = 1235;
				break;
			case 1:
				addr = args[0];
				port = 1235;
				break;
			case 2:
				addr = args[0];
				port = Integer.parseInt(args[1]);
				break;
			default:
				throw(new Exception("Too many input args."));
		}
		
		// Get the streams.
		cin = new BufferedReader(new InputStreamReader(System.in));
		mSock = new MSocketClient(addr, port);
		
		// Read and send input to MATLAB until an "exit" is sent.
		System.out.print(addr + ":" + port + " >> ");
		do {
			cmd = cin.readLine();
			System.out.println(mSock.exec(cmd));
			System.out.print(addr + ":" + port + " >> ");
		} while ( !mSock.isClosed() );
	}
}