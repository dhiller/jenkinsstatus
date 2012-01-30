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

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.WindowAdapter;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.dhiller.ci.jenkins.Job;
import de.dhiller.ci.jenkins.JobStatus;
import de.dhiller.ci.jenkins.Status;
import de.dhiller.steelseries.extras.DefectLightbulb;
import eu.hansolo.steelseries.extras.Led;
import eu.hansolo.steelseries.extras.LightBulb;
import eu.hansolo.steelseries.tools.LedColor;

final class StatusPanel extends JPanel {

    private static final float MAXIMUM_SIZE = 50f;
    private static final float MINIMUM_SIZE = 30f;
    public static final String JOB_NAME = "jobName";
    public static final String LIGHTBULB = "lightbulb";
    private Status lastServerStatus = new Status();

    {
	setBackground(background());
	setLayout(new GridBagLayout());
	java.awt.EventQueue.invokeLater(new Runnable() {
	    public void run() {
		final Window windowAncestor = SwingUtilities
			.getWindowAncestor(StatusPanel.this);
		((JFrame) windowAncestor)
			.addComponentListener(new ComponentAdapter() {
			    @Override
			    public void componentResized(ComponentEvent e) {
				reinitWithLastStatus();
			    }

			});
	    }
	});
    }

    Color background() {
	return Color.black;
    }

    private static final Insets INSETS = new Insets(2, 2, 2, 2);
    private float maxHeightPerLine;

    void updateStatus(Status serverStatus) {
	if (serverStatus.equals(lastServerStatus))
	    return;
	reinitComponents(serverStatus);
    }

    void reinitWithLastStatus() {
	reinitComponents(lastServerStatus);
    }

    private void reinitComponents(Status serverStatus) {
	removeAll();
	int row = 0;
	calculatemaxHeightPerLine(serverStatus);
	for (Job job : serverStatus.jobs()) {
	    final LightBulb comp = (job.status() == JobStatus.ABORTED ? new DefectLightbulb()
		    : new LightBulb());
	    comp.setName(LIGHTBULB);
	    comp.setOn(job.status() != JobStatus.DISABLED);
	    setSizes(comp);
	    add(comp, new GridBagConstraints(0, row, 1, 1, 0, 0,
		    GridBagConstraints.WEST, GridBagConstraints.NONE, INSETS,
		    0, 0));
	    addStatusLed(job, LedColor.GREEN, JobStatus.BLUE, row, 1);
	    addStatusLed(job, LedColor.YELLOW, JobStatus.YELLOW, row, 2);
	    addStatusLed(job, LedColor.RED, JobStatus.RED, row, 3);
	    final JLabel jobName = new JLabel(job.name());
	    jobName.setName(JOB_NAME);
	    jobName.setForeground(Color.lightGray);
	    jobName.setFont(jobName.getFont().deriveFont(maxHeightPerLine)
		    .deriveFont(Font.BOLD));
	    add(jobName, new GridBagConstraints(4, row, 1, 1, 0.0, 0,
		    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
		    INSETS, 0, 0));
	    row++;
	}
	add(newFillPanel(), new GridBagConstraints(0, row, 5, 1, 1.0, 1.0,
		GridBagConstraints.WEST, GridBagConstraints.BOTH, INSETS, 0, 0));
	lastServerStatus = serverStatus;
	invalidate();
	revalidate();
	repaint();
    }

    protected void calculatemaxHeightPerLine(Status serverStatus) {
	final Dimension windowSize = SwingUtilities.getWindowAncestor(this)
		.getSize();
	final float pixelSizePerLine = (float) (windowSize.getHeight() / ((double) serverStatus
		.jobs().size() * 1.4));
	maxHeightPerLine = pixelSizePerLine < MINIMUM_SIZE ? MINIMUM_SIZE
		: (pixelSizePerLine > MAXIMUM_SIZE ? MAXIMUM_SIZE
			: pixelSizePerLine);
    }

    JPanel newFillPanel() {
	final JPanel jPanel = new JPanel();
	jPanel.setBackground(background());
	return jPanel;
    }

    void addStatusLed(Job job, final LedColor ledColor,
	    final JobStatus statusToSwitchOn, int row, final int column) {
	final Led statusLed = new Led();
	statusLed.setName(ledColor.name().toLowerCase());
	setSizes(statusLed);
	statusLed.setLedColor(ledColor);
	statusLed.setLedOn(job.status() == statusToSwitchOn);
	statusLed
		.setLedBlinking(job.isRunning()
			&& (job.status() == statusToSwitchOn || job.status() == JobStatus.ABORTED));
	add(statusLed, newLedConstraints(row, column));
    }

    void setSizes(Component statusLed) {
	final int adjustedHeight = (int) (maxHeightPerLine * 1.2);
	statusLed.setMinimumSize(new Dimension(adjustedHeight, adjustedHeight));
	statusLed
		.setPreferredSize(new Dimension(adjustedHeight, adjustedHeight));
    }

    GridBagConstraints newLedConstraints(int row, int column) {
	return new GridBagConstraints(column, row, 1, 1, 0, 0,
		GridBagConstraints.CENTER, GridBagConstraints.NONE, INSETS, 0,
		0);
    }
}