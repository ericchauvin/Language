// Copyright Eric Chauvin 2020.


  // This is similar to one page.
  // This can be anything at a specific URL.  It can
  // be an html file, a jpeg file, a pdf file,
  // or anything.
  

import java.time.LocalDateTime;



public class URLFile
  {
  private MainApp mApp;
  private StrA title = StrA.Empty;
  private StrA fileName = StrA.Empty;
  private StrA url = StrA.Empty;
  private StrA dateTime = StrA.Empty;


  private URLFile()
    {
    }


  public URLFile( MainApp appToUse )
    {
    mApp = appToUse;
    dateTime = makeDateTime();
    }



  public URLFile( MainApp appToUse, StrA urlToUse )
    {
    mApp = appToUse;
    url = urlToUse.cleanUnicodeField().trim();
    fileName = makeNewFileName( url );
    dateTime = makeDateTime();
    }



  public URLFile( MainApp appToUse,
                  StrA titleToUse,
                  StrA urlToUse )
    {
    mApp = appToUse;
    url = urlToUse.cleanUnicodeField().trim();
    title = titleToUse.cleanUnicodeField();
    fileName = makeNewFileName( url );
    dateTime = makeDateTime();
    }



  public StrA getTitle()
    {
    return title;
    }


  public StrA getFileName()
    {
    return fileName;
    }



  public StrA getUrl()
    {
    return url;
    }


  private StrA makeNewFileName( StrA fromURL )
    {
    if( fromURL.length() == 0 )
      return StrA.Empty;

    LocalDateTime rightNow = LocalDateTime.now();
    int year = rightNow.getYear();
     
    int month = rightNow.getMonthValue();
    int day = rightNow.getDayOfMonth();
     
    // int getDayOfYear()
    // getDayOfWeek()
     
    int hour = rightNow.getHour();
    int minute = rightNow.getMinute();
    int second = rightNow.getSecond();
    int nano = rightNow.getNano();

    int index = url.GetCRC16();

    String fileName = "UFile_" +
           year + "_" +
           month + "_" +
           day + "_" +
           hour + "_" +
           minute + "_" +
           second + "_" + 
           nano + "_" + 
           index + ".txt";

    return new StrA( fileName );
    }



  private StrA makeDateTime()
    {
    LocalDateTime rightNow = LocalDateTime.now();
    int year = rightNow.getYear();
    int month = rightNow.getMonthValue();
    int day = rightNow.getDayOfMonth();
    int hour = rightNow.getHour();
    int minute = rightNow.getMinute();
    int second = rightNow.getSecond();
    int nano = rightNow.getNano();

    String result = "" +
           year + ";" +
           month + ";" +
           day + ";" +
           hour + ";" +
           minute + ";" +
           second + ";" + 
           nano;

    return new StrA( result );
    }




  public StrA toStrA()
    {
    StrABld sBld = new StrABld( 1024 * 4 );
    sBld.appendStrA( url );
    sBld.appendChar( Markers.URLFileDelimit );
    sBld.appendStrA( title );
    sBld.appendChar( Markers.URLFileDelimit );
    sBld.appendStrA( fileName );
    sBld.appendChar( Markers.URLFileDelimit );
    sBld.appendStrA( dateTime );
    sBld.appendChar( Markers.URLFileDelimit );
 
    return sBld.toStrA();
    }


  public void setFromStrA( StrA in )
    {
    // mApp.showStatusAsync( "in: " + in );

    StrArray fields = in.splitChar( Markers.URLFileDelimit );
    final int last = fields.length();
    if( last < 4 )
      {
      mApp.showStatusAsync( "URLFile: Fields < 4 in setFromStrA()." );
      mApp.showStatusAsync( "in: " + in );
      return;
      }

    url = fields.getStrAt( 0 );
    title = fields.getStrAt( 1 );
    fileName = fields.getStrAt( 2 );
    dateTime = fields.getStrAt( 3 );
    }



  }
