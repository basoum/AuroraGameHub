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

import aurora.V1.core.screen_handler.LibraryHandler;
import aurora.V1.core.screen_ui.LibraryUI;
import aurora.engine.V1.UI.AImage;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.Box;
import org.apache.log4j.Logger;

/**
 * GridSearch Searches through Game Library
 *
 * @author Sammy
 * @version 0.2
 */
public class GridSearch {

    private char typed; // This is the character user types

    private String AppendedName = ""; //This is the concatenation of all characters

    private AuroraCoreUI ui; // The is the UI object

    private LibraryUI libraryUI; // This is the Library View Object

    private AImage sideSearchImage; // The Image To The Side of The Grid that says: Search

    private GridManager SearchManager = null; // The Grid Manager For the Manager

    private ArrayList<Game> foundGameList; //List of all games found

    private boolean clearedGrid; // clearedGrid boolean

    private Game foundGame; //Current found game

    private Game GameOriginal; //Original GameCover for copying purpouses

    private FavListener FaveListener;

    private final LibraryHandler handler;

    static final Logger logger = Logger.getLogger(GridSearch.class);

    private boolean restoredGrid = true;

    public GridSearch(LibraryUI aLibraryUI,
                      LibraryHandler aLibraryHandler) {
        this.ui = aLibraryUI.getCoreUI();
        this.libraryUI = aLibraryUI;
        this.handler = aLibraryHandler;
        foundGameList = new ArrayList<Game>();
        this.clearedGrid = false;

    }

    public void setUp() {
        libraryUI.getSearchBar().addKeyListener(new EnterKeyListener());
    }

    public void typedChar(char typedChar) {
        typed = typedChar; // Set variable to typeChar

        //Append to Appended Name
        AppendedName = AppendedName.concat(String.valueOf(typed));

        if (logger.isDebugEnabled()) {
            logger.debug("TYPED Character: " + String.valueOf(typed));
            logger.debug("Appended name: " + AppendedName);
        }

        //clear library grid if not already clear
        if (!clearedGrid) {
            clearGameGrid();
            System.out.println("Cleared Grid");
        }

        //Initiate Search Sequence
        searchGame();
        restoredGrid = false;
    }

    public void removeChar(char typedChar) {
        typed = typedChar;

        //What Happends When The Length is zero
        if (AppendedName.length() == 0 || libraryUI.getSearchBar().getText()
                .length() == 0) {
            try {
                restoredGrid = false;
                restoreGrid(); //Restores to The original Library
                resetAppendedName(); // Resets AppendName variable
                libraryUI.getGameGridContainer().revalidate(); // Refreshes the Grid.

            } catch (MalformedURLException ex) {
                logger.error(ex);
            }
        }
        //Remove ONE Character From End of Appended Name
        if (AppendedName.length() - 1 >= 0) {
            AppendedName = (String) AppendedName.subSequence(0, AppendedName
                                                             .length() - 1);
            if (AppendedName.length() == 0) {
                removeChar(typedChar);
            }

        }

        if (logger.isDebugEnabled()) {
            logger.debug("Appended name: " + AppendedName);
        }

        searchGame();

    }

    public void resetAppendedName() {
        AppendedName = "";
        foundGameList.removeAll(foundGameList);
    }

