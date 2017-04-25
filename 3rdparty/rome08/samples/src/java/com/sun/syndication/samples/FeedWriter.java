/*
 * Copyright 2004 Sun Microsystems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.sun.syndication.samples;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedOutput;

import java.io.FileWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * It creates a feed and writes it to a file.
 * <p>
 * @author Alejandro Abdelnur
 *
 */
public class FeedWriter {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public static void main(String[] args) {
        boolean ok = false;
        if (args.length==2) {
            try {
                String feedType = args[0];
                String fileName = args[1];

                DateFormat dateParser = new SimpleDateFormat(DATE_FORMAT);

                SyndFeed feed = new SyndFeedImpl();
                feed.setFeedType(feedType);

                feed.setTitle("Sample Feed (created with Rome)");
                feed.setLink("http://rome.dev.java.net");
                feed.setDescription("This feed has been created using Rome (Java syndication utilities");

                List entries = new ArrayList();
                SyndEntry entry;
                SyndContent description;

                entry = new SyndEntryImpl();
                entry.setTitle("Rome v1.0");
                entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome01");
                entry.setPublishedDate(dateParser.parse("2004-06-08"));
                description = new SyndContentImpl();
                description.setType("text/plain");
                description.setValue("Initial release of Rome");
                entry.setDescription(description);
                entries.add(entry);

                entry = new SyndEntryImpl();
                entry.setTitle("Rome v2.0");
                entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome02");
                entry.setPublishedDate(dateParser.parse("2004-06-16"));
                description = new SyndContentImpl();
                description.setType("text/xml");
                description.setValue("Bug fixes, <xml>XML</xml> minor API changes and some new features");
                entry.setDescription(description);
                entries.add(entry);

                entry = new SyndEntryImpl();
                entry.setTitle("Rome v3.0");
                entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome03");
                entry.setPublishedDate(dateParser.parse("2004-07-27"));
                description = new SyndContentImpl();
                description.setType("text/html");
                description.setValue("<p>More Bug fixes, mor API changes, some new features and some Unit testing</p>"+
                                     "<p>For details check the <a href=\"http://wiki.java.net/bin/view/Javawsxml/RomeChangesLog#RomeV03\">Changes Log</a></p>");
                entry.setDescription(description);
                entries.add(entry);

                feed.setEntries(entries);

                Writer writer = new FileWriter(fileName);
                SyndFeedOutput output = new SyndFeedOutput();
                output.output(feed,writer);
                writer.close();

                System.out.println("The feed has been written to the file ["+fileName+"]");

                ok = true;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("ERROR: "+ex.getMessage());
            }
        }

        if (!ok) {
            System.out.println();
            System.out.println("FeedWriter creates a RSS/Atom feed and writes it to a file.");
            System.out.println("The first parameter must be the syndication format for the feed");
            System.out.println("  (rss_0.90, rss_0.91, rss_0.92, rss_0.93, rss_0.94, rss_1.0 rss_2.0 or atom_0.3)");
            System.out.println("The second parameter must be the file name for the feed");
            System.out.println();
        }
    }

}
