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
import java.util.ArrayList;
import java.util.Arrays;
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
import com.xwiki.homework.internal.helpers.Homework;
import com.xwiki.homework.internal.helpers.Student;
import com.xwiki.homework.internal.helpers.UploadDoc;

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
	private UploadDoc uploadDoc;

	public void downloadAllAttachments(DocumentReference homeworkReference) {
		xwikiContext = xcontext.get();
        xwiki = xwikiContext.getWiki();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        final int BUFFER = 2048;
        byte buffer[] = new byte[BUFFER];

        try {
            homework = xwiki.getDocument(homeworkReference, xwikiContext);

            List<DocumentReference> childReferences = homework.getChildrenReferences(xwikiContext);
            List<XWikiAttachment> attachments;
            XWikiAttachment attachment;

            for(int i=0; i<childReferences.size(); i++) {
                XWikiDocument doc = xwiki.getDocument(childReferences.get(i), xwikiContext);
                uploadDoc = new UploadDoc(xwikiContext, childReferences.get(i));

                attachments = doc.getAttachmentList();

                for(int j=0; j<attachments.size(); j++) {
                    attachment = attachments.get(j);

                    if(attachment.getFilename().compareTo(uploadDoc.getLastUploadName()) == 0) {

                        // Create the entry
                        ZipEntry ze= new ZipEntry(uploadDoc.getAttachmentName());
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
                }
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
	
	public Boolean isBeforeDeadline(DocumentReference homeworkReference) {
		xwikiContext = xcontext.get();
		
		Homework home = new Homework(xwikiContext, homeworkReference);
        return home.isBeforeDeadline();
	}
	
}
