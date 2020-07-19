// Copyright Eric Chauvin 2020.


// Some programs add a byte order mark at the start
// of a file even though they shouldn't.  They are
// the three bytes: 0xEF, 0xBB, 0xBF
// Those three bytes encode the one character that
// is the Byte Order Mark.

// http://en.wikipedia.org/wiki/Unicode
// http://en.wikipedia.org/wiki/UTF-8
// http://en.wikipedia.org/wiki/Basic_Multilingual_Plane



public class UTF8StrA
  {


  public static void doTest( MainApp mApp )
    {
    StrABld sBld = new StrABld( 1024 * 64);

    // ErrorPoint is 0x2708.
    for( int count = 1; count < 0xD800; count++ )
    // for( int count = 0x3FF; count < 0x700; count++ )
      {
      char c = (char)count;
      sBld.appendChar( c );
      }

    StrA testS = sBld.toStrA();
    final int max = testS.length();
    byte[] buf = strAToBytes( testS );
    if( !isValidUTF8( mApp, buf, 1000000000 ))
      {
      mApp.showStatusAsync( "Not valid UTF8." );
      return;
      }

    // byte[] buf = UTF8Strings.strAToBytes( testS );
    StrA result = bytesToStrA( buf, 1000000000 );
    // StrA result = UTF8Strings.bytesToStrA( buf, 1000000000 );

    // for( int count = 0; count < max; count++ )
      // {
      // if( result.charAt( count ) !=

    if( result.length() != testS.length())
      {
      mApp.showStatusAsync( "Not the same length." );
      return;
      }
 
    if( result.equalTo( testS ))
      mApp.showStatusAsync( "It passed the test." );
    else
      mApp.showStatusAsync( "It failed the test." );

    }



  public static byte[] strAToBytes( StrA in )
    {
    if( in == null )
      return null;

    final int max = in.length();

    if( max == 0 )
      return null;

    byte[] result;
    try
    {
    result = new byte[max + (1024 * 64)];
    }
    catch( Exception e )
      {
      return null;
      }

    int where = 0;
    for( int count = 0; count < max; count++ )
      {
      char fullChar = in.charAt( count );
      if( fullChar >= 0xD800 ) // High Surrogates
        fullChar = ' '; 

      if( fullChar <= 0x7F )
        {
        // Regular ASCII.
        if( where >= result.length )
          {
          result = Utility.resizeByteArrayBigger(
                                          result,
                                          1024 * 64 );
          }

        result[where] = (byte)fullChar;
        where++;
        continue;
        }


      //  7   U+007F   0xxxxxxx
      // 11   U+07FF   110xxxxx   10xxxxxx
      // 16   U+FFFF   1110xxxx   10xxxxxx   10xxxxxx
      if( (fullChar > 0x7F) && (fullChar <= 0x7FF) )
        {
        // Notice that this cast (byte) from 
        // characters to bytes doesn't involve
        // characters over 0x7F.
        // Bottom 6 bits.
        byte smallByte = (byte)(fullChar & 0x3F); 
        // Big 5 bits.
        byte bigByte = (byte)((fullChar >> 6) & 0x1F); 

        bigByte |= 0xC0; // Mark it as the beginning byte.
        smallByte |= 0x80; // Mark it as a continuing byte.

        if( (where + 2) >= result.length )
          {
          result = Utility.resizeByteArrayBigger( result,
                                          1024 * 64 );
          }

        result[where] = bigByte;
        where++;
        result[where] = smallByte;
        where++;
        }

      // 16   U+FFFF   1110xxxx   10xxxxxx   10xxxxxx
      if( fullChar > 0x7FF )
        {
        byte byte3 = (byte)(fullChar & 0x3F);
        byte byte2 = (byte)((fullChar >> 6) & 0x3F);
        byte bigByte = (byte)((fullChar >> 12) & 0x0F); // Biggest 4 bits.

        bigByte |= 0xE0; // Mark it as the beginning byte.
        byte2 |= 0x80; // Mark it as a continuing byte.
        byte3 |= 0x80; // Mark it as a continuing byte.

        if( (where + 3) >= result.length )
          {
          result = Utility.resizeByteArrayBigger( result,
                                          1024 * 64 );
          }

        result[where] = bigByte;
        where++;
        result[where] = byte2;
        where++;
        result[where] = byte3;
        where++;
        }
      }

    return Utility.resizeByteArraySmaller( result,
                                             where );
    }




  public static StrA bytesToStrA( byte[] in,
                                  int maxLen )
    {
    try
    {
    if( in == null )
      return StrA.Empty;

    if( in.length == 0 )
      return StrA.Empty;

    if( in[0] == 0 )
      return StrA.Empty;

    if( maxLen > in.length )
      maxLen = in.length;

    final int last = maxLen;
    StrABld sBld = new StrABld( last + (1024 * 8));
    boolean isInside = false;
    for( int count = 0; count < last; count++ )
      {
      char c = (char)in[count];
      if( (c & 0x80) == 0 )
        {
        // It is regular ASCII.
        if( isInside )
          sBld.appendChar( Markers.End );

        isInside = false;
        sBld.appendChar( c );
        continue;
        }

      // If it is a beginning byte.
      if( (c & 0xC0) == 0xC0 )
        {
        if( isInside )
          {
          // There can be a sequence of high bytes
          // with no ASCII characters in between.
          sBld.appendChar( Markers.End );
          sBld.appendChar( Markers.Begin );
          }
        }

      if( !isInside )
        {
        sBld.appendChar( Markers.Begin );
        isInside = true;
        }

      sBld.appendChar( c );
      }

    if( isInside )
      sBld.appendChar( Markers.End );

    StrA markedS = sBld.toStrA();
    StrA result = convertMarked( markedS );
    return result;
    }
    catch( Exception e )
      {
      // mApp.showStatusAsync( "Exception in UTF8StrA.bytesToStrA(): " + e.getMessage() );
      return StrA.Empty;
      }
    }



  private static StrA convertMarked( // MainApp mApp, 
                                     StrA in )
    {
    if( in.length() == 0 )
      return StrA.Empty;

    StrArray splitS = in.splitChar( Markers.Begin );
    final int last = splitS.length();
    StrABld sBld = new StrABld( last );

    // Add what's before the first Begin marker.
    sBld.appendStrA( splitS.getStrAt( 0 ));

    for( int count = 1; count < last; count++ )
      {
      StrA line = splitS.getStrAt( count );
      StrArray splitLine = line.splitChar(
                                       Markers.End );

      StrA highChars = splitLine.getStrAt( 0 );
      StrA lowChars = StrA.Empty;
      if( splitLine.length() > 1 )
        lowChars = splitLine.getStrAt( 1 );

      char fullChar = sequenceToChar( highChars );
      sBld.appendChar( fullChar );
      sBld.appendStrA( lowChars );
      }

    return sBld.toStrA();
    }



  private static char sequenceToChar( // MainApp mApp,
                                      StrA in )
    {
    final int max = in.length();
    if( max < 2 )
      {
      // mApp.showStatusAsync( "Sequence was less than two characters." );
      return ' ';
      }
    
    if( max > 4 )
      {
      // mApp.showStatusAsync( "max > 4." );
      return ' ';
      }

    //  7   U+007F   0xxxxxxx
    // 11   U+07FF   110xxxxx   10xxxxxx
    // 16   U+FFFF   1110xxxx   10xxxxxx   10xxxxxx
    // 4 bytes       11110xxx  10xxxxxx  10xxxxxx  10xxxxxx

    // A 1010
    // B 1011
    // C 1100
    // D 1101
    // E 1110
    // F 1111 
    char first = in.charAt( 0 );
    char second = in.charAt( 1 );
    // It should be a continuing byte.
    if( (second & 0xC0) != 0x80 )
      {
      // mApp.showStatusAsync( "Second is not a continuing byte." );
      return ' ';
      }

    // A beginning byte is either 110xxxxx or
    // 1110xxxx or 11110xxx.
    if( (first & 0xC0) != 0xC0 )
      {
      // mApp.showStatusAsync( "First is not a beginning byte." );
      return ' ';
      }

    // Two bytes.
    if( (first & 0xE0) == 0xC0 )
      {
      // It is a 2 byte sequence.
      // Starts with 110xxxxx.
      if( max != 2 )
        {
        // mApp.showStatusAsync( "Max != 2." );
        return ' ';
        }

      char fullChar = (char)(first & 0x1F);
      fullChar = (char)(fullChar << 6);
      char byte2 = (char)(second & 0x3F);
      fullChar |= byte2;
      // fullChar is <= 0x07FF.
      // if( fullChar >= 0xD800 ) // High Surrogates

      // mApp.showStatusAsync( "FullChar: " + (int)fullChar );

      return fullChar;
      }

    // Three bytes.
    if( (first & 0xF0) == 0xE0 )
      {
      // It is a 3 byte sequence.
      if( max != 3 )
        {
        // mApp.showStatusAsync( "max != 3." );
        return ' ';
        }

      char third = in.charAt( 2 );
      if( (third & 0xC0) != 0x80 )
      {
      // mApp.showStatusAsync( "third is not a continuing byte." );
      return ' ';
      }

      char fullChar = (char)(first & 0x0F);
      char byte2 = (char)(second & 0x3F);
      char byte3 = (char)(third & 0x3F);
      fullChar = (char)(fullChar << 12);
      fullChar |= byte2 << 6;
      fullChar |= byte3;
      if( fullChar >= 0xD800 ) // High Surrogates
        {
        // mApp.showStatusAsync( "fullChar >= 0xD800." );
        return ' ';
        }

      return fullChar;
      }

    // Four bytes.
    if( (first & 0xF0) == 0xF0 )
      {
      // This doesn't convert the longer unusual 4
      // byte sequences.  It just does the Basic
      // Multilingual Plane.

      if( max != 4 )
        {
        // mApp.showStatusAsync( "max != 4." );
        return ' ';
        }

      // mApp.showStatusAsync( "It was four bytes." );
      return '_';
      }

    // mApp.showStatusAsync( "It was none of the above." );
    return '_';
    }



  public static boolean isValidUTF8( MainApp mApp,
                                  byte[] in,
                                  int maxLen )
    {
    try
    {
    if( in == null )
      return false;

    if( in.length == 0 )
      return false;

    if( in[0] == 0 )
      return false;

    if( maxLen > in.length )
      maxLen = in.length;

    final int last = maxLen;
    StrABld sBld = new StrABld( last + (1024 * 8));
    boolean isInside = false;
    for( int count = 0; count < last; count++ )
      {
      char c = (char)in[count];
      if( (c & 0x80) == 0 )
        {
        // It is regular ASCII.
        if( isInside )
          sBld.appendChar( Markers.End );

        isInside = false;
        sBld.appendChar( c );
        continue;
        }

      // If it is a beginning byte.
      if( (c & 0xC0) == 0xC0 )
        {
        if( isInside )
          {
          // There can be a sequence of high bytes
          // with no ASCII characters in between.
          sBld.appendChar( Markers.End );
          sBld.appendChar( Markers.Begin );
          }
        }

      if( !isInside )
        {
        sBld.appendChar( Markers.Begin );
        isInside = true;
        }

      sBld.appendChar( c );
      }

    if( isInside )
      sBld.appendChar( Markers.End );

    StrA markedS = sBld.toStrA();
    return convertMarkedTest( mApp, markedS );
    }
    catch( Exception e )
      {
      mApp.showStatusAsync( "Exception in UTF8StrA.isValidUTF8(): " + e.getMessage() );
      return false;
      }
    }



  private static boolean convertMarkedTest(
                                     MainApp mApp, 
                                     StrA in )
    {
    if( in.length() == 0 )
      return false;

    StrArray splitS = in.splitChar( Markers.Begin );
    final int last = splitS.length();
    for( int count = 1; count < last; count++ )
      {
      StrA line = splitS.getStrAt( count );
      StrArray splitLine = line.splitChar(
                                       Markers.End );

      StrA highChars = splitLine.getStrAt( 0 );
      StrA lowChars = StrA.Empty;
      if( splitLine.length() > 1 )
        lowChars = splitLine.getStrAt( 1 );

      if( !sequenceToCharTest( mApp, highChars ))
        return false;

      }

    return true;
    }



  private static boolean sequenceToCharTest(
                                      MainApp mApp,
                                      StrA in )
    {
    final int max = in.length();
    if( max < 2 )
      {
      mApp.showStatusAsync( "Sequence was less than two characters." );
      return false;
      }
    
    if( max > 4 )
      {
      mApp.showStatusAsync( "max > 4." );
      return false;
      }

    //  7   U+007F   0xxxxxxx
    // 11   U+07FF   110xxxxx   10xxxxxx
    // 16   U+FFFF   1110xxxx   10xxxxxx   10xxxxxx
    // 4 bytes       11110xxx  10xxxxxx  10xxxxxx  10xxxxxx

    // A 1010
    // B 1011
    // C 1100
    // D 1101
    // E 1110
    // F 1111 
    char first = in.charAt( 0 );
    char second = in.charAt( 1 );
    // It should be a continuing byte.
    if( (second & 0xC0) != 0x80 )
      {
      mApp.showStatusAsync( "Second is not a continuing byte." );
      return false;
      }

    // A beginning byte is either 110xxxxx or
    // 1110xxxx or 11110xxx.
    if( (first & 0xC0) != 0xC0 )
      {
      mApp.showStatusAsync( "First is not a beginning byte." );
      return false;
      }

    // Two bytes.
    if( (first & 0xE0) == 0xC0 )
      {
      // It is a 2 byte sequence.
      // Starts with 110xxxxx.
      if( max != 2 )
        {
        mApp.showStatusAsync( "Max != 2." );
        return false;
        }

      char fullChar = (char)(first & 0x1F);
      fullChar = (char)(fullChar << 6);
      char byte2 = (char)(second & 0x3F);
      fullChar |= byte2;
      return true;
      }

    // Three bytes.
    if( (first & 0xF0) == 0xE0 )
      {
      // It is a 3 byte sequence.
      if( max != 3 )
        {
        mApp.showStatusAsync( "max != 3." );
        return false;
        }

      char third = in.charAt( 2 );
      if( (third & 0xC0) != 0x80 )
      {
      mApp.showStatusAsync( "third is not a continuing byte." );
      return false;
      }

      char fullChar = (char)(first & 0x0F);
      char byte2 = (char)(second & 0x3F);
      char byte3 = (char)(third & 0x3F);
      fullChar = (char)(fullChar << 12);
      fullChar |= byte2 << 6;
      fullChar |= byte3;
      if( fullChar >= 0xD800 ) // High Surrogates
        {
        mApp.showStatusAsync( "fullChar >= 0xD800." );
        return false;
        }

      return true;
      }

    // Four bytes.
    if( (first & 0xF0) == 0xF0 )
      {
      // This doesn't convert the longer unusual 4
      // byte sequences.  It just does the Basic
      // Multilingual Plane.

      if( max != 4 )
        {
        mApp.showStatusAsync( "max != 4." );
        return false;
        }

      mApp.showStatusAsync( "It was four bytes." );
      return true;
      }

    mApp.showStatusAsync( "It was none of the above." );
    return false;
    }




/*
  private static StrA convertAsWindowsCodePage( StrA in )
    {
    final int last = in.length();
    char[] result = new char[last];
    for( int count = 0; count < last; count++ )
      {
      result[count] = replaceWinCodePageChar( 
                                  in.charAt( count ));
      }

    return new StrA( result );
    }



  public static char replaceWinCodePageChar( char in )
    {
    if( in == '\n' )
      return '\n';

    if( in == '\t' )
      return '\t';

    if( in < ' ' )
      return ' ';

    if( in > 0xFF )
      return Markers.ShowOddChar; // '_';

    // 126 is the tilde character.
    // 127 is delete.
    if( in >= 127 )
      {
      return Markers.ShowOddChar;

      // if( (in >= 232) && (in <= 235))
        // return 'e';

      // if( in == 169 ) // copyright.
        // return 'c';

      // if( in == 174 ) // rights symbol
        // return 'r';

      // return '_';

      // C1 Controls and Latin-1 Supplement (0080 00FF)
      // Latin Extended-A (0100 017F)
      // Latin Extended-B (0180 024F)

    // 161 is upside down exclamation.
    // 209 is capital N like el niNa.
    // 232 through 235 is e.
    //    mApp.showStatusAsync( "\n\n" );

      }
    
    return Markers.ShowOddChar;
    }
*/


  }
