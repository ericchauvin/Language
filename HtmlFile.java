// Copyright Eric Chauvin 2020.


// Just get the title and link and decide what to
// do with it separately.

// The a tag might not have an ending </a> tag.
// It might have no text.  Just an icon in 
// the attributes.
// endsWith() the slash then it's the ending tag
// all in one.

// There can be only two fields for _any_ tag.
// The attributes and then the text outside
// of any tag that follows it.

// Make simpler functions.  getTitle() and all that.
// <b/> has no attributes and no spaces.



public class HtmlFile
  {
  private MainApp mApp;
  private StrA htmlS = StrA.Empty;
  private StrA scriptS = StrA.Empty;
  private StrA cDataS = StrA.Empty;
  private StrA title = StrA.Empty;
  private URLFileDictionary urlFileDictionary;



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
    }



  public boolean processFile( StrA fileName,
                              StrA fileURL )
    {
    if( fileName.length() == 0 )
      return true; // false;

    mApp.showStatusAsync( "\n\nReading: " +
              fileName + "\nCame from URL " + fileURL );

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
    processHtml( fileURL );

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

    StrA cData = new StrA( "![CDATA[" );
    for( int count = 1; count < last; count++ )
      {
      StrA line = tagParts.getStrAt( count );
      if( line.containsStrA( cData ))
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
         //  /*]]>*/

        mApp.showStatusAsync( "lastPart > 2." );
        // mApp.showStatusAsync( "line: " + line );
        return;
        }

      StrA tag = lineParts.getStrAt( 0 );
 
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
        isInsideHeader = true;

      if( tagName.equalTo( TagHeadEnd ))
        isInsideHeader = false;

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
        mApp.showStatusAsync( "Title: " + title );
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
            // mApp.showStatusAsync( "" + attr );

            // href="mailto:adsales@foxnews.com and so on.

// See how it's passing this URL?
// Get rid of these.
// href="https://wa.me/?text=https://www.paysonroundup.com/special/retirement-ready/edition_4caf862a-6d23-5b3e-b25d-f787d27e4916.html"

// href="#"

// twitter.com/
// instagram.com/
// google.com/


            }

          // mApp.showStatusAsync( "" + countA +
          //               ") Tag attributes: " +
          //               attr );
          }
        }

      if( tagName.equalTo( TagAnchorEnd ))
        {
        mApp.showStatusAsync( "currentText: " +
                                       currentText );

        mApp.showStatusAsync( "currentLink: " +
                                       currentLink );

        currentLink = StrA.Empty;
        currentText = StrA.Empty;

        isInsideAnchor = false;
        }

      if( isInsideAnchor )
        {
        // for( int countL = 1; countL < lastPart; countL++ )
          // {
          if( lastPart >= 2 )
            {
            currentText = lineParts.getStrAt( 1 );
            }
          // }

        mApp.showStatusAsync( "\n\n" );
        }


      }

    }



  }
