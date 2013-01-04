/*
 * Copyright 2012 Sardonix Creative.
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
package aurora.V1.core.screen_logic;

import aurora.V1.core.AuroraCoreUI;
import aurora.V1.core.AuroraStorage;
import aurora.V1.core.Game;
import aurora.V1.core.screen_handler.DashboardHandler;
import aurora.V1.core.screen_ui.DashboardUI;
import aurora.V1.core.screen_ui.GameLibraryUI;
import aurora.V1.core.screen_ui.GamerProfileUI;
import aurora.V1.core.screen_ui.SettingsUI;
import aurora.engine.V1.Logic.ARssReader;
import aurora.engine.V1.Logic.ARssReader.Feed;
import aurora.engine.V1.Logic.AThreadWorker;
import aurora.engine.V1.Logic.AuroraScreenHandler;
import aurora.engine.V1.Logic.AuroraScreenLogic;
import aurora.engine.V1.UI.ACarouselPane;
import aurora.engine.V1.UI.AImagePane;
import aurora.engine.V1.UI.AInfoFeedLabel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * .------------------------------------------------------------------------.
 * | DashboardLogic
 * .------------------------------------------------------------------------.
 * |
 * |
 * | This Class is the Logic component of the Dashboard Screen. Its instanced
 * | In DashboardUI.
 * |
 * | This class is supposed to handle all of the Long Processing of UI or
 * | Actions generated by the Handler. Reusable processing and long logic
 * | methods should go here.
 * |
 * .........................................................................
 *
 * @author Sammy Guergachi <sguergachi at gmail.com>
 * @author Carlos Machado <camachado@gmail.com>
 * <p/>
 */
public class DashboardLogic implements AuroraScreenLogic {

    /**
     * The UI Component of the Dashboard Screen.
     */
    private final DashboardUI dashboardUI;

    /**
     * The Handler Component of the Dashboard Screen.
     */
    private DashboardHandler dashboardHandler;

    /**
     * The AuroraStorage instance from DashboardUI.
     */
    private final AuroraStorage storage;

    /**
     * The AuroraCoreUI instance from DashboardUI.
     */
    private final AuroraCoreUI coreUI;

    /**
     * Instance of the GameLibrary UI.
     */
    private GameLibraryUI libraryUI;

    /**
     * Instance of the ProfileUI.
     */
    private GamerProfileUI profileUI;

    /**
     * Instance of the SettingsUI.
     */
    private SettingsUI settingsUI;

    private ARssReader rssReader;

    private Feed auroraGameHubFeed;

    /**
     * .-----------------------------------------------------------------------.
     * | DashboardLogic(DashboardUI)
     * .-----------------------------------------------------------------------.
     * |
     * | This is the Constructor of the Dashboard Logic class.
     * |
     * | The DashboardUI is required to make adjustments to the UI from the
     * | logic.
     * | The storage will be extracted from DashboardUI and initialized
     * | here.
     * | CoreUI will also be internally initialized here and extracted
     * | from DashboardUI.
     * |
     * | NOTE: for Logic to work you must use the set(HandlerDashboardHandler)
     * | method for the logic to be able to attach some handlers to UI
     * | elements
     * |
     * .........................................................................
     *
     * @param dashboardUi DashboardUI
     *
     */
    public DashboardLogic(final DashboardUI dashboardUi) {

        this.dashboardUI = dashboardUi;
        this.coreUI = dashboardUI.getCoreUI();

        this.storage = dashboardUI.getStorage();

        this.rssReader = new ARssReader();

        loadAuroraApps();
    }

    @Override
    public final void setHandler(final AuroraScreenHandler handler) {
        this.dashboardHandler = (DashboardHandler) handler;
    }

