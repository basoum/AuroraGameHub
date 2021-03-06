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
package aurora.V1.core;

import aurora.V1.core.screen_ui.LibraryUI;
import aurora.engine.V1.Logic.APostHandler;
import aurora.engine.V1.Logic.ASimpleDB;
import aurora.engine.V1.UI.AButton;
import aurora.engine.V1.UI.AImage;
import aurora.engine.V1.UI.AImagePane;
import java.awt.Color;
import java.net.MalformedURLException;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.swing.DefaultListModel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

/**
 * For the AddGameUI to search through the AuroraDB for games.
 *
 * @author Sammy
 */
public class GameSearch implements Runnable {

    public static String DEFAULT_SEARCH_TEXT = "Search For Game...";

    public static String DEFAULT_SEARCH_TEXT2 = "Enter Name Of Game...";

    private final AuroraCoreUI coreUI;

    private final LibraryUI libraryUI;

    private final ASimpleDB db;

    private String AppendedName = ""; //This is the concatenation of all characters

    private String foundGame;

    private static Game foundGameCover;

    private Thread typeThread;

    private int sleep;

    private Object[] foundArray;

    private final AuroraStorage storage;

    static final Logger logger = Logger.getLogger(GameSearch.class);

    private AImagePane imgBlankCover;

    private AImagePane pnlGameCoverPane;

    private DefaultListModel listModel;

    private AImage imgStatus;

    private JTextField txtSearch;

    private Game staticGameCover;

    private boolean canEditCover = true;

    private AImage statusIcon;

    private boolean isSearchEnabled = true;

    private boolean isStatusChangeEnabled = true;

    private AButton statusButton;

    public GameSearch(LibraryUI libraryUI, ASimpleDB database) {

        this.coreUI = libraryUI.getCoreUI();
        this.db = database;
        this.storage = libraryUI.getStorage();
        this.libraryUI = libraryUI;

    }

    public void setUpGameSearch(AImagePane imgBlank, AImagePane coverPane,
                                DefaultListModel model, AImage status,
                                JTextField textField) {

        this.imgBlankCover = imgBlank;
        this.pnlGameCoverPane = coverPane;
        this.listModel = model;
        this.imgStatus = status;
        this.txtSearch = textField;

    }

    public void setUpGameSearch(AImagePane imgBlank, AImagePane coverPane,
                                DefaultListModel model,
                                JTextField textField) {

        this.imgBlankCover = imgBlank;
        this.pnlGameCoverPane = coverPane;
        this.listModel = model;
        isStatusChangeEnabled = false;
        this.txtSearch = textField;

    }

    // Reset text, Cover Image, List and turn notification to red
    public void resetCover() {

        if (staticGameCover == null || staticGameCover.getCoverURL() == null) {
            // Create the new GameCover object
            staticGameCover = new Game(libraryUI.getGridManager(), coreUI,
                                       libraryUI
                                       .getDashboardUI(), storage);
            staticGameCover.setCoverUrl("library_noGameFound.png");

            staticGameCover.setCoverSize(imgBlankCover
                    .getImageWidth(), imgBlankCover.getImageHeight());

            try {
                staticGameCover.update();

            } catch (MalformedURLException ex) {
                logger.error(ex);
            }
        }

        if (isSearchEnabled) {

            staticGameCover.removeOverlayUI();
            // Allow for custom editing cover art
            if (canEditCover) {
                staticGameCover.enableEditCoverOverlay();
                staticGameCover.getBtnAddCustomOverlay()
                        .addActionListener(
                                libraryUI.getHandler().new GameCoverEditListner(
                                        staticGameCover));
            }

            pnlGameCoverPane.removeAll();
            pnlGameCoverPane.revalidate();
            pnlGameCoverPane.add(staticGameCover);
            pnlGameCoverPane.revalidate();
            pnlGameCoverPane.repaint();
            imgBlankCover.repaint();

            AppendedName = "";
            foundGame = "";

            foundArray = null;
            listModel.removeAllElements();
            if (isStatusChangeEnabled) {
                imgStatus.setImgURl("addUI_badge_idle.png");
            }
            libraryUI.getLogic().checkManualAddGameStatus();
        }



    }

