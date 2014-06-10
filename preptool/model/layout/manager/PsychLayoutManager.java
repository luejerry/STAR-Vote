/**
  * This file is part of VoteBox.
  * 
  * VoteBox is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License version 3 as published by
  * the Free Software Foundation.
  * 
  * You should have received a copy of the GNU General Public License
  * along with VoteBox, found in the root of any distribution or
  * repository containing all or part of VoteBox.
  * 
  * THIS SOFTWARE IS PROVIDED BY WILLIAM MARSH RICE UNIVERSITY, HOUSTON,
  * TX AND IS PROVIDED 'AS IS' AND WITHOUT ANY EXPRESS, IMPLIED OR
  * STATUTORY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, WARRANTIES OF
  * ACCURACY, COMPLETENESS, AND NONINFRINGEMENT.  THE SOFTWARE USER SHALL
  * INDEMNIFY, DEFEND AND HOLD HARMLESS RICE UNIVERSITY AND ITS FACULTY,
  * STAFF AND STUDENTS FROM ANY AND ALL CLAIMS, ACTIONS, DAMAGES, LOSSES,
  * LIABILITIES, COSTS AND EXPENSES, INCLUDING ATTORNEYS' FEES AND COURT
  * COSTS, DIRECTLY OR INDIRECTLY ARISING OUR OF OR IN CONNECTION WITH
  * ACCESS OR USE OF THE SOFTWARE.
 */

package preptool.model.layout.manager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import preptool.model.ballot.*;
import preptool.model.language.Language;
import preptool.model.language.LiteralStrings;
import preptool.model.layout.*;


/**
 * PsychLayoutManager is a concrete implementation of a LayoutManager, as
 * specified by the Psychology department.<br>
 * See the wiki for more details about this layout
 *
 * @author Corey Shaw, Ted Torous, Kyle Derr
 */
public class PsychLayoutManager extends ALayoutManager {

    /** Constant used to indicate how wide the text boxes describing a race are to be drawn */
    private static final int RACE_DESCRIPTION_WIDTH = 600;

    /** Constant to indicate how high presedential labels should be */
    private static final int PRESIDENTIAL_RACE_LABEL_COMPONENT_HEIGHT = 40;

	/** Width of each candidate or contest on the review screen (RenderButton) */
	private static final int REVIEW_SCREEN_WIDTH = 330;

    /** Allows the review screen to show party information */
	private static final Boolean REVIEW_SCREEN_SHOW_PARTY = true;

    /** Allows the review screen to put parentheses around the party info */
	private static final Boolean REVIEW_SCREEN_PARENTHESIZE_PARTY = true;

    /** Dictates the number of columns the review screen will have */
	private static final int REVIEW_SCREEN_NUM_COLUMNS = 1;

    /** Dictates the number of races that can be shown on a review screen */
	private static int CARDS_PER_REVIEW_PAGE = 10;

    /** Constant for the width of the language selection page box */
    private static final int LANG_SELECT_WIDTH = 600;

    /**
     * Extension of the ICardLayout for use by this manager
     */
    public class PsychCardLayout implements ICardLayout {

        /** The title of the card this layout represents */
        private String titleText = "";

        /** The description of the card being laid out */
        private String descriptionText = "";

        /** A list of the candidates on this card */
        private ArrayList<ToggleButton> candidates;

        /**
         * Constructor, simply initializes the list of candidates
         */
        public PsychCardLayout() {
            candidates = new ArrayList<>();
        }

        /**
         * @see preptool.model.layout.manager.ALayoutManager.ICardLayout#addCandidate(String, String)
         */
        public void addCandidate(String uid, String name) {
            ToggleButton tb = new ToggleButton(uid, name);
            candidates.add(tb);
        }

        /**
         * @see preptool.model.layout.manager.ALayoutManager.ICardLayout#addCandidate(String, String, String)
         */
        public void addCandidate(String uid, String name, String party) {
            ToggleButton tb = new ToggleButton(uid, name);
            tb.setParty(party);

            candidates.add(tb);
        }

        /**
         * @see preptool.model.layout.manager.ALayoutManager.ICardLayout#addCandidate(String, String, String, String)
         */
        public void addCandidate(String uid, String name, String name2, String party) {
            ToggleButton tb = new ToggleButton(uid, name);
            tb.setSecondLine(name2);
            tb.setParty(party);

            candidates.add(tb);
        }

        /**
         * @see preptool.model.layout.manager.ALayoutManager.ICardLayout#makeIntoPanels()
         */
        public ArrayList<JPanel> makeIntoPanels() {

            /* Keep track of how many candidates we've added */
            int cnt = 0;

            /* A list of the panels we make, to be returned */
            ArrayList<JPanel> panels = new ArrayList<>();

            /* Create new JPanels for each candidate on this card */
            while (cnt < candidates.size()) {

                /* Create a new panel, and set its layout to GridBag*/
                JPanel panel = new JPanel();
                panel.setLayout(new GridBagLayout());

                /* Layout constraints for the elements on this panel*/
                GridBagConstraints panelConstraints = new GridBagConstraints();

                /* The panel will be anchored to the bottom of the screen and filled vertically*/
                panelConstraints.anchor = GridBagConstraints.SOUTH;
                panelConstraints.fill = GridBagConstraints.VERTICAL;

                /* The coordinate dictating where a candidate will be put in the gridbag (in this case, starting at the top) */
                int ycoord = 0;

                /* Initialize the grid part of gridbag */
                panelConstraints.gridy = ycoord;
                panelConstraints.gridx = 0;

                /* Position the first element 1 unit off the bottom of the panel */
                ycoord++;

                /* Build the title label*/
                Label title = new Label(getNextLayoutUID(), titleText);

                /* Add a description, set the width, box, and center the label */
                title.setDescription(descriptionText);
                title.setWidth(RACE_DESCRIPTION_WIDTH); 
                title.setBoxed(true);
                title.setCentered(true);

                /* If there are more candidates than one page can accommodate, add a label indicating this in the title */
                if (candidates.size() > MAX_CANDIDATES)
                    title.setInstructions("("
                            + LiteralStrings.Singleton.get("PAGE", language)
                            + " "
                            + (cnt / MAX_CANDIDATES + 1)
                            + " "
                            + LiteralStrings.Singleton.get("OF", language)
                            + " "
                            + (int) Math.ceil((double) candidates.size()
                                    / MAX_CANDIDATES) + ")");

                /* Set the size of the title box with its visitor */
                title.setSize(title.execute(sizeVisitor));

                /* Put the title in a spacer, then add the spacer to the panel */
                Spacer PTitle = new Spacer(title, panel);
                panel.add(PTitle, panelConstraints);

                /* Now build a toggle button group for the candidates */
                ToggleButtonGroup tbg = new ToggleButtonGroup("Race");

                /* For every candidate, add a button to the group */
                for (int i = 0; i < MAX_CANDIDATES && cnt < candidates.size(); ++i, ++cnt) {
                    /* Create the button */
                    ToggleButton button = candidates.get(cnt);

                    /* Set up its rendering properties */
                    button.setWidth(RACE_DESCRIPTION_WIDTH);
                    button.setIncreasedFontSize(true);
                    button.setSize(button.execute(sizeVisitor));

                    /* Account for it in the layout */
                    panelConstraints.gridy = ycoord++;
                    panelConstraints.gridx = 0;

                    /* Put the button on a spacer, add the spacer to the panel and add the button to the group */
                    Spacer PDrawable = new Spacer(button, panel);
                    panel.add(PDrawable, panelConstraints);
                    tbg.getButtons().add(button);
                }

                /* Add the group to the panel */
                panel.add(new Spacer(tbg, panel));

                /* Stick our panel in the list of panels */
                panels.add(panel);
            }
            return panels;
        }

        /**
         * @see preptool.model.layout.manager.ALayoutManager.ICardLayout#setDescription(String)
         */
        public void setDescription(String description) {
            this.descriptionText = description;
        }

        /**
         * @see preptool.model.layout.manager.ALayoutManager.ICardLayout#setTitle(String)
         */
        public void setTitle(String title) {
            this.titleText = title;
        }
    }

    /**
     * PsychLayoutPanel is a subclass of JFrame and is used to temporarily hold
     * layout components so that GridBagLayout can be used to get the locations
     * of all of the components.
     *
     * @author Corey Shaw
     */
    public class PsychLayoutPanel extends JFrame {

        /**
         * North panel (for the title)
         */
        public JPanel north;

        /**
         * South panel (for the navigation buttons)
         */
        public JPanel south;

        /**
         * East panel (for the main content of the page)
         */
        public JPanel east;

        /**
         * West panel (for the sidebar - current step)
         */
        public JPanel west;

