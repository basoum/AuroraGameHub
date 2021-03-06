/*
 * Made By Sardonix Creative.
 *
 * This work is licensed under the
 * Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit
 *
 *      http://creativecommons.org/licenses/by-nc-nd/3.0/
 *
 * or send a letter to Creative Commons, 444 Castro Street, Suite 900,
 * Mountain View, California, 94041, USA.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package aurora.V1.core.screen_handler;

import aurora.V1.core.StoredSettings;
import aurora.V1.core.screen_logic.LibraryLogic;
import aurora.V1.core.screen_logic.SettingsLogic;
import aurora.V1.core.screen_ui.SettingsUI;
import aurora.engine.V1.Logic.ASound;
import aurora.engine.V1.Logic.AThreadWorker;
import aurora.engine.V1.Logic.AuroraScreenHandler;
import aurora.engine.V1.Logic.AuroraScreenLogic;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class SettingsHandler implements AuroraScreenHandler {

    private AuroraScreenLogic settingsLogic;

    private SettingsUI settingsUI;

    public static String WASD_SETTING = SettingsLogic.WASD_NAV_SETTING;

    public static String BACKGROUNDLOAD_SETTING = SettingsLogic.BACKGROUND_SEARCH_SETTING;

    public static String SOUNDFX_SETTING = SettingsLogic.SFX_SETTING;

    public static String GAMEPAD_SETTING = SettingsLogic.SFX_SETTING;

    private final StoredSettings storage;

    public SettingsHandler(SettingsUI aThis) {
        this.settingsUI = aThis;
        storage = settingsUI.getDashboardUI().getStorage().getStoredSettings();
    }

    @Override
    public void setLogic(AuroraScreenLogic logic) {
        this.settingsLogic = logic;
    }

    public class RefreshAuroraDBHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (storage.getSettingValue(SettingsLogic.SFX_SETTING).equals(
                    "enabled")) {
                int num = (int) (Math.random() * (2 - 1)) + 1;
                ASound sfx = new ASound("radio_on_" + num
                                        + ".wav", false);
                sfx.Play();
            }

            AThreadWorker worker = new AThreadWorker(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {


                    SettingsUI.lblSettingsStatus.setForeground(Color.cyan);
                    SettingsUI.lblSettingsStatus.setText(
                            "Updating Aurora Cover DB...");



                    if (settingsUI.getDashboardUI().getStartUI().getLogic()
                            .downloadAuroraDB()) {
                        // display completion message in green
                        SettingsUI.lblSettingsStatus.setForeground(Color.GREEN);
                        SettingsUI.lblSettingsStatus.setText("Update Complete");
                    } else {
                        // display error in red
                        SettingsUI.lblSettingsStatus.setForeground(Color.red);
                        SettingsUI.lblSettingsStatus.setText(
                                "Unable to Download");
                    }



                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(LibraryLogic.class
                                .getName()).
                                log(Level.SEVERE, null, ex);
                    }

                    if (SettingsUI.lblSettingsStatus.getCurrentText().equals(
                            "Update Complete") || SettingsUI.lblSettingsStatus
                            .getCurrentText().equals(
                                    "Unable to Download")) {
                        // Show default message after 1.5 seconds
                        SettingsUI.lblSettingsStatus.setForeground(
                                SettingsUI.DEFAULT_SETTINGS_COLOR);
                        SettingsUI.lblSettingsStatus.setText(
                                SettingsUI.DEAFULT_SETTINGS_STATUS);
                    }

                }
            });


            worker.startOnce();


        }

    }

    //-------------------- Background Game Search ---------------------------//
    public class EnableBackgroundGameSearchHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {


            if (storage.getSettingValue(SettingsLogic.SFX_SETTING).equals(
                    "enabled")) {
                int num = (int) (Math.random() * (2 - 1)) + 1;
                ASound sfx = new ASound("radio_on_" + num
                                        + ".wav", false);
                sfx.Play();
            }

            AThreadWorker worker = new AThreadWorker(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    storage.saveSetting(SettingsLogic.BACKGROUND_SEARCH_SETTING, "enabled");

                    SettingsUI.lblSettingsStatus.setForeground(Color.GREEN);
                    SettingsUI.lblSettingsStatus.setText(
                            "Background Game Search Enabled");

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(LibraryLogic.class
                                .getName()).
                                log(Level.SEVERE, null, ex);
                    }

                    if (SettingsUI.lblSettingsStatus.getCurrentText().equals(
                            "Background Game Search Enabled")) {
                        // Show default message after 1.5 seconds
                        SettingsUI.lblSettingsStatus.setForeground(
                                SettingsUI.DEFAULT_SETTINGS_COLOR);
                        SettingsUI.lblSettingsStatus.setText(
                                SettingsUI.DEAFULT_SETTINGS_STATUS);
                    }

                }
            });


            worker.startOnce();



        }

    }

    public class DisableBackgroundGameSearchHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (storage.getSettingValue(SettingsLogic.SFX_SETTING).equals(
                    "enabled")) {
                int num = (int) (Math.random() * (2 - 1)) + 1;
                ASound sfx = new ASound("radio_off_" + num
                                        + ".wav", false);
                sfx.Play();
            }

            AThreadWorker worker = new AThreadWorker(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    storage.saveSetting(SettingsLogic.BACKGROUND_SEARCH_SETTING, "disabled");

                    SettingsUI.lblSettingsStatus.setForeground(Color.red);
                    SettingsUI.lblSettingsStatus.setText(
                            "Background Game Search Disabled");

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(LibraryLogic.class
                                .getName()).
                                log(Level.SEVERE, null, ex);
                    }

                    if (SettingsUI.lblSettingsStatus.getCurrentText().equals(
                            "Background Game Search Disabled")) {
                        // Show default message after 1.5 seconds
                        SettingsUI.lblSettingsStatus.setForeground(
                                SettingsUI.DEFAULT_SETTINGS_COLOR);
                        SettingsUI.lblSettingsStatus.setText(
                                SettingsUI.DEAFULT_SETTINGS_STATUS);
                    }

                }
            });


            worker.startOnce();

        }

    }

    //----------------------- WASD Navigation --------------------------//
    public class EnableWASDNavigationHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (storage.getSettingValue(SettingsLogic.SFX_SETTING).equals(
                    "enabled")) {
                int num = (int) (Math.random() * (2 - 1)) + 1;
                ASound sfx = new ASound("radio_on_" + num
                                        + ".wav", false);
                sfx.Play();
            }

            AThreadWorker worker = new AThreadWorker(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {


                    storage.saveSetting(SettingsLogic.WASD_NAV_SETTING, "enabled");

                    SettingsUI.lblSettingsStatus.setForeground(Color.GREEN);
                    SettingsUI.lblSettingsStatus.setText(
                            "WASD Navigation Enabled");

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(LibraryLogic.class
                                .getName()).
                                log(Level.SEVERE, null, ex);
                    }

                    if (SettingsUI.lblSettingsStatus.getCurrentText().equals(
                            "WASD Navigation Enabled")) {
                        // Show default message after 1.5 seconds
                        SettingsUI.lblSettingsStatus.setForeground(
                                SettingsUI.DEFAULT_SETTINGS_COLOR);
                        SettingsUI.lblSettingsStatus.setText(
                                SettingsUI.DEAFULT_SETTINGS_STATUS);
                    }

                }
            });


            worker.startOnce();
        }

    }

    public class DisableWASDNavigationHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (storage.getSettingValue(SettingsLogic.SFX_SETTING).equals(
                    "enabled")) {
                int num = (int) (Math.random() * (2 - 1)) + 1;
                ASound sfx = new ASound("radio_off_" + num
                                        + ".wav", false);
                sfx.Play();
            }

            AThreadWorker worker = new AThreadWorker(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {


                    storage.saveSetting(SettingsLogic.WASD_NAV_SETTING, "disabled");

                    SettingsUI.lblSettingsStatus.setForeground(Color.red);
                    SettingsUI.lblSettingsStatus.setText(
                            "WASD Navigation Disabled");

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(LibraryLogic.class
                                .getName()).
                                log(Level.SEVERE, null, ex);
                    }

                    if (SettingsUI.lblSettingsStatus.getCurrentText().equals(
                            "WASD Navigation Disabled")) {
                        // Show default message after 1.5 seconds
                        SettingsUI.lblSettingsStatus.setForeground(
                                SettingsUI.DEFAULT_SETTINGS_COLOR);
                        SettingsUI.lblSettingsStatus.setText(
                                SettingsUI.DEAFULT_SETTINGS_STATUS);
                    }

                }
            });


            worker.startOnce();

        }

    }

    //----------------------- Gamepad Navigation --------------------------//
    public class EnableGamepadNavigationHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (storage.getSettingValue(SettingsLogic.GAMEPAD_SETTING).equals("enabled")) {
                int num = (int) (Math.random() * (2 - 1)) + 1;
                ASound sfx = new ASound("radio_on_" + num + ".wav", false);
                sfx.Play();
            }

            AThreadWorker worker = new AThreadWorker(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {


                    storage.saveSetting(SettingsLogic.GAMEPAD_SETTING, "enabled");

                    SettingsUI.lblSettingsStatus.setForeground(Color.GREEN);
                    SettingsUI.lblSettingsStatus.setText("Gamepad Navigation Enabled");

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(LibraryLogic.class
                                .getName()).
                                log(Level.SEVERE, null, ex);
                    }

                    if (SettingsUI.lblSettingsStatus.getCurrentText().equals(
                            "Gamepad Navigation Enabled")) {
                        // Show default message after 1.5 seconds
                        SettingsUI.lblSettingsStatus.setForeground(
                                SettingsUI.DEFAULT_SETTINGS_COLOR);
                        SettingsUI.lblSettingsStatus.setText(
                                SettingsUI.DEAFULT_SETTINGS_STATUS);
                    }

                }
            });


            worker.startOnce();
        }

    }

    public class DisableGamepadNavigationHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (storage.getSettingValue(SettingsLogic.GAMEPAD_SETTING).equals(
                    "enabled")) {
                int num = (int) (Math.random() * (2 - 1)) + 1;
                ASound sfx = new ASound("radio_off_" + num
                                        + ".wav", false);
                sfx.Play();
            }

            AThreadWorker worker = new AThreadWorker(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {


                    storage.saveSetting(SettingsLogic.GAMEPAD_SETTING, "disabled");

                    SettingsUI.lblSettingsStatus.setForeground(Color.red);
                    SettingsUI.lblSettingsStatus.setText(
                            "Gamepad Navigation Disabled");

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(LibraryLogic.class
                                .getName()).
                                log(Level.SEVERE, null, ex);
                    }

                    if (SettingsUI.lblSettingsStatus.getCurrentText().equals(
                            "Gamepad Navigation Disabled")) {
                        // Show default message after 1.5 seconds
                        SettingsUI.lblSettingsStatus.setForeground(
                                SettingsUI.DEFAULT_SETTINGS_COLOR);
                        SettingsUI.lblSettingsStatus.setText(
                                SettingsUI.DEAFULT_SETTINGS_STATUS);
                    }

                }
            });


            worker.startOnce();

        }

    }

    //------------------------- Sound Effects -----------------------//
    public class EnableSoundEffectsHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            int num = (int) (Math.random() * (2 - 1)) + 1;
            ASound sfx = new ASound("radio_on_" + num
                                    + ".wav", false);
            sfx.Play();


            AThreadWorker worker = new AThreadWorker(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {



                    storage.saveSetting(SettingsLogic.SFX_SETTING, "enabled");

                    SettingsUI.lblSettingsStatus.setForeground(Color.green);
                    SettingsUI.lblSettingsStatus.setText(
                            "Sound Effects Enabled");

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(LibraryLogic.class
                                .getName()).
                                log(Level.SEVERE, null, ex);
                    }

                    if (SettingsUI.lblSettingsStatus.getCurrentText().equals(
                            "Sound Effects Enabled")) {
                        // Show default message after 1.5 seconds
                        SettingsUI.lblSettingsStatus.setForeground(
                                SettingsUI.DEFAULT_SETTINGS_COLOR);
                        SettingsUI.lblSettingsStatus.setText(
                                SettingsUI.DEAFULT_SETTINGS_STATUS);
                    }

                }
            });


            worker.startOnce();
        }

    }

    public class DisableSoundEffectsHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            AThreadWorker worker = new AThreadWorker(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    storage.saveSetting(SettingsLogic.SFX_SETTING, "disabled");

                    SettingsUI.lblSettingsStatus.setForeground(Color.red);
                    SettingsUI.lblSettingsStatus.setText(
                            "Sound Effects Disabled");

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(LibraryLogic.class
                                .getName()).
                                log(Level.SEVERE, null, ex);
                    }


                    if (SettingsUI.lblSettingsStatus.getCurrentText().equals(
                            "Sound Effects Disabled")) {
                        // Show default message after 1.5 seconds
                        SettingsUI.lblSettingsStatus.setForeground(
                                SettingsUI.DEFAULT_SETTINGS_COLOR);
                        SettingsUI.lblSettingsStatus.setText(
                                SettingsUI.DEAFULT_SETTINGS_STATUS);
                    }

                }
            });


            worker.startOnce();
        }

    } // End Sound Effects

    //-------------------- Open Log Data Folder ---------------------------//
    public class OpenLogDataFolderHandler implements ActionListener {

        String logDataPath = System.getProperty("user.home") + "/AuroraData/Log Data";

        @Override
        public void actionPerformed(ActionEvent e) {

            AThreadWorker worker = new AThreadWorker(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {


                    SettingsUI.lblSettingsStatus.setForeground(Color.cyan);
                    SettingsUI.lblSettingsStatus.setText(
                            "Opening log data folder...");

//                    try {
//                        if (System.getProperty("os.name").contains("Windows")) {
//                            Runtime.getRuntime().exec(new String[]{"explorer.exe","/select," + logDataPath}).waitFor();
//                        } else {
//                            Runtime.getRuntime().exec(new String[]{"/usr/bin/open", logDataPath}).waitFor();
//                        }
//
//                    } catch (InterruptedException | IOException e1) {
//                    }

                    File dir = new File(logDataPath);
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.open(dir);
                    } catch (IOException ex) {
                        Logger.getLogger(SettingsHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (dir.exists()) {
                        // display completion message in green
                        SettingsUI.lblSettingsStatus.setForeground(Color.GREEN);
                        SettingsUI.lblSettingsStatus.setText("Log data folder opened");
                    } else {
                        // display error in red
                        SettingsUI.lblSettingsStatus.setForeground(Color.red);
                        SettingsUI.lblSettingsStatus.setText(
                                "Unable to open log data folder");
                    }



                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(LibraryLogic.class
                                .getName()).
                                log(Level.SEVERE, null, ex);
                    }

                    if (SettingsUI.lblSettingsStatus.getCurrentText().equals(
                            "Log data folder opened") || SettingsUI.lblSettingsStatus
                            .getCurrentText().equals(
                                    "Unable to open log data folder")) {
                        // Show default message after 1.5 seconds
                        SettingsUI.lblSettingsStatus.setForeground(
                                SettingsUI.DEFAULT_SETTINGS_COLOR);
                        SettingsUI.lblSettingsStatus.setText(
                                SettingsUI.DEAFULT_SETTINGS_STATUS);
                    }

                }
            });


            worker.startOnce();

        }

    }

    //------------------------- Minimize To Taskbar -----------------------//
    public class EnableMinimizeToTaskbarHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            int num = (int) (Math.random() * (2 - 1)) + 1;
            ASound sfx = new ASound("radio_on_" + num
                                    + ".wav", false);
            sfx.Play();


            AThreadWorker worker = new AThreadWorker(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {



                    storage.saveSetting(SettingsLogic.TASKBAR_MINIMIZE_SETTING, "enabled");

                    SettingsUI.lblSettingsStatus.setForeground(Color.green);
                    SettingsUI.lblSettingsStatus.setText(
                            "Minimize to Taskbar Enabled");

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(LibraryLogic.class
                                .getName()).
                                log(Level.SEVERE, null, ex);
                    }

                    if (SettingsUI.lblSettingsStatus.getCurrentText().equals(
                            "Minimize to Taskbar Enabled")) {
                        // Show default message after 1.5 seconds
                        SettingsUI.lblSettingsStatus.setForeground(
                                SettingsUI.DEFAULT_SETTINGS_COLOR);
                        SettingsUI.lblSettingsStatus.setText(
                                SettingsUI.DEAFULT_SETTINGS_STATUS);
                    }

                }
            });


            worker.startOnce();
        }

    }

    public class DisableMinimizeToTaskbarHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            AThreadWorker worker = new AThreadWorker(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    storage.saveSetting(SettingsLogic.TASKBAR_MINIMIZE_SETTING, "disabled");

                    SettingsUI.lblSettingsStatus.setForeground(Color.red);
                    SettingsUI.lblSettingsStatus.setText(
                            "Minimize to Taskbar Disabled");

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(LibraryLogic.class
                                .getName()).
                                log(Level.SEVERE, null, ex);
                    }


                    if (SettingsUI.lblSettingsStatus.getCurrentText().equals(
                            "Minimize to Taskbar Disabled")) {
                        // Show default message after 1.5 seconds
                        SettingsUI.lblSettingsStatus.setForeground(
                                SettingsUI.DEFAULT_SETTINGS_COLOR);
                        SettingsUI.lblSettingsStatus.setText(
                                SettingsUI.DEAFULT_SETTINGS_STATUS);
                    }

                }
            });


            worker.startOnce();
        }

    } // End Sound Effects

}
