// Copyright Eric Chauvin 2020.



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
    // getContentType: text/html; charset=utf-8
    // UTF8 doesn't have to be ASCII.

    // if contentType is ...
    getURLToAsciiFile();
    }



  private void getURLToAsciiFile()
    {
    try
    {
    URL url = new URL( URLToGet );

    // Calling openConnection() in the URL object
    // Creates the specific type of URLConnection.
    // Like HttpURLConnection.
    URLConnection uConnect = url.openConnection();
    BufferedReader in = new BufferedReader(
                      new InputStreamReader(
                          uConnect.getInputStream()));

    mApp.showStatusAsync( "Getting: " + URLToGet );
    mApp.showStatusAsync( "getContentEncoding: " +
               uConnect.getContentEncoding() );

    // mApp.showStatusAsync( "getContentLength: " +
    //           uConnect.getContentLength() );

    mApp.showStatusAsync( "getContentType: " +
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
      sBuilder.append( line + "\n" );
      // mApp.showStatusAsync( "Got line: " + line );
      }

    in.close();

    // Charset iso 8859-1
    // Windows-1252.
    // Characters from 128 to 159 used for symbols.

    String asciiS = getAsciiOnly( sBuilder.toString());
    StrA StrToWrite = new StrA( asciiS );

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



  private String getAsciiOnly( String in )
    {
    StringBuilder sBuilder = new StringBuilder();
    final int last = in.length();
    for( int count = 0; count < last; count++ )
      {
      char testC = in.charAt( count );
      if( testC <= (char)126 )
        sBuilder.append( testC );

      }

    return sBuilder.toString();
    }



  }


  
