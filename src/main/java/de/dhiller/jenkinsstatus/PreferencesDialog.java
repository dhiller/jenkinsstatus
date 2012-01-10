/*
 * Copyright (c) 2012, dhiller, http://www.dhiller.de Daniel Hiller, Warendorfer Str. 47, 48231 Warendorf, NRW,
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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

final class PreferencesDialog extends JDialog {
    private Main main;

    private final class Cancel extends AbstractAction {
	private Cancel() {
	    super("Cancel");
	}

	public void actionPerformed(ActionEvent e) {
	    dispose();
	}
    }

    private final class Save extends AbstractAction {
	private final JTextField serverURI;

	private Save(JTextField serverURI) {
	    super("OK");
	    this.serverURI = serverURI;
	}

	public void actionPerformed(ActionEvent e) {
	    try {
		final URI uri = new URI(serverURI.getText());
		Main.preferences.put(Constants.SERVER_URI, uri.toASCIIString());
		main.initStatus();
		dispose();
	    } catch (Exception e1) {
		e1.printStackTrace(); // TODO
		JOptionPane.showMessageDialog(main, "URI invalid!");
	    }
	}
    }

    PreferencesDialog(Main main, String title, boolean modal) {
	super(main, title, modal);
	this.main = main;
	getContentPane().setLayout(new BorderLayout());
	final JPanel preferencesPanel = new JPanel();
	preferencesPanel.setLayout(new GridLayout(1, 2));
	preferencesPanel.add(new JLabel("Server URI"));
	final JTextField serverURI = new JTextField();
	serverURI.setText(Main.preferences.get(Constants.SERVER_URI, ""));
	preferencesPanel.add(serverURI);
	getContentPane().add(preferencesPanel);
	final JPanel okCancel = new JPanel();
	okCancel.setLayout(new FlowLayout(FlowLayout.RIGHT));
	okCancel.add(new JButton(new Save(serverURI)));
	okCancel.add(new JButton(new Cancel()));
	getContentPane().add(okCancel, BorderLayout.SOUTH);
	pack();
    }
}