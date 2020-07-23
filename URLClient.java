// Copyright Eric Chauvin 2020.



import java.net.URLConnection;
import java.net.URL;
// import java.io.BufferedReader;
// import java.io.InputStreamReader;
import java.io.BufferedInputStream;
// BufferedOutputStream 



public class URLClient implements Runnable
  {
  private MainApp mApp;
  private String fileName = "";
  private String URLToGet = "";




  private URLClient()
    {
    }



  public URLClient( MainApp useApp, String fileToUse,
                                String URLToUse )
    {
    mApp = useApp;
    fileName = fileToUse;
    URLToGet = URLToUse;    
    }



  @Override
  public void run()
    {
    getURLToFile();
    }



  private void getURLToFile()
    {
    try
    {
    mApp.showStatusAsync( "\n\nGetting: " + URLToGet );

    URL url = new URL( URLToGet );

    // Calling openConnection() in the URL object
    // Creates the specific type of URLConnection.
    // Like HttpURLConnection.
    URLConnection uConnect = url.openConnection();
    BufferedInputStream inStream = new
                          BufferedInputStream(
                          uConnect.getInputStream());

    ByteBld byteBld = new ByteBld( 2 ); // 1024 * 64
 
    String contentType = ""; 

    try
    {
    // mApp.showStatusAsync( "getContentEncoding: " +
    //           uConnect.getContentEncoding() );

    // mApp.showStatusAsync( "getContentLength: " +
    //           uConnect.getContentLength() );

    // getContentType: text/html; charset=utf-8

    contentType = uConnect.getContentType(); 
    mApp.showStatusAsync( "\n\nContentType: " +
                                     contentType );

    // mApp.showStatusAsync( "getDate: " +
    //           uConnect.getDate() );

    // mApp.showStatusAsync( "getExpiration: " +
    //           uConnect.getExpiration() );

    // mApp.showStatusAsync( "getLastModified: " +
    //           uConnect.getLastModified() );

    for( int count = 0; count < 1000000; count++ )
      {
      // the next byte of data, or -1 if the end of
      // the stream is reached.
      int b = inStream.read();
      if( b == -1 )
        break;
     
// Yes this works.
      // if( b > 127 )
        // mApp.showStatusAsync( "Byte > 127: " + b );

      // Convert to byte?
      byteBld.appendByte( (byte)b );

      // out.write(c);

      }
    }
    finally
      {
      if( inStream != null )
        inStream.close();

      }

    byte[] bytesBuf = byteBld.toByteArray();
    if( bytesBuf == null )
      return;

    StrA showS = new StrA( URLToGet + " "  + contentType );
    StrA strToWrite = UTF8StrA.bytesToStrA( mApp,
                                       bytesBuf,
                                       1000000000,
                                       showS );

    StrA fileToWrite = new StrA( fileName );
    FileUtility.writeStrAToFile( mApp,
                                 fileToWrite,
                                 strToWrite,
                                 false,
                                 false );

    }
    catch( Exception e )
      {
      mApp.showStatusAsync( "Exception in URLClient.getURLToFile()." );
      mApp.showStatusAsync( e.getMessage() );
      }
    }



/*
  private void getURLToFile()
    {
    try
    {
    mApp.showStatusAsync( "\n\nGetting: " + URLToGet );

    URL url = new URL( URLToGet );

    // Calling openConnection() in the URL object
    // Creates the specific type of URLConnection.
    // Like HttpURLConnection.
    URLConnection uConnect = url.openConnection();
    InputStreamReader inStream = new
                 InputStreamReader(
                          uConnect.getInputStream());
 
    BufferedReader in = new BufferedReader( inStream );

    // Always null.
    // mApp.showStatusAsync( "getContentEncoding: " +
    //           uConnect.getContentEncoding() );

    // mApp.showStatusAsync( "getContentLength: " +
    //           uConnect.getContentLength() );

    // getContentType: text/html; charset=utf-8

    String contentType = uConnect.getContentType(); 
    mApp.showStatusAsync( "\n\nContentType: " +
                                     contentType );

    // mApp.showStatusAsync( "getDate: " +
    //           uConnect.getDate() );

    // mApp.showStatusAsync( "getExpiration: " +
    //           uConnect.getExpiration() );

    // mApp.showStatusAsync( "getLastModified: " +
    //           uConnect.getLastModified() );


    StringBuilder sBuilder = new StringBuilder();
    for( int count = 0; count < 1000000; count++ )
      {
      String line = in.readLine();
      if( line == null )
        break;

      if( line.length() == 0 )
        continue;
 
      // Things like two-slash comments in JavaScript
      // need to have the linefeed character to
      // end the line.
      sBuilder.append( line ); // + "\n" );
      // mApp.showStatusAsync( "Got line: " + line );
      }

    in.close();

    // Charset iso 8859-1
    // Windows-1252.

    // SocketTimeoutException

    StrA StrToWrite = new StrA( sBuilder.toString());
    StrA StrToWriteKeep = StrToWrite;
    byte[] bytesBuf = strAToBytes( StrToWrite );
    StrA showS = new StrA( URLToGet + " "  + contentType );
    StrToWrite = UTF8StrA.bytesToStrA( mApp,
                                       bytesBuf,
                                       1000000000,
                                       showS );

    StrA fileToWrite = new StrA( fileName );

    // mApp.showStatusAsync( "File StrA: " + StrToWrite );

    FileUtility.writeStrAToFile( mApp,
                                 fileToWrite,
                                 StrToWrite,
                                 false,
                                 false );

    }
    catch( Exception e )
      {
      mApp.showStatusAsync( "Exception in URLClient.getURLToFile()." );
      mApp.showStatusAsync( e.getMessage() );
      }
    }
*/


/*
  private byte[] strAToBytes( StrA in )
    {
    final int last = in.length();
    byte[] result = new byte[last];
    for( int count = 0; count < last; count++ )
      result[count] = (byte)(in.charAt( count ));

    return result;
    }
*/


/*
Below this is notes from the Java tutorials:


/*
// HTTP POST:

import java.io.*;
import java.net.*;

                  URLEncoder.encode(args[1], "UTF-8");

    URLConnection connection = url.openConnection();
    connection.setDoOutput(true);

    OutputStreamWriter out = new OutputStreamWriter(
                        connection.getOutputStream());
    out.write("string=" + stringToReverse);
    out.close();


URL myURL = new URL("http://example.com/");


URL url = new URL("http://example.com/hello%20world");


import javax.net.ssl.SSLSocket;


// implements runnable.
// java.net.URLConnection


// java.net.Socket



JSSE (Java Secure Socket Extension) API.
Java Cryptography Architecture (JCA)
Java Cryptographic Extension (JCE)
SSLSocketFactory
javax.net.ssl.SSLSocketFactory
getDefault()
Socket createSocket(String host, int port)
Socket createSocket(String host, int port, InetAddress clientHost, int clientPort)
Socket createSocket(InetAddress host, int port)
Socket createSocket(InetAddress host, int port, InetAddress clientHost, int clientPort)
Socket createSocket(Socket socket, String host, int port, boolean autoClose)
javax.net.ssl.SSLContext
SSLSocketFactory
SSLSocket
SSLServerSocketFactory
SSLServerSocket
createServerSocket

String host = getHost(...);
Integer port = getPort(...);
SSLSocketFactory sslsocketfactory = SSLSocketFactory.getDefault();
SSLSocket sslsocket = (SSLSocket) sslsocketfactory
  .createSocket(host, port);
InputStream in = sslsocket.getInputStream();
OutputStream out = sslsocket.getOutputStream();
 
out.write(1);
while (in.available() > 0) {
    System.out.print(in.read());
}
 
javax.net.ssl.SSLHandshakeException
sun.security.validator.ValidatorException
sun.security.provider.certpath.SunCertPathBuilderException
The truststore is the file containing trusted 
certificates that Java uses to validate secured
connections.

javax.net.ssl.trustStore

Does Java use OpenSSL?


import java.net.*;
import java.io.*;
import javax.net.ssl.*;


public class SSLSocketClient {

    public static void main(String[] args) throws Exception {
        try {
            SSLSocketFactory factory =
                (SSLSocketFactory)SSLSocketFactory.getDefault();
            SSLSocket socket =
                (SSLSocket)factory.createSocket("www.verisign.com", 443);

            socket.startHandshake();

            PrintWriter out = new PrintWriter(
                                  new BufferedWriter(
                                  new OutputStreamWriter(
                                  socket.getOutputStream())));

            out.println("GET / HTTP/1.0");
            out.println();
            out.flush();


            if (out.checkError())
                System.out.println(
                    "SSLSocketClient:  java.io.PrintWriter error");


            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                    socket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);

            in.close();
            out.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


SSL port 443



import javax.security.cert.X509Certificate;
import java.security.KeyStore;





public class SSLSocketClientWithClientAuth {

    public static void main(String[] args) throws Exception {
        String host = null;
        int port = -1;
        String path = null;
        for (int i = 0; i < args.length; i++)
            System.out.println(args[i]);

        if (args.length < 3) {
            System.out.println(
                "USAGE: java SSLSocketClientWithClientAuth " +
                "host port requestedfilepath");
            System.exit(-1);
        }

        try {
            host = args[0];
            port = Integer.parseInt(args[1]);
            path = args[2];
        } catch (IllegalArgumentException e) {
             System.out.println("USAGE: java SSLSocketClientWithClientAuth " +
                 "host port requestedfilepath");
             System.exit(-1);
        }

        try {

  
             * Set up a key manager for client authentication
             * if asked by the server.  Use the implementation's
             * default TrustStore and secureRandom routines.
  
            SSLSocketFactory factory = null;
            try {
                SSLContext ctx;
                KeyManagerFactory kmf;
                KeyStore ks;
                char[] passphrase = "passphrase".toCharArray();

                ctx = SSLContext.getInstance("TLS");
                kmf = KeyManagerFactory.getInstance("SunX509");
                ks = KeyStore.getInstance("JKS");

                ks.load(new FileInputStream("testkeys"), passphrase);

                kmf.init(ks, passphrase);
                ctx.init(kmf.getKeyManagers(), null, null);

                factory = ctx.getSocketFactory();
            } catch (Exception e) {
                throw new IOException(e.getMessage());
            }

            SSLSocket socket = (SSLSocket)factory.createSocket(host, port);

            *
             * send http request
             *
             * See SSLSocketClient.java for more information about why
             * there is a forced handshake here when using PrintWriters.
             *
            socket.startHandshake();

            PrintWriter out = new PrintWriter(
                                  new BufferedWriter(
                                  new OutputStreamWriter(
                                  socket.getOutputStream())));
            out.println("GET " + path + " HTTP/1.0");
            out.println();
            out.flush();

            *
             * Make sure there were no surprises
             *
            if (out.checkError())
                System.out.println(
                    "SSLSocketClient: java.io.PrintWriter error");

             
            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                    socket.getInputStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);

            in.close();
            out.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


https://localhost:port/file 


ClassFileServer.java

import java.io.*;
import java.net.*;
import java.security.KeyStore;
import javax.net.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;


public class ClassFileServer extends ClassServer {

    private String docroot;

    private static int DefaultServerPort = 2001;


    public ClassFileServer(ServerSocket ss, String docroot) throws IOException
    {
        super(ss);
        this.docroot = docroot;
    }


    public byte[] getBytes(String path)
        throws IOException
    {
        System.out.println("reading: " + path);
        File f = new File(docroot + File.separator + path);
        int length = (int)(f.length());
        if (length == 0) {
            throw new IOException("File length is zero: " + path);
        } else {
            FileInputStream fin = new FileInputStream(f);
            DataInputStream in = new DataInputStream(fin);

            byte[] bytecodes = new byte[length];
            in.readFully(bytecodes);
            return bytecodes;
        }
    }

    public static void main(String args[])
    {
        System.out.println(
            "USAGE: java ClassFileServer port docroot [TLS [true]]");
        System.out.println("");
        System.out.println(
            "If the third argument is TLS, it will start as\n" +
            "a TLS/SSL file server, otherwise, it will be\n" +
            "an ordinary file server. \n" +
            "If the fourth argument is true,it will require\n" +
            "client authentication as well.");

        int port = DefaultServerPort;
        String docroot = "";

        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }

        if (args.length >= 2) {
            docroot = args[1];
        }
        String type = "PlainSocket";
        if (args.length >= 3) {
            type = args[2];
        }
        try {
            ServerSocketFactory ssf =
                ClassFileServer.getServerSocketFactory(type);
            ServerSocket ss = ssf.createServerSocket(port);
            if (args.length >= 4 && args[3].equals("true")) {
                ((SSLServerSocket)ss).setNeedClientAuth(true);
            }
            new ClassFileServer(ss, docroot);
        } catch (IOException e) {
            System.out.println("Unable to start ClassServer: " +
                               e.getMessage());
            e.printStackTrace();
        }
    }

    private static ServerSocketFactory getServerSocketFactory(String type) {
        if (type.equals("TLS")) {
            SSLServerSocketFactory ssf = null;
            try {
                // set up key manager to do server authentication
                SSLContext ctx;
                KeyManagerFactory kmf;
                KeyStore ks;
                char[] passphrase = "passphrase".toCharArray();

                ctx = SSLContext.getInstance("TLS");
                kmf = KeyManagerFactory.getInstance("SunX509");
                ks = KeyStore.getInstance("JKS");

                ks.load(new FileInputStream("testkeys"), passphrase);
                kmf.init(ks, passphrase);
                ctx.init(kmf.getKeyManagers(), null, null);

                ssf = ctx.getServerSocketFactory();
                return ssf;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return ServerSocketFactory.getDefault();
        }
        return null;
    }
}



ClassServer.java

import java.io.*;
import java.net.*;
import javax.net.*;



public abstract class ClassServer implements Runnable {

    private ServerSocket server = null;

    protected ClassServer(ServerSocket ss)
    {
        server = ss;
        newListener();
    }

    public abstract byte[] getBytes(String path)
        throws IOException, FileNotFoundException;

    public void run()
    {
        Socket socket;

        // accept a connection
        try {
            socket = server.accept();
        } catch (IOException e) {
            System.out.println("Class Server died: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // create a new thread to accept the next connection
        newListener();

        try {
            OutputStream rawOut = socket.getOutputStream();

            PrintWriter out = new PrintWriter(
                                new BufferedWriter(
                                new OutputStreamWriter(
                                rawOut)));
            try {
                // get path to class file from header
                BufferedReader in =
                    new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                String path = getPath(in);
                // retrieve bytecodes
                byte[] bytecodes = getBytes(path);
                // send bytecodes in response (assumes HTTP/1.0 or later)
                try {
                    out.print("HTTP/1.0 200 OK\r\n");
                    out.print("Content-Length: " + bytecodes.length +
                                   "\r\n");
                    out.print("Content-Type: text/html\r\n\r\n");
                    out.flush();
                    rawOut.write(bytecodes);
                    rawOut.flush();
                } catch (IOException ie) {
                    ie.printStackTrace();
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
                // write out error response
                out.println("HTTP/1.0 400 " + e.getMessage() + "\r\n");
                out.println("Content-Type: text/html\r\n\r\n");
                out.flush();
            }

        } catch (IOException ex) {
            // eat exception (could log error to log file, but
            // write out to stdout for now).
            System.out.println("error writing response: " + ex.getMessage());
            ex.printStackTrace();

        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }


    private void newListener()
    {
        (new Thread(this)).start();
    }

    private static String getPath(BufferedReader in)
        throws IOException
    {
        String line = in.readLine();
        String path = "";
        // extract class from GET line
        if (line.startsWith("GET /")) {
            line = line.substring(5, line.length()-1).trim();
            int index = line.indexOf(' ');
            if (index != -1) {
                path = line.substring(0, index);
            }
        }

        // eat the rest of header
        do {
            line = in.readLine();
        } while ((line.length() != 0) &&
                 (line.charAt(0) != '\r') && (line.charAt(0) != '\n'));

        if (path.length() != 0) {
            return path;
        } else {
            throw new IOException("Malformed Header");
        }
  }
*/



  }


  
