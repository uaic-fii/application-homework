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
public class UploadDoc
{
	private DocumentReference docRef;
	private XWikiContext xwikiContext;

	private XWikiDocument uploadDoc;
	private BaseObject uploadObj;
	private String lastUploadName;
	private String authors;
	private String attachmentName;

	public UploadDoc(XWikiContext xwikiContext, DocumentReference docRef) {		
		this.docRef=docRef;
		this.xwikiContext=xwikiContext;
	}

	@SuppressWarnings("deprecation")
	public void setLastUploadName() {
		XWiki xwiki = xwikiContext.getWiki();

		try {
			uploadDoc = xwiki.getDocument(docRef, xwikiContext);
			uploadObj = uploadDoc.getObject("Homework.Code.UploadClass", 0);
			this.lastUploadName = uploadObj.getStringValue("lastUploadName");
		} catch (XWikiException e) {
			e.printStackTrace();
		}
	}

	public String getLastUploadName() {
		setLastUploadName();
		return this.lastUploadName;
	}

	@SuppressWarnings("deprecation")
	public void setAttachmentName() {
		XWiki xwiki = xwikiContext.getWiki();
		Student student;

		try {
			uploadDoc = xwiki.getDocument(docRef, xwikiContext);
			uploadObj = uploadDoc.getObject("Homework.Code.UploadClass", 0);
			this.authors = uploadObj.getStringValue("authors");
			String[] listAuthors = authors.split(",");
			this.attachmentName = "";

			for(String author : listAuthors) {
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

}