    public void resetText() {
        if (txtSearch.getText().length() < 1) {
            txtSearch.setForeground(Color.darkGray);
            txtSearch.setText(
                    GameSearch.DEFAULT_SEARCH_TEXT);
        }
    }

    public void setAppendedName(String AppendedName) {
        this.AppendedName = AppendedName;
        if (logger.isDebugEnabled()) {
            logger.debug("Appended name: " + AppendedName);
        }

        // Remove ONE Character From End of Appended Name
        if (AppendedName.length() <= 0) {
            if (isSearchEnabled) {
                resetCover();
                searchGame();
            }

        } // Start search only when more than 1 character is typed
        else if (AppendedName.length() > 0) {

            //Delay to allow for typing
            if (AppendedName.length() == 1) {
                sleep = 300;
            } else {
                sleep = 260;
            }
            if (typeThread == null) {
                typeThread = new Thread(this);
            }

            // Start Search thread with Delay
            try {
                if (!typeThread.isAlive()) {
                    typeThread.start();
                }
            } catch (IllegalThreadStateException ex) {
                ex.printStackTrace();
            }

        }
    }

    /**
     * Check game exists in users Game DB
     * <p>
     * @param gameName
     *                 <p>
     * @return Boolean true or false whether it exists
     */
    public Boolean checkGameExist(String gameName) {

        try {
            foundGame = (String) db.getRowFlex("AuroraTable", new String[]{
                "FILE_NAME"}, "GAME_NAME='" + gameName
                                               .replace("'", "''") + "'",
                                               "FILE_NAME")[0];
        } catch (Exception ex) {
            logger.error(ex);
            foundGame = null;
        }

        if (foundGame == null) {
            return false;
        } else {
            return true;
        }
    }

    public AImagePane getSpecificGame(String gameImageName) {

        if (isSearchEnabled) {

            if (statusIcon != null) {
                statusIcon.setImgURl("addUI_img_autoSearchLooking.png");
                if (main.LAUNCHES < 20) {
                    statusButton.setToolTipText("Searching AuroraCoverDB...");
                }
            }

            // If not found show Placeholder and turn notification red
            if (gameImageName == null) {

                pnlGameCoverPane.removeAll();
                if (staticGameCover == null) {
                    // Create the new GameCover object
                    staticGameCover = new Game(libraryUI.getGridManager(), coreUI,
                                               libraryUI
                                               .getDashboardUI(), storage);
                    staticGameCover.setCoverUrl("library_noGameFound.png");

                    staticGameCover.setCoverSize(imgBlankCover
                            .getImageWidth(), imgBlankCover.getImageHeight());

                    try {
                        staticGameCover.update();
                        staticGameCover.removeOverlayUI();

                        if (canEditCover) {
                            staticGameCover.enableEditCoverOverlay();
                            staticGameCover.getBtnAddCustomOverlay()
                                    .addActionListener(
                                            libraryUI.getHandler().new GameCoverEditListner(staticGameCover));
                        }
                    } catch (MalformedURLException ex) {
                        logger.error(ex);
                    }
                }
                pnlGameCoverPane.add(staticGameCover);
                //Change notification
                if (isStatusChangeEnabled) {
                    imgStatus.setImgURl("addUI_badge_invalid.png");
                }
                pnlGameCoverPane.repaint();
                pnlGameCoverPane.revalidate();
                staticGameCover.revalidate();

                statusIcon.setImgURl("addUI_img_autoSearchOn.png");
                if (main.LAUNCHES < 20) {
                    statusButton.setToolTipText("Aurora Cover DB is Enabled");
                }


                return staticGameCover;

                // Show the game Cover if a single database item is found
            } else {

                pnlGameCoverPane.removeAll();
                // Create the new GameCover object
                foundGameCover = new Game(libraryUI.getGridManager(), coreUI,
                                          libraryUI
                                          .getDashboardUI(), storage);
                foundGameCover.setCoverUrl(gameImageName);

                foundGameCover.setCoverSize(imgBlankCover
                        .getImageWidth(), imgBlankCover.getImageHeight());

                pnlGameCoverPane.add(foundGameCover);
                try {
                    foundGameCover.update();
                    foundGameCover.removeOverlayUI();

                    // Enable editing of cover art
                    if (canEditCover) {
                        foundGameCover.enableEditCoverOverlay();
                        foundGameCover.getBtnAddCustomOverlay()
                                .addActionListener(
                                        libraryUI.getHandler().new GameCoverEditListner(
                                                foundGameCover));
                    }
                } catch (MalformedURLException ex) {
                    logger.error(ex);
                }

                // Change notification
                if (isStatusChangeEnabled) {
                    imgStatus.setImgURl("addUI_badge_valid.png");
                }
                pnlGameCoverPane.repaint();
                pnlGameCoverPane.revalidate();

                foundGameCover.setPostLoad(new APostHandler() {

                    @Override
                    public void doAction() {
                        if (statusIcon != null) {
                            statusIcon.setImgURl("addUI_img_autoSearchOn.png");
                            if (main.LAUNCHES < 20) {
                                statusButton.setToolTipText(
                                        "Aurora Cover DB is Enabled");
                            }
                        }
                    }
                });


                return foundGameCover;
            }
        } else {
            return null;
        }
    }

