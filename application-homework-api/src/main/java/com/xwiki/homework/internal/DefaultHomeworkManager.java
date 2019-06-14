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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
import com.xwiki.homework.internal.helpers.UploadDoc;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


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
	
	private ZipOutputStream getZOS(DocumentReference docRef, ZipOutputStream zos) throws XWikiException, IOException {
		XWikiDocument doc = xwiki.getDocument(docRef, xwikiContext);
        uploadDoc = new UploadDoc(xwikiContext, docRef);
        final int BUFFER = 2048;
        byte buffer[] = new byte[BUFFER];
        List<XWikiAttachment> attachments;

        attachments = doc.getAttachmentList();
        XWikiAttachment attachment;

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
        return zos;
	}

	public void downloadAllAttachments(DocumentReference homeworkReference, String student) {
		xwikiContext = xcontext.get();
        xwiki = xwikiContext.getWiki();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        try {
            homework = xwiki.getDocument(homeworkReference, xwikiContext);

            List<DocumentReference> childReferences = homework.getChildrenReferences(xwikiContext);
            for(int i=0; i<childReferences.size(); i++) {
                if(!student.isEmpty()) {
                    if (childReferences.get(i).getName().compareTo(student)==0) {
                        zos = getZOS(childReferences.get(i), zos);
                    }
                } else {
                    zos = getZOS(childReferences.get(i), zos);
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
	
	@SuppressWarnings("deprecation")
	public void export(DocumentReference homeworkReference) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("student Details");
        xwikiContext = xcontext.get();
        xwiki = xwikiContext.getWiki();

        try {
			Homework home = new Homework(xwikiContext, homeworkReference);
			
			// Get dates.
	        Map<String, Object[]> data = new TreeMap<String, Object[]>();
	        data = home.setExcelData();

	        // Iterate over data and write to sheet.
	        Set<String> keyset = data.keySet();
	        int rownum = 0;
	        for (String key : keyset) {
	            Row row = sheet.createRow(rownum++);
	            Object[] objArr = data.get(key);
	            int cellnum = 0;
	            for (Object obj : objArr) {
	                Cell cell = row.createCell(cellnum++);
	                if (obj instanceof String)
	                    cell.setCellValue((String)obj);
	                else if (obj instanceof Integer)
	                    cell.setCellValue((Integer)obj);
	            }
	        }
            FileOutputStream out = new FileOutputStream(new File(home.getName()));
            workbook.write(out);
            out.close();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            byte[] xls = baos.toByteArray();
            workbook.close();

            // Add the archive to an attachment.
            XWikiAttachment attachmentExcel = new XWikiAttachment();
            attachmentExcel.setContent(xls);
            attachmentExcel.setFilename(home.getName() + ".xlsx");

            // Download the attachment.
            download(xwikiContext, attachmentExcel);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


	public void addMarkObjects(DocumentReference homeworkReference, List<String> members) {
		xwikiContext = xcontext.get();
        xwiki = xwikiContext.getWiki();
        Homework home = new Homework(xwikiContext, homeworkReference);

        home.addMarks(members);
	}
}
