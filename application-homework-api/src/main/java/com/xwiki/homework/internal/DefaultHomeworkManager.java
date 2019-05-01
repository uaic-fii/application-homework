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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;

import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.web.XWikiResponse;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xwiki.homework.HomeworkManager;
import com.xwiki.homework.internal.helpers.Student;

import org.apache.commons.io.IOUtils;


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
	public String homeworkAuthor;

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
	            return "rd";
	        }
    	} catch (XWikiException e) {
    		e.printStackTrace();
    	}
    	return "is not working";
    }
	
	public void downloadAllAttachments(DocumentReference homeworkReference) {
		xwikiContext = xcontext.get();
        xwiki = xwikiContext.getWiki();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        final int BUFFER = 2048;
        byte buffer[] = new byte[BUFFER];

        try {
            homework = xwiki.getDocument(homeworkReference, xwikiContext);

            List<XWikiAttachment> attachments = homework.getAttachmentList();

            for(int i=0; i<attachments.size(); i++) {
                XWikiAttachment attachment = attachments.get(i);

                // Get the author of the attachment.
                homeworkAuthor = attachment.getAuthorReference().getName();
                Student student = new Student(xwikiContext, new DocumentReference(xwikiContext.getWikiId(), "XWiki", homeworkAuthor));

                // Create the entry
                ZipEntry ze= new ZipEntry(student.getAttachmentName());
                zos.putNextEntry(ze);

                // Write the content.
                InputStream inputStream = attachment.getContentInputStream(xwikiContext);
                int length;
                while ((length = inputStream.read(buffer)) >= 0) {
                   zos.write(buffer, 0, length);
	            }

                // Close the entry.
                zos.closeEntry();
            }

            zos.close();

            // Add the archive to an attachment.
            XWikiAttachment attachmentZip = new XWikiAttachment();
            attachmentZip.setContent(new ByteArrayInputStream(baos.toByteArray()));
            attachmentZip.setFilename(homeworkReference.getName() + ".zip");

            // Download the attachment.
            download(xwikiContext, attachmentZip);

            baos.close();
        } catch (XWikiException e) {
            e.printStackTrace();
        } catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void download(XWikiContext xwikiContext, XWikiAttachment attachment) {
		InputStream stream = null;
        try {
            XWikiResponse response = xwikiContext.getResponse();

            response.addHeader("Content-disposition", "attachment;filename=\"" + attachment.getFilename() + "\"");
            response.setDateHeader("Last-Modified", attachment.getDate().getTime());
            response.setContentType("application/download");

            response.setHeader("Accept-Ranges", "bytes");
            response.setContentLength((int) attachment. getContentLongSize(xwikiContext));

            stream = attachment.getContentInputStream(xwikiContext);
            IOUtils.copy(stream, response.getOutputStream());
            response.flushBuffer();
        }
        catch (IOException ignored) { }
        catch (XWikiException ignored) { }
        finally {
            if (stream != null) {
                IOUtils.closeQuietly(stream);
            }
        }
	}
	
}
