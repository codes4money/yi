package org.sipdroid.sipua.record;

import java.util.ArrayList;

public class CallLogs {
	long contactId = 0L;
	  String name = null;
	  String phone = null;
	  ArrayList<CallItem> phoneList;

	  public long getContactID()
	  {
	    return this.contactId;
	  }

	  public String getName()
	  {
	    return this.name;
	  }

	  public String getPhone()
	  {
	    return this.phone;
	  }

	  public ArrayList<CallItem> getPhoneList()
	  {
	    if (this.phoneList == null)
	      this.phoneList = new ArrayList();
	    return this.phoneList;
	  }

	  public void putPhone(CallItem paramCallItem)
	  {
	    if (this.phoneList == null)
	      this.phoneList = new ArrayList();
	    this.phoneList.add(paramCallItem);
	  }

	  public void setName(String paramString)
	  {
	    this.name = paramString;
	  }

	  public void setPhone(String paramString)
	  {
	    this.phone = paramString;
	  }

	  public void setPhoneList(ArrayList<CallItem> paramArrayList)
	  {
	    this.phoneList = paramArrayList;
	  }
}
