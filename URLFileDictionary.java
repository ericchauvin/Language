// Copyright Eric Chauvin 2019 - 2020.




public class URLFileDictionary
  {
  private MainApp mApp;
  private URLFileDictionaryLine lineArray[];

// ====== See notes below.
// The URLFile object has file name, url, etc.

  private static final int maxIndexLetter =
                                        'z' - 'a' + 2;
  private static final int keySize =
                             ((maxIndexLetter << 6) |
                              maxIndexLetter);




  private URLFileDictionary()
    {
    }



  public URLFileDictionary( MainApp useApp )
    {
    mApp = useApp;

    // mApp.showStatusAsync( "maxIndexLetter for macros: " + maxIndexLetter );
    // int index = ('z' + 1) - 'a';
    // mApp.showStatusAsync( "Index for z is: " + index );
    // Underscore is decimal 137 or 0x5F.
    // mApp.showStatusAsync( "Underscore: " + (char)0x5F );

    lineArray = new URLFileDictionaryLine[keySize];
    }



  public void clear()
    {
    for( int count = 0; count < keySize; count++ )
      lineArray[count] = null;

    }


/*
Fix up the CRC thing in StrA.java in CPreprocessor
and then copy it to here to use.  Use the CRC as
part of the index.


  private int letterToIndexNumber( char letter )
    {
    // This is case-insensitive so that it sorts
    // names in' a case-insensitive way.

    letter = Character.toLowerCase( letter );

    // The underscore is used in a lot of macros
    // and it is sorted before the letter A.  So
    // it will be at index 0, and with the plus
    // one, the letter A is at index 1.

    if( letter == '_' )
      return 0;

    int index = (letter + 1) - 'a';
    if( index < 0 )
      index = 0;

    // A letter that's way up there in Unicode, like
    // a Chinese character, would be given the value
    // maxindexletter - 1.
    if( index >= maxIndexLetter )
      index = maxIndexLetter - 1;

    return index;
    }



  private boolean isBadKey( StrA key )
    {
    // C++ named operators.
    if( key.equalTo( new StrA( "and" )))
      return true;

    if( key.equalTo( new StrA( "and_eq" )))
      return true;

    if( key.equalTo( new StrA( "bitand" )))
      return true;

    if( key.equalTo( new StrA( "bitor" )))
      return true;

    if( key.equalTo( new StrA( "compl" )))
      return true;

    if( key.equalTo( new StrA( "not" )))
      return true;

    if( key.equalTo( new StrA( "not_eq" )))
      return true;

    if( key.equalTo( new StrA( "or" )))
      return true;

    if( key.equalTo( new StrA( "or_eq" )))
      return true;

    if( key.equalTo( new StrA( "xor" )))
      return true;

    return false;
    }



  private int getIndex( StrA key )
    {
    // This index needs to be in sorted order.

    int keyLength = key.length();
    if( keyLength < 1 )
      return 0;

    // The letter z by itself would be sorted before
    // the two letters az unless the space character
    // is added.
    if( keyLength == 1 )
      key = key.concat( new StrA( " " ));

    int one = letterToIndexNumber( key.charAt( 0 ));
    int tempTwo = letterToIndexNumber( key.charAt( 1 ));
    int two = (one << 6) | tempTwo;

    if( two >= keySize )
      two = keySize - 1;

    return two;
    }



  public void setMacro( StrA key, Macro value )
    {
    try
    {
    if( key == null )
      return;

    key = key.trim();
    if( key.length() < 1 )
      return;

    if( isBadKey( key ))
      {
      mApp.showStatusAsync( "This can't be used as a key: " + key );
      return;
      }

    int index = getIndex( key );
    if( lineArray[index] == null )
      lineArray[index] = new MacroDictionaryLine( mApp );

    lineArray[index].setMacro( key, value );
    }
    catch( Exception e )
      {
      mApp.showStatusAsync( "Exception in setMacro()." );
      mApp.showStatusAsync( e.getMessage() );
      }
    }



  public void setMacroEnabled( StrA key,
                               boolean setTo )
    {
    try
    {
    if( key == null )
      return;

    key = key.trim();
    if( key.length() < 1 )
      return;

    int index = getIndex( key );

    if( lineArray[index] == null )
      return;

    lineArray[index].setMacroEnabled( key, setTo );

    }
    catch( Exception e )
      {
      mApp.showStatusAsync( "Exception in setMacroEnabled()." );
      mApp.showStatusAsync( e.getMessage() );
      }
    }



  public Macro getMacro( StrA key )
    {
    if( key == null )
      return null;

    key = key.trim();
    if( key.length() < 1 )
      return null;

    int index = getIndex( key );
    if( lineArray[index] == null )
      return null;

    return lineArray[index].getMacro( key );
    }



  public boolean getMacroEnabled( StrA key )
    {
    if( key == null )
      return false;

    key = key.trim();
    if( key.length() < 1 )
      return false;

    int index = getIndex( key );
    if( lineArray[index] == null )
      return false;

    return lineArray[index].getMacroEnabled( key );
    }



  public void sort()
    {
    // This is a Library Sort mixed with a Bubble
    // Sort for each line in the array.
    for( int count = 0; count < keySize; count++ )
      {
      if( lineArray[count] == null )
        continue;

      lineArray[count].sort();
      }
    }


/////
  public StrA makeKeysValuesStrA()
    {
    try
    {
    sort();
  
    StringBuilder sBuilder = new StringBuilder();

    for( int count = 0; count < keySize; count++ )
      {
      if( lineArray[count] == null )
        continue;

      sBuilder.append( lineArray[count].
                            makeKeysValuesString());

      }

    return sBuilder.toString();

    }
    catch( Exception e )
      {
      mApp.showStatus( "Exception in MacroDictionary.makeKeysValuesString():\n" );
      mApp.showStatus( e.getMessage() );
      return "";
      }
    }
////////



  public boolean keyExists( StrA key )
    {
    if( key == null )
      return false;

    key = key.trim();
    if( key.length() < 1 )
      return false;

    int index = getIndex( key );
    if( lineArray[index] == null )
      return false;

    return lineArray[index].keyExists( key );
    }



  public boolean setNewMacro( boolean dostrict,
                              StrA key,
                              Macro macro )
    {
    if( dostrict )
      {
      if( keyExists( key ))
        {
        mApp.showStatusAsync( "Macro key already exists: " + key );
        mApp.showStatusAsync( "markedUpS: " + macro.getMarkedUpS() );
        Macro showMac = getMacro( key );
        mApp.showStatusAsync( "Original markedUpS: " +
                            showMac.getMarkedUpS());
        return false;
        }
      }

    setMacro( key, macro );
    // mApp.showStatusAsync( " " );
    // mApp.showStatusAsync( "Setting new key: " + key );
    // mApp.showStatusAsync( "markedUpString: " + markedUpString );
    // mApp.showStatusAsync( " " );
    return true;
    }

*/
  }
