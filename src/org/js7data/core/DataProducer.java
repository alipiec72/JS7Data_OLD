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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Aleksander Lipiec
 */
public class DataProducer {

    private final String timeStamp;
    private final PrintWriter pw;
    private final StringBuilder data;
    private final StringBuilder data1;

    DataProducer() throws FileNotFoundException {
        this.timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Calendar.getInstance().getTime());
        this.data = new StringBuilder("");
        this.data1 = new StringBuilder("");
        this.pw = new PrintWriter(new File("Dane_" + timeStamp + ".csv"));
        data.append("a1_rk");
        data.append(',');
        data.append("a1_st");
        data.append(',');
        data.append("a1_msp_fe");
        data.append(',');
        data.append("a2_rk");
        data.append(',');
        data.append("a2_st");
        data.append(',');
        data.append("a2_msp_fe");
        data.append(',');
        data.append("b1_rk");
        data.append(',');
        data.append("b1_st");
        data.append(',');
        data.append("b1_msp_fe");
        data.append(',');
        data.append("b2_rk");
        data.append(',');
        data.append("b2_st");
        data.append(',');
        data.append("b2_msp_fe");
        data.append(',');
        data.append("a1_sch");
        data.append(',');
        data.append("a2_sch");
        data.append(',');
        data.append("b1_sch");
        data.append(',');
        data.append("b2_sch");
        data.append('\n');
        pw.write(data.toString());
    }

    private void writeData(byte[] buffer) {
        data.setLength(0);
        for (int k = 0; k < buffer.length; k += 4) {
            data1.setLength(0);
            for (int l = k; l < k + 4; l++) {
                String a = Integer.toHexString(buffer[l]);
                if (a.length() == 1) {
                    a = "0" + a;
                }
                if (a.length() > 2) {
                    a = a.substring(a.length() - 2);
                }
                data1.append(a);
            }
            Long i = Long.valueOf(data1.toString().toUpperCase(), 16);
            Float f = Float.intBitsToFloat(i.intValue());
            data.append(f);
            data.append(',');
        }
        data.append('\n');
        pw.write(data.toString());
        pw.flush();

    }

    void setBuffer(byte[] buffer) {
        writeData(buffer);
    }

    public PrintWriter getPw() {
        return pw;
    }

}
