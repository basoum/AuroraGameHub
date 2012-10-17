/*
 * Copyright 2012 Sardonix Creative.
 *
 * This work is licensed under the 
 * Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit 
 *
 *      http://creativecommons.org/licenses/by-nc-nd/3.0/
 *
 * or send a letter to Creative Commons, 444 Castro Street, ScoreUIte 900, 
 * Mountain View, California, 94041, USA.
 * Unless reqcoreUIred by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package aurora.V1.core.screen_ui;

import aurora.V1.core.*;
import aurora.V1.core.screen_handler.GameLibraryHandler;
import aurora.V1.core.screen_handler.GameLibraryHandler.AddGameHandler;
import aurora.V1.core.screen_handler.GameLibraryHandler.GameLibraryKeyListener;
import aurora.V1.core.screen_handler.GameLibraryHandler.HoverButtonLeft;
import aurora.V1.core.screen_handler.GameLibraryHandler.HoverButtonRight;
import aurora.V1.core.screen_handler.GameLibraryHandler.MoveToLastGrid;
import aurora.V1.core.screen_handler.GameLibraryHandler.searchBoxHandler;
import aurora.V1.core.screen_handler.GameLibraryHandler.searchFocusHandler;
import aurora.V1.core.screen_ui.DashboardUI;
import aurora.engine.V1.Logic.*;
import aurora.engine.V1.UI.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author Sammy, Carlos
 * @version 0.5
 */
public class GameLibraryUI extends AuroraApp {

    private aButton ZoomP;
    private aButton ZoomM;
    private ArrayList<aImagePane> gameCover;
    private int zoom;
    private JPanel GameBack;
    private aImage imgFavorite;
    private aHoverButton imgGameRight;
    private GridManager GridSplit;
    private aHoverButton imgGameLeft;
    private int currentIndex;
    private ActionListener a;
    private int currentPanel;
    private ArrayList<Boolean> loadedPanels;
    public static int SIZE_GameCoverHeight;
    public static int SIZE_GameCoverWidth;
    public static int SIZE_ZoomButtonHeight;
    public static int SIZE_SelectedGameBarHeight;
    public static int SIZE_SelectedGameBarWidth;
    public static int SIZE_AddGameWidth;
    public static int SIZE_AddGameHeight;
    public static int SIZE_GameNameFont;
    private GridAnimation GridAnimate;
    private aImagePane imgSelectedGamePane;
    public static JLabel lblGameName;
    private aImage imgKeyIco;
    private JLabel lblKeyAction;
    private aButton btnAddGame;
    private JPanel SelectedGameContainer;
    private aImagePane SearchBarBG;
    private JTextField gridSearchBar;
    private JPanel SearchPane;
    private JPanel TextPane;
    private aImagePane SearchButtonBG;
    private aButton SearchButton;
    private JPanel SearchContainer;
    private JPanel ButtonPane;
    private int SIZE_FramePanePadding;
    private GameLibraryHandler handler;
    private GridSearch Search;
    private aImage imgBlank;
    private aSimpleDB CoverDB;
    private AddGameHandler addGameHandler;
    private aImagePane addGamePane;
    private aAnimate addGameAnimator;
    private aButton btnCloseAddUI;
    private JPanel topPane;
    private aImagePane searchBG;
    private aImagePane searchArrow;
    private aImagePane searchBox;
    private aImagePane bottomPane;
    private JPanel centerPane;
    private JLabel lblAddTitle;
    private JTextField gameSearchBar;
    private JPanel addSearchContainer;
    private JPanel bottomTop;
    private JPanel TopCenterPane;
    private JPanel LeftTopCenter;
    private JPanel RightTopCenter;
    private aImage stepOne;
    private aImage stepTwo;
    private JLabel lblLeftTitle;
    private JLabel lblRightTitle;
    private aImagePane coverPane;
    private aImagePane coverGame;
    private JPanel BottomCenterPane;
    private JPanel LeftBottomCenter;
    private JPanel RightBottomCenter;
    private JPanel ContentPane;
    private GameSearch GameSearch;
    private searchBoxHandler searchBoxHandler;
    private searchFocusHandler searchFocusHandler;
    private boolean addGameUI_Visible = false;
    private JList gamesList;
    private JScrollPane gameScrollPane;
    private JFileChooser gameLocator;
    private DefaultListModel listModel;
    private JPanel RightBottomContainer;
    private String currentPath;
    private aButton addGameToLibButton;
    private aAnimate addGameToLibButtonAnimator;
    private AuroraStorage storage;
    private HoverButtonLeft MoveLibraryLeftHandler;
    private HoverButtonRight MoveLibraryRightHandler;
    private MoveToLastGrid GridMove;
    private boolean isAddGameUILoaded = false;
    private boolean isGameLibraryKeyListenerAdded;
    private int SIZE_SearchBarWidth;
    private aButton removeSearchButton;
    private final DashboardUI dashboardUI;
    private final AuroraCoreUI coreUI;

    public GameLibraryUI(AuroraStorage storage
            ,final DashboardUI dashboardUi
            ,final AuroraCoreUI auroraCoreUI) {
        this.coreUI = auroraCoreUI;
        this.storage = storage;
        this.dashboardUI = dashboardUi;
        this.handler = new GameLibraryHandler();
        this.clearUI_Forwards();
        isGameLibraryKeyListenerAdded = false;

    }

