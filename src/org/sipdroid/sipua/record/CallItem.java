package org.sipdroid.sipua.record;
import java.util.Date;

public class CallItem {
	int callTpye = 0;
	  Date date = null;
	  long duration = 0L;
	  long id = 0L;

	  public int getCallTpye()
	  {
	    return this.callTpye;
	  }

	  public Date getDate()
	  {
	    return this.date;
	  }

	  public long getDuration()
	  {
	    return this.duration;
	  }

	  public long getId()
	  {
	    return this.id;
	  }

	  public void setCallTpye(int paramInt)
	  {
	    this.callTpye = paramInt;
	  }

	  public void setDate(Date paramDate)
	  {
	    this.date = paramDate;
	  }

	  public void setDuration(long paramLong)
	  {
	    this.duration = paramLong;
	  }

	  public void setId(long paramLong)
	  {
	    this.id = paramLong;
	  }
}