        /**
         * Constructs a new PsychLayoutPanel. Initializes the frame, and the
         * four internal panes.
         */
        PsychLayoutPanel() {
            /* Compute size of window */
            int width = WINDOW_WIDTH;
            int height = WINDOW_HEIGHT;

            /* Construct a frame so we can get an Insets object from it */
            JFrame sampleFrame = new JFrame();
            sampleFrame.setSize(width, height);
            sampleFrame.pack();

            /* Get the insets and use them to determine the visible width and height of the frame */
            Insets insets = sampleFrame.getInsets();
            int insetWidth = insets.left + insets.right;
            int insetHeight = insets.top + insets.bottom;
            height = height + insetHeight - 2;
            width = width + insetWidth - 2;
            setSize(width, height);

            /* Initialize this frame and its layout with the previously computed sizes */
            pack();
            setPreferredSize(new Dimension(width, height));
            setMinimumSize(new Dimension(width, height));
            setResizable(false);
            setLayout(new GridBagLayout());

            /* Initialize east pane, which will contain the actual ballot card */
            east = new JPanel();

            /* Initialize west pane */
            /* This pane will hold progress information about the voting session */
            west = new JPanel();
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridheight = 3;
            constraints.gridwidth = 1;
            constraints.weighty = 1;
            constraints.weightx = 0;
            /* Set the background to STAR-blue */
            west.setBackground(new Color(48, 149, 242));
            add(west, constraints);
            west.setLayout(new GridBagLayout());

            /* Initialize north pane */
            /* This pane will hold information about the current election and voting session */
            north = new JPanel();
            constraints.gridx = 1;
            constraints.gridy = 0;
            constraints.gridheight = 1;
            constraints.gridwidth = 1;
            constraints.weighty = 0;
            constraints.weightx = 0;
            north.setBackground(Color.pink);
            add(north, constraints);
            north.setLayout(new GridBagLayout());

            /* Initialize south pane, which holds navigation buttons */
            south = new JPanel();
            constraints.gridx = 1;
            constraints.gridy = 2;
            constraints.gridheight = 1;
            constraints.gridwidth = 1;
            constraints.weighty = 0;
            constraints.weightx = 0;
            constraints.anchor = GridBagConstraints.SOUTH;
            south.setBackground(Color.pink);
            add(south, constraints);
            south.setLayout(new GridBagLayout());
        }

        /**
         * @return an array of all components in the four panes, in order: north, south, west, east
         */ /* TODO Vet this revision */
        public Component[] getAllComponents() {
            List<Component> comps = new ArrayList<>(Arrays.asList(north.getComponents()));
            comps.addAll(Arrays.asList(south.getComponents()));
            comps.addAll(Arrays.asList(west.getComponents()));
            comps.addAll(Arrays.asList(east.getComponents()));

            return comps.toArray(north.getComponents());
        }

        /**
         * Adds a JPanel to the frame as the east panel
         *
         * @param panel the panel to add
         */
        protected void addAsEastPanel(JPanel panel) {
            /* Remove east pane if already exists */
            if (east != null) remove(east);

            /* Set constraints and add east pane */
            east = panel;
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 1;
            constraints.gridy = 1;
            constraints.gridheight = 1;
            constraints.gridwidth = 1;
            constraints.weighty = 1;
            constraints.weightx = 1;
            east.setBackground(Color.white);
            add(east, constraints);
        }

        /**
         * Adds a Commit Ballot button to the frame, with the given label as instructions
         *
         * @param l the label that tells the user where they're going
         */
        protected void addCommitButton(Label l) {
            Spacer PCastInfo = new Spacer(l, south);
            
            Spacer PCastButton = new Spacer(commitButton, south);

            /* Setup constraints  */
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.weightx = .5;
            constraints.weighty = .5;
            constraints.insets = new Insets(0, 0, 0, 0);
            constraints.gridx = 1;
            constraints.gridy = 0;
            constraints.anchor = GridBagConstraints.LINE_END;

            /* Add the label */
            south.add(PCastInfo, constraints);

            constraints.gridx = 1;
            constraints.gridy = 1;
            constraints.insets = new Insets(0, 0, 32, 50);

            /* Add the button */
            south.add(PCastButton, constraints);
        }

        /**
         * Adds a Next button to the frame, with the given label as instructions
         *
         * @param l the label that tells the user where they're going
         */
        protected void addNextButton(Label l) {
            /* Create a spacer for the label */
            Spacer PNextInfo = new Spacer(l, south);

            /* Create the next, er, next button and set its size constraint*/
            nextButton = new Button(getNextLayoutUID(), LiteralStrings.Singleton.get("NEXT_PAGE_BUTTON", language), "NextPage");
            nextButton.setIncreasedFontSize(true);
            nextButton.setSize(nextButton.execute(sizeVisitor));

            /* Add the button to a spacer */
            Spacer PNextButton = new Spacer(nextButton, south);


            /* Setup constraints and add label and button spacers */
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.weightx = .5;
            constraints.weighty = .5;
            constraints.insets = new Insets(0, 0, 0, 0);
            constraints.gridx = 1;
            constraints.gridy = 0;
            constraints.anchor = GridBagConstraints.LINE_END;
            south.add(PNextInfo, constraints);
            constraints.gridx = 1;
            constraints.gridy = 1;
            constraints.insets = new Insets(0, 0, 32, 50);
            south.add(PNextButton, constraints);
        }


        /**
         * Adds a Previous button to the frame, with the given label as instructions
         *
         * @param l the label that tells the user where they're going
         */
        protected void addPreviousButton(Label l) {
            /* Create a spacer for the label */
            Spacer PPreviousInfo = new Spacer(l, south);

            /* Create a new previous button and set its size information */
            previousButton = new Button(getNextLayoutUID(), LiteralStrings.Singleton.get("PREVIOUS_PAGE_BUTTON", language), "PreviousPage");
            previousButton.setIncreasedFontSize(true);
            previousButton.setSize(previousButton.execute(sizeVisitor));

            /* Add the button to a spacer*/
            Spacer PPreviousButton = new Spacer(previousButton, south);

            /* Setup constraints and add label and button spacers */
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.weightx = .5;
            constraints.weighty = .5;
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.anchor = GridBagConstraints.LINE_START;
            south.add(PPreviousInfo, constraints);
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.insets = new Insets(0, 50, 32, 0);
            south.add(PPreviousButton, constraints);
        }

        /**
         * Adds a Return button to the frame, with the given label as instructions
         *
         * @param l the label that tells the user where they're going
         * @param target page number of the target
         */
        protected void addReturnButton(Label l, int target) {
            /* Create the return button, loading its text and setting its font size, etc. */
        	returnButton = new Button(getNextLayoutUID(), LiteralStrings.Singleton.get("RETURN_BUTTON", language), "GoToPage");
            returnButton.setIncreasedFontSize(true);
            returnButton.setSize(returnButton.execute(sizeVisitor));
            returnButton.setPageNum(target);

            /* Put the label and button on spacers */
            Spacer PReturnInfo = new Spacer(l, south);
            Spacer PReturnButton = new Spacer(returnButton, south);

            /* Setup constraints and add label and button spacers */
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 0;
            south.add(PReturnInfo, constraints);
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.insets = new Insets(0, 0, 32, 0);
            south.add(PReturnButton, constraints);
        }

        /**
         * Adds the sidebar to the west pane, with the given step highlighted
         *
         * @param step the current step
         */
        protected void addSideBar(int step) {
            /* Create special constraints for teh side bar */
            GridBagConstraints constraints = new GridBagConstraints();

            Spacer PYouAreNowOn       = new Spacer(instructions, west);
            Spacer PMakeYourChoice    = new Spacer(makeYourChoices, west);
            Spacer PReviewYourChoices = new Spacer(reviewYourChoices, west);
            Spacer PRecordYourVote    = new Spacer(recordYourVote, west);

            /* Select correct highlighted label for current step */

            switch (step) {

                case 1:

                    PYouAreNowOn       = new Spacer(instructionsBold, west);
                    break;

                case 2:

                    PMakeYourChoice    = new Spacer(makeYourChoicesBold, west);
                    break;

                case 3:

                    PReviewYourChoices = new Spacer(reviewYourChoicesBold, west);
                    break;

                case 4:

                    PRecordYourVote    = new Spacer(recordYourVoteBold, west);
                    break;

                default:

                    throw new IllegalStateException("Not on any current valid step in the voting process!");

            }

            /* Add the labels to west pane */
            constraints.gridy = 0;
            constraints.gridx = 0;
            constraints.weighty = 1;
            constraints.fill = GridBagConstraints.VERTICAL;
            constraints.gridwidth = 1;
            west.add(PYouAreNowOn, constraints);

            constraints.gridx = 0;
            constraints.gridy = 1;
            west.add(PMakeYourChoice, constraints);

            constraints.gridx = 0;
            constraints.gridy = 2;
            west.add(PReviewYourChoices, constraints);

            constraints.gridx = 0;
            constraints.gridy = 3;
            west.add(PRecordYourVote, constraints);
        }

        /**
         * Adds a title to this frame
         *
         * @param title the title to add
         * @return a spacer containing the added title
         */
        protected Spacer addTitle(Label title) {
            /* Setup constraints and add title to north pane */
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridwidth = 1;
            constraints.weightx = 1;
            constraints.weighty = 1;
            constraints.anchor = GridBagConstraints.NORTH;
            title.setCentered(true);

            /* We reserve this space for the side bar */
            title.setWidth(WINDOW_WIDTH - 225);

            title.setSize(title.execute(sizeVisitor));
            Spacer label = new Spacer(title, north);
            north.add(label, constraints);
            
            return label;
        }

        /**
         * Adds a title String to this frame
         *
         * @param titleText the String title
         */
        protected Spacer addTitle(String titleText) {
            Label title = new Label(getNextLayoutUID(), titleText, sizeVisitor);
            return addTitle(title);
            
        }

    }

    /**
     * Maximum number of candidates on a page
     */
    private static final int MAX_CANDIDATES = 6;

    /**
     * Constant used when determining the font size
     */
    private static int FONT_SIZE_MULTIPLE = 8;

    /**
     * Width of the VoteBox window
     */
    private static final int WINDOW_WIDTH = 1600;

    /**
     * Height of the VoteBox window
     */
    private static final int WINDOW_HEIGHT = 900;


