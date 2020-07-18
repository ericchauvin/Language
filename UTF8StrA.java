// Copyright Eric Chauvin 2020.


// If a byte array is not valid UTF8 then this
// will convert it as Windows-1252. 
// CP-1252 is code page 1252.


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


  public static StrA bytesToStrA( MainApp mApp,
                                  byte[] in,
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
      char c = in[count];
      if( (c & 0x80) == 0 )
        {
        // It is regular ASCII.
        if( isInside )
          sBld.appendChar( Markers.End );

        isInside = false;
        sBld.appendChar( c );
        continue;
        }

      if( !isInside )
        {
        sBld.appendChar( Markers.Begin );
        isInside = true;
        }

      sBld.appendChar( c );
      }

    StrA markedS = sBld.toStrA();
    StrA result = convertMarked( markedS );

    if( result.containsChar( Markers.ErrorPoint ))
      return convertAsWindowsCodePage( markedS );

    return result;
    }
    catch( Exception e )
      {
      mApp.showStatusAsync( "Exception in UTF8StrA.bytesToString(): " + e.getMessage() );
      return StrA.Empty;
      }
    }



  private static StrA convertMarked( StrA in )
    {
    if( in.length() == 0 )
      return StrA.Empty;

    StrArray splitS = in.splitChar( Markers.Begin );
    final int last = splitS.length();
    StABld sBld = new StrABld( last );

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

      // StrA charSeq = removeAllMarkers( highChars );

      char fullChar = sequenceToChar( highChars );
      if( fullChar == Markers.ErrorPoint )
        {
        sBld.appendChar( Markers.ErrorPoint );
        return sBld.toStrA();
        }
      }

    return sBld.toStrA();
    }



  private static char sequenceToChar( StrA in )
    {
    final int max = in.length();
    if( max < 2 )
      return Markers.ErrorPoint;
    
    // This doesn't convert the longer unusual 4
    // byte sequences.  It just does the Basic
    // Multilingual Plane.

    if( max > 4 )
      return Markers.ErrorPoint;

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
      return Markers.ErrorPoint;


    // A beginning byte is either 110xxxxx or
    // 1110xxxx or 11110xxx.
    if( (first & 0xC0) != 0xC0 )
      return Markers.ErrorPoint;

    // Two bytes.
    if( (first & 0xE0) == 0xC0 )
      {
      // It is a 2 byte sequence.
      // Starts with 110xxxxx.
      if( max != 2 )
        return Markers.ErrorPoint;

      char fullChar = (char)(first & 0x0F);
      char byte2 = (char)(second & 0x3F);
      fullChar = fullChar << 6;
      fullChar |= byte2;
      // fullChar is <= 0x07FF.
      // if( fullChar >= 0xD800 ) // High Surrogates

      return fullChar;
      }

    // Three bytes.
    if( (first & 0xF0) == 0xE0 )
      {
      // It is a 3 byte sequence.
      if( max != 3 )
        return Markers.ErrorPoint;

      char third = in.charAt( 2 );
      if( (third & 0xC0) != 0x80 )
        return Markers.ErrorPoint;

      char fullChar = (char)(first & 0x0F);
      char byte2 = (char)(second & 0x3F);
      char byte3 = (char)(third & 0x3F);
      fullChar = fullChar << 12;
      fullChar |= byte2 << 6;
      fullChar |= byte3;
      if( fullChar >= 0xD800 ) // High Surrogates
        return Markers.ErrorPoint;

      return fullChar;
      }

    // Four bytes.
    if( (first & 0xF0) == 0xF0 )
      {
      // This doesn't convert the longer unusual 4
      // byte sequences.  It just does the Basic
      // Multilingual Plane.

      if( max != 4 )
        return Markers.ErrorPoint;

      return '_';
      }

    return Markers.ErrorPoint;
    }




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
      return '_';

    // 126 is the tilde character.
    // 127 is delete.
    if( in >= 127 )
      {
      // if( (in >= 232) && (in <= 235))
        // return 'e';

      // if( in == 169 ) // copyright.
        // return 'c';

      // if( in == 174 ) // rights symbol
        // return 'r';

      return '_';

      // C1 Controls and Latin-1 Supplement (0080 00FF)
      // Latin Extended-A (0100 017F)
      // Latin Extended-B (0180 024F)

    // 161 is upside down exclamation.
    // 209 is capital N like el niNa.
    // 232 through 235 is e.
    //    mApp.showStatusAsync( "\n\n" );

      }
    
    return '_';
    }



  }
