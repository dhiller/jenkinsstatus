/*
 * Copyright (c) 2012, dhiller, http://www.dhiller.de,
 * Daniel Hiller, Warendorfer Str. 47, 48231 Warendorf, NRW, Germany
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice, this list of
 *    conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice, this list
 *    of conditions and the following disclaimer in the documentation and/or other materials
 *    provided with the distribution.
 *  - Neither the name of dhiller nor the names of its contributors may be used to endorse
 *    or promote products derived from this software without specific prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.dhiller.platform;

import de.dhiller.jenkinsstatus.PlatformUIException;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public enum Platform {
    OSX {
        @Override
        public void requestToggleFullScreen(final Window w) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    try {
                        applicationClass().getMethod("requestToggleFullScreen", Window.class).invoke(application(), w);
                    } catch (IllegalAccessException e) {
                        throw new PlatformUIException(e);
                    } catch (InvocationTargetException e) {
                        throw new PlatformUIException(e);
                    } catch (NoSuchMethodException e) {
                        throw new PlatformUIException(e);
                    }
                }
            });
        }

        @Override
        public void setDockIcon(Image image) {
            try {
                applicationClass().getMethod("setDockIconImage", Image.class).invoke(application(), image);
            } catch (IllegalAccessException e) {
                throw new PlatformUIException(e);
            } catch (InvocationTargetException e) {
                throw new PlatformUIException(e);
            } catch (NoSuchMethodException e) {
                throw new PlatformUIException(e);
            }
        }

        @Override
        public void markWindowAsFullScreen(Window w) {
            try {
                final Class<?> fullScreenUtilsClass = Class.forName("com.apple.eawt.FullScreenUtilities");
                final Method method = fullScreenUtilsClass
                        .getMethod("setWindowCanFullScreen", Window.class, boolean.class);
                method.invoke(null, w, true);
            } catch (NoSuchMethodException e) {
                throw new PlatformUIException(e);
            } catch (ClassNotFoundException e) {
                throw new PlatformUIException(e);
            } catch (InvocationTargetException e) {
                throw new PlatformUIException(e);
            } catch (IllegalAccessException e) {
                throw new PlatformUIException(e);
            }
        }

        @Override
        boolean isCurrent() {
            return (System.getProperty("mrj.version") != null);
        }

        private Object application() {
            try {
                return applicationClass().getMethod("getApplication").invoke(applicationClass().newInstance());
            } catch (IllegalAccessException e) {
                throw new PlatformUIException(e);
            } catch (InvocationTargetException e) {
                throw new PlatformUIException(e);
            } catch (NoSuchMethodException e) {
                throw new PlatformUIException(e);
            } catch (InstantiationException e) {
                throw new PlatformUIException(e);
            }
        }

        private Class<?> applicationClass() {
            try {
                return Class.forName("com.apple.eawt.Application");
            } catch (ClassNotFoundException e) {
                throw new PlatformUIException(e);
            }
        }

    },

    OTHER {
        @Override
        public void setDockIcon(Image image) {
            // Not implemented
        }

        @Override
        public void markWindowAsFullScreen(Window w) {
            // Not implemented
        }

        @Override
        public void requestToggleFullScreen(Window w) {
            // Not implemented
        }

        @Override
        boolean isCurrent() {
            return !OSX.isCurrent();
        }
    };

    public static Platform current() {
        for (Platform p : values()) {
            if (p.isCurrent())
                return p;
        }
        throw new IllegalStateException("Platform " + System.getProperty("os.name") + "not supported!");
    }

    abstract boolean isCurrent();

    public abstract void setDockIcon(Image image);

    public abstract void markWindowAsFullScreen(Window w);

    public abstract void requestToggleFullScreen(final Window w);
}
