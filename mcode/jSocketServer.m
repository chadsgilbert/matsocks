function jSocketServer(port)
%JSOCKETSERVER allows users to issue MATLAB commands remotely over sockets.
%  JSOCKETSERVER listens on a specified port for command-input clients to issue
%  MATLAB commands to this server. It accepts a connection and then evaluates
%  any number of commands from the client, returning the result of each command
%  as a string.
%
%  JSOCKETSERVER(PORT) wehere port is the comm port to listen for connections
%  on, listens for incoming connections from either a jSocketClient, or from
%  MSocketClient.main().
%
%  In order to run this server, the java package, `matsocks.jar` must be on the
%  classpath. Add it by calling:
%    `>> javaaddpath <path-to-jar>`
%
%  This server can be shut down remotely by sending a specialized command:
%  `quit`.
%
%  Example:
%  >> jSocketServer(1235);
%  Running jSocketServer on port 1235
%  Waiting for connection from client
%  localhost:1235~>> 
%
%  The remote user can now evoke commands, which will also be displayed along
%  with their results here.
%
%See also: jSocketClient

import ca.csg.matsocks.MSocketServer;
ip = ['localhost:' num2str(port)];

try
    mSock = MSocketServer(port);
    disp(['jSocketServer listening on port ' num2str(port)]);
    while ~mSock.isConnected()
        mSock.accept(10000);
    end
    
    while 1
        fprintf([ip '~>> ']);
        cmd = mSock.read().toCharArray()';
        
        if strcmp(cmd,'quit')
            mSock.closeServer();
            disp('Closed the MSocketServer');
            break;
            
        elseif strcmp(cmd,'exit')
            mSock.close();
            disp(' ');
            while mSock.isClosed()
                mSock.accept(2000);
            end
            
        else
            disp(cmd);
            try
                rslt = evalc(cmd);
            catch ex
                rslt = ex.message;
            end
            disp(rslt);
            mSock.write(rslt);
        end
    end

catch ex
    disp(' ');
    disp('An error has occurred. Closing the sockets connection.');
    if exist('mSock','var')
        mSock.closeServer();
    end
    rethrow(ex);
end

end