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
package com.xwiki.homework.internal;


import javax.inject.Inject;
import javax.inject.Provider;

import java.util.List;

import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;

import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xwiki.homework.HomeworkManager;


/**
 * @version $Id$
 * @since 1.0
 */
@Component
@Singleton
public class DefaultHomeworkManager implements HomeworkManager
{
	@Inject
    private Provider<XWikiContext> xcontext;

	private XWikiContext xwikiContext;
	private XWiki xwiki;
	private XWikiDocument homework;

    public String getName(DocumentReference homeworkReference)
    {
    	xwikiContext = xcontext.get();
    	xwiki = xwikiContext.getWiki();
    	try {
	    	homework = xwiki.getDocument(homeworkReference, xwikiContext);
	    	
	        List<XWikiAttachment> attachments = homework.getAttachmentList();
	        if (attachments.size() == 1) {
	        	XWikiAttachment attachment = attachments.get(0);
	        	String fileName = attachment.getFilename();
	            return fileName;
	        } else {
	            return "to work on it";
	        }
    	} catch (XWikiException e) {
    		e.printStackTrace();
    	}
    	return "is not working";
    }

}
