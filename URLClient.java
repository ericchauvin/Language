// Copyright Eric Chauvin 2020.

// The default encoding for InputStreamReader is:
// Cp1252.  Windows-1252.  CP-1252 is code page 1252.


import java.net.URLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class URLClient implements Runnable
  {
  private MainApp mApp;
  private String fileName = "";
  private String URLToGet = "";
  private String contentType = "";




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
    InputStreamReader inStream = new
                 InputStreamReader(
                          uConnect.getInputStream());
 
    // Cp1252
    String encoding = inStream.getEncoding();
    if( !encoding.contains( "Cp1252" ))
       mApp.showStatusAsync( "\n\nIt's not the Windows encoding: " + encoding + "\n\n" );

    BufferedReader in = new BufferedReader( inStream );

    // Always null.
    // mApp.showStatusAsync( "getContentEncoding: " +
    //           uConnect.getContentEncoding() );

    // mApp.showStatusAsync( "getContentLength: " +
    //           uConnect.getContentLength() );

    // Encoding for inStream is: Cp1252
    // getContentType: text/html; charset=utf-8

    // But this will say it's UTF8.
    mApp.showStatusAsync( "\n\ngetContentType: " +
               uConnect.getContentType() );

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

    // Encoding for inStream is: Cp1252
    // This encoding just means that the byte is
    // converted in to a character of the same value.
    // So get the original bytes.
    byte[] bytesBuf = stringToBytes( 
                               sBuilder.toString());

    if( !UTF8Strings.isValidBytesArray( bytesBuf ))
      {
      mApp.showStatusAsync( "\n\nNot a valid UTF8 byte buffer." );
      mApp.showStatusAsync( URLToGet );
      }


    StrA StrToWrite = UTF8Strings.bytesToStrA(
                              bytesBuf, 1000000000 );

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



  private byte[] stringToBytes( String in )
    {
    final int last = in.length();
    byte[] result = new byte[last];
    for( int count = 0; count < last; count++ )
      result[count] = (byte)(in.charAt( count ));

    return result;
    }



  }


  
