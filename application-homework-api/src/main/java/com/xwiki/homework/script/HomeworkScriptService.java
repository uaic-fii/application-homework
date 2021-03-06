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
package com.xwiki.homework.script;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.script.service.ScriptService;

import com.xwiki.homework.HomeworkManager;

/**
 * @version $Id$
 * @since 1.0
 */
@Component
@Named("homework")
@Singleton
public class HomeworkScriptService implements ScriptService
{
    @Inject
    private HomeworkManager homeworkManager;

    /**
     * Downloads attachments of a page.
     *
     * @param homeworkDoc the homework document
     * @param student the student
     * @return avoid
     */
    public void downloadAttachments(DocumentReference homeworkReference, String student)
    {
       homeworkManager.downloadAllAttachments(homeworkReference, student);
    }

    /**
     * Checks if it's past deadline.
     *
     * @param homeworkDoc the homework document
     * @return avoid
     */
    public Boolean canUploadHomework(DocumentReference homeworkReference)
    {
       return homeworkManager.isBeforeDeadline(homeworkReference);
    }

    /**
     * Exports.
     *
     * @param homeworkDoc the homework document
     * @return avoid
     */
    public void export(DocumentReference homeworkReference)
    {
       homeworkManager.export(homeworkReference);
    }

    /**
     * Add mark objects.
     *
     * @param homeworkDoc the homework document
     * @return avoid
     */
    public void addMarkObjects(DocumentReference homeworkReference, List<String> members)
    {
       homeworkManager.addMarkObjects(homeworkReference, members);
    }

    /**
     * Get uploadDoc name.
     *
     * @param homeworkDoc the homework document
     * @param student the current user
     * @return avoid
     */
    public DocumentReference getUploadDocName(DocumentReference homeworkReference, String student)
    {
       return homeworkManager.getUploadDocName(homeworkReference, student);
    }

    /**
     * Creates a document.
     *
     * @param homeworkDoc the homework document
     * @param student the current user
     * @return avoid
     */
    public void createDocument(DocumentReference docReference, DocumentReference userReference)
    {
       homeworkManager.createDocument(docReference, userReference);
    }

    /**
     * Updates rights object of an uploadDoc if the authors have been changes.
     *
     * @param docReference the uploadDoc reference
     * @return avoid
     */
    public void updateRights(DocumentReference docReference)
    {
       homeworkManager.updateRights(docReference);
    }
}
