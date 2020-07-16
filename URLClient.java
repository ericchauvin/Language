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
    getURLToFile();
    }



  private void getURLToFile()
    {
    try
    {
    URL url = new URL( URLToGet );

    // Calling openConnection() in the URL object
    // Creates the specific type of URLConnection.
    // Like HttpURLConnection.
    URLConnection uConnect = url.openConnection();
    InputStreamReader inStream = new
                 InputStreamReader(
                          uConnect.getInputStream());
 
: Cp1252
    mApp.showStatusAsync( "Encoding for inStream is: " +
                              inStream.getEncoding());

    BufferedReader in = new BufferedReader( inStream );

    mApp.showStatusAsync( "Getting: " + URLToGet );

    // Alsways null.
    // mApp.showStatusAsync( "getContentEncoding: " +
    //           uConnect.getContentEncoding() );

    // mApp.showStatusAsync( "getContentLength: " +
    //           uConnect.getContentLength() );

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
      sBuilder.append( line + "\n" );
      // mApp.showStatusAsync( "Got line: " + line );
      }

    in.close();

    // Charset iso 8859-1
    // Windows-1252.
    // Characters from 128 to 159 used for symbols.

    String fixedChars = removeWindowsCharacters( 
                                 sBuilder.toString());

    StrA StrToWrite = new StrA( fixedChars );
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



  private String removeWindowsCharacters( String in )
    {
    StringBuilder sBuilder = new StringBuilder();
    final int last = in.length();
    for( int count = 0; count < last; count++ )
      {
      char testC = in.charAt( count );
      if( (testC >= (char)127) && 
          (testC <= (char)255))
        continue;
 
      sBuilder.append( testC );
      }

    return sBuilder.toString();
    }



  }


  
