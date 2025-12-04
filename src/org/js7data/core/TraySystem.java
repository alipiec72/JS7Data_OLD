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

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import static org.js7data.core.DataInServer.getStackTrace;

/**
 *
 * @author Aleksander Lipiec
 */
@SuppressWarnings("ClassWithMultipleLoggers")
class TraySystem extends JFrame implements Serializable {

    private static final long serialVersionUID = 6L;
    final PopupMenu popup = new PopupMenu();
    transient TrayIcon trayIcon = new TrayIcon(createImage("/org/js7data/resources/ac-adapter.png", "tray icon"));
    transient SystemTray tray = SystemTray.getSystemTray();
    private static final Logger LOGGER_ERR = Logger.getLogger("LOG_ERR.log");
    transient Preferences root = Preferences.userRoot();
    private final transient Preferences node = root.node("/org/js7data");
    private final JFrame jFrame;
    private final DataInServer dataInServer;

    TraySystem(JFrame jFrame, DataInServer dataInServer) {
        this.jFrame = jFrame;
        this.dataInServer = dataInServer;
    }

    public void createTryIcon() {
        if (!SystemTray.isSupported()) {
            LOGGER_ERR.log(Level.SEVERE,"SystemTray is not supported");
            return;
        }
        
        MenuItem shutDownItem = new MenuItem("ShutDown");
        shutDownItem.setFont(new Font("Tahoma", Font.PLAIN, 11));
        popup.add(shutDownItem);
        trayIcon.setPopupMenu(popup);
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Akwizycja danych v " + node.get("Setings.version", null));

        try {
            tray.add(trayIcon);
        } catch (AWTException ex) {
            String exceptionMessage = getStackTrace(ex);
            LOGGER_ERR.log(Level.SEVERE, exceptionMessage);
            return;
        }

        trayIcon.addActionListener((ActionEvent e) -> {
            if (!jFrame.isVisible()) {
                jFrame.setVisible(true);
            }
            if (jFrame.getExtendedState() == ICONIFIED) {
                jFrame.setExtendedState(NORMAL);
            }
        });

        shutDownItem.addActionListener((ActionEvent e) -> {
            tray.remove(trayIcon);
            if (dataInServer != null) {
                dataInServer.finito();
            }
            jFrame.dispose();
            System.exit(0);
        });
    }

    public void displayMessage(String caption, String message, MessageType typ) {
        trayIcon.displayMessage(caption, message, typ);
    }

    protected static Image createImage(String path, String description) {
        URL imageURL = Main.class.getResource(path);
        if (imageURL == null) {
            Logger.getLogger("LOG_ERR.log").logp(Level.SEVERE, "TraySystem", "createImage", ("Resource not found: " + path));
            LOGGER_ERR.log(Level.SEVERE, () -> ("Resource not found: " + path));
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}
