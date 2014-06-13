package org.sipdroid.sipua.contact;

public class ContactEntity {
	static final String DEFAULT_SORT_KEY = "Z";
	  long contactId = 0L;
	  String name = "";

	  public long getId()
	  {
	    return this.contactId;
	  }

	  public String getName()
	  {
	    return this.name;
	  }

	  public void setId(long paramLong)
	  {
	    this.contactId = paramLong;
	  }

	  public void setName(String paramString)
	  {
	    this.name = paramString;
	  }
}
