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

import static org.testng.AssertJUnit.*;
import static org.mockito.Mockito.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collections;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.dhiller.ci.jenkins.Job;
import de.dhiller.ci.jenkins.JobStatus;
import de.dhiller.ci.jenkins.Status;

public class MainTest {

    static final Job[] jobs = { newJob(null, JobStatus.BLUE, false),
	    newJob(null, JobStatus.YELLOW, false),
	    newJob(null, JobStatus.RED, false),
	    newJob(null, JobStatus.GREY, false),
	    newJob(null, JobStatus.ABORTED, false),
	    newJob(null, JobStatus.DISABLED, false),
	    newJob(null, JobStatus.NOTBUILT, false),
	    newJob(null, JobStatus.BLUE, true),
	    newJob(null, JobStatus.YELLOW, true),
	    newJob(null, JobStatus.RED, true),
	    newJob(null, JobStatus.GREY, true),
	    newJob(null, JobStatus.ABORTED, true),
	    newJob(null, JobStatus.DISABLED, true),
	    newJob(null, JobStatus.NOTBUILT, true) };

    boolean disableTimer = true;

    protected static Job newJob(String name, JobStatus status, boolean running) {
	return MockFactory.newJob((name == null ? status.toString() + " "
		+ (running ? "" : "not ") + "running" : name), status, running);
    }

    public static void main(String[] args) throws Exception {
	final MainTest mainTest = new MainTest();
	mainTest.initMocks();
	mainTest.disableTimer = false;
	mainTest.setUpTestInstance();
    }

    @Mock
    StatusProvider statusProvider;

    @Mock
    Status status;

    private FrameFixture frameFixture;

    @BeforeMethod
    public void initMocks() throws Exception {
	MockitoAnnotations.initMocks(this);
	final Status newStatus = MockFactory.newStatus(jobs);
	when(statusProvider.provide()).thenReturn(newStatus);
    }

    @BeforeMethod(dependsOnMethods = { "initMocks" })
    public void setUpTestInstance() {
	frameFixture = new FrameFixture(
		GuiActionRunner.execute(new GuiQuery<Main>() {

		    @Override
		    protected Main executeInEDT() throws Throwable {
			final Main main = new Main(disableTimer);
			main.setStatusProvider(statusProvider);
			main.setVisible(true);
			return main;
		    }
		}));
    }

    @AfterMethod
    public void cleanUp() {
	frameFixture.cleanUp();
    }

    @Test
    public void create() throws Exception {
	frameFixture.requireVisible();
    }

    @Test
    public void panelVisible() throws Exception {
	statusPanel().requireVisible();
    }

    protected JPanelFixture statusPanel() throws InterruptedException {
	final StatusUpdater statusUpdater = GuiActionRunner
		.execute(new GuiQuery<StatusUpdater>() {

		    @Override
		    protected StatusUpdater executeInEDT() throws Throwable {
			return ((Main) frameFixture.component()).initStatus();
		    }
		});
	statusUpdater.addPropertyChangeListener(new PropertyChangeListener() {

	    @Override
	    public void propertyChange(PropertyChangeEvent evt) {
		System.out.println(evt); //$NON-NLS-1$ // TODO: Remove
	    }
	});
	while (!statusUpdater.isDone()) {
	    synchronized (statusUpdater) {
		statusUpdater.wait(100);

	    }
	}
	final JPanelFixture statusPanelFixture = new JPanelFixture(
		frameFixture.robot, frameFixture.robot.finder().findByName(
			"statusPanel", StatusPanel.class));
	return statusPanelFixture;
    }

}
