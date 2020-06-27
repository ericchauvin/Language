// Copyright Eric Chauvin 2020.



// Language Project.


// https://github.com/openjdk-mirror/jdk7u-jdk/blob/master/src/share/classes/javax/net/ssl/SSLSocket.java
// https://github.com/openjdk-mirror/


import javax.swing.SwingUtilities;



class MainApp implements Runnable
  {
  public static final String versionDate = "6/27/2020";
  private MainWindow mainWin;
  // public ConfigureFile mainConfigFile;
  private String[] argsArray;



  public static void main( String[] args )
    {
    MainApp mApp = new MainApp( args );
    SwingUtilities.invokeLater( mApp );
    }



  @Override
  public void run()
    {
    setupProgram();
    }



  private MainApp()
    {
    }


  public MainApp( String[] args )
    {
    argsArray = args;
    }



  private void setupProgram()
    {
    // checkSingleInstance()
 
    /*
    String programDirectory = "\\Eric\\Language\\";
    int length = argsArray.length;
    if( length > 0 )
      programDirectory = argsArray[0];

    String mainConfigFileName = programDirectory +
                                      "MainConfigure.txt";

    mainConfigFile = new ConfigureFile( this,
                                mainConfigFileName );
    */

    mainWin = new MainWindow( this, "Language" );
    mainWin.initialize();

    /*
    showStatus( " " );
    showStatus( "argsArray length: " + length );
    for( int count = 0; count < length; count++ )
      showStatus( argsArray[count] );
    */

    // showStatus( " " );
    }



  public void showStatusAsync( String toShow )
    {
    if( mainWin == null )
      return;

    mainWin.showStatusAsync( toShow );
    }



  public void clearStatus()
    {
    if( mainWin == null )
      return;

    mainWin.clearStatus();
    }




  }