    /**
     * Visitor that renders a component and returns an image
     */
    private static ILayoutComponentVisitor<Boolean, BufferedImage> imageVisitor = new ILayoutComponentVisitor<Boolean, BufferedImage>() {
    	
        /**
         * Gets the image from the Background
         *
         * @see preptool.model.layout.ILayoutComponentVisitor#forBackground(preptool.model.layout.Background, Object[])
         */
        public BufferedImage forBackground(Background bg, Boolean... param) {
            return bg.getImage();
        }

        /**
         * Renders a Button
         *
         * @see preptool.model.layout.ILayoutComponentVisitor#forButton(preptool.model.layout.Button, Object[])
         */
        public BufferedImage forButton(Button button, Boolean... param) {

            /* Buttons will be scaled to twice the normal font size */
            int fontsize = 2 * FONT_SIZE_MULTIPLE;

            /* If the button is supposed to be of increased size, increment the font size by 4 */
            if (button.isIncreasedFontSize()) {
                fontsize += 4;
            }

            /* Now call the rendering utility to render the button */
            return RenderingUtils.renderButton(button.getText(), fontsize, button.isBold(), button.isBoxed(), -1, button.getBackgroundColor(), param[0]);
        }

        /**
         * Renders a label
         *
         * @see preptool.model.layout.ILayoutComponentVisitor#forLabel(preptool.model.layout.Label, Object[])
         */
        public BufferedImage forLabel(Label l, Boolean... param) {

            /* Labels will be scaled to twice the normal font size */
            int fontsize = 2 * FONT_SIZE_MULTIPLE;

             /* If the label is supposed to be of increased size, increment the font size by 4 */
            if (l.isIncreasedFontSize()) {
                fontsize += 4;
            }

            /* Now call the rendering utility to render the label */
            return RenderingUtils.renderLabel(l.getText(), l.getInstructions(), l.getDescription(), fontsize, l.getWidth(),
                                              l.getColor(), l.isBold(), l.isBoxed(), l.isCentered(), param[0]);
        }

        /**
         * Renders a ReviewButton
         *
         *  @see preptool.model.layout.ILayoutComponentVisitor#forReviewButton(preptool.model.layout.ReviewButton, Object[])
         */
        public BufferedImage forReviewButton(ReviewButton rb, Boolean... param) {

            /* Review buttons will be scaled to twice less 4 the normal font size */
            int fontsize = 2 * (FONT_SIZE_MULTIPLE - 1) - 2;

            /* Render the button using the rendering utility */
            BufferedImage buttonImg = RenderingUtils.renderButton(rb.getText(), fontsize, rb.isBold(), rb.isBoxed(),
                                                                  REVIEW_SCREEN_WIDTH, rb.getBackgroundColor(), param[0]);
            
			/* render party information [dsandler] */
			String aux = rb.getAuxText();

            /* Check that we are showing the party and that there is party data to show */
			if (REVIEW_SCREEN_SHOW_PARTY && aux != null && !aux.equals("")) {

                /* If the party information is supposed to be parenthesized, do it here [ e.g. (DEM) ]*/
				if (REVIEW_SCREEN_PARENTHESIZE_PARTY) 
					aux = "(" + aux + ")";

                /* Now render the String. TODO Perhaps this should be in RenderingUtils? */
				Graphics2D g = buttonImg.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                /* Load the preselected font */
				Font font = new Font(RenderingUtils.FONT_NAME, Font.PLAIN, fontsize);
				g.setFont(font);
				g.setColor(Color.BLACK);

                /* Draw a bounding box around the party */
				Rectangle2D partyTextBounds = font.getStringBounds(aux, new FontRenderContext(null, true, true));

				/* draw the party on the far right side (Right-aligned) */
				g.drawString(aux, (int) (buttonImg.getWidth() - partyTextBounds.getWidth() - 10), (int) (partyTextBounds.getHeight() + 8));
			}

			return buttonImg;
        }

        /**
         * Renders a ReviewLabel
         *
         *  @see preptool.model.layout.ILayoutComponentVisitor#forReviewLabel(preptool.model.layout.ReviewLabel, Object[])
         */
        public BufferedImage forReviewLabel(ReviewLabel rl, Boolean... param) {

             /* Review labels will be scaled to twice less 2 the normal font size */
            int fontsize = 2 * (FONT_SIZE_MULTIPLE - 1);

            /* Return the result of the rendering utility's render of the label */
            return RenderingUtils.renderLabel(rl.getText(), "", "", fontsize, 100, rl.getColor(), rl.isBold(), rl.isBoxed(), rl.isCentered(), param[0]);
        }

        /**
         * Renders a ToggleButton
         *
         *  @see preptool.model.layout.ILayoutComponentVisitor#forToggleButton(preptool.model.layout.ToggleButton, Object[])
         */
        public BufferedImage forToggleButton(ToggleButton tb, Boolean... param) {

            /* The toggle button font size will be twice the normal font size */
            int fontsize = 2 * FONT_SIZE_MULTIPLE;

            /* If the button is supposed to have larger font size, make it 4 larger than twice the normal size */
            if (tb.isIncreasedFontSize()) {
                fontsize += 4;
            }

            /* Return the rendering utility's render of the button */
            return RenderingUtils.renderToggleButton(tb.getText(), tb.getSecondLine(), tb.getParty(), fontsize,	tb.getWidth(), tb.isBold(), param[0], param[1]);
        }

        /**
         * @see preptool.model.layout.ILayoutComponentVisitor#forToggleButtonGroup(preptool.model.layout.ToggleButtonGroup, Object[])
         *
         * @return null
         */
        public BufferedImage forToggleButtonGroup(ToggleButtonGroup tbg, Boolean... param) {
            return null;
        }


        /**
         * @see preptool.model.layout.ILayoutComponentVisitor#forPrintButton(preptool.model.layout.PrintButton, Object[])
         */
        public BufferedImage forPrintButton(PrintButton pb, Boolean... param) {

            /* Make the font size for print buttons the normal font size */
            int fontsize = FONT_SIZE_MULTIPLE;

            /* Increase the font size if necessary */
            if (pb.isIncreasedFontSize()) {
                fontsize += 4;
            }

            /* Render using the rendering utility. Chose 281 for the width because it suits our printing well */
            return RenderingUtils.renderPrintButton(pb.getUID(), pb.getText(), pb.getSecondLine(), pb.getParty(), fontsize, 281, false, true);
		}
    };

    /**
     * Visitor that calculates the size of a component
     */
    private static ILayoutComponentVisitor<Object, Dimension> sizeVisitor = new ILayoutComponentVisitor<Object, Dimension>() {

        /**
         * Gets the size of the Background
         */
        public Dimension forBackground(Background bg, Object... param) {
            return new Dimension(bg.getWidth(), bg.getHeight());
        }

        /**
         * Calculates the size of the Button
         */
        public Dimension forButton(Button button, Object... param) {
            int size = 1;
            int fontsize = (size + 1) * FONT_SIZE_MULTIPLE;

            if (button.isIncreasedFontSize()) {
                fontsize += 4;
            }

            return RenderingUtils.getButtonSize(button.getText(), fontsize,
                    button.isBold());
        }

        /**
         * Calculates the size of the
         */
        public Dimension forLabel(Label l, Object... param) {
            int size = 1;
            int fontsize = (size + 1) * FONT_SIZE_MULTIPLE;
            if (l.isIncreasedFontSize()) {
                fontsize += 4;
            }

            return RenderingUtils.getLabelSize(l.getText(),
                    l.getInstructions(), l.getDescription(), fontsize, l
                            .getWidth(), l.isBold(), l.isCentered());
        }

        /**
         * Calculates the size of the ReviewButton
         */
        public Dimension forReviewButton(ReviewButton rb, Object... param) {
            int size = 1;
            int fontsize = (int) ((size + .5) * (FONT_SIZE_MULTIPLE - 2));

            return RenderingUtils.getButtonSize(rb.getText(), fontsize, rb
                    .isBold());
        }

        /**
         * Calculates the size of the ReviewLabel
         */
        public Dimension forReviewLabel(ReviewLabel rl, Object... param) {
            int size = 1;
            int fontsize = (int) ((size + .5) * (FONT_SIZE_MULTIPLE - 2));

            return RenderingUtils.getLabelSize(rl.getText(), "", "", fontsize,
                    rl.getWidth(), rl.isBold(), rl.isCentered());
        }

        /**
         * Calculates the size of the ToggleButton
         */
        public Dimension forToggleButton(ToggleButton tb, Object... param) {
            int size = 1;

            int fontsize = (size + 1) * FONT_SIZE_MULTIPLE;
            if (tb.isIncreasedFontSize()) {
                fontsize += 4;
            }

            return RenderingUtils
                    .getToggleButtonSize(tb.getText(), tb.getSecondLine(), tb
                            .getParty(), fontsize, RACE_DESCRIPTION_WIDTH, tb.isBold());
        }

        /**
         * Returns null
         */
        public Dimension forToggleButtonGroup(ToggleButtonGroup tbg,
                Object... param) {
            return null;
        }

		public Dimension forPrintButton(PrintButton pb, Object... param) {
            int size = 1;

            int fontsize = (size + 1) * FONT_SIZE_MULTIPLE;
            if (pb.isIncreasedFontSize()) {
                fontsize += 4;
            }

            return RenderingUtils
                    .getToggleButtonSize(pb.getText(), pb.getSecondLine(), pb
                            .getParty(), fontsize, RACE_DESCRIPTION_WIDTH, pb.isBold());
		}
    };

    /**
     * The language this LayoutManager is responsible for
     */
    private Language language;

    /**
     * A Common Cast Button
    */
    protected Button castButton;
    
    /**
     * A Common Commit Button
     */
    protected Button commitButton;

    /**
     * A Common NextButton
     */
    protected Button nextButton;

    /**
     * A Common PreviousButton
     */
    protected Button previousButton;

