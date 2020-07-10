// Copyright Eric Chauvin 2019 - 2020.



public class URLFileDictionaryLine
  {
  private MainApp mApp;
  private StrA[] keyArray;
  private URLFile[] valueArray;
  private int[] sortIndexArray;
  private int arrayLast = 0;



  private URLFileDictionaryLine()
    {
    }



  public URLFileDictionaryLine( MainApp appToUse )
    {
    mApp = appToUse;
    keyArray = new StrA[8];
    valueArray = new URLFile[8];
    sortIndexArray = new int[8];
    resetSortIndexArray();
    }


  private void resetSortIndexArray()
    {
    // It's not to arrayLast.  It's to the whole length.
    final int max = sortIndexArray.length;
    for( int count = 0; count < max; count++ )
      sortIndexArray[count] = count;

    }


/*
  private void resizeArrays( int toAdd )
    {
    int max = sortIndexArray.length;
    sortIndexArray = new int[max + toAdd];
    resetSortIndexArray();

    StrA[] tempKeyArray = new StrA[max + toAdd];
    Macro[] tempValueArray = new Macro[max + toAdd];

    for( int count = 0; count < max; count++ )
      {
      tempKeyArray[count] = keyArray[count];
      tempValueArray[count] = valueArray[count];
      }

    keyArray = tempKeyArray;
    valueArray = tempValueArray;
    }



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



  public void setMacro( StrA key, Macro value )
    {
    // This sets the macro to the new value whether
    // it's already there or not, and whether it's
    // enabled or not.
    int index = getIndexOfKey( key );
    if( index >= 0 )
      {
      // mApp.showStatusAsync( "Existing macro: " + key );
      valueArray[index] = value;
      }
    else
      {
      if( arrayLast >= sortIndexArray.length )
        resizeArrays( 1024 * 4 );

      // mApp.showStatusAsync( "New macro: " + key );
      keyArray[arrayLast] = key;
      valueArray[arrayLast] = value;
      arrayLast++;
      }
    }



  public void setMacroEnabled( StrA key,
                               boolean setTo )
    {
    int index = getIndexOfKey( key );
    if( index >= 0 )
      {
      valueArray[index].setEnabled( setTo );
      }
    }



  public Macro getMacro( StrA key )
    {
    int index = getIndexOfKey( key );
    if( index < 0 )
      return null;

    return valueArray[index];
    }




  public boolean getMacroEnabled( StrA key )
    {
    int index = getIndexOfKey( key );
    if( index < 0 )
      return false;

    return valueArray[index].getEnabled();
    }



  public boolean keyExists( StrA key )
    {
    int index = getIndexOfKey( key );
    if( index < 0 )
      return false;

    // Being disabled means it doesn't exist.    
    return valueArray[index].getEnabled();
    }



/////////
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
