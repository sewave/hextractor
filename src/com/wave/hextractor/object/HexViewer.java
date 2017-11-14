package com.wave.hextractor.object;

import java.awt.Adjustable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import com.wave.hextractor.pojo.OffsetEntry;
import com.wave.hextractor.pojo.TableSearchResult;
import com.wave.hextractor.util.Constants;
import com.wave.hextractor.util.FileUtils;
import com.wave.hextractor.util.ProjectUtils;
import com.wave.hextractor.util.Utils;

/**
 * Gui for the hextractor tools.
 * @author slcantero
 */
//TODO: refactor this
public class HexViewer extends JFrame implements ActionListener {

	/**  serialVersion UID. */
	private static final long serialVersionUID = -4438721009010549343L;

	/** The Constant DEFAULT_SPLIT_VALUE. */
	//Various constants
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

	/** The Constant OFFSET_UNIT. */
	private static final int OFFSET_UNIT = 32;

	/** The Constant OFFSET_BLOCK. */
	private static final int OFFSET_BLOCK = OFFSET_UNIT * OFFSET_UNIT;

	/** The Constant VIEW_SIZE. */
	private static final int VIEW_SIZE = OFFSET_UNIT * OFFSET_UNIT;

	/** The Constant HEX_STARTS. */
	private static final String HEX_STARTS = "0x";

	/** The Constant DEC_STARTS. */
	private static final String DEC_STARTS = "d";

	/** The Constant RB_NAME. */
	private static final String RB_NAME= "app";

	/** The Constant CURRENT_DIR. */
	private static final String CURRENT_DIR = ".";

	/** The Constant DEFAULT_TABLE. */
	private static final String DEFAULT_TABLE = "ascii.tbl";

	/** The Constant DEFAULT_HEXFILE. */
	private static final String DEFAULT_HEXFILE = "empty.hex";

	/** The Constant EXTENSION_TABLE. */
	private static final String EXTENSION_TABLE = ".tbl";

	/** The Constant EXTENSION_OFFSET. */
	private static final String EXTENSION_OFFSET = ".off";

	/** The Constant SEARCH_RES_DIMENSION. */
	private static final Dimension SEARCH_RES_DIMENSION = new Dimension(600, 200);

	/** The Constant SEARCHRES_FONT. */
	private static final String SEARCHRES_FONT = "Courier New";

	/** The Constant SEARCHRES_FONT_SIZE. */
	private static final int SEARCHRES_FONT_SIZE =  18;

	/** The Constant HEX_VALUE_SIZE. */
	private static final int HEX_VALUE_SIZE =  3;

	/** The Constant REGEXP_OFFSET_ENTRIES. */
	private static final String REGEXP_OFFSET_ENTRIES = "[0-9A-Fa-f]{2}(-[0-9A-Fa-f]{2})*";

	/** The Constant KEY_FILEMENU. */
	//Resource bundle keys
	private static final String KEY_FILEMENU = "fileMenu";

	/** The Constant KEY_YES. */
	private static final String KEY_YES ="yes";

	/** The Constant KEY_NO. */
	private static final String KEY_NO ="no";

	/** The Constant KEY_CONFIRM_EXIT. */
	private static final String KEY_CONFIRM_EXIT = "confirmExit";

	/** The Constant KEY_EXIT_MENUITEM. */
	private static final String KEY_EXIT_MENUITEM = "exitMenuItem";

	/** The Constant KEY_TABLEMENU. */
	private static final String KEY_TABLEMENU = "tableMenu";

	/** The Constant KEY_OFFSETMENU. */
	private static final String KEY_OFFSETMENU = "offsetMenu";

	/** The Constant KEY_NEXT_RANGE_MENUITEM. */
	private static final String KEY_NEXT_RANGE_MENUITEM	 = "nextRangeMenuItem";

	/** The Constant KEY_PREV_TANGE_MENUITEM. */
	private static final String KEY_PREV_TANGE_MENUITEM	 = "prevRangeMenuItem";

	/** The Constant KEY_OPEN_TABLE_MENUITEM. */
	private static final String KEY_OPEN_TABLE_MENUITEM = "openTableMenuItem";

	/** The Constant KEY_SAVE_TABLE_MENUITEM. */
	private static final String KEY_SAVE_TABLE_MENUITEM = "saveTableMenuItem";

	/** The Constant KEY_OPEN_OFFSETS_MENUITEM. */
	private static final String KEY_OPEN_OFFSETS_MENUITEM = "openOffsetsMenuItem";

	/** The Constant KEY_SAVE_OFFSETS_MENUITEM. */
	private static final String KEY_SAVE_OFFSETS_MENUITEM = "saveOffsetsMenuItem";

	/** The Constant KEY_HELP_MENU. */
	private static final String KEY_HELP_MENU = "helpMenu";

	/** The Constant KEY_ABOUT_MENUITEM. */
	private static final String KEY_ABOUT_MENUITEM = "aboutMenuItem";

	/** The Constant KEY_HELP_MENUITEM. */
	private static final String KEY_HELP_MENUITEM = "helpMenuItem";

	/** The Constant KEY_TOOLS_MENU. */
	private static final String KEY_TOOLS_MENU = "toolsMenu";

	/** The Constant KEY_GOTO_MENUITEM. */
	private static final String KEY_GOTO_MENUITEM = "goToMenuItem";

	/** The Constant KEY_SEARCH_RELATIVE_MENUITEM. */
	private static final String KEY_SEARCH_RELATIVE_MENUITEM = "searchRelativeMenuItem";

	/** The Constant KEY_OFFSET_INPUT. */
	private static final String KEY_OFFSET_INPUT = "offsetInput";

	/** The Constant KEY_SEARCH_RELATIVE. */
	private static final String KEY_SEARCH_RELATIVE = "searchRelative";

	/** The Constant KEY_SEARCH_RELATIVE_MIN_LENGTH. */
	private static final String KEY_SEARCH_RELATIVE_MIN_LENGTH = "searchRelativeMinLength";

	/** The Constant KEY_FIND_MENUITEM. */
	private static final String KEY_FIND_MENUITEM = "findMenuItem";

	/** The Constant KEY_FIND. */
	private static final String KEY_FIND = "find";

	/** The Constant KEY_FIND_MIN_LENGTH. */
	private static final String KEY_FIND_MIN_LENGTH = "findMinLength";

	/** The Constant KEY_TITLE. */
	private static final String KEY_TITLE= "appTitle";

	/** The Constant KEY_FILTER_TABLE. */
	private static final String KEY_FILTER_TABLE = "filterTable";

	/** The Constant KEY_FILTER_OFFSET. */
	private static final String KEY_FILTER_OFFSET = "filterOffset";

	/** The Constant KEY_SAVE_BUTTON. */
	private static final String KEY_SAVE_BUTTON = "saveButton";

	/** The Constant KEY_OPEN_FILE_MENUITEM. */
	private static final String KEY_OPEN_FILE_MENUITEM = "openFileMenuItem";

	/** The Constant KEY_NEW_PROJECT_MENUITEM. */
	private static final String KEY_NEW_PROJECT_MENUITEM = "newProjectMenuItem";

	/** The Constant KEY_SEARCH_RESULT_TITLE. */
	private static final String KEY_SEARCH_RESULT_TITLE = "searchResultTitle";

	/** The Constant KEY_SEARCH_RESULT_TABLE. */
	private static final String KEY_SEARCH_RESULT_TABLE = "selectSearchResTable";

	/** The Constant KEY_SEARCH_RESULT_TABLE_TITLE. */
	private static final String KEY_SEARCH_RESULT_TABLE_TITLE = "selectSearchResTableTitle";

	/** The Constant KEY_OFFSET_SET_START. */
	private static final String KEY_OFFSET_SET_START = "offsetSetStart";