    /**
     * A Common Return Button
     */
    protected Button returnButton;

    /**
     * Instructions label for the sidebar
     */
    protected Label instructions;

    /**
     * Make your choices label for the sidebar
     */
    protected Label makeYourChoices;

    /**
     * Review your choices label for the sidebar
     */
    protected Label reviewYourChoices;

    /**
     * Record your vote label for the sidebar
     */
    protected Label recordYourVote;

    /**
     * Bold Instructions label for the sidebar
     */
    protected Label instructionsBold;

    /**
     * Bold Make your choices label for the sidebar
     */
    protected Label makeYourChoicesBold;

    /**
     * Bold Review your choices label for the sidebar
     */
    protected Label reviewYourChoicesBold;

    /**
     * Bold Record your vote label for the sidebar
     */
    protected Label recordYourVoteBold;

    /**
     * Next race label
     */
    protected Label nextInfo;

    /**
     * Previous race label
     */
    protected Label previousInfo;

    /**
     * Return label
     */
    protected Label returnInfo;

    /**
     * More candidates label
     */
    protected Label moreCandidatesInfo;

    /**
     * The background for this layout
     */
    protected Background background;

    /**
     * Background for this layout, without the sidebar
     */
    protected Background simpleBackground;

    /**
     * Creates a new PsychLayoutManager and initializes many of the "common"
     * components, such as the next button.
     */
    public PsychLayoutManager(Language language, int numCardsPerReviewPage, int fontSize, boolean textToSpeech) {
        this.language = language;
        GENERATE_AUDIO = textToSpeech;
        
        CARDS_PER_REVIEW_PAGE = numCardsPerReviewPage;
    	FONT_SIZE_MULTIPLE = fontSize;

        instructions = new Label(getNextLayoutUID(), LiteralStrings.Singleton
                .get("SIDEBAR_INSTRUCTIONS", language));
        instructions.setWidth(225);
        instructions.setIncreasedFontSize(true);
        instructions.setColor(new Color(72, 72, 72));
        instructions.setSize(instructions.execute(sizeVisitor));

        makeYourChoices = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("SIDEBAR_MAKE_CHOICES", language));
        makeYourChoices.setWidth(225);
        makeYourChoices.setIncreasedFontSize(true);
        makeYourChoices.setColor(new Color(72, 72, 72));
        makeYourChoices.setSize(makeYourChoices.execute(sizeVisitor));

