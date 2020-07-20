// Copyright Eric Chauvin 2020.

// The default encoding for InputStreamReader is:
// Cp1252.  Windows-1252.  CP-1252 is code page 1252.
// That means it doesn't decode anything.  The value
// of the byte is just the value of the byte.


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
    // This will never happen because the default
    // constructor was called to make the inStream
    // and no character set was given to it.
    if( !encoding.contains( "Cp1252" ))
       mApp.showStatusAsync( "\n\nThis will never happen. It's not the Windows encoding: " + encoding + "\n\n" );

    BufferedReader in = new BufferedReader( inStream );

    // Always null.
    // mApp.showStatusAsync( "getContentEncoding: " +
    //           uConnect.getContentEncoding() );

    // mApp.showStatusAsync( "getContentLength: " +
    //           uConnect.getContentLength() );

    // getContentType: text/html; charset=utf-8

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

    StrA StrToWrite = new StrA( sBuilder.toString());
    StrA StrToWriteKeep = StrToWrite;
    byte[] bytesBuf = strAToBytes( StrToWrite );
    StrToWrite = UTF8StrA.bytesToStrA( mApp,
                                       bytesBuf,
                                       1000000000 );

/*
    if( StrToWrite.length() == 0 )
      {
      StrToWrite = convertAsWindowsCodePage(
                                     StrToWriteKeep );

      mApp.showStatusAsync( "\n\nMaking it Windows code page type. URL:\n" + URLToGet );
      }
 */

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



  private byte[] strAToBytes( StrA in )
    {
    final int last = in.length();
    byte[] result = new byte[last];
    for( int count = 0; count < last; count++ )
      result[count] = (byte)(in.charAt( count ));

    return result;
    }



// The default encoding for InputStreamReader is:
// Cp1252.  Windows-1252.  CP-1252 is code page 1252.
// That means it doesn't decode anything.  The value
// of the byte is just the value of the byte.
/*
  private StrA convertAsWindowsCodePage( StrA in )
    {
    final int last = in.length();
    char[] result = new char[last];
    for( int count = 0; count < last; count++ )
      {
      result[count] = replaceWinCodePageChar( 
                                  in.charAt( count ));
      }

    return new StrA( result );
    }
*/


/*
  private char replaceWinCodePageChar( char in )
    {
    if( in == '\n' )
      return '\n';

    if( in == '\t' )
      return '\t';

    if( in < ' ' )
      return ' ';

    if( in > 0xFF )
      return Markers.ShowOddChar; // '_';

    // 126 is the tilde character.
    // 127 is delete.
    if( in >= 127 )
      {
      return Markers.ShowOddChar;

      // if( (in >= 232) && (in <= 235))
        // return 'e';

      // if( in == 169 ) // copyright.
        // return 'c';

      // if( in == 174 ) // rights symbol
        // return 'r';

      // return '_';

      // C1 Controls and Latin-1 Supplement (0080 00FF)
      // Latin Extended-A (0100 017F)
      // Latin Extended-B (0180 024F)

    // 161 is upside down exclamation.
    // 209 is capital N like el niNa.
    // 232 through 235 is e.
    //    mApp.showStatusAsync( "\n\n" );

      }
    
    return Markers.ShowOddChar;
    }
*/



  }


  
