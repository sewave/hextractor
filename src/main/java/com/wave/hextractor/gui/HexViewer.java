package com.wave.hextractor.gui;

import com.wave.hextractor.object.FileDrop;
import com.wave.hextractor.object.HexTable;
import com.wave.hextractor.pojo.OffsetEntry;
import com.wave.hextractor.pojo.TableSearchResult;
import com.wave.hextractor.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gui for the hextractor tools.
 * @author slcantero
 */
public class HexViewer extends JFrame implements ActionListener {

    private static final Logger LOGGER = Logger.getLogger(HexViewer.class.getName());

    /**  serialVersion UID. */
    private static final long serialVersionUID = -4438721009010549343L;

    /** The Constant DEFAULT_SPLIT_VALUE. */
    private static final int DEFAULT_SPLIT_VALUE = 1024;

    /** The Constant NEW_PROJECT_NAME_MIN_LENGTH. */
    private static final int NEW_PROJECT_NAME_MIN_LENGTH = 4;

    /** The Constant NEW_PROJECT_GRID_ROWS. */
    private static final int NEW_PROJECT_GRID_ROWS = 4;

    /** The Constant NEW_PROJECT_GRID_COLS. */
    private static final int NEW_PROJECT_GRID_COLS = 3;

    /** The Constant NEW_PROJECT_GRID_HGAP. */
    private static final int NEW_PROJECT_GRID_HGAP = 10;

    /** The Constant NEW_PROJECT_GRID_VGAP. */
    private static final int NEW_PROJECT_GRID_VGAP = 10;

    /** The Constant SEARCH_ALL_GRID_ROWS. */
    private static final int SEARCH_ALL_GRID_ROWS = 4;

    /** The Constant SEARCH_ALL_GRID_COLS. */
    private static final int SEARCH_ALL_GRID_COLS = 2;

    /** The Constant SEARCH_ALL_GRID_HGAP. */
    private static final int SEARCH_ALL_GRID_HGAP = 10;

    /** The Constant SEARCH_ALL_GRID_VGAP. */
    private static final int SEARCH_ALL_GRID_VGAP = 10;

    /** The Constant SEARCH_ALL_END_CHARS_LENGTH. */
    private static final int SEARCH_ALL_END_CHARS_LENGTH = 40;

    /** The Constant MAX_COLS_AND_ROWS. */
    private static final int MAX_COLS_AND_ROWS = 32;

    /** The Constant MIN_COLS_AND_ROWS. */
    private static final int MIN_COLS_AND_ROWS = 16;

    /** The Constant HEX_STARTS. */
    private static final String HEX_STARTS = "0x";

    /** The Constant DEC_STARTS. */
    private static final String DEC_STARTS = "d";

    /** The Constant DEFAULT_TABLE. */
    private static final String DEFAULT_TABLE = "ascii.tbl";

    /** The Constant DEFAULT_HEXFILE. */
    private static final String DEFAULT_HEXFILE = "empty.hex";

    /** The Constant EXTENSION_TABLE. */
    private static final String EXTENSION_TABLE = ".tbl";

    /** The Constant EXTENSION_OFFSET. */
    private static final String EXTENSION_OFFSET = ".off";

    /** The Constant EXTENSION_EXTRACTION. */
    private static final String EXTENSION_EXTRACTION = ".ext";

    /** The Constant SEARCH_RES_DIMENSION. */
    private static final Dimension SEARCH_RES_DIMENSION = new Dimension(600, 200);

    /** The Constant SEARCHRES_FONT_SIZE. */
    private static final int SEARCHRES_FONT_SIZE =  18;

    /** The Constant REGEXP_OFFSET_ENTRIES. */
    private static final String REGEXP_OFFSET_ENTRIES = "[0-9A-Fa-f]{2}(-[0-9A-Fa-f]{2})*";

    /** The Constant DIMENSION_0_0. */
    private static final Dimension DIMENSION_0_0 = new Dimension(0,0);

    /** The blue painter. */
    private static final HighlightPainter BLUE_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(Color.BLUE);