        reviewYourChoices = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton
                        .get("SIDEBAR_REVIEW_CHOICES", language));
        reviewYourChoices.setWidth(225);
        reviewYourChoices.setIncreasedFontSize(true);
        reviewYourChoices.setColor(new Color(72, 72, 72));
        reviewYourChoices.setSize(reviewYourChoices.execute(sizeVisitor));

        recordYourVote = new Label(getNextLayoutUID(), LiteralStrings.Singleton
                .get("SIDEBAR_RECORD_VOTE", language));
        recordYourVote.setWidth(225);
        recordYourVote.setIncreasedFontSize(true);
        recordYourVote.setColor(new Color(72, 72, 72));
        recordYourVote.setSize(recordYourVote.execute(sizeVisitor));

        instructionsBold = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get(
                        "SIDEBAR_INSTRUCTIONS_HIGHLIGHTED", language));
        instructionsBold.setWidth(225);
        instructionsBold.setIncreasedFontSize(true);
        instructionsBold.setColor(Color.WHITE);
        instructionsBold.setBold(false);
        instructionsBold.setSize(instructionsBold.execute(sizeVisitor));

        makeYourChoicesBold = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get(
                        "SIDEBAR_MAKE_CHOICES_HIGHLIGHTED", language));
        makeYourChoicesBold.setWidth(225);
        makeYourChoicesBold.setIncreasedFontSize(true);
        makeYourChoicesBold.setColor(Color.WHITE);
        makeYourChoicesBold.setBold(false);
        makeYourChoicesBold.setSize(makeYourChoicesBold.execute(sizeVisitor));

        reviewYourChoicesBold = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get(
                        "SIDEBAR_REVIEW_CHOICES_HIGHLIGHTED", language));
        reviewYourChoicesBold.setWidth(225);
        reviewYourChoicesBold.setIncreasedFontSize(true);
        reviewYourChoicesBold.setColor(Color.WHITE);
        reviewYourChoicesBold.setBold(false);
        reviewYourChoicesBold.setSize(reviewYourChoicesBold
                .execute(sizeVisitor));

        recordYourVoteBold = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("SIDEBAR_RECORD_VOTE_HIGHLIGHTED",
                        language));
        recordYourVoteBold.setWidth(225);
        recordYourVoteBold.setIncreasedFontSize(true);
        recordYourVoteBold.setColor(Color.WHITE);
        recordYourVoteBold.setBold(false);
        recordYourVoteBold.setSize(recordYourVoteBold.execute(sizeVisitor));

        nextButton = new Button(getNextLayoutUID(), LiteralStrings.Singleton
                .get("NEXT_PAGE_BUTTON", language), "NextPage");
        nextButton.setIncreasedFontSize(true);
        nextButton.setSize(nextButton.execute(sizeVisitor));

        previousButton = new Button(getNextLayoutUID(),
                LiteralStrings.Singleton.get("PREVIOUS_PAGE_BUTTON", language),
                "PreviousPage");
        previousButton.setIncreasedFontSize(true);
        previousButton.setSize(previousButton.execute(sizeVisitor));

        returnButton = new Button(getNextLayoutUID(), LiteralStrings.Singleton
                .get("RETURN_BUTTON", language), "GoToPage");
        returnButton.setIncreasedFontSize(true);
        returnButton.setSize(returnButton.execute(sizeVisitor));

        castButton = new Button(getNextLayoutUID(), LiteralStrings.Singleton
                .get("CAST_BUTTON", language), "CastBallot");
        castButton.setIncreasedFontSize(true);
        castButton.setSize(castButton.execute(sizeVisitor));
        
        commitButton = new Button(getNextLayoutUID(), LiteralStrings.Singleton
                .get("COMMIT_BUTTON", language), "CommitBallot");
        commitButton.setIncreasedFontSize(true);
        commitButton.setSize(commitButton.execute(sizeVisitor));

        nextInfo = new Label(getNextLayoutUID(), LiteralStrings.Singleton.get(
                "FORWARD_NEXT_RACE", language), sizeVisitor);
        previousInfo = new Label(getNextLayoutUID(), LiteralStrings.Singleton
                .get("BACK_PREVIOUS_RACE", language), sizeVisitor);
        returnInfo = new Label(getNextLayoutUID(), LiteralStrings.Singleton
                .get("RETURN_REVIEW_SCREEN", language), sizeVisitor);
        moreCandidatesInfo = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("MORE_CANDIDATES", language),
                sizeVisitor);



        background = makeBackground();
        simpleBackground = makeSimpleBackground();
    }


    public Label getPartyInstructions() {
        return new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("PARTY_INSTRUCTIONS", language),
                sizeVisitor);
    }

    public Label getPropInstructions() {
        return new Label(getNextLayoutUID(),
                        LiteralStrings.Singleton.get("PROPOSITION_INSTRUCTIONS", language),
                        sizeVisitor);
    }

    public Label getRaceInstructions() {
        return new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("RACE_INSTRUCTIONS", language),
                sizeVisitor);
    }

    /**
     * @return the image rendering visitor
     */
    @Override
    public ILayoutComponentVisitor<Boolean, BufferedImage> getImageVisitor() {
        return imageVisitor;
    }

    /**
     * @return the language
     */
    @Override
    public Language getLanguage() {
        return language;
    }

    /**
     * @return the size calculating visitor
     */
    @Override
    public ILayoutComponentVisitor<Object, Dimension> getSizeVisitor() {
        return sizeVisitor;
    }

    @Override
    public ArrayList<JPanel> makeCardPage(ACard card) {
        return card.layoutCard(this, new PsychCardLayout()).makeIntoPanels();
    }

    /**
     * Makes the layout from the given ballot, as specified by the Psychology
     * department.
     */
    @Override
    public Layout makeLayout(Ballot ballot) {
        assignUIDsToBallot(ballot);
        Layout layout = new Layout();
        if (ballot.getLanguages().size() > 1) {
            layout.getPages()
                    .add(makeLanguageSelectPage(ballot.getLanguages()));
            layout.getPages().add(makeInstructionsPage(true));
        } else {
            layout.getPages().add(makeInstructionsPage(false));
        }
        int cnt = 1;
        for (ACard card : ballot.getCards()) {
            layout.getPages().addAll(
                    makeCardLayoutPage(card, false, 0, cnt, ballot.getCards()
                            .size()));
            ++cnt;
        }
        //layout.getPages().add(new Page());
        int reviewPageNum = layout.getPages().size();

        HashMap<Integer, Integer> pageTargets = new HashMap<>();
        
        for (int raceN = 0; raceN < ballot.getCards().size(); raceN++) {
        	ACard card = ballot.getCards().get(raceN);
        	//correctly deal with boundary conditions: size = 1, size = CARDS_PER_REVIEW_PAGE or a multiple thereof
        	int additionalReviewPages = (ballot.getCards().size() - 1) / CARDS_PER_REVIEW_PAGE;
        	//there are 2 pages after the last review screen: Cast and Success 
        	int reviewCardNumber = raceN + 3;

            pageTargets.put(raceN, reviewPageNum + additionalReviewPages + reviewCardNumber);
            int currentReviewPage = raceN / CARDS_PER_REVIEW_PAGE;
            layout.getPages().addAll(
                    makeCardLayoutPage(card, true, reviewPageNum + currentReviewPage, 0, 0));
        }
        
        List<Page> reviewPages = makeReviewPage(ballot, pageTargets);
        
        layout.getPages().addAll(reviewPageNum, reviewPages);
        
        for(Page reviewPage : reviewPages)
        	reviewPage.markAsReviewPage();

        layout.getPages().add(reviewPageNum + (ballot.getCards().size() / CARDS_PER_REVIEW_PAGE) + 1, makeCommitPage());

        layout.getPages().add(reviewPageNum + (ballot.getCards().size() / CARDS_PER_REVIEW_PAGE) + 2, makeSuccessPage());
        
        layout.getPages().add(makeOverrideCancelPage());
        layout.setOverrideCancelPage(layout.getPages().size()-1);
        layout.getPages().add(makeOverrideCastPage());
        layout.setOverrideCastPage(layout.getPages().size()-1);
        

        layout.getPages().add(makeResponsePage());
        layout.getPages().add(makeProvisionalSuccessPage());
        layout.setReponsePage(layout.getPages().size()-2);
        layout.setProvisionalPage(layout.getPages().size() - 1);

        
        //LAST_LAYOUT = layout;
        
        //#ifdef NONE_OF_ABOVE
        // Get the number of cards
        int numCards = ballot.getCards().size();
        // Add a "no-selection alert page" for each race
//        for (int raceN = 0; raceN < numCards; raceN++) {
//        	layout.getPages().add(makeNoSelectionPage(raceN+1));
//        }
//        // Add another no-selection alert page for each race-review screen
//        for (int raceN = 0; raceN < numCards; raceN++) {
//        	layout.getPages().add(makeNoSelectionPage(raceN+numCards+4));
//        }
        //#endif


        return layout;
    }

    /**
     * Makes a Background for this LayoutManager.
     * @return the Background
     */
    protected Background makeBackground() {
        PsychLayoutPanel frame = new PsychLayoutPanel();

        Label instructionsTitle = new Label("L0", LiteralStrings.Singleton.get(
                "INSTRUCTIONS_TITLE", language));
        instructionsTitle.setCentered(true);
        instructionsTitle.setSize(instructionsTitle.execute(sizeVisitor));
        frame.addTitle(instructionsTitle);
        frame.addSideBar(1);
        frame.addNextButton(nextInfo);

        JPanel east = new JPanel();
        east.setLayout(new GridBagLayout());
        Label instrLabel = new Label("L0", LiteralStrings.Singleton.get(
                "INSTRUCTIONS", language), sizeVisitor);
        Spacer sp = new Spacer(instrLabel, east);
        east.add(sp);
        frame.addAsEastPanel(east);

        frame.validate();
        frame.pack();

        BufferedImage image = new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphs = (Graphics2D) image.getGraphics();
        graphs.setColor(Color.WHITE);
        graphs.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        graphs.setColor(new Color(48, 149, 242));
        graphs.fillRect(0, 0, frame.west.getWidth(), WINDOW_HEIGHT);
        graphs.setColor(Color.PINK);
        graphs.fillRect(frame.west.getWidth(), 0, frame.north.getWidth(),
                frame.north.getHeight());
        graphs.fillRect(frame.west.getWidth(), WINDOW_HEIGHT
                - frame.south.getHeight(), frame.south.getWidth(), frame.south
                .getHeight());
        return new Background(getNextLayoutUID(), image);
    }

    /**
     * Makes a Page that contains a Card, by creating the page as normal and
     * then calling into the visitor to handle the Card-specific components
     * @param card the card
     * @param jump whether this page is a jump page from the review screen
     * @param target page number of the review screen
     * @param idx the index of the card
     * @param total total number of cards
     * @return the completed Page
     */
    protected ArrayList<Page> makeCardLayoutPage(ACard card, boolean jump,
            int target, int idx, int total) {
        ArrayList<JPanel> cardPanels = makeCardPage(card);
        ArrayList<Page> pages = new ArrayList<>();

        @SuppressWarnings("unused")
		Spacer title = null;
        
        for (int i = 0; i < cardPanels.size(); i++) {
            // Setup card frame
            PsychLayoutPanel cardFrame = new PsychLayoutPanel();
            title = cardFrame.addTitle(card.getTitle(language));
            cardFrame.addSideBar(2);
            if (!jump) {
                if (i > 0) {
                    cardFrame.addPreviousButton((Label) moreCandidatesInfo.clone());
                }
                else if (idx == 1) {
                    cardFrame.addPreviousButton(new Label(getNextLayoutUID(),
                            LiteralStrings.Singleton.get("BACK_INSTRUCTIONS",
                                    language), sizeVisitor));
                }
                else {
                    cardFrame.addPreviousButton(previousInfo);
                }
                if (i < cardPanels.size() - 1) {
                    cardFrame.addNextButton((Label) moreCandidatesInfo.clone());
                }
                else if (idx == total) {
                	Label forward = new Label(
                			getNextLayoutUID(),
                            LiteralStrings.Singleton.get("FORWARD_REVIEW", language), 
                            sizeVisitor);

                    cardFrame.addNextButton(forward);
                }
                else {
                    cardFrame.addNextButton(nextInfo);
                }
            } else {
                cardFrame.addReturnButton(returnInfo, target);

                if (i > 0)
                    cardFrame.addPreviousButton((Label) moreCandidatesInfo.clone());
                if (i < cardPanels.size() - 1)
                    cardFrame.addNextButton((Label) moreCandidatesInfo.clone());
            }

            // Add card's content as east pane
            cardFrame.addAsEastPanel(cardPanels.get(i));

            // Add instructions
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.NORTH;
            constraints.fill = GridBagConstraints.VERTICAL;
            constraints.gridy = 1;
            constraints.gridx = 0;
            constraints.weightx = 1;
            constraints.weighty = 1;
            Spacer instspacer;

            Label instructions = null;

            if(card instanceof PartyCard)
                instructions = getPartyInstructions();
            else if(card instanceof PropositionCard)
                instructions = getPropInstructions();
            else
                instructions = getRaceInstructions();


            instspacer = new Spacer(instructions, cardFrame.north);

            cardFrame.north.add(instspacer, constraints);
            cardFrame.validate();
            cardFrame.pack();

            // Add all components to a Page, with their updated positions
            Page cardPage = new Page();
            cardPage.getComponents().add(background);
            cardPage.setBackgroundLabel(background.getUID());

            boolean titled = false;

            ALayoutComponent tempButton = null;

            for (Component c : cardFrame.getAllComponents()) {
                Spacer s = (Spacer) c;
                s.updatePosition();
                if (!(s.getComponent() instanceof ToggleButton)){
                    cardPage.getComponents().add(s.getComponent());
                }//if

                ALayoutComponent button = s.getComponent();
                boolean flag = false;


                if(button instanceof Label){
                    if(((Label) button).getText().equals(card.getTitle(language)))
                        if(titled)
                            flag = true;
                        else
                            titled = true;
                }






                if(button instanceof ToggleButton || flag){
                    if(jump){

                        //TODO Make this work with lots of candidates (i > n)
                        if(tempButton == null){
                            button.setPrevious(instructions);
                            button.setUp(instructions);
                            instructions.setDown(button);
                            instructions.setNext(button);

                        }else{
                            button.setPrevious(tempButton);
                            button.setUp(tempButton);
                            tempButton.setNext(button);
                            tempButton.setDown(button);
                        }

                        button.setLeft(returnButton);
                        button.setRight(returnButton);


                        tempButton = button;
                    } else{
                        if(tempButton == null){
                            button.setPrevious(instructions);
                            button.setUp(instructions);
                            previousButton.setNext(instructions);
                            instructions.setNext(button);
                            instructions.setPrevious(previousButton);
                            instructions.setLeft(previousButton);
                            instructions.setDown(button);
                            instructions.setRight(button);
                            nextButton.setDown(button);

                        }else{
                            button.setPrevious(tempButton);
                            button.setUp(tempButton);
                            tempButton.setNext(button);
                            tempButton.setDown(button);
                        }

                        button.setLeft(previousButton);
                        button.setRight(nextButton);


                        tempButton = button;

                    }

                }
            }

            if(jump){
                tempButton.setNext(returnButton);
                tempButton.setDown(returnButton);
                returnButton.setPrevious(tempButton);
                returnButton.setUp(tempButton);
            } else{
                tempButton.setNext(nextButton);
                tempButton.setDown(nextButton);
                nextButton.setPrevious(tempButton);
                nextButton.setUp(tempButton);
                nextButton.setLeft(tempButton);
                previousButton.setUp(tempButton);
            }

            pages.add(cardPage);
        }
        return pages;
    }

    @Override
    protected Page makeCommitPage() {
        PsychLayoutPanel frame = new PsychLayoutPanel();
        Label recordTitle = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("RECORD_TITLE", language));
        recordTitle.setBold(true);
        recordTitle.setCentered(true);
        recordTitle.setSize(recordTitle.execute(sizeVisitor));
        frame.addTitle(recordTitle);
        frame.addSideBar(4);
        frame.addPreviousButton(new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("BACK_REVIEW", language),
                sizeVisitor));
        frame.addCommitButton(new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("NEXT_PAGE_BUTTON", language),
                sizeVisitor));

        JPanel east = new JPanel();
        east.setLayout(new GridBagLayout());
        Label instrLabel = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("RECORD_INSTRUCTIONS", language),
                sizeVisitor);
        Spacer sp = new Spacer(instrLabel, east);

        previousButton.setNext(instrLabel);
        previousButton.setRight(instrLabel);
        instrLabel.setLeft(previousButton);
        instrLabel.setPrevious(previousButton);
        instrLabel.setRight(commitButton);
        instrLabel.setNext(commitButton);
        commitButton.setPrevious(instrLabel);
        commitButton.setLeft(instrLabel);

        east.add(sp);
        frame.addAsEastPanel(east);

        frame.validate();
        frame.pack();

        Page page = new Page();
        page.getComponents().add(background);
        page.setBackgroundLabel(background.getUID());

        for (Component c : frame.getAllComponents()) {
            Spacer s = (Spacer) c;
            s.updatePosition();
            page.getComponents().add(s.getComponent());
        }
        return page;
    }

    @Override
    protected Page makeInstructionsPage(boolean hadLanguageSelect) {
        PsychLayoutPanel frame = new PsychLayoutPanel();

        Label instructionsTitle = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("INSTRUCTIONS_TITLE", language));
        instructionsTitle.setCentered(true);
        instructionsTitle.setSize(instructionsTitle.execute(sizeVisitor));
        frame.addTitle(instructionsTitle);
        frame.addSideBar(1);

        JPanel east = new JPanel();
        east.setLayout(new GridBagLayout());
        Label instrLabel = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("INSTRUCTIONS", language),
                sizeVisitor);

        if (hadLanguageSelect){
            frame.addPreviousButton(new Label(getNextLayoutUID(),
                    LiteralStrings.Singleton.get("BACK_LANGUAGE_SELECT",
                            language), sizeVisitor));
            frame.addNextButton(new Label(getNextLayoutUID(),
                    LiteralStrings.Singleton.get("FORWARD_FIRST_RACE", language),
                    sizeVisitor));
            nextButton.setPrevious(instrLabel);
            nextButton.setLeft(instrLabel);
            instrLabel.setNext(nextButton);
            instrLabel.setRight(nextButton);
            instrLabel.setPrevious(previousButton);
            instrLabel.setLeft(previousButton);
            previousButton.setNext(instrLabel);
            previousButton.setRight(instrLabel);
        } else{
            frame.addNextButton(new Label(getNextLayoutUID(),
                    LiteralStrings.Singleton.get("FORWARD_FIRST_RACE", language),
                    sizeVisitor));

            instrLabel.setNext(nextButton);
            instrLabel.setRight(nextButton);
            nextButton.setPrevious(instrLabel);
            nextButton.setLeft(instrLabel);


        }


        Spacer sp = new Spacer(instrLabel, east);
        east.add(sp);
        frame.addAsEastPanel(east);

        frame.validate();
        frame.pack();

        Page page = new Page();
        if (background != null) {
            page.getComponents().add(background);
            page.setBackgroundLabel(background.getUID());
        }

        for (Component c : frame.getAllComponents()) {
            Spacer s = (Spacer) c;
            s.updatePosition();
            page.getComponents().add(s.getComponent());
        }
        return page;
    }

    @Override
    protected Page makeLanguageSelectPage(ArrayList<Language> languages) {
        PsychLayoutPanel frame = new PsychLayoutPanel();

        frame.addTitle(LiteralStrings.Singleton.get("LANGUAGE_SELECT_TITLE",
                language));
        frame.addSideBar(1);
        frame.addNextButton(new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("FORWARD_INSTRUCTIONS", language),
                sizeVisitor));

        JPanel east = new JPanel();
        east.setLayout(new GridBagLayout());
        GridBagConstraints eastConstraints = new GridBagConstraints();

        eastConstraints.anchor = GridBagConstraints.SOUTH;
        eastConstraints.fill = GridBagConstraints.VERTICAL;
        int ycoord = 0; // the ycoordinate of where to add in gridbag
        eastConstraints.gridy = ycoord;
        eastConstraints.gridx = 0;

        ycoord++;
        Label title = new Label(getNextLayoutUID(), LiteralStrings.Singleton
                .get("LANGUAGE_SELECT_TITLE", language));
        title.setWidth(LANG_SELECT_WIDTH);
        title.setBoxed(true);
        title.setCentered(true);
        title.setSize(title.execute(sizeVisitor));

        Spacer PTitle = new Spacer(title, east);
        east.add(PTitle, eastConstraints);

        Label instLabel = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("LANGUAGE_SELECT_INSTRUCTIONS",
                        language), sizeVisitor);

        instLabel.setNext(title);
        title.setPrevious(instLabel);


        instLabel.setRight(nextButton);

        ToggleButtonGroup tbg = new ToggleButtonGroup("LanguageSelect");

        ALayoutComponent tempButton = null;

        for (Language lang : languages) {
            LanguageButton button = new LanguageButton(getNextLayoutUID(), lang
                    .getName());

            //Setup the navigation for this.
            //TODO Write some sort of manual for how this all works
            if(tempButton == null){
                title.setNext(button);
                title.setDown(button);
                button.setUp(title);
                button.setPrevious(title);

            } else{
                button.setPrevious(tempButton);
                button.setUp(tempButton);
                tempButton.setNext(button);
                tempButton.setDown(button);
            }

            button.setLeft(nextButton);
            button.setRight(nextButton);

            button.setLanguage(lang);
            button.setWidth(LANG_SELECT_WIDTH);
            button.setIncreasedFontSize(true);
            button.setSize(button.execute(sizeVisitor));
            eastConstraints.gridy = ycoord++;
            eastConstraints.gridx = 0;
            Spacer PDrawable = new Spacer(button, east);
            east.add(PDrawable, eastConstraints);
            tbg.getButtons().add(button);

            tempButton = button;
        }

        tempButton.setNext(nextButton);
        tempButton.setDown(nextButton);
        nextButton.setPrevious(tempButton);
        nextButton.setUp(tempButton);
        nextButton.setLeft(tempButton);

        east.add(new Spacer(tbg, east));
        frame.addAsEastPanel(east);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.gridy = 1;
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;

        Spacer instspacer = new Spacer(instLabel, frame.north);
        frame.north.add(instspacer, constraints);

        frame.validate();
        frame.pack();

        Page page = new Page();
        if (background != null) {
            page.getComponents().add(background);
            page.setBackgroundLabel(background.getUID());
        }

        for (Component c : frame.getAllComponents()) {
            Spacer s = (Spacer) c;
            s.updatePosition();
            if (!(s.getComponent() instanceof ToggleButton))
                page.getComponents().add(s.getComponent());
        }
        return page;
    }

    @Override
    protected Page makeOverrideCancelPage() {
        PsychLayoutPanel frame = new PsychLayoutPanel();
        Label successTitle = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("OVERRIDE_CANCEL_TITLE", language));
        successTitle.setBold(true);
        successTitle.setCentered(true);
        successTitle.setSize(successTitle.execute(sizeVisitor));
        frame.addTitle(successTitle);
        frame.remove(frame.west);

        Label reviewInstructions = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("OVERRIDE_CANCEL_INSTRUCTIONS",
                        language), sizeVisitor);
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.VERTICAL;
        c.gridy = 1;
        c.gridx = 0;
        c.weightx = 1;
        c.weighty = 1;
        Spacer instspacer = new Spacer(reviewInstructions, frame.north);
        frame.north.add(instspacer, c);

        JPanel east = new JPanel();
        east.setLayout(new GridBagLayout());
        c = new GridBagConstraints();

        Button confirmBtn = new Button(getNextLayoutUID(),
                LiteralStrings.Singleton.get("OVERRIDE_CANCEL_CONFIRM",
                        language), "OverrideCancelConfirm");
        confirmBtn.setIncreasedFontSize(true);
        confirmBtn.setSize(confirmBtn.execute(sizeVisitor));
        Spacer sp = new Spacer(confirmBtn, east);
        east.add(sp, c);
        
        Button denyBtn = new Button(getNextLayoutUID(),
                LiteralStrings.Singleton.get("OVERRIDE_DENY",
                        language), "OverrideCancelDeny");
        denyBtn.setIncreasedFontSize(true);
        denyBtn.setSize(denyBtn.execute(sizeVisitor));

        reviewInstructions.setNext(confirmBtn);
        reviewInstructions.setDown(confirmBtn);
        confirmBtn.setPrevious(reviewInstructions);
        confirmBtn.setUp(reviewInstructions);
        confirmBtn.setNext(denyBtn);
        confirmBtn.setDown(denyBtn);
        denyBtn.setPrevious(confirmBtn);
        denyBtn.setUp(confirmBtn);

        sp = new Spacer(denyBtn, east);
        c.gridy = 1;
        c.insets = new Insets(50, 0, 0, 0);
        east.add(sp, c);
        
        frame.addAsEastPanel(east);
        frame.validate();
        frame.pack();

        Page page = new Page();
        page.getComponents().add(simpleBackground);
        page.setBackgroundLabel(simpleBackground.getUID());

        for (Component co : frame.getAllComponents()) {
            Spacer s = (Spacer) co;
            s.updatePosition();
            page.getComponents().add(s.getComponent());
        }
        return page;
    }

    @Override
    protected Page makeOverrideCastPage() {
        PsychLayoutPanel frame = new PsychLayoutPanel();
        Label successTitle = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("OVERRIDE_CAST_TITLE", language));
        successTitle.setBold(true);
        successTitle.setCentered(true);
        successTitle.setSize(successTitle.execute(sizeVisitor));
        frame.addTitle(successTitle);
        frame.remove(frame.west);

        Label reviewInstructions = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("OVERRIDE_CAST_INSTRUCTIONS",
                        language), sizeVisitor);
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.VERTICAL;
        c.gridy = 1;
        c.gridx = 0;
        c.weightx = 1;
        c.weighty = 1;
        Spacer instspacer = new Spacer(reviewInstructions, frame.north);
        frame.north.add(instspacer, c);

        JPanel east = new JPanel();
        east.setLayout(new GridBagLayout());
        c = new GridBagConstraints();

        Button confirmBtn = new Button(getNextLayoutUID(),
                LiteralStrings.Singleton.get("OVERRIDE_CAST_CONFIRM",
                        language), "OverrideCastConfirm");
        confirmBtn.setIncreasedFontSize(true);
        confirmBtn.setSize(confirmBtn.execute(sizeVisitor));
        Spacer sp = new Spacer(confirmBtn, east);
        east.add(sp, c);
        
        Button denyBtn = new Button(getNextLayoutUID(),
                LiteralStrings.Singleton.get("OVERRIDE_DENY",
                        language), "OverrideCastDeny");
        denyBtn.setIncreasedFontSize(true);
        denyBtn.setSize(denyBtn.execute(sizeVisitor));

        reviewInstructions.setNext(confirmBtn);
        reviewInstructions.setDown(confirmBtn);
        confirmBtn.setPrevious(reviewInstructions);
        confirmBtn.setUp(reviewInstructions);
        confirmBtn.setNext(denyBtn);
        confirmBtn.setDown(denyBtn);
        denyBtn.setPrevious(confirmBtn);
        denyBtn.setUp(confirmBtn);

        sp = new Spacer(denyBtn, east);
        c.gridy = 1;
        c.insets = new Insets(50, 0, 0, 0);
        east.add(sp, c);
        
        frame.addAsEastPanel(east);
        frame.validate();
        frame.pack();

        Page page = new Page();
        page.getComponents().add(simpleBackground);
        page.setBackgroundLabel(simpleBackground.getUID());

        for (Component co : frame.getAllComponents()) {
            Spacer s = (Spacer) co;
            s.updatePosition();
            page.getComponents().add(s.getComponent());
        }
        return page;
    }

    @Override
    protected ArrayList<Page> makeReviewPage(Ballot ballot, HashMap<Integer, Integer> pageTargets) {

    	ArrayList<Page> reviewPages = new ArrayList<>();
    	int position = 0; //the current position in the list of race review things

    	//inline modification of the amount of review screens
    	int numReviewPages = 1; //starting value; increases if there is a need.
    	for ( int reviewPageNum = 0; reviewPageNum < numReviewPages; reviewPageNum++) {
    		//set up review frame
    		PsychLayoutPanel frame = new PsychLayoutPanel();
    		Label reviewTitle = new Label(getNextLayoutUID(), LiteralStrings.Singleton
    				.get("REVIEW_TITLE", language));
    		reviewTitle.setBold(true);
    		reviewTitle.setCentered(true);
    		reviewTitle.setSize(reviewTitle.execute(sizeVisitor));
    		frame.addTitle(reviewTitle);
    		frame.addSideBar(3);
    		
    		if (reviewPageNum == 0) // first page
    			frame.addPreviousButton(new Label(getNextLayoutUID(),
    	                LiteralStrings.Singleton.get("BACK_LAST_RACE", language),
    	                sizeVisitor));
    		else
    			frame.addPreviousButton(new Label(getNextLayoutUID(), LiteralStrings.Singleton
    					.get("PREVIOUS_PAGE_BUTTON", language), sizeVisitor));
    		if (position == ballot.getCards().size() - 1) //last page, see bottom conditional
    			frame.addNextButton(new Label(getNextLayoutUID(),
    	                LiteralStrings.Singleton.get("FORWARD_RECORD", language),
    	                sizeVisitor));
    		else
    			frame.addNextButton(new Label(getNextLayoutUID(), LiteralStrings.Singleton
    					.get("NEXT_PAGE_BUTTON", language), sizeVisitor));

            Label reviewInstructions = new Label(getNextLayoutUID(), LiteralStrings.Singleton.get("REVIEW_INSTRUCTIONS", language), sizeVisitor);

    		//add content as east pane
    		JPanel east = new JPanel();
    		east.setLayout(new GridBagLayout());
    		GridBagConstraints c = new GridBagConstraints();
    		east.setLayout(new GridBagLayout());
    		c.gridx = 0;
    		c.gridy = 0;
    		c.anchor = GridBagConstraints.FIRST_LINE_START;
    		c.fill = GridBagConstraints.HORIZONTAL;
    		int align = 0;

    		//current review button number
    		int cnt = 0; 

    		int columnLength = (int)Math.ceil(ballot.getCards().size() / REVIEW_SCREEN_NUM_COLUMNS);

            //Represents the button to the left of this one
            ALayoutComponent tempButton = null;

            //Represents the button above this one
            ALayoutComponent temp2Button = null;

    		for (int i = position; i < ballot.getCards().size(); i++) {

    			ACard card = ballot.getCards().get(i);

    			ReviewButton rl = new ReviewButton(getNextLayoutUID(), card.getReviewTitle(language), "GoToPage", sizeVisitor);
    			rl.setBold(true);
    			rl.setBoxed(true);
    			rl.setWidth(REVIEW_SCREEN_WIDTH);
    			rl.setPageNum(pageTargets.get(position));


    			ReviewButton rb = new ReviewButton(card.getUID(), card.getReviewBlankText(language), "GoToPage", sizeVisitor);

    			rb.setBoxed(true);
    			rb.setWidth(REVIEW_SCREEN_WIDTH);
    			rb.setPageNum(pageTargets.get(position));

                if(temp2Button == null){
                    reviewInstructions.setNext(rl);
                    reviewInstructions.setRight(rl);
                    reviewInstructions.setDown(rl);
                    rl.setPrevious(reviewInstructions);
                    rl.setUp(reviewInstructions);
                    rl.setLeft(reviewInstructions);
                    rb.setUp(reviewInstructions);
                } else{
                    rl.setPrevious(temp2Button);
                    rl.setUp(tempButton);
                    rb.setUp(temp2Button);
                    tempButton.setDown(rl);
                    temp2Button.setDown(rb);
                    temp2Button.setNext(rl);
                }
                rl.setNext(rb);
                rl.setRight(rb);
                rl.setLeft(previousButton);
                rb.setLeft(previousButton);
                rb.setPrevious(rl);
                rb.setLeft(rl);
                rb.setRight(    nextButton);


    			Spacer rlSpacer = new Spacer(rl, east);
    			c.gridx = align;
    			east.add(rlSpacer, c);

    			Spacer rbSpacer = new Spacer(rb, east);
    			c.gridx = c.gridx + 1;
    			east.add(rbSpacer, c);

    			cnt++;
    			c.gridy++;

    			if (cnt > columnLength) {
    				cnt = 0;
    				align++;
    				c.gridy = 0;
    				c.anchor = GridBagConstraints.FIRST_LINE_END;
    			}
    			position++;
    			if (i % CARDS_PER_REVIEW_PAGE >= CARDS_PER_REVIEW_PAGE - 1) //number of races to put on each card
    				break;

                tempButton = rl;
                temp2Button = rb;

    		}

            previousButton.setUp(reviewInstructions);
            previousButton.setNext(reviewInstructions);
            reviewInstructions.setPrevious(previousButton);
            reviewInstructions.setLeft(previousButton);
            reviewInstructions.setRight(nextButton);
            nextButton.setPrevious(temp2Button);
            nextButton.setLeft(temp2Button);
            nextButton.setUp(temp2Button);

            temp2Button.setNext(nextButton);
            temp2Button.setDown(nextButton);
            tempButton.setDown(nextButton);

    		frame.addAsEastPanel(east);

    		//add instructions
    		GridBagConstraints constraints = new GridBagConstraints();
    		constraints.anchor = GridBagConstraints.NORTH;
    		constraints.fill = GridBagConstraints.VERTICAL;
    		constraints.gridy = 1;
    		constraints.gridx = 0;
    		constraints.weightx = 1;
    		constraints.weighty = 1;
    		Spacer instspacer = new Spacer(reviewInstructions, frame.north);
    		frame.north.add(instspacer, constraints);

    		frame.validate();
    		frame.pack();

    		//add to a Page
    		Page cardPage = new Page();
    		cardPage.getComponents().add(background);
    		cardPage.setBackgroundLabel(background.getUID());

//            previousButton = returnButton;
//            nextButton = returnButton;

            // This variable is used to shift down all the race labels that come after a presidential election label.
            //int yShift = 0;
            //int currentIndex = 0;
            Component[] componentsArray = frame.getAllComponents();
    		for (Component cmp : componentsArray) {
    			int componentHeight = cmp.getHeight();
                Spacer s = (Spacer) cmp;
                //System.out.println("UID: " + s.getComponent().getUID() + ". Height: " + componentHeight + " | Width: " + cmp.getWidth());
                s.updatePosition();
                if (componentHeight == PRESIDENTIAL_RACE_LABEL_COMPONENT_HEIGHT) // This detects either a presidential race label or a presidential race selection.
                {
                    // Use the old shift length.
                    s.getComponent().setYPos(s.getComponent().getYPos()/* + yShift*/);
                    /*if (s.getComponent().getUID().contains("B"))  // This uniquely detects presidential race selections. They always follow a label, so the latter of the two should set the yShift.
                    {
                        //System.out.println("UID " + s.getComponent().getUID() + " is a presidential election. Updating yShift from " + yShift + " to " + (yShift + PRESIDENTIAL_RACE_SHIFT_HEIGHT));
                        // Update the shift length.
                        yShift += PRESIDENTIAL_RACE_SHIFT_HEIGHT;
                    }*/
                    cardPage.getComponents().add(s.getComponent());
                    //currentIndex++;
                    continue;
                }
                /**
                 * Shift everything down except the button labels.
                 * They are descriptions of the buttons on the current page and they should remain where they are.
                 * Normally, the review page card would contain components that have UIDs that alternate between L and B.
                 * The only two exceptions are the button labels:
                 *      The first button label comes after a B but before an L.
                 *      The second button label comes after an L but before an L.
                 *      No other component meets these conditions.
                 */

                    cardPage.getComponents().add(s.getComponent());


            }
    		reviewPages.add(cardPage);
    		if (position < ballot.getCards().size())
    			numReviewPages++;
    	}
    	
    	return reviewPages;
    }

    /**
     * Makes a simple background (without the sidebar). Used on override pages
     * and the success page.
     * @return the success page background
     */
    protected Background makeSimpleBackground() {
        PsychLayoutPanel frame = new PsychLayoutPanel();

        Label instructionsTitle = new Label("L0", LiteralStrings.Singleton.get(
                "INSTRUCTIONS_TITLE", language));
        instructionsTitle.setCentered(true);
        instructionsTitle.setSize(instructionsTitle.execute(sizeVisitor));
        frame.addTitle(instructionsTitle);
        frame.addSideBar(1);
        frame.addNextButton(nextInfo);

        JPanel east = new JPanel();
        east.setLayout(new GridBagLayout());
        Label instrLabel = new Label(getNextLayoutUID(), LiteralStrings.Singleton.get(
                "INSTRUCTIONS", language), sizeVisitor);

        nextButton.setPrevious(instrLabel);
        nextButton.setLeft(instrLabel);
        instrLabel.setNext(nextButton);
        instrLabel.setRight(nextButton);

        Spacer sp = new Spacer(instrLabel, east);
        east.add(sp);
        frame.addAsEastPanel(east);

        frame.validate();
        frame.pack();

        BufferedImage image = new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphs = (Graphics2D) image.getGraphics();
        graphs.setColor(Color.WHITE);
        graphs.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        graphs.setColor(Color.PINK);
        graphs.fillRect(0, 0, WINDOW_WIDTH, frame.north.getHeight());
        return new Background(getNextLayoutUID(), image);
    }

    //#ifdef NONE_OF_ABOVE