    /**
     * .-----------------------------------------------------------------------.
     * | getLibraryIcon()
     * .-----------------------------------------------------------------------.
     * |
     * | This method tries to generate a random game if the storage contains
     * | any games.
     * |
     * | If no games are found in storage it will return a simple blank case
     * | icon
     * |
     * .........................................................................
     *
     * @return an ArrayList with info
     */
    public final AImagePane getLibraryIcon() {

        AImagePane icon;

        //* Double check there are no games in Storage *//
        System.out.println(storage);
        if (storage.getStoredLibrary().getBoxArtPath() == null || storage.
                getStoredLibrary().getBoxArtPath().isEmpty()) {

            //* Set icon to Blank Case *//
            icon = new AImagePane("Blank-Case.png",
                    dashboardUI.getGameCoverWidth() - 10, dashboardUI.
                    getGameCoverHeight() - 10);

        } else {
            Random rand = new Random();

            //* Generate random num based on number of games in storage *//
            int randomNum = rand.nextInt(dashboardUI.getStorage().
                    getStoredLibrary().
                    getGameNames().size());

            //* Get the random game *//
            Game randomGame = new Game(dashboardUI.getStorage().
                    getStoredLibrary().
                    getBoxArtPath().
                    get(randomNum), dashboardUI);
            randomGame.setCoverSize(dashboardUI.getGameCoverWidth() - 10,
                    dashboardUI.getGameCoverHeight() - 10);
            try {
                randomGame.update();
            } catch (MalformedURLException ex) {
                Logger.getLogger(DashboardUI.class.getName()).log(Level.SEVERE,
                        null, ex);
            }

            //* Disable overlay UI of Game *//
            randomGame.removeInteraction();
            //* Instead, when clicking on game, launch appropriate App *//
            randomGame.getInteractivePane().
                    addMouseListener(
                    dashboardHandler.new CarouselLibraryMouseListener());

            //* Now give icon the cleaned up Random game *//
            icon = randomGame;
        }

        return icon;

    }

    /**
     * .-----------------------------------------------------------------------.
     * | createFeed(ArrayList<String>) --> ArrayList <String>
     * .-----------------------------------------------------------------------.
     * |
     * | This method takes an array and fills it up with field for the info
     * | feed to output.
     * |
     * | An ArrayList which should contain nothing is required and in the output
     * | An ArrayList filled with latest info is given. This ArrayList should go
     * | into the InfoFeed component.
     * |
     * | If No ArrayList is provided (null) then this method will be super smart
     * | and not crash and totally be nice by creating one for you then
     * | offering it to you filled with sweet info totally for free
     * |
     * .........................................................................
     *
     * @param array ArrayList
     * <p/>
     * @return an ArrayList with info
     */
    public final ArrayList<String> createFeed(final ArrayList<String> array) {

        ArrayList<String> Array = null;

        if (array == null) {
            Array = new ArrayList<String>();
        } else {
            Array = array;
        }


        try {
            ARssReader.RSSFeedParser auroraGameHubParser = rssReader.new RSSFeedParser(
                    "http://www.rssmix.com/u/3621720/rss.xml");
            auroraGameHubFeed = auroraGameHubParser.readFeed();
        } catch (Exception ex) {
            //* fall back if above feed mixer dies *//
            ARssReader.RSSFeedParser auroraGameHubParser = rssReader.new RSSFeedParser(
                    "http://www.gamespot.com/rss/game_updates.php?platform=5");
            auroraGameHubFeed = auroraGameHubParser.readFeed();
        }


        for (Iterator<ARssReader.FeedMessage> it = auroraGameHubFeed
                .getMessages().
                iterator(); it.hasNext();) {
            ARssReader.FeedMessage message = it.next();
            Array.add(message.getTitle());
        }

        return Array;
    }


    /**
     * .-----------------------------------------------------------------------.
     * | createFeed() --> ArrayList <AInfoFeedLabel>
     * .-----------------------------------------------------------------------.
     * |
     * | This method returns an array list of AInfoFeedLabel's where each label
     * | contains the text of each news item to be used to display on the info
     * | feed
     * |
     * .........................................................................
     *
     * @param array ArrayList
     * <p/>
     * @return an ArrayList with info
     */
    public final ArrayList<AInfoFeedLabel> createFeed() {

        ArrayList<AInfoFeedLabel> Array = new ArrayList<AInfoFeedLabel>();

        try {
            ARssReader.RSSFeedParser auroraGameHubParser = rssReader.new RSSFeedParser(
                    "http://www.rssmix.com/u/3630806/rss.xml");
            auroraGameHubFeed = auroraGameHubParser.readFeed();
        } catch (Exception ex) {
            //* fall back if above feed mixer dies *//
            ARssReader.RSSFeedParser auroraGameHubParser = rssReader.new RSSFeedParser(
                    "http://www.gamespot.com/rss/game_updates.php?platform=5");
            auroraGameHubFeed = auroraGameHubParser.readFeed();
        }


        for (Iterator<ARssReader.FeedMessage> it = auroraGameHubFeed
                .getMessages().
                iterator(); it.hasNext();) {
            ARssReader.FeedMessage message = it.next();
            AInfoFeedLabel label = new AInfoFeedLabel(" " + message.getTitle() + " ", message.getLink());

            // Determine the source of the news article
            String url = message.getLink();
            int i = url.indexOf(".");
            int j = url.indexOf('.', i + 1);
            String sourceName = url.substring(i + 1,  j);
            label.setSourceName(sourceName.toUpperCase());

            Array.add(label);

        }

        return Array;
    }


