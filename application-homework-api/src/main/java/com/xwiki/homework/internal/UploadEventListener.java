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

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.xwiki.bridge.event.DocumentUpdatingEvent;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.event.Event;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xwiki.homework.internal.helpers.Homework;

@Component
@Named("UploadEventListener")
@Singleton
public class UploadEventListener implements EventListener
{
    @Inject
    private Provider<XWikiContext> xcontext;

	private XWikiContext xwikiContext;
	private XWikiDocument uploadDocument;

    @Override public String getName()
    {
        return "UploadEventListener";
    }

    @Override public List<Event> getEvents()
    {
        return Arrays.<Event>asList(new DocumentUpdatingEvent());
    }

    @SuppressWarnings("deprecation")
    @Override public void onEvent(Event event, Object source, Object data)
    {
    	uploadDocument = (XWikiDocument) source;
        xwikiContext = xcontext.get();

        DocumentUpdatingEvent updatingEvent = (DocumentUpdatingEvent) event;
        BaseObject uploadObject = uploadDocument.getObject("Homework.Code.UploadClass", 0);
        if (uploadObject != null) {
            String[] parent = uploadObject.getStringValue("parent").split("\\.");
			DocumentReference homeworkReference = new DocumentReference(xwikiContext.getWikiId(), Arrays.asList(parent[0], parent[1], parent[2]), parent[3]);

			Homework homework = new Homework(xwikiContext, homeworkReference);
			homework.setDeadline();

			// Cancel the event if is past deadline.
			if(!homework.isBeforeDeadline()) {
				updatingEvent.cancel("It's past deadline, you cannot upload files anymore.");
			}
        }
    }
}