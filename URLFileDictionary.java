// Copyright Eric Chauvin 2020.




public class URLFileDictionary
  {
  private MainApp mApp;
  private URLFileDictionaryLine lineArray[];
  private static final int keySize = 0xFFFF + 1;



  private URLFileDictionary()
    {
    }



  public URLFileDictionary( MainApp useApp )
    {
    mApp = useApp;

    lineArray = new URLFileDictionaryLine[keySize];
    }



  public void clear()
    {
    for( int count = 0; count < keySize; count++ )
      lineArray[count] = null;

    }



  private int getIndex( StrA url )
    {
    if( url.length() == 0 )
      return 0;

    int index = url.GetCRC16();
    if( index >= keySize )
      index = keySize - 1;

    return index;
    }



  public void setValue( StrA key, URLFile value )
    {
    try
    {
    if( key == null )
      return;

    key = key.trim().toLowerCase();
    if( key.length() < 1 )
      return;

    int index = getIndex( key );
    if( lineArray[index] == null )
      lineArray[index] = new URLFileDictionaryLine( mApp );

    lineArray[index].setValue( key, value );
    }
    catch( Exception e )
      {
      mApp.showStatusAsync( "Exception in setValue()." );
      mApp.showStatusAsync( e.getMessage() );
      }
    }


/*
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
*/



  public URLFile getValue( StrA key )
    {
    if( key == null )
      return null;

    key = key.trim().toLowerCase();
    if( key.length() < 1 )
      return null;

    int index = getIndex( key );
    if( lineArray[index] == null )
      return null;

    return lineArray[index].getValue( key );
    }


/*
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
*/


/*
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
*/


/*
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
*/



  public boolean keyExists( StrA key )
    {
    if( key == null )
      return false;

    key = key.trim().toLowerCase();
    if( key.length() < 1 )
      return false;

    int index = getIndex( key );
    if( lineArray[index] == null )
      return false;

    return lineArray[index].keyExists( key );
    }



/*
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
