// Copyright Eric Chauvin 2020.




public class HtmlFile
  {
  private MainApp mApp;
  private StrA htmlS = StrA.Empty;
  private StrA scriptS = StrA.Empty;
  private StrA cDataS = StrA.Empty;
  private StrA title = StrA.Empty;
  private URLFileDictionary urlFileDictionary;
  private StrArray badLinkArray;



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

  // href="https://example.com/"

  private static final StrA AttrHrefStart = new
                                      StrA( "href" );

  private static final StrA CDataStart = new
                                  StrA( "<![CDATA[" );

  private static final StrA CDataEnd = new
                                       StrA( "]]>" );

  private HtmlFile()
    {
    }


  public HtmlFile( MainApp appToUse, URLFileDictionary
                                   useDictionary )
    {
    mApp = appToUse;
    urlFileDictionary = useDictionary;
    setupBadLinkArray();
    }



  public boolean processFile( StrA fileName,
                              StrA fileURL )
    {
    if( fileName.length() == 0 )
      return true; // false;

    // mApp.showStatusAsync( "\n\nReading: " +
    //          fileName + "\nCame from URL " + fileURL );

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
    processHtml( fileURL );

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



  private void processHtml( StrA fileURL )
    {
    boolean isInsideHeader = false;
    boolean isInsideTitle = false;
    boolean isInsideAnchor = false;

    StrA currentLink = StrA.Empty;
    StrA currentText = StrA.Empty;


    // The link tag is for style sheets.

    StrArray tagParts = htmlS.splitChar( '<' );
    final int last = tagParts.length();
    // mApp.showStatusAsync( "Before first tag: " + tagParts.getStrAt( 0 ));

    StrA styleS = new StrA( "style" );
    StrA metaS = new StrA( "meta" );
    StrA linkS = new StrA( "link" );
    StrA divS = new StrA( "div" );
    StrA spanS = new StrA( "span" );
    StrA cDashData = new StrA( "c-data" );


    for( int count = 1; count < last; count++ )
      {
      StrA line = tagParts.getStrAt( count );
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
        return;
        }

      if( lastPart > 2 )
        {
        mApp.showStatusAsync( "lastPart > 2." );
        mApp.showStatusAsync( "line: " + line );
        return;
        }

      // Short tag: input 

      StrA tag = lineParts.getStrAt( 0 );
      // It's a short tag that I don't want to 
      // deal with yet.
      if( tag.endsWithChar( '/' ))
        {
        // mApp.showStatusAsync( "Short tag: " + tag );
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

      if( tagName.equalTo( TagHeadStart ))
        {
        isInsideHeader = true;
        continue;
        }

      if( tagName.equalTo( TagHeadEnd ))
        {
        isInsideHeader = false;
        continue;
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
          return;
          }

        title = lineParts.getStrAt( 1 );
        mApp.showStatusAsync( "\n\nTitle: " + title );
        }

      if( tagName.equalTo( TagAnchorStart ))
        {
        isInsideAnchor = true;
        for( int countA = 1; countA < lastAttr; countA++ )
          {
          StrA attr = tagAttr.getStrAt( countA );
          if( attr.containsStrA( AttrHrefStart ))
            {
            currentLink = attr;
            }
          }
        }

      if( tagName.equalTo( TagAnchorEnd ))
        {
        processLink( currentText, currentLink, fileURL );
        currentLink = StrA.Empty;
        currentText = StrA.Empty;
        isInsideAnchor = false;
        }

      if( isInsideAnchor )
        {
        if( lastPart >= 2 )
          {
          currentText = lineParts.getStrAt( 1 );
          }
        }
      }
    }



  private void processLink( StrA text, StrA link,
                                       StrA base )
    {
    if( text.length() == 0 )
      return;

    link = link.replace( new StrA( "href=" ),
                             new StrA( "" ));

    link = link.replaceChar( '"', ' ' );
    link = link.cleanUnicodeField().trim();

    text = text.cleanUnicodeField().trim();

    if( link.length() == 1 ) // Like a # character.
      return;

    link = fixupLink( link, base );

    if( isBadLink( link ))
      return;

    if( urlFileDictionary.keyExists( link ))
      return;

    URLFile uFile = new URLFile( mApp, text, link );
    urlFileDictionary.setValue( link, uFile );

    mApp.showStatusAsync( "\nTitle: " + text );
    mApp.showStatusAsync( "Link: " + link );
    }



  private StrA fixupLink( StrA in, StrA base )
    {
    StrA result = in;
    StrA twoSlashes = new StrA( "//" );
    StrA httpS = new StrA( "https:" );

    if( result.startsWith( twoSlashes ))
      result = httpS.concat( result );
    
    return result;
    }



  private void setupBadLinkArray()
    {
    badLinkArray = new StrArray();
    badLinkArray.append( new StrA( "//radio." ));
    badLinkArray.append( new StrA( "//video." ));
    badLinkArray.append( new StrA(
                            ".foxnews.com/sports" ));
    badLinkArray.append( new StrA( 
                     ".foxnews.com/real-estate" ));

    badLinkArray.append( new StrA( 
                ".foxnews.com/category/real-estate" ));

    badLinkArray.append( new StrA( 
                "foxnews.com/category/style-and-beauty" ));

    badLinkArray.append( new StrA( 
                "foxnews.com/family" ));

    badLinkArray.append( new StrA( 
                "foxnews.com/category/faith-values" ));


    badLinkArray.append( new StrA(
                        ".foxnews.com/food-drink" ));
    badLinkArray.append( new StrA( 
       ".foxnews.com/category/fitness-and-wellbeing" ));
    badLinkArray.append( new StrA(
                       ".foxnews.com/entertainment" ));

    badLinkArray.append( new StrA(
                       "foxnews.com/category/world/world-religion" ));

    badLinkArray.append( new StrA(
           ".foxbusiness.com/category/real-estate" ));

    badLinkArray.append( new StrA(
                    "www.foxnews.com/lifestyle" ));
    badLinkArray.append( new StrA(
                "www.foxbusiness.com/lifestyle" ));

    badLinkArray.append( new StrA(
                "www.foxbusiness.com/sports" ));
    badLinkArray.append( new StrA( "/privacy-policy" ));
    badLinkArray.append( new StrA(
             "foxnews.com/category/entertainment" ));
    badLinkArray.append( new StrA(
              "www.foxbusiness.com/category/arts" ));
    badLinkArray.append( new StrA(
                           "www.foxnews.com/shows" ));
    badLinkArray.append( new StrA(
                   "www.foxnews.com/official-polls" ));
    badLinkArray.append( new StrA(
                           "www.foxnews.com/auto" ));
    badLinkArray.append( new StrA(
                         "www.foxnews.com/travel" ));
    badLinkArray.append( new StrA( "foxnews.com/category/tech/topics/video-games" ));
    badLinkArray.append( new StrA( ".foxbusiness.com/closed-captioning/" ));
    badLinkArray.append( new StrA( ".foxnews.com/person/" ));
    badLinkArray.append( new StrA( ".foxnews.com/about/rss/" ));
    badLinkArray.append( new StrA( ".foxnews.com/category/media/" ));
    badLinkArray.append( new StrA( "foxbusiness.com/category/personal-real-estate" ));
    badLinkArray.append( new StrA( "help.foxbusiness.com" ));
    badLinkArray.append( new StrA( ".foxbusiness.com/real-estate/" ));
    badLinkArray.append( new StrA( "https://www.foxbusiness.com/luxury" ));
    badLinkArray.append( new StrA( ".foxnews.com/category/world/united-nations" ));
    badLinkArray.append( new StrA( "press.foxbusiness.com/" ));
    badLinkArray.append( new StrA( "press.foxnews.com/" ));
    badLinkArray.append( new StrA( ".foxnews.com/rss/" ));
    badLinkArray.append( new StrA( "obituaries.durangoherald.com" ));
    badLinkArray.append( new StrA( ".foxnews.com/newsletters" ));
    badLinkArray.append( new StrA( ".foxnews.com/accessibility-statement" ));
    badLinkArray.append( new StrA( ".foxnews.com/contact" ));
    badLinkArray.append( new StrA( "nation.foxnews.com/" ));
    badLinkArray.append( new StrA( "foxbusiness.com/real-estate/" ));
    badLinkArray.append( new StrA( "foxnews.com/foxaroundtheworld/" ));
    badLinkArray.append( new StrA( ".foxnews.com/compliance" ));
    badLinkArray.append( new StrA( ".foxbusiness.com/category/travel" ));
    badLinkArray.append( new StrA( ".foxbusiness.com/category/luxury-properties" ));
    badLinkArray.append( new StrA( ".foxbusiness.com/terms-of-use" ));
    badLinkArray.append( new StrA( "//www.facebook.com/" ));
    badLinkArray.append( new StrA( "//twitter.com/" ));
    badLinkArray.append( new StrA( "durangoherald.com/galleries/" ));
    badLinkArray.append( new StrA( "subscriptions.durangoherald.com" ));
    badLinkArray.append( new StrA( ".paysonroundup.com/multimedia/" ));
    badLinkArray.append( new StrA( ".paysonroundup.com/users/" ));

    // badLinkArray.append( new StrA( "" ));
    }



  private boolean hasValidDomain( StrA link )
    {
    // if( link.containsStrA( new StrA( ".foxnews.com/" )))
      // return true;

    // if( link.containsStrA( new StrA( ".foxbusiness.com/" )))
      // return true;

    if( link.containsStrA( new StrA( "durangoherald.com" )))
      return true;

    // if( link.containsStrA( new StrA( "durangogov.org/" )))
      // return true;

    // if( link.containsStrA( new StrA( "gilacountyaz.gov/" )))
      // return true;

    if( link.containsStrA( new StrA( "paysonaz.gov/" )))
      return true;

    if( link.containsStrA( new StrA( "paysonroundup.com/" )))
      return true;

    // if( link.containsStrA( new StrA( "azcentral.com" )))
      // return true;

    // if( link.containsStrA( new StrA( "noticiasya.com" )))
      // return true;

    // if( link.containsStrA( new StrA( "diario.mx" )))
      // return true;

    // if( link.containsStrA( new StrA( "la-prensa.com.mx" )))
      // return true;

    // if( link.containsStrA( new StrA( "milenio.com" )))
      // return true;

    return false;
    }



  private boolean isBadLink( StrA link )
    {
    // wa.me is WhatsApp.
    // Messaging app owned by Facebook.

    // The Roundup uses these.
    if( link.containsStrA( new StrA( "https://wa.me/" )))
      return true;

    if( link.containsStrA( new StrA( "mailto:" )))
      return true;

    if( link.containsStrA( new StrA( "ftp://" )))
      return true;

    if( link.containsStrA( new StrA( "sms:" )))
      return true;

    // This will miss relative links until I fix that.
    if( !hasValidDomain( link ))
      return true;

    if( link.endsWith( new StrA( ".pdf" )))
      return true;

    final int last = badLinkArray.length();
    for( int count = 0; count < last; count++ )
      {
      StrA text = badLinkArray.getStrAt( count );
      if( link.containsStrA( text ))
        return true;

      }

    return false;
    }



  }