    /**
     * .-----------------------------------------------------------------------.
     * | launchAuroraApp(ACarouselPane aCarouselPane)
     * .-----------------------------------------------------------------------.
     * |
     * | This method takes in a CarouselPane and tries to determine which APP
     * | Is associated with the specific Carousel Pane and then Launch that APP
     * |
     * | The method does an if check on each known Carousel Pane found in
     * | Dashboard UI such as: LibraryPane, ProfilePane, SettingsPane etc.
     * | then it launches the appropriate UI of the APP associated with that
     * | Carousel Pane.
     * |
     * .........................................................................
     *
     * @param aCarouselPane ACarouselPane
     * <p/>
     */
    public final void launchAuroraApp(final ACarouselPane aCarouselPane) {

        ACarouselPane pane = aCarouselPane;

        if (pane == dashboardUI.getLibraryPane()) {
            //* action on click right Panel *//
            if (dashboardUI != null) {

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        libraryUI.clearUI(true);
                        libraryUI.buildUI();
                    }
                });
            }
        } else if (pane == dashboardUI.getProfilePane()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    profileUI.clearUI(true);
                    profileUI.buildUI();
                }
            });
        } else if (pane == dashboardUI.getSettingsPane()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    settingsUI.clearUI(true);
                    settingsUI.buildUI();
                }
            });
        } else if (pane == dashboardUI.getAuroraNetPane()) {
            //* do nothing for now *//
        }


    }

    /**
     * .-----------------------------------------------------------------------.
     * | navigateCarousel(ACarouselPane aCarouselPane)
     * .-----------------------------------------------------------------------.
     * |
     * | This method takes a CarouselPane and determines based on known points
     * | if it is the Center Pane, the Left Pane or the Right Pane. It then asks
     * | The Carousel in the DashboardUI to move Right, Left or launch the APP
     * | associated with that Pane depending on the location of that Pane.
     * |
     * | IF Pane on the Right Side >> Move Carousel to the Left
     * | IF Pane on the Left Side >> Move Carousel to the Right
     * | IF Pane is in the Center >> Launch App by passing pane to
     * | launchAuroraApp(ACarouselPane)
     * .........................................................................
     *
     * @param aCarouselPane ACarouselPane
     * <p/>
     */
    public final void navigateCarousel(final ACarouselPane aCarouselPane) {

        ACarouselPane pane = aCarouselPane;

        if (pane != null) {
            /* if Pane is to the Right side, move carousel Left */
            if (pane.getPointX() == dashboardUI.getCarousel().getRightX()) {
                dashboardUI.getCarousel().MoveLeft();

                /* if Pane is to the Left side, move carousel Right */
            } else if (pane.getPointX() == dashboardUI.getCarousel().getLeftX()) {
                dashboardUI.getCarousel().MoveRight();

                /* if Pane is in the Center then launch the App associated with it*/
            } else if (pane.getPointX() == dashboardUI.getCarousel().getCentX()) {
                this.launchAuroraApp(pane);
            }
        }
    }

    /**
     * .-----------------------------------------------------------------------.
     * | loadAuroraApps()
     * .-----------------------------------------------------------------------.
     * |
     * | This method will load the Aurora Apps so that they are ready to launch
     * | as soon as needed.
     * |
     * | This method is called on the creation of the DashboardLogic class
     * | so that the aurora Apps are loaded and ready for reuse instead of
     * | being recreated each time.
     * .........................................................................
     *
     * <p/>
     */
    private void loadAuroraApps() {

        AThreadWorker asyncLoad = new AThreadWorker(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                libraryUI = new GameLibraryUI(dashboardUI
                        .getStartUI().getAuroraStorage(), dashboardUI,
                        dashboardUI.getCoreUI());
                libraryUI.loadUI();


                profileUI = new GamerProfileUI(dashboardUI,
                        dashboardUI.getCoreUI());
                profileUI.loadUI();

                settingsUI = new SettingsUI(dashboardUI,
                        dashboardUI.getCoreUI());

                settingsUI.loadUI();



                System.out.println("Apps Pre Loaded");
            }
        });

        asyncLoad.startOnce();

    }
}
