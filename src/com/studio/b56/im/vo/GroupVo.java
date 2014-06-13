package com.studio.b56.im.vo;
import java.io.Serializable;
import com.studio.b56.im.command.TextdescTool;

import android.text.TextUtils;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;
@Table(name = "grouplist")
public class GroupVo implements Serializable{
	
	@Id(column="id")
	private int id;
	private String uid;
	private String groupid;
	private String groupname;
	private String groupdes; 
	private String role;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUid() {
		return uid;
	}

	public void setUid(String Uid1) {
		this.uid = Uid1;
	}
	
	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String GroupId1) {
		this.groupid = GroupId1;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname1) {
		this.groupname = groupname1;
	}

	public String getGroupdes() {
		return groupdes;
	}
	public void setGroupdes(String groupdes1) {
		this.groupdes = groupdes1;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role1) {
		this.role = role1;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupVo other = (GroupVo) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public GroupVo clone() {
		try {
			return (GroupVo) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

}
