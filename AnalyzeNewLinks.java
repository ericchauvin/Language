// Copyright Eric Chauvin 2020.




public class AnalyzeNewLinks implements Runnable
  {
  private MainApp mApp;
  private URLFileDictionary urlDictionary;



  private AnalyzeNewLinks()
    {
    }


  public AnalyzeNewLinks( MainApp appToUse,
                                URLFileDictionary
                                  urlDictionaryToUse )
    {
    mApp = appToUse;
    // Just don't use the dictionary while it's being
    // used in here.
    urlDictionary = urlDictionaryToUse;
    }
    


  @Override
  public void run()
    {
    // UTF8StrA.doTest( mApp );
    // showCharacters();
    getLinks();
    }



  private void getLinks()
    {
    mApp.showStatusAsync( "Getting links..." );
    StrA fileS = urlDictionary.makeKeysValuesStrA();

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
      mApp.showStatusAsync( "\n" + title );
      // mApp.showStatusAsync( "" + fileName );

      // mApp.showStatusAsync( "" + line );
      StrA filePath = new StrA( "\\ALang\\URLFiles\\" );
      filePath = filePath.concat( fileName );
      // mApp.showStatusAsync( "filePath: " + filePath );

      if( !FileUtility.exists( filePath ))
        {
        // This doesn't happen below:
        // setAnchorsPulledTrue();
        continue;
        }

      HtmlFile hFile = new HtmlFile( mApp,
                                     urlDictionary,
                                     uFile.getUrl(),
                                     filePath );

      if( !hFile.markUpFile())
        {
        return;
        }

      hFile.processNewAnchorTags();

      StrA oldTitle = uFile.getTitle();
      if( oldTitle.length() < 5 )
        {
        StrA newTitle = hFile.getTitle();
        mApp.showStatusAsync( "Old Title: " + oldTitle );
        mApp.showStatusAsync( "New Title: " + newTitle );
        uFile.setTitle( newTitle );
        }

      // If the file doesn't exist then anchorsPulled
      // doesn't get set to true because it never
      // gets here.
      uFile.setAnchorsPulledTrue();
      urlDictionary.setValue( uFile.getUrl(), uFile );
      }

    urlDictionary.saveToFile();

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
