// Copyright Eric Chauvin 2020.




// Remove comments: like this: /a>-->



public class HtmlFile
  {
  private MainApp mApp;
  private StrA htmlS = StrA.Empty;
  private StrA scriptS = StrA.Empty;
  private StrA cDataS = StrA.Empty;
  private URLFileDictionary urlFileDictionary;
  private URLParse urlParse;
  private StrA inURL = StrA.Empty;



  private static final StrA TagTitleStart = new
                                      StrA( "title" );

  private static final StrA TagTitleEnd = new
                                      StrA( "/title" );

  private static final StrA TagAnchorStart = new
                                         StrA( "a" );

  private static final StrA TagAnchorEnd = new
                                         StrA( "/a" );

  private static final StrA TagHeadStart = new
                                       StrA( "head" );

  private static final StrA TagHeadEnd = new
                                       StrA( "/head" );

  private static final StrA CDataStart = new
                                  StrA( "<![CDATA[" );

  private static final StrA CDataEnd = new
                                       StrA( "]]>" );

  private HtmlFile()
    {
    }


  public HtmlFile( MainApp appToUse, URLFileDictionary
                                   useDictionary,
                                   StrA baseURL )
    {
    mApp = appToUse;
    inURL = baseURL;
    urlParse = new URLParse( mApp, baseURL );
    urlFileDictionary = useDictionary;
    }



  public boolean processLinks( StrA fileName )
    {
    if( fileName.length() == 0 )
      return true; // false;

    // mApp.showStatusAsync( "\n\nReading: " +
     //         fileName + "\nCame from URL " + inURL );

    StrA fileS = FileUtility.readFileToStrA( mApp,
                                 fileName,
                                 false,
                                 false );

    if( fileS.length() == 0 )
      {
      // mApp.showStatusAsync( "File length zero.\n" + fileName );
      return true; // false;
      }

    // CData can be commented out within a script:
    // / *]]><![CDATA[* /
    // It is to make it so it's not interpreted
    // as HTML.  But it's within a script.

    getScriptAndHtml( fileS );
    htmlS = getCData( htmlS );

    processNewAnchorTags();
    // StrA getTitle()

    return true;
    }




  // This needs to be marked up like in a
  // CodeBlockDictionary.
  // Also, this assumes there's not a script tag
  // within the CData.  For now.
  private StrA getCData( StrA in )
    {
    cDataS = StrA.Empty;

    StrABld textBld = new StrABld( in.length() );
    StrABld cDataBld = new StrABld( in.length() );

    StrArray splitS = in.splitStrA( CDataStart );

    // At zero, before the first cData.
    textBld.appendStrA( splitS.getStrAt( 0 ));

    final int last = splitS.length();
    for( int count = 1; count < last; count++ )
      {
      StrA line = splitS.getStrAt( count );
      StrArray lineSplit = line.splitStrA( CDataEnd );
      int splitfields = lineSplit.length();
      if( splitfields > 2 )
        {
        mApp.showStatusAsync( "CData has more than one end marker." );
        mApp.showStatusAsync( "line: " + line );
        return StrA.Empty;
        }

      if( splitfields == 0 )
        {
        mApp.showStatusAsync( "CData has nothing in it." );
        mApp.showStatusAsync( "line: " + line );
        return StrA.Empty;
        }

      cDataBld.appendStrA( CDataStart );
      cDataBld.appendStrA( lineSplit.getStrAt( 0 ));
      cDataBld.appendStrA( CDataEnd );

      if( splitfields > 1 )
        textBld.appendStrA( lineSplit.getStrAt( 1 ));
   
      }

    StrA result = textBld.toStrA();
    cDataS = cDataBld.toStrA();

    // if( cDataS.length() > 0 )
      // mApp.showStatusAsync( "\n\ncData: " + cDataS );

    return result;
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



  private void processNewAnchorTags()
    {
    boolean isInsideAnchor = false;

    urlParse.clear();

    // if( inURL.containsStrA( new StrA( "/classified" )))
      // mApp.showStatusAsync( "\n\nClassified: " + htmlS );

    // The link tag is for style sheets.

    StrArray tagParts = htmlS.splitChar( '<' );
    final int last = tagParts.length();
    // mApp.showStatusAsync( "Before first tag: " + tagParts.getStrAt( 0 ));

    for( int count = 1; count < last; count++ )
      {
      StrA line = tagParts.getStrAt( count );

      StrA lowerLine = line.toLowerCase();
      if( !( lowerLine.startsWith( TagAnchorStart ) ||
             lowerLine.startsWith( TagAnchorEnd ) ))
        continue;

      StrArray lineParts = line.splitChar( '>' );
      final int lastPart = lineParts.length();
      if( lastPart == 0 )
        {
        mApp.showStatusAsync( "The tag doesn't have any parts." );
        mApp.showStatusAsync( "line: " + line );
        return;
        }

      if( lastPart > 2 )
        {
        // line: /span> Posting">Post comment

        mApp.showStatusAsync( "lastPart > 2." );
        mApp.showStatusAsync( "line: " + line );
        // return;
        }

      StrA tag = lineParts.getStrAt( 0 );
      // It's a short tag that I don't want to 
      // deal with yet.
      if( tag.endsWithChar( '/' ))
        {
        // if( tag.startsWithChar( 'a' ))
          mApp.showStatusAsync( "Short tag: " + tag );

        continue;
        }

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

      if( tagName.equalTo( TagAnchorStart ))
        {
        // It is called an anchor tag.
        isInsideAnchor = true;
        urlParse.clear();

        for( int countA = 1; countA < lastAttr; countA++ )
          {
          StrA attr = tagAttr.getStrAt( countA );
          attr = attr.concat( new StrA( " " ));
          urlParse.addRawText( attr );
          }

        urlParse.addRawText( new StrA( " >" ));
        }

      if( tagName.equalTo( TagAnchorEnd ))
        {
        if( urlParse.processLink())
          {
          StrA link = urlParse.getLink();
          StrA linkText = urlParse.getLinkText();
          if( linkText.length() > 0 )
            {
            if( !urlFileDictionary.keyExists( link ))
              {
              mApp.showStatusAsync( "\n\nLinkText: " + linkText );
              mApp.showStatusAsync( "Link: " + link );
              URLFile uFile = new URLFile( mApp,
                                     linkText, link );
              urlFileDictionary.setValue( link, uFile );
              }
            }
          }
        }

      if( isInsideAnchor )
        {
        if( lastPart >= 2 )
          {
          urlParse.addRawText( lineParts.getStrAt( 1 ));
          }
        }
      }
    }



/*
  private StrA getTitle()
    {
    boolean isInsideHeader = false;
    boolean isInsideTitle = false;

    urlParse.clear();

    StrArray tagParts = htmlS.splitChar( '<' );
    final int last = tagParts.length();
    // mApp.showStatusAsync( "Before first tag: " + tagParts.getStrAt( 0 ));

    StrA styleS = new StrA( "style" );
    StrA metaS = new StrA( "meta" );
    StrA linkS = new StrA( "link" );
    StrA divS = new StrA( "div" );
    StrA spanS = new StrA( "span" );
    StrA cDashData = new StrA( "c-data" );
    // StrA anchor = new StrA( "a" );


    for( int count = 1; count < last; count++ )
      {
      StrA line = tagParts.getStrAt( count );
      // if( !line.startsWith( anchor ))
        // continue;

===== Only the tags I want here.
      if( !( line.startsWith( TagAnchorStart ) ||
             line.startsWith( TagAnchorEnd ) )
        continue;

      if( line.startsWith( styleS ))
        continue;

      if( line.startsWith( metaS ))
        continue;

      if( line.startsWith( linkS ))
        continue;

      if( line.startsWith( divS ))
        continue;

      if( line.startsWith( spanS ))
        continue;

      if( line.startsWith( cDashData ))
        continue;

      StrArray lineParts = line.splitChar( '>' );
      final int lastPart = lineParts.length();
      if( lastPart == 0 )
        {
        mApp.showStatusAsync( "The tag doesn't have any parts." );
        mApp.showStatusAsync( "line: " + line );
        return StrA.Empty;
        }

      if( lastPart > 2 )
        {
        // line: /span> Posting">Post comment

        // mApp.showStatusAsync( "lastPart > 2." );
        // mApp.showStatusAsync( "line: " + line );
        // return;
        }

      // Short tag: input 
      // Look for input tags.

      StrA tag = lineParts.getStrAt( 0 );
      // It's a short tag that I don't want to 
      // deal with yet.
      if( tag.endsWithChar( '/' ))
        {
        continue;
        }

      // mApp.showStatusAsync( "tag: " + tag );
      StrArray tagAttr = tag.splitChar( ' ' );
      final int lastAttr = tagAttr.length();
      if( lastAttr == 0 )
        {
        mApp.showStatusAsync( "lastAttr is zero for the tag." );
        mApp.showStatusAsync( "tag: " + tag );
        return StrA.Empty;
        }

      StrA tagName = tagAttr.getStrAt( 0 );
      tagName = tagName.toLowerCase();
      // mApp.showStatusAsync( "\n\ntagName: " + tagName );

      if( tagName.equalTo( TagHeadStart ))
        {
        isInsideHeader = true;
        continue;
        }

      if( tagName.equalTo( TagHeadEnd ))
        {
        return StrA.Empty;
        }

      if( tagName.equalTo( TagTitleStart ))
        isInsideTitle = true;

      if( tagName.equalTo( TagTitleEnd ))
        isInsideTitle = false;

      // Inside the div tag there can be a title tag
      // for that division.

      if( isInsideTitle && isInsideHeader )
        {
        if( lastPart < 2 )
          {
          mApp.showStatusAsync( "Title has no text: " +
                         line );
          return StrA.Empty;
          }

        return lineParts.getStrAt( 1 ).
                           cleanUnicodeField().trim();
        }
      }

    return StrA.Empty;
    }
*/


  }