    /**
     * Search from outside Class using specific String
     *
     * @param gameName the name of the Game you want to search for
     *
     */
    public AImagePane searchSpecificGame(String gameName) {

        if (isSearchEnabled) {

            statusIcon.setImgURl("addUI_img_autoSearchLooking.png");
            if (main.LAUNCHES < 20) {
                statusButton.setToolTipText("Searching AuroraCoverDB...");
            }

            try {
                foundGame = (String) db.getRowFlex("AuroraTable", new String[]{
                    "FILE_NAME"}, "GAME_NAME='" + gameName
                                                   .replace("'", "''") + "'",
                                                   "FILE_NAME")[0];
            } catch (Exception ex) {
                logger.error(ex);
                foundGame = null;
            }

            foundGameCover = null;

            // If not found show Placeholder and turn notification red
            if (foundGame == null) {

                pnlGameCoverPane.removeAll();
                if (staticGameCover == null) {
                    // Create the new GameCover object
                    staticGameCover = new Game(libraryUI.getGridManager(), coreUI,
                                               libraryUI
                                               .getDashboardUI(), storage);
                    staticGameCover.setCoverUrl("library_noGameFound.png");

                    staticGameCover.setCoverSize(imgBlankCover
                            .getImageWidth(), imgBlankCover.getImageHeight());

                    staticGameCover.setGameName(gameName);

                    try {
                        staticGameCover.update();
                        staticGameCover.removeOverlayUI();

                        // Enable editing of cover art
                        if (canEditCover) {
                            staticGameCover.enableEditCoverOverlay();
                            staticGameCover.getBtnAddCustomOverlay()
                                    .addActionListener(
                                            libraryUI.getHandler().new GameCoverEditListner(
                                                    staticGameCover));
                        }
                    } catch (MalformedURLException ex) {
                        logger.error(ex);
                    }
                }

                pnlGameCoverPane.add(staticGameCover);
                // Change notification
                if (isStatusChangeEnabled) {
                    imgStatus.setImgURl("addUI_badge_invalid.png");
                }
                pnlGameCoverPane.repaint();
                pnlGameCoverPane.revalidate();
                staticGameCover.revalidate();

                statusIcon.setImgURl("addUI_img_autoSearchOn.png");
                if (main.LAUNCHES < 20) {
                    statusButton.setToolTipText("Aurora Cover DB is Enabled");
                }
                return staticGameCover;

                // Show the game Cover if a single database item is found
            } else {

                pnlGameCoverPane.removeAll();
                // Create the new GameCover object
                foundGameCover = new Game(libraryUI.getGridManager(), coreUI,
                                          libraryUI
                                          .getDashboardUI(), storage);
                foundGameCover.setCoverUrl(foundGame);

                foundGameCover.setCoverSize(imgBlankCover
                        .getImageWidth(), imgBlankCover.getImageHeight());

                foundGameCover.setGameName(gameName);

                pnlGameCoverPane.add(foundGameCover);
                try {
                    foundGameCover.update();
                    foundGameCover.removeOverlayUI();

                    // Enable editing of cover art
                    if (canEditCover) {
                        foundGameCover.enableEditCoverOverlay();
                        foundGameCover.getBtnAddCustomOverlay()
                                .addActionListener(
                                        libraryUI.getHandler().new GameCoverEditListner(
                                                foundGameCover));
                    }
                } catch (MalformedURLException ex) {
                    logger.error(ex);
                }

                // Change notification

                if (isStatusChangeEnabled) {
                    imgStatus.setImgURl("addUI_badge_valid.png");
                }


                pnlGameCoverPane.repaint();
                pnlGameCoverPane.revalidate();
                foundGameCover.revalidate();

                statusIcon.setImgURl("addUI_img_autoSearchOn.png");
                if (main.LAUNCHES < 20) {
                    statusButton.setToolTipText("Aurora Cover DB is Enabled");
                }

                return foundGameCover;
            }

        } else {

            return null;
        }
    }