//    protected Page makeNoSelectionPage(int target) {
//        PsychLayoutPanel frame = new PsychLayoutPanel();
//          successTitle = new  (getNextLayoutUID(),
//                LiteralStrings.Singleton.get("NO_SELECTION_TITLE", language));
//        successTitle.setBold(true);
//        successTitle.setCentered(true);
//        successTitle.setSize(successTitle.execute(sizeVisitor));
//        frame.addTitle(successTitle);
//        frame.remove(frame.west);
//
//        JPanel east = new JPanel();
//        east.setLayout(new GridBagLayout());
//          instrLabel = new  (getNextLayoutUID(),
//                LiteralStrings.Singleton.get("NO_SELECTION", language), sizeVisitor);
//        Spacer sp = new Spacer(instrLabel, east);
//        east.add(sp);
//        frame.addAsEastPanel(east);
//
//          returnLbl = new  (getNextLayoutUID(), LiteralStrings.Singleton
//                .get("RETURN_RACE", language), sizeVisitor);
//        frame.addReturnButton(returnLbl, target);
//
//        frame.validate();
//        frame.pack();
//
//        Page page = new Page();
//        page.getComponents().add(simpleBackground);
//        page.setBackgroundLabel(simpleBackground.getUID());
//
//        ALayoutComponent button = null;
//        ALayoutComponent tempButton = null;
//
//        for (Component c : frame.getAllComponents()) {
//            Spacer s = (Spacer) c;
//            s.updatePosition();
//            page.getComponents().add(s.getComponent());
//            button = s.getComponent();
//
//            if(button instanceof ToggleButton){
//                if(tempButton == null){
//                    button.setPrevious(previousButton);
//                    previousButton.setNext(button);
//
//                }else{
//                    button.setPrevious(tempButton);
//                    tempButton.setNext(button);
//                }
//
//                tempButton = button;
//
//            }
//        }
//
//        //If the temporary button is still null at this point that means the
//        //page contains no ToggleButtons
//        if(tempButton != null){
//            tempButton.setNext(nextButton);
//            nextButton.setPrevious(tempButton);
//        }
//
//        return page;
//    }
    //#endif

    @Override
    protected Page makeSuccessPage() {
        PsychLayoutPanel frame = new PsychLayoutPanel();
        Label successTitle = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("SUCCESS_TITLE", language));
        successTitle.setBold(true);
        successTitle.setCentered(true);
        successTitle.setSize(successTitle.execute(sizeVisitor));
        frame.addTitle(successTitle);
        frame.remove(frame.west);

        JPanel east = new JPanel();
        east.setLayout(new GridBagLayout());
        Label instrLabel = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("SUCCESS", language), sizeVisitor);
        Spacer sp = new Spacer(instrLabel, east);
        east.add(sp);
        frame.addAsEastPanel(east);

        frame.validate();
        frame.pack();

        Page page = new Page();
        page.getComponents().add(simpleBackground);
        page.setBackgroundLabel(simpleBackground.getUID());

        ALayoutComponent button = null;
        ALayoutComponent tempButton = null;
