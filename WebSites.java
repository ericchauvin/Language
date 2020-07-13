// Copyright Eric Chauvin 2020.



// A WebSite has one base URL.  Anchor tag that
// has a new base URL goes to a new WebSite object
// or it gets thrown away.


import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;




public class WebSites implements ActionListener
  {
  private MainApp mApp;
  private Timer getURLTimer;
  private FifoStrA urlFifo;
  private URLFileDictionary urlDictionary;




  private WebSites()
    {
    }


  public WebSites( MainApp appToUse )
    {
    mApp = appToUse;
    urlDictionary = new URLFileDictionary( mApp );
    }



  public void cancel()
    {
    if( getURLTimer != null )
      {
      getURLTimer.stop();
      getURLTimer = null;
      }
    }



  private void setupTimer()
    {
    int delay = 1000 * 4; 
    getURLTimer = new Timer( delay, this );
    getURLTimer.start();
    mApp.showStatusAsync( "Timer started." );
    }



  public void actionPerformed( ActionEvent event )
    {
    try
    {
    // String paramS = event.paramString();
    String command = event.getActionCommand();
    if( command == null )
      {
      // mApp.showStatusAsync( "ActionEvent command is null." );
      doTimerEvent();
      return;
      }

    // showStatus( "ActionEvent Command is: " + command );
    }
    catch( Exception e )
      {
      mApp.showStatusAsync( "Exception in ActionPerformed()." );
      mApp.showStatusAsync( e.getMessage() );
      }
    }



  private void doTimerEvent()
    {
    StrA urlToGet = urlFifo.getValue();
    if( urlToGet == null )
      {
      mApp.showStatusAsync( "Nothing in Fifo." );
      return;
      }
 
    mApp.showStatusAsync( "\nurlToGet is:\n" + urlToGet );
    URLFile uFile = urlDictionary.getValue( urlToGet );
    if( uFile == null )
      {
      uFile = new URLFile( mApp, urlToGet );
      urlDictionary.setValue( urlToGet, uFile );
      }

    String fileName = uFile.getFileName().toString();
    fileName = "\\AFiles\\" + fileName;
    mApp.showStatusAsync( "File name: " + fileName );
    String urlS = urlToGet.toString();
    URLClient urlClient = new URLClient( mApp,
                                 fileName,
                                 urlS );
 
    Thread urlThread = new Thread( urlClient );
    urlThread.start();
    }



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


  public void processWebSites()
    {
    urlFifo = new FifoStrA( mApp, 1024 * 4 );

    // Mexican newspaper
    urlFifo.setValue( new StrA( 
                    "https://www.cronica.com.mx/" ));

    urlFifo.setValue( new StrA( 
                       "https://news.google.com/" ));

    urlFifo.setValue( new StrA( 
                        "https://www.foxnews.com/" ));

    urlFifo.setValue( new StrA( 
                    "https://durangoherald.com/" ));

    urlFifo.setValue( new StrA( 
                    "https://www.durangogov.org/" ));

    urlFifo.setValue( new StrA( 
                  "https://www.gilacountyaz.gov/" ));

    urlFifo.setValue( new StrA( 
                     "https://www.paysonaz.gov/" ));

    urlFifo.setValue( new StrA( 
              "https://www.paysonroundup.com/news/" ));

    urlFifo.setValue( new StrA( 
                     "https://www.azcentral.com/" ));

 
    setupTimer();
    }




  }