    public void setSize() {

        int Ratio = (coreUI.getFrame().getWidth() - coreUI.getFrame().getHeight()) / 2;
        System.out.println("Ratio " + Ratio);
        System.out.println("Height " + coreUI.getFrame().getHeight());
        System.out.println("Width " + coreUI.getFrame().getWidth());
        if (coreUI.isLargeScreen()) {
            SIZE_GameCoverHeight = coreUI.getFrame().getHeight() / 3 - (Ratio / 10) + 5;
            SIZE_GameCoverWidth = coreUI.getFrame().getWidth() / 5 - (Ratio / 10) - 5;
            SIZE_ZoomButtonHeight = 30;
            SIZE_SelectedGameBarHeight = 65;
            SIZE_SelectedGameBarWidth = 380;
            SIZE_AddGameWidth = 351;
            SIZE_AddGameHeight = 51;
            SIZE_GameNameFont = 35;
            SIZE_FramePanePadding = 20;
            SIZE_SearchBarWidth = 880;

        } else {
            SIZE_FramePanePadding = 10;
            SIZE_GameCoverHeight = coreUI.getFrame().getHeight() / 3 - (Ratio / 10);
            SIZE_GameCoverWidth = coreUI.getFrame().getWidth() / 5 - (Ratio / 10);
            SIZE_ZoomButtonHeight = 25;
            SIZE_AddGameWidth = 300;
            SIZE_AddGameHeight = 40;
            SIZE_SelectedGameBarHeight = 60;
            SIZE_SelectedGameBarWidth = 360;
            SIZE_GameNameFont = 32;
            SIZE_SearchBarWidth = coreUI.getFrame().getWidth() / 2 + coreUI.getSIZE_controlWidth() / 2;
        }


    }

