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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
public class Homework
{
	private DocumentReference docRef;
	private XWikiContext xwikiContext;
	
	private XWikiDocument homeworkDoc;
	private BaseObject homeworkObj;
	private Calendar deadline;
	
	public Homework(XWikiContext xwikiContext, DocumentReference docRef) {		
		this.docRef=docRef;
		this.xwikiContext=xwikiContext;
	}
	
	@SuppressWarnings("deprecation")
	public void setDeadline() {
		XWiki xwiki = xwikiContext.getWiki();
		String deadlineString;
		
		try {
			homeworkDoc = xwiki.getDocument(docRef, xwikiContext);
			homeworkObj = homeworkDoc.getObject("Homework.Code.HomeworkClass", 0);
			deadlineString = homeworkObj.getStringValue("deadline");
			
			SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
			Date date = sdf.parse(deadlineString);
			deadline = Calendar.getInstance();
			deadline.setTime(date);			
		} catch (XWikiException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public Boolean isBeforeDeadline() {
		setDeadline();
		Calendar today = Calendar.getInstance();
		Date todayD = today.getTime();
		Date deadlineD = deadline.getTime();
		
		if(todayD.before(deadlineD))
			return true;
		return false;
	}
	
	
	
}
