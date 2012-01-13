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

import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.swing.JFrame;

import org.fest.swing.core.Robot;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.ComponentFixture;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.GenericComponentFixture;
import org.fest.swing.fixture.JLabelFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.testing.FestSwingTestCaseTemplate;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.dhiller.ci.jenkins.Job;
import de.dhiller.ci.jenkins.Status;
import eu.hansolo.steelseries.extras.Led;

public class StatusPanelTest {

    private final class LedFixture extends GenericComponentFixture<Led> {
	private LedFixture(Robot robot, Led target) {
	    super(robot, target);
	}
    }

    private static final String GREEN_LED_NAME = "green";
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
	final JFrame f = GuiActionRunner.execute(new GuiQuery<JFrame>() {

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
    public void labelText() throws Exception {
	setJob();
	assertEquals("Job 1", jobLabel().text());
    }

    @Test
    public void greenLed() throws Exception {
	setJob();
	assertLedOn(false, ledFixture(GREEN_LED_NAME));
    }

    GenericComponentFixture<Led> ledFixture(final String name) {
	final GenericComponentFixture<Led> ledFixture = new LedFixture(
		frameFixture.robot, led(name));
	return ledFixture;
    }

    Led led(final String ledName) {
	return frameFixture.robot.finder().findByName(ledName, Led.class);
    }

    void assertLedOn(final boolean expected,
	    final GenericComponentFixture<Led> ledFixture) {
	assertEquals(Boolean.valueOf(expected),
		GuiActionRunner.execute(new GuiQuery<Boolean>() {

		    @Override
		    protected Boolean executeInEDT() throws Throwable {
			return ledFixture.component().isLedOn();
		    }
		}));
    }

    void setJob() {
	when(firstJob.name()).thenReturn("Job 1");
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
