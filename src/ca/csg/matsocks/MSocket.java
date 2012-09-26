package ca.csg.matsocks;

import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;

/** An abstract class to help create MSocketClients and MSocketServers.
 *
 * @author Chad S Gilbert
 * @version 26 Sep 2012
 */
public abstract class MSocket {
	
	/** For communicating with MATLAB. */
	protected Socket cSock;
	
	/** The port to connect on. */
	protected int port;
	
	/** For writing to the socket. */
	protected PrintWriter pw;

	/** For reading from the socket. */
	protected BufferedReader br;
	
	/** Checks if the MSocket object is connected.
	 * Returns <code>false</code> if the socket doesn't yet exist.
	 *
	 * @return logical 1 if connected, 0 otherwise.
	 */
	public boolean isConnected() throws Exception {
		if ( cSock != null ) {
			return cSock.isConnected();
		} else {
			return false;
		}
	}
	
	/** Checks if the MSocketClient is closed. 
	 * Returns <code>false</code> if the socket doesn't yet exist.
	 *
	 * @return logical 1 if the connection has been closed, 0 otherwise.
	 */
	public boolean isClosed() {
		if ( cSock != null ) {
			return cSock.isClosed();
		} else {
			return false;
		}
	}
	
	/** Echos the result of the previous MATLAB command.
	 *
	 * @return the result of the previous command.
	 */
	public String read() throws Exception {
		StringBuilder s = new StringBuilder();
		String c;
		Thread.sleep(50);
		c = br.readLine();
		s.append(c);
		while ( br.ready() ) {
			c = br.readLine();
			s.append("\n" + c);
		}
		
		if ( s.toString().equals("null") ) {
			return "\n";
		} else {
			return s.toString();
		}
	}
	
	/** Writes a command to the server.
	 * Most commands evoke a call in MATLAB and return the result as a string.
	 * However, if the command <code>exit</code> is passed in, the client socket
     * is killed, while leaving the server on and able to accept a new 
	 * connection from another client. <code>quit</code> kills the client and 
	 * the server.
	 *
	 * @param cmd the MATLAB command to execut on the server.
     * @throws Exception if connection to the server fails	 
	 */
	public void write(String cmd) throws Exception {
		pw.println(cmd);
	}
	
	/** Closes the client socket and its reader/writer connections.
	 *
	 * @throws Exception if an IO error occurs.
	 */
	public void close() throws Exception {
		if ( cSock != null ) {
			cSock.close();
			br.close();
			pw.close();
		}
	}
	
	/** Returns a representation of the object as a string.
	 * 
	 * @return the object as a string
	 */
	public String toString() {
		if ( cSock== null ) {
			return "  cSock: not connected\n";
		} else if ( !cSock.isConnected() ) {
			return "  cSock: not connected\n";
		} else if ( !cSock.isClosed() ) {
			return "  cSock: connected on " + cSock.getLocalSocketAddress() + "\n";
		} else {
			return "  cSock :closed\n";
		}
	}
}