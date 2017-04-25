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
package com.sun.syndication.samples.servlet;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Sample Servlet that serves a feed created with Rome.
 * <p>
 * The feed type is determined by the 'type' request parameter, if the parameter is missing it defaults
 * to the 'default.feed.type' servlet init parameter, if the init parameter is missing it defaults to 'atom_0.3'
 * <p>
 * @author Alejandro Abdelnur
 *
 */
public class FeedServlet extends HttpServlet {
    private static final String DEFAULT_FEED_TYPE = "default.feed.type";
    private static final String FEED_TYPE = "type";
    private static final String MIME_TYPE = "application/xml; charset=UTF-8";
    private static final String COULD_NOT_GENERATE_FEED_ERROR = "Could not generate feed";

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private String _defaultFeedType;

    public void init() {
        _defaultFeedType = getServletConfig().getInitParameter(DEFAULT_FEED_TYPE);
        _defaultFeedType = (_defaultFeedType!=null) ? _defaultFeedType : "atom_0.3";
    }

    public void doGet(HttpServletRequest req,HttpServletResponse res) throws IOException {
        try {
            SyndFeed feed = getFeed(req);

            String feedType = req.getParameter(FEED_TYPE);
            feedType = (feedType!=null) ? feedType : _defaultFeedType;
            feed.setFeedType(feedType);

            res.setContentType(MIME_TYPE);
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed,res.getWriter());
        }
        catch (FeedException ex) {
            String msg = COULD_NOT_GENERATE_FEED_ERROR;
            log(msg,ex);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,msg);
        }
    }

    protected SyndFeed getFeed(HttpServletRequest req) throws IOException,FeedException {
        DateFormat dateParser = new SimpleDateFormat(DATE_FORMAT);

        SyndFeed feed = new SyndFeedImpl();

        feed.setTitle("Sample Feed (created with Rome)");
        feed.setLink("http://rome.dev.java.net");
        feed.setDescription("This feed has been created using Rome (Java syndication utilities");

        List entries = new ArrayList();
        SyndEntry entry;
        SyndContent description;

        entry = new SyndEntryImpl();
        entry.setTitle("Rome v0.1");
        entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome01");
        try {
            entry.setPublishedDate(dateParser.parse("2004-06-08"));
        }
        catch (ParseException ex) {
            // IT CANNOT HAPPEN WITH THIS SAMPLE
        }
        description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue("Initial release of Rome");
        entry.setDescription(description);
        entries.add(entry);

        entry = new SyndEntryImpl();
        entry.setTitle("Rome v0.2");
        entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome02");
        try {
            entry.setPublishedDate(dateParser.parse("2004-06-16"));
        }
        catch (ParseException ex) {
            // IT CANNOT HAPPEN WITH THIS SAMPLE
        }
        description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue("Bug fixes, minor API changes and some new features"+
                             "<p>For details check the <a href=\"http://wiki.java.net/bin/view/Javawsxml/RomeChangesLog#RomeV02\">Changes Log for 0.2</a></p>");
        entry.setDescription(description);
        entries.add(entry);

        entry = new SyndEntryImpl();
        entry.setTitle("Rome v0.3");
        entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome03");
        try {
            entry.setPublishedDate(dateParser.parse("2004-07-27"));
        }
        catch (ParseException ex) {
            // IT CANNOT HAPPEN WITH THIS SAMPLE
        }
        description = new SyndContentImpl();
        description.setType("text/html");
        description.setValue("<p>Bug fixes, API changes, some new features and some Unit testing</p>"+
                             "<p>For details check the <a href=\"http://wiki.java.net/bin/view/Javawsxml/RomeChangesLog#RomeV03\">Changes Log for 0.3</a></p>");
        entry.setDescription(description);
        entries.add(entry);

        entry = new SyndEntryImpl();
        entry.setTitle("Rome v0.4");
        entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome04");
        try {
            entry.setPublishedDate(dateParser.parse("2004-09-24"));
        }
        catch (ParseException ex) {
            // IT CANNOT HAPPEN WITH THIS SAMPLE
        }
        description = new SyndContentImpl();
        description.setType("text/html");
        description.setValue("<p>Bug fixes, API changes, some new features, Unit testing completed</p>"+
                             "<p>For details check the <a href=\"http://wiki.java.net/bin/view/Javawsxml/RomeChangesLog#RomeV04\">Changes Log for 0.4</a></p>");
        entry.setDescription(description);
        entries.add(entry);

        feed.setEntries(entries);

        return feed;
    }

}
