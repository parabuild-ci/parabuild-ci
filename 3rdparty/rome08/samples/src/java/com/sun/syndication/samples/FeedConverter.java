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

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;

import java.io.PrintWriter;
import java.net.URL;

/**
 * It Converts any RSS/Atom feed type to a an RSS/Atom feed of the
 * specified type.
 * <p>
 * @author Alejandro Abdelnur
 *
 */
public class FeedConverter {

    public static void main(String[] args) {
        boolean ok = false;
        if (args.length==2) {
            try {
                String outputType = args[0];
                URL feedUrl = new URL(args[1]);
                SyndFeedInput input = new SyndFeedInput();

                SyndFeed feed = input.build(new XmlReader(feedUrl));

                feed.setFeedType(outputType);
                SyndFeedOutput output = new SyndFeedOutput();
                output.output(feed,new PrintWriter(System.out));

                ok = true;
            }
            catch (Exception ex) {
                System.out.println("ERROR: "+ex.getMessage());
            }
        }

        if (!ok) {
            System.out.println();
            System.out.println("Converter converts between syndication feeds types.");
            System.out.println("The first parameter must be the feed type to convert to.");
            System.out.println(" [valid values are: rss_0.9, rss_0.91, rss_0.92, rss_0.93, ]");
            System.out.println(" [                  rss_0.94, rss_1.0, rss_2.0 & atom_0.3  ]");
            System.out.println("The second parameter must be the URL of the feed to convert.");
            System.out.println();
        }
    }

}