    /**
     * Searches for games similar to the selected one.
     * Returns an array
     * [0] contains possible game name
     * [1] contains possible game names game path
     * <p>
     * @param gameName
     *                 <p>
     * @return
     */
    public Object[] searchSimilarGame(String gameName) {

        String possibleGameName = null;
        String possibleGameImageName = null;
        int attempt = 0;
        boolean findExact = true;
        try {

            String tableName = "AuroraTable";
            String columnCSV = "FILE_NAME";


            String savedGameName = null;
            while (attempt >= 0) {

                String whereQuery = "GAME_NAME='" + gameName.replace("'", "''")
                                    + "'";
                ResultSet rs = null;
                rs = db.flexQuery("SELECT " + columnCSV + " FROM "
                                  + tableName + " WHERE " + whereQuery);

                // Check if found a match
                if (rs.getRow() > 0) {
                    Array a = rs.getArray(columnCSV);
                    Object[] array = (Object[]) a.getArray();
                    possibleGameName = gameName;
                    possibleGameImageName = (String) array[0];
                    break;
                } else { // If no match found, change game name a little



                    switch (attempt) {
                        case 0: // First attempt: remove garbage characters
                            if (gameName.matches("^.*[©®™°²³º¼½¾].*$")) {
                                gameName = gameName.replaceAll("[©®™°²³º¼½¾]",
                                                               "");
                                break;
                            }
                        case 1: // Second attempt: add spaces between Letters
                            savedGameName = gameName;
                            gameName = addSpaces(gameName);
                            break;
                        case 2: // Last attempt: remove one letter at a time untill it works
                            findExact = false;
                            gameName = savedGameName;
                            possibleGameName = gameName;
                            possibleGameImageName = reductiveSearch(gameName);
                            attempt = -2;
                            break;
                        default:
                            attempt = -2;
                            break;

                    }
                }
                attempt++;
            }

        } catch (SQLException ex) {
            logger.error(ex);
            foundGame = null;
        }

        Object[] returnArray = new Object[3];

        returnArray[0] = possibleGameName;
        returnArray[1] = possibleGameImageName;
        returnArray[2] = findExact;

        if (possibleGameName == null) {
            returnArray = null;
        }
        if (db != null) {
            db.CloseConnection();
        }
        return returnArray;
    }

