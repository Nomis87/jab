package org.jab.model.contact;

import java.util.ArrayList;
import java.util.List;

public class RosterContactGroup {
	
	private String groupName;
	private List<RosterContact> groupContacts;
	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public RosterContactGroup(List<RosterContact> rosterContactGroup) {
		
		this.groupContacts = rosterContactGroup;
	}
	
	public RosterContactGroup() {
		
		this.groupContacts = new ArrayList<RosterContact>();
	}
	
	public void addRosterContact(RosterContact rc){
		
		groupContacts.add(rc);
	}
	
	public List<RosterContact> getGroupContacts(){
		
		return this.groupContacts;
	}

}
