package org.jab.model.contact;

import java.util.ArrayList;
import java.util.List;

public class RosterContactGroup {
	
	private List<RosterContact> rosterContactGroup;
	
	public RosterContactGroup(List<RosterContact> rosterContactGroup) {
		
		this.rosterContactGroup = rosterContactGroup;
	}
	
	public RosterContactGroup() {
		
		this.rosterContactGroup = new ArrayList<RosterContact>();
	}
	
	public void addRosterContact(RosterContact rc){
		
		rosterContactGroup.add(rc);
	}

}
