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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.dhiller.ci.jenkins.Job;
import de.dhiller.ci.jenkins.JobStatus;
import de.dhiller.ci.jenkins.Status;
import eu.hansolo.steelseries.extras.Led;
import eu.hansolo.steelseries.tools.LedColor;

final class StatusPanel extends JPanel {

    {
	setBackground(background());
	setLayout(new GridBagLayout());
    }

    Color background() {
	return Color.black;
    }

    private static final Insets INSETS = new Insets(2, 2, 2, 2);

    void updateStatus(Status serverStatus) {
	removeAll();
	int row = 0;
	for (Job job : serverStatus.jobs()) {
	    addStatusLed(job, LedColor.RED, JobStatus.RED, row, 2);
	    addStatusLed(job, LedColor.YELLOW, JobStatus.YELLOW, row, 1);
	    addStatusLed(job, LedColor.GREEN, JobStatus.BLUE, row, 0);
	    final JLabel jobName = new JLabel(job.name());
	    jobName.setForeground(Color.lightGray);
	    jobName.setFont(jobName.getFont().deriveFont(20.0f)
		    .deriveFont(Font.BOLD));
	    add(jobName, new GridBagConstraints(3, row, 1, 1, 0, 0,
		    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
		    INSETS, 0, 0));
	    row++;
	}
	add(newFillPanel(), new GridBagConstraints(0, row, 4, 1, 0, 0,
		GridBagConstraints.WEST, GridBagConstraints.BOTH, INSETS, 0, 0));
    }

    JPanel newFillPanel() {
	final JPanel jPanel = new JPanel();
	jPanel.setBackground(background());
	return jPanel;
    }

    void addStatusLed(Job job, final LedColor ledColor,
	    final JobStatus statusToSwitchOn, int row, final int column) {
	final Led statusLed = new Led();
	statusLed.setMinimumSize(new Dimension(25, 25));
	statusLed.setPreferredSize(new Dimension(25, 25));
	statusLed.setLedColor(ledColor);
	statusLed.setLedOn(job.status() == statusToSwitchOn);
	statusLed.setLedBlinking(job.isRunning());
	add(statusLed, newLedConstraints(row, column));
    }

    GridBagConstraints newLedConstraints(int row, int column) {
	return new GridBagConstraints(column, row, 1, 1, 0, 0,
		GridBagConstraints.CENTER, GridBagConstraints.NONE, INSETS, 0,
		0);
    }
}