	/** The Constant KEY_OFFSET_SET_END. */
	private static final String KEY_OFFSET_SET_END = "offsetSetEnd";

	/** The Constant KEY_OFFSET_DELETE. */
	private static final String KEY_OFFSET_DELETE = "offsetDelete";

	/** The Constant KEY_OFFSET_SPLIT. */
	private static final String KEY_OFFSET_SPLIT = "offsetSplit";

	/** The Constant KEY_OFFSET_SPLIT_CANCEL_TITLE. */
	private static final String KEY_OFFSET_SPLIT_CANCEL_TITLE = "offsetSplitCancelTitle";

	/** The Constant KEY_OFFSET_SPLIT_CANCEL. */
	private static final String KEY_OFFSET_SPLIT_CANCEL = "offsetSplitCancel";

	/** The Constant KEY_CONFIRM_RANGE_DELETE. */
	private static final String KEY_CONFIRM_RANGE_DELETE = "confirmRangeDelete";

	/** The Constant KEY_CONFIRM_RANGE_DELETE_TITLE. */
	private static final String KEY_CONFIRM_RANGE_DELETE_TITLE = "confirmRangeDeleteTitle";

	/** The Constant KEY_ALERT_INVALID_ENDCHARS. */
	private static final String KEY_ALERT_INVALID_ENDCHARS = "alertInvalidEndchars";

	/** The Constant KEY_ALERT_INVALID_ENDCHARS_TITLE. */
	private static final String KEY_ALERT_INVALID_ENDCHARS_TITLE = "alertInvalidEndcharsTitle";

	/** The Constant KEY_INPUT_ENDCHARS. */
	private static final String KEY_INPUT_ENDCHARS = "inputEndchars";

	/** The Constant KEY_HELP_DESC. */
	private static final String KEY_HELP_DESC = "helpDesc";

	/** The Constant KEY_ABOUT_DESC. */
	private static final String KEY_ABOUT_DESC = "aboutDesc";

	/** The Constant KEY_OFFSET_LABEL. */
	private static final String KEY_OFFSET_LABEL = "offsetLabel";

	/** The Constant KEY_NO_RESULTS_DESC. */
	private static final String KEY_NO_RESULTS_DESC = "noResultsDesc";

	/** The Constant KEY_NO_RESULTS_TITLE. */
	private static final String KEY_NO_RESULTS_TITLE = "noResultsTitle";

	/** The Constant KEY_CLEAN_OFFSETS. */
	private static final String KEY_CLEAN_OFFSETS = "clearOffsets";

	/** The Constant KEY_NEW_PRJ_TITLE. */
	private static final String KEY_NEW_PRJ_TITLE = "newProjectTitle";

	/** The Constant KEY_NEW_PRJ_NAME. */
	private static final String KEY_NEW_PRJ_NAME = "newProjectName";

	/** The Constant KEY_NEW_PRJ_FILE. */
	private static final String KEY_NEW_PRJ_FILE = "newProjectFile";

	/** The Constant KEY_NEW_PRJ_FILETYPE. */
	private static final String KEY_NEW_PRJ_FILETYPE = "newProjectTipoArchivo";

	/** The Constant KEY_NEW_PRJ_SMD. */
	private static final String KEY_NEW_PRJ_SMD = "newProjectTipoArchivoMegadrive";

	/** The Constant KEY_NEW_PRJ_SNES. */
	private static final String KEY_NEW_PRJ_SNES = "newProjectTipoArchivoSnes";

	/** The Constant KEY_NEW_PRJ_NGB. */
	private static final String KEY_NEW_PRJ_NGB = "newProjectTipoArchivoGameboy";

	/** The Constant KEY_NEW_PRJ_SPT. */
	private static final String KEY_NEW_PRJ_SPT = "newProjectTipoArchivoSpectrumTap";

	/** The Constant KEY_NEW_PRJ_SPZ. */
	private static final String KEY_NEW_PRJ_SPZ = "newProjectTipoArchivoTZX";

	/** The Constant KEY_NEW_PRJ_OTHER. */
	private static final String KEY_NEW_PRJ_OTHER= "newProjectTipoArchivoOtros";

	/** The Constant KEY_NEW_PRJ_CREA_BUT. */
	private static final String KEY_NEW_PRJ_CREA_BUT = "newProjectCreateButton";

	/** The Constant KEY_NEW_PRJ_CLOSE_BUT. */
	private static final String KEY_NEW_PRJ_CLOSE_BUT = "newProjectClose";

	/** The Constant KEY_NEW_PRJ_GENERATING_MSG. */
	private static final String KEY_NEW_PRJ_GENERATING_MSG = "newProjectMsgGenerating";

	/** The Constant KEY_NEW_PRJ_ERRORS_MSG. */
	private static final String KEY_NEW_PRJ_ERRORS_MSG = "newProjectErrors";

	/** The Constant KEY_ERROR. */
	private static final String KEY_ERROR = "error";

	/** The Constant KEY_ERROR_TITLE. */
	private static final String KEY_ERROR_TITLE = "errorTitle";

	/** The Constant KEY_CONFIRM_ACTION_TITLE. */
	private static final String KEY_CONFIRM_ACTION_TITLE = "confirmActionTitle";

	/** The Constant KEY_CONFIRM_ACTION. */
	private static final String KEY_CONFIRM_ACTION = "confirmAction";

	/** The Constant KEY_CONFIRM_FILE_OVERWRITE_ACTION. */
	private static final String KEY_CONFIRM_FILE_OVERWRITE_ACTION = "confirmReplaceFileAction";

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

	/** The offset label. */
	//GUI members
	private JLabel offsetLabel;

	/** The offset label value. */
	private JTextField offsetLabelValue;

	/** The menu bar. */
	private JMenuBar menuBar;

	/** The file menu. */
	private JMenu fileMenu;

	/** The table menu. */
	private JMenu tableMenu;

	/** The offset menu. */
	private JMenu offsetMenu;

	/** The open table. */
	private JMenuItem openTable;

	/** The save table. */
	private JMenuItem saveTable;

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

	/** The help menu. */
	private JMenu helpMenu;

	/** The about menu item. */
	private JMenuItem aboutMenuItem;

	/** The help item. */
	private JMenuItem helpItem;

	/** The tools menu. */
	private JMenu toolsMenu;

	/** The go to. */
	private JMenuItem goTo;

	/** The search relative. */
	private JMenuItem searchRelative;

	/** The clear offsets. */
	private JMenuItem clearOffsets;

	/** The find. */
	private JMenuItem find;

	/** The rb. */
	private ResourceBundle rb;

	/** The hex text area. */
	private JTextArea hexTextArea;

	/** The ascii text area. */
	private JTextArea asciiTextArea;

	/** The offsets text area. */
	private JTextArea offsetsTextArea;

	/** The v list. */
	private VerticalListener vList;

	/** The vsb. */
	private JScrollBar vsb;

	/** The p mouse wheel listener. */
	private PanelMouseWheelListener pMouseWheelListener;

	/** The key listener. */
	private KeyListener keyListener;

	/** The first row. */
	private JPanel firstRow;

	/** The second row. */
	private JPanel secondRow;

	/** The table filter. */
	private SimpleFilter tableFilter;

	/** The offset file filter. */
	private SimpleFilter offsetFileFilter;

	/** The results window. */
	private JFrame resultsWindow;

	/** The search results. */
	private JList<TableSearchResult> searchResults;

	/** The new project window. */
	private JFrame newProjectWindow;

	/** The new project window name label. */
	private JLabel newProjectWindowNameLabel;

	/** The new project window name input. */
	private JTextField newProjectWindowNameInput;

	/** The new project window file label. */
	private JLabel newProjectWindowFileLabel;

	/** The new project window file input. */
	private JTextField newProjectWindowFileInput;

	/** The new project window file type label. */
	private JLabel newProjectWindowFileTypeLabel;