    @Override
    public final void loadUI() {

        ///...SET UP
        setSize();


        ///... setup
        coreUI.getLblInfo().setText("   Loading...   ");
        coreUI.getPnlCenter().repaint();

        ///...Create Components

        ///Panel Containing Arrow Buttons and GameCover Panel
        GameBack = new JPanel(true);
        GameBack.setOpaque(false);
        GameBack.setLayout(new BorderLayout(5, 5)); //Inital Size = 10 Games



        imgFavorite = new aImage("Aurora_Favorite.png");
        imgBlank = new aImage("Aurora_Blank.png");


        MoveLibraryLeftHandler = handler.new HoverButtonLeft(this, coreUI);
        MoveLibraryRightHandler = handler.new HoverButtonRight(this, coreUI);

        imgGameRight = new aHoverButton(MoveLibraryRightHandler, 3, "Aurora_RightLib_normal.png", "Aurora_RightLib_over.png");
        imgGameLeft = new aHoverButton(MoveLibraryLeftHandler, 3, "Aurora_LeftLib_normal.png", "Aurora_LeftLib_over.png");
        GridMove = handler.new MoveToLastGrid(this);

        //...Zoom Buttons
        ZoomP = new aButton("Aurora_ZoomP_normal.png", "Aurora_ZoomP_down.png", "Aurora_ZoomP_over.png", 0, SIZE_ZoomButtonHeight);
        ZoomM = new aButton("Aurora_ZoomM_normal.png", "Aurora_ZoomM_down.png", "Aurora_ZoomM_over.png", 0, SIZE_ZoomButtonHeight);

        //Add Zoom Buttons
        coreUI.getPnlInfo().removeAll();

        coreUI.getPnlInfo().add(ZoomM);
        coreUI.getPnlInfo().add(coreUI.getLblInfo());
        coreUI.getPnlInfo().add(ZoomP);


        //Key Actions Panel

        imgKeyIco = new aImage("KeyboardKeys/arrows.png", coreUI.getSIZE_KeyIconWidth(), coreUI.getSIZE_KeyIconHeight());
        lblKeyAction = new JLabel(" Move ");
        lblKeyAction.setFont(coreUI.getDefaultFont().deriveFont(Font.PLAIN, coreUI.getSIZE_keysFont()));
        lblKeyAction.setForeground(Color.YELLOW);
        coreUI.getPnlKeyToPress().add(coreUI.getImgKeyIco());
        coreUI.getPnlKeyToPress().add(coreUI.getLblKeyAction());
        coreUI.getPnlKeyToPress().add(imgKeyIco);
        coreUI.getPnlKeyToPress().add(lblKeyAction);
        coreUI.getPnlHeaderOfCenterFromBottom().setPreferredSize(new Dimension(coreUI.getFrame().getWidth(), 5));
        coreUI.getPnlHeaderOfCenterFromBottom().revalidate();

        ///Selected GameCover Bar

        SelectedGameContainer = new JPanel();
        SelectedGameContainer.setOpaque(false);

        imgSelectedGamePane = new aImagePane("Aurora_SelectedGameBar.png", SIZE_SelectedGameBarWidth, SIZE_SelectedGameBarHeight,
                new FlowLayout(FlowLayout.CENTER, 0, 5));

        imgSelectedGamePane.setPreferredSize(new Dimension(SIZE_SelectedGameBarWidth, SIZE_SelectedGameBarHeight));
        //imgSelectedGamePane.setOpaque(true);


        lblGameName = new JLabel("Select A Game For Info");
        lblGameName.setOpaque(false);
        lblGameName.setFont(coreUI.getDefaultFont().deriveFont(Font.PLAIN, SIZE_GameNameFont));
        lblGameName.setForeground(Color.LIGHT_GRAY);

        //Add game Name Lable
        imgSelectedGamePane.add(lblGameName);
        SelectedGameContainer.add(imgSelectedGamePane);


        ////Add Game Button
        //TODO needs a container
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        addGameHandler = handler.new AddGameHandler(this);

        btnAddGame = new aButton("Aurora_Add_normal.png", "Aurora_Add_down.png", "Aurora_Add_over.png", SIZE_AddGameWidth, SIZE_AddGameHeight);
        btnAddGame.addActionListener(addGameHandler);


        container.add(btnAddGame, BorderLayout.CENTER);


        coreUI.getPnlUserSpace().setLayout(new BorderLayout());
        coreUI.getPnlUserSpace().setVisible(true);
        coreUI.getPnlUserSpace().add(container);


        //Center Bottom

        coreUI.getPnlCenterFromBottom().setLayout(new BorderLayout());
        coreUI.getPnlCenterFromBottom().add(BorderLayout.CENTER, SelectedGameContainer);
        coreUI.getPnlCenterFromBottom().add(BorderLayout.SOUTH, coreUI.getPnlUserSpace());

        //Search Bar


        SearchBarBG = new aImagePane("SearchBar_inactive.png", new BorderLayout());
        SearchBarBG.setPreferredSize(new Dimension(SIZE_SearchBarWidth, 50));

        removeSearchButton = new aButton("SearchButtonBack_up.png", "SearchButtonBack_down.png", "SearchButtonBack_over.png");
        removeSearchButton.setPreferredSize(new Dimension(70, 51));

        gridSearchBar = new JTextField("Start Typing To Search...");
        gridSearchBar.setOpaque(false);
        gridSearchBar.setBorder(null);
        gridSearchBar.setColumns(19);
        gridSearchBar.setForeground(Color.darkGray);
        gridSearchBar.setFont(coreUI.getDefaultFont().deriveFont(Font.BOLD, 40));
        gridSearchBar.setPreferredSize(new Dimension(880, 50));



        SearchButton = new aButton("SearchButton_up.png", "SearchButton_down.png", "SearchButton_over.png");
        SearchButton.setPreferredSize(new Dimension(70, 51));
        SearchButton.addActionListener(handler.new searchButtonHandler(this));

        SearchButtonBG = new aImagePane("SearchButtonBG.png", new BorderLayout());
        SearchButtonBG.setPreferredSize(new Dimension(70, 51));
        SearchButtonBG.add(SearchButton, BorderLayout.NORTH);


        TextPane = new JPanel(new BorderLayout());
        TextPane.setOpaque(false);
        TextPane.add(gridSearchBar, BorderLayout.NORTH);

        ///     +

        ButtonPane = new JPanel(new BorderLayout());
        ButtonPane.setOpaque(false);
        ButtonPane.add(SearchButtonBG, BorderLayout.NORTH);


        ///   =

        SearchContainer = new JPanel(new BorderLayout());
        SearchContainer.setOpaque(false);
        SearchContainer.add(ButtonPane, BorderLayout.WEST);
        SearchContainer.add(TextPane, BorderLayout.CENTER);


        SearchBarBG.add(SearchContainer, BorderLayout.WEST);
        SearchBarBG.validate();

        SearchPane = new JPanel(new BorderLayout());
        SearchPane.setOpaque(false);
        SearchPane.add(SearchBarBG, BorderLayout.EAST);
        SearchPane.setPreferredSize(new Dimension(SearchPane.getBounds().width, 75));
        SearchPane.validate();


        coreUI.getPnlSouthFromTop().add(BorderLayout.CENTER, SearchPane);
        coreUI.getPnlSouthFromTop().setPreferredSize(
                new Dimension(coreUI.getPnlSouthFromTop().getWidth(), coreUI.getPnlFrameControl().getHeight()));

        ///Search Bar Auto Focus//
        ////Search Backend////

        Search = new GridSearch(coreUI, this, GridSplit);

        //Handle Search Queries 
        searchBoxHandler = handler.new searchBoxHandler(this);
        searchFocusHandler = handler.new searchFocusHandler(this);
        gridSearchBar.addKeyListener(searchBoxHandler);
        gridSearchBar.addFocusListener(searchFocusHandler);
        gridSearchBar.addMouseListener(handler.new searchSelectHandler(this));

        //TODO PUT IN SEPERATE METHOD

        coreUI.getFrame().addKeyListener(handler.new searchRefocusListener(this));
        coreUI.getFrame().addKeyListener(handler.new GameLibraryKeyListener(this, coreUI));
        coreUI.getFrame().addMouseWheelListener(handler.new GridMouseWheelListener(this));

        coreUI.getPnlBackground().addKeyListener(handler.new searchRefocusListener(this));
        coreUI.getPnlBackground().addKeyListener(handler.new GameLibraryKeyListener(this, coreUI));
        coreUI.getPnlBackground().addMouseWheelListener(handler.new GridMouseWheelListener(this));

        coreUI.getPnlBottom().addKeyListener(handler.new searchRefocusListener(this));
        coreUI.getPnlBottom().addKeyListener(handler.new GameLibraryKeyListener(this, coreUI));
        coreUI.getPnlBottom().addMouseWheelListener(handler.new GridMouseWheelListener(this));

        coreUI.getPnlCenter().addKeyListener(handler.new searchRefocusListener(this));
        coreUI.getPnlCenter().addKeyListener(handler.new GameLibraryKeyListener(this, coreUI));
        coreUI.getPnlCenter().addMouseWheelListener(handler.new GridMouseWheelListener(this));

        coreUI.getPnlSouthFromTop().addKeyListener(handler.new searchRefocusListener(this));
        coreUI.getPnlSouthFromTop().addKeyListener(handler.new GameLibraryKeyListener(this, coreUI));
        coreUI.getPnlSouthFromTop().addMouseWheelListener(handler.new GridMouseWheelListener(this));

        coreUI.getPnlFrameControl().addKeyListener(handler.new searchRefocusListener(this));
        coreUI.getPnlFrameControl().addKeyListener(handler.new GameLibraryKeyListener(this, coreUI));
        coreUI.getPnlFrameControl().addMouseWheelListener(handler.new GridMouseWheelListener(this));

        coreUI.getPnlTop().addKeyListener(handler.new searchRefocusListener(this));
        coreUI.getPnlTop().addKeyListener(handler.new GameLibraryKeyListener(this, coreUI));
        coreUI.getPnlTop().addMouseWheelListener(handler.new GridMouseWheelListener(this));

        this.btnAddGame.addKeyListener(handler.new searchRefocusListener(this));
        this.btnAddGame.addKeyListener(handler.new GameLibraryKeyListener(this, coreUI));

        this.GameBack.addKeyListener(handler.new searchRefocusListener(this));
        this.GameBack.addKeyListener(handler.new GameLibraryKeyListener(this, coreUI));

        this.imgSelectedGamePane.addKeyListener(handler.new searchRefocusListener(this));
        this.imgSelectedGamePane.addKeyListener(handler.new GameLibraryKeyListener(this, coreUI));

        this.imgGameLeft.addKeyListener(handler.new searchRefocusListener(this));
        this.imgGameLeft.addKeyListener(handler.new GameLibraryKeyListener(this, coreUI));

        this.imgGameRight.addKeyListener(handler.new searchRefocusListener(this));
        this.imgGameRight.addKeyListener(handler.new GameLibraryKeyListener(this, coreUI));


        //Add to Volatile Listeners Array List

        addToVolatileListenerBank(gridSearchBar);
        addToVolatileListenerBank(coreUI.getPnlBackground());
        addToVolatileListenerBank(coreUI.getPnlBottom());
        addToVolatileListenerBank(coreUI.getPnlCenter());
        addToVolatileListenerBank(coreUI.getPnlSouthFromTop());
        addToVolatileListenerBank(coreUI.getPnlFrameControl());
        addToVolatileListenerBank(coreUI.getPnlTop());
        addToVolatileListenerBank(this.btnAddGame);
        addToVolatileListenerBank(this.GameBack);
        addToVolatileListenerBank(this.imgSelectedGamePane);
        addToVolatileListenerBank(this.imgGameLeft);
        addToVolatileListenerBank(this.imgGameRight);


        coreUI.getFrame().requestFocus();


        /////---ADD TO UI END


        //Start Aurora Dabatase connection
        try {
            CoverDB = new aSimpleDB("AuroraDB", "AuroraTable", false);
        } catch (SQLException ex) {
            Logger.getLogger(GameLibraryUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        GridSplit = new GridManager(2, 4, coreUI);

        GridSplit.initiateGrid(0);

        //Grid Animator
        this.GridAnimate = new GridAnimation(GridSplit, GameBack);

        int width = SIZE_GameCoverWidth;
        int height = SIZE_GameCoverHeight;




        ////////////////////////
        //ADD GAMES TO LIBRARY//
        ////////////////////////


        try {

            //ADD FAVOURITE GAMES FIRST

            for (int i = 0; i < storage.getStoredLibrary().getGameNames().size(); i++) {



                Game Game = new Game(GridSplit, coreUI, dashboardUI, storage);
                if (storage.getStoredLibrary().getFaveStates().get(i)) {
                    Game.setGameName(storage.getStoredLibrary().getGameNames().get(i));
                    Game.setCoverUrl(storage.getStoredLibrary().getBoxArtPath().get(i));
                    //Handle appostrophese in game path
                    Game.setGamePath(storage.getStoredLibrary().getGamePath().get(i).replace("'", "''"));
                    Game.setFavorite(storage.getStoredLibrary().getFaveStates().get(i));
                    Game.setCoverSize(SIZE_GameCoverWidth, SIZE_GameCoverHeight);

                    GridSplit.addGame(Game);
                }
            }

            //ADD NON-FAVOURITE GAMES LAST

            for (int i = 0; i < storage.getStoredLibrary().getGameNames().size(); i++) {

                Game Game = new Game(GridSplit, coreUI, dashboardUI, storage);
                if (!storage.getStoredLibrary().getFaveStates().get(i)) {
                    Game.setGameName(storage.getStoredLibrary().getGameNames().get(i));
                    Game.setCoverUrl(storage.getStoredLibrary().getBoxArtPath().get(i));
                    //Handle appostrophese in game path
                    Game.setGamePath(storage.getStoredLibrary().getGamePath().get(i).replace("'", "''"));
                    Game.setFavorite(storage.getStoredLibrary().getFaveStates().get(i));
                    Game.setCoverSize(SIZE_GameCoverWidth, SIZE_GameCoverHeight);

                    GridSplit.addGame(Game);
                }
            }

            GridSplit.finalizeGrid(addGameHandler, width, height);

            //Load First Grid by default
            load(0);
        } catch (MalformedURLException ex) {
            System.out.println("MalformedURLExeption \n" + ex);
        }




        ///...Add To UI

        GameBack.add(BorderLayout.WEST, imgFavorite);
        GameBack.add(BorderLayout.CENTER, GridSplit.getGrid(0));
        GameBack.add(BorderLayout.EAST, imgGameRight);



        //add Grid
        coreUI.getPnlCenter().add(BorderLayout.CENTER, GameBack);
        coreUI.getPnlCenter().repaint();

        //Finalize
        coreUI.getLblInfo().setText("   Game Library   ");

    }

   

    /**
     * SmartLoad GameCover Covers to minimize memory usage through burst loading
     *
     */
    public void load(int currentIndex) throws MalformedURLException {

        System.out.println("LAUNCHING LOAD METHOD");
        currentPanel = (currentIndex);
        if (currentPanel < 0) {
            currentPanel = 0;
        }
        System.out.println("current panel: " + currentPanel);



        //Load First Panels

        isGameLibraryKeyListenerAdded = false;
        for (int i = 0; i < GridSplit.getGrid(currentPanel).getArray().size(); i++) {
            Game game = new Game(GridSplit, coreUI, dashboardUI);
            try {
                game = (Game) GridSplit.getGrid(currentPanel).getArray().get(i);
                game.addKeyListener(handler.new searchRefocusListener(this));

                for (int j = 0; j < game.getKeyListeners().length; j++) {
                    //  if (game.getKeyListeners()[j] instanceof GameLibraryUI.GameLibraryKeyListener) {
                    if (game.getKeyListeners()[j] instanceof GameLibraryKeyListener) {
                        isGameLibraryKeyListenerAdded = true;
                        break;
                    }
                }

                if (!isGameLibraryKeyListenerAdded) {
                    System.out.println("ADDING GAMELIBRARYLISTENER TO " + game.getName());
                    //  game.addKeyListener(new GameLibraryUI.GameLibraryKeyListener());
                    game.addKeyListener(handler.new GameLibraryKeyListener(this, coreUI));
                }


                if (!game.isLoaded()) {
                    game.update();

                    System.out.println("loading: " + game.getGameName());
                }
            } catch (RuntimeException ex) {
            }
        }


        isGameLibraryKeyListenerAdded = false;
        //Load Second Panel if exists -- SMART LOAD
        if (currentPanel < GridSplit.getArray().size() - 1) {
            for (int i = 0; i < GridSplit.getGrid(currentPanel + 1).getArray().size(); i++) {
                Game game = new Game(GridSplit, coreUI, dashboardUI);
                try {
                    game = (Game) GridSplit.getGrid(currentPanel + 1).getArray().get(i);

                    for (int j = 0; j < game.getKeyListeners().length; j++) {
                        //   if (game.getKeyListeners()[j] instanceof GameLibraryUI.GameLibraryKeyListener) {
                        if (game.getKeyListeners()[j] instanceof GameLibraryKeyListener) {
                            isGameLibraryKeyListenerAdded = true;
                            break;
                        }
                    }

                    if (!isGameLibraryKeyListenerAdded) {
                        System.out.println("ADDING GAMELIBRARYLISTENER TO" + game.getName());
                        // game.addKeyListener(new GameLibraryUI.GameLibraryKeyListener());
                        game.addKeyListener(handler.new GameLibraryKeyListener(this, coreUI));
                    }
                    if (!game.isLoaded()) {
                        game.update();
                        System.out.println("Secondary loading: " + game.getName());
                    }
                } catch (RuntimeException ex) {
                }


            }
        }
    }
    
     @Override
    public void buildUI() {

     /*
      *
      * TODO, ADD BUILD PART OF UI CODE HERE
      *
      */
     }

    public final void setAddGameUI() {

        if (!isAddGameUILoaded) {

            /////////////////////
            //Create Components//
            /////////////////////


            ////ADD GAME UI////
            JPanel glass = (JPanel) coreUI.getFrame().getGlassPane();
            addGamePane = new aImagePane("Aurora_AddGame_BG.png", new BorderLayout());

            ////TOP PANEL COMPONENTS ////
            //Major Panels
            topPane = new JPanel(new BorderLayout());
            topPane.setOpaque(false);
            //UI components
            btnCloseAddUI = new aButton("Aurora_Close_normal.png", "Aurora_Close_down.png", "Aurora_Close_over.png");


            ////CENTRAL PANEL COMPONENTS ////
            //Major Panels
            centerPane = new JPanel(new BorderLayout());
            centerPane.setOpaque(false);
            TopCenterPane = new JPanel(new BorderLayout());
            TopCenterPane.setOpaque(false);
            LeftTopCenter = new JPanel(new FlowLayout(FlowLayout.LEFT));
            LeftTopCenter.setOpaque(false);
            RightTopCenter = new JPanel(new FlowLayout(FlowLayout.LEFT));
            RightTopCenter.setOpaque(false);
            LeftBottomCenter = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            LeftBottomCenter.setOpaque(false);
            RightBottomCenter = new JPanel();
            RightBottomCenter.setOpaque(true);
            RightBottomContainer = new JPanel(new BorderLayout());
            RightBottomContainer.setOpaque(false);
            ContentPane = new JPanel(new BorderLayout(0, 0));
            ContentPane.setOpaque(false);
            //UI Components
            lblLeftTitle = new JLabel("Name");
            lblRightTitle = new JLabel("Location");
            stepOne = new aImage("AddGame_step1_normal.png");
            stepTwo = new aImage("AddGame_step2_normal.png");
            coverPane = new aImagePane("AddGame_CoverBG.png", new BorderLayout());
            coverGame = new aImagePane("Blank-Case.png", 220, 250);
            gamesList = new JList();
            gameLocator = new JFileChooser(System.getProperty("user.home"));



            ////BOTTOM PANEL COMPONENTS ////
            //Major Panels
            bottomPane = new aImagePane("AddGame_BottomBar.png", new BorderLayout());
            bottomTop = new JPanel(new FlowLayout(FlowLayout.CENTER));
            bottomTop.setOpaque(false);
            BottomCenterPane = new JPanel(new BorderLayout());
            BottomCenterPane.setOpaque(false);
            //UI Components
            searchBG = new aImagePane("AddGame_SearchBG.png", new BorderLayout());
            searchArrow = new aImagePane("AddGame_SearchArrow_light.png", new BorderLayout());
            searchBox = new aImagePane("AddGame_SearchBox.png", new BorderLayout());
            gameSearchBar = new JTextField("Search For Game To Add...");
            lblAddTitle = new JLabel("GAME NAME");
            addSearchContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
            addSearchContainer.setOpaque(false);
            //Other components
            GameSearch = new GameSearch(this, CoverDB, storage);
            addGameToLibButton = new aButton("AddGame_AddToLib_normal.png", "AddGame_AddToLib_down.png", "AddGame_AddToLib_over.png");
            addGameToLibButton.setVisible(false);
            addGameToLibButtonAnimator = new aAnimate(addGameToLibButton);


            /////////////////////
            //Set up Components//
            /////////////////////


            ////CENTRAL PANEL COMPONENTS ////
            ///Set Up Title labels for both Left and Right side of the Central Panel
            lblLeftTitle.setFont(coreUI.getDefaultFont().deriveFont(Font.BOLD, 32));
            lblRightTitle.setFont(coreUI.getDefaultFont().deriveFont(Font.BOLD, 32));
            ///Set Up 2 Panels containing the Left and Right titles at the top of the Content panel
            LeftTopCenter.setPreferredSize(new Dimension(addGamePane.getImgIcon().getIconWidth() / 2, 75));
            RightTopCenter.setPreferredSize(new Dimension(addGamePane.getImgIcon().getIconWidth() / 2, 75));
            //SET Up Content Panels
            LeftBottomCenter.setPreferredSize(new Dimension(490, coverPane.getImgIcon().getIconHeight()));
            RightBottomCenter.setPreferredSize(new Dimension(490, coverPane.getImgIcon().getIconHeight()));
            RightBottomCenter.setBackground(Color.DARK_GRAY);
            RightBottomContainer.setPreferredSize(new Dimension(500, coverPane.getImgIcon().getIconHeight()));
            //Set Up Panels containing the Game Cover Art
            coverPane.setPreferredSize(new Dimension(coverPane.getImgIcon().getIconWidth(), coverPane.getImgIcon().getIconHeight()));
            coverGame.setPreferredSize(new Dimension(220, 260));
            //Set up List
            gamesList.setPreferredSize(new Dimension(coverPane.getImgIcon().getIconWidth() + 80, coverPane.getImgIcon().getIconHeight()));
            gamesList.setBackground(Color.DARK_GRAY);
            gamesList.setForeground(Color.lightGray);
            gamesList.setFont(coreUI.getDefaultFont().deriveFont(Font.BOLD, 19));
            gamesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            gamesList.setLayoutOrientation(JList.VERTICAL);
            gamesList.setVisibleRowCount(10);
            //List model for JList
            listModel = new DefaultListModel();
            gamesList.setModel(listModel);



            //Set up File Chooser UI
            UIManager.put("Viewport.background", Color.GRAY);
            UIManager.put("Viewport.foreground", Color.WHITE);
            UIManager.put("ScrollPane.background", Color.GRAY);
            UIManager.put("ComboBox.background", Color.LIGHT_GRAY);
            UIManager.put("ScrollBar.background", Color.GRAY);
            UIManager.put("Panel.background", Color.GRAY);
            UIManager.put("Panel.forground", Color.WHITE);
            UIManager.put("TextField.foreground", Color.WHITE);
            UIManager.put("TextField.background", Color.DARK_GRAY);
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            } catch (ClassNotFoundException ex) {
//                Logger.getLogger(GameLibraryUI.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (InstantiationException ex) {
//                Logger.getLogger(GameLibraryUI.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (IllegalAccessException ex) {
//                Logger.getLogger(GameLibraryUI.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (UnsupportedLookAndFeelException ex) {
//                Logger.getLogger(GameLibraryUI.class.getName()).log(Level.SEVERE, null, ex);
//            }

            //Set up File Chooser
            SwingUtilities.updateComponentTreeUI(gameLocator);

            gameLocator.setApproveButtonText("Select");
            gameLocator.setDragEnabled(false);
            gameLocator.setDialogType(JFileChooser.OPEN_DIALOG);
            gameLocator.setMultiSelectionEnabled(false);
            gameLocator.setAcceptAllFileFilterUsed(true);
            gameLocator.setEnabled(true);
            gameLocator.revalidate();

            gameLocator.setPreferredSize(new Dimension(coverPane.getImgIcon().getIconWidth() * 2 + 100, coverPane.getImgIcon().getIconHeight() - 5));

            try {
                Field field = PopupFactory.class.getDeclaredField("forceHeavyWeightPopupKey");
                field.setAccessible(true);
                gameLocator.putClientProperty(field.get(null), true);
            } catch (Exception e) {
                e.printStackTrace();
            }



            ////BOTTOM PANEL COMPONENTS ////
            //Major Panel Setup
            bottomPane.setPreferredSize(new Dimension(bottomPane.getImgIcon().getIconWidth(), bottomPane.getImgIcon().getIconHeight()));
            ///Set Up Textfield where user will search for game to add
            gameSearchBar.setFont(coreUI.getDefaultFont().deriveFont(Font.BOLD, 20));
            gameSearchBar.setForeground(Color.DARK_GRAY);
            gameSearchBar.setOpaque(false);
            gameSearchBar.setBorder(null);
            gameSearchBar.setPreferredSize(new Dimension(500, 50));
            //Set up image sizes for the Search box
            bottomTop.setPreferredSize(new Dimension(addGamePane.getImgIcon().getIconWidth(), 20));
            searchBox.setPreferredSize(new Dimension(searchBox.getImgIcon().getIconWidth(), searchBox.getImgIcon().getIconHeight()));
            searchBG.setPreferredSize(new Dimension(searchBG.getImgIcon().getIconWidth(), searchBG.getImgIcon().getIconHeight()));
            searchArrow.setPreferredSize(new Dimension(searchArrow.getImgIcon().getIconWidth(), searchArrow.getImgIcon().getIconHeight()));
            ///Set Up Title Label for Add Game UI
            lblAddTitle.setFont(coreUI.getDefaultFont().deriveFont(Font.BOLD, 15));
            lblAddTitle.setOpaque(false);
            lblAddTitle.setForeground(Color.black);

            ////ADD GAME UI////
            //Set up glass panel to make the Add Game UI visible ontop of all the rest of the UI
            glass.setVisible(true);
            glass.setLayout(null);
            glass.setOpaque(false);
            //Set up the actuall Add Game UI panel
            addGamePane.setLocation((coreUI.getFrame().getWidth() / 2) - (addGamePane.getImgIcon().getIconWidth() / 2), -380);
            addGamePane.setSize(new Dimension(addGamePane.getImgIcon().getIconWidth(), addGamePane.getImgIcon().getIconHeight()));
            addGamePane.revalidate();

            addGameToLibButton.setLocation((coreUI.getFrame().getWidth() / 2) - (335 / 2), addGamePane.getImgIcon().getIconHeight() - 90);
            addGameToLibButton.setSize(new Dimension(340, 140));


            /////////////////
            //Add To Panels//
            /////////////////


            ////TOP PANEL COMPONENTS ////
            //Add the Close button to the Top most Panel
            topPane.add(btnCloseAddUI, BorderLayout.EAST);
            topPane.add(btnCloseAddUI, BorderLayout.EAST);


            ////BOTTOM PANEL COMPONENTS ////
            //Add components to form the Search Box
            searchBox.add(gameSearchBar, BorderLayout.CENTER);
            searchBG.add(searchArrow, BorderLayout.WEST);
            searchBG.add(searchBox, BorderLayout.EAST);
            addSearchContainer.add(searchBG);
            //Add UI elements to the Bottom Panel in the Add Game UI
            bottomTop.add(lblAddTitle);
            bottomPane.add(addSearchContainer, BorderLayout.CENTER);
            bottomPane.add(bottomTop, BorderLayout.PAGE_START);


            ////CENTRAL PANEL COMPONENTS ////
            //Add the Titles and Notificatons at the Top of the CENTRAL panel
            LeftTopCenter.add(stepOne);
            LeftTopCenter.add(lblLeftTitle);
            RightTopCenter.add(stepTwo);
            RightTopCenter.add(lblRightTitle);
            //Content Panels
            coverPane.add(coverGame, BorderLayout.SOUTH);
            LeftBottomCenter.add(coverPane);
            LeftBottomCenter.add(gamesList);

            //Add the main Content to the Central Panel
            ContentPane.add(LeftBottomCenter, BorderLayout.WEST);
            RightBottomCenter.add(gameLocator);
            RightBottomContainer.add(RightBottomCenter, BorderLayout.WEST);
            ContentPane.add(RightBottomContainer, BorderLayout.EAST);
            TopCenterPane.add(LeftTopCenter, BorderLayout.WEST);
            TopCenterPane.add(RightTopCenter, BorderLayout.EAST);
            centerPane.add(TopCenterPane, BorderLayout.NORTH);
            centerPane.add(ContentPane, BorderLayout.CENTER);


            ////ADD GAME UI////
            //Add the TOP the CENTER and the BOTTOM panels to the Add Game UI
            addGamePane.add(topPane, BorderLayout.PAGE_START);
            addGamePane.add(centerPane, BorderLayout.CENTER);
            addGamePane.add(bottomPane, BorderLayout.PAGE_END);
            //Finish by adding the Add Game UI to the Glass.

            glass.add(addGamePane);
            glass.add(addGameToLibButton);



            ////////////
            //Handlers//
            ////////////


            btnCloseAddUI.addActionListener(handler.new HideGameAddUIHandler(this));

            addGamePane.addMouseListener(handler.new EmptyMouseHandler()); //so that you dont select behind the Panel
            gameSearchBar.addFocusListener(handler.new addGameFocusHandler(this));
            gameSearchBar.addMouseListener(handler.new addGameMouseHandler(this));
            gameSearchBar.addKeyListener(handler.new addGameSearchBoxHandler(this));
            gamesList.addListSelectionListener(handler.new SelectListHandler(this));
            gameLocator.setFileFilter(handler.new ExecutableFilterHandler(coreUI));
            gameLocator.addActionListener(handler.new ExecutableChooserHandler(this, gameLocator));
            addGameToLibButton.addActionListener(handler.new AddToLibraryHandler(this));

            ////////////
            //Finalize//
            ////////////


            addGamePane.setVisible(false);
            addGameAnimator = new aAnimate(addGamePane);
            addGameToLibButton.revalidate();
            addGamePane.revalidate();

            glass.revalidate();

            isAddGameUILoaded = true;
        }
    }

    public void checkNotifiers() {

        if (stepOne.getImgURl().equals("AddGame_step1_green.png") && stepTwo.getImgURl().equals("AddGame_step2_green.png")) {
            //Animate the Button bellow Add Game UI
            addGameToLibButton.setVisible(true);
            addGameToLibButtonAnimator.setInitialLocation((coreUI.getFrame().getWidth() / 2) - (335 / 2), addGamePane.getImgIcon().getIconHeight() - 180);
            addGameToLibButtonAnimator.moveVertical(addGamePane.getImgIcon().getIconHeight() - 55, 20);
            addGameToLibButtonAnimator.removeAllListeners();
        }

        if ((stepOne.getImgURl().equals("AddGame_step1_red.png") || stepTwo.getImgURl().equals("AddGame_step2_red.png")) && addGameToLibButton.isVisible()) {


            addGameToLibButtonAnimator.moveVertical(0, 16);
            addGameToLibButtonAnimator.addPostAnimationListener(new aPostHandler() {
                @Override
                public void actionPerformed() {
                    addGameToLibButton.setVisible(false);
                }
            });
        }

    }

    public void showAddGameUI() {
        addGameUI_Visible = true;
        //prep Add Game UI
        setAddGameUI();


        //Animate
        addGameAnimator.setInitialLocation((coreUI.getFrame().getWidth() / 2) - (addGamePane.getImgIcon().getIconWidth() / 2), -390);
        addGameAnimator.moveVertical(0, 32); //even only
        addGamePane.revalidate();

        gameSearchBar.setFocusable(true);

        addGameAnimator.addPostAnimationListener(new aPostHandler() {
            @Override
            public void actionPerformed() {
                gameSearchBar.requestFocus();
            }
        });

    }

    public void hideAddGameUI() {
        addGameUI_Visible = false;
        addGameToLibButton.setVisible(false);
        //Animate
        addGameAnimator.moveVertical(-470, 32);

        //restore
        //GameSearch.resetCover();
        stepTwo.setImgURl("AddGame_step2_red.png");
        addGameAnimator.removeAllListeners();
        addGameToLibButton.setVisible(false);
        addGameToLibButton.repaint();
        gridSearchBar.requestFocus();
        coreUI.getFrame().requestFocus();
        gridSearchBar.requestFocus();
        coreUI.getFrame().requestFocus();

    }

    public void moveGridLeft() {
        System.out.println("Left key pressed");
        if (!GridAnimate.getAnimator1().isAnimating() && !GridAnimate.getAnimator2().isAnimating()) {

            ///Get The Index of The Current Panel Being Displayed///
            ///Refer too GridManager array of All panels to find it///
            currentIndex = GridSplit.getArray().indexOf(GameBack.getComponent(1));

            //Stop from going to far left
            if (currentIndex - 1 == -1) {
                currentIndex = 1;
                imgGameLeft.mouseExit();
            }


            if (currentIndex < GridSplit.getArray().size()) {

                GridSplit.decrementVisibleGridIndex();
                //Clear Panel
                if (currentIndex - 1 <= 0) {
                    //Far Left Image
                    GameBack.remove(0);
                    GameBack.add(imgFavorite, BorderLayout.WEST, 0);

                } else {
                    //Left Button
                    GameBack.remove(0);
                    GameBack.add(imgGameLeft, BorderLayout.WEST, 0);
                }
                //Add GameCover Covers

                GridAnimate.moveLeft(currentIndex);

                GameBack.add(BorderLayout.EAST, imgGameRight);

                try {
                    load(currentIndex - 1);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(GameLibraryUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            coreUI.getPnlCenter().removeAll();
            coreUI.getPnlCenter().add(BorderLayout.CENTER, GameBack);

            GameBack.repaint();
            GameBack.revalidate();

            coreUI.getFrame().requestFocus();

        }
        imgGameLeft.mouseExit();
    }

    public void moveGridRight() {
        System.out.println("Right key pressed");

        if (!GridAnimate.getAnimator1().isAnimating() && !GridAnimate.getAnimator2().isAnimating()) {

            currentIndex = GridSplit.getArray().indexOf(GameBack.getComponent(1));


            if (currentIndex < GridSplit.getArray().size() - 1) {

                GridSplit.incrementVisibleGridIndex();

                GameBack.remove(0);
                GameBack.add(imgGameLeft, BorderLayout.WEST, 0);


                GameBack.add(imgGameRight, BorderLayout.EAST, 2);

                GridAnimate.moveRight(currentIndex);


                try {
                    load(currentIndex + 1);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(GameLibraryUI.class.getName()).log(Level.SEVERE, null, ex);
                }



                //of on last Grid then dont show right arrow button
                if (!(currentIndex + 1 < GridSplit.getArray().size() - 1)) {

                    GameBack.remove(imgGameRight);
                    GameBack.add(imgBlank, BorderLayout.EAST, 2);
                    imgGameRight.mouseExit();
                }
            }

            coreUI.getPnlCenter().removeAll();
            coreUI.getPnlCenter().add(BorderLayout.CENTER, GameBack);

            GameBack.repaint();
            GameBack.revalidate();

            coreUI.getFrame().requestFocus();

        }
        imgGameRight.mouseExit();
    }

    //GETTER AND SETTERS
    public aAnimate getAddGameAnimator() {
        return addGameAnimator;
    }
    
     public JLabel getLblGameName() {
        return lblGameName;
    }

    public HoverButtonLeft getMoveLibraryLeftHandler() {
        return MoveLibraryLeftHandler;
    }

    public HoverButtonRight getMoveLibraryRightHandler() {
        return MoveLibraryRightHandler;
    }

    public aButton getAddGameToLibButton() {
        return addGameToLibButton;
    }

    public aAnimate getAddGameToLibButtonAnimator() {
        return addGameToLibButtonAnimator;
    }

    public JPanel getAddSearchContainer() {
        return addSearchContainer;
    }

    public JPanel getBottomCenterPane() {
        return BottomCenterPane;
    }

    public JFileChooser getGameLocator() {
        return gameLocator;
    }

    public GameLibraryHandler getHandler() {
        return handler;
    }

    public aButton getRemoveSearchButton() {
        return removeSearchButton;
    }

    public JTextField getGameSearchBar() {
        return gameSearchBar;
    }

    public aSimpleDB getCoverDB() {
        return CoverDB;
    }

    public DefaultListModel getListModel() {
        return listModel;
    }

    public JList getGamesList() {
        return gamesList;
    }

    public MoveToLastGrid getGridMove() {
        return GridMove;
    }

    public JScrollPane getListScrollPane() {
        return gameScrollPane;
    }

    public aImage getStepOne() {
        return stepOne;
    }

    public aImage getStepTwo() {
        return stepTwo;
    }

    public aImagePane getAddGamePane() {
        return addGamePane;
    }

    public AddGameHandler getAddGameHandler() {
        return addGameHandler;
    }

    public aImagePane getCoverPane() {
        return coverPane;
    }

    public void setCoverGame(aImagePane coverGame) {
        this.coverGame = coverGame;
    }

    public aImagePane getCoverGame() {
        return coverGame;
    }

    public aImagePane getBottomPane() {
        return bottomPane;
    }

    public JLabel getLblAddUITitle() {
        return lblAddTitle;
    }

    public aImagePane getSearchArrow() {
        return searchArrow;
    }

    public aImagePane getSearchBG() {
        return searchBG;
    }

    public aImagePane getSearchBox() {
        return searchBox;
    }

    public JTextField getSearchText() {
        return gameSearchBar;
    }

    public GameSearch getGameSearch() {
        return GameSearch;
    }

    public JPanel getButtonPane() {
        return ButtonPane;
    }

    public JPanel getGameBack() {
        return GameBack;
    }

    public GridAnimation getGridAnimate() {
        return GridAnimate;
    }

    public GridManager getGridSplit() {
        return GridSplit;
    }

    public int getSIZE_AddGameHeight() {
        return SIZE_AddGameHeight;
    }

    public int getSIZE_AddGameWidth() {
        return SIZE_AddGameWidth;
    }

    public int getSIZE_FramePanePadding() {
        return SIZE_FramePanePadding;
    }

    public int getSIZE_GameCoverHeight() {
        return SIZE_GameCoverHeight;
    }

    public int getSIZE_GameCoverWidth() {
        return SIZE_GameCoverWidth;
    }

    public int getSIZE_GameNameFont() {
        return SIZE_GameNameFont;
    }

    public int getSIZE_SelectedGameBarHeight() {
        return SIZE_SelectedGameBarHeight;
    }

    public int getSIZE_SelectedGameBarWidth() {
        return SIZE_SelectedGameBarWidth;
    }

    public int getSIZE_ZoomButtonHeight() {
        return SIZE_ZoomButtonHeight;
    }

    public GridSearch getSearch() {
        return Search;
    }

    public JTextField getSearchBar() {
        return gridSearchBar;
    }

    public aImagePane getSearchBarBG() {
        return SearchBarBG;
    }

    public aButton getSearchButton() {
        return SearchButton;
    }

    public aImagePane getSearchButtonBG() {
        return SearchButtonBG;
    }

    public JPanel getSearchContainer() {
        return SearchContainer;
    }

    public JPanel getSearchPane() {
        return SearchPane;
    }

    public JPanel getSelectedGameContainer() {
        return SelectedGameContainer;
    }

    public AuroraStorage getStorage() {
        return storage;
    }

    public JPanel getTextPane() {
        return TextPane;
    }

    public aButton getZoomM() {
        return ZoomM;
    }

    public aButton getZoomP() {
        return ZoomP;
    }

    public ActionListener getA() {
        return a;
    }

    public aButton getBtnAddGame() {
        return btnAddGame;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int index) {
        currentIndex = index;
    }

    public int getCurrentPanel() {
        return currentPanel;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String path) {
        currentPath = path;
    }

    public ArrayList<aImagePane> getGameCover() {
        return gameCover;
    }

    public aImage getImgFavorite() {
        return imgFavorite;
    }

    public aImage getImgBlank() {
        return imgBlank;
    }

    public aHoverButton getImgGameLeft() {
        return imgGameLeft;
    }

    public aHoverButton getImgGameRight() {
        return imgGameRight;
    }

    public aImage getImgKeyIco() {
        return imgKeyIco;
    }

    public aImagePane getImgSelectedGamePane() {
        return imgSelectedGamePane;
    }

    public JLabel getLblKeyAction() {
        return lblKeyAction;
    }

    public ArrayList<Boolean> getLoadedPanels() {
        return loadedPanels;
    }

    public DashboardUI getObj() {
        return dashboardUI;
    }

    public boolean isAddGameUI_Visible() {
        return addGameUI_Visible;
    }

    public int getZoom() {
        return zoom;
    }

    public AuroraCoreUI getCoreUI() {
        return coreUI;
    }

    public DashboardUI getDashUI() {
        return dashboardUI;
    }


}