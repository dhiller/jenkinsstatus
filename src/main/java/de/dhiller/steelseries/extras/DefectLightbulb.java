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

package de.dhiller.steelseries.extras;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.Timer;

import eu.hansolo.steelseries.extras.LightBulb;

public class DefectLightbulb extends LightBulb {

    static class LightbulbState {

	int msecs;
	boolean on;

	LightbulbState(int msecs, boolean on) {
	    this.msecs = msecs;
	    this.on = on;
	}

    }

    private static final Random random = new Random();

    private static List<LightbulbState> sequence = Arrays.asList(
	    new LightbulbState(50, true), new LightbulbState(150, false),
	    new LightbulbState(50, true), new LightbulbState(150, false),
	    new LightbulbState(150, true), new LightbulbState(150, false),
	    new LightbulbState(100, true), new LightbulbState(100, false),
	    new LightbulbState(250, true), new LightbulbState(100, false),
	    new LightbulbState(100, true), new LightbulbState(100, false),
	    new LightbulbState(500, true), new LightbulbState(100, false),
	    new LightbulbState(250, true), new LightbulbState(100, false),
	    new LightbulbState(1500, true), new LightbulbState(100, false));

    private int sequenceIndex = 0;

    private final Timer timer = new Timer(0, new ActionListener() {

	@Override
	public void actionPerformed(ActionEvent e) {
	    setNextState();
	}
    });

    private boolean on;

    @Override
    public void setOn(boolean ON) {
	timer.stop();
	super.setOn(false);
	this.on = ON;
	if (ON) {
	    sequenceIndex = 0;
	    setNextState();
	}
    }

    @Override
    public boolean isOn() {
	return this.on;
    }

    private void setNextState() {
	timer.stop();
	if (!this.on)
	    return;
	if (sequenceIndex >= sequence.size())
	    sequenceIndex = 0;
	final LightbulbState nextState = sequence.get(sequenceIndex);
	timer.setInitialDelay(delay(nextState));
	super.setOn(nextState.on);
	sequenceIndex++;
	timer.restart();
    }

    int delay(final LightbulbState nextState) {
	return nextState.msecs
		+ (random.nextInt() % ((int) (nextState.msecs * 0.1)));
    }

}
