// Copyright Eric Chauvin 2020.




public class AnalyzeNewLinks implements Runnable
  {
  private MainApp mApp;
  private URLFileDictionary urlDictionary;
  private StrA urlDictionaryFileName;



  private AnalyzeNewLinks()
    {
    }


  public AnalyzeNewLinks( MainApp appToUse )
    {
    mApp = appToUse;
    urlDictionaryFileName = new StrA(
             "\\ALang\\UrlDictionary.txt" );

    urlDictionary = new URLFileDictionary( mApp );
    }
    



  @Override
  public void run()
    {
    processLinks();
    }



  private void processLinks()
    {
    urlDictionary.readFromFile( urlDictionaryFileName );

    // UTF8StrA.doTest( mApp );
    // showCharacters();
    processFiles();
    }



  private void processFiles()
    {
    mApp.showStatusAsync( "Processing files..." );
    StrA fileS = urlDictionary.makeKeysValuesStrA();

    // mApp.showStatusAsync( "fileS: " + fileS );

    StrArray titleArray = new StrArray();
    StrArray linesArray = fileS.splitChar( '\n' );
    final int last = linesArray.length();
    for( int count = 0; count < last; count++ )
      {
      if( (count % 20) == 0 )
        mApp.showStatusAsync( "Files: " + count );

      StrA line = linesArray.getStrAt( count );

      URLFile uFile = new URLFile( mApp );
      uFile.setFromStrA( line );

      StrA pulled = uFile.getAnchorsPulled();
      if( pulled.startsWithChar( 't' ))
        continue;

      StrA fileName = uFile.getFileName();
      StrA title = uFile.getTitle();
      titleArray.append( title );
      // mApp.showStatusAsync( "" + line );
      StrA filePath = new StrA( "\\ALang\\URLFiles\\" );
      filePath = filePath.concat( fileName );
      // mApp.showStatusAsync( "filePath: " + filePath );

      if( !FileUtility.exists( filePath ))
        continue;

      HtmlFile hFile = new HtmlFile( mApp,
                                     urlDictionary,
                                     uFile.getUrl() );

      if( !hFile.processFile( filePath ))
        {
        return;
        }

      uFile.setAnchorsPulledTrue();
      urlDictionary.setValue( uFile.getUrl(), uFile );
      }

    urlDictionary.saveToFile( urlDictionaryFileName );

    mApp.showStatusAsync( "\n\nSorting...\n\n" );
    titleArray.sort();
    final int lastTitle = titleArray.length();
    StrABld sBld = new StrABld( 1024 * 64 );
    StrA homeless = new StrA( "homeless" );
    StrA shelter = new StrA( "shelter" );
    for( int count = 0; count < lastTitle; count++ )
      {
      StrA showS = titleArray.getStrAt( count ).
                                        toLowerCase();
      if( !(showS.containsStrA( homeless ) ||
            showS.containsStrA( shelter ) ))
        continue;

      showS = showS.concat( new StrA( "\n" ));
      sBld.appendStrA( showS );
      }

    mApp.showStatusAsync( sBld.toStrA().toString() );

    mApp.showStatusAsync( "\nDone processing." );
    }


  }