    private String addSpaces(String text) {
        String tempString = text;
        String modifiedText = text;

        while (tempString.length() > 1) {
            int spaceIndex = 0; // location of spaces

            Character c = tempString.charAt(tempString.length() - 1);

            // Check if Upper case is detected
            if (Character.isUpperCase(c)
                && tempString.length() - 2 > 0) {

                // Afterward check if previous char is lowercase
                Character c2 = tempString.charAt(tempString.length() - 2);

                if (Character.isLowerCase(c2)) {
                    // Need to add a space in between them.
                    spaceIndex = tempString.length() - 1;

                    // Add space
                    modifiedText = modifiedText.substring(0, spaceIndex)
                                   + " " + modifiedText.substring(
                                    spaceIndex,
                                    modifiedText
                                    .length());

                }

            }

            // Remove one character each time
            tempString = tempString.substring(0, tempString.length() - 1);
        }

        return modifiedText.trim();

    }

    /**
     * Searches approximate game by removing characters each time
     * <p>
     * @param gameName
     *                 <p>
     * @return
     */
    private String reductiveSearch(String gameName) {

        String tableName = "AuroraTable";
        String column = "FILE_NAME";
        String whereQuery = "GAME_NAME";
        String tempName = gameName;
        String gameImagePath = null;
        for (int i = 0; i < gameName.length(); i++) {

            ResultSet rs = null;
            try {
                rs = db.flexQuery("SELECT "
                                  + column
                                  + " FROM "
                                  + tableName
                                  + " WHERE "
                                  + whereQuery
                                  + " LIKE '%"
                                  + tempName
                        .replace("'", "''") + "%'");

                // Check if found a match
                if (rs != null && rs.getRow() > 0) {
                    Array a = rs.getArray(column);
                    Object[] array = (Object[]) a.getArray();
                    gameImagePath = (String) array[0];
                    break;
                } else {

                    tempName = tempName.substring(0, tempName.length() - 1);
                }
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(GameSearch.class
                        .getName())
                        .log(Level.SEVERE, null, ex);
            }

        }

        return gameImagePath;

    }

    private void searchGame() {

        if (isSearchEnabled) {

            statusIcon.setImgURl("addUI_img_autoSearchLooking.png");
            if (main.LAUNCHES < 20) {
                statusButton.setToolTipText("Searching AuroraCoverDB...");
            }

            // What Happends When The Length is zero
            if (AppendedName.length() <= 0 || txtSearch.getText()
                    .length() == 0) {

                if (logger.isDebugEnabled()) {
                    logger.debug("RESETTING PANE");
                }

                resetCover();
                pnlGameCoverPane.repaint();
                pnlGameCoverPane.revalidate();
            } else {
                listModel.removeAllElements();

                // Query the database
                try {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Searching for" + AppendedName.toString());
                    }

                    foundArray = db.searchAprox("AuroraTable", "FILE_NAME",
                                                "GAME_NAME", AppendedName
                                                .toString());
                } catch (SQLException ex) {
                    logger.error(ex);
                }
                try {
                    // Get the first game name as a seperate string to show
                    // in cover Art
                    foundGame = (String) foundArray[0];
                    if (logger.isDebugEnabled()) {
                        logger.debug(foundGame);
                    }

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {

                            //Add rest of found items to the List to allow for selection of other games
                            for (int i = 0; i <= 10 && i < foundArray.length;
                                    i++) {
                                if (foundArray[i] != null) {
                                    String gameItem = (String) foundArray[i];
                                    String gameName = gameItem
                                            .replace("-", " ").replace("+",
                                                                       " ")
                                            .replace(".png",
                                                     "");
                                    if (!listModel.contains(gameName)) {
                                        listModel.addElement(gameName);
                                    }
                                }
                            }

                        }
                    });
                } catch (Exception ex) {
                    foundGame = null;
                }

