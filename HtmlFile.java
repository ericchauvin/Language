// Copyright Eric Chauvin 2020.




public class HtmlFile
  {
  private MainApp mApp;
  private URLFileDictionary urlFileDictionary;
  private URLParse urlParse;
  private StrA inURL = StrA.Empty;
  private StrA fileName = StrA.Empty;
  private StrA markedUpS = StrA.Empty;
  private StrA htmlS = StrA.Empty;



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


  private HtmlFile()
    {
    }


  public HtmlFile( MainApp appToUse, URLFileDictionary
                                   useDictionary,
                                   StrA baseURL,
                                   StrA fileNameToUse )
    {
    mApp = appToUse;
    inURL = baseURL;
    urlParse = new URLParse( mApp, baseURL );
    urlFileDictionary = useDictionary;
    fileName = fileNameToUse;
    }



  public boolean markUpFile()
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

    markupSections( fileS );
    return true;
    }




  public void processNewAnchorTags()
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
        if( tag.startsWithChar( 'a' ))
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




  public StrA getTitle()
    {
    boolean isInsideHeader = false;
    boolean isInsideTitle = false;

    StrArray tagParts = htmlS.splitChar( '<' );
    final int last = tagParts.length();

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
        return StrA.Empty;
        }

      if( lastPart > 2 )
        {
        // line: /span> Posting">Post comment

        // mApp.showStatusAsync( "lastPart > 2." );
        // mApp.showStatusAsync( "line: " + line );
        // return;
        }

      StrA tag = lineParts.getStrAt( 0 );
      if( tag.endsWithChar( '/' ))
        {
        // It's a short tag.
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
        return StrA.Empty;

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




  private void markupSections( StrA in )
    {
    // CData can be commented out within a script:
    // slash star  ]]><![CDATA[  star slash.
    // It is to make it so it's not interpreted
    // as HTML.  But it's within a script.
    // And then the script interprets the CData 
    // begin and end markers as something within
    // star slash comments.  To be ignored.

    // You could also have // -->
    // Two slashes for comments, which comment out
    // the ending --> comment marker.
    // Or a script tag followed by: <!--

    StrABld htmlBld = new StrABld( in.length() );

    StrA result = in;
    result = result.replace(
                             new StrA( "<![CDATA[" ),
                 new StrA( "" + Markers.BeginCData ));

    result = result.replace(
                               new StrA( "]]>" ),
                   new StrA( "" + Markers.EndCData ));

    result = result.replace(
                             new StrA( "<script" ),
                 new StrA( "" + Markers.BeginScript ));

    result = result.replace(
                             new StrA( "</script>" ),
                 new StrA( "" + Markers.EndScript ));


    result = result.replace(
                             new StrA( "<!--" ),
           new StrA( "" + Markers.BeginHtmlComment ));

    result = result.replace(
                             new StrA( "-->" ),
           new StrA( "" + Markers.EndHtmlComment ));

    boolean isInsideCData = false;
    boolean isInsideScript = false;
    boolean isInsideHtmlComment = false;
    final int last = result.length();
    for( int count = 0; count < last; count++ )
      {
      char testC = result.charAt( count );
      if( testC == Markers.BeginCData )
        {
        // This is very common.
        // if( isInsideScript )
          // mApp.showStatusAsync( "\nBegin CData inside script.\n" + fileName );

        isInsideCData = true;
        continue;
        }

      if( testC == Markers.EndCData )
        {
        isInsideCData = false;
        continue;
        }

      if( testC == Markers.BeginScript )
        {
        if( isInsideCData )
          mApp.showStatusAsync( "\nBegin script tag inside CData.\n" + fileName );

        isInsideScript = true;
        continue;
        }

      if( testC == Markers.EndScript )
        {
        if( isInsideCData )
          mApp.showStatusAsync( "\nEnd script tag inside CData.\n" + fileName );

        isInsideScript = false;
        continue;
        }

      if( testC == Markers.BeginHtmlComment )
        {
        if( isInsideCData )
          mApp.showStatusAsync( "\nBegin html comment tag inside CData.\n" + fileName );

        isInsideHtmlComment = true;
        continue;
        }

      if( testC == Markers.EndHtmlComment )
        {
        if( isInsideCData )
          mApp.showStatusAsync( "\nEnd html comment tag inside CData.\n" + fileName );

        isInsideHtmlComment = false;
        continue;
        }

      if( !(isInsideCData ||
            isInsideScript ||
            isInsideHtmlComment ))
        {
        htmlBld.appendChar( testC );
        }
      }

    htmlS = htmlBld.toStrA();
    markedUpS = result;
    }


/*
static so it can be called from anywhere.

  public static StrA fixAmpersandChars( StrA in )
    {


////
&aring; 00E5 
&aelig; 00E6 
&ccedil; 00E7 
&egrave; 00E8 
&eacute; 00E9 
&ecirc; 00EA 
&euml; 00EB 
&igrave; 00EC 
&iacute; 00ED 
&icirc; 00EE 
&iuml; 00EF 
&eth; 00F0 
&ntilde; 00F1 
&ograve; 00F2 
&oacute; 00F3 
&ocirc; 00F4 
&otilde; 00F5 
&ouml; 00F6 
&divide; 00F7 
&oslash; 00F8 
&ugrave; 00F9 
&uacute; 00FA 
&ucirc; 00FB 
&uuml; 00FC 
&yacute; 00FD 
&thorn; 00FE 
&yuml; 00FF 
&Amacr; 0100 
&amacr; 0101 
&Abreve; 0102 
&abreve; 0103 
&Aogon; 0104 
&aogon; 0105 
&Cacute; 0106 
&cacute; 0107 
&Ccirc; 0108 
&ccirc; 0109 
&Cdot; 010A 
&cdot; 010B 
&Ccaron; 010C 
&ccaron; 010D 
&Dcaron; 010E 
&dcaron; 010F 
&Dstrok; 0110 
&dstrok; 0111 
&Emacr; 0112 
&emacr; 0113 
&Edot; 0116 
&edot; 0117 
&Eogon; 0118 
&eogon; 0119 
&Ecaron; 011A 
&ecaron; 011B 
&Gcirc; 011C 
&gcirc; 011D 
&Gbreve; 011E 
&gbreve; 011F 
&Gdot; 0120 
&gdot; 0121 
&Gcedil; 0122 
&Hcirc; 0124 
&hcirc; 0125 
&Hstrok; 0126 
&hstrok; 0127 
&Itilde; 0128 
&itilde; 0129 
&Imacr; 012A 
&imacr; 012B 
&Iogon; 012E 
&iogon; 012F 
&Idot; 0130 
&imath; 0131 
&IJlig; 0132 
&ijlig; 0133 
&Jcirc; 0134 
&jcirc; 0135 
&Kcedil; 0136 
&kcedil; 0137 
&kgreen; 0138 
&Lacute; 0139 
&lacute; 013A 
&Lcedil; 013B 
&lcedil; 013C 
&Lcaron; 013D 
&lcaron; 013E 
&Lmidot; 013F 
&lmidot; 0140 
&Lstrok; 0141 
&lstrok; 0142 
&Nacute; 0143 
&nacute; 0144 
&Ncedil; 0145 
&ncedil; 0146 
&Ncaron; 0147 
&ncaron; 0148 
&napos; 0149 
&ENG; 014A 
&eng; 014B 
&Omacr; 014C 
&omacr; 014D 

    }
*/

  }
