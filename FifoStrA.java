// Copyright Eric Chauvin 2020.



public class FifoStrA
  {
  private MainApp mApp;
  private StrA[] values;
  private int first = 0;
  private int last = 0;
  private final int arraySize;


  private FifoStrA()
    {
    arraySize = 0;
    }



  public FifoStrA( MainApp useApp, int setSize )
    {
    mApp = useApp;
    arraySize = setSize;
    values = new StrA[arraySize];
    }

  
  public void setValue( StrA in )
    {
    if( in == null )
      return;

    values[last] = in;
    last++;
    if( last >= arraySize )
      last = 0;

    if( last == first )
      {
      mApp.showStatusAsync( "The Fifo was overrun." );
      return;
      }
    }




  public StrA getValue()
    {
    if( last == first )
      return null; // Nothing in Fifo.

    StrA result = values[first];
    values[first] = null;
    first++;
    if( first >= arraySize )
      first = 0;

    return result;
    }



  }