    /** The light gray painter. */
    private static final HighlightPainter LGRAY_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY);

    /** The yellow painter. */
    private static final HighlightPainter YELLOW_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);

    /** The orange painter. */
    private static final HighlightPainter ORANGE_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(Color.ORANGE);

    /** The Constant SEARCH_ALL_DEFAULT_CHARS_INDEX. */
    private static final int SEARCH_ALL_DEFAULT_CHARS_INDEX = 4;

    /** The Constant SEARCH_ALL_DEFAULT_END_CHARS. */
    private static final String SEARCH_ALL_DEFAULT_END_CHARS = "FF";

    /** The Constant SEARCH_ALL_MIN_PROGRESS. */
    private static final int SEARCH_ALL_MIN_PROGRESS = 0;

    /** The Constant SEARCH_ALL_MAX_PROGRESS. */
    private static final int SEARCH_ALL_MAX_PROGRESS = 100;

    /** The Constant OFFSET_LABEL_LENGTH. */
    private static final int OFFSET_LABEL_LENGTH = 50;

    /** The other entry. */
    private SimpleEntry<String, String> otherEntry;

    /** The smd entry. */
    private SimpleEntry<String, String> smdEntry;

    /** The snes entry. */
    private SimpleEntry<String, String> snesEntry;

    /** The gb entry. */
    private SimpleEntry<String, String> gbEntry;

    /** The tap entry. */
    private SimpleEntry<String, String> tapEntry;

    /** The tzx entry. */
    private SimpleEntry<String, String> tzxEntry;

    /** The sms entry. */
    private SimpleEntry<String, String> smsEntry;

    /** The file bytes. */
    private byte[] fileBytes;

    /** The hex table. */
    private HexTable hexTable;

    /** The offset. */
    private int offset = 0;

    /** The hex file. */
    private File hexFile;

    /** The table file. */
    private File tableFile;

    /** The offset file. */
    private File offsetFile;

    /** The off entries. */
    private List<OffsetEntry> offEntries;

    /** The curr entry. */
    private OffsetEntry currEntry;

    /** The last selected end chars. */
    private String lastSelectedEndChars = "FF";

    /** The project file. */
    private File projectFile;

    /** The offset label value. */
    private JTextField offsetLabelValue;

    /** The open table. */
    private JMenuItem openTable;

    /** The save table. */
    private JMenuItem saveTable;

    /** The reload table. */
    private JMenuItem reloadTable;

    /** The open offsets. */
    private JMenuItem openOffsets;

    /** The save offsets. */
    private JMenuItem saveOffsets;

    /** The next offset. */
    private JMenuItem nextOffset;

    /** The prev offset. */
    private JMenuItem prevOffset;

    /** The open file. */
    private JMenuItem openFile;

    /** The new project. */
    private JMenuItem newProject;

    /** The exit. */
    private JMenuItem exit;

    /** The about menu item. */
    private JMenuItem about;

    /** The help item. */
    private JMenuItem help;

    /** The go to. */
    private JMenuItem goTo;

    /** The search relative. */
    private JMenuItem searchRelative;

    /** The search all. */
    private JMenuItem searchAll;

    /** The extract. */
    private JMenuItem extract;

    /** The clear offsets. */
    private JMenuItem clearOffsets;

    /** The find. */
    private JMenuItem find;

    /** The rb. */
    private transient ResourceBundle rb;

    /** The hex text area. */
    private JTextArea hexTextArea;

    /** The ascii text area. */
    private JTextArea asciiTextArea;

    /** The offsets text area. */
    private JTextArea offsetsTextArea;

    /** The vsb. */
    private JScrollBar vsb;

    /** The table filter. */
    private SimpleFilter tableFilter;

    /** The offset file filter. */
    private SimpleFilter offsetFileFilter;

    /** The offset only file filter. */
    private SimpleFilter offsetOnlyFileFilter;

    /** The ext only file filter. */
    private SimpleFilter extOnlyFileFilter;

    /** The results window. */
    private JFrame resultsWindow;

    /** The search results. */
    private JList<TableSearchResult> searchResults;

    /** The new project window. */
    private JFrame newPrjWin;

    /** The new project window name input. */
    private JTextField newPrjWinNameInput;

    /** The new project window file input. */
    private JTextField newPrjWinFileInput;

    /** The new project window file type options. */
    private JComboBox<Entry<String, String>> newPrjWinFileTypeOpt;

    /** The new project window search file button. */
    private JButton newPrjWinSearchFileButton;

    /** The new project window create button. */
    private JButton newPrjWinCreateButton;

    /** The new project window cancel button. */
    private JButton newPrjWinCancelButton;

    /** The Constant OFFSET_UNIT. */
    private int visibleColumns = MAX_COLS_AND_ROWS;

    /** The visible rows. */
    private int visibleRows = MAX_COLS_AND_ROWS;

    /** The view 16 cols menu item. */
    private JCheckBoxMenuItem view16Cols;

    /** The view 16 rows menu item. */
    private JCheckBoxMenuItem view16Rows;

    /** The search all strings window. */
    private JFrame searchAllStringsWin;

    /** The search all win skip chars opt. */
    private JComboBox<Integer> searchAllWinSkipCharsOpt;

    /** The search all win end chars input. */
    private JTextField searchAllWinEndCharsInput;

    /** The search all win search button. */
    private JButton searchAllWinSearchButton;

    /** The search all win cancel button. */
    private JButton searchAllWinCancelButton;

    /** The search all win progress bar. */
    private JProgressBar searchAllWinProgressBar;

    /** The search all thread. */
    private transient Thread searchAllThread = null;

    /** The search all thread error. */
    private boolean searchAllThreadError = false;

    /** The BASE_FONT for the application. */
    private static final Font BASE_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 13);

    /** The SEARCH_JOKER_EXPANSIONS for searches. */
    private static final int SEARCH_JOKER_EXPANSIONS = 8;

    /** The Constant ICON16. */
    private static final URL ICON16 = HexViewer.class.getResource("/icon/rom16.png");

    /** The Constant ICON32. */
    private static final URL ICON32 = HexViewer.class.getResource("/icon/rom32.png");

    /** The Constant ICON96. */
    private static final URL ICON96 = HexViewer.class.getResource("/icon/rom96.png");

    private final transient Object searchAllLock = new Object();

    /**
     * Gets the offset block.
     *
     * @return the offset block
     */
    private int getOffsetBlock() {
        return visibleColumns * visibleRows;
    }

    /**
     * Gets the view size.
     *
     * @return the view size
     */
    private int getViewSize() {
        return visibleColumns * visibleRows;
    }

    /**
     * Instantiates a new hex viewer.
     *
     * @param fileBytes the file bytes
     * @param fileName the file name
     * @param hexTable the hex table
     * @param tableName the table name
     */
    private HexViewer(byte[] fileBytes, String fileName, HexTable hexTable, String tableName) {
        this.hexFile = new File(fileName);
        this.fileBytes = fileBytes;
        this.hexTable = hexTable;
        String projectName = ProjectUtils.getProjectName();
        String offsetFileName = Constants.EMPTY_OFFSET_FILE;
        if(projectName != null && !Constants.EMPTY.equals(projectName)) {
            if(DEFAULT_TABLE.equals(tableName)) {
                tableName = projectName + EXTENSION_TABLE;
            }
            offsetFileName = projectName + EXTENSION_OFFSET;
        }
        this.tableFile = new File(tableName);
        this.offsetFile = new File(offsetFileName);
        createFrame();
    }

    /**
     * Refresh selection.
     */
    private void refreshSelection() {
        hexTextArea.setCaretPosition(asciiTextArea.getCaretPosition() * Constants.HEX_VALUE_SIZE);
        Highlighter highlighterHex = hexTextArea.getHighlighter();
        highlighterHex.removeAllHighlights();
        try {
            highlighterHex.addHighlight(hexTextArea.getCaretPosition(),
                    hexTextArea.getCaretPosition() + Constants.HEX_VALUE_SIZE - 1, BLUE_PAINTER);
        } catch (BadLocationException e1) {
            //Do nothing
        }
        Highlighter highlighterAscii = asciiTextArea.getHighlighter();
        highlighterAscii.removeAllHighlights();
        try {
            highlighterAscii.addHighlight(asciiTextArea.getCaretPosition(), asciiTextArea.getCaretPosition() + 1,
                    BLUE_PAINTER);
            for (OffsetEntry entry : offEntries) {
                drawOffsetEntry(entry, highlighterAscii, LGRAY_PAINTER, ORANGE_PAINTER);
            }
            if (currEntry.getStart() > 0 && currEntry.getStart() - offset >= 0) {
                highlighterAscii.addHighlight(currEntry.getStart() - offset, currEntry.getStart() - offset + 1,
                        YELLOW_PAINTER);
            }
            if (currEntry.getEnd() > 0 && currEntry.getEnd() - offset >= 0) {
                highlighterAscii.addHighlight(currEntry.getEnd() - offset, currEntry.getEnd() - offset + 1,
                        ORANGE_PAINTER);
            }
        } catch (BadLocationException e1) {
            Utils.log("Bad location.");
        }
        offsetLabelValue.setText(getOffsetLabelValue());
    }

    private String getOffsetLabelValue() {
        int currPos = offset + asciiTextArea.getCaretPosition();
        int size = fileBytes.length - 1;
        int lengthDec = String.valueOf(size).length();
        int lengthHex = Integer.toHexString(size).length();
        String strFormat = "%0" + lengthDec + "d";
        return "0x" + Utils.intToHexString(currPos, lengthHex) + " (" + String.format(strFormat, currPos)
                + ") / 0x" + Utils.intToHexString(size, lengthHex) + " (" + String.format(strFormat, size)
                + ") - (" + String.format("%03.2f", (100f * currPos) / size) + "% )";
    }

    /**
     * Draw offset entry.
     *
     * @param entry the entry
     * @param highlighter the highlighter
     * @param painter the painter
     * @param borderPainter the border painter
     * @throws BadLocationException the bad location exception
     */
    private void drawOffsetEntry(OffsetEntry entry, Highlighter highlighter,
                                 HighlightPainter painter, HighlightPainter borderPainter) throws BadLocationException {
        if (entry.getStart() <= offset + getViewSize() && entry.getEnd() >= offset) {
            int start = entry.getStart();
            int end = entry.getEnd();
            if (start < offset) {
                start = offset;
            }
            if (end >= offset + getViewSize()) {
                end = offset + getViewSize() - 1;
            }
            if(entry.getStart() >= offset) {
                highlighter.addHighlight(start - offset, start - offset + 1, borderPainter);
            }
            else {
                start--;
            }
            highlighter.addHighlight(start - offset + 1, end - offset, painter);
            highlighter.addHighlight(end - offset, end - offset + 1, borderPainter);
        }
    }

    /**
     * The Class PopUpOffsetEntry.
     */
    class PopUpOffsetEntry extends JPopupMenu {

        /**  serialVersionUID. */
        private static final long serialVersionUID = 8840279664255620962L;

        /** The start item. */
        JMenuItem startItem;

        /** The end item. */
        JMenuItem endItem;

        /** The delete item. */
        JMenuItem deleteItem;

        /** The split item. */
        JMenuItem splitItem;

        /** The selected entry. */
        OffsetEntry selectedEntry;

        /**
         * Instantiates a new pop up offset entry.
         *
         * @param entry the entry
         */
        PopUpOffsetEntry(OffsetEntry entry){
            selectedEntry = entry;
            startItem = new JMenuItem();
            startItem.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_OFFSET_SET_START)) {
                /**  serialVersion UID. */
                private static final long serialVersionUID = 251417879942401217L;
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectedEntry.setStart(offset + asciiTextArea.getCaretPosition());
                    refreshAll();
                }
            });
            add(startItem);
            if(currEntry.getStart() > 0 || selectedEntry != currEntry) {
                endItem = new JMenuItem();
                endItem.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_OFFSET_SET_END)) {
                    /**  serialVersion UID. */
                    private static final long serialVersionUID = 251427879942401217L;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        endItemAction(selectedEntry);
                        refreshAll();
                    }
                });
                add(endItem);
            }
            //Hemos caido en una entry existente, tendra opcion de borrar o hacer split
            if(currEntry != selectedEntry) {
                splitItem = new JMenuItem();
                splitItem.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_OFFSET_SPLIT)) {
                    private static final long serialVersionUID = 251427879942401214L;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        splitItemAction(selectedEntry);
                    }
                });
                add(splitItem);
                deleteItem = new JMenuItem();
                deleteItem.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_OFFSET_DELETE)) {
                    private static final long serialVersionUID = 251427879942401219L;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        deleteItemAction(selectedEntry);
                    }
                });
                add(deleteItem);
            }
        }
    }

    /**
     * Delete item action.
     *
     * @param selectedEntry the selected entry
     */
    private void deleteItemAction(OffsetEntry selectedEntry) {
        if(GuiUtils.confirmActionAlert(rb.getString(KeyConstants.KEY_CONFIRM_RANGE_DELETE_TITLE),
                rb.getString(KeyConstants.KEY_CONFIRM_RANGE_DELETE) + selectedEntry.toEntryString())){
            offEntries.remove(selectedEntry);
            refreshAll();
        }
    }

    /**
     * Split item action.
     *
     * @param selectedEntry the selected entry
     */
    private void splitItemAction(OffsetEntry selectedEntry) {
        int minMaxLength = 0;
        String valor = JOptionPane.showInputDialog(rb.getString(KeyConstants.KEY_OFFSET_SPLIT), DEFAULT_SPLIT_VALUE);
        if(valor != null) {
            try {
                minMaxLength = Integer.parseInt(valor);
            } catch (NumberFormatException e1) {
                JOptionPane.showMessageDialog(help, rb.getString(KeyConstants.KEY_OFFSET_SPLIT_CANCEL),
                        rb.getString(KeyConstants.KEY_OFFSET_SPLIT_CANCEL_TITLE), JOptionPane.INFORMATION_MESSAGE);
            }
        }
        if(minMaxLength > 0) {
            offEntries.remove(selectedEntry);
            offEntries.addAll(selectedEntry.split(minMaxLength, fileBytes));
            refreshAll();
        }
    }

    /**
     * End item action.
     *
     * @param selectedEntry the selected entry
     */
    private void endItemAction(OffsetEntry selectedEntry) {
        asciiTextArea.setSelectionEnd(asciiTextArea.getCaretPosition());
        String result = JOptionPane.showInputDialog(rb.getString(KeyConstants.KEY_INPUT_ENDCHARS), lastSelectedEndChars);
        if(result != null && result.length() > 0) {
            if(result.matches(REGEXP_OFFSET_ENTRIES)) {
                lastSelectedEndChars = result;
                selectedEntry.setEndChars(Arrays.asList(
                        lastSelectedEndChars.toUpperCase().replace(Constants.SPACE_STR, Constants.EMPTY)
                                .split(Constants.OFFSET_CHAR_SEPARATOR)));
                selectedEntry.setEnd(offset + asciiTextArea.getCaretPosition());
                if((selectedEntry.getStart() > 0 || selectedEntry.getEnd() > 0) && selectedEntry == currEntry) {
                    currEntry.mergeInto(offEntries);
                    currEntry = new OffsetEntry();
                    currEntry.setStart(-1);
                    currEntry.setEnd(-1);
                }
            }
            else {
                JOptionPane.showMessageDialog(asciiTextArea.getParent(),
                        rb.getString(KeyConstants.KEY_ALERT_INVALID_ENDCHARS),
                        rb.getString(KeyConstants.KEY_ALERT_INVALID_ENDCHARS_TITLE), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Gets the caret entry.
     *
     * @return the caret entry
     */
    private OffsetEntry getCaretEntry() {
        //By default our current is in range, is there anybody else?
        int caretPosition = asciiTextArea.getCaretPosition() + offset;
        OffsetEntry inRange = currEntry;
        for(OffsetEntry entry : offEntries) {
            //1D collision, positions are inclusive
            if(caretPosition >= entry.getStart() && caretPosition <= entry.getEnd()) {
                inRange = entry;
            }
        }
        return inRange;
    }

    /**
     * Close app.
     */
    private void closeApp() {
        if(GuiUtils.confirmActionAlert(rb.getString(KeyConstants.KEY_TITLE),
                rb.getString(KeyConstants.KEY_CONFIRM_EXIT))) {
            System.exit(0);
        }
    }

    /**
     * Creates the frame.
     */
    private void createFrame() {
        setVisible(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLookAndFeel();
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        createMenu();
        JPanel firstRow = new JPanel();
        firstRow.setLayout(new FlowLayout());
        JPanel secondRow = new JPanel();
        secondRow.setLayout(new FlowLayout(FlowLayout.LEADING));
        add(firstRow);
        add(secondRow);
        offsetsTextArea = new JTextArea(visibleRows, Constants.HEX_ADDR_SIZE + HEX_STARTS.length() + Constants.SPACE_STR.length());
        offsetsTextArea.setPreferredSize(DIMENSION_0_0);
        offsetsTextArea.setLineWrap(true);
        offsetsTextArea.setBackground(Color.BLACK);
        offsetsTextArea.setForeground(Color.WHITE);
        offsetsTextArea.setEditable(false);
        offsetsTextArea.setDisabledTextColor(Color.WHITE);
        offsetsTextArea.setEnabled(false);
        offsetsTextArea.setFont(BASE_FONT);
        firstRow.add(offsetsTextArea);
        offsetsTextArea.setText(getVisibleOffsets(offset, visibleColumns, visibleRows));
        hexTextArea = new JTextArea(visibleRows, visibleColumns * Constants.HEX_VALUE_SIZE);
        hexTextArea.setLineWrap(true);
        hexTextArea.setBackground(Color.BLACK);
        hexTextArea.setForeground(Color.WHITE);
        hexTextArea.setEditable(false);
        hexTextArea.setDisabledTextColor(Color.WHITE);
        hexTextArea.setEnabled(false);
        hexTextArea.setCaretColor(Color.GRAY);
        hexTextArea.setFont(BASE_FONT);
        firstRow.add(hexTextArea);
        hexTextArea.setText(Utils.getHexAreaFixedWidth(offset, getViewSize(), fileBytes, visibleColumns));
        asciiTextArea = new JTextArea(visibleRows, visibleColumns);
        asciiTextArea.setLineWrap(true);
        asciiTextArea.setBackground(Color.BLACK);
        asciiTextArea.setForeground(Color.WHITE);
        asciiTextArea.setDisabledTextColor(Color.WHITE);
        asciiTextArea.setEnabled(false);
        asciiTextArea.setEditable(false);
        asciiTextArea.setCaretColor(Color.GRAY);
        asciiTextArea.setFont(BASE_FONT);
        firstRow.add(asciiTextArea);
        asciiTextArea.setText(Utils.getTextArea(offset, getViewSize(), fileBytes, hexTable));
        resultsWindow = new JFrame(rb.getString(KeyConstants.KEY_SEARCH_RESULT_TITLE));
        resultsWindow.setLayout(new FlowLayout());
        searchResults = new JList<>(new TableSearchResult[0]);
        searchResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchResults.setLayoutOrientation(JList.VERTICAL);
        searchResults.setVisibleRowCount(8);
        searchResults.setFont(new Font(Font.MONOSPACED, Font.PLAIN, SEARCHRES_FONT_SIZE));
        JScrollPane listScroller = new JScrollPane(searchResults);
        listScroller.setPreferredSize(SEARCH_RES_DIMENSION);
        resultsWindow.add(listScroller);
        resultsWindow.pack();
        resultsWindow.setResizable(Boolean.FALSE);
        createNewPrjWin();
        vsb = new JScrollBar();
        firstRow.add(vsb);
        JLabel offsetLabel = new JLabel(rb.getString(KeyConstants.KEY_OFFSET_LABEL));
        secondRow.add(offsetLabel);
        offsetLabelValue = new JTextField(getOffsetLabelValue(), OFFSET_LABEL_LENGTH);
        offsetLabelValue.setEnabled(false);
        secondRow.add(offsetLabelValue);
        offsetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        offsetLabelValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        offEntries = new ArrayList<>();
        currEntry = new OffsetEntry();
        currEntry.setStart(-1);
        currEntry.setEnd(-1);
        createSearchAllWin();
        setResizable(Boolean.FALSE);
        addAllListeners();
        pack();
        vsb.setPreferredSize(new Dimension((int) vsb.getPreferredSize().getWidth(), (int) (getSize().getHeight() * 0.85)));
        refreshViewMode();
        setIcons();
        setVisible(true);
        refreshAll();
    }

    /**
     * Sets the icons.
     */
    private void setIcons() {
        List<Image> images = new ArrayList<>();
        try {
            images.add(ImageIO.read(ICON96));
            images.add(ImageIO.read(ICON32));
            images.add(ImageIO.read(ICON16));
        } catch (IOException e) {
            Utils.logException(e);
        }
        this.setIconImages(images);
    }

    /**
     * createSearchAllWin.
     */
    private void createSearchAllWin() {
        searchAllStringsWin = new JFrame(rb.getString(KeyConstants.KEY_SEARCH_ALL_WIN_TITLE));
        searchAllStringsWin.setLayout(new GridLayout(SEARCH_ALL_GRID_ROWS, SEARCH_ALL_GRID_COLS, SEARCH_ALL_GRID_HGAP, SEARCH_ALL_GRID_VGAP));
        JLabel searchAllWinSkipCharsLabel = new JLabel(rb.getString(KeyConstants.KEY_SEARCH_ALL_WIN_SKIP_CHARS_LABEL), SwingConstants.LEFT);
        JLabel searchAllWinEndCharsLabel = new JLabel(rb.getString(KeyConstants.KEY_SEARCH_ALL_WIN_END_CHARS_LABEL), SwingConstants.LEFT);
        searchAllWinEndCharsInput = new JTextField(SEARCH_ALL_END_CHARS_LENGTH);
        searchAllWinSkipCharsOpt = new JComboBox<>();
        for(int i = 0; i <= 16; i++) {
            searchAllWinSkipCharsOpt.addItem(i);
        }
        searchAllWinProgressBar = new JProgressBar(SEARCH_ALL_MIN_PROGRESS, SEARCH_ALL_MAX_PROGRESS);
        searchAllStringsWin.add(searchAllWinSkipCharsLabel);
        searchAllStringsWin.add(searchAllWinSkipCharsOpt);
        searchAllStringsWin.add(searchAllWinEndCharsLabel);
        searchAllStringsWin.add(searchAllWinEndCharsInput);
        searchAllStringsWin.add(searchAllWinSearchButton);
        searchAllStringsWin.add(searchAllWinCancelButton);
        searchAllStringsWin.add(searchAllWinProgressBar);
        searchAllStringsWin.add(new JLabel());
        searchAllStringsWin.pack();
        searchAllStringsWin.setResizable(Boolean.FALSE);
    }

    /**
     * createNewPrjWin.
     */
    private void createNewPrjWin() {
        newPrjWin = new JFrame(rb.getString(KeyConstants.KEY_NEW_PRJ_TITLE));
        newPrjWin.setLayout(new GridLayout(NEW_PROJECT_GRID_ROWS, NEW_PROJECT_GRID_COLS, NEW_PROJECT_GRID_HGAP, NEW_PROJECT_GRID_VGAP));
        JLabel newPrjWinNameLabel = new JLabel(rb.getString(KeyConstants.KEY_NEW_PRJ_NAME), SwingConstants.LEFT);
        JLabel newPrjWinFileLabel = new JLabel(rb.getString(KeyConstants.KEY_NEW_PRJ_FILE), SwingConstants.LEFT);
        JLabel newPrjWinFileTypeLabel = new JLabel(rb.getString(KeyConstants.KEY_NEW_PRJ_FILETYPE), SwingConstants.LEFT);
        newPrjWinNameInput = new JTextField(30);
        newPrjWinFileInput = new JTextField();
        newPrjWinFileTypeOpt = new JComboBox<>();
        otherEntry = new AbstractMap.SimpleEntry<>(rb.getString(KeyConstants.KEY_NEW_PRJ_OTHER), Constants.FILE_TYPE_OTHER);
        smdEntry = new AbstractMap.SimpleEntry<>(rb.getString(KeyConstants.KEY_NEW_PRJ_SMD), Constants.FILE_TYPE_MEGADRIVE);
        snesEntry = new AbstractMap.SimpleEntry<>(rb.getString(KeyConstants.KEY_NEW_PRJ_SNES), Constants.FILE_TYPE_SNES);
        gbEntry = new AbstractMap.SimpleEntry<>(rb.getString(KeyConstants.KEY_NEW_PRJ_NGB), Constants.FILE_TYPE_NGB);
        tapEntry = new AbstractMap.SimpleEntry<>(rb.getString(KeyConstants.KEY_NEW_PRJ_SPT), Constants.FILE_TYPE_ZXTAP);
        tzxEntry = new AbstractMap.SimpleEntry<>(rb.getString(KeyConstants.KEY_NEW_PRJ_SPZ), Constants.FILE_TYPE_TZX);
        smsEntry = new AbstractMap.SimpleEntry<>(rb.getString(KeyConstants.KEY_NEW_PRJ_SMS), Constants.FILE_TYPE_MASTERSYSTEM);
        newPrjWinFileTypeOpt.addItem(otherEntry);
        newPrjWinFileTypeOpt.addItem(smdEntry);
        newPrjWinFileTypeOpt.addItem(smsEntry);
        newPrjWinFileTypeOpt.addItem(gbEntry);
        newPrjWinFileTypeOpt.addItem(snesEntry);
        newPrjWinFileTypeOpt.addItem(tapEntry);
        newPrjWinFileTypeOpt.addItem(tzxEntry);
        newPrjWin.add(newPrjWinFileLabel);
        newPrjWin.add(newPrjWinFileInput);
        newPrjWin.add(newPrjWinSearchFileButton);
        newPrjWin.add(newPrjWinNameLabel);
        newPrjWin.add(newPrjWinNameInput);
        newPrjWin.add(new JLabel());
        newPrjWin.add(newPrjWinFileTypeLabel);
        newPrjWin.add(newPrjWinFileTypeOpt);
        newPrjWin.add(new JLabel());
        newPrjWin.add(new JLabel());
        newPrjWin.add(newPrjWinCreateButton);
        newPrjWin.add(newPrjWinCancelButton);
        newPrjWin.pack();
        newPrjWin.setResizable(Boolean.FALSE);
    }

    /**
     * Refresh view mode.
     */
    private void refreshViewMode() {
        hexTextArea.setCaretPosition(0);
        asciiTextArea.setCaretPosition(0);
        if(view16Cols.getState()) {
            visibleColumns = MIN_COLS_AND_ROWS;
        }
        else {
            visibleColumns = MAX_COLS_AND_ROWS;
        }
        int oldVisibleRows = visibleRows;
        if(view16Rows.getState()) {
            visibleRows = MIN_COLS_AND_ROWS;
        }
        else {
            visibleRows = MAX_COLS_AND_ROWS;
        }
        if(oldVisibleRows > visibleRows) {
            vsb.setPreferredSize(new Dimension((int) vsb.getPreferredSize().getWidth(), (int) vsb.getPreferredSize().getHeight() / 2));
        }
        if(oldVisibleRows < visibleRows) {
            vsb.setPreferredSize(new Dimension((int) vsb.getPreferredSize().getWidth(), (int) vsb.getPreferredSize().getHeight() * 2));
        }
        hexTextArea.setColumns(visibleColumns * Constants.HEX_VALUE_SIZE);
        hexTextArea.setRows(visibleRows);
        hexTextArea.setPreferredSize(DIMENSION_0_0);
        asciiTextArea.setColumns(visibleColumns);
        asciiTextArea.setRows(visibleRows);
        asciiTextArea.setPreferredSize(DIMENSION_0_0);
        offsetsTextArea.setPreferredSize(DIMENSION_0_0);
        offsetsTextArea.setRows(visibleRows);
        pack();
        //Fix for java 8+ width
        asciiTextArea.setSize(asciiTextArea.getWidth() + 1, asciiTextArea.getHeight());
        asciiTextArea.setPreferredSize(new Dimension(asciiTextArea.getWidth() + 1, asciiTextArea.getHeight()));
        pack();
        refreshAll();
    }

    /**
     * Adds all the program listeners.
     */
    private void addAllListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                closeApp();
            }
        });
        asciiTextArea.addCaretListener(e -> refreshSelection());
        asciiTextArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    doPop(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    doPop(e);
                }
            }
            private void doPop(MouseEvent e) {
                asciiTextArea.setCaretPosition(e.getX() / 8 + e.getY() / 18 * visibleColumns);
                PopUpOffsetEntry menu = new PopUpOffsetEntry(getCaretEntry());
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        /* (non-Javadoc)
         * @see java.awt.event.AdjustmentListener#adjustmentValueChanged(java.awt.event.AdjustmentEvent)
         */
        vsb.addAdjustmentListener(evt -> {
            Adjustable source = evt.getAdjustable();
            if (evt.getValueIsAdjusting()) {
                return;
            }
            int orient = source.getOrientation();
            if (orient == Adjustable.VERTICAL) {
                int type = evt.getAdjustmentType();
                int value = evt.getValue();
                switch (type) {
                    case AdjustmentEvent.UNIT_INCREMENT:
                        offset += visibleColumns;
                        break;
                    case AdjustmentEvent.UNIT_DECREMENT:
                        offset -= visibleColumns;
                        break;
                    case AdjustmentEvent.BLOCK_INCREMENT:
                        offset += getOffsetBlock();
                        break;
                    case AdjustmentEvent.BLOCK_DECREMENT:
                        offset -= getOffsetBlock();
                        break;
                    case AdjustmentEvent.TRACK:
                        offset = value;
                        break;
                    default:
                        break;
                }
                asciiTextArea.setText(Utils.getTextArea(offset, getViewSize(), fileBytes, hexTable));
                hexTextArea.setText(Utils.getHexAreaFixedWidth(offset, getViewSize(), fileBytes, visibleColumns));
                asciiTextArea.setCaretPosition(asciiTextArea.getText().length());
            }
        });
        vsb.getModel().addChangeListener(e -> {
            offset = vsb.getValue();
            refreshAll();
        });
        //Drop on new project, batch creation
        new FileDrop(newPrjWin, files -> {
            if(files != null) {
                if(files.length > 1) {
                    newPrjWin.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    disableProjectWindow();
                    newPrjWin.repaint();
                    for(File file : files) {
                        try {
                            ProjectUtils.createProject(file);
                        } catch (Exception e) {
                            Utils.logException(e);
                        }
                    }
                    newPrjWin.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    newPrjWin.setVisible(false);
                    JOptionPane.showMessageDialog(asciiTextArea.getParent(),
                            rb.getString(KeyConstants.KEY_NEW_PRJ_GENERATING_MSG),
                            rb.getString(KeyConstants.KEY_NEW_PRJ_GENERATING_MSG), JOptionPane.INFORMATION_MESSAGE);
                }
                else {
                    for(File file : files) {
                        newPrjWinFileInput.setText(file.getName());
                        projectFile = file;
                        selectProjectFileType(projectFile);
                        newPrjWinNameInput.setText(ProjectUtils.getProjectName(projectFile.getName()));
                    }
                }
            }
        });
        newPrjWinFileInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                cleanFile();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                cleanFile();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                cleanFile();
            }
            void cleanFile() {
                projectFile = null;
            }
        });
        searchResults.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                @SuppressWarnings("unchecked")
                JList<TableSearchResult> list = (JList<TableSearchResult>) evt.getSource();
                TableSearchResult tsr = list.getSelectedValue();

                if(evt.getClickCount() == 1 && tsr != null) {
                    if(SwingUtilities.isLeftMouseButton(evt)) {
                        offset = tsr.getOffset();
                        asciiTextArea.setCaretPosition(0);
                        hexTextArea.setCaretPosition(0);
                        refreshAll();
                    }
                    if(SwingUtilities.isRightMouseButton(evt)) {
                        int selectedOption = JOptionPane.showConfirmDialog(null,
                                rb.getString(KeyConstants.KEY_SEARCH_RESULT_TABLE),
                                rb.getString(KeyConstants.KEY_SEARCH_RESULT_TABLE_TITLE),
                                JOptionPane.YES_NO_OPTION);
                        if(selectedOption == JOptionPane.YES_OPTION) {
                            offset = tsr.getOffset();
                            hexTable = tsr.getHexTable();
                            refreshAll();
                        }
                    }
                }
            }
        });
        new FileDrop(this, files -> {
            if(files != null && files.length > 0) {
                requestFocus();
                requestFocusInWindow();
                for(File file : files) {
                    if(file.getAbsolutePath().endsWith(EXTENSION_TABLE)) {
                        reloadTableFile(file);
                    } else {
                        if(file.getAbsolutePath().endsWith(EXTENSION_OFFSET)) {
                            reloadOffsetsFile(file);
                        } else {
                            if(file.getAbsolutePath().endsWith(EXTENSION_EXTRACTION)) {
                                reloadExtAsOffsetsFile(file);
                            } else {
                                reloadHexFile(file);
                            }
                        }
                    }
                }
            }
        });
        addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) {
                if(offset > visibleColumns) {
                    offset -= visibleColumns;
                }
            } else {
                if(offset < fileBytes.length - getViewSize()) {
                    offset += visibleColumns;
                }
            }
            refreshAll();
        });
        addListenerForKeys();
    }

    /**
     * Adds the listener for the key presses.
     */
    private void addListenerForKeys() {
        addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                // Auto-repeated on time
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_ADD:
                    case KeyEvent.VK_PLUS:
                        offset++;
                        break;
                    case KeyEvent.VK_SUBTRACT:
                    case KeyEvent.VK_MINUS:
                        offset--;
                        break;
                    case KeyEvent.VK_PAGE_DOWN:
                        offset += getOffsetBlock();
                        break;
                    case KeyEvent.VK_PAGE_UP:
                        offset -= getOffsetBlock();
                        break;
                    case KeyEvent.VK_LEFT:
                        if(asciiTextArea.getCaretPosition() > 0) {
                            asciiTextArea.setCaretPosition(asciiTextArea.getCaretPosition() - 1);
                        }
                        else {
                            offset -= visibleColumns;
                            if(offset > 0) {
                                asciiTextArea.setCaretPosition(asciiTextArea.getCaretPosition() + visibleColumns - 1);
                            }
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if(asciiTextArea.getCaretPosition() > 0) {
                            int carUpPos = asciiTextArea.getCaretPosition() - visibleColumns;
                            if(carUpPos < 0) {
                                carUpPos += visibleColumns;
                                offset -= visibleColumns;
                                if(offset < 0) {
                                    offset = 0;
                                    carUpPos = 0;
                                }
                            }
                            asciiTextArea.setCaretPosition(carUpPos);
                        }
                        else {
                            offset -= visibleColumns;
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if(asciiTextArea.getCaretPosition() < getViewSize()) {
                            int carDownPos = asciiTextArea.getCaretPosition() + visibleColumns;
                            if(carDownPos >= getViewSize()) {
                                carDownPos -= visibleColumns;
                                offset += visibleColumns;
                                if(offset > fileBytes.length - getViewSize()) {
                                    offset = fileBytes.length - getViewSize();
                                    carDownPos = getViewSize() - 1;
                                }
                            }
                            asciiTextArea.setCaretPosition(carDownPos);
                        }
                        else {
                            offset += visibleColumns;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        int carRightPos = asciiTextArea.getCaretPosition() + 1;
                        if(carRightPos >= getViewSize()) {
                            offset += visibleColumns;
                            if(offset > fileBytes.length - getViewSize()) {
                                carRightPos = getViewSize() - 1;
                            }
                            else {
                                carRightPos -= visibleColumns;
                            }
                        }
                        asciiTextArea.setCaretPosition(carRightPos);
                        break;
                    case KeyEvent.VK_END:
                        if ((keyEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
                            offset = fileBytes.length - getViewSize();
                        } else {
                            OffsetEntry selectedEntry = getCaretEntry();
                            if (currEntry.getStart() > 0 || selectedEntry != currEntry) {
                                endItemAction(selectedEntry);
                            }
                        }
                        break;
                    case KeyEvent.VK_HOME:
                        if ((keyEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
                            offset = 0;
                        } else {
                            getCaretEntry().setStart(offset + asciiTextArea.getCaretPosition());
                        }
                        break;
                    case KeyEvent.VK_INSERT:
                        OffsetEntry selectedEntry = getCaretEntry();
                        if (selectedEntry != currEntry) {
                            splitItemAction(selectedEntry);
                        }
                        break;
                    case KeyEvent.VK_DELETE:
                        OffsetEntry selectedEntryDel = getCaretEntry();
                        if (selectedEntryDel != currEntry) {
                            deleteItemAction(selectedEntryDel);
                        }
                        break;
                    default:
                        break;
                }
                refreshAll();
            }

            /*
             * (non-Javadoc)
             *
             * @see
             * java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
             */
            @Override
            public void keyReleased(KeyEvent keyEvent) {
                //No action
            }

            /*
             * (non-Javadoc)
             *
             * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
             */
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                //No action
            }
        });
    }

    /**
     * Gets the visible offsets.
     *
     * @param offset the starting offset
     * @param columns num of columns
     * @param rows num of rows
     * @return the visible offsets
     */
    private static String getVisibleOffsets(int offset, int columns, int rows) {
        StringBuilder sb = new StringBuilder((Constants.HEX_ADDR_SIZE + HEX_STARTS.length() + Constants.SPACE_STR.length()) * rows);
        for(int i = 0; i < rows; i++) {
            sb.append(HEX_STARTS);
            sb.append(Utils.intToHexString(offset + i * columns, Constants.HEX_ADDR_SIZE));
            sb.append(Constants.S_NEWLINE);
        }
        return sb.toString();
    }

    /**
     * Refresh title.
     */
    private void refreshTitle() {
        setTitle(rb.getString(KeyConstants.KEY_TITLE) + " [" + hexFile +"] - [" + tableFile.getName() + "] - [" + offsetFile.getName() + "]" );
    }

    /**
     * Refresh all.
     */
    private void refreshAll() {
        Collections.sort(offEntries);
        if(offset > fileBytes.length - getViewSize()) {
            offset = fileBytes.length - getViewSize();
        }
        if(offset < 0) {
            offset = 0;
        }
        int pos = asciiTextArea.getCaretPosition();
        if(getViewSize() != asciiTextArea.getText().length()) {
            pos = 0;
            asciiTextArea.setCaretPosition(0);
            hexTextArea.setCaretPosition(0);
        }
        hexTextArea.setText(Utils.getHexAreaFixedWidth(offset, getViewSize(), fileBytes, visibleColumns));
        asciiTextArea.setText(Utils.getTextArea(offset, getViewSize(), fileBytes, hexTable));
        offsetsTextArea.setText(getVisibleOffsets(offset, visibleColumns, visibleRows));
        vsb.setMinimum(0);
        vsb.setMaximum(fileBytes.length - getViewSize());
        vsb.setUnitIncrement(visibleColumns);
        vsb.setBlockIncrement(getViewSize());
        vsb.setValue(offset);
        refreshTitle();
        refreshSelection();
        asciiTextArea.setCaretPosition(pos);
        hexTextArea.setCaretPosition(pos * Constants.HEX_VALUE_SIZE);
        asciiTextArea.repaint();
        hexTextArea.repaint();
        offsetsTextArea.repaint();
    }

    /**
     * Creates the menu.
     */
    private void createMenu() {
        //Create objects
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        //Menus
        JMenu fileMenu = new JMenu(rb.getString(KeyConstants.KEY_FILEMENU));
        JMenu tableMenu = new JMenu(rb.getString(KeyConstants.KEY_TABLEMENU));
        JMenu offsetMenu = new JMenu(rb.getString(KeyConstants.KEY_OFFSETMENU));
        JMenu toolsMenu = new JMenu(rb.getString(KeyConstants.KEY_TOOLS_MENU));
        JMenu helpMenu = new JMenu(rb.getString(KeyConstants.KEY_HELP_MENU));
        JMenu viewMenu = new JMenu(rb.getString(KeyConstants.KEY_VIEW_MENU));

        //Items
        exit =  new JMenuItem(rb.getString(KeyConstants.KEY_EXIT_MENUITEM));
        openFile = new JMenuItem(rb.getString(KeyConstants.KEY_OPEN_FILE_MENUITEM));
        newProject = new JMenuItem(rb.getString(KeyConstants.KEY_NEW_PROJECT_MENUITEM));
        openTable = new JMenuItem(rb.getString(KeyConstants.KEY_OPEN_TABLE_MENUITEM));
        saveTable = new JMenuItem(rb.getString(KeyConstants.KEY_SAVE_TABLE_MENUITEM));
        reloadTable = new JMenuItem(rb.getString(KeyConstants.KEY_RELOAD_TABLE_MENUITEM));

        about = new JMenuItem(rb.getString(KeyConstants.KEY_ABOUT_MENUITEM));
        help = new JMenuItem(rb.getString(KeyConstants.KEY_HELP_MENUITEM));
        goTo = new JMenuItem(rb.getString(KeyConstants.KEY_GOTO_MENUITEM));
        searchRelative = new JMenuItem(rb.getString(KeyConstants.KEY_SEARCH_RELATIVE_MENUITEM));
        searchAll = new JMenuItem(rb.getString(KeyConstants.KEY_SEARCH_ALL_MENUITEM));
        extract = new JMenuItem(rb.getString(KeyConstants.KEY_EXTRACT_MENUITEM));
        find = new JMenuItem(rb.getString(KeyConstants.KEY_FIND_MENUITEM));
        openOffsets = new JMenuItem(rb.getString(KeyConstants.KEY_OPEN_OFFSETS_MENUITEM));
        saveOffsets = new JMenuItem(rb.getString(KeyConstants.KEY_SAVE_OFFSETS_MENUITEM));
        nextOffset = new JMenuItem(rb.getString(KeyConstants.KEY_NEXT_RANGE_MENUITEM));
        prevOffset = new JMenuItem(rb.getString(KeyConstants.KEY_PREV_TANGE_MENUITEM));
        clearOffsets = new JMenuItem(rb.getString(KeyConstants.KEY_CLEAN_OFFSETS));
        view16Cols = new JCheckBoxMenuItem(rb.getString(KeyConstants.KEY_16COLS_MENUITEM));
        view16Rows = new JCheckBoxMenuItem(rb.getString(KeyConstants.KEY_16ROWS_MENUITEM));

        tableFilter = new SimpleFilter(EXTENSION_TABLE, rb.getString(KeyConstants.KEY_FILTER_TABLE));
        offsetFileFilter = new SimpleFilter(Arrays.asList(EXTENSION_OFFSET, EXTENSION_EXTRACTION),
                rb.getString(KeyConstants.KEY_FILTER_OFFSET));

        offsetOnlyFileFilter = new SimpleFilter(Collections.singletonList(EXTENSION_OFFSET),
                rb.getString(KeyConstants.KEY_FILTER_OFFSET_ONLY));

        extOnlyFileFilter = new SimpleFilter(Collections.singletonList(EXTENSION_EXTRACTION),
                rb.getString(KeyConstants.KEY_FILTER_EXT_ONLY));

        //Setup menu
        fileMenu.add(openFile);
        fileMenu.add(newProject);
        fileMenu.add(exit);

        tableMenu.add(openTable);
        tableMenu.add(saveTable);
        tableMenu.add(reloadTable);

        offsetMenu.add(openOffsets);
        offsetMenu.add(saveOffsets);
        offsetMenu.add(extract);
        offsetMenu.add(nextOffset);
        offsetMenu.add(prevOffset);
        offsetMenu.add(clearOffsets);

        toolsMenu.add(goTo);
        toolsMenu.add(searchRelative);
        toolsMenu.add(find);
        toolsMenu.add(searchAll);

        viewMenu.add(view16Cols);
        viewMenu.add(view16Rows);

        helpMenu.add(help);
        helpMenu.add(about);

        menuBar.add(fileMenu);
        menuBar.add(tableMenu);
        menuBar.add(offsetMenu);
        menuBar.add(toolsMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        //Actions
        setActions();

        //Accelerators
        openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        newProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        goTo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
        openTable.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
        saveTable.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        reloadTable.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
        searchRelative.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        searchAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        extract.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        openOffsets.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        saveOffsets.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        nextOffset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
        prevOffset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        clearOffsets.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        help.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        view16Cols.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        view16Rows.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
    }

    /**
     * Next offset.
     */
    private void nextOffset() {
        Collections.sort(offEntries);
        //Nos paramos en el primero cuyo start sea mayor a nuestro offset
        for(OffsetEntry entry : offEntries) {
            if(entry.getStart() > offset) {
                offset = entry.getStart();
                refreshAll();
                break;
            }
        }
    }

    /**
     * Prev offset.
     */
    private void prevOffset() {
        offEntries.sort(Collections.reverseOrder());
        for(OffsetEntry entry : offEntries) {
            if(entry.getStart() < offset) {
                offset = entry.getStart();
                refreshAll();
                break;
            }
        }
    }

    /**
     * Clean offsets.
     */
    private void cleanOffsets() {
        if(GuiUtils.confirmActionAlert(rb.getString(KeyConstants.KEY_CONFIRM_ACTION_TITLE),
                rb.getString(KeyConstants.KEY_CONFIRM_ACTION))) {
            offEntries.clear();
            offsetFile = new File(Constants.EMPTY_OFFSET_FILE);
            refreshAll();
        }
    }

    /**
     * Sets the actions.
     */
    private void setActions() {
        view16Cols.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_16COLS_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = 251407879942401227L;
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshViewMode();
            }
        });
        view16Rows.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_16ROWS_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = 251407879942401225L;
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshViewMode();
            }
        });
        help.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_HELP_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = 251407879942401229L;
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(help, rb.getString(KeyConstants.KEY_HELP_DESC),
                        rb.getString(KeyConstants.KEY_HELP_MENUITEM), JOptionPane.INFORMATION_MESSAGE);
            }
        });
        about.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_ABOUT_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = 251407879942401229L;
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(about, rb.getString(KeyConstants.KEY_ABOUT_DESC) ,
                        rb.getString(KeyConstants.KEY_ABOUT_MENUITEM), JOptionPane.INFORMATION_MESSAGE);
            }
        });
        extract.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_EXTRACT_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = 251407879942401215L;
            @Override
            public void actionPerformed(ActionEvent e) {
                String outFileName = tableFile.getName().replaceAll(FileUtils.getFileExtension(tableFile.getName()),
                        Constants.EXTRACT_EXTENSION_NODOT);
                JFileChooser fileChooser = new JFileChooser();
                File selectedFile = new File(outFileName);
                fileChooser.setSelectedFile(selectedFile);
                File parent = selectedFile.getParentFile();
                if(parent == null) {
                    parent = hexFile.getParentFile();
                }
                if(parent == null) {
                    parent = tableFile.getParentFile();
                }
                fileChooser.setCurrentDirectory(parent);
                fileChooser.setFileFilter(extOnlyFileFilter);
                fileChooser.setApproveButtonText(rb.getString(KeyConstants.KEY_SAVE_BUTTON));
                if (fileChooser.showSaveDialog(saveOffsets) == JFileChooser.APPROVE_OPTION &&
                        confirmSelectedFile(fileChooser.getSelectedFile())) {
                    try {
                        FileUtils.extractAsciiFile(hexTable, fileBytes, fileChooser.getSelectedFile().getAbsolutePath(),
                                offEntries, false);
                    } catch (Exception e1) {
                        Utils.logException(e1);
                    }
                }
                refreshAll();
            }
        });
        nextOffset.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_NEXT_RANGE_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = 251407879942401229L;
            @Override
            public void actionPerformed(ActionEvent e) {
                nextOffset();
            }
        });
        prevOffset.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_PREV_TANGE_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = 251407879942401239L;
            @Override
            public void actionPerformed(ActionEvent e) {
                prevOffset();
            }
        });
        clearOffsets.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_CLEAN_OFFSETS)) {
            /** serialVersionUID */
            private static final long serialVersionUID = 251407879942401239L;
            @Override
            public void actionPerformed(ActionEvent e) {
                cleanOffsets();
            }
        });
        exit.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_EXIT_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = 251407879942401219L;
            @Override
            public void actionPerformed(ActionEvent e) {
                closeApp();
            }
        });
        openFile.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_OPEN_FILE_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = 251407879942401219L;
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                File parent = hexFile.getParentFile();
                if(parent == null) {
                    parent = tableFile.getParentFile();
                }
                fileChooser.setCurrentDirectory(parent);
                if (fileChooser.showOpenDialog(openFile) ==
                        JFileChooser.APPROVE_OPTION) {
                    reloadHexFile(fileChooser.getSelectedFile());
                }
            }
        });
        newProject.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_NEW_PROJECT_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = 251417879942401219L;
            @Override
            public void actionPerformed(ActionEvent e) {
                showProjectWindow();
            }
        });
        goTo.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_GOTO_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = 251407879942401217L;
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog(rb.getString(KeyConstants.KEY_OFFSET_INPUT));
                if(s != null && s.length() > 0) {
                    try {
                        if (s.startsWith(DEC_STARTS)) {
                            offset = Integer.parseInt(s.substring(DEC_STARTS.length()));
                        } else {
                            offset = Integer.parseInt(s, Constants.HEX_RADIX);
                        }
                    } catch (NumberFormatException e1) {
                        //Do nothing
                    }
                    refreshAll();
                }
            }
        });
        searchAll.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_SEARCH_ALL_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = 251407879942401219L;
            @Override
            public void actionPerformed(ActionEvent e) {
                showSearchAllWindow();
            }
        });
        searchRelative.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_SEARCH_RELATIVE_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = 251407879942401218L;

            @Override
            public void actionPerformed(ActionEvent e) {
                String searchString = JOptionPane.showInputDialog(rb.getString(KeyConstants.KEY_SEARCH_RELATIVE));
                if(searchString != null && searchString.length() > 0) {
                    try {
                        List<TableSearchResult> results = FileUtils.multiSearchRelative8Bits(fileBytes, searchString, SEARCH_JOKER_EXPANSIONS);
                        if(results.isEmpty()) {
                            JOptionPane.showMessageDialog(help, rb.getString(KeyConstants.KEY_NO_RESULTS_DESC),
                                    rb.getString(KeyConstants.KEY_NO_RESULTS_TITLE), JOptionPane.INFORMATION_MESSAGE);
                        }
                        else {
                            searchResults.setListData(results.toArray(new TableSearchResult[0]));
                            resultsWindow.pack();
                            resultsWindow.setLocationRelativeTo(resultsWindow.getParent());
                            resultsWindow.setVisible(true);
                        }
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(searchRelative, rb.getString(KeyConstants.KEY_SEARCH_RELATIVE_MIN_LENGTH));
                    }
                    vsb.setValue(offset);
                }
            }
        });
        find.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_FIND_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = 251407879942401219L;

            @Override
            public void actionPerformed(ActionEvent e) {
                String searchString = JOptionPane.showInputDialog(rb.getString(KeyConstants.KEY_FIND));
                if(searchString != null && searchString.length() > 0) {
                    try {
                        List<TableSearchResult>  results = FileUtils.multiFindString(fileBytes, hexTable, searchString, true,
                                SEARCH_JOKER_EXPANSIONS);
                        if(results.isEmpty()) {
                            JOptionPane.showMessageDialog(help, rb.getString(KeyConstants.KEY_NO_RESULTS_DESC),
                                    rb.getString(KeyConstants.KEY_NO_RESULTS_TITLE), JOptionPane.INFORMATION_MESSAGE);
                        }
                        else {
                            searchResults.setListData(results.toArray(new TableSearchResult[0]));
                            resultsWindow.pack();
                            resultsWindow.setLocationRelativeTo(resultsWindow.getParent());
                            resultsWindow.setVisible(true);
                        }
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(searchRelative, rb.getString(KeyConstants.KEY_FIND_MIN_LENGTH));
                    }
                    vsb.setValue(offset);
                }
            }
        });
        openTable.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_OPEN_TABLE_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = 251407879942401219L;
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                File parent = tableFile.getParentFile();
                if(parent == null) {
                    parent = hexFile.getParentFile();
                }
                fileChooser.setCurrentDirectory(parent);
                fileChooser.setFileFilter(tableFilter);
                int result = fileChooser.showOpenDialog(openTable);
                if (result == JFileChooser.APPROVE_OPTION) {
                    reloadTableFile(fileChooser.getSelectedFile());
                }
            }
        });
        saveTable.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_SAVE_TABLE_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = 251407879942401219L;
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(tableFile);
                File parent = tableFile.getParentFile();
                if(parent == null) {
                    parent = hexFile.getParentFile();
                }
                fileChooser.setCurrentDirectory(parent);
                fileChooser.setApproveButtonText(rb.getString(KeyConstants.KEY_SAVE_BUTTON));
                fileChooser.setFileFilter(tableFilter);
                int result = fileChooser.showSaveDialog(saveTable);
                if (result == JFileChooser.APPROVE_OPTION) {
                    boolean accepted = confirmSelectedFile(fileChooser.getSelectedFile());
                    if(accepted) {
                        tableFile = fileChooser.getSelectedFile();
                        if(!tableFile.getAbsolutePath().endsWith(EXTENSION_TABLE)) {
                            tableFile = new File(tableFile.getAbsolutePath() + EXTENSION_TABLE);
                        }
                        try {
                            FileUtils.writeFileAscii(tableFile.getAbsolutePath(), hexTable.toAsciiTable());
                        } catch (Exception e1) {
                            Utils.logException(e1);
                        }
                    }
                    refreshAll();
                }
            }
        });
        reloadTable.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_RELOAD_TABLE_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = 251407879942401218L;
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tableFile != null && tableFile.exists()) {
                    reloadTableFile(tableFile);
                }
            }
        });
        openOffsets.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_OPEN_OFFSETS_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = -6816696525367794844L;

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                File parent = offsetFile.getParentFile();
                if(parent == null) {
                    parent = hexFile.getParentFile();
                }
                if(parent == null) {
                    parent = tableFile.getParentFile();
                }
                fileChooser.setCurrentDirectory(parent);
                fileChooser.setFileFilter(offsetFileFilter);
                int result = fileChooser.showOpenDialog(openOffsets);
                if (result == JFileChooser.APPROVE_OPTION) {
                    if(fileChooser.getSelectedFile().getName().endsWith(EXTENSION_OFFSET)) {
                        reloadOffsetsFile(fileChooser.getSelectedFile());
                    }
                    else {
                        reloadExtAsOffsetsFile(fileChooser.getSelectedFile());
                    }
                }
            }
        });
        saveOffsets.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_SAVE_OFFSETS_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = -1281167224371368937L;

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                File selectedFile;
                if(offsetFile.getAbsolutePath().endsWith(Constants.EXTRACT_EXTENSION)) {
                    selectedFile = new File(FileUtils.getFilePath(offsetFile) + File.separator +
                            tableFile.getName().replaceAll(Constants.TBL_EXTENSION_REGEX,
                                    Constants.OFFSET_EXTENSION));
                } else {
                    selectedFile = offsetFile;
                }
                fileChooser.setSelectedFile(selectedFile);
                File parent = selectedFile.getParentFile();
                if(parent == null) {
                    parent = hexFile.getParentFile();
                }
                if(parent == null) {
                    parent = tableFile.getParentFile();
                }
                fileChooser.setCurrentDirectory(parent);
                fileChooser.setFileFilter(offsetOnlyFileFilter);
                fileChooser.setApproveButtonText(rb.getString(KeyConstants.KEY_SAVE_BUTTON));
                if (fileChooser.showSaveDialog(saveOffsets) == JFileChooser.APPROVE_OPTION &&
                        confirmSelectedFile(fileChooser.getSelectedFile())) {
                    offsetFile = fileChooser.getSelectedFile();
                    if(!offsetFile.getAbsolutePath().endsWith(EXTENSION_OFFSET)) {
                        offsetFile = new File(offsetFile.getAbsolutePath() + EXTENSION_OFFSET);
                    }
                    try {
                        Collections.sort(offEntries);
                        FileUtils.writeFileAscii(offsetFile.getAbsolutePath(), Utils.toFileString(offEntries));
                    } catch (Exception e1) {
                        Utils.logException(e1);
                    }
                }
                refreshAll();
            }
        });
        setActionsNewPrjWin();
        setActionsAllStringsWin();
    }

    /**
     * setActionsNewPrjWin.
     */
    private void setActionsNewPrjWin() {
        newPrjWinCreateButton = new JButton(rb.getString(KeyConstants.KEY_NEW_PRJ_CREA_BUT));
        newPrjWinCancelButton = new JButton(rb.getString(KeyConstants.KEY_NEW_PRJ_CLOSE_BUT));
        newPrjWinSearchFileButton = new JButton(rb.getString(KeyConstants.KEY_FIND_MENUITEM));
        newPrjWinSearchFileButton.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_FIND_MENUITEM)) {
            /** serialVersionUID */
            private static final long serialVersionUID = -1221167224372368937L;

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(openFile);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    newPrjWinFileInput.setText(file.getName());
                    projectFile = file;
                    selectProjectFileType(projectFile);
                    newPrjWinNameInput.setText(ProjectUtils.getProjectName(projectFile.getName()));
                }
            }
        });
        newPrjWinCreateButton.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_NEW_PRJ_CREA_BUT)) {
            /** serialVersionUID */
            private static final long serialVersionUID = -1221167224371368937L;

            @SuppressWarnings("unchecked")
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean formErrors = false;
                try{
                    String name = newPrjWinNameInput.getText();
                    String fileName = newPrjWinFileInput.getText();
                    formErrors = name == null || name.length() < NEW_PROJECT_NAME_MIN_LENGTH ||
                            fileName == null || fileName.length() == 0 ||
                            !Utils.isValidFileName(fileName) || !Utils.isValidFileName(name);
                    if(formErrors) {
                        JOptionPane.showMessageDialog(null, rb.getString(KeyConstants.KEY_NEW_PRJ_ERRORS_MSG), rb.getString(KeyConstants.KEY_ERROR_TITLE), JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        newPrjWin.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                        disableProjectWindow();
                        newPrjWin.repaint();
                        ProjectUtils.createNewProject(name, fileName, ((Entry<String, String>) Objects.requireNonNull(newPrjWinFileTypeOpt.getSelectedItem())).getValue(), projectFile);
                        newPrjWin.setVisible(false);
                        JOptionPane.showMessageDialog(asciiTextArea.getParent(),
                                rb.getString(KeyConstants.KEY_NEW_PRJ_GENERATING_MSG),
                                rb.getString(KeyConstants.KEY_NEW_PRJ_GENERATING_MSG), JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e1) {
                    Utils.logException(e1);
                    JOptionPane.showMessageDialog(null, rb.getString(KeyConstants.KEY_ERROR), rb.getString(KeyConstants.KEY_ERROR_TITLE), JOptionPane.ERROR_MESSAGE);
                } finally {
                    newPrjWin.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    if(!formErrors) {
                        enableProjectWindow();
                    }
                }
            }
        });
        newPrjWinCancelButton.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_NEW_PRJ_CLOSE_BUT)) {
            private static final long serialVersionUID = -1221167224371368937L;
            @Override
            public void actionPerformed(ActionEvent e) {
                newPrjWin.setVisible(false);
            }
        });
    }

    /**
     * Sets the actions all strings win.
     */
    private void setActionsAllStringsWin() {
        searchAllWinSearchButton = new JButton(rb.getString(KeyConstants.KEY_SEARCH_ALL_WIN_SEARCH_BUTTON));
        searchAllWinCancelButton = new JButton(rb.getString(KeyConstants.KEY_SEARCH_ALL_WIN_CANCEL_BUTTON));
        searchAllWinCancelButton.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_SEARCH_ALL_WIN_CANCEL_BUTTON)) {
            private static final long serialVersionUID = -1221167224371368933L;
            @Override
            public void actionPerformed(ActionEvent e) {
                if(searchAllThread != null) {
                    searchAllThread.interrupt();
                    searchAllThreadError = true;
                }
                SwingUtilities.invokeLater(() -> {
                    searchAllWinProgressBar.setValue(0);
                    enableSearchAllWindow();
                });
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
        searchAllWinSearchButton.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_SEARCH_ALL_WIN_SEARCH_BUTTON)) {
            private void runSearchAllThreadAction() {
                searchAllThreadAction();
            }

            private static final long serialVersionUID = -1221167224371368933L;
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    disableSearchAllWindow();
                    searchAllWinProgressBar.setValue(SEARCH_ALL_MIN_PROGRESS);
                });
                if(searchAllWinEndCharsInput.getText() != null && searchAllWinEndCharsInput.getText().length() > 0 &&
                        searchAllWinEndCharsInput.getText().matches(REGEXP_OFFSET_ENTRIES)) {
                    new Thread(this::runSearchAllThreadAction).start();
                }
                else {
                    JOptionPane.showMessageDialog(asciiTextArea.getParent(),
                            rb.getString(KeyConstants.KEY_ALERT_INVALID_ENDCHARS),
                            rb.getString(KeyConstants.KEY_ALERT_INVALID_ENDCHARS_TITLE), JOptionPane.ERROR_MESSAGE);
                    SwingUtilities.invokeLater(() -> {
                        enableSearchAllWindow();
                        searchAllWinProgressBar.setValue(SEARCH_ALL_MIN_PROGRESS);
                    });
                }
            }
        });
    }


    /**
     * Search all thread action.
     */
    private void searchAllThreadAction() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        final String extractFile = hexFile.getName() + Constants.EXTRACT_EXTENSION;
        searchAllThreadError = false;
        searchAllThread = new Thread(() -> {
            try {
                File file = new File(Constants.DEFAULT_DICT);
                if(!file.exists()) {
                    file = new File(Constants.PARENT_DIR + Constants.DEFAULT_DICT);
                }
                if(file.exists()) {
                    FileUtils.searchAllStrings(hexTable, fileBytes, searchAllWinSkipCharsOpt.getSelectedIndex(),
                            searchAllWinEndCharsInput.getText(), file.getAbsolutePath(), extractFile);
                }
                else {
                    searchAllThreadError = true;
                }
            } catch (Exception e) {
                searchAllThreadError = true;
                Utils.logException(e);
            }
        });
        searchAllThread.start();
        synchronized (searchAllLock) {
            while (searchAllThread != null &&
                    searchAllThread.isAlive() &&
                    !searchAllThread.isInterrupted()) {
                try {
                    searchAllLock.wait(50);
                    SwingUtilities.invokeLater(() -> searchAllWinProgressBar.setValue((int) hexTable.getSearchPercent()));
                    searchAllLock.wait(50);
                } catch (InterruptedException ex) {
                    searchAllThreadError = true;
                    LOGGER.log(Level.SEVERE, "Exception occured:", ex);
                    searchAllThread.interrupt();
                }
            }
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if(searchAllThreadError) {
            JOptionPane.showMessageDialog(searchAllStringsWin, rb.getString(KeyConstants.KEY_SEARCH_ALL_WIN_ERROR),
                    rb.getString(KeyConstants.KEY_SEARCH_ALL_WIN_ERROR), JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            JOptionPane.showMessageDialog(searchAllStringsWin, rb.getString(KeyConstants.KEY_SEARCHED_ALL_DESC) + extractFile,
                    rb.getString(KeyConstants.KEY_SEARCHED_ALL_TITLE), JOptionPane.INFORMATION_MESSAGE);
            searchAllStringsWin.setVisible(false);
        }
        SwingUtilities.invokeLater(() -> {
            enableSearchAllWindow();
            searchAllWinProgressBar.setValue(SEARCH_ALL_MIN_PROGRESS);
        });
    }

    /**
     * Confirm selected file.
     *
     * @param selectedFile the selected file
     * @return true, if successful
     */
    private boolean confirmSelectedFile(File selectedFile) {
        boolean accepted = true;
        if (selectedFile != null && selectedFile.exists()) {
            accepted = GuiUtils.confirmActionAlert(rb.getString(KeyConstants.KEY_CONFIRM_ACTION_TITLE),
                    rb.getString(KeyConstants.KEY_CONFIRM_FILE_OVERWRITE_ACTION));
        }
        return accepted;
    }

    /**
     * Select project file type.
     *
     * @param file the file
     */
    private void selectProjectFileType(File file) {
        if(file != null) {
            String extension =  FileUtils.getFileExtension(file);
            newPrjWinFileTypeOpt.setSelectedItem(otherEntry);
            if(Constants.EXTENSIONS_MEGADRIVE.contains(extension)) {
                newPrjWinFileTypeOpt.setSelectedItem(smdEntry);
            }
            if(Constants.EXTENSIONS_SNES.contains(extension)) {
                newPrjWinFileTypeOpt.setSelectedItem(snesEntry);
            }
            if(Constants.EXTENSIONS_GB.contains(extension)) {
                newPrjWinFileTypeOpt.setSelectedItem(gbEntry);
            }
            if(Constants.EXTENSIONS_TAP.contains(extension)) {
                newPrjWinFileTypeOpt.setSelectedItem(tapEntry);
            }
            if(Constants.EXTENSIONS_TZX_CDT.contains(extension)) {
                newPrjWinFileTypeOpt.setSelectedItem(tzxEntry);
            }
            if(Constants.EXTENSIONS_SMS.contains(extension)) {
                newPrjWinFileTypeOpt.setSelectedItem(smsEntry);
            }
        }
    }

    /**
     * Enable project window.
     */
    private void enableProjectWindow() {
        newPrjWinNameInput.setEnabled(true);
        newPrjWinFileInput.setEnabled(true);
        newPrjWinFileTypeOpt.setEnabled(true);
        newPrjWinCreateButton.setEnabled(true);
        newPrjWinCancelButton.setEnabled(true);
    }

    /**
     * Clean project window.
     */
    private void cleanProjectWindow() {
        newPrjWinNameInput.setText(Constants.EMPTY);
        newPrjWinFileInput.setText(Constants.EMPTY);
        newPrjWinFileTypeOpt.setSelectedIndex(0);
        projectFile = null;
    }

    /**
     * Disable project window.
     */
    private void disableProjectWindow() {
        newPrjWinNameInput.setEnabled(false);
        newPrjWinFileInput.setEnabled(false);
        newPrjWinFileTypeOpt.setEnabled(false);
        newPrjWinCreateButton.setEnabled(false);
        newPrjWinCancelButton.setEnabled(false);
    }

    /**
     * Creates the project window.
     */
    private void showProjectWindow() {
        cleanProjectWindow();
        enableProjectWindow();
        if(hexFile != null) {
            newPrjWinFileInput.setText(hexFile.getName());
            selectProjectFileType(hexFile);
            newPrjWinNameInput.setText(ProjectUtils.getProjectName(hexFile.getName()));
            projectFile = hexFile;
        }
        newPrjWin.pack();
        newPrjWin.setLocationRelativeTo(this);
        newPrjWin.setVisible(true);
    }

    /**
     * Enable search all window.
     */
    private void enableSearchAllWindow() {
        searchAllThread = null;
        searchAllWinSkipCharsOpt.setEnabled(true);
        searchAllWinEndCharsInput.setEnabled(true);
        searchAllWinSearchButton.setEnabled(true);
        searchAllWinCancelButton.setEnabled(false);
    }

    /**
     * Clean search all window.
     */
    private void cleanSearchAllWindow() {
        //Valores por defecto
        searchAllWinSkipCharsOpt.setSelectedIndex(SEARCH_ALL_DEFAULT_CHARS_INDEX);
        searchAllWinEndCharsInput.setText(SEARCH_ALL_DEFAULT_END_CHARS);
        searchAllWinProgressBar.setValue(SEARCH_ALL_MIN_PROGRESS);
    }

    /**
     * Disable search all window.
     */
    private void disableSearchAllWindow() {
        searchAllWinSkipCharsOpt.setEnabled(false);
        searchAllWinEndCharsInput.setEnabled(false);
        searchAllWinSearchButton.setEnabled(false);
        searchAllWinCancelButton.setEnabled(true);
    }

    /**
     * Show search all window.
     */
    private void showSearchAllWindow() {
        enableSearchAllWindow();
        cleanSearchAllWindow();
        searchAllStringsWin.pack();
        searchAllStringsWin.setLocationRelativeTo(this);
        searchAllStringsWin.setVisible(true);
    }

    /**
     * Reload table file.
     *
     * @param selectedFile the selected file
     */
    private void reloadTableFile(File selectedFile) {
        tableFile = selectedFile;
        try {
            hexTable = new HexTable(tableFile.getAbsolutePath());
        } catch (Exception e1) {
            Utils.logException(e1);
        }
        refreshAll();
    }

    /**
     * Reload offsets file.
     *
     * @param selectedFile the selected file
     */
    private void reloadOffsetsFile(File selectedFile) {
        offsetFile = selectedFile;
        try {
            offEntries = Utils.getOffsets(FileUtils.getCleanOffsets(offsetFile.getAbsolutePath()));
        } catch (Exception e1) {
            Utils.logException(e1);
        }
        refreshAll();
    }

    /**
     * Reload ext as offsets file.
     *
     * @param selectedFile the selected file
     */
    private void reloadExtAsOffsetsFile(File selectedFile) {
        offsetFile = selectedFile;
        try {
            offEntries = Utils.getOffsets(FileUtils.getCleanOffsetsString(
                    FileUtils.cleanExtractedFile(selectedFile.getAbsolutePath())));
        } catch (Exception e1) {
            Utils.logException(e1);
        }
        refreshAll();
    }

    /**
     * Reload hex file.
     *
     * @param selectedFile the selected file
     */
    private void reloadHexFile(File selectedFile) {
        hexFile = selectedFile;
        try {
            byte[] newFileBytes = Files.readAllBytes(Paths.get(hexFile.getAbsolutePath()));
            if(newFileBytes.length >= getViewSize()) {
                fileBytes = newFileBytes;
            }
            else {
                fileBytes = new byte[getViewSize()];
                System.arraycopy(newFileBytes, 0, fileBytes, 0, newFileBytes.length);
            }
        } catch (Exception e1) {
            Utils.logException(e1);
        }
        offset = 0;
        asciiTextArea.setCaretPosition(0);
        refreshAll();
    }

    /**
     * Sets the look and feel.
     */
    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Utils.logException(e);
        }
        rb = ResourceBundle.getBundle(Constants.RB_NAME, Locale.getDefault());
    }

    /**
     * New HexViewer with inputFile and tableFile.
     *
     * @param inputFile the input file
     * @param tableFile the table file
     * @throws IOException the exception
     */
    public static void view(String inputFile, String tableFile) throws IOException {
        Utils.log("Viewing Hex file \"" + inputFile + "\"\n with table file: \"" + tableFile + "\".");
        new HexViewer(Files.readAllBytes(Paths.get(inputFile)), inputFile, new HexTable(tableFile), tableFile);
    }

    /**
     * New HexViewer with inputFile and default table.
     *
     * @param inputFile the input file
     * @throws IOException the exception
     */
    public static void view(String inputFile) throws IOException {
        Utils.log("Viewing Hex file \"" + inputFile + "\"\n with table file ascii.");
        new HexViewer(Files.readAllBytes(Paths.get(inputFile)), inputFile, new HexTable(0), DEFAULT_TABLE);
    }

    /**
     * New empty HexViewer.
     */
    public static void view() {
        Utils.log("Viewing Hex file empty with table file ascii.");
        new HexViewer(new byte[MAX_COLS_AND_ROWS * MAX_COLS_AND_ROWS], DEFAULT_HEXFILE, new HexTable(0), DEFAULT_TABLE);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //None
    }

}
