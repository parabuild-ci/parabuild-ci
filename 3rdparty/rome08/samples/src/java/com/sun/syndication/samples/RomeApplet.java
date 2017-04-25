package com.sun.syndication.samples;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * Created on Sep 5, 2004
 * Updated on Sep 13 to remove inner classes
 *
 * This applet accepts a local file name or a URL, fetches the XML feed and
 * displays it using SyndFeed.toString()
 *
 * The applet adheres to the security policy of the appletviewer
 */
/**
 * @author Ken Kast
 *
 */
public class RomeApplet extends Applet implements ActionListener {

	private static final int fieldWidth = 80;
	private URL home = null;
	private Label feedLabel;
	private TextArea feedText;
	private Button fetchButton;
	private Checkbox fileButton;
	private CheckboxGroup fileURLButtons;
	private TextField urlField;
	private Checkbox urlButton;
	private GridBagLayout layout = new GridBagLayout();
	private GridBagConstraints constraints = new GridBagConstraints();

	public void init() {

		//Determine where applet lives
		//Used as base directory for fetching files
		home = getCodeBase();

		//Layout GUI.
		//It consists of
		//	two radio buttons to choose URL or file as input
		//	a text field to enter file name or URL
		//	a text box to display the feed
		//	a button to fetch the feed
		setSize(10 * fieldWidth, 450);
		setBackground(Color.lightGray);
		setLayout(layout);
		fileURLButtons = new CheckboxGroup();
		urlButton = new Checkbox("URL", fileURLButtons, true);
		add(urlButton);
		fileButton = new Checkbox("File", fileURLButtons, false);
		add(fileButton);
		urlField = new TextField("", fieldWidth);
		urlField.setEditable(true);
		add(urlField);
		feedLabel = new Label("Feed");
		add(feedLabel);
		feedText = new TextArea(20, fieldWidth);
		add(feedText);
		fetchButton = new Button("Fetch");
		fetchButton.addActionListener(this); //Event handler
		add(fetchButton);
		//Place widgets in window
		locateWidget(urlButton, 0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER);
		locateWidget(fileButton, 0, 1, 1, 1, 2, 0, GridBagConstraints.WEST);
		locateWidget(urlField, 1, 0, 1, 2, 2, 0, GridBagConstraints.WEST);
		locateWidget(feedLabel, 0, 2, 1, 1, 10, 10,
				GridBagConstraints.NORTHEAST);
		locateWidget(feedText, 1, 2, 1, 1, 10, 10, GridBagConstraints.NORTHWEST);
		locateWidget(fetchButton, 1, 3, 1, 1, 10, 10, GridBagConstraints.CENTER);
		validate();
		urlField.requestFocus();
	}

	private void locateWidget(Component widget, int gridx, int gridy,
			int gridwidth, int gridheight, int top, int bottom, int anchor) {
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.gridwidth = gridwidth;
		constraints.gridheight = gridheight;
		constraints.insets.top = top;
		constraints.insets.bottom = bottom;
		constraints.anchor = anchor;
		layout.setConstraints(widget, constraints);
	}

	/*
	 * Event handler for "Fetch" button
	 */
	public void actionPerformed(ActionEvent e) {
		Checkbox selChkbx = fileURLButtons.getSelectedCheckbox();
		URL url;
		if (e.getSource() == fetchButton) {
			boolean isSourceURL = (selChkbx == urlButton);
			try { // Crash if there is a problem with the URL
				if (isSourceURL)
					url = new URL(urlField.getText().toString());
				else
					url = new URL(home, urlField.getText().toString());
				feedText.setText(getRSSFeed(url).toString());
			}
			catch (MalformedURLException ex) {
				System.err.println("Malformed URL " + ex.getMessage());
			}
			catch (Exception ex) {
				ex.printStackTrace();
				System.err.println("ERROR: " + ex.getMessage());
			}
		}
	}

	private SyndFeed getRSSFeed(URL url) throws Exception {
		SyndFeed feed;
		SyndFeedInput input = new SyndFeedInput();
		feed = input.build(new InputStreamReader(url.openStream()));
		return feed;
	}
}