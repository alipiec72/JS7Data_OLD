/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 */

package org.js7data.core;

import Moka7.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aleksander Lipiec
 */
@SuppressWarnings("ClassWithMultipleLoggers")
class DataInServer extends Thread {

    private static final Logger LOGGER_ERR = Logger.getLogger("LOGGER_ERR.log");
    private final MainJFrame mainJFrame;
    
    private boolean doStop;
    
    private static final String PLC_IP = "192.168.5.172";
    private static final int RACK = 0;
    private static final int SLOT = 2;
    private static final int DB_NUMBER = 80;
    private static final int START = 0;
    private static final int LENDB = 242;
    
        
    static final byte[] BUFFER = new byte[65536];
    static final S7Client CLIENT = new S7Client();

    DataInServer(MainJFrame mainJFrame) {
        this.mainJFrame = mainJFrame;
        CLIENT.SetConnectionType(S7.OP);
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
    
    void finito() {
        this.doStop();
    }

    public synchronized void doStop() {
        this.doStop = true;
    }

    private synchronized boolean keepRunning() {
        return this.doStop == false;
    }
    
    private void init() {
        doStop = false;
    }
        
    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        try {
            System.out.println("DataInServer Run");
            init();
            while (keepRunning()){
                if (CLIENT.Connected== false) {
                    int error = CLIENT.ConnectTo(PLC_IP, RACK, SLOT);
                    if (error > 0) {
                        String exceptionMessage = S7Client.ErrorText(error);
                        LOGGER_ERR.log(Level.SEVERE, exceptionMessage);
                        Thread.currentThread().interrupt();
                        mainJFrame.runDataInServer();
                    }
		} 
                else {
                    CLIENT.ReadArea(S7.S7AreaDB, DB_NUMBER, START, LENDB, BUFFER);
                     Thread.sleep(20);
                }
            }
                
        } catch (NullPointerException | InterruptedException ex) {
            String exceptionMessage = getStackTrace(ex);
            LOGGER_ERR.log(Level.SEVERE, exceptionMessage);
            Thread.currentThread().interrupt();
        }finally {
            this.doStop();
        }
    }
}