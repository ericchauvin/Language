// Copyright Eric Chauvin 2020.




public class URLParse
  {
  private MainApp mApp;
  private StrABld rawTagBld;
  private StrArray badLinkArray;
  private StrA linkText = StrA.Empty;
  private StrA link = StrA.Empty;
  private StrA baseDomain = StrA.Empty;
  private StrA baseURL = StrA.Empty;


  private static final StrA HrefStart = new
                                      StrA( "href=" );



  private URLParse()
    {
    }


  public URLParse( MainApp appToUse, StrA useBaseURL )
    {
    mApp = appToUse;
    baseURL = useBaseURL;

    baseDomain = getDomainFromLink( baseURL );
    StrA baseHttpS = new StrA( "https://" );
    baseDomain = baseHttpS.concat( baseDomain );

    rawTagBld = new StrABld( 1024 * 4 );
    setupBadLinkArray();
    }


  public StrA getLink()
    {
    return link;
    }



  public StrA getLinkText()
    {
    return linkText;
    }



  public void addRawText( StrA in )
    {
    rawTagBld.appendStrA( in );
    }



  public void clear()
    {
    rawTagBld.clear();
    linkText = StrA.Empty;
    link = StrA.Empty;
    }



  public boolean processLink()
    {
    StrA text = rawTagBld.toStrA();
    if( text.length() == 0 )
      {
      mApp.showStatusAsync( "Raw text length is zero." );
      return false;
      }

    if( !text.containsStrA( HrefStart ))
      return false;

    if( text.containsStrA( new StrA( "href=\"\"" )))
      return false;
    
    if( text.containsStrA( new StrA( "onclick" )))
      return false;

    // mApp.showStatusAsync( "\n\nRaw: " + text );

    StrArray lineParts = text.splitChar( '>' );
    final int lastPart = lineParts.length();
    if( lastPart == 0 )
      {
      mApp.showStatusAsync( "The anchor tag doesn't have any parts." );
      mApp.showStatusAsync( "Text: " + text );
      return false;
      }

    if( lastPart > 2 )
      {
      mApp.showStatusAsync( "Anchor tag lastPart > 2." );
      mApp.showStatusAsync( "text: " + text );
      return false;
      }

    linkText = StrA.Empty;
    if( lastPart >= 2 )
      linkText = lineParts.getStrAt( 1 );

    linkText = linkText.cleanUnicodeField().trim();
    // mApp.showStatusAsync( "\nlinkText: " + linkText );

    StrA insideTag = lineParts.getStrAt( 0 );

    // mApp.showStatusAsync( "insideTag: " + insideTag );

    StrArray tagAttr = insideTag.splitChar( ' ' );
    final int lastAttr = tagAttr.length();
    if( lastAttr == 0 )
      {
      mApp.showStatusAsync( "URLParse: lastAttr is zero." );
      return false;
      }

    link = StrA.Empty;
    for( int count = 0; count < lastAttr; count++ )
      {
      StrA attr = tagAttr.getStrAt( count );
      if( attr.containsStrA( HrefStart ))
        {
        link = attr;
        break;
        }
      }

    link = link.replace( HrefStart, StrA.Empty );
    link = link.replaceChar( '"', ' ' );
    link = link.cleanUnicodeField().trim();
    link = fixupLink( link );
    if( link.length() == 0 )
      return false;

    if( isBadLink( link ))
      return false;

    // Don't add new Spanish links.
    if( isSpanish( link ))
      return false;

    // mApp.showStatusAsync( "Link: " + link );

    return true;
    }




  private StrA getDomainFromLink( StrA link )
    {
    if( link.length() == 0 )
      return StrA.Empty;

    StrA dotCom = new StrA( ".com" );
    StrA dotMex = new StrA( ".mx" );
    StrA dotOrg = new StrA( ".org" );
    StrA dotGov = new StrA( ".gov" );

    StrArray linkParts = link.splitChar( '/' );
    final int last = linkParts.length();
    for( int count = 0; count < last; count++ )
      {
      StrA part = linkParts.getStrAt( count );
      if( (part.containsStrA( dotCom )) ||
          (part.containsStrA( dotMex )) ||
          (part.containsStrA( dotOrg )) ||
          (part.containsStrA( dotGov )) )
        {
        return part;
        }
      }
    
    return StrA.Empty;
    }



  private StrA fixupLink( StrA in )
    {
    if( in.length() < 2 )
      return StrA.Empty;

    // if( base.endsWithChar( '/' ))
      // base = base.substring( 0, base.length() - 2 );

    StrA result = in;

    StrArray paramParts = result.splitChar( '?' );
    final int lastParam = paramParts.length();
    if( lastParam == 0 )
      {
      mApp.showStatusAsync( "URLParse: lastParam is zero." );
      return StrA.Empty;
      }

    result = paramParts.getStrAt( 0 );

    StrA twoSlashes = new StrA( "//" );
    StrA httpS = new StrA( "https:" );

    if( result.startsWith( twoSlashes ))
      result = httpS.concat( result );

    if( result.startsWithChar( '/' ))
      result = baseDomain.concat( result );

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
    badLinkArray.append( new StrA(
      "foxnews.com/category/tech/topics/video-games" ));
    badLinkArray.append( new StrA(
                 ".foxbusiness.com/closed-captioning/" ));
    badLinkArray.append( new StrA(
                            ".foxnews.com/person/" ));
    badLinkArray.append( new StrA(
                            ".foxnews.com/about/rss/" ));
    badLinkArray.append( new StrA(
                      ".foxnews.com/category/media/" ));
    badLinkArray.append( new StrA(
       "foxbusiness.com/category/personal-real-estate" ));
    badLinkArray.append( new StrA(
                            "help.foxbusiness.com" ));
    badLinkArray.append( new StrA(
                      ".foxbusiness.com/real-estate/" ));
    badLinkArray.append( new StrA( 
                          ".foxbusiness.com/luxury" ));
    badLinkArray.append( new StrA(
           ".foxnews.com/category/world/united-nations" ));
    badLinkArray.append( new StrA(
                          "press.foxbusiness.com/" ));
    badLinkArray.append( new StrA( "press.foxnews.com/" ));
    badLinkArray.append( new StrA( ".foxnews.com/rss/" ));

    badLinkArray.append( new StrA(
                    "obituaries.durangoherald.com" ));

    badLinkArray.append( new StrA(
                         ".foxnews.com/newsletters" ));
    badLinkArray.append( new StrA(
               ".foxnews.com/accessibility-statement" ));
    badLinkArray.append( new StrA(
                              ".foxnews.com/contact" ));
    badLinkArray.append( new StrA(
                             "nation.foxnews.com/" ));
    badLinkArray.append( new StrA(
                      "foxbusiness.com/real-estate/" ));
    badLinkArray.append( new StrA(
                  "foxnews.com/foxaroundtheworld/" ));
    badLinkArray.append( new StrA(
                          ".foxnews.com/compliance" ));
    badLinkArray.append( new StrA(
                 ".foxbusiness.com/category/travel" ));
    badLinkArray.append( new StrA(
         ".foxbusiness.com/category/luxury-properties" ));
    badLinkArray.append( new StrA(
                   ".foxbusiness.com/terms-of-use" ));
    badLinkArray.append( new StrA(
                             "//www.facebook.com/" ));
    badLinkArray.append( new StrA( "//twitter.com/" ));
    badLinkArray.append( new StrA(
                     "durangoherald.com/galleries/" ));
    badLinkArray.append( new StrA(
                  "subscriptions.durangoherald.com" ));
    badLinkArray.append( new StrA(
                   ".paysonroundup.com/multimedia/" ));
    badLinkArray.append( new StrA(
                          ".paysonroundup.com/users/" ));

    badLinkArray.append( new StrA(
                   ".paysonroundup.com/classifieds" ));

    badLinkArray.append( new StrA( 
                   ".paysonroundup.com/multimedia" ));

    badLinkArray.append( new StrA(
            ".paysonroundup.com/tncms/auth/admin/" ));
    badLinkArray.append( new StrA(
                      ".paysonroundup.com/search/" ));
    badLinkArray.append( new StrA( 
                    ".paysonroundup.com/eedition/" ));

    badLinkArray.append( new StrA( 
                 ".paysonroundup.com/placead/" ));

    badLinkArray.append( new StrA( 
            ".paysonroundup.com/site/about.html" ));

    // badLinkArray.append( new StrA( "" ));
    }



  private boolean hasValidDomain( StrA link )
    {
    if( link.containsStrA( new StrA( ".foxnews.com/" )))
      return true;

    if( link.containsStrA( new StrA( ".foxbusiness.com/" )))
      return true;

    // if( link.containsStrA( new StrA( "durangoherald.com" )))
      // return true;

    // if( link.containsStrA( new StrA( "durangogov.org/" )))
      // return true;

    // if( link.containsStrA( new StrA( "gilacountyaz.gov/" )))
      // return true;

    // if( link.containsStrA( new StrA( "paysonaz.gov/" )))
      // return true;

    if( link.containsStrA( new StrA( "paysonroundup.com/" )))
      return true;

    // if( link.containsStrA( new StrA( "azcentral.com" )))
      // return true;

    // if( isSpanish( link ))
      // return true;

    return false;
    }



  private static boolean isSpanish( StrA link )
    {
    if( link.containsStrA( new StrA( "noticiasya.com" )))
      return true;

    if( link.containsStrA( new StrA( "diario.mx" )))
      return true;

    if( link.containsStrA( new StrA( "la-prensa.com.mx" )))
      return true;

    if( link.containsStrA( new StrA( "milenio.com" )))
      return true;

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
