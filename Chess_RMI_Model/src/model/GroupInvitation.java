package model;

import java.io.Serializable;

public class GroupInvitation implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int id;
	private int inviteBy;
	private int groupMemberId;
	
	
	
	public GroupInvitation() {
		super();
	}
	public GroupInvitation(int id, int inviteBy, int groupMemberId) {
		super();
		this.id = id;
		this.inviteBy = inviteBy;
		this.groupMemberId = groupMemberId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getInviteBy() {
		return inviteBy;
	}
	public void setInviteBy(int inviteBy) {
		this.inviteBy = inviteBy;
	}
	public int getGroupMemberId() {
		return groupMemberId;
	}
	public void setGroupMemberId(int groupMemberId) {
		this.groupMemberId = groupMemberId;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
