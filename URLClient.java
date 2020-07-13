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
    BufferedReader in = new BufferedReader(
                      new InputStreamReader(
                          uConnect.getInputStream()));

    StringBuilder sBuilder = new StringBuilder();
    for( int count = 0; count < 1000000; count++ )
      {
      String line = in.readLine();
      if( line == null )
        break;
 
      sBuilder.append( line );
      // mApp.showStatusAsync( "Got line: " + line );
      }

    in.close();

    StrA StrToWrite = new StrA( sBuilder.toString());
    StrA fileToWrite = new StrA( fileName );

    mApp.showStatusAsync( "File StrA: " + StrToWrite );

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



  }


  
