package ca.csg.matsocks;

import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.ServerSocket;
import ca.csg.matsocks.MSocket;

/** A class for accepting and handling remote connections into MATLAB.
 *
 * @author Chad S Gilbert
 * @version 26 Sep 2012
 */
public class MSocketServer extends MSocket {
	
	/** The server socket. */
	protected ServerSocket sSock;
	
	/** Default constructor for the MSocketServer class. */
	public MSocketServer() { }
	
	/** Creates the MSocketServer object.
	 *
	 * @param port the port number to listen on
	 * @throws Exception if initialization fails. Probabl cause: ServerSocket 
	 * cannot be opened.
	 */
	public MSocketServer(int port) throws Exception {
		initialize(port);
	}
	
	/** Initializes the ServerSocket.
	 *
	 * @param port the port number to listen on
	 * @throws Exception if initalization fails. Probable cause: ServerSocket
	 * cannot be opened.
	 */
	public void initialize(int port) throws Exception {
		this.port = port;
		this.sSock = new ServerSocket(port);
	}
	
	/** Accepts incoming connection request from client.
	 *
	 * The intended pattern in MATLAB for this method is: 
	 * <pre>
	 * >> while ~mSock.isConnected()
	 * >>   mSock.accept(waittime);
	 * >> end </pre>
	 * This allows the process to be interrupted from MATLAB, whereas there is
	 * no nice way to interrupt a java thread from MATLAB.
	 *
	 * @param timeout the amount of time to wait for a connection before
	 * returning control to the calling process.
	 * @throws Exception if <code>cSoc</code> is invalid, or if the connection 
	 * fails unexpectedly.
	 */
	public void accept(int timeout) throws Exception {
		sSock.setSoTimeout(timeout);
		try {
			cSock = sSock.accept();
			pw = new PrintWriter(cSock.getOutputStream(), true);
			br = new BufferedReader(new InputStreamReader(cSock.getInputStream()));
		} catch (Exception ex) {
			System.out.println("Waiting for connection from client");
		}
	}
	
	/** Returns the object as a string.
	 * 
	 * @return string representation of the object
	 */
	public String toString() {
		if ( !sSock.isClosed() ) {
			return super.toString() + "  sSock: listening on localhost:" + port + "\n";
		} else {
			return super.toString() + "  sSock: no longer listening.\n ";
		}
	}
	
	/** Closes the MATLAB socket server. */
	public void closeServer() throws Exception {
		if ( cSock == null ) {
		} else if ( !super.isClosed() ) {
			super.close();
		}
		if ( !sSock.isClosed() ) {
			sSock.close();
		}
	}
}