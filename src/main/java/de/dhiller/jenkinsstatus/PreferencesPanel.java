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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

final class PreferencesPanel extends JPanel {

    private final Main main;

    PreferencesPanel(Main main) {
	if (main == null)
	    throw new IllegalArgumentException("main null!?");
	this.main = main;
	this.setLayout(new BorderLayout());
	this.setBackground(Color.BLACK);
	final JPanel preferencesMainPanel = new JPanel();
	preferencesMainPanel.setBackground(Color.BLACK);
	preferencesMainPanel.setLayout(new GridLayout(1, 2));
	final JLabel label = new JLabel("Server URI");
	label.setForeground(Color.LIGHT_GRAY);
	preferencesMainPanel.add(label);
	final JTextField serverURI = new JTextField();
	serverURI.setText(ServerPreferences.serverURI());
	preferencesMainPanel.add(serverURI);
	this.add(preferencesMainPanel);
	final JPanel okCancel = new JPanel();
	okCancel.setBackground(Color.BLACK);
	okCancel.setLayout(new FlowLayout(FlowLayout.RIGHT));
	okCancel.add(new JButton(new Save(serverURI)));
	okCancel.add(new JButton(new Cancel()));
	this.add(okCancel, BorderLayout.SOUTH);
    }

    private class Cancel extends AbstractAction {
	protected Cancel() {
	    super("Cancel");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	    setVisible(false);
	}

    }

    private class Save extends AbstractAction {
	protected final JTextField serverURI;

	private Save(JTextField serverURI) {
	    super("OK");
	    this.serverURI = serverURI;
	}

	public void actionPerformed(ActionEvent e) {
	    try {
		final String uriText = serverURI.getText();
		ServerPreferences.saveURI(uriText);
		main.initStatus();
		setVisible(false);
	    } catch (Exception e1) {
		e1.printStackTrace(); // TODO
		JOptionPane.showMessageDialog(main, "URI invalid!");
	    }
	}
    }

    private PreferencesPanel(Main main, Object preferencesDialog) {
	if (main == null && preferencesDialog == null)
	    throw new IllegalArgumentException("Both null!?");
	setBackground(Color.BLACK);
	this.main = main;
	final JPanel preferencesMainPanel = new JPanel();
	preferencesMainPanel.setBackground(Color.BLACK);
	preferencesMainPanel.setLayout(new GridLayout(1, 2));
	preferencesMainPanel.add(new JLabel("Server URI"));
	final JTextField serverURI = new JTextField();
	serverURI.setText(ServerPreferences.serverURI());
	preferencesMainPanel.add(serverURI);
	this.add(preferencesMainPanel);
	final JPanel okCancel = new JPanel();
	okCancel.setBackground(Color.BLACK);
	okCancel.setLayout(new FlowLayout(FlowLayout.RIGHT));
	okCancel.add(new JButton(new Save(serverURI)));
	okCancel.add(new JButton(new Cancel()));
	this.add(okCancel, BorderLayout.SOUTH);

    }
}