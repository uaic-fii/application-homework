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
package com.xwiki.homework;

import java.util.List;

import org.xwiki.component.annotation.Role;
import org.xwiki.model.reference.DocumentReference;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiAttachment;

/**
 * @version $Id$
 * @since 1.0
 */
@Role
public interface HomeworkManager
{
    /**
     * Download all attachments of a homework.
     *
     * @param homeworkReference the homework reference
     * @return void
     */
    public void downloadAllAttachments(DocumentReference homeworkReference, String student);

    /**
     * Download to browser.
     *
     * @param homeworkReference the homework reference
     * @return void
     */
    public void download(XWikiContext xwikiContext, XWikiAttachment attachment);

    /**
     * Checks if the deadline is over.
     *
     * @param homeworkReference the homework reference
     * @return void
     */
    public Boolean isBeforeDeadline(DocumentReference homeworkReference);

    /**
     * Export grades.
     *
     * @param homeworkReference the homework reference
     * @return void
     */
    public void export(DocumentReference homeworkReference);

    /**
     * Add mark objects.
     *
     * @param homeworkDoc the homework document
     * @return avoid
     */
    public void addMarkObjects(DocumentReference homeworkReference, List<String> members);
}
