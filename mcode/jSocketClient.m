function jSocketClient(addr, port)
%JSOCKETCLIENT connect to a remote MATLAB instane and issue commands to it.
%  JSOCKETCLIENT allows a user to connect to a remote MATLAB instance and issue
%  commands to it over a sockets connection using the Java-based "matsocks" 
%  library. The remote MATLAB instance must be running the JSOCKETSERVER 
%  process. If this is your first time using JSOCKETCLIENT, make sure the
%  matsocks Java library is on your classpath by calling:
%    `>> javaaddpath <path-to-matsocks.jar>`
%
%  JSOCKETCLIENT(ADDR, PORT) where ADDR is a string representation locating the
%  machine, either the machine name (e.g. "localhost" or "Dolores") or its ip
%  address (e.g. "127.0.0.1" or "192.168.1.13") and PORT is the port number to 
%  connect to (by default, 1235), connects to the jSocketServer, if it exists.
%
%  Once connected, the terminal will display a different prompt:
%    `ADDR:PORT >> `
%  where ADDR and PORT are the address and port that this MATLAB instance is
%  now connected on. The prompt will go back to the normal:
%    `>> `
%  if this instance is disconnected.
%
%  Example:
%  >> !ping 192.168.1.13
%  Pinging 192.168..1.13 with 32 bytes of data:
%  Reply from 192.168.1.13: bytes=32 time <1ms TTl=64
%  >> jSocketClient('192.168.1.13', 1235)
%  192.168.1.13:1235 >> 
%  Give you the prompt on the remote machine.
%
%  >> !ping Sally
%  Ping request could not find host Sally. Please check the name and try again.
%  >> jSocketClient('Sally', 2020)
%  Error using jSocketClient (line 53)
%  Java exception occurred:
%  java.net.UnknownHostException: Sally
%
%See also: jSocketServer

import ca.csg.matsocks.MSocketClient;

try
    mSock = MSocketClient(addr, port);
    connid = [addr ':' num2str(port)];    
    while ~mSock.isClosed()
        cmd = input([connid ' >> '],'s');
        disp(mSock.exec(cmd));
    end
catch ex
    if exist('mSock', 'var')
        if ~mSock.isClosed()
            mSock.exec('stopClient');
            mSock.close();
        end
    end
    throw(ex);
end

end