	/** The new project window file type options. */
	private JComboBox<Entry<String, String>> newProjectWindowFileTypeOptions;

	/** The new project window search file button. */
	private JButton newProjectWindowSearchFileButton;

	/** The new project window create button. */
	private JButton newProjectWindowCreateButton;

	/** The new project window cancel button. */
	private JButton newProjectWindowCancelButton;

	/**
	 * Instantiates a new hex viewer.
	 *
	 * @param fileBytes the file bytes
	 * @param fileName the file name
	 * @param hexTable the hex table
	 * @param tableName the table name
	 */
	public HexViewer(byte[] fileBytes, String fileName, HexTable hexTable, String tableName) {
		this.hexFile = new File(fileName);
		this.fileBytes = fileBytes;
		this.hexTable = hexTable;
		String projectName = getProjectName();
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
	 * Gets the project name.
	 *
	 * @return the project name
	 */
	private String getProjectName() {
		try {
			return new File(CURRENT_DIR).getCanonicalFile().getName();
		} catch (IOException e) {
			e.printStackTrace();
			return Constants.EMPTY;
		}
	}

	/**
	 * The listener interface for receiving panelMouseWheel events.
	 * The class that is interested in processing a panelMouseWheel
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addPanelMouseWheelListener<code> method. When
	 * the panelMouseWheel event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see PanelMouseWheelEvent
	 */
	class PanelMouseWheelListener implements MouseWheelListener {

		/* (non-Javadoc)
		 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
		 */
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getWheelRotation() < 0) {
				if(offset > OFFSET_UNIT) {
					offset -= OFFSET_UNIT;
				}
			} else {
				if(offset < fileBytes.length - VIEW_SIZE) {
					offset += OFFSET_UNIT;
				}
			}
			refreshAll();
		}
	}

	/**
	 * The listener interface for receiving panelKey events.
	 * The class that is interested in processing a panelKey
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addPanelKeyListener<code> method. When
	 * the panelKey event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see PanelKeyEvent
	 */
	class PanelKeyListener implements KeyListener {

		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyPressed(KeyEvent keyEvent) {
			//Estas se autorepiten
			switch (keyEvent.getKeyCode()) {
			case KeyEvent.VK_PAGE_DOWN:
				offset += OFFSET_BLOCK;
				break;
			case KeyEvent.VK_PAGE_UP:
				offset -= OFFSET_BLOCK;
				break;
			case KeyEvent.VK_END:
				if((keyEvent.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK ) != 0) {
					offset = (fileBytes.length - VIEW_SIZE);
				}
				else {
					OffsetEntry selectedEntry = getCaretEntry();
					if(currEntry.start > 0 || selectedEntry != currEntry) {
						endItemAction(selectedEntry);
					}
				}
				break;
			case KeyEvent.VK_HOME:
				if((keyEvent.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
					offset = 0;
				}
				else {
					getCaretEntry().setStart(offset + asciiTextArea.getCaretPosition());
				}
				break;
			case KeyEvent.VK_INSERT:
				OffsetEntry selectedEntry = getCaretEntry();
				if(selectedEntry != currEntry) {
					splitItemAction(selectedEntry);
				}
				break;
			case KeyEvent.VK_DELETE:
				OffsetEntry selectedEntryDel = getCaretEntry();
				if(selectedEntryDel != currEntry) {
					deleteItemAction(selectedEntryDel);
				}
				break;
			}
			refreshAll();
		}

		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyReleased(KeyEvent keyEvent) {
		}

		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyTyped(KeyEvent keyEvent) {
		}
	}

	/**
	 * The listener interface for receiving panelSelection events.
	 * The class that is interested in processing a panelSelection
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addPanelSelectionListener<code> method. When
	 * the panelSelection event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see PanelSelectionEvent
	 */
	class PanelSelectionListener implements CaretListener {

		/* (non-Javadoc)
		 * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
		 */
		@Override
		public void caretUpdate(CaretEvent e) {
			refreshSelection();
		}
	}

	/**
	 * Refresh selection.
	 */
	private void refreshSelection() {
		HighlightPainter bluePainter = new DefaultHighlighter.DefaultHighlightPainter(Color.BLUE);
		HighlightPainter lightGrayPainter= new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY);
		HighlightPainter yellowPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
		HighlightPainter orangePainter = new DefaultHighlighter.DefaultHighlightPainter(Color.ORANGE);

		if(hexTextArea.getText().length() > asciiTextArea.getCaretPosition() * HEX_VALUE_SIZE) {
			hexTextArea.setCaretPosition(asciiTextArea.getCaretPosition() * HEX_VALUE_SIZE);
		}
		else {
			hexTextArea.setCaretPosition(hexTextArea.getText().length());
		}
		Highlighter highlighterHex = hexTextArea.getHighlighter();
		highlighterHex.removeAllHighlights();
		try {
			highlighterHex.addHighlight(hexTextArea.getCaretPosition(), hexTextArea.getCaretPosition() + HEX_VALUE_SIZE, bluePainter);
		} catch (BadLocationException e1) {
		}
		Highlighter highlighterAscii = asciiTextArea.getHighlighter();
		highlighterAscii.removeAllHighlights();
		try {
			highlighterAscii.addHighlight(asciiTextArea.getCaretPosition(), asciiTextArea.getCaretPosition() + 1, bluePainter);
			for (OffsetEntry entry : offEntries) {
				drawOffsetEntry(entry, highlighterAscii, lightGrayPainter, orangePainter);
			}
			if(currEntry.getStart() > 0 && currEntry.getStart() - offset >= 0) {
				highlighterAscii.addHighlight(currEntry.getStart() - offset, currEntry.getStart() - offset + 1, yellowPainter);
			}
			if(currEntry.getEnd() > 0 && currEntry.getEnd() - offset >= 0) {
				highlighterAscii.addHighlight(currEntry.getEnd() - offset, currEntry.getEnd() - offset + 1, orangePainter);
			}
		} catch (BadLocationException e1) {
		}
		offsetLabelValue.setText(Utils.intToHexString(offset + asciiTextArea.getCaretPosition(), Constants.HEX_ADDR_SIZE));
		//Offset highlight
		//		Highlighter highlighterOffset = offsetsTextArea.getHighlighter();
		//		highlighterOffset.removeAllHighlights();
		//		try {
		//			highlighterOffset.addHighlight(asciiTextArea.getCaretPosition() / OFFSET_UNIT * (Constants.HEX_ADDR_SIZE + HEX_STARTS.length() + Constants.SPACE_STR.length() + Constants.SPACE_STR.length()),
		//					asciiTextArea.getCaretPosition() / OFFSET_UNIT * (Constants.HEX_ADDR_SIZE + HEX_STARTS.length() + Constants.SPACE_STR.length() + Constants.SPACE_STR.length()) + Constants.HEX_ADDR_SIZE + HEX_STARTS.length() + Constants.SPACE_STR.length() + Constants.SPACE_STR.length(), grayPainter );
		//		} catch (BadLocationException e1) {
		//		}
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
		if(entry.getStart() <= offset + VIEW_SIZE && entry.getEnd() >= offset) {
			int start = entry.getStart();
			int end = entry.getEnd();
			if(start < offset) {
				start = offset;
			}
			if(end >= offset + VIEW_SIZE) {
				end = offset + VIEW_SIZE - 1;
			}
			highlighter.addHighlight(start - offset + 1, end - offset, painter );
			highlighter.addHighlight(start - offset, start - offset + 1, borderPainter );
			highlighter.addHighlight(end - offset, end - offset + 1, borderPainter );
		}
	}

	/**
	 * The Class SimpleFilter.
	 */
	class SimpleFilter extends FileFilter {

		/** The ext. */
		String ext;

		/** The message. */
		String message;

		/**
		 * Instantiates a new simple filter.
		 *
		 * @param ext the ext
		 * @param message the message
		 */
		public SimpleFilter(String ext, String message) {
			this.ext = ext;
			this.message = message;
		}

		/* (non-Javadoc)
		 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
		 */
		@Override
		public boolean accept(File selectedFile) {
			return selectedFile.getName().endsWith(ext) || selectedFile.isDirectory();
		}

		/* (non-Javadoc)
		 * @see javax.swing.filechooser.FileFilter#getDescription()
		 */
		@Override
		public String getDescription() {
			return message;
		}
	}

	/**
	 * The listener interface for receiving vertical events.
	 * The class that is interested in processing a vertical
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addVerticalListener<code> method. When
	 * the vertical event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see VerticalEvent
	 */
	class VerticalListener implements AdjustmentListener {

		/* (non-Javadoc)
		 * @see java.awt.event.AdjustmentListener#adjustmentValueChanged(java.awt.event.AdjustmentEvent)
		 */
		public void adjustmentValueChanged(AdjustmentEvent evt) {
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
					offset += OFFSET_UNIT;
					break;
				case AdjustmentEvent.UNIT_DECREMENT:
					offset -= OFFSET_UNIT;
					break;
				case AdjustmentEvent.BLOCK_INCREMENT:
					offset += OFFSET_BLOCK;
					break;
				case AdjustmentEvent.BLOCK_DECREMENT:
					offset -= OFFSET_BLOCK;
					break;
				case AdjustmentEvent.TRACK:
					offset = value;
					break;
				}
				asciiTextArea.setText(getTextArea(offset, VIEW_SIZE));
				hexTextArea.setText(getHexArea(offset, VIEW_SIZE));
				asciiTextArea.setCaretPosition(asciiTextArea.getText().length());
			}
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
		public PopUpOffsetEntry(OffsetEntry entry){
			selectedEntry = entry;
			startItem = new JMenuItem();
			startItem.setAction(new AbstractAction(rb.getString(KEY_OFFSET_SET_START)) {
				private static final long serialVersionUID = 251417879942401217L;

				@Override
				public void actionPerformed(ActionEvent e) {
					selectedEntry.setStart(offset + asciiTextArea.getCaretPosition());
					refreshAll();
				}
			});
			add(startItem);
			if(currEntry.start > 0 || selectedEntry != currEntry) {
				endItem = new JMenuItem();
				endItem.setAction(new AbstractAction(rb.getString(KEY_OFFSET_SET_END)) {
					private static final long serialVersionUID = 251427879942401217L;

					@Override
					public void actionPerformed(ActionEvent e) {
						endItemAction(selectedEntry);
						refreshAll();
					}
				});
				add(endItem);
			}
			//Hemos caido en una entry existente, tendrá opcion de borrar o hacer split
			if(currEntry != selectedEntry) {
				splitItem = new JMenuItem();
				splitItem.setAction(new AbstractAction(rb.getString(KEY_OFFSET_SPLIT)) {
					private static final long serialVersionUID = 251427879942401214L;

					@Override
					public void actionPerformed(ActionEvent e) {
						splitItemAction(selectedEntry);
					}
				});
				add(splitItem);
				deleteItem = new JMenuItem();
				deleteItem.setAction(new AbstractAction(rb.getString(KEY_OFFSET_DELETE)) {
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
		int selectedOption = JOptionPane.showConfirmDialog(null,
				rb.getString(KEY_CONFIRM_RANGE_DELETE) + selectedEntry.toEntryString(),
				rb.getString(KEY_CONFIRM_RANGE_DELETE_TITLE),
				JOptionPane.YES_NO_OPTION);
		if(selectedOption == JOptionPane.YES_OPTION){
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
		String valor = JOptionPane.showInputDialog(rb.getString(KEY_OFFSET_SPLIT), DEFAULT_SPLIT_VALUE);
		if(valor != null) {
			try {
				minMaxLength = Integer.parseInt(valor);
			} catch (NumberFormatException e1) {
				JOptionPane.showMessageDialog(helpItem, rb.getString(KEY_OFFSET_SPLIT_CANCEL),
						rb.getString(KEY_OFFSET_SPLIT_CANCEL_TITLE), JOptionPane.INFORMATION_MESSAGE);
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
		String result = JOptionPane.showInputDialog(rb.getString(KEY_INPUT_ENDCHARS), lastSelectedEndChars);
		if(result != null && result.length() > 0) {
			if(result.matches(REGEXP_OFFSET_ENTRIES)) {
				lastSelectedEndChars = result;
				selectedEntry.setEndChars(Arrays.asList(
						lastSelectedEndChars.toUpperCase().replaceAll(Constants.SPACE_STR, Constants.EMPTY)
						.split(Constants.OFFSET_CHAR_SEPARATOR)));
				selectedEntry.setEnd(offset + asciiTextArea.getCaretPosition());
				if((selectedEntry.getStart() > 0 || selectedEntry.getEnd() > 0)) {
					if(selectedEntry == currEntry) {
						currEntry.mergeInto(offEntries);
						currEntry = new OffsetEntry();
						currEntry.start = -1;
						currEntry.end = -1;
					}
				}
			}
			else {
				JOptionPane.showMessageDialog(asciiTextArea.getParent(),
						rb.getString(KEY_ALERT_INVALID_ENDCHARS),
						rb.getString(KEY_ALERT_INVALID_ENDCHARS_TITLE), JOptionPane.ERROR_MESSAGE);
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
		String ObjButtons[] = {rb.getString(KEY_YES), rb.getString(KEY_NO)};
		int PromptResult = JOptionPane.showOptionDialog(null, rb.getString(KEY_CONFIRM_EXIT),
				rb.getString(KEY_TITLE), JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons,
				ObjButtons[1]);
		if(PromptResult==JOptionPane.YES_OPTION)
		{
			System.exit(0);
		}
	}

	/**
	 * Creates the frame.
	 */
	private void createFrame() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we)
			{
				closeApp();
			}
		});
		setLookAndFeel();
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		createMenu();
		firstRow = new JPanel();
		firstRow.setLayout(new FlowLayout());
		secondRow = new JPanel();
		secondRow.setLayout(new FlowLayout(FlowLayout.LEADING));
		add(firstRow);
		add(secondRow);

		offsetsTextArea = new JTextArea(OFFSET_UNIT, Constants.HEX_ADDR_SIZE);
		offsetsTextArea.setLineWrap(Boolean.TRUE);
		offsetsTextArea.setBackground(Color.BLACK);
		offsetsTextArea.setForeground(Color.WHITE);
		offsetsTextArea.setEditable(false);
		offsetsTextArea.setDisabledTextColor(Color.WHITE);
		offsetsTextArea.setEnabled(false);
		firstRow.add(offsetsTextArea);
		offsetsTextArea.setText(getVisibleOffsets(offset));

		hexTextArea = new JTextArea(OFFSET_UNIT, OFFSET_UNIT * HEX_VALUE_SIZE);
		hexTextArea.setLineWrap(Boolean.TRUE);
		hexTextArea.setBackground(Color.BLACK);
		hexTextArea.setForeground(Color.WHITE);
		hexTextArea.setEditable(false);
		hexTextArea.setDisabledTextColor(Color.WHITE);
		hexTextArea.setEnabled(false);
		hexTextArea.setCaretColor(Color.GRAY);
		firstRow.add(hexTextArea);
		hexTextArea.setText(getHexArea(offset, VIEW_SIZE));

		asciiTextArea = new JTextArea(OFFSET_UNIT, OFFSET_UNIT);
		asciiTextArea.setLineWrap(Boolean.TRUE);
		asciiTextArea.setBackground(Color.BLACK);
		asciiTextArea.setForeground(Color.WHITE);
		asciiTextArea.setDisabledTextColor(Color.WHITE);
		asciiTextArea.setEnabled(false);
		asciiTextArea.setEditable(false);
		asciiTextArea.setCaretColor(Color.GRAY);
		firstRow.add(asciiTextArea);
		asciiTextArea.setText(getTextArea(offset, VIEW_SIZE));
		asciiTextArea.addCaretListener(new PanelSelectionListener());
		asciiTextArea.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					doPop(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					doPop(e);
				}
			}
			private void doPop(MouseEvent e) {
				asciiTextArea.setCaretPosition(e.getX() / 8 + (e.getY() / 18) * OFFSET_UNIT);
				PopUpOffsetEntry menu = new PopUpOffsetEntry(getCaretEntry());
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		});

		vsb = new JScrollBar();
		vList = new VerticalListener();
		vsb.addAdjustmentListener(vList);
		vsb.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				offset = vsb.getValue();
				refreshAll();
			}
		});

		resultsWindow = new JFrame(rb.getString(KEY_SEARCH_RESULT_TITLE));
		resultsWindow.setLayout(new FlowLayout());
		searchResults = new JList<TableSearchResult>(new TableSearchResult[0]);
		searchResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		searchResults.setLayoutOrientation(JList.VERTICAL);
		searchResults.setVisibleRowCount(8);
		searchResults.setFont(new Font(SEARCHRES_FONT, Font.PLAIN, SEARCHRES_FONT_SIZE));
		JScrollPane listScroller = new JScrollPane(searchResults);
		listScroller.setPreferredSize(SEARCH_RES_DIMENSION);
		resultsWindow.add(listScroller);
		resultsWindow.pack();
		resultsWindow.setResizable(Boolean.FALSE);

		newProjectWindow = new JFrame(rb.getString(KEY_NEW_PRJ_TITLE));
		newProjectWindow.setLayout(new GridLayout(NEW_PROJECT_GRID_ROWS, NEW_PROJECT_GRID_COLS, NEW_PROJECT_GRID_HGAP, NEW_PROJECT_GRID_VGAP));
		newProjectWindowNameLabel = new JLabel(rb.getString(KEY_NEW_PRJ_NAME), SwingConstants.LEFT);
		newProjectWindowFileLabel = new JLabel(rb.getString(KEY_NEW_PRJ_FILE), SwingConstants.LEFT);
		newProjectWindowFileTypeLabel = new JLabel(rb.getString(KEY_NEW_PRJ_FILETYPE), SwingConstants.LEFT);
		newProjectWindowNameInput = new JTextField(30);
		newProjectWindowFileInput = new JTextField();
		newProjectWindowFileTypeOptions = new JComboBox<Map.Entry<String, String>>();
		otherEntry = new AbstractMap.SimpleEntry<String, String>(rb.getString(KEY_NEW_PRJ_OTHER), Constants.FILE_TYPE_OTHER);
		smdEntry = new AbstractMap.SimpleEntry<String, String>(rb.getString(KEY_NEW_PRJ_SMD), Constants.FILE_TYPE_MEGADRIVE);
		snesEntry = new AbstractMap.SimpleEntry<String, String>(rb.getString(KEY_NEW_PRJ_SNES), Constants.FILE_TYPE_SNES);
		gbEntry = new AbstractMap.SimpleEntry<String, String>(rb.getString(KEY_NEW_PRJ_NGB), Constants.FILE_TYPE_NGB);
		tapEntry = new AbstractMap.SimpleEntry<String, String>(rb.getString(KEY_NEW_PRJ_SPT), Constants.FILE_TYPE_ZXTAP);
		tzxEntry = new AbstractMap.SimpleEntry<String, String>(rb.getString(KEY_NEW_PRJ_SPZ), Constants.FILE_TYPE_TZX);
		newProjectWindowFileTypeOptions.addItem(otherEntry);
		newProjectWindowFileTypeOptions.addItem(smdEntry);
		newProjectWindowFileTypeOptions.addItem(gbEntry);
		newProjectWindowFileTypeOptions.addItem(snesEntry);
		newProjectWindowFileTypeOptions.addItem(tapEntry);
		newProjectWindowFileTypeOptions.addItem(tzxEntry);
		newProjectWindow.add(newProjectWindowFileLabel);
		newProjectWindow.add(newProjectWindowFileInput);
		newProjectWindow.add(newProjectWindowSearchFileButton);
		newProjectWindow.add(newProjectWindowNameLabel);
		newProjectWindow.add(newProjectWindowNameInput);
		newProjectWindow.add(new JLabel());
		newProjectWindow.add(newProjectWindowFileTypeLabel);
		newProjectWindow.add(newProjectWindowFileTypeOptions);
		newProjectWindow.add(new JLabel());
		newProjectWindow.add(new JLabel());
		newProjectWindow.add(newProjectWindowCreateButton);
		newProjectWindow.add(newProjectWindowCancelButton);
		newProjectWindow.pack();
		newProjectWindow.setResizable(Boolean.FALSE);

		new FileDrop(newProjectWindow, new FileDrop.Listener() {
			public void filesDropped(java.io.File[] files) {
				if(files != null) {
					if(files.length > 1) {
						newProjectWindow.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						disableProjectWindow();
						newProjectWindow.repaint();
						for(File file : files) {
							try {
								ProjectUtils.createProject(file);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						newProjectWindow.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						newProjectWindow.setVisible(false);
						JOptionPane.showMessageDialog(asciiTextArea.getParent(),
								rb.getString(KEY_NEW_PRJ_GENERATING_MSG),
								rb.getString(KEY_NEW_PRJ_GENERATING_MSG), JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						for(File file : files) {
							newProjectWindowFileInput.setText(file.getName());
							projectFile = file;
							selectProjectFileType(projectFile);
							newProjectWindowNameInput.setText(ProjectUtils.getProjectName(projectFile.getName()));
						}
					}
				}
			}
		});

		newProjectWindowFileInput.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				cleanFile();
			}

			public void removeUpdate(DocumentEvent e) {
				cleanFile();
			}

			public void insertUpdate(DocumentEvent e) {
				cleanFile();
			}

			public void cleanFile() {
				projectFile = null;
			}
		});

		searchResults.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				@SuppressWarnings("unchecked")
				JList<TableSearchResult> list = (JList<TableSearchResult>) evt.getSource();
				TableSearchResult tsr = list.getSelectedValue();

				if(evt.getClickCount() == 1 && tsr != null) {
					if(SwingUtilities.isLeftMouseButton(evt)) {
						offset = tsr.getOffset();
						refreshAll();
					}
					if(SwingUtilities.isRightMouseButton(evt)) {
						int selectedOption = JOptionPane.showConfirmDialog(null,
								rb.getString(KEY_SEARCH_RESULT_TABLE),
								rb.getString(KEY_SEARCH_RESULT_TABLE_TITLE),
								JOptionPane.YES_NO_OPTION);
						if(selectedOption == JOptionPane.YES_OPTION){
							offset = tsr.getOffset();
							hexTable = tsr.getHexTable();
							refreshAll();
						}
					}
				}
			}
		});

		new FileDrop(this, new FileDrop.Listener() {
			public void filesDropped(java.io.File[] files) {
				if(files != null) {
					for(File file : files) {
						if(file.getAbsolutePath().endsWith(EXTENSION_TABLE)) {
							reloadTableFile(file);
						} else {
							if(file.getAbsolutePath().endsWith(EXTENSION_OFFSET)) {
								reloadOffsetsFile(file);
							} else {
								reloadHexFile(file);
							}
						}
					}
				}
			}
		});

		firstRow.add(vsb);

		offsetLabel = new JLabel(rb.getString(KEY_OFFSET_LABEL));
		secondRow.add(offsetLabel);
		offsetLabelValue = new JTextField(Utils.intToHexString(offset, Constants.HEX_ADDR_SIZE), Constants.HEX_ADDR_SIZE);
		offsetLabelValue.setEnabled(false);
		secondRow.add(offsetLabelValue);

		offsetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		offsetLabelValue.setAlignmentX(Component.LEFT_ALIGNMENT);

		pMouseWheelListener = new PanelMouseWheelListener();
		addMouseWheelListener(pMouseWheelListener);
		keyListener = new PanelKeyListener();
		addKeyListener(keyListener);
		offEntries = new ArrayList<OffsetEntry>();
		currEntry = new OffsetEntry();
		currEntry.start = -1;
		currEntry.end = -1;
		setResizable(Boolean.FALSE);
		pack();
		vsb.setPreferredSize(new Dimension((int) vsb.getPreferredSize().getWidth(), (int) (getSize().getHeight() * 0.85)));
		setVisible(true);
		refreshAll();
	}

	/**
	 * Gets the visible offsets.
	 *
	 * @param offset2 the offset 2
	 * @return the visible offsets
	 */
	private String getVisibleOffsets(int offset2) {
		StringBuffer sb = new StringBuffer((Constants.HEX_ADDR_SIZE + HEX_STARTS.length()) * OFFSET_UNIT);
		for(int i = 0; i < OFFSET_UNIT; i++) {
			sb.append(HEX_STARTS);
			sb.append(Utils.intToHexString(offset + i * OFFSET_UNIT, Constants.HEX_ADDR_SIZE));
			sb.append(Constants.SPACE_STR);
			sb.append(Constants.SPACE_STR);
		}
		return sb.toString();
	}

	/**
	 * Refresh title.
	 */
	private void refreshTitle() {
		setTitle(rb.getString(KEY_TITLE) + " [" + hexFile +"] - [" + tableFile.getName() + "] - [" + offsetFile.getName() + "]" );
	}

	/**
	 * Refresh all.
	 */
	private void refreshAll() {
		Collections.sort(offEntries);
		if(offset > fileBytes.length - VIEW_SIZE) {
			offset = fileBytes.length - VIEW_SIZE;
		}
		if(offset < 0) {
			offset = 0;
		}
		asciiTextArea.setText(getTextArea(offset, VIEW_SIZE));
		hexTextArea.setText(getHexArea(offset, VIEW_SIZE));
		offsetsTextArea.setText(getVisibleOffsets(offset));
		vsb.setMinimum(0);
		vsb.setMaximum(fileBytes.length - VIEW_SIZE);
		vsb.setUnitIncrement(OFFSET_UNIT);
		vsb.setBlockIncrement(VIEW_SIZE);
		vsb.setValue(offset);
		refreshTitle();
		refreshSelection();
		asciiTextArea.repaint();
		hexTextArea.repaint();
		offsetsTextArea.repaint();
	}

	/**
	 * Creates the menu.
	 */
	private void createMenu() {
		//Create objects
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		//Menus
		fileMenu = new JMenu(rb.getString(KEY_FILEMENU));
		tableMenu = new JMenu(rb.getString(KEY_TABLEMENU));
		offsetMenu = new JMenu(rb.getString(KEY_OFFSETMENU));
		toolsMenu = new JMenu(rb.getString(KEY_TOOLS_MENU));
		helpMenu = new JMenu(rb.getString(KEY_HELP_MENU));

		//Items
		exit =  new JMenuItem(rb.getString(KEY_EXIT_MENUITEM));
		openFile = new JMenuItem(rb.getString(KEY_OPEN_FILE_MENUITEM));
		newProject = new JMenuItem(rb.getString(KEY_NEW_PROJECT_MENUITEM));
		openTable = new JMenuItem(rb.getString(KEY_OPEN_TABLE_MENUITEM));
		saveTable = new JMenuItem(rb.getString(KEY_SAVE_TABLE_MENUITEM));
		aboutMenuItem = new JMenuItem(rb.getString(KEY_ABOUT_MENUITEM));
		helpItem = new JMenuItem(rb.getString(KEY_HELP_MENUITEM));
		goTo = new JMenuItem(rb.getString(KEY_GOTO_MENUITEM));
		searchRelative = new JMenuItem(rb.getString(KEY_SEARCH_RELATIVE_MENUITEM));
		find = new JMenuItem(rb.getString(KEY_FIND_MENUITEM));
		openOffsets = new JMenuItem(rb.getString(KEY_OPEN_OFFSETS_MENUITEM));
		saveOffsets = new JMenuItem(rb.getString(KEY_SAVE_OFFSETS_MENUITEM));
		nextOffset = new JMenuItem(rb.getString(KEY_NEXT_RANGE_MENUITEM));
		prevOffset = new JMenuItem(rb.getString(KEY_PREV_TANGE_MENUITEM));
		clearOffsets = new JMenuItem(rb.getString(KEY_CLEAN_OFFSETS));

		tableFilter = new SimpleFilter(EXTENSION_TABLE, rb.getString(KEY_FILTER_TABLE));
		offsetFileFilter = new SimpleFilter(EXTENSION_OFFSET, rb.getString(KEY_FILTER_OFFSET));

		//Setup menu
		fileMenu.add(openFile);
		fileMenu.add(newProject);
		fileMenu.add(exit);

		tableMenu.add(openTable);
		tableMenu.add(saveTable);

		offsetMenu.add(openOffsets);
		offsetMenu.add(saveOffsets);
		offsetMenu.add(nextOffset);
		offsetMenu.add(prevOffset);
		offsetMenu.add(clearOffsets);

		toolsMenu.add(goTo);
		toolsMenu.add(searchRelative);
		toolsMenu.add(find);
		helpMenu.add(helpItem);
		helpMenu.add(aboutMenuItem);

		menuBar.add(fileMenu);
		menuBar.add(tableMenu);
		menuBar.add(offsetMenu);
		menuBar.add(toolsMenu);
		menuBar.add(helpMenu);

		//Actions
		setActions();

		//Accelerators
		openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		newProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
		goTo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK));
		openTable.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK));
		saveTable.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		searchRelative.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
		find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
		openOffsets.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
		saveOffsets.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
		nextOffset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
		prevOffset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		clearOffsets.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK));
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
		Collections.sort(offEntries, Collections.reverseOrder());
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
		int selectedOption = JOptionPane.showConfirmDialog(null,
				rb.getString(KEY_CONFIRM_ACTION),
				rb.getString(KEY_CONFIRM_ACTION_TITLE),
				JOptionPane.YES_NO_OPTION);
		if(selectedOption == JOptionPane.YES_OPTION){
			offEntries.clear();
			offsetFile = new File(Constants.EMPTY_OFFSET_FILE);
			refreshAll();
		}
	}

	/**
	 * Sets the actions.
	 */
	private void setActions() {
		helpItem.setAction(new AbstractAction(rb.getString(KEY_HELP_MENUITEM)) {
			/**
			 *
			 */
			private static final long serialVersionUID = 251407879942401229L;
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(helpItem, rb.getString(KEY_HELP_DESC),
						rb.getString(KEY_HELP_MENUITEM), JOptionPane.INFORMATION_MESSAGE);
			}
		});
		aboutMenuItem.setAction(new AbstractAction(rb.getString(KEY_ABOUT_MENUITEM)) {
			/**
			 *
			 */
			private static final long serialVersionUID = 251407879942401229L;
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(aboutMenuItem, rb.getString(KEY_ABOUT_DESC) ,
						rb.getString(KEY_ABOUT_MENUITEM), JOptionPane.INFORMATION_MESSAGE);
			}
		});
		nextOffset.setAction(new AbstractAction(rb.getString(KEY_NEXT_RANGE_MENUITEM)) {
			/**
			 *
			 */
			private static final long serialVersionUID = 251407879942401229L;
			@Override
			public void actionPerformed(ActionEvent e) {
				nextOffset();
			}
		});
		prevOffset.setAction(new AbstractAction(rb.getString(KEY_PREV_TANGE_MENUITEM)) {
			/**
			 *
			 */
			private static final long serialVersionUID = 251407879942401239L;
			@Override
			public void actionPerformed(ActionEvent e) {
				prevOffset();
			}
		});
		clearOffsets.setAction(new AbstractAction(rb.getString(KEY_CLEAN_OFFSETS)) {
			/**
			 *
			 */
			private static final long serialVersionUID = 251407879942401239L;
			@Override
			public void actionPerformed(ActionEvent e) {
				cleanOffsets();
			}
		});
		exit.setAction(new AbstractAction(rb.getString(KEY_EXIT_MENUITEM)) {
			/**
			 *
			 */
			private static final long serialVersionUID = 251407879942401219L;
			@Override
			public void actionPerformed(ActionEvent e) {
				closeApp();
			}
		});
		openFile.setAction(new AbstractAction(rb.getString(KEY_OPEN_FILE_MENUITEM)) {
			/**
			 *
			 */
			private static final long serialVersionUID = 251407879942401219L;
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				File parent = hexFile.getParentFile();
				if(parent == null) {
					parent = tableFile.getParentFile();
				}
				fileChooser.setCurrentDirectory(parent);
				int result = fileChooser.showOpenDialog(openFile);
				if (result == JFileChooser.APPROVE_OPTION) {
					reloadHexFile(fileChooser.getSelectedFile());
				}
			}
		});

		newProject.setAction(new AbstractAction(rb.getString(KEY_NEW_PROJECT_MENUITEM)) {
			/**
			 *
			 */
			private static final long serialVersionUID = 251417879942401219L;
			@Override
			public void actionPerformed(ActionEvent e) {
				createProjectWindow();
			}
		});

		goTo.setAction(new AbstractAction(rb.getString(KEY_GOTO_MENUITEM)) {
			/**
			 *
			 */
			private static final long serialVersionUID = 251407879942401217L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String s = JOptionPane.showInputDialog(rb.getString(KEY_OFFSET_INPUT));
				if(s != null && s.length() > 0) {
					try {
						if (s.startsWith(DEC_STARTS)) {
							offset = Integer.parseInt(s.substring(DEC_STARTS.length()));
						} else {

							offset = Integer.parseInt(s, Constants.HEX_RADIX);
						}
					} catch (NumberFormatException e1) {
					}
					refreshAll();
				}
			}
		});
		searchRelative.setAction(new AbstractAction(rb.getString(KEY_SEARCH_RELATIVE_MENUITEM)) {
			/**
			 *
			 */
			private static final long serialVersionUID = 251407879942401218L;

			@Override
			public void actionPerformed(ActionEvent e) {

				String searchString = JOptionPane.showInputDialog(rb.getString(KEY_SEARCH_RELATIVE));
				if(searchString != null && searchString.length() > 0) {
					try {
						List<TableSearchResult> results = FileUtils.searchRelative8Bits(fileBytes, searchString);
						if(results.isEmpty()) {
							JOptionPane.showMessageDialog(helpItem, rb.getString(KEY_NO_RESULTS_DESC),
									rb.getString(KEY_NO_RESULTS_TITLE), JOptionPane.INFORMATION_MESSAGE);
						}
						else {
							searchResults.setListData(results.toArray(new TableSearchResult[0]));
							resultsWindow.pack();
							resultsWindow.setLocationRelativeTo(resultsWindow.getParent());
							resultsWindow.setVisible(true);
						}
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(searchRelative, rb.getString(KEY_SEARCH_RELATIVE_MIN_LENGTH));
					}
					vsb.setValue(offset);
				}
			}
		});

		find.setAction(new AbstractAction(rb.getString(KEY_FIND_MENUITEM)) {
			/**
			 *
			 */
			private static final long serialVersionUID = 251407879942401219L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String searchString = JOptionPane.showInputDialog(rb.getString(KEY_FIND));
				if(searchString != null && searchString.length() > 0) {
					try {
						List<Integer> results = FileUtils.findString(fileBytes, hexTable, searchString, true);
						if(results.isEmpty()) {
							JOptionPane.showMessageDialog(helpItem, rb.getString(KEY_NO_RESULTS_DESC),
									rb.getString(KEY_NO_RESULTS_TITLE), JOptionPane.INFORMATION_MESSAGE);
						}
						else {
							List<TableSearchResult> searchRes = new ArrayList<TableSearchResult>();
							for(Integer res : results) {
								TableSearchResult tsr = new TableSearchResult();
								tsr.setHexTable(hexTable);
								tsr.setOffset(res);
								tsr.setWord(searchString);
								searchRes.add(tsr);
							}
							searchResults.setListData(searchRes.toArray(new TableSearchResult[0]));
							resultsWindow.pack();
							resultsWindow.setLocationRelativeTo(resultsWindow.getParent());
							resultsWindow.setVisible(true);
						}
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(searchRelative, rb.getString(KEY_FIND_MIN_LENGTH));
					}
					vsb.setValue(offset);
				}
			}
		});
		openTable.setAction(new AbstractAction(rb.getString(KEY_OPEN_TABLE_MENUITEM)) {
			/**
			 *
			 */
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
		saveTable.setAction(new AbstractAction(rb.getString(KEY_SAVE_TABLE_MENUITEM)) {
			/**
			 *
			 */
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
				fileChooser.setApproveButtonText(rb.getString(KEY_SAVE_BUTTON));
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
							e1.printStackTrace();
						}
					}
					refreshAll();
				}
			}
		});
		openOffsets.setAction(new AbstractAction(rb.getString(KEY_OPEN_OFFSETS_MENUITEM)) {
			/**
			 *
			 */
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
					reloadOffsetsFile(fileChooser.getSelectedFile());
				}
			}
		});
		saveOffsets.setAction(new AbstractAction(rb.getString(KEY_SAVE_OFFSETS_MENUITEM)) {
			/**
			 *
			 */
			private static final long serialVersionUID = -1281167224371368937L;

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setSelectedFile(offsetFile);
				File parent = offsetFile.getParentFile();
				if(parent == null) {
					parent = hexFile.getParentFile();
				}
				if(parent == null) {
					parent = tableFile.getParentFile();
				}
				fileChooser.setCurrentDirectory(parent);
				fileChooser.setFileFilter(offsetFileFilter);
				fileChooser.setApproveButtonText(rb.getString(KEY_SAVE_BUTTON));
				int result = fileChooser.showSaveDialog(saveOffsets);
				if (result == JFileChooser.APPROVE_OPTION) {
					boolean accepted = confirmSelectedFile(fileChooser.getSelectedFile());
					if(accepted) {
						offsetFile = fileChooser.getSelectedFile();
						if(!offsetFile.getAbsolutePath().endsWith(EXTENSION_OFFSET)) {
							offsetFile = new File(offsetFile.getAbsolutePath() + EXTENSION_OFFSET);
						}
						try {
							Collections.sort(offEntries);
							FileUtils.writeFileAscii(offsetFile.getAbsolutePath(), Utils.toFileString(offEntries));
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					refreshAll();
				}
			}
		});

		newProjectWindowCreateButton = new JButton(rb.getString(KEY_NEW_PRJ_CREA_BUT));
		newProjectWindowCancelButton = new JButton(rb.getString(KEY_NEW_PRJ_CLOSE_BUT));
		newProjectWindowSearchFileButton = new JButton(rb.getString(KEY_FIND_MENUITEM));

		newProjectWindowSearchFileButton.setAction(new AbstractAction(rb.getString(KEY_FIND_MENUITEM)) {
			/**
			 *
			 */
			private static final long serialVersionUID = -1221167224372368937L;

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int result = fileChooser.showOpenDialog(openFile);
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					newProjectWindowFileInput.setText(file.getName());
					projectFile = file;
					selectProjectFileType(projectFile);
					newProjectWindowNameInput.setText(ProjectUtils.getProjectName(projectFile.getName()));
				}
			}
		});
		newProjectWindowCreateButton.setAction(new AbstractAction(rb.getString(KEY_NEW_PRJ_CREA_BUT)) {
			/**
			 *
			 */
			private static final long serialVersionUID = -1221167224371368937L;

			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean formErrors = false;
				try{
					String name = newProjectWindowNameInput.getText();
					String fileName = newProjectWindowFileInput.getText();
					formErrors = (name == null || name.length() < NEW_PROJECT_NAME_MIN_LENGTH ||
							fileName == null || fileName.length() == 0 ||
							!Utils.isValidFileName(fileName) || !Utils.isValidFileName(name));
					if(formErrors) {
						JOptionPane.showMessageDialog(null, rb.getString(KEY_NEW_PRJ_ERRORS_MSG), rb.getString(KEY_ERROR_TITLE), JOptionPane.ERROR_MESSAGE);
					}
					else {
						newProjectWindow.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						disableProjectWindow();
						newProjectWindow.repaint();
						ProjectUtils.createNewProject(name, fileName, ((Entry<String, String>) newProjectWindowFileTypeOptions.getSelectedItem()).getValue(), projectFile);
						newProjectWindow.setVisible(false);
						JOptionPane.showMessageDialog(asciiTextArea.getParent(),
								rb.getString(KEY_NEW_PRJ_GENERATING_MSG),
								rb.getString(KEY_NEW_PRJ_GENERATING_MSG), JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, rb.getString(KEY_ERROR), rb.getString(KEY_ERROR_TITLE), JOptionPane.ERROR_MESSAGE);
				} finally {
					newProjectWindow.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					if(!formErrors) {
						enableProjectWindow();
					}
				}
			}
		});
		newProjectWindowCancelButton.setAction(new AbstractAction(rb.getString(KEY_NEW_PRJ_CLOSE_BUT)) {
			/**
			 *
			 */
			private static final long serialVersionUID = -1221167224371368937L;
			@Override
			public void actionPerformed(ActionEvent e) {
				newProjectWindow.setVisible(false);
			}
		});
	}

	/**
	 * Confirm selected file.
	 *
	 * @param selectedFile the selected file
	 * @return true, if successful
	 */
	protected boolean confirmSelectedFile(File selectedFile) {
		boolean accepted = true;
		if (selectedFile != null && selectedFile.exists()) {
			int response = JOptionPane.showConfirmDialog(null, //
					rb.getString(KEY_CONFIRM_FILE_OVERWRITE_ACTION), //
					rb.getString(KEY_CONFIRM_ACTION_TITLE), JOptionPane.YES_NO_OPTION, //
					JOptionPane.QUESTION_MESSAGE);
			if (response != JOptionPane.YES_OPTION) {
				accepted = false;
			}
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
			newProjectWindowFileTypeOptions.setSelectedItem(otherEntry);
			if(Constants.EXTENSIONS_MEGADRIVE.contains(extension)) {
				newProjectWindowFileTypeOptions.setSelectedItem(smdEntry);
			}
			else {
				if(Constants.EXTENSIONS_SNES.contains(extension)) {
					newProjectWindowFileTypeOptions.setSelectedItem(snesEntry);
				}
				else {
					if(Constants.EXTENSIONS_GB.contains(extension)) {
						newProjectWindowFileTypeOptions.setSelectedItem(gbEntry);
					}
					else {
						if(Constants.EXTENSIONS_TAP.contains(extension)) {
							newProjectWindowFileTypeOptions.setSelectedItem(tapEntry);
						}
						else {
							if(Constants.EXTENSIONS_TZX_CDT.contains(extension)) {
								newProjectWindowFileTypeOptions.setSelectedItem(tzxEntry);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Enable project window.
	 */
	private void enableProjectWindow() {
		newProjectWindowNameInput.setEnabled(true);
		newProjectWindowFileInput.setEnabled(true);
		newProjectWindowFileTypeOptions.setEnabled(true);
		newProjectWindowCreateButton.setEnabled(true);
		newProjectWindowCancelButton.setEnabled(true);
	}

	/**
	 * Clean project window.
	 */
	private void cleanProjectWindow() {
		newProjectWindowNameInput.setText(Constants.EMPTY);
		newProjectWindowFileInput.setText(Constants.EMPTY);
		newProjectWindowFileTypeOptions.setSelectedIndex(0);
		projectFile = null;
	}

	/**
	 * Disable project window.
	 */
	private void disableProjectWindow() {
		newProjectWindowNameInput.setEnabled(false);
		newProjectWindowFileInput.setEnabled(false);
		newProjectWindowFileTypeOptions.setEnabled(false);
		newProjectWindowCreateButton.setEnabled(false);
		newProjectWindowCancelButton.setEnabled(false);
	}

	/**
	 * Creates the project window.
	 */
	private void createProjectWindow() {
		cleanProjectWindow();
		enableProjectWindow();
		newProjectWindow.pack();
		newProjectWindow.setLocationRelativeTo(this);
		newProjectWindow.setVisible(true);
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
			e1.printStackTrace();
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
			e1.printStackTrace();
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
			byte[] newFileBytes = FileUtils.getFileBytes(hexFile.getAbsolutePath());
			if(newFileBytes.length >= VIEW_SIZE) {
				fileBytes = newFileBytes;
			}
			else {
				fileBytes = new byte[VIEW_SIZE];
				System.arraycopy(newFileBytes, 0, fileBytes, 0, newFileBytes.length);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
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
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
		rb = ResourceBundle.getBundle(RB_NAME, Locale.getDefault());
	}

	/**
	 * View.
	 *
	 * @param inputFile the input file
	 * @param tableFile the table file
	 * @throws Exception the exception
	 */
	public static void view(String inputFile, String tableFile) throws Exception {
		System.out.println("Viewing Hex file \"" + inputFile + "\"\n with table file: \"" + tableFile + "\".");
		new HexViewer(FileUtils.getFileBytes(inputFile), inputFile, new HexTable(tableFile), tableFile);
	}

	/**
	 * View.
	 *
	 * @param inputFile the input file
	 * @throws Exception the exception
	 */
	public static void view(String inputFile) throws Exception {
		System.out.println("Viewing Hex file \"" + inputFile + "\"\n with table file ascii.");
		new HexViewer(FileUtils.getFileBytes(inputFile), inputFile, new HexTable(0), DEFAULT_TABLE);
	}

	/**
	 * View.
	 *
	 * @throws Exception the exception
	 */
	public static void view() throws Exception {
		System.out.println("Viewing Hex file empty with table file ascii.");
		new HexViewer(new byte[VIEW_SIZE], DEFAULT_HEXFILE, new HexTable(0), DEFAULT_TABLE);
	}

	/**
	 * Gets the text area.
	 *
	 * @param offset the offset
	 * @param length the length
	 * @return the text area
	 */
	private String getTextArea(int offset, int length) {
		StringBuffer sb = new StringBuffer(length);
		int end = offset + length;
		if(end > fileBytes.length) {
			end = fileBytes.length;
		}
		for(int i = offset; i < end; i++) {
			sb.append(hexTable.toString(fileBytes[i]));
		}
		return sb.toString();
	}

	/**
	 * Gets the hex area.
	 *
	 * @param offset the offset
	 * @param length the length
	 * @return the hex area
	 */
	private String getHexArea(int offset, int length) {
		StringBuffer sb = new StringBuffer(length * HEX_VALUE_SIZE);
		int end = offset + length;
		if(end > fileBytes.length) {
			end = fileBytes.length;
		}
		for(int i = offset; i < end; i++) {
			sb.append(String.format(Constants.HEX_16_FORMAT, fileBytes[i]));
			sb.append(Constants.SPACE_STR);
		}
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		//None
	}

}
