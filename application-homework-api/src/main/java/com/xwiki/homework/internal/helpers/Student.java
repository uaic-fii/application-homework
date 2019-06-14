/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.xwiki.homework.internal.helpers;

import org.xwiki.model.reference.DocumentReference;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;


/**
 * @version $Id$
 * @since 1.0
 */
public class Student
{
	private XWikiDocument studentDoc;
	private BaseObject studentObj;
	private BaseObject userObj;
	
	private String firstName;
	private String lastName;
	private String group;
	
	Student(String firstName, String lastName, String group) {
		this.firstName=firstName;
		this.lastName=lastName;
		this.group=group;
	}
	
	@SuppressWarnings("deprecation")
	public Student(XWikiContext xwikiContext, DocumentReference document) {
    	try {
        	XWiki xwiki = xwikiContext.getWiki();
    		
    		studentDoc = xwiki.getDocument(document, xwikiContext);
    		studentObj = studentDoc.getObject("Structure.Code.StudentClass", 0);
    		userObj = studentDoc.getObject("XWiki.XWikiUsers", 0);
            if(studentObj != null) {
                this.setInformations();
            }
    	} catch (XWikiException e) {
    		e.printStackTrace();
    	}
	}
	
	public void setInformations() {
		this.setGroup();
		this.setFirstName();
		this.setLastName();
	}
	
	public void getInformations() {
		System.out.println(this.getGroup() + ' ' + this.getFirstName() + ' ' + this.getLastName());
	}
	
	public void setGroup() {
		this.group = studentObj.getStringValue("group");
	}

	public String getGroup() {
		return group;
	}
	
	public void setFirstName() {
		this.firstName = userObj.getStringValue("first_name");
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setLastName() {
		this.lastName = userObj.getStringValue("last_name");
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getFullName() {
		return firstName + ' ' + lastName;
	}
	
	public String getUsername() {
		return firstName + lastName;
	}
	
	public String getAttachmentName() {
		String atachName = this.lastName+this.firstName+'_'+this.group;
		return atachName;
	}
	
	public Boolean isStudent() {
		if(studentObj == null)
			return false;
		return true;
	}
	
}
