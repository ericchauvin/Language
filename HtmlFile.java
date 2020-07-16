// Copyright Eric Chauvin 2020.




public class HtmlFile
  {
  private MainApp mApp;
  private StrA htmlS = StrA.Empty;
  private StrA scriptS = StrA.Empty;
  private StrA title = StrA.Empty;


  private static final StrA tagTitleStart = new
                                      StrA( "title" );

  private static final StrA tagTitleEnd = new
                                      StrA( "/title" );

  private static final StrA tagAnchorStart = new
                                         StrA( "a" );

  private static final StrA tagAnchorEnd = new
                                         StrA( "/a" );

  private static final StrA tagHeadStart = new
                                       StrA( "head" );

  private static final StrA tagHeadEnd = new
                                       StrA( "/head" );


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
      return true; // false;

    mApp.showStatusAsync( "\n\nReading: " + fileName );

    StrA fileS = FileUtility.readFileToStrA( mApp,
                                 fileName,
                                 false,
                                 false );

    if( fileS.length() == 0 )
      {
      mApp.showStatusAsync( "File length zero.\n" + fileName );
      return true; // false;
      }

    // mApp.showStatusAsync( "HTML file: " + fileS );
    getScriptAndHtml( fileS );
    processHtml();

    return true;
    }




  private void getScriptAndHtml( StrA in )
    {
    htmlS = StrA.Empty;
    scriptS = StrA.Empty;

    StrABld textBld = new StrABld( in.length() );
    StrABld scriptBld = new StrABld( in.length() );

    StrArray splitS = in.splitStrA( new StrA(
                       "<script" ));

    // At zero, before the first script tag.
    textBld.appendStrA( splitS.getStrAt( 0 ));

    final int last = splitS.length();
    for( int count = 1; count < last; count++ )
      {
      StrA line = splitS.getStrAt( count );
      StrArray lineSplit = line.splitStrA( new StrA(
                       "</script>" ));

      int splitfields = lineSplit.length();
      if( splitfields > 2 )
        {
        mApp.showStatusAsync( "Script has more than one end tag." );
        mApp.showStatusAsync( "line: " + line );
        return;
        }

      if( splitfields == 0 )
        {
        mApp.showStatusAsync( "Script has nothing in it." );
        mApp.showStatusAsync( "line: " + line );
        return;
        }

      scriptBld.appendStrA( new StrA( "<script" ));
      scriptBld.appendStrA( lineSplit.getStrAt( 0 ));
      scriptBld.appendStrA( new StrA( "</script>" ));

      if( splitfields > 1 )
        textBld.appendStrA( lineSplit.getStrAt( 1 ));
   
      }

    htmlS = textBld.toStrA();
    scriptS = scriptBld.toStrA();

    // mApp.showStatusAsync( "\n\nhtmlS: " + htmlS );
    // mApp.showStatusAsync( "\n\nscriptS: " + scriptS );
    }


  private void processHtml()
    {
    boolean isInsideHeader = false;
    boolean isInsideTitle = false;
    boolean isInsideAnchor = false;


    // The link tag is for style sheets.

    StrArray tagParts = htmlS.splitChar( '<' );
    final int last = tagParts.length();
    // mApp.showStatusAsync( "Before first tag: " + tagParts.getStrAt( 0 ));

    for( int count = 1; count < last; count++ )
      {
      StrA line = tagParts.getStrAt( count );
      StrArray lineParts = line.splitChar( '>' );
      final int lastPart = lineParts.length();
      if( lastPart == 0 )
        {
        mApp.showStatusAsync( "The tag doesn't have any parts." );
        mApp.showStatusAsync( "line: " + line );
        return;
        }

      StrA tag = lineParts.getStrAt( 0 );
 
      // tag: a href="//radio.foxnews.com/podcast" data-omtr-intcmp="hp1navpod"
      // tag: /a


      // mApp.showStatusAsync( "tag: " + tag );
      StrArray tagAttr = tag.splitChar( ' ' );
      final int lastAttr = tagAttr.length();
      if( lastAttr == 0 )
        {
        mApp.showStatusAsync( "lastAttr is zero for the tag." );
        mApp.showStatusAsync( "tag: " + tag );
        return;
        }

      StrA tagName = tagAttr.getStrAt( 0 );
      tagName = tagName.toLowerCase();
      // mApp.showStatusAsync( "\n\ntagName: " + tagName );

      if( tagName.equalTo( tagHeadStart ))
        isInsideHeader = true;

      if( tagName.equalTo( tagHeadEnd ))
        isInsideHeader = false;

      if( tagName.equalTo( tagTitleStart ))
        isInsideTitle = true;

      if( tagName.equalTo( tagTitleEnd ))
        isInsideTitle = false;

      // Inside the div tag there can be a title tag
      // for that division.

      if( isInsideTitle && isInsideHeader )
        {
        if( lastPart < 2 )
          {
          mApp.showStatusAsync( "Title has no text: " +
                         line );
          return;
          }

        title = lineParts.getStrAt( 1 );
        mApp.showStatusAsync( "Title: " + title );
        }

      if( tagName.equalTo( tagAnchorStart ))
        {
        isInsideAnchor = true;
        for( int countA = 1; countA < lastAttr; countA++ )
          {
          mApp.showStatusAsync( "Tag attribues: " +
                        tagAttr.getStrAt( countA ));
          }
        }


      if( tagName.equalTo( tagAnchorEnd ))
        isInsideAnchor = false;


/*
      for( int countL = 1; countL < lastPart; countL++ )
        {
        mApp.showStatusAsync( "Outside tags: " +
                         lineParts.getStrAt( countL ));

        }
*/

      }

    }



  }
