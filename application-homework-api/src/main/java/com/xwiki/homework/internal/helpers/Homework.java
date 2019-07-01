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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.LocalDocumentReference;
import org.xwiki.model.reference.SpaceReference;

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
	private String groups;
	public static final LocalDocumentReference MARK =
	        new LocalDocumentReference(Arrays.asList("Homework","Code"),"MarkClass");
	public static final LocalDocumentReference HOMEWORK =
	        new LocalDocumentReference(Arrays.asList("Homework","Code"),"HomeworkClass");
	public static final LocalDocumentReference UPLOAD =
	        new LocalDocumentReference(Arrays.asList("Homework","Code"),"UploadClass");


	public Homework(XWikiContext xwikiContext, DocumentReference docRef) {		
		this.docRef=docRef;
		this.xwikiContext=xwikiContext;
	}

	public Map<String, Object[]>  setExcelData() {
		Student student;
		XWiki xwiki = xwikiContext.getWiki();
		Map<String, Object[]> data = new TreeMap<String, Object[]>();
		data.put("1", new Object[]{ "Student", "Group", "Mark" });
		
		try {
			homeworkDoc = xwiki.getDocument(docRef, xwikiContext);
			
			List<BaseObject> markObjects = homeworkDoc.getXObjects(MARK);
			if (markObjects != null) {
	            for (BaseObject object : markObjects) {
	                if (object == null) {
	                    continue;
	                }
	                String studentL = object.getStringValue("student");
	                student = new Student(xwikiContext,
                            new DocumentReference(xwikiContext.getWikiId(), "XWiki", studentL));
	                String group = object.getStringValue("group");
					String mark = object.getStringValue("mark");
					data.put(String.valueOf(object.getNumber()+2),
							new Object[]{ student.getFullName(), group, mark });
	            }
			}
			
		
		} catch (XWikiException e) {
			e.printStackTrace();
		}
		return data;
	}

	public void setDeadline() {
		XWiki xwiki = xwikiContext.getWiki();
		String deadlineString;
		
		try {
			homeworkDoc = xwiki.getDocument(docRef, xwikiContext);
			homeworkObj = homeworkDoc.getXObject(HOMEWORK, 0);
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

	public void setGroups() {
		XWiki xwiki = xwikiContext.getWiki();
		
		try {
			homeworkDoc = xwiki.getDocument(docRef, xwikiContext);
			homeworkObj = homeworkDoc.getXObject(HOMEWORK, 0);
			this.groups = homeworkObj.getStringValue("groups");	
		} catch (XWikiException e) {
			e.printStackTrace();
		}
	}

	public String getGroups() {
		this.setGroups();
		return this.groups;
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

	public String getName() {
		String name = docRef.getParent().getName();
		return name;
	}
	
	public void addMarks(List<String> members) {
		XWiki xwiki = xwikiContext.getWiki();
		BaseObject markObj;
		Student student;
		int count;
		try {
			homeworkDoc = xwiki.getDocument(docRef, xwikiContext);
			List<BaseObject> markObjects = homeworkDoc.getXObjects(MARK);

			List<String> studentsMarks = new ArrayList<String>();
            if (markObjects != null) {
	            for (BaseObject object : markObjects) {
	                if (object == null) {
	                    continue;
	                }
	                studentsMarks.add("XWiki." + object.getStringValue("student"));
	            }
			}

			markObj = homeworkDoc.getXObject(MARK, false, xwikiContext);
			if(markObj == null) {
				markObj = homeworkDoc.getXObject(MARK, true, xwikiContext);
				count = 0;
			} else {
				count = homeworkDoc.getXObjectSize(MARK);
			}

			for(String member : members) {
				if(!studentsMarks.contains(member)) {
					markObj = homeworkDoc.getXObject(MARK, count, true, xwikiContext);
					markObj.setNumber(count);
					student = new Student(xwikiContext,
                            new DocumentReference(xwikiContext.getWikiId(), "XWiki", member.split("\\.")[1]));
					if(student.isStudent()) {
						markObj.set("student", student.getUsername(), xwikiContext);
						markObj.set("group", student.getGroup(), xwikiContext);
						markObj.set("mark", "0.0", xwikiContext);
						count++;
						studentsMarks.add(member);
						xwiki.saveDocument(homeworkDoc, xwikiContext);
					}
				}
			}
		} catch (XWikiException e) {
			e.printStackTrace();
		}
	}

	public DocumentReference getNextUploadDoc(List<DocumentReference> childReferences) {
		int randomNum =  (int)(Math.random() * 10000);
		String name = "Homework" + randomNum;
		DocumentReference newUploadDocRef = new DocumentReference(name, docRef.getLastSpaceReference());

		if(childReferences.contains(newUploadDocRef)) {
			getNextUploadDoc(childReferences);
		}
		return newUploadDocRef;
	}

	public String getNextUploadDocName() {
		int randomNum =  (int)(Math.random() * 10000);
		return "Homework" + randomNum;
	}

	public DocumentReference getUploadDocName(String student) {
		XWiki xwiki = xwikiContext.getWiki();
		UploadDoc uploadDoc;
		List<String> authors;

		try {
			homeworkDoc = xwiki.getDocument(docRef, xwikiContext);
			
			DocumentReference parentRef = new DocumentReference(
					docRef.getLastSpaceReference().getName(),
					new SpaceReference(docRef.getLastSpaceReference().getParent()));

			XWikiDocument parentDoc = xwiki.getDocument(parentRef, xwikiContext);

			List<DocumentReference> childReferences = parentDoc.getChildrenReferences(xwikiContext);

			for(int i=0; i<childReferences.size(); i++) {
				uploadDoc = new UploadDoc(xwikiContext, childReferences.get(i));
				if(uploadDoc.hasObject(UPLOAD)) {
					authors = uploadDoc.getAuthors();
					if(authors.contains(student)) {
						return childReferences.get(i);
					}
				}
             }

			DocumentReference newUploadDoc = getNextUploadDoc(childReferences);
			return newUploadDoc;
		} catch (XWikiException e) {
			e.printStackTrace();
		}

		DocumentReference newUploadDoc = new DocumentReference(getNextUploadDocName(), docRef.getLastSpaceReference());
		return newUploadDoc;
	}

	public Boolean hasObject(LocalDocumentReference OBJECT) {
		XWiki xwiki = xwikiContext.getWiki();

		try {
			homeworkDoc = xwiki.getDocument(docRef, xwikiContext);
			homeworkObj = homeworkDoc.getXObject(OBJECT, 0);
			if(homeworkObj != null) {
				return true;
			}
		} catch (XWikiException e) {
			e.printStackTrace();
		}
		return false;
	}
}