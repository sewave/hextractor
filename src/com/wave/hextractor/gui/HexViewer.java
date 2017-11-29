package com.wave.hextractor.gui;

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
import javax.swing.JCheckBoxMenuItem;
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
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import com.wave.hextractor.object.FileDrop;
import com.wave.hextractor.object.HexTable;
import com.wave.hextractor.pojo.OffsetEntry;
import com.wave.hextractor.pojo.TableSearchResult;
import com.wave.hextractor.util.Constants;
import com.wave.hextractor.util.FileUtils;
import com.wave.hextractor.util.GuiUtils;
import com.wave.hextractor.util.KeyConstants;
import com.wave.hextractor.util.ProjectUtils;
import com.wave.hextractor.util.Utils;

/**
 * Gui for the hextractor tools.
 * @author slcantero
 */
public class HexViewer extends JFrame implements ActionListener {

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

	/** The Constant SEARCH_RES_DIMENSION. */
	private static final Dimension SEARCH_RES_DIMENSION = new Dimension(600, 200);

	/** The Constant SEARCHRES_FONT. */
	private static final String SEARCHRES_FONT = "Courier New";

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
	private JMenuItem about;

	/** The help item. */
	private JMenuItem help;

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

	/** The vsb. */
	private JScrollBar vsb;

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
	private JFrame newPrjWin;

	/** The new project window name label. */
	private JLabel newPrjWinNameLabel;

	/** The new project window name input. */
	private JTextField newPrjWinNameInput;

	/** The new project window file label. */
	private JLabel newPrjWinFileLabel;

	/** The new project window file input. */
	private JTextField newPrjWinFileInput;

	/** The new project window file type label. */
	private JLabel newPrjWinFileTypeLabel;

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

	/** The view menu. */
	private JMenu viewMenu;

	/** The view 16 cols menu item. */
	private JCheckBoxMenuItem view16Cols;

