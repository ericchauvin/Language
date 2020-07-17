// Copyright Eric Chauvin 2018 - 2020.



// Some programs add a byte order mark at the start
// of a file even though they shouldn't.  They are
// the three bytes: 0xEF, 0xBB, 0xBF
// Those three bytes encode the one character that
// is the Byte Order Mark.

// http://en.wikipedia.org/wiki/Unicode
// http://en.wikipedia.org/wiki/UTF-8
// http://en.wikipedia.org/wiki/Basic_Multilingual_Plane


  public class UTF8Strings
  {

  public static byte[] strAToBytes( StrA in )
    {
    if( in == null )
      return null;

    if( in.length() == 0 )
      return null;

    byte[] result;
    try
    {
    result = new byte[in.length() + (1024 * 64)];
    }
    catch( Exception e )
      {
      return null;
      }

    int where = 0;
    final int max = in.length();
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
        // Notice that this conversion from 
        // characters to bytes
        // doesn't involve characters over 0x7F.
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
      return new StrA( "" );

    if( in.length == 0 )
      return new StrA( "" );

    if( in[0] == 0 )
      return new StrA( "" );

    if( maxLen > in.length )
      maxLen = in.length;

    int runOnBytes = 0;
    char fullChar = ' ';
    StrABld sBld = new StrABld( maxLen );
    for( int count = 0; count < maxLen; count++ )
      {
      byte charPart = in[count];
      if( charPart == 0 )
        break;

      if( (charPart & 0x80) == 0 )
        {
        runOnBytes = 0;
        // It's regular ASCII.
        sBld.appendChar( (char)charPart );
        continue;
        }

      if( (charPart & 0xC0) == 0x80 )
        {
        runOnBytes++;
        if( runOnBytes > 3 )
          return sBld.toStrA();

        // It's a continuing byte that has already
        // been read below.
        continue;
        }

      if( (charPart & 0xC0) == 0xC0 )
        {
        runOnBytes = 0;
        // It's a beginning byte.
        // A beginning byte is either 110xxxxx or
        // 1110xxxx.
        if( (charPart & 0xF0) == 0xE0 )
          {
          // Starts with 1110xxxx.
          // It's a 3-byte character.
          if( (count + 2) >= maxLen )
            break; // Ignore the garbage.

          char bigByte = (char)(charPart & 0x0F);
          char byte2 = (char)(in[count + 1] & 0x3F);
          char byte3 = (char)(in[count + 2] & 0x3F);

          fullChar = (char)(bigByte << 12);
          fullChar |= (char)(byte2 << 6);
          fullChar |= byte3;
          if( fullChar < 0xD800 ) // High Surrogates
            sBld.appendChar( fullChar );

          }

        if( (charPart & 0xE0) == 0xC0 )
          {
          // Starts with 110xxxxx.
          // It's a 2-byte character.
          if( (count + 1) >= maxLen )
            continue; // Ignore the garbage.

          char bigByte = (char)(charPart & 0x1F);
          char byte2 = (char)(in[count + 1] & 0x3F);

          fullChar = (char)(bigByte << 6);
          fullChar |= byte2;

          if( fullChar < 0xD800 ) // High Surrogates
            sBld.appendChar( fullChar );

          }

        // If it doesn't match the two above it
        // gets ignored.
        }
      }

    StrA result = sBld.toStrA();
    return result;
    }
    catch( Exception e )
      {
      return new StrA( "Error in UTF8Strings.bytesToString(): " + e.getMessage() );
      // return "";
      }
    }



  public static boolean isValidBytesArray( byte[] in )
    {
    try
    {
    if( in == null )
      return false;

    if( in.length == 0 )
      return false;

    if( in[0] == 0 )
      return false;

    final int last = in.length;
    // char fullChar = ' ';
    byte prevByte = 0;
    for( int count = 0; count < last; count++ )
      {
      byte charPart = in[count];
      if( charPart == 0 )
        return false;

      if( (charPart & 0x80) == 0 )
        {
        // It's regular ASCII.
        if( count > 0 )
          {
          // If the previous byte is a beginning byte.
          prevByte = in[count - 1];
          if( (prevByte & 0xC0) == 0xC0 )
            return false;

          }

        continue;
        }

      if( (charPart & 0xC0) == 0x80 )
        {
        // It's a continuing byte.

        // Can't start with a continuing byte.
        if( count == 0 )
          return false;

        // A continuing byte has to follow either
        // a continuing byte or a beginning byte.
        prevByte = in[count - 1];
        if( (prevByte & 0x80) == 0 )
          return false;

        continue;
        }

      if( (charPart & 0xC0) == 0xC0 )
        {
        // It's a beginning byte.
        // A beginning byte is either 110xxxxx or
        // 1110xxxx.
        if( (charPart & 0xF0) == 0xE0 )
          {
          // Starts with 1110xxxx.
          // It's a 3-byte character.
          if( (count + 2) >= last )
            return false;

          char bigByte = (char)(charPart & 0x0F);
          // char byte2 = (char)(in[count + 1] & 0x3F);
          // char byte3 = (char)(in[count + 2] & 0x3F);
          byte byte2 = in[count + 1];
          byte byte3 = in[count + 2];

          // It should be a continuing byte.
          if( (byte2 & 0xC0) == 0 )
            return false;

          if( (byte3 & 0xC0) == 0 )
            return false;


          // fullChar = (char)(bigByte << 12);
          // fullChar |= (char)(byte2 << 6);
          // fullChar |= byte3;
          // if( fullChar < 0xD800 ) // High Surrogates
            // sBld.appendChar( fullChar );

          }

        if( (charPart & 0xE0) == 0xC0 )
          {
          // Starts with 110xxxxx.
          // It's a 2-byte character.
          if( (count + 1) >= last )
            return false;

          // char bigByte = (char)(charPart & 0x1F);
          byte byte2 = in[count + 1]; //  & 0x3F);

          // It should be a continuing byte.
          if( (byte2 & 0xC0) == 0 )
            return false;

          // fullChar = (char)(bigByte << 6);
          // fullChar |= byte2;

          // if( fullChar < 0xD800 ) // High Surrogates
            // sBld.appendChar( fullChar );

          }
        }
      }

    return true;
    }
    catch( Exception e )
      {
      return false;
      }
    }



  }