    /**
     * Search is split in two Exact Search and Lenient Search Exact search is
     * when the game typed is the same as a game in library Lenient search is
     * when the first few letters are typed and a search for games with those
     * letters occurs
     */
    private void searchGame() {
        //TODO Progress Wheel

        this.clearSearchGrid();

        int results = 0;

        //EXACT SEARCH
        if (checkGameExistsInLibrary(AppendedName)) { // Check if game is exact match to library game

            if (logger.isDebugEnabled()) {
                logger.debug("Performing Exact Search: " + AppendedName);
            }

            //Add the exact game found
            addFoundGame(AppendedName);
            results++;

            //Display the game found
            try {
                displayGames();

            } catch (MalformedURLException ex) {
                logger.error(ex);
            }

            //LENIENT SEARCH
        } else if (AppendedName.length() > 0) {

            if (logger.isDebugEnabled()) {
                logger.debug("Performing Lenient Search: " + AppendedName);
            }

            //Search Each Grid
            for (int g = 0; g < libraryUI.getGridManager().getArray().size(); g++) {
                //Search GameCover in specific Grid

                for (int j = 0; j < libraryUI.getGridManager().getGrid(g)
                        .getArray().size(); j++) {

                    //check if placeholder object
                    if (libraryUI.getGridManager().getGrid(g).getArray().get(j)
                            .getClass() != GamePlaceholder.class) {

                        //Convert each object in specific grid to GameCover Object
                        Game game = (Game) libraryUI.getGridManager().getGrid(g)
                                .getArray().get(j);

                        int checkLength = ((AppendedName.length()));


                        if (checkLength < game.getName().length()) {
                            //String containing Substring of Games
                            String gameSub = game.getName().toLowerCase();
                            //String Containing Substring of Text Typed
                            String appendedSub = AppendedName.substring(0,
                                                                        checkLength)
                                    .toLowerCase();


                            if (logger.isDebugEnabled()) {
                                logger.debug("!Substring of Appended is: "
                                             + appendedSub);
                                logger
                                        .debug("!Substring of Game is: "
                                               + gameSub);
                                logger.debug("!!Match Found?: " + gameSub
                                        .equals(appendedSub));
                            }

                            //* check if name contains characters *//
                            if (gameSub.contains(appendedSub)) {

                                //Check for duplicates
                                if (!foundGameList.contains(game)) {
                                    addFoundGame(game.getName());
                                    results += 1;
                                    try {
                                        displayGames();

                                    } catch (MalformedURLException ex) {
                                        logger.error(ex);
                                    }
                                }
                            }

                        }

                    }
                }

            }

            //If Nothing Found clear grid
            if (foundGameList.isEmpty()) {
                this.clearSearchGrid();
                libraryUI.getGameGridContainer().repaint();
                results = 0;
            }

            //Clear grid
        } else if (AppendedName.length() != 0) {
            this.clearSearchGrid();
            libraryUI.getGameGridContainer().repaint();
            results = 0;
        }

        //add the place holders at the end
        SearchManager.addPlaceHolders(libraryUI.getGameCoverWidth(), libraryUI
                                      .getGameCoverHeight());

        // Padding scalling depending on results
        if (results <= 9) {
            libraryUI.getLblSearchResults().setPreferredSize(new Dimension(30,
                                                                           libraryUI
                                                                           .getCoreUI()
                                                                           .getFrameControlImagePane()
                                                                           .getRealImageHeight()));
        } else if (results >= 10 && results < 100) {
            libraryUI.getLblSearchResults().setPreferredSize(new Dimension(50,
                                                                           libraryUI
                                                                           .getCoreUI()
                                                                           .getFrameControlImagePane()
                                                                           .getRealImageHeight()));
        } else if (results >= 100) {
            libraryUI.getLblSearchResults().setPreferredSize(new Dimension(60,
                                                                           libraryUI
                                                                           .getCoreUI()
                                                                           .getFrameControlImagePane()
                                                                           .getRealImageHeight()));
        }

        if (results == 1) {
            libraryUI.getLblSearchResultsInfo().setText("Result");
        } else {
            libraryUI.getLblSearchResultsInfo().setText("Results");
        }

        libraryUI.getLblSearchResults()
                .setText(Integer.toString(results));

        if (results > 999) {
            libraryUI.getLblSearchResults().setPreferredSize(new Dimension(70,
                                                                           libraryUI
                                                                           .getCoreUI()
                                                                           .getFrameControlImagePane()
                                                                           .getRealImageHeight()));
            libraryUI.getLblSearchResults()
                    .setText("999+");
        }


        SearchManager.getGrid(0).revalidate();
        SearchManager.getGrid(0).repaint();
    }

