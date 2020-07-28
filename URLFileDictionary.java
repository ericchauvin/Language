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

    // mApp.showStatusAsync( "Setting value: " + key );

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



  public StrA makeKeysValuesStrA()
    {
    try
    {
    StrABld sBld = new StrABld( 1024 * 64 );

    for( int count = 0; count < keySize; count++ )
      {
      if( lineArray[count] == null )
        continue;

      StrA lines = lineArray[count].
                                 makeKeysValuesStrA();

      if( lines.length() == 0 )
        continue;

      sBld.appendStrA( lines );
      }

    return sBld.toStrA();
    }
    catch( Exception e )
      {
      mApp.showStatusAsync( "Exception in URLFileDictionary.makeKeysValuesStrA():\n" );
      mApp.showStatusAsync( e.getMessage() );
      return StrA.Empty;
      }
    }



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



  public void saveToFile( StrA fileName )
    {
    StrA fileS = makeKeysValuesStrA();
    FileUtility.writeStrAToFile( mApp,
                                 fileName,
                                 fileS,
                                 false,
                                 true );
    }



  public void readFromFile( StrA fileName )
    {
    clear();

    StrA fileS = FileUtility.readFileToStrA( mApp,
                                 fileName,
                                 false,
                                 true );

    // mApp.showStatusAsync( "Read file: " + fileS );
    StrArray lines = fileS.splitChar( '\n' );
    final int last = lines.length();
    StrA badS = new StrA( "/eedition/" );
    for( int count = 0; count < last; count++ )
      {
      StrA line = lines.getStrAt( count );
      if( line.containsStrA( badS ))
        continue;

      // mApp.showStatusAsync( "line: " + line );
      URLFile uFile = new URLFile( mApp );
      uFile.setFromStrA( line );
      setValue( uFile.getUrl(), uFile );
      }
    }



  }