                // If Can't Get the game then show a Placeholder Image
                // and turn the notifier red
                if (foundGame == null) {

                    pnlGameCoverPane.removeAll();
                    if (staticGameCover == null) {
                        // Create the new GameCover object
                        staticGameCover = new Game(libraryUI.getGridManager(),
                                                   coreUI,
                                                   libraryUI
                                                   .getDashboardUI(), storage);
                        staticGameCover.setCoverUrl("library_noGameFound.png");

                        staticGameCover.setCoverSize(imgBlankCover
                                .getImageWidth(), imgBlankCover.getImageHeight());

                        try {
                            staticGameCover.update();
                            staticGameCover.removeOverlayUI();

                            // Allow for custom editing cover art
                            if (canEditCover) {
                                staticGameCover.enableEditCoverOverlay();
                                staticGameCover.getBtnAddCustomOverlay()
                                        .addActionListener(
                                                libraryUI.getHandler().new GameCoverEditListner(
                                                        staticGameCover));
                            }
                        } catch (MalformedURLException ex) {
                            logger.error(ex);
                        }
                    }

                    pnlGameCoverPane.add(staticGameCover);
                    // Change notification
                    imgStatus.setImgURl("addUI_badge_invalid.png");
                    pnlGameCoverPane.repaint();
                    pnlGameCoverPane.revalidate();
                    staticGameCover.revalidate();

                } else if (foundGame != null) {

                    pnlGameCoverPane.removeAll();

                    // Set up GameCover object with First Database item found
                    foundGameCover = new Game(libraryUI.getGridManager(), coreUI,
                                              libraryUI.getDashboardUI(),
                                              storage);
                    foundGameCover.setCoverUrl(foundGame); //use seperate string

                    foundGameCover.setCoverSize(imgBlankCover
                            .getImageWidth(), imgBlankCover
                                                .getImageHeight());
                    foundGameCover.setGameName(foundGame.replace("-", " ")
                            .replace("+", " ")
                            .replace(
                                    ".png", ""));

                    pnlGameCoverPane.add(foundGameCover);
                    // Show GameCover
                    try {
                        foundGameCover.update();
                        foundGameCover.removeOverlayUI();

                        // Enable the ability to edit cover art
                        if (canEditCover) {
                            foundGameCover.enableEditCoverOverlay();
                            foundGameCover.getBtnAddCustomOverlay()
                                    .addActionListener(
                                            libraryUI.getHandler().new GameCoverEditListner(
                                                    foundGameCover));
                        }
                    } catch (MalformedURLException ex) {
                        logger.error(ex);
                    }

                    // Turn notifier Green
                    imgStatus.setImgURl("addUI_badge_valid.png");
                    libraryUI.getLogic().checkManualAddGameStatus();
                    pnlGameCoverPane.repaint();
                    pnlGameCoverPane.revalidate();
                }
            }

            statusIcon.setImgURl("addUI_img_autoSearchOn.png");
            if (main.LAUNCHES < 20) {
                statusButton.setToolTipText("Aurora Cover DB is Enabled");
            }
        } else {
            listModel.clear();

            // Check if Game was inddeed added to the AddGameUI
            if (!(pnlGameCoverPane.getComponent(0) instanceof Game)) {

                pnlGameCoverPane.removeAll();

                if (staticGameCover == null) {
                    // Create the new GameCover object
                    staticGameCover = new Game(libraryUI.getGridManager(), coreUI,
                                               libraryUI
                                               .getDashboardUI(), storage);
                    staticGameCover.setCoverUrl("library_noGameFound.png");

                    staticGameCover.setCoverSize(imgBlankCover
                            .getImageWidth(), imgBlankCover.getImageHeight());

                    try {
                        staticGameCover.update();

                        // Allow for custom editing cover art
                        if (canEditCover) {
                            staticGameCover.enableEditCoverOverlay();
                            staticGameCover.getBtnAddCustomOverlay()
                                    .addActionListener(
                                            libraryUI.getHandler().new GameCoverEditListner(
                                                    staticGameCover));
                        }


                    } catch (MalformedURLException ex) {
                        logger.error(ex);
                    }
                }

                staticGameCover.removeOverlayUI();
                pnlGameCoverPane.add(staticGameCover);

                // Change status
                imgStatus.setImgURl("addUI_badge_invalid.png");
                pnlGameCoverPane.repaint();
                pnlGameCoverPane.revalidate();
                staticGameCover.revalidate();

                // Check if there is any value in text to validate
            } else if (txtSearch.getText().length() != 0
                       && !txtSearch.getText().equals(
                            DEFAULT_SEARCH_TEXT)) {


                // Change status
                imgStatus.setImgURl("addUI_badge_valid.png");
                pnlGameCoverPane.repaint();
                pnlGameCoverPane.revalidate();
                staticGameCover.revalidate();
                if (foundGameCover != null) {
                    foundGameCover.setGameName(txtSearch.getText());
                } else {
                    AppendedName = txtSearch.getText();
                }
                // If no text or default text then its not valid for adding to lib
            } else {
                // Change status
                imgStatus.setImgURl("addUI_badge_invalid.png");
                pnlGameCoverPane.repaint();
                pnlGameCoverPane.revalidate();
            }
        }
    }

    public void enableSearch() {
        isSearchEnabled = true;

        statusIcon.setImgURl("addUI_img_autoSearchOn.png");
        if (main.LAUNCHES < 20) {
            statusButton.setToolTipText("Aurora Cover DB is Enabled");
        }

        DEFAULT_SEARCH_TEXT = "Search For Game...";
        if (txtSearch.getText().equals(DEFAULT_SEARCH_TEXT2)) {
            txtSearch.setText(DEFAULT_SEARCH_TEXT);
        }

    }

    public void disableSearch() {

        isSearchEnabled = false;

        searchGame();

        staticGameCover.removeOverlayUI();
        // Allow for custom editing cover art
        if (canEditCover) {
            staticGameCover.enableEditCoverOverlay();
            staticGameCover.getBtnAddCustomOverlay()
                    .addActionListener(
                            libraryUI.getHandler().new GameCoverEditListner(
                                    staticGameCover));
        }

        pnlGameCoverPane.removeAll();
        pnlGameCoverPane.revalidate();
        pnlGameCoverPane.add(staticGameCover);
        pnlGameCoverPane.revalidate();
        pnlGameCoverPane.repaint();
        imgBlankCover.repaint();

        statusIcon.setImgURl("addUI_img_autoSearchOff.png");
        if (main.LAUNCHES < 20) {
            statusButton.setToolTipText("Aurora Cover DB is Disabled");
        }

        if (txtSearch.getText().equals(DEFAULT_SEARCH_TEXT)) {
            txtSearch.setText(DEFAULT_SEARCH_TEXT2);
        }
        DEFAULT_SEARCH_TEXT = DEFAULT_SEARCH_TEXT2;


    }

    public boolean isSearchEnabled() {
        return isSearchEnabled;
    }

    @Override
    public void run() {

        while (Thread.currentThread() == typeThread) {
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("WAITING FOR SEARCH");
                }

                Thread.sleep(sleep);
            } catch (InterruptedException ex) {
                logger.error(ex);
            }
            break;
        }
        searchGame();
        typeThread = null;
    }

    public String getAppendedName() {
        return AppendedName;
    }

    public Game getFoundGameCover() {
        return foundGameCover;
    }

    public Game getStaticGameCover() {
        return staticGameCover;
    }

    public Game getCurrentlySearchedGame() {
        return (Game) pnlGameCoverPane.getComponent(0);
    }

    public void setCanEditCover(boolean canEditCover) {
        this.canEditCover = canEditCover;
    }

    public void setStatusIcon(AImage icon) {
        this.statusIcon = icon;
    }

    public JTextField getTxtSearch() {
        return txtSearch;
    }

    public void setStatusIcon(AImage icon, AButton btn) {
        this.statusIcon = icon;
        this.statusButton = btn;
    }

}
