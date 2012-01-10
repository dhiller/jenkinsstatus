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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class Status {

    private String serverName = "?";
    private List<Job> jobs = Collections.emptyList();

    public void parse(String jenkinsrssLatestURI) throws Exception {
	// TODO: Reduce number of exception types
	final InputStream stream = new URI(jenkinsrssLatestURI
		+ (jenkinsrssLatestURI.endsWith("/") ? "" : "/") + "api/xml")
		.toURL().openStream();
	try {
	    parse(stream);
	} finally {
	    stream.close();
	}
    }

    public String serverName() {
	return serverName;
    }

    void parse(final InputStream stream) throws Exception {
	SAXReader reader = new SAXReader();
	Document document = reader.read(stream);
	serverName = description(document)
		.getText();
	jobs = new ArrayList<Job>();
	final List<?> jobElements = document.selectNodes("//hudson/job");
	for (int index = 0, n = jobElements.size(); index < n; index++) {
	    final Job job = new Job();
	    final Node jobElement = (Node) jobElements.get(index);
	    job.name = document.selectSingleNode(
		    "//hudson/job[" + (index + 1) + "]/name").getText();
	    job.status = JobStatus.valueOf(document.selectSingleNode(
				    "//hudson/job[" + (index + 1) + "]/color")
			    .getText().toUpperCase());
	    jobs.add(job);
	}
    }

    Node description(Document document) {
	final Node description = document
		.selectSingleNode("//hudson/description");
	if (description != null)
	    return description;
	return document.selectSingleNode("//hudson/nodeName");
    }

    public List<Job> jobs() {
	return jobs;
    }

}