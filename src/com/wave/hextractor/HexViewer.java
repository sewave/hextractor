package com.wave.hextractor;

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
import java.util.Calendar;
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

/**
 * Gui for the hextractor tools.
 * @author wave
 *
 */
public class HexViewer extends JFrame implements ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = -4438721009010549343L;

	//Various constants
	private static final int DEFAULT_SPLIT_VALUE = 1024;
	private static final int NEW_PROJECT_NAME_MIN_LENGTH = 4;
	private static final int NEW_PROJECT_GRID_ROWS = 4;
	private static final int NEW_PROJECT_GRID_COLS = 3;
	private static final int NEW_PROJECT_GRID_HGAP = 10;
	private static final int NEW_PROJECT_GRID_VGAP = 10;
	private static final int OFFSET_UNIT = 32;
	private static final int OFFSET_BLOCK = OFFSET_UNIT * OFFSET_UNIT;
	private static final int VIEW_SIZE = OFFSET_UNIT * OFFSET_UNIT;
	private static final String HEX_STARTS = "0x";
	private static final String DEC_STARTS = "d";
	private static final String RB_NAME= "app";
	private static final String CURRENT_DIR = ".";
	private static final String DEFAULT_TABLE = "ascii.tbl";
	private static final String DEFAULT_HEXFILE = "empty.hex";
	private static final String EXTENSION_TABLE = ".tbl";
	private static final String EXTENSION_OFFSET = ".off";
	private static final Dimension SEARCH_RES_DIMENSION = new Dimension(600, 200);
	private static final String SEARCHRES_FONT = "Courier New";
	private static final int SEARCHRES_FONT_SIZE =  18;
	private static final int HEX_VALUE_SIZE =  3;
	private static final String REGEXP_OFFSET_ENTRIES = "[0-9A-Fa-f]{2}(-[0-9A-Fa-f]{2})*";

	private static final List<String> EXTENSIONS_MEGADRIVE = Arrays.asList(new String[]{"smd", "gen", "bin", "md", "32x"});
	private static final List<String> EXTENSIONS_SNES = Arrays.asList(new String[]{"sfc", "smc"});
	private static final List<String> EXTENSIONS_GB = Arrays.asList(new String[]{"gb", "gbc"});
	private static final List<String> EXTENSIONS_TAP = Arrays.asList(new String[]{"tap"});
	private static final List<String> EXTENSIONS_TZX_CDT = Arrays.asList(new String[]{"tzx", "cdt"});

	private static final String ECHO_OFF = "@echo off";
	private static final String PAUSE = "pause";
	private static final String PROG_CALL = "java -jar Hextractor.jar ";
	private static final String EXCUTEHEXVIEWER_FILE = "0.hexviewer.bat";
	private static final String SEARCHALL_FILE = "1.searchAll.bat";
	private static final String CLEANEXTRACTED_FILE = "2.cleanAutoExtract.bat";
	private static final String EXTRACTHEX_FILE = "3.extractHex.bat";
	private static final String ORIGINALSCRIPT_FILE = "4.originalScript.bat";
	private static final String CLEAN_FILE = "5.clean.bat";
	private static final String INSERT_FILE = "6.insert.bat";
	private static final String CREATEPATCH_FILE = "7.createPatch.bat";
	private static final String HEX_EXTENSION = ".hex";
	private static final String TR_FILENAME_PREFIX = "TR_";
	private static final String YEAR = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
	private static final String SCRIPTNAME_VAR = "%SCRIPTNAME%";
	private static final String TFILENAMENAME_VAR = "%T_FILENAME%";
	private static final String SFILENAMENAME_VAR = "%S_FILENAME%";

	//Create project
	private static final String FILE_TYPE_OTHER =  "0";
	private static final String FILE_TYPE_MEGADRIVE =  "1";
	private static final String FILE_TYPE_NGB =  "2";
	private static final String FILE_TYPE_SNES =  "3";
	private static final String FILE_TYPE_ZXTAP =  "4";
	private static final String FILE_TYPE_TZX =  "5";

	private static final String FILE_HEXTRACTOR = "Hextractor.jar";
	private static final String FILE_README = "_readme.txt";

	//Resource bundle keys
	private static final String KEY_FILEMENU = "fileMenu";
	private static final String KEY_YES ="yes";
	private static final String KEY_NO ="no";
	private static final String KEY_CONFIRM_EXIT = "confirmExit";
	private static final String KEY_EXIT_MENUITEM = "exitMenuItem";
	private static final String KEY_TABLEMENU = "tableMenu";
	private static final String KEY_OFFSETMENU = "offsetMenu";
	private static final String KEY_NEXT_RANGE_MENUITEM	 = "nextRangeMenuItem";
	private static final String KEY_PREV_TANGE_MENUITEM	 = "prevRangeMenuItem";
	private static final String KEY_OPEN_TABLE_MENUITEM = "openTableMenuItem";
	private static final String KEY_SAVE_TABLE_MENUITEM = "saveTableMenuItem";
	private static final String KEY_OPEN_OFFSETS_MENUITEM = "openOffsetsMenuItem";
	private static final String KEY_SAVE_OFFSETS_MENUITEM = "saveOffsetsMenuItem";
	private static final String KEY_HELP_MENU = "helpMenu";
	private static final String KEY_ABOUT_MENUITEM = "aboutMenuItem";
	private static final String KEY_HELP_MENUITEM = "helpMenuItem";
	private static final String KEY_TOOLS_MENU = "toolsMenu";
	private static final String KEY_GOTO_MENUITEM = "goToMenuItem";
	private static final String KEY_SEARCH_RELATIVE_MENUITEM = "searchRelativeMenuItem";
	private static final String KEY_OFFSET_INPUT = "offsetInput";
	private static final String KEY_SEARCH_RELATIVE = "searchRelative";
	private static final String KEY_SEARCH_RELATIVE_MIN_LENGTH = "searchRelativeMinLength";
	private static final String KEY_FIND_MENUITEM = "findMenuItem";
	private static final String KEY_FIND = "find";
	private static final String KEY_FIND_MIN_LENGTH = "findMinLength";
	private static final String KEY_TITLE= "appTitle";
	private static final String KEY_FILTER_TABLE = "filterTable";
	private static final String KEY_FILTER_OFFSET = "filterOffset";
	private static final String KEY_SAVE_BUTTON = "saveButton";
	private static final String KEY_OPEN_FILE_MENUITEM = "openFileMenuItem";
	private static final String KEY_NEW_PROJECT_MENUITEM = "newProjectMenuItem";
	private static final String KEY_SEARCH_RESULT_TITLE = "searchResultTitle";
	private static final String KEY_SEARCH_RESULT_TABLE = "selectSearchResTable";
	private static final String KEY_SEARCH_RESULT_TABLE_TITLE = "selectSearchResTableTitle";
	private static final String KEY_OFFSET_SET_START = "offsetSetStart";
	private static final String KEY_OFFSET_SET_END = "offsetSetEnd";
	private static final String KEY_OFFSET_DELETE = "offsetDelete";
	private static final String KEY_OFFSET_SPLIT = "offsetSplit";
	private static final String KEY_OFFSET_SPLIT_CANCEL_TITLE = "offsetSplitCancelTitle";
	private static final String KEY_OFFSET_SPLIT_CANCEL = "offsetSplitCancel";
	private static final String KEY_CONFIRM_RANGE_DELETE = "confirmRangeDelete";
	private static final String KEY_CONFIRM_RANGE_DELETE_TITLE = "confirmRangeDeleteTitle";
	private static final String KEY_ALERT_INVALID_ENDCHARS = "alertInvalidEndchars";
	private static final String KEY_ALERT_INVALID_ENDCHARS_TITLE = "alertInvalidEndcharsTitle";
	private static final String KEY_INPUT_ENDCHARS = "inputEndchars";
	private static final String KEY_HELP_DESC = "helpDesc";
	private static final String KEY_ABOUT_DESC = "aboutDesc";
	private static final String KEY_OFFSET_LABEL = "offsetLabel";
	private static final String KEY_NO_RESULTS_DESC = "noResultsDesc";
	private static final String KEY_NO_RESULTS_TITLE = "noResultsTitle";
	private static final String KEY_CLEAN_OFFSETS = "clearOffsets";
	private static final String KEY_NEW_PRJ_TITLE = "newProjectTitle";
	private static final String KEY_NEW_PRJ_NAME = "newProjectName";
	private static final String KEY_NEW_PRJ_FILE = "newProjectFile";
	private static final String KEY_NEW_PRJ_FILETYPE = "newProjectTipoArchivo";
	private static final String KEY_NEW_PRJ_SMD = "newProjectTipoArchivoMegadrive";
	private static final String KEY_NEW_PRJ_SNES = "newProjectTipoArchivoSnes";
	private static final String KEY_NEW_PRJ_NGB = "newProjectTipoArchivoGameboy";
	private static final String KEY_NEW_PRJ_SPT = "newProjectTipoArchivoSpectrumTap";
	private static final String KEY_NEW_PRJ_SPZ = "newProjectTipoArchivoTZX";
	private static final String KEY_NEW_PRJ_OTHER= "newProjectTipoArchivoOtros";
	private static final String KEY_NEW_PRJ_CREA_BUT = "newProjectCreateButton";
	private static final String KEY_NEW_PRJ_CLOSE_BUT = "newProjectClose";
	private static final String KEY_NEW_PRJ_GENERATING_MSG = "newProjectMsgGenerating";
	private static final String KEY_NEW_PRJ_ERRORS_MSG = "newProjectErrors";
	private static final String KEY_ERROR = "error";
	private static final String KEY_ERROR_TITLE = "errorTitle";

	private static final String KEY_CONFIRM_ACTION_TITLE = "confirmActionTitle";
	private static final String KEY_CONFIRM_ACTION = "confirmAction";
	private static final String KEY_CONFIRM_FILE_OVERWRITE_ACTION = "confirmReplaceFileAction";

	private SimpleEntry<String, String> otherEntry;
	private SimpleEntry<String, String> smdEntry;
	private SimpleEntry<String, String> snesEntry;
	private SimpleEntry<String, String> gbEntry;
	private SimpleEntry<String, String> tapEntry;
	private SimpleEntry<String, String> tzxEntry;

	private byte[] fileBytes;
	private HexTable hexTable;
	private int offset = 0;
	private File hexFile;
	private File tableFile;
	private File offsetFile;
	private List<OffsetEntry> offEntries;
	private OffsetEntry currEntry;
	private String lastSelectedEndChars = "FF";
	private File projectFile;

	//GUI members
	private JLabel offsetLabel;
	private JTextField offsetLabelValue;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu tableMenu;
	private JMenu offsetMenu;
	private JMenuItem openTable;
	private JMenuItem saveTable;
	private JMenuItem openOffsets;
	private JMenuItem saveOffsets;
	private JMenuItem nextOffset;
	private JMenuItem prevOffset;
	private JMenuItem openFile;
	private JMenuItem newProject;
	private JMenuItem exit;
	private JMenu helpMenu;
	private JMenuItem aboutMenuItem;
	private JMenuItem helpItem;
	private JMenu toolsMenu;
	private JMenuItem goTo;
	private JMenuItem searchRelative;
	private JMenuItem clearOffsets;
	private JMenuItem find;
	private ResourceBundle rb;
	private JTextArea hexTextArea;
	private JTextArea asciiTextArea;
	private JTextArea offsetsTextArea;
	private VerticalListener vList;
	private JScrollBar vsb;
	private PanelMouseWheelListener pMouseWheelListener;
	private KeyListener keyListener;
	private JPanel firstRow;
	private JPanel secondRow;
	private SimpleFilter tableFilter;
	private SimpleFilter offsetFileFilter;
	private JFrame resultsWindow;
	private JList<TableSearchResult> searchResults;
	private JFrame newProjectWindow;

	private JLabel newProjectWindowNameLabel;
	private JTextField newProjectWindowNameInput;
	private JLabel newProjectWindowFileLabel;
	private JTextField newProjectWindowFileInput;
	private JLabel newProjectWindowFileTypeLabel;
	private JComboBox<Entry<String, String>> newProjectWindowFileTypeOptions;
	private JButton newProjectWindowSearchFileButton;
	private JButton newProjectWindowCreateButton;
	private JButton newProjectWindowCancelButton;

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

	private String getProjectName() {
		try {
			return new File(CURRENT_DIR).getCanonicalFile().getName();
		} catch (IOException e) {
			e.printStackTrace();
			return Constants.EMPTY;
		}
	}

	class PanelMouseWheelListener implements MouseWheelListener {
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

	class PanelKeyListener implements KeyListener {
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
		@Override
		public void keyReleased(KeyEvent keyEvent) {
		}
		@Override
		public void keyTyped(KeyEvent keyEvent) {
		}
	}

	class PanelSelectionListener implements CaretListener {
		@Override
		public void caretUpdate(CaretEvent e) {
			refreshSelection();
		}
	}
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

	class SimpleFilter extends FileFilter {
		String ext;
		String message;
		public SimpleFilter(String ext, String message) {
			this.ext = ext;
			this.message = message;
		}
		@Override
		public boolean accept(File selectedFile) {
			return selectedFile.getName().endsWith(ext) || selectedFile.isDirectory();
		}
		@Override
		public String getDescription() {
			return message;
		}
	}

	class VerticalListener implements AdjustmentListener {
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

	class PopUpOffsetEntry extends JPopupMenu {
		/** serialVersionUID */
		private static final long serialVersionUID = 8840279664255620962L;
		JMenuItem startItem;
		JMenuItem endItem;
		JMenuItem deleteItem;
		JMenuItem splitItem;
		OffsetEntry selectedEntry;
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
		otherEntry = new AbstractMap.SimpleEntry<String, String>(rb.getString(KEY_NEW_PRJ_OTHER), FILE_TYPE_OTHER);
		smdEntry = new AbstractMap.SimpleEntry<String, String>(rb.getString(KEY_NEW_PRJ_SMD), FILE_TYPE_MEGADRIVE);
		snesEntry = new AbstractMap.SimpleEntry<String, String>(rb.getString(KEY_NEW_PRJ_SNES), FILE_TYPE_SNES);
		gbEntry = new AbstractMap.SimpleEntry<String, String>(rb.getString(KEY_NEW_PRJ_NGB), FILE_TYPE_NGB);
		tapEntry = new AbstractMap.SimpleEntry<String, String>(rb.getString(KEY_NEW_PRJ_SPT), FILE_TYPE_ZXTAP);
		tzxEntry = new AbstractMap.SimpleEntry<String, String>(rb.getString(KEY_NEW_PRJ_SPZ), FILE_TYPE_TZX);
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
								createProject(file);
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
							newProjectWindowNameInput.setText(getProjectName(projectFile.getName()));
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

	private void createProject(File file) throws Exception {
		createNewProject(getProjectName(file.getName()), file.getName(), getFileType(file), file);
	}

	private String getFileType(File file) {
		String res = FILE_TYPE_OTHER;
		if(file != null) {
			String extension =  FileUtils.getFileExtension(file);
			if(EXTENSIONS_MEGADRIVE.contains(extension)) {
				res = FILE_TYPE_MEGADRIVE;
			}
			else {
				if(EXTENSIONS_SNES.contains(extension)) {
					res = FILE_TYPE_SNES;
				}
				else {
					if(EXTENSIONS_GB.contains(extension)) {
						res = FILE_TYPE_NGB;
					}
					else {
						if(EXTENSIONS_TAP.contains(extension)) {
							res = FILE_TYPE_ZXTAP;
						}
						else {
							if(EXTENSIONS_TZX_CDT.contains(extension)) {
								res = FILE_TYPE_TZX;
							}
						}
					}
				}
			}
		}
		return res;
	}

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

	private void refreshTitle() {
		setTitle(rb.getString(KEY_TITLE) + " [" + hexFile +"] - [" + tableFile.getName() + "] - [" + offsetFile.getName() + "]" );
	}

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
					newProjectWindowNameInput.setText(getProjectName(projectFile.getName()));
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
						createNewProject(name, fileName, ((Entry<String, String>) newProjectWindowFileTypeOptions.getSelectedItem()).getValue(), projectFile);
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

	private String getProjectName(String fileName) {
		String projectName = fileName;
		for(String ext : EXTENSIONS_MEGADRIVE) {
			if(!"32x".equals(ext)) {
				projectName = projectName.replace("."+ext, "smd");
			}
		}
		for(String ext : EXTENSIONS_SNES) {
			projectName = projectName.replace("."+ext, "sfc");
		}
		projectName = projectName.replaceAll(" ", "");
		projectName = projectName.replaceAll("(\\(.*\\))", "");
		projectName = projectName.replaceAll("(\\[.*\\])", "");
		projectName = projectName.replaceAll("[^A-Za-z0-9]", "");
		return projectName.toLowerCase();
	}

	private void selectProjectFileType(File file) {
		if(file != null) {
			String extension =  FileUtils.getFileExtension(file);
			newProjectWindowFileTypeOptions.setSelectedItem(otherEntry);
			if(EXTENSIONS_MEGADRIVE.contains(extension)) {
				newProjectWindowFileTypeOptions.setSelectedItem(smdEntry);
			}
			else {
				if(EXTENSIONS_SNES.contains(extension)) {
					newProjectWindowFileTypeOptions.setSelectedItem(snesEntry);
				}
				else {
					if(EXTENSIONS_GB.contains(extension)) {
						newProjectWindowFileTypeOptions.setSelectedItem(gbEntry);
					}
					else {
						if(EXTENSIONS_TAP.contains(extension)) {
							newProjectWindowFileTypeOptions.setSelectedItem(tapEntry);
						}
						else {
							if(EXTENSIONS_TZX_CDT.contains(extension)) {
								newProjectWindowFileTypeOptions.setSelectedItem(tzxEntry);
							}
						}
					}
				}
			}
		}
	}

	private String getTfileName(String name) {
		return "set T_FILENAME=\"" + name + "\"";
	}

	private String getSfileName(String name) {
		return "set S_FILENAME=\"" + name + "\"";
	}

	private String getScriptName(String name) {
		return "set SCRIPTNAME=\"" + name + "\"";
	}

	private void createNewProject(String name, String fileName, String fileType, File projectFile) throws Exception {
		File projectFolder = createProjectFolder(name);
		String transfileName = TR_FILENAME_PREFIX + fileName;
		copyBaseFiles(projectFolder, name, projectFile);
		Utils.createFile(Utils.getJoinedFileName(projectFolder, EXCUTEHEXVIEWER_FILE), createHexviewerFile(name, fileName));
		Utils.createFile(Utils.getJoinedFileName(projectFolder, SEARCHALL_FILE), createSearchAllFile(name, fileName));
		Utils.createFile(Utils.getJoinedFileName(projectFolder, CLEANEXTRACTED_FILE), createCleanExtractedFile(fileName));
		Utils.createFile(Utils.getJoinedFileName(projectFolder, EXTRACTHEX_FILE), createExtractHexFile(name, fileName, transfileName));
		Utils.createFile(Utils.getJoinedFileName(projectFolder, ORIGINALSCRIPT_FILE), createOriginalScriptFile(name, fileName));
		Utils.createFile(Utils.getJoinedFileName(projectFolder, CLEAN_FILE), createCleanFile(name));
		Utils.createFile(Utils.getJoinedFileName(projectFolder, INSERT_FILE), createInsertFile(name, fileName, fileType, transfileName));
		Utils.createFile(Utils.getJoinedFileName(projectFolder, CREATEPATCH_FILE), createCreatePatchFile(name, fileName, transfileName));
		Utils.createFile(Utils.getJoinedFileName(projectFolder, name + HEX_EXTENSION), createHexFile(name));
	}

	private String createHexviewerFile(String name, String fileName) {
		StringBuilder fileContent = new StringBuilder();
		System.out.println("Generating / Generando " + EXCUTEHEXVIEWER_FILE + "...");
		fileContent.append(ECHO_OFF).append(Constants.NEWLINE);
		fileContent.append(PROG_CALL).append(Constants.NEWLINE);
		fileContent.append(PAUSE).append(Constants.NEWLINE);
		return fileContent.toString();
	}

	private String createCleanFile(String name) {
		StringBuilder fileContent = new StringBuilder();
		System.out.println("Generating / Generando " + CLEAN_FILE + "...");
		fileContent.append(ECHO_OFF).append(Constants.NEWLINE);
		fileContent.append(getScriptName(name)).append(Constants.NEWLINE);
		fileContent.append(PROG_CALL).append("-ca tr_"+ SCRIPTNAME_VAR +".ext tr_"+ SCRIPTNAME_VAR +"_clean.ext").append(Constants.NEWLINE);
		fileContent.append(PAUSE).append(Constants.NEWLINE);
		return fileContent.toString();
	}

	private String createOriginalScriptFile(String name, String fileName) {
		StringBuilder fileContent = new StringBuilder();
		System.out.println("Generating / Generando " + ORIGINALSCRIPT_FILE + "...");
		fileContent.append(ECHO_OFF).append(Constants.NEWLINE);
		fileContent.append(getTfileName(fileName)).append(Constants.NEWLINE);
		fileContent.append(getScriptName(name)).append(Constants.NEWLINE);
		fileContent.append(PROG_CALL).append("-a "+ SCRIPTNAME_VAR +".tbl "+ TFILENAMENAME_VAR +" "+ SCRIPTNAME_VAR +".ext "+ SCRIPTNAME_VAR +".off").append(Constants.NEWLINE);
		fileContent.append(PAUSE).append(Constants.NEWLINE);
		return fileContent.toString();
	}

	private String createInsertFile(String name, String fileName, String fileType, String transfileName) {
		StringBuilder fileContent = new StringBuilder();
		System.out.println("Generating / Generando " + INSERT_FILE + "...");
		fileContent.append(ECHO_OFF).append(Constants.NEWLINE);
		fileContent.append(getTfileName(transfileName)).append(Constants.NEWLINE);
		fileContent.append(getSfileName(fileName)).append(Constants.NEWLINE);
		fileContent.append(getScriptName(name)).append(Constants.NEWLINE);
		fileContent.append("del " + TFILENAMENAME_VAR).append(Constants.NEWLINE);
		fileContent.append("copy " + SFILENAMENAME_VAR + " " + TFILENAMENAME_VAR).append(Constants.NEWLINE);
		fileContent.append(PROG_CALL).append("-ih "+ SCRIPTNAME_VAR +".hex " + TFILENAMENAME_VAR).append(Constants.NEWLINE);
		fileContent.append(PROG_CALL).append("-h "+ SCRIPTNAME_VAR +".tbl tr_"+ SCRIPTNAME_VAR +".ext " + TFILENAMENAME_VAR).append(Constants.NEWLINE);
		String checksumMode = Constants.EMPTY;
		if(FILE_TYPE_MEGADRIVE.equals(fileType) || fileName.endsWith(".32x")) {
			checksumMode = Hextractor.MODE_FIX_MEGADRIVE_CHECKSUM;
		}
		else {
			if(FILE_TYPE_NGB.equals(fileType)) {
				checksumMode = Hextractor.MODE_FIX_GAMEBOY_CHECKSUM;
			}
			else {
				if(FILE_TYPE_SNES.equals(fileType)) {
					checksumMode = Hextractor.MODE_FIX_SNES_CHECKSUM;
				}
				else {
					if(FILE_TYPE_ZXTAP.equals(fileType)) {
						checksumMode = Hextractor.MODE_FIX_ZXTAP_CHECKSUM;
					}
					else {
						if(FILE_TYPE_TZX.equals(fileType)) {
							checksumMode = Hextractor.MODE_FIX_ZXTZX_CHECKSUM;
						}
					}
				}
			}
		}
		if(checksumMode.length() > 0) {
			fileContent.append(PROG_CALL).append(checksumMode + " " + TFILENAMENAME_VAR).append(Constants.NEWLINE);
		}
		fileContent.append(PAUSE).append(Constants.NEWLINE);
		return fileContent.toString();
	}

	private String createCreatePatchFile(String name, String fileName, String transFileName) {
		StringBuilder fileContent = new StringBuilder();
		System.out.println("Generating / Generando " + CREATEPATCH_FILE + "...");
		fileContent.append(ECHO_OFF).append(Constants.NEWLINE);
		fileContent.append(getTfileName(transFileName)).append(Constants.NEWLINE);
		fileContent.append(getSfileName(fileName)).append(Constants.NEWLINE);
		fileContent.append(getScriptName(name)).append(Constants.NEWLINE);
		fileContent.append(PROG_CALL).append("-cip " + SFILENAMENAME_VAR + " " + TFILENAMENAME_VAR + " " + SCRIPTNAME_VAR +".ips").append(Constants.NEWLINE);
		fileContent.append(PAUSE).append(Constants.NEWLINE);
		return fileContent.toString();
	}

	private String createSearchAllFile(String name, String fileName) {
		StringBuilder fileContent = new StringBuilder();
		System.out.println("Generating / Generando " + SEARCHALL_FILE + "...");
		fileContent.append(ECHO_OFF).append(Constants.NEWLINE);
		fileContent.append(getTfileName(fileName)).append(Constants.NEWLINE);
		fileContent.append(getScriptName(name)).append(Constants.NEWLINE);
		fileContent.append(PROG_CALL).append("-sa "+SCRIPTNAME_VAR+".tbl "+TFILENAMENAME_VAR+" 4 FF \"..\\EngDict.txt\"").append(Constants.NEWLINE);
		fileContent.append(PAUSE).append(Constants.NEWLINE);
		return fileContent.toString();
	}

	private String createHexFile(String name) {
		StringBuilder fileContent = new StringBuilder();
		System.out.println("Generating / Generando " + name + HEX_EXTENSION + "...");
		fileContent.append(";Traducciones Wave " + YEAR).append(Constants.NEWLINE);
		fileContent.append(";54 72 61 64 75 63 63 69 6F 6E 65 73 20 57 61 76 65 20 3");
		fileContent.append(YEAR.substring(0, 1) + " 3" + YEAR.substring(1, 2) + " 3" + YEAR.substring(2, 3) + " 3" + YEAR.substring(3, 4) + "@000000E0:000000F5").append(Constants.NEWLINE);
		return fileContent.toString();
	}

	private String createCleanExtractedFile(String fileName) {
		StringBuilder fileContent = new StringBuilder();
		System.out.println("Generating / Generando " + CLEANEXTRACTED_FILE + "...");
		fileContent.append(ECHO_OFF).append(Constants.NEWLINE);
		fileContent.append(getTfileName(fileName)).append(Constants.NEWLINE);
		fileContent.append(PROG_CALL).append("-cef "+ TFILENAMENAME_VAR +".ext "+ TFILENAMENAME_VAR +".ext.off").append(Constants.NEWLINE);
		fileContent.append(PAUSE).append(Constants.NEWLINE);
		return fileContent.toString();
	}

	private String createExtractHexFile(String name, String fileName, String transfileName) {
		StringBuilder fileContent = new StringBuilder();
		System.out.println("Generating / Generando " + EXTRACTHEX_FILE + "...");
		fileContent.append(ECHO_OFF).append(Constants.NEWLINE);
		fileContent.append(getTfileName(transfileName)).append(Constants.NEWLINE);
		fileContent.append(getScriptName(name)).append(Constants.NEWLINE);
		fileContent.append(PROG_CALL).append("-eh "+ TFILENAMENAME_VAR +" "+ SCRIPTNAME_VAR +".ext.hex").append(Constants.NEWLINE);
		fileContent.append(PAUSE).append(Constants.NEWLINE);
		return fileContent.toString();
	}

	private void copyBaseFiles(File projectFolder, String name, File projectFile) throws IOException {
		Utils.copyFileUsingStream(FILE_HEXTRACTOR, Utils.getJoinedFileName(projectFolder, FILE_HEXTRACTOR));
		Utils.copyFileUsingStream(FILE_README, Utils.getJoinedFileName(projectFolder, name + FILE_README));
		if(projectFile != null) {
			Utils.copyFileUsingStream(projectFile.getAbsolutePath(), Utils.getJoinedFileName(projectFolder, projectFile.getName()));
		}
	}

	private File createProjectFolder(String name) throws Exception {
		File projectFolder = new File(name);
		if(!projectFolder.exists() && !projectFolder.mkdir()) {
			throw new Exception("Error generating: " + name + " directory." );
		}
		return projectFolder;
	}

	private void enableProjectWindow() {
		newProjectWindowNameInput.setEnabled(true);
		newProjectWindowFileInput.setEnabled(true);
		newProjectWindowFileTypeOptions.setEnabled(true);
		newProjectWindowCreateButton.setEnabled(true);
		newProjectWindowCancelButton.setEnabled(true);
	}

	private void cleanProjectWindow() {
		newProjectWindowNameInput.setText(Constants.EMPTY);
		newProjectWindowFileInput.setText(Constants.EMPTY);
		newProjectWindowFileTypeOptions.setSelectedIndex(0);
		projectFile = null;
	}

	private void disableProjectWindow() {
		newProjectWindowNameInput.setEnabled(false);
		newProjectWindowFileInput.setEnabled(false);
		newProjectWindowFileTypeOptions.setEnabled(false);
		newProjectWindowCreateButton.setEnabled(false);
		newProjectWindowCancelButton.setEnabled(false);
	}

	private void createProjectWindow() {
		cleanProjectWindow();
		enableProjectWindow();
		newProjectWindow.pack();
		newProjectWindow.setLocationRelativeTo(this);
		newProjectWindow.setVisible(true);
	}

	private void reloadTableFile(File selectedFile) {
		tableFile = selectedFile;
		try {
			hexTable = new HexTable(tableFile.getAbsolutePath());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		refreshAll();
	}

	private void reloadOffsetsFile(File selectedFile) {
		offsetFile = selectedFile;
		try {
			offEntries = Utils.getOffsets(FileUtils.getCleanOffsets(offsetFile.getAbsolutePath()));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		refreshAll();
	}

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

	public static void view(String inputFile, String tableFile) throws Exception {
		System.out.println("Viewing Hex file \"" + inputFile + "\"\n with table file: \"" + tableFile + "\".");
		new HexViewer(FileUtils.getFileBytes(inputFile), inputFile, new HexTable(tableFile), tableFile);
	}

	public static void view(String inputFile) throws Exception {
		System.out.println("Viewing Hex file \"" + inputFile + "\"\n with table file ascii.");
		new HexViewer(FileUtils.getFileBytes(inputFile), inputFile, new HexTable(0), DEFAULT_TABLE);
	}

	public static void view() throws Exception {
		System.out.println("Viewing Hex file empty with table file ascii.");
		new HexViewer(new byte[VIEW_SIZE], DEFAULT_HEXFILE, new HexTable(0), DEFAULT_TABLE);
	}

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

	@Override
	public void actionPerformed(ActionEvent e) {
		//None
	}

}
