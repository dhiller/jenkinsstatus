/*
 * Copyright (c) 2011, dhiller, http://www.dhiller.de Daniel Hiller, Warendorfer Str. 47, 48231 Warendorf, NRW,
 * Germany, All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer. - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided with the distribution. - Neither the
 * name of dhiller nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.dhiller.jenkinsstatus;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.prefs.Preferences;

import javax.swing.*;

import org.jdom.JDOMException;

import de.dhiller.ci.jenkins.Status;

public class Main extends JFrame {

    private static final String SERVER_URI = "ServerURI";
    private static final Preferences preferences = Preferences
	    .userNodeForPackage(Main.class);

    private Main() {
	super("Jenkins Status");
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.getContentPane().setLayout(new BorderLayout());
	final JPanel status = new JPanel();
	this.getContentPane().add(status);
	final JPanel buttons = new JPanel();
	buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));
	buttons.add(new JButton(new AbstractAction("...") {

	    public void actionPerformed(ActionEvent e) {
		new JDialog(Main.this, "Settings", true) {
		    {
			getContentPane().setLayout(new BorderLayout());
			final JPanel main = new JPanel();
			main.setLayout(new GridLayout(1, 2));
			main.add(new JLabel("Server URI"));
			final JTextField serverURI = new JTextField();
			serverURI.setText(preferences.get(SERVER_URI, ""));
			main.add(serverURI);
			getContentPane().add(main);
			final JPanel okCancel = new JPanel();
			okCancel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			okCancel.add(new JButton(new AbstractAction("OK") {

			    public void actionPerformed(ActionEvent e) {
				try {
				    final URI uri = new URI(serverURI.getText());
				    preferences.put(SERVER_URI, uri
					    .toASCIIString());
				    final String jenkinsrssLatestURI = uri
					    .toASCIIString();
				    new Status()
					    .parse(jenkinsrssLatestURI);
				} catch (URISyntaxException e1) {
				    e1.printStackTrace(); // TODO
				    JOptionPane.showMessageDialog(Main.this,
					    "URI invalid!");
				} catch (JDOMException e2) {
				    e2.printStackTrace(); // TODO
				} catch (IOException e3) {
				    e3.printStackTrace(); // TODO
				}
			    }
			}));
			okCancel.add(new JButton(new AbstractAction("Cancel") {

			    public void actionPerformed(ActionEvent e) {
				dispose();
			    }
			}));
			getContentPane().add(okCancel, BorderLayout.SOUTH);
			pack();

		    }
		}.setVisible(true);
	    }
	}));
	this.getContentPane().add(buttons, BorderLayout.SOUTH);
	pack();
    }

    public static void main(String[] args) {
	SwingUtilities.invokeLater(new Runnable() {

	    public void run() {
		new Main().setVisible(true);
	    }
	});
    }

}
