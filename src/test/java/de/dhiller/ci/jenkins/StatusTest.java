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

package de.dhiller.ci.jenkins;

import static org.testng.AssertJUnit.*;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.dhiller.ci.jenkins.Status;

public class StatusTest {

    private Status status;

    @BeforeClass
    void setUpTestInstance() throws Exception {
	status = new Status();
	status.parse(getClass().getResourceAsStream(
		"/api.xml"));
    }

    @Test
    public void serverName() throws Exception {
	assertTrue(status.serverName().contains("Jenkins Instanz auf dhiller"));
    }

    @Test
    public void hasJobs() throws Exception {
	assertFalse(status.jobs().isEmpty());
    }

    @Test
    public void names() throws Exception {
	assertEquals("First_Job", first().name());
	assertEquals("Second_Job", second().name());
	assertEquals("Third_Job", third().name());
    }

    @Test
    public void statuses() throws Exception {
	assertEquals(JobStatus.YELLOW, first().status());
	assertEquals(JobStatus.RED, second().status());
	assertEquals(JobStatus.BLUE, third().status());
	assertEquals(JobStatus.YELLOW, job(3).status());
	assertEquals(JobStatus.RED, job(4).status());
	assertEquals(JobStatus.BLUE, job(5).status());
    }

    @Test
    public void running() throws Exception {
	assertFalse(first().isRunning());
	assertTrue(job(3).isRunning());
    }

    @Test
    public void nameNotEqual() throws Exception {
	assertFalse(newStatus("17").equals(newStatus("42")));
    }

    @Test
    public void nameEqual() throws Exception {
	assertTrue(newStatus("17").equals(newStatus("17")));
    }

    protected Status newStatus(final String serverName) {
	final Status first = new Status();
	first.setServerName(serverName);
	return first;
    }

    public void notEqual() throws Exception {
	final Status first = new Status();
	first.setServerName("17");
	assertTrue(first.equals(new Status()));
    }

    Job first() {
	return job(0);
    }

    Job second() {
	return job(1);
    }

    Job third() {
	return job(2);
    }

    Job job(int index) {
	return status.jobs().get(index);
    }

}
