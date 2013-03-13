package org.jab.model.contact;

import java.util.ArrayList;
import java.util.List;

public class RosterContactGroup {
	
	private String id;
	private String groupName;
	private List<RosterContact> groupContacts;
	
	
	public RosterContactGroup(List<RosterContact> rosterContactGroup) {
		
		this.groupContacts = rosterContactGroup;
	}
	
	public RosterContactGroup() {
		
		this.groupContacts = new ArrayList<RosterContact>();
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	
	public void addRosterContact(RosterContact rc){
		
		groupContacts.add(rc);
	}
	
	public List<RosterContact> getGroupContacts(){
		
		return this.groupContacts;
	}

}
