// Copyright Eric Chauvin 2020.


  // This is a collection of WebSite objects.


// java.net.URLConnection
import java.net.*;
import java.io.*;



public class WebSites
  {
  private MainApp mApp;

  private WebSites()
    {
    }


  public WebSites( MainApp appToUse )
    {
    mApp = appToUse;

    }



  public void processWebSites()
    {
    // Mexican newspaper
    // https://www.cronica.com.mx/

    // https://news.google.com/
    // https://news.google.com/topstories
    // https://www.foxnews.com/

    // https://durangoherald.com/
    // https://www.durangogov.org/
    // https://www.gilacountyaz.gov/
    // https://www.paysonaz.gov/
    // https://www.co.laplata.co.us/
    // https://www.paysonroundup.com/news/
    // https://www.azcentral.com/

    doTest();
    }



  private void doTest()
    {
    try
    {

    // https://www.paysonroundup.com/news/
    // java.net.URLConnection
    // import java.net.*;
    // import java.io.*;
    URL payson = new URL(
           "https://www.paysonroundup.com/news/" );

    // Calling openConnection() in the URL object.
    URLConnection uConnect = payson.openConnection();
    BufferedReader in = new BufferedReader(
                      new InputStreamReader(
                          uConnect.getInputStream()));
    for( int count = 0; count < 100000; count++ )
      {
      String inputLine = in.readLine();
      if( inputLine == null )
        break;
 
      mApp.showStatusAsync( "Got line: " + 
                                         inputLine );
      }

    in.close();
    }
    catch( Exception e )
      {
      mApp.showStatusAsync( "Exception in doTest()." );
      mApp.showStatusAsync( e.getMessage() );
      }
    }


  }
