// Copyright Eric Chauvin 2020.


  // This is similar to one page.
  // This can be anything at a specific URL.  It can
  // be an html file, a script page, a jpeg file,
  // or anything.
  

import java.time.LocalDateTime;



public class URLFile
  {
  private MainApp mApp;
  private StrA title = StrA.Empty;
  private StrA fileName = StrA.Empty;
  private StrA url = StrA.Empty;


  private URLFile()
    {
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


  }
