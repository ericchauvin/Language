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
  private StrA fileType = StrA.Empty;


  private URLFile()
    {
    }


  public URLFile( MainApp appToUse )
    {
    mApp = appToUse;
    }



  public URLFile( MainApp appToUse, StrA urlToUse )
    {
    mApp = appToUse;
    url = urlToUse;
    fileName = makeNewFileName( url );
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
    // int getNano()

    int index = url.GetCRC16();

    String fileName = "UFile_" +
           year + "_" +
           month + "_" +
           day + "_" +
           hour + "_" +
           minute + "_" +
           second + "_" + 
           index + ".txt";
 

    return new StrA( fileName );
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
    sBld.appendStrA( fileType );
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
      mApp.showStatusAsync( "Fields < 4 in setFromStrA()." );
      return;
      }

    url = fields.getStrAt( 0 );
    title = fields.getStrAt( 1 );
    fileName = fields.getStrAt( 2 );
    fileType = fields.getStrAt( 3 );
    }



  }
