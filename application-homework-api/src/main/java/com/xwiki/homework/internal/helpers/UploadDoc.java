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

import java.util.Arrays;
import java.util.List;

import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.LocalDocumentReference;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;



/**
 * @version $Id$
 * @since 1.0
 */
public class UploadDoc
{
	private DocumentReference docRef;
	private XWikiContext xwikiContext;

	private XWikiDocument uploadDoc;
	private BaseObject uploadObj;
	private String lastUploadName;
	private List<String> authors;
	private String attachmentName;
	
	public static final LocalDocumentReference UPLOAD =
	        new LocalDocumentReference(Arrays.asList("Homework","Code"),"UploadClass");
	public static final LocalDocumentReference RIGHTS =
	        new LocalDocumentReference("XWiki","XWikiRights");

	public UploadDoc(XWikiContext xwikiContext, DocumentReference docRef) {		
		this.docRef=docRef;
		this.xwikiContext=xwikiContext;
	}

	public void setLastUploadName() {
		XWiki xwiki = xwikiContext.getWiki();

		try {
			uploadDoc = xwiki.getDocument(docRef, xwikiContext);
			uploadObj = uploadDoc.getXObject(UPLOAD, 0);
			this.lastUploadName = uploadObj.getStringValue("lastUploadName");
		} catch (XWikiException e) {
			e.printStackTrace();
		}
	}

	public String getLastUploadName() {
		setLastUploadName();
		return this.lastUploadName;
	}
	
	public void setAuthors() {
		XWiki xwiki = xwikiContext.getWiki();

		try {
			uploadDoc = xwiki.getDocument(docRef, xwikiContext);
			uploadObj = uploadDoc.getXObject(UPLOAD, 0);
			this.authors = Arrays.asList(uploadObj.getStringValue("authors").split(","));
		} catch (XWikiException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getAuthors() {
		this.setAuthors();
		return this.authors;
	}

	public void setAttachmentName() {
		XWiki xwiki = xwikiContext.getWiki();
		Student student;

		try {
			uploadDoc = xwiki.getDocument(docRef, xwikiContext);
			uploadObj = uploadDoc.getXObject(UPLOAD, 0);
			this.authors = Arrays.asList(uploadObj.getStringValue("authors").split(","));
			this.attachmentName = "";

			for(String author : this.authors) {
				String authorName = author.split("\\.")[1];
				student = new Student(xwikiContext, new DocumentReference(xwikiContext.getWikiId(), "XWiki", authorName));
				this.attachmentName = attachmentName + student.getAttachmentName() + '-';
			}

		} catch (XWikiException e) {
			e.printStackTrace();
		}
	}

	public String getAttachmentName() {
		setAttachmentName();
		return this.attachmentName;
	}

	public Boolean hasObject(LocalDocumentReference OBJECT) {
		XWiki xwiki = xwikiContext.getWiki();

		try {
			uploadDoc = xwiki.getDocument(docRef, xwikiContext);
			uploadObj = uploadDoc.getXObject(OBJECT, 0);
			if(uploadObj != null) {
				return true;
			}
		} catch (XWikiException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void updateRights() {
		XWiki xwiki = xwikiContext.getWiki();
		BaseObject rightsObject;

		try {
			uploadDoc = xwiki.getDocument(docRef, xwikiContext);
			if(this.hasObject(RIGHTS) && this.hasObject(UPLOAD)) {
				rightsObject = uploadDoc.getXObject(RIGHTS, 0);
				String users = rightsObject.getStringValue("users");

				this.setAuthors();
				List<String> usersList = Arrays.asList(users.split(","));

				for(String author : authors) {
					if(!usersList.contains(author)) {
						rightsObject.set("users", users + "," + author, xwikiContext);
					}
				}
				xwiki.saveDocument(uploadDoc, xwikiContext);
//				TODO: when a user is deleted from author remove his rights?
//				for(String user : usersList) {
//					if(!authors.contains(user)) {
//						usersList.remove(user);
//						rightsObject.set("users", usersList.toString(), xwikiContext);
//						System.out.println("inside  " + usersList);
//					}
//				}
			}
		} catch (XWikiException e) {
			e.printStackTrace();
		}
	}
}
