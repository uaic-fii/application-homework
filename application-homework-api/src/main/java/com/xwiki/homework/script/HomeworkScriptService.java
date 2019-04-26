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
    private HomeworkManager homework;

    /**
     * Lists attachments name from a page.
     * 
     * @param homeworkDoc the homework document
     * @return a string
     */
    public String listAttachments(DocumentReference homeworkReference)
    {
        return homework.getName(homeworkReference);
    }

    /**
     * Sets names for attachments.
     *
     * @param homeworkDoc the homework document
     * @return avoid
     */
    public void setAttachments(DocumentReference homeworkReference)
    {
        homework.setAttachmentsName(homeworkReference);
    }
}