//
//        previousButton.setNext(instrLabel);
//        previousButton.setLeft(instrLabel);
//        instrLabel.setPrevious(previousButton);
//        instrLabel.setLeft(previousButton);
//        instrLabel.setLeft(nextButton);

        for (Component c : frame.getAllComponents()) {
            Spacer s = (Spacer) c;
            s.updatePosition();
            page.getComponents().add(s.getComponent());
//            button = s.getComponent();

//            if(button instanceof ToggleButton){
//                if(tempButton == null){
//                    button.setPrevious(instrLabel);
//                    instrLabel.setNext(button);
//
//                }else{
//                    button.setPrevious(tempButton);
//                    tempButton.setNext(button);
//                }
//
//                tempButton = button;
//
//            }
        }

        //If the temporary button is still null at this point that means the
        //page contains no ToggleButtons
//        if(tempButton != null){
//            tempButton.setNext(nextButton);
//            nextButton.setPrevious(instrLabel);
//        }

        return page;
    }
    
    private Page makeResponsePage() {
        PsychLayoutPanel frame = new PsychLayoutPanel();
        Label responseTitle = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("RESPONSE_TITLE", language));
        responseTitle.setBold(true);
        responseTitle.setCentered(true);
        responseTitle.setSize(responseTitle.execute(sizeVisitor));
        frame.addTitle(responseTitle);
        frame.remove(frame.west);

        JPanel east = new JPanel();
        east.setLayout(new GridBagLayout());
        Label instrLabel = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("RESPONSE", language), sizeVisitor);
        Spacer sp = new Spacer(instrLabel, east);
        east.add(sp);
        frame.addAsEastPanel(east);

        frame.validate();
        frame.pack();

        Page page = new Page();
        page.getComponents().add(simpleBackground);
        page.setBackgroundLabel(simpleBackground.getUID());