	/** The view 16 rows menu item. */
	private JCheckBoxMenuItem view16Rows;

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
	public HexViewer(byte[] fileBytes, String fileName, HexTable hexTable, String tableName) {
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
					hexTextArea.getCaretPosition() + Constants.HEX_VALUE_SIZE, BLUE_PAINTER);
		} catch (BadLocationException e1) {
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
		}
		offsetLabelValue.setText(Utils.intToHexString(offset + asciiTextArea.getCaretPosition(), Constants.HEX_ADDR_SIZE));
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
		public PopUpOffsetEntry(OffsetEntry entry){
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
			if(currEntry.start > 0 || selectedEntry != currEntry) {
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
			//Hemos caido en una entry existente, tendrá opcion de borrar o hacer split
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
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLookAndFeel();
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		createMenu();
		firstRow = new JPanel();
		firstRow.setLayout(new FlowLayout());
		secondRow = new JPanel();
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
		firstRow.add(hexTextArea);
		hexTextArea.setText(Utils.getHexArea(offset, getViewSize(), fileBytes));
		asciiTextArea = new JTextArea(visibleRows, visibleColumns);
		asciiTextArea.setLineWrap(Boolean.TRUE);
		asciiTextArea.setBackground(Color.BLACK);
		asciiTextArea.setForeground(Color.WHITE);
		asciiTextArea.setDisabledTextColor(Color.WHITE);
		asciiTextArea.setEnabled(false);
		asciiTextArea.setEditable(false);
		asciiTextArea.setCaretColor(Color.GRAY);
		firstRow.add(asciiTextArea);
		asciiTextArea.setText(Utils.getTextArea(offset, getViewSize(), fileBytes, hexTable));
		resultsWindow = new JFrame(rb.getString(KeyConstants.KEY_SEARCH_RESULT_TITLE));
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
		newPrjWin = new JFrame(rb.getString(KeyConstants.KEY_NEW_PRJ_TITLE));
		newPrjWin.setLayout(new GridLayout(NEW_PROJECT_GRID_ROWS, NEW_PROJECT_GRID_COLS, NEW_PROJECT_GRID_HGAP, NEW_PROJECT_GRID_VGAP));
		newPrjWinNameLabel = new JLabel(rb.getString(KeyConstants.KEY_NEW_PRJ_NAME), SwingConstants.LEFT);
		newPrjWinFileLabel = new JLabel(rb.getString(KeyConstants.KEY_NEW_PRJ_FILE), SwingConstants.LEFT);
		newPrjWinFileTypeLabel = new JLabel(rb.getString(KeyConstants.KEY_NEW_PRJ_FILETYPE), SwingConstants.LEFT);
		newPrjWinNameInput = new JTextField(30);
		newPrjWinFileInput = new JTextField();
		newPrjWinFileTypeOpt = new JComboBox<Map.Entry<String, String>>();
		otherEntry = new AbstractMap.SimpleEntry<String, String>(rb.getString(KeyConstants.KEY_NEW_PRJ_OTHER), Constants.FILE_TYPE_OTHER);
		smdEntry = new AbstractMap.SimpleEntry<String, String>(rb.getString(KeyConstants.KEY_NEW_PRJ_SMD), Constants.FILE_TYPE_MEGADRIVE);
		snesEntry = new AbstractMap.SimpleEntry<String, String>(rb.getString(KeyConstants.KEY_NEW_PRJ_SNES), Constants.FILE_TYPE_SNES);
		gbEntry = new AbstractMap.SimpleEntry<String, String>(rb.getString(KeyConstants.KEY_NEW_PRJ_NGB), Constants.FILE_TYPE_NGB);
		tapEntry = new AbstractMap.SimpleEntry<String, String>(rb.getString(KeyConstants.KEY_NEW_PRJ_SPT), Constants.FILE_TYPE_ZXTAP);
		tzxEntry = new AbstractMap.SimpleEntry<String, String>(rb.getString(KeyConstants.KEY_NEW_PRJ_SPZ), Constants.FILE_TYPE_TZX);
		newPrjWinFileTypeOpt.addItem(otherEntry);
		newPrjWinFileTypeOpt.addItem(smdEntry);
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
		vsb = new JScrollBar();
		firstRow.add(vsb);
		offsetLabel = new JLabel(rb.getString(KeyConstants.KEY_OFFSET_LABEL));
		secondRow.add(offsetLabel);
		offsetLabelValue = new JTextField(Utils.intToHexString(offset, Constants.HEX_ADDR_SIZE), Constants.HEX_ADDR_SIZE);
		offsetLabelValue.setEnabled(false);
		secondRow.add(offsetLabelValue);
		offsetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		offsetLabelValue.setAlignmentX(Component.LEFT_ALIGNMENT);
		offEntries = new ArrayList<OffsetEntry>();
		currEntry = new OffsetEntry();
		currEntry.start = -1;
		currEntry.end = -1;
		setResizable(Boolean.FALSE);
		addAllListeners();
		pack();
		vsb.setPreferredSize(new Dimension((int) vsb.getPreferredSize().getWidth(), (int) (getSize().getHeight() * 0.85)));
		setVisible(true);
		refreshAll();
	}

	/**
	 * Change view mode.
	 */
	private void changeViewMode() {
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
		asciiTextArea.addCaretListener(new CaretListener() {
			/* (non-Javadoc)
			 * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
			 */
			@Override
			public void caretUpdate(CaretEvent e) {
				refreshSelection();
			}
		});
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
				asciiTextArea.setCaretPosition(e.getX() / 8 + (e.getY() / 18) * visibleColumns);
				PopUpOffsetEntry menu = new PopUpOffsetEntry(getCaretEntry());
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		});
		vsb.addAdjustmentListener(new AdjustmentListener() {
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
					}
					asciiTextArea.setText(Utils.getTextArea(offset, getViewSize(), fileBytes, hexTable));
					hexTextArea.setText(Utils.getHexArea(offset, getViewSize(), fileBytes));
					asciiTextArea.setCaretPosition(asciiTextArea.getText().length());
				}
			}
		});
		vsb.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				offset = vsb.getValue();
				refreshAll();
			}
		});
		//Drop on new project, batch creation
		new FileDrop(newPrjWin, new FileDrop.Listener() {
			public void filesDropped(java.io.File[] files) {
				if(files != null) {
					if(files.length > 1) {
						newPrjWin.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						disableProjectWindow();
						newPrjWin.repaint();
						for(File file : files) {
							try {
								ProjectUtils.createProject(file);
							} catch (Exception e) {
								e.printStackTrace();
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
			}
		});
		newPrjWinFileInput.getDocument().addDocumentListener(new DocumentListener() {
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
								rb.getString(KeyConstants.KEY_SEARCH_RESULT_TABLE),
								rb.getString(KeyConstants.KEY_SEARCH_RESULT_TABLE_TITLE),
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
								reloadHexFile(file);
							}
						}
					}
				}
			}
		});
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
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
			}
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
					if ((keyEvent.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
						offset = (fileBytes.length - getViewSize());
					} else {
						OffsetEntry selectedEntry = getCaretEntry();
						if (currEntry.start > 0 || selectedEntry != currEntry) {
							endItemAction(selectedEntry);
						}
					}
					break;
				case KeyEvent.VK_HOME:
					if ((keyEvent.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
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
			}

			/*
			 * (non-Javadoc)
			 *
			 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
			 */
			@Override
			public void keyTyped(KeyEvent keyEvent) {
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
	private static final String getVisibleOffsets(int offset, int columns, int rows) {
		StringBuffer sb = new StringBuffer((Constants.HEX_ADDR_SIZE + HEX_STARTS.length() + Constants.SPACE_STR.length()) * rows);
		for(int i = 0; i < rows; i++) {
			sb.append(HEX_STARTS);
			sb.append(Utils.intToHexString(offset + i * columns, Constants.HEX_ADDR_SIZE));
			sb.append(Constants.SPACE_STR);
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
		hexTextArea.setText(Utils.getHexArea(offset, getViewSize(), fileBytes));
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
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		//Menus
		fileMenu = new JMenu(rb.getString(KeyConstants.KEY_FILEMENU));
		tableMenu = new JMenu(rb.getString(KeyConstants.KEY_TABLEMENU));
		offsetMenu = new JMenu(rb.getString(KeyConstants.KEY_OFFSETMENU));
		toolsMenu = new JMenu(rb.getString(KeyConstants.KEY_TOOLS_MENU));
		helpMenu = new JMenu(rb.getString(KeyConstants.KEY_HELP_MENU));
		viewMenu = new JMenu(rb.getString(KeyConstants.KEY_VIEW_MENU));

		//Items
		exit =  new JMenuItem(rb.getString(KeyConstants.KEY_EXIT_MENUITEM));
		openFile = new JMenuItem(rb.getString(KeyConstants.KEY_OPEN_FILE_MENUITEM));
		newProject = new JMenuItem(rb.getString(KeyConstants.KEY_NEW_PROJECT_MENUITEM));
		openTable = new JMenuItem(rb.getString(KeyConstants.KEY_OPEN_TABLE_MENUITEM));
		saveTable = new JMenuItem(rb.getString(KeyConstants.KEY_SAVE_TABLE_MENUITEM));
		about = new JMenuItem(rb.getString(KeyConstants.KEY_ABOUT_MENUITEM));
		help = new JMenuItem(rb.getString(KeyConstants.KEY_HELP_MENUITEM));
		goTo = new JMenuItem(rb.getString(KeyConstants.KEY_GOTO_MENUITEM));
		searchRelative = new JMenuItem(rb.getString(KeyConstants.KEY_SEARCH_RELATIVE_MENUITEM));
		find = new JMenuItem(rb.getString(KeyConstants.KEY_FIND_MENUITEM));
		openOffsets = new JMenuItem(rb.getString(KeyConstants.KEY_OPEN_OFFSETS_MENUITEM));
		saveOffsets = new JMenuItem(rb.getString(KeyConstants.KEY_SAVE_OFFSETS_MENUITEM));
		nextOffset = new JMenuItem(rb.getString(KeyConstants.KEY_NEXT_RANGE_MENUITEM));
		prevOffset = new JMenuItem(rb.getString(KeyConstants.KEY_PREV_TANGE_MENUITEM));
		clearOffsets = new JMenuItem(rb.getString(KeyConstants.KEY_CLEAN_OFFSETS));
		view16Cols = new JCheckBoxMenuItem(rb.getString(KeyConstants.KEY_16COLS_MENUITEM));
		view16Rows = new JCheckBoxMenuItem(rb.getString(KeyConstants.KEY_16ROWS_MENUITEM));

		tableFilter = new SimpleFilter(EXTENSION_TABLE, rb.getString(KeyConstants.KEY_FILTER_TABLE));
		offsetFileFilter = new SimpleFilter(EXTENSION_OFFSET, rb.getString(KeyConstants.KEY_FILTER_OFFSET));

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
				changeViewMode();
			}
		});
		view16Rows.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_16ROWS_MENUITEM)) {
			/** serialVersionUID */
			private static final long serialVersionUID = 251407879942401225L;
			@Override
			public void actionPerformed(ActionEvent e) {
				changeViewMode();
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
				createProjectWindow();
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
					}
					refreshAll();
				}
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
						List<TableSearchResult> results = FileUtils.searchRelative8Bits(fileBytes, searchString);
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
						List<Integer> results = FileUtils.findString(fileBytes, hexTable, searchString, true);
						if(results.isEmpty()) {
							JOptionPane.showMessageDialog(help, rb.getString(KeyConstants.KEY_NO_RESULTS_DESC),
									rb.getString(KeyConstants.KEY_NO_RESULTS_TITLE), JOptionPane.INFORMATION_MESSAGE);
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
							e1.printStackTrace();
						}
					}
					refreshAll();
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
					reloadOffsetsFile(fileChooser.getSelectedFile());
				}
			}
		});
		saveOffsets.setAction(new AbstractAction(rb.getString(KeyConstants.KEY_SAVE_OFFSETS_MENUITEM)) {
			/** serialVersionUID */
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
				fileChooser.setApproveButtonText(rb.getString(KeyConstants.KEY_SAVE_BUTTON));
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
					formErrors = (name == null || name.length() < NEW_PROJECT_NAME_MIN_LENGTH ||
							fileName == null || fileName.length() == 0 ||
							!Utils.isValidFileName(fileName) || !Utils.isValidFileName(name));
					if(formErrors) {
						JOptionPane.showMessageDialog(null, rb.getString(KeyConstants.KEY_NEW_PRJ_ERRORS_MSG), rb.getString(KeyConstants.KEY_ERROR_TITLE), JOptionPane.ERROR_MESSAGE);
					}
					else {
						newPrjWin.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						disableProjectWindow();
						newPrjWin.repaint();
						ProjectUtils.createNewProject(name, fileName, ((Entry<String, String>) newPrjWinFileTypeOpt.getSelectedItem()).getValue(), projectFile);
						newPrjWin.setVisible(false);
						JOptionPane.showMessageDialog(asciiTextArea.getParent(),
								rb.getString(KeyConstants.KEY_NEW_PRJ_GENERATING_MSG),
								rb.getString(KeyConstants.KEY_NEW_PRJ_GENERATING_MSG), JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
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
	 * Confirm selected file.
	 *
	 * @param selectedFile the selected file
	 * @return true, if successful
	 */
	protected boolean confirmSelectedFile(File selectedFile) {
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
			else {
				if(Constants.EXTENSIONS_SNES.contains(extension)) {
					newPrjWinFileTypeOpt.setSelectedItem(snesEntry);
				}
				else {
					if(Constants.EXTENSIONS_GB.contains(extension)) {
						newPrjWinFileTypeOpt.setSelectedItem(gbEntry);
					}
					else {
						if(Constants.EXTENSIONS_TAP.contains(extension)) {
							newPrjWinFileTypeOpt.setSelectedItem(tapEntry);
						}
						else {
							if(Constants.EXTENSIONS_TZX_CDT.contains(extension)) {
								newPrjWinFileTypeOpt.setSelectedItem(tzxEntry);
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
	private void createProjectWindow() {
		cleanProjectWindow();
		enableProjectWindow();
		newPrjWin.pack();
		newPrjWin.setLocationRelativeTo(this);
		newPrjWin.setVisible(true);
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
			if(newFileBytes.length >= getViewSize()) {
				fileBytes = newFileBytes;
			}
			else {
				fileBytes = new byte[getViewSize()];
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		rb = ResourceBundle.getBundle(Constants.RB_NAME, Locale.getDefault());
	}

	/**
	 * New HexViewer with inputFile and tableFile.
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
	 * New HexViewer with inputFile and default table.
	 *
	 * @param inputFile the input file
	 * @throws Exception the exception
	 */
	public static void view(String inputFile) throws Exception {
		System.out.println("Viewing Hex file \"" + inputFile + "\"\n with table file ascii.");
		new HexViewer(FileUtils.getFileBytes(inputFile), inputFile, new HexTable(0), DEFAULT_TABLE);
	}

	/**
	 * New empty HexViewer.
	 *
	 * @throws Exception the exception
	 */
	public static void view() throws Exception {
		System.out.println("Viewing Hex file empty with table file ascii.");
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