    private boolean checkGameExistsInSearch(String name) {

        for (int i = 0; i < SearchManager.getArray().size(); i++) {
            for (int j = 0; j < SearchManager.getGrid(i).getArray().size(); j++) {
                if (!SearchManager.getGrid(i).getArray().get(j).getClass()
                        .getName().equals(GamePlaceholder.class.getName())) {
                    Game game = (Game) SearchManager.getGrid(i).getArray()
                            .get(j);
                    if (game.getName().equals(name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //Check if GameCover with exact string exists in library
    private boolean checkGameExistsInLibrary(String name) {

        if (libraryUI.getGridManager().findGameName(name)[0] != -1
            && libraryUI.getGridManager().findGameName(name)[0] != -1) {
            return true;
        }
        return false;
    }

    private void addFoundGame(String name) {

        //MAKE A COPY OF THE PREVIEOUS GAME

        GameOriginal = (Game) (libraryUI.getGridManager().getGrid(
                libraryUI.getGridManager().findGameName(name)[0]).getArray().get(
                        libraryUI.getGridManager().findGameName(name)[1]));

        // Set sizes for SearchManager
        SearchManager.setWidth(GameOriginal.getWidth());
        SearchManager.setHeight(GameOriginal.getHeight());
        SearchManager.setAllowAddGameButton(false);
        //Set Up New Cover
        foundGame = new Game(SearchManager, ui, GameOriginal.getBoxArtUrl());
        foundGame.setLibraryManager(GameOriginal.getManager());
        //manually copying it over
        foundGame.setFavorite(GameOriginal.isFavorite());
        foundGame.setGameName(GameOriginal.getName());
        foundGame
                .setCoverSize(GameOriginal.getWidth(), GameOriginal.getHeight());
        foundGame.setDashboardUI(GameOriginal.getDashboardUI());
        foundGame.setStorage(GameOriginal.getStorage());
        foundGame.setGamePath(GameOriginal.getGamePath());
        foundGame.setDashboardUI(GameOriginal.getDashboardUI());
        foundGame.setLibraryLogic(GameOriginal.getLibraryLogic());
        foundGame.setGameType(GameOriginal.getGameType());
        foundGame.setLastPlayed(GameOriginal.getLastPlayed());
        foundGame.setOcurrencesPlayed(GameOriginal.getOccurencesPlayed());
        foundGame.setTotalTimePlayed(GameOriginal.getTotalTimePlayed());


        try {
            foundGame.update();
        } catch (MalformedURLException ex) {
            java.util.logging.Logger.getLogger(GridSearch.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        foundGame.setSettingsListener(GameOriginal.getSettingsListener());
        if (foundGameList.size() <= 7) {
            foundGameList.add(foundGame);
        }

        if (logger.isDebugEnabled()) {
            logger.debug(foundGameList);
        }

    }

    //Live Display of GameCover Found
    private void displayGames() throws MalformedURLException {

        // Prevent tranision info in status bar
        for (Game game : foundGameList) {
            game.setGameRemoved(true);
        }

        //Go through Array and add to Grid
        for (int i = 0; i < foundGameList.size(); i++) {
            //Check for dupicates
            if (!checkGameExistsInSearch(foundGameList.get(i).getName())) {

                SearchManager.addGame(foundGameList.get(i)); // add to the grid.

                if (!foundGameList.get(i).isLoaded()) {
                    foundGameList.get(i).update();
                }
                foundGameList.get(i)
                        .addFocusListener(handler.new SearchLostFocusHandler());
                foundGameList.get(i).update();

                //Handle Remote Favorting to affect original game too
                foundGame = foundGameList.get(i);
                GameOriginal = (Game) (libraryUI.getGridManager().getGrid(
                        libraryUI.getGridManager().findGameName(foundGame
                                .getGameName())[0]).getArray().get(
                                libraryUI.getGridManager().findGameName(foundGame
                                        .getGameName())[1]));

                FaveListener = new FavListener(foundGame, GameOriginal);

                foundGameList.get(i).getFavoriteButton().addActionListener(
                        FaveListener);

                libraryUI.getGameGridContainer().revalidate();
                libraryUI.getGameGridContainer().repaint();




            }

        }

        SearchManager.addPlaceHolders(foundGame.getImageWidth(), foundGame
                                      .getImageHeight());

        SearchManager.getGrid(0).revalidate();
        SearchManager.getGrid(0).repaint();



    }

    //Clears Search grid and foundGameList
    public void clearSearchGrid() {

        if (logger.isDebugEnabled()) {
            logger.debug("Clearing Search Grid");
        }

        SearchManager.clearAllGrids();
        foundGameList.removeAll(foundGameList);

        SearchManager.getGrid(0).revalidate();
        SearchManager.getGrid(0).repaint();


    }

    /**
     * Remove Library Grid, add new one for search
     */
    public void clearGameGrid() {


        // show search results
        libraryUI.getPnlSearchResultsContainer().setVisible(true);
        libraryUI.getGridManager().unselectPrevious(null);

        //Remove Favorite Side Image
        libraryUI.getGameGridContainer().remove(0);
        libraryUI.getGameGridContainer().remove(libraryUI.getImgOrganizeType());
        libraryUI.getGameGridContainer().remove(libraryUI
                .getPnlMoveRightContainer());
        libraryUI.getGameGridContainer().add(Box.createHorizontalStrut(libraryUI
                .getBtnMoveRight().getImgWidth()), BorderLayout.EAST);
        //Add search Side image
        this.sideSearchImage = new AImage("library_search.png");
        libraryUI.getGameGridContainer().add(sideSearchImage, BorderLayout.WEST);

        for (int i = 0; i < libraryUI.getGridManager().getArray().size(); i++) {
            libraryUI.getGameGridContainer().remove(libraryUI.getGridManager()
                    .getGrid(i));

        }

        setUpGrid();


        libraryUI.getGameGridContainer().revalidate();
        libraryUI.getGameGridContainer().repaint();

        //Tells Every body here that this method has already been executed
        clearedGrid = true;

    }

    public GridManager getGridManager() {
        return SearchManager;
    }

    /*
     * Restore Library Grid
     */
    public void restoreGrid() throws MalformedURLException {

        if (!restoredGrid) {

            // hide search results
            libraryUI.getPnlSearchResultsContainer().setVisible(false);

            // show righ arrow again

            restoredGrid = true;
            clearedGrid = false;

            libraryUI.getGameGridContainer().removeAll();

            libraryUI.getGameGridContainer().add(libraryUI.getImgOrganizeType(),
                                              BorderLayout.WEST);
            libraryUI.getGameGridContainer().add(libraryUI.getGridManager()
                    .getGrid(0),
                                              BorderLayout.CENTER);
            libraryUI.getGameGridContainer().add(libraryUI
                    .getPnlMoveRightContainer(),
                                              BorderLayout.EAST);

            libraryUI.getGameGridContainer().revalidate();
            libraryUI.getGameGridContainer().repaint();
        }
    }

    //New Grid for Search
    private void setUpGrid() {
        this.SearchManager = new GridManager(2, 4, ui);
        SearchManager.initiateGrid(0);
        libraryUI.getGameGridContainer().add(SearchManager.getGrid(0),
                                          BorderLayout.CENTER);

    }

    private class EnterKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER && foundGameList.size() > 0) {
                foundGameList.get(0).getPlayHandler().actionPerformed(null);
            }

        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    class FavListener implements ActionListener {

        private Game game;

        private Game original;

        public FavListener(Game game, Game original) {
            this.game = game;
            this.original = original;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (game.isFavorite()) {
                original.setUnFavorite();
            } else {
                original.setFavorite();
            }
        }
    }
}
