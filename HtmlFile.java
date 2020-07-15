// Copyright Eric Chauvin 2020.



public class HtmlFile
  {
  private MainApp mApp;
  private StrA textS = StrA.Empty;
  private StrA scriptS = StrA.Empty;

  private HtmlFile()
    {
    }


  public HtmlFile( MainApp appToUse )
    {
    mApp = appToUse;
    }


  public boolean processFile( StrA fileName )
    {
    if( fileName.length() == 0 )
      return false;

    mApp.showStatusAsync( "\n\nReading: " + fileName );

    StrA fileS = FileUtility.readFileToStrA( mApp,
                                 fileName,
                                 false,
                                 false );

    if( fileS.length() == 0 )
      {
      mApp.showStatusAsync( "File length zero.\n" + fileName );
      return false;
      }

    // mApp.showStatusAsync( "HTML file: " + fileS );
    getScriptAndText( fileS );

/*
    StrArray lines = fileS.splitChar( '\n' );
    final int last = lines.length();
    for( int count = 0; count < last; count++ )
      {
      StrA line = lines.getStrAt( count );
      // mApp.showStatusAsync( "Line: " + line );
      }
*/
    return true;
    }



  private void getScriptAndText( StrA in )
    {
    textS = StrA.Empty;
    scriptS = StrA.Empty;

    StrABld textBld = new StrABld( in.length() );
    StrABld scriptBld = new StrABld( in.length() );

    StrArray splitS = in.splitStrA( new StrA(
                       "<script" ));

    textBld.appendStrA( splitS.getStrAt( 0 ));

    final int last = splitS.length();
    for( int count = 1; count < last; count++ )
      {
      StrA line = splitS.getStrAt( count );
      StrArray lineSplit = line.splitStrA( new StrA(
                       "</script>" ));

      if( lineSplit.length() < 2 )
        {
// ======= It just has the first part.  Nothing
// following it.

        mApp.showStatusAsync( "Script doesn't have an end tag." );
        mApp.showStatusAsync( "line: " + line );
        return;
        }

      scriptBld.appendStrA( new StrA( "<script" ));
      scriptBld.appendStrA( lineSplit.getStrAt( 0 ));
      scriptBld.appendStrA( new StrA( "</script>" ));

      textBld.appendStrA( lineSplit.getStrAt( 1 ));
      }


    textS = textBld.toStrA();
    scriptS = scriptBld.toStrA();

    // mApp.showStatusAsync( "\n\ntextS: " + textS );
    mApp.showStatusAsync( "\n\nscriptS: " + scriptS );

    }



  }
