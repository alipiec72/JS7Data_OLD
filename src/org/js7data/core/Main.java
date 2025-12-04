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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Aleksander Lipiec
 */
@SuppressWarnings("ClassWithMultipleLoggers")
class Main {

    private static final Logger LOGGER_ERR = Logger.getLogger("LOG_ERR.log");
    private static final Logger LOGGER_INF = Logger.getLogger("LOG_INF.log");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            FileHandler FileHandlerErr = new FileHandler("LOG_ERR.log", true);
            LOGGER_ERR.addHandler(FileHandlerErr);
            LOGGER_ERR.setLevel(Level.SEVERE);

            FileHandler FileHandlerInf = new FileHandler("LOG_INF.log", true);
            LOGGER_INF.addHandler(FileHandlerInf);
            LOGGER_INF.setLevel(Level.SEVERE);

            FileFormatter fileFormatter = new FileFormatter();
            FileHandlerErr.setFormatter(fileFormatter);
            FileHandlerInf.setFormatter(fileFormatter);
            SwingUtilities.invokeLater(() -> {
                new MainJFrame().setVisible(true);
            });
        } catch (IOException | SecurityException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException ex) {
            String exceptionMessage = getStackTrace(ex);
            LOGGER_ERR.log(Level.SEVERE, exceptionMessage);
        }
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

}

class FileFormatter extends Formatter {

    private final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");

    @Override
    public String format(LogRecord logrecord) {
        StringBuilder builder = new StringBuilder(1000);
        builder.append(DATE_FORMAT.format(new java.util.Date(logrecord.getMillis()))).append(" - ");
        builder.append(logrecord.getSourceClassName());
        builder.append(" ");
        builder.append(logrecord.getSourceMethodName());
        builder.append(" ");
        builder.append(formatMessage(logrecord));
        builder.append("\n");
        return builder.toString();
    }

    @Override
    public String getHead(Handler h) {
        return super.getHead(h);
    }

    @Override
    public String getTail(Handler h) {
        return super.getTail(h);
    }
}
