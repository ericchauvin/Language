// Copyright Eric Chauvin 2020.



public class URLFileDictionaryLine
  {
  private MainApp mApp;
  private StrA[] keyArray;
  private URLFile[] valueArray;
  // private int[] sortIndexArray;
  private int arrayLast = 0;



  private URLFileDictionaryLine()
    {
    }



  public URLFileDictionaryLine( MainApp appToUse )
    {
    mApp = appToUse;
    keyArray = new StrA[8];
    valueArray = new URLFile[8];
    // sortIndexArray = new int[8];
    // resetSortIndexArray();
    }

/*
  private void resetSortIndexArray()
    {
    // It's not to arrayLast.  It's to the whole length.
    final int max = sortIndexArray.length;
    for( int count = 0; count < max; count++ )
      sortIndexArray[count] = count;

    }
*/


  private void resizeArrays( int toAdd )
    {
    final int max = keyArray.length;
    // sortIndexArray = new int[max + toAdd];
    // resetSortIndexArray();

    StrA[] tempKeyArray = new StrA[max + toAdd];
    URLFile[] tempValueArray = new URLFile[max + toAdd];

    for( int count = 0; count < max; count++ )
      {
      tempKeyArray[count] = keyArray[count];
      tempValueArray[count] = valueArray[count];
      }

    keyArray = tempKeyArray;
    valueArray = tempValueArray;
    }


/*
  public void sort()
    {
    if( arrayLast < 2 )
      return;

    for( int count = 0; count < arrayLast; count++ )
      {
      if( !bubbleSortOnePass() )
        break;

      }
    }
*/



/*
  private boolean bubbleSortOnePass()
    {
    // This returns true if it swaps anything.

    boolean switched = false;
    for( int count = 0; count < (arrayLast - 1); count++ )
      {
      // compareTo() uses case.
      if( keyArray[count].compareToIgnoreCase(
                              keyArray[count + 1] ) > 0 )
        {
        int temp = sortIndexArray[count];
        sortIndexArray[count] = sortIndexArray[count + 1];
        sortIndexArray[count + 1] = temp;
        switched = true;
        }
      }

    return switched;
    }
*/


  private int getIndexOfKey( StrA key )
    {
    if( arrayLast < 1 )
      return -1;

    for( int count = 0; count < arrayLast; count++ )
      {
      if( keyArray[count].equalTo( key ))
        return count;

      }

    return -1;
    }



  public void setValue( StrA key, URLFile value )
    {
    // This sets the URLFile to the new value whether
    // it's already there or not.
    int index = getIndexOfKey( key );
    if( index >= 0 )
      {
      valueArray[index] = value;
      }
    else
      {
      if( arrayLast >= sortIndexArray.length )
        resizeArrays( 1024 * 64 );

      // mApp.showStatusAsync( key );
      keyArray[arrayLast] = key;
      valueArray[arrayLast] = value;
      arrayLast++;
      }
    }


/*
  public void setMacroEnabled( StrA key,
                               boolean setTo )
    {
    int index = getIndexOfKey( key );
    if( index >= 0 )
      {
      valueArray[index].setEnabled( setTo );
      }
    }
*/



  public URLFile getValue( StrA key )
    {
    int index = getIndexOfKey( key );
    if( index < 0 )
      return null;

    return valueArray[index];
    }



/*
  public boolean getMacroEnabled( StrA key )
    {
    int index = getIndexOfKey( key );
    if( index < 0 )
      return false;

    return valueArray[index].getEnabled();
    }
*/



  public boolean keyExists( StrA key )
    {
    int index = getIndexOfKey( key );
    if( index < 0 )
      return false;

    return true;

    // Being disabled means it doesn't exist.    
    // return valueArray[index].getEnabled();
    }



/*
  public StrA makeKeysValuesString()
    {
    if( arrayLast < 1 )
      return "";

    StringBuilder sBuilder = new StringBuilder();

    for( int count = 0; count < arrayLast; count++ )
      {
      // Using the sortIndexArray for the sorted order.
      String oneLine = keyArray[sortIndexArray[count]] +
                       "\t" +
                       valueArray[sortIndexArray[count]] +
                       "\n";

      sBuilder.append( oneLine );
      }

    return sBuilder.toString();
    }
*/


  }
