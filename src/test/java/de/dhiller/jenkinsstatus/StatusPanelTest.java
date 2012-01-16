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

import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.swing.JFrame;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JLabelFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.dhiller.ci.jenkins.Job;
import de.dhiller.ci.jenkins.JobStatus;
import de.dhiller.ci.jenkins.Status;
import eu.hansolo.steelseries.extras.Led;
import eu.hansolo.steelseries.extras.LightBulb;

public class StatusPanelTest {

    private static final String JOB_NAME = "Job 1";
    JFrame f;
    StatusPanel statusPanel;
    private FrameFixture frameFixture;
    private JPanelFixture jPanelFixture;

    @Mock
    Status serverStatus;

    @Mock
    Job firstJob;

    @BeforeMethod
    public void initMocks() {
	MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void init() throws InterruptedException, InvocationTargetException {
	GuiActionRunner.execute(new GuiQuery<JFrame>() {

	    @Override
	    protected JFrame executeInEDT() throws Throwable {
		final JFrame f = new JFrame();
		statusPanel = new StatusPanel();
		f.getContentPane().add(statusPanel);
		f.setVisible(true);
		frameFixture = new FrameFixture(f);
		jPanelFixture = new JPanelFixture(frameFixture.robot,
			statusPanel);
		return f;
	    }
	});
    }

    @AfterMethod
    public void cleanUp() throws InterruptedException,
	    InvocationTargetException {
	frameFixture.cleanUp();
    }

    @Test(enabled = false)
    public void noJobs() throws Exception {
	// TODO: Search for other components
    }

    @Test
    public void jobName() throws Exception {
	setJob(JobStatus.RED, false);
	assertEquals(JOB_NAME, jobLabel().text());
    }

    @Test
    public void blueStatus() throws Exception {
	setJob(JobStatus.BLUE, false);
	greenLed().requireOn().requireNonBlinking();
	yellowLed().requireOff().requireNonBlinking();
	redLed().requireOff().requireNonBlinking();
    }

    @Test
    public void yellowStatus() throws Exception {
	setJob(JobStatus.YELLOW, false);
	greenLed().requireOff().requireNonBlinking();
	yellowLed().requireOn().requireNonBlinking();
	redLed().requireOff().requireNonBlinking();
    }

    @Test
    public void redStatus() throws Exception {
	setJob(JobStatus.RED, false);
	greenLed().requireOff().requireNonBlinking();
	yellowLed().requireOff().requireNonBlinking();
	redLed().requireOn().requireNonBlinking();
    }

    @Test
    public void runningAfterRedStatus() throws Exception {
	setJob(JobStatus.RED, true);
	greenLed().requireOff().requireNonBlinking();
	yellowLed().requireOff().requireNonBlinking();
	redLed().requireOn().requireBlinking();
    }

    @Test
    public void disabledSwitchesLightbulbOff() throws Exception {
	setJob(JobStatus.DISABLED, false);
	lightBulb().requireOff();
    }

    @Test
    public void blueStatusSwitchesLightbulbOff() throws Exception {
	setJob(JobStatus.BLUE, false);
	lightBulb().requireOn(true);
    }

    LightBulbFixture lightBulb() {
	return new LightBulbFixture(frameFixture.robot, frameFixture.robot
		.finder()
		.findByName(StatusPanel.LIGHTBULB, LightBulb.class));
    }

    @Test
    public void notBuilt() throws Exception {
	setJob(JobStatus.NOTBUILT, true);
	greenLed().requireOff().requireNonBlinking();
	yellowLed().requireOff().requireNonBlinking();
	redLed().requireOff().requireNonBlinking();
    }

    LedFixture yellowLed() {
	return led("yellow");
    }

    LedFixture greenLed() {
	return led("green");
    }

    LedFixture redLed() {
	return led("red");
    }

    LedFixture led(final String name) {
	return new LedFixture(frameFixture.robot, frameFixture.robot.finder()
		.findByName(name, Led.class));
    }

    void setJob(JobStatus jobStatus2, boolean running) {
	when(firstJob.name()).thenReturn(JOB_NAME);
	when(firstJob.status()).thenReturn(jobStatus2);
	when(firstJob.isRunning()).thenReturn(running);
	when(serverStatus.jobs()).thenReturn(Arrays.asList(firstJob));
	updateStatus(serverStatus);
    }

    void updateStatus(final Status serverStatus) {
	GuiActionRunner.execute(new GuiTask() {

	    @Override
	    protected void executeInEDT() throws Throwable {
		statusPanel.updateStatus(serverStatus);
		frameFixture.component().pack();
	    }
	});
    }

    JLabelFixture jobLabel() {
	return jPanelFixture.label(StatusPanel.JOB_NAME);
    }

}