//        ALayoutComponent button = null;
//        ALayoutComponent tempButton = null;
//
//        previousButton.setNext(instrLabel);
//        instrLabel.setPrevious(previousButton);


        for (Component c : frame.getAllComponents()) {
            Spacer s = (Spacer) c;
            s.updatePosition();
            page.getComponents().add(s.getComponent());
//            button = s.getComponent();
//
//            if(button instanceof ToggleButton){
//                if(tempButton == null){
//                    button.setPrevious(instrLabel);
//                    instrLabel.setNext(button);
//
//                }else{
//                    button.setPrevious(tempButton);
//                    tempButton.setNext(button);
//                }
//
//                tempButton = button;
//
//            }
        }

        //If the temporary button is still null at this point that means the
        //page contains no ToggleButtons
//        if(tempButton != null){
//            tempButton.setNext(nextButton);
//            nextButton.setPrevious(tempButton);
//        }

        return page;
    }

    protected Page makeProvisionalSuccessPage() {
        PsychLayoutPanel frame = new PsychLayoutPanel();
        Label successTitle = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("SUCCESS_TITLE", language));
        successTitle.setBold(true);
        successTitle.setCentered(true);
        successTitle.setSize(successTitle.execute(sizeVisitor));
        frame.addTitle(successTitle);
        frame.remove(frame.west);

        JPanel east = new JPanel();
        east.setLayout(new GridBagLayout());
        Label instrLabel = new Label(getNextLayoutUID(),
                LiteralStrings.Singleton.get("PROVISIONAL", language), sizeVisitor);
        Spacer sp = new Spacer(instrLabel, east);
        east.add(sp);
        frame.addAsEastPanel(east);

        frame.validate();
        frame.pack();

        Page page = new Page();
        page.getComponents().add(simpleBackground);
        page.setBackgroundLabel(simpleBackground.getUID());

//        ALayoutComponent button = null;
//        ALayoutComponent tempButton = null;

//        previousButton.setNext(instrLabel);
//        instrLabel.setPrevious(previousButton);

        for (Component c : frame.getAllComponents()) {
            Spacer s = (Spacer) c;
            s.updatePosition();
            page.getComponents().add(s.getComponent());
//            button = s.getComponent();

//            if(button instanceof ToggleButton){
//                if(tempButton == null){
//                    button.setPrevious(instrLabel);
//                    instrLabel.setNext(button);
//
//                }else{
//                    button.setPrevious(tempButton);
//                    tempButton.setNext(button);
//                }
//
//                tempButton = button;
//
//            }
        }

        //If the temporary button is still null at this point that means the
        //page contains no ToggleButtons
//        if(tempButton != null){
//            tempButton.setNext(nextButton);
//            nextButton.setPrevious(tempButton);
//        }

        return page;
    }

}
