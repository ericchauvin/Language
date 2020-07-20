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
  private StrA urlDictionaryFileName;



  private WebSites()
    {
    }


  public WebSites( MainApp appToUse )
    {
    mApp = appToUse;
    urlDictionaryFileName = new StrA(
             "\\ALang\\UrlDictionary.txt" );

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
    urlDictionary.saveToFile( urlDictionaryFileName );

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
    fileName = "\\ALang\\URLFiles\\" + fileName;
    mApp.showStatusAsync( "File name: " + fileName );
    String urlS = urlToGet.toString();
    URLClient urlClient = new URLClient( mApp,
                                 fileName,
                                 urlS );
 
    Thread urlThread = new Thread( urlClient );
    urlThread.start();
    }



  public void showCharacters()
    {
    // 126 is the tilde character.
    // 127 is delete.
    // 161 is upside down exclamation.
    // 169 is copyright.
    // 174 is rights symbol.
    // 209 is capital N like el niNa.
    // 232 through 235 is e.

    // C1 Controls and Latin-1 Supplement (0080 00FF)
    // Latin Extended-A (0100 017F)
    // Latin Extended-B (0180 024F)


    mApp.showStatusAsync( "\n\n" );
    // for( int count = 0x100; count <= 0x17F; count++ )
    for( int count = 126; count <= 0x024F; count++ )
      {
      char testC = (char)count;
      mApp.showStatusAsync( "" + count + ") " + testC );
      }

    mApp.showStatusAsync( "\n\n" );
    }



  public void processWebSites()
    {
    urlDictionary.readFromFile( urlDictionaryFileName );
    urlFifo = new FifoStrA( mApp, 1024 * 4 );

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
              "https://www.paysonroundup.com/" ));
              // "https://www.paysonroundup.com/news/" ));

    urlFifo.setValue( new StrA( 
                     "https://www.azcentral.com/" ));

    urlFifo.setValue( new StrA( 
                     "https://noticiasya.com/el-paso/" ));

    urlFifo.setValue( new StrA( 
              "https://diario.mx/seccion/El_Paso/" ));

    urlFifo.setValue( new StrA( 
                   "https://www.la-prensa.com.mx/" ));

    urlFifo.setValue( new StrA( 
                     "https://www.reforma.com/" ));

    urlFifo.setValue( new StrA( 
                     "https://www.milenio.com/" ));


    // UTF8StrA.doTest( mApp );

    processFiles();

    // showCharacters();
 
    // setupTimer();
    }



  public void processFiles()
    {
    mApp.showStatusAsync( "Processing files..." );
    StrA fileS = urlDictionary.makeFilesStrA();
    // mApp.showStatusAsync( "fileS: " + fileS );

    StrArray linesArray = fileS.splitChar( '\n' );
    final int last = linesArray.length();
    for( int count = 0; count < last; count++ )
      {
      // Test:
      // if( count > 2 )
        // break;

      StrA line = linesArray.getStrAt( count );
      // mApp.showStatusAsync( "" + line );
      StrA filePath = new StrA( "\\ALang\\URLFiles\\" );
      filePath = filePath.concat( line );
      // mApp.showStatusAsync( "" + filePath );

      HtmlFile hFile = new HtmlFile( mApp );
      if( !hFile.processFile( filePath ))
        return;

      }
    }



  }
