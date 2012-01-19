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

import java.util.Arrays;
import java.util.Collections;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.dhiller.ci.jenkins.Job;
import de.dhiller.ci.jenkins.JobStatus;
import de.dhiller.ci.jenkins.Status;

public class MainTest {

    @Mock
    StatusProvider statusProvider;

    @Mock
    Status status;

    private FrameFixture frameFixture;

    @BeforeMethod
    public void initMocks() throws Exception {
	MockitoAnnotations.initMocks(this);
	final Job firstJob = MockFactory.newJob(
		JobStatus.BLUE, false);
	final Status newStatus = MockFactory.newStatus(firstJob);
	when(statusProvider.provide()).thenReturn(newStatus);
    }

    @BeforeMethod(dependsOnMethods = { "initMocks" })
    public void setUpTestInstance() {
	frameFixture = new FrameFixture(
		GuiActionRunner.execute(new GuiQuery<Main>() {

		    @Override
		    protected Main executeInEDT() throws Throwable {
			final Main main = new Main(false);
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
	GuiActionRunner.execute(new GuiTask() {

	    @Override
	    protected void executeInEDT() throws Throwable {
		((Main) frameFixture.component()).initStatus();
	    }
	});
	frameFixture.robot.finder()
		.findByName("statusPanel", StatusPanel.class);
    }

}
