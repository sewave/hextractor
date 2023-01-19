package com.wave.hextractor.gui

import com.wave.hextractor.helper.FileDrop
import com.wave.hextractor.helper.HexTable
import com.wave.hextractor.pojo.OffsetEntry
import com.wave.hextractor.pojo.TableSearchResult
import com.wave.hextractor.util.Constants
import com.wave.hextractor.util.FileUtils
import com.wave.hextractor.util.GuiUtils
import com.wave.hextractor.util.KeyConstants
import com.wave.hextractor.util.ProjectUtils
import com.wave.hextractor.util.Utils
import java.awt.Adjustable
import java.awt.Color
import java.awt.Cursor
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.GridLayout
import java.awt.Image
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.AdjustmentEvent
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.AbstractMap.SimpleEntry
import java.util.logging.Level
import java.util.logging.Logger
import javax.imageio.ImageIO
import javax.swing.AbstractAction
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JCheckBoxMenuItem
import javax.swing.JComboBox
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.JProgressBar
import javax.swing.JScrollBar
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.KeyStroke
import javax.swing.ListSelectionModel
import javax.swing.SwingConstants
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.BadLocationException
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter
import javax.swing.text.Highlighter
import kotlin.system.exitProcess

/**
 * Gui for the hextractor tools.
 * @author slcantero
 */
class HexViewer private constructor(fileBytes: ByteArray, fileName: String, hexTable: HexTable, tableName: String) :
    JFrame(), ActionListener {
    /** The other entry.  */
    private var otherEntry: SimpleEntry<String, String>? = null

    /** The smd entry.  */
    private var smdEntry: SimpleEntry<String, String>? = null

    /** The snes entry.  */
    private var snesEntry: SimpleEntry<String, String>? = null

    /** The gb entry.  */
    private var gbEntry: SimpleEntry<String, String>? = null

    /** The tap entry.  */
    private var tapEntry: SimpleEntry<String, String>? = null

    /** The tzx entry.  */
    private var tzxEntry: SimpleEntry<String, String>? = null

    /** The sms entry.  */
    private var smsEntry: SimpleEntry<String, String>? = null

    /** The file bytes.  */
    private var fileBytes: ByteArray

    /** The hex table.  */
    private var hexTable: HexTable

    /** The offset.  */
    private var offset = 0

    /** The hex file.  */
    private var hexFile: File?

    /** The table file.  */
    private var tableFile: File?

    /** The offset file.  */
    private var offsetFile: File

    /** The off entries.  */
    private var offEntries: MutableList<OffsetEntry> = mutableListOf()

    /** The curr entry.  */
    private var currEntry: OffsetEntry? = null

    /** The last selected end chars.  */
    private var lastSelectedEndChars = "FF"

    /** The project file.  */
    private var projectFile: File? = null

    /** The offset label value.  */
    private var offsetLabelValue: JTextField? = null

    /** The open table.  */
    private var openTable: JMenuItem? = null

    /** The save table.  */
    private var saveTable: JMenuItem? = null

    /** The reload table.  */
    private var reloadTable: JMenuItem? = null

    /** The open offsets.  */
    private var openOffsets: JMenuItem? = null

    /** The save offsets.  */
    private var saveOffsets: JMenuItem? = null

    /** The next offset.  */
    private var nextOffset: JMenuItem? = null

    /** The prev offset.  */
    private var prevOffset: JMenuItem? = null

    /** The open file.  */
    private var openFile: JMenuItem? = null

    /** The new project.  */
    private var newProject: JMenuItem? = null

    /** The exit.  */
    private var exit: JMenuItem? = null

    /** The about menu item.  */
    private var about: JMenuItem? = null

    /** The help item.  */
    private var help: JMenuItem? = null

    /** The go to.  */
    private var goTo: JMenuItem? = null

    /** The search relative.  */
    private var searchRelative: JMenuItem? = null

    /** The search all.  */
    private var searchAll: JMenuItem? = null

    /** The extract.  */
    private var extract: JMenuItem? = null

    /** The clear offsets.  */
    private var clearOffsets: JMenuItem? = null

    /** The find.  */
    private var find: JMenuItem? = null

    /** The rb.  */
    @Transient
    private var rb: ResourceBundle? = null

    /** The hex text area.  */
    private var hexTextArea: JTextArea? = null

    /** The ascii text area.  */
    private var asciiTextArea: JTextArea? = null

    /** The offsets text area.  */
    private var offsetsTextArea: JTextArea? = null

    /** The vsb.  */
    private var vsb: JScrollBar? = null

    /** The table filter.  */
    private var tableFilter: SimpleFilter? = null

    /** The offset file filter.  */
    private var offsetFileFilter: SimpleFilter? = null

    /** The offset only file filter.  */
    private var offsetOnlyFileFilter: SimpleFilter? = null

    /** The ext only file filter.  */
    private var extOnlyFileFilter: SimpleFilter? = null

    /** The results window.  */
    private var resultsWindow: JFrame? = null

    /** The search results.  */
    private var searchResults: JList<TableSearchResult?>? = null

    /** The new project window.  */
    private var newPrjWin: JFrame? = null

    /** The new project window name input.  */
    private var newPrjWinNameInput: JTextField? = null

    /** The new project window file input.  */
    private var newPrjWinFileInput: JTextField? = null

    /** The new project window file type options.  */
    private var newPrjWinFileTypeOpt: JComboBox<Map.Entry<String, String>>? = null

    /** The new project window search file button.  */
    private var newPrjWinSearchFileButton: JButton? = null

    /** The new project window create button.  */
    private var newPrjWinCreateButton: JButton? = null

    /** The new project window cancel button.  */
    private var newPrjWinCancelButton: JButton? = null

    /** The Constant OFFSET_UNIT.  */
    private var visibleColumns = MAX_COLS_AND_ROWS

    /** The visible rows.  */
    private var visibleRows = MAX_COLS_AND_ROWS

    /** The view 16 cols menu item.  */
    private var view16Cols: JCheckBoxMenuItem? = null

    /** The view 16 rows menu item.  */
    private var view16Rows: JCheckBoxMenuItem? = null

    /** The search all strings window.  */
    private var searchAllStringsWin: JFrame? = null

    /** The search all win skip chars opt.  */
    private var searchAllWinSkipCharsOpt: JComboBox<Int>? = null

    /** The search all win end chars input.  */
    private var searchAllWinEndCharsInput: JTextField? = null

    /** The search all win search button.  */
    private var searchAllWinSearchButton: JButton? = null

    /** The search all win cancel button.  */
    private var searchAllWinCancelButton: JButton? = null

    /** The search all win progress bar.  */
    private var searchAllWinProgressBar: JProgressBar? = null

    /** The search all thread.  */
    @Transient
    private var searchAllThread: Thread? = null

    /** The search all thread error.  */
    private var searchAllThreadError = false

    @Transient
    private val searchAllLock = Object()
    private val offsetBlock: Int
        /**
         * Gets the offset block.
         *
         * @return the offset block
         */
        get() = visibleColumns * visibleRows
    private val viewSize: Int
        /**
         * Gets the view size.
         *
         * @return the view size
         */
        get() = visibleColumns * visibleRows

    /**
     * Instantiates a new hex viewer.
     */
    init {
        var tableNameInit = tableName
        hexFile = File(fileName)
        this.fileBytes = fileBytes
        this.hexTable = hexTable
        val projectName = ProjectUtils.getProjectName(fileName)
        var offsetFileName = Constants.EMPTY_OFFSET_FILE
        if (Constants.EMPTY != projectName) {
            if (DEFAULT_TABLE == tableNameInit) {
                tableNameInit = projectName + EXTENSION_TABLE
            }
            offsetFileName = projectName + EXTENSION_OFFSET
        }
        tableFile = File(tableNameInit)
        offsetFile = File(offsetFileName)
        createFrame()
    }

    /**
     * Refresh selection.
     */
    private fun refreshSelection() {
        hexTextArea!!.caretPosition = asciiTextArea!!.caretPosition * Constants.HEX_VALUE_SIZE
        val highlighterHex = hexTextArea!!.highlighter
        highlighterHex.removeAllHighlights()
        try {
            highlighterHex.addHighlight(
                hexTextArea!!.caretPosition,
                hexTextArea!!.caretPosition + Constants.HEX_VALUE_SIZE - 1,
                BLUE_PAINTER
            )
        } catch (e1: BadLocationException) {
            // Do nothing
        }
        val highlighterAscii = asciiTextArea!!.highlighter
        highlighterAscii.removeAllHighlights()
        try {
            highlighterAscii.addHighlight(
                asciiTextArea!!.caretPosition,
                asciiTextArea!!.caretPosition + 1,
                BLUE_PAINTER
            )
            for (entry in offEntries) {
                drawOffsetEntry(entry, highlighterAscii, LGRAY_PAINTER, ORANGE_PAINTER)
            }
            if (currEntry!!.start > 0 && currEntry!!.start - offset >= 0) {
                highlighterAscii.addHighlight(
                    currEntry!!.start - offset,
                    currEntry!!.start - offset + 1,
                    YELLOW_PAINTER
                )
            }
            if (currEntry!!.end > 0 && currEntry!!.end - offset >= 0) {
                highlighterAscii.addHighlight(
                    currEntry!!.end - offset,
                    currEntry!!.end - offset + 1,
                    ORANGE_PAINTER
                )
            }
        } catch (e1: BadLocationException) {
            Utils.log("Bad location.")
        }
        offsetLabelValue!!.text = getOffsetLabelValue()
    }

    private fun getOffsetLabelValue(): String {
        val currPos = offset + asciiTextArea!!.caretPosition
        val size = fileBytes.size - 1
        val lengthDec = size.toString().length
        val lengthHex = Integer.toHexString(size).length
        val strFormat = "%0" + lengthDec + "d"
        return "0x" + Utils.intToHexString(currPos, lengthHex) + " (" + String.format(
            strFormat,
            currPos
        ) + ") / 0x" + Utils.intToHexString(size, lengthHex) + " (" + String.format(
            strFormat,
            size
        ) + ") - (" + String.format("%03.2f", 100f * currPos / size) + "% )"
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
    @Throws(BadLocationException::class)
    private fun drawOffsetEntry(
        entry: OffsetEntry?,
        highlighter: Highlighter,
        painter: Highlighter.HighlightPainter,
        borderPainter: Highlighter.HighlightPainter
    ) {
        if (entry!!.start <= offset + viewSize && entry.end >= offset) {
            var start = entry.start
            var end = entry.end
            if (start < offset) {
                start = offset
            }
            if (end >= offset + viewSize) {
                end = offset + viewSize - 1
            }
            if (entry.start >= offset) {
                highlighter.addHighlight(start - offset, start - offset + 1, borderPainter)
            } else {
                start--
            }
            highlighter.addHighlight(start - offset + 1, end - offset, painter)
            highlighter.addHighlight(end - offset, end - offset + 1, borderPainter)
        }
    }

    /**
     * The Class PopUpOffsetEntry.
     */
    internal inner class PopUpOffsetEntry(
        /** The selected entry.  */
        var selectedEntry: OffsetEntry?
    ) : JPopupMenu() {
        /** The start item.  */
        private var startItem: JMenuItem = JMenuItem()

        /** The end item.  */
        private var endItem: JMenuItem? = null

        /** The delete item.  */
        private var deleteItem: JMenuItem? = null

        /** The split item.  */
        private var splitItem: JMenuItem? = null

        /**
         * Instantiates a new pop-up offset entry.
         */
        init {
            startItem.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_OFFSET_SET_START)) {
                override fun actionPerformed(e: ActionEvent) {
                    selectedEntry!!.start = offset + asciiTextArea!!.caretPosition
                    refreshAll()
                }
            }
            add(startItem)
            if (currEntry!!.start > 0 || selectedEntry !== currEntry) {
                endItem = JMenuItem()
                endItem!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_OFFSET_SET_END)) {
                    override fun actionPerformed(e: ActionEvent) {
                        endItemAction(selectedEntry)
                        refreshAll()
                    }
                }
                add(endItem)
            }
            // We fell in an existing entry, can be deleted or split
            if (currEntry !== selectedEntry) {
                splitItem = JMenuItem()
                splitItem!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_OFFSET_SPLIT)) {
                    override fun actionPerformed(e: ActionEvent) {
                        splitItemAction(selectedEntry!!)
                    }
                }
                add(splitItem)
                deleteItem = JMenuItem()
                deleteItem!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_OFFSET_DELETE)) {
                    override fun actionPerformed(e: ActionEvent) {
                        deleteItemAction(selectedEntry)
                    }
                }
                add(deleteItem)
            }
        }
    }

    /**
     * Delete item action.
     *
     * @param selectedEntry the selected entry
     */
    private fun deleteItemAction(selectedEntry: OffsetEntry?) {
        if (GuiUtils.confirmActionAlert(
                rb!!.getString(KeyConstants.KEY_CONFIRM_RANGE_DELETE_TITLE),
                rb!!.getString(KeyConstants.KEY_CONFIRM_RANGE_DELETE) + selectedEntry!!.toEntryString()
            )
        ) {
            offEntries.remove(selectedEntry)
            refreshAll()
        }
    }

    /**
     * Split item action.
     *
     * @param selectedEntry the selected entry
     */
    private fun splitItemAction(selectedEntry: OffsetEntry) {
        var minMaxLength = 0
        val value = JOptionPane.showInputDialog(rb!!.getString(KeyConstants.KEY_OFFSET_SPLIT), DEFAULT_SPLIT_VALUE)
        if (value != null) {
            try {
                minMaxLength = value.toInt()
            } catch (e1: NumberFormatException) {
                JOptionPane.showMessageDialog(
                    help,
                    rb!!.getString(KeyConstants.KEY_OFFSET_SPLIT_CANCEL),
                    rb!!.getString(KeyConstants.KEY_OFFSET_SPLIT_CANCEL_TITLE),
                    JOptionPane.INFORMATION_MESSAGE
                )
            }
        }
        if (minMaxLength > 0) {
            offEntries.remove(selectedEntry)
            offEntries.addAll(selectedEntry.split(minMaxLength, fileBytes))
            refreshAll()
        }
    }

    /**
     * End item action.
     *
     * @param selectedEntry the selected entry
     */
    private fun endItemAction(selectedEntry: OffsetEntry?) {
        asciiTextArea!!.selectionEnd = asciiTextArea!!.caretPosition
        val result = JOptionPane.showInputDialog(rb!!.getString(KeyConstants.KEY_INPUT_ENDCHARS), lastSelectedEndChars)
        if (result != null && result.isNotEmpty()) {
            if (result.matches(REGEXP_OFFSET_ENTRIES.toRegex())) {
                lastSelectedEndChars = result
                selectedEntry!!.endChars =
                    lastSelectedEndChars.uppercase(Locale.getDefault()).replace(Constants.SPACE_STR, Constants.EMPTY)
                        .split(Constants.OFFSET_CHAR_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toMutableList()
                selectedEntry.end = offset + asciiTextArea!!.caretPosition
                if ((selectedEntry.start > 0 || selectedEntry.end > 0) && selectedEntry === currEntry) {
                    currEntry!!.mergeInto(offEntries)
                    currEntry = OffsetEntry()
                    currEntry!!.start = -1
                    currEntry!!.end = -1
                }
            } else {
                JOptionPane.showMessageDialog(
                    asciiTextArea!!.parent,
                    rb!!.getString(KeyConstants.KEY_ALERT_INVALID_ENDCHARS),
                    rb!!.getString(KeyConstants.KEY_ALERT_INVALID_ENDCHARS_TITLE),
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    private val caretEntry: OffsetEntry?
        /**
         * Gets the caret entry.
         *
         * @return the caret entry
         */
        get() {
            // By default, our current is in range, is there anybody else?
            val caretPosition = asciiTextArea!!.caretPosition + offset
            var inRange = currEntry
            for (entry in offEntries) {
                // 1D collision, positions are inclusive
                if (caretPosition >= entry.start && caretPosition <= entry.end) {
                    inRange = entry
                }
            }
            return inRange
        }

    /**
     * Close app.
     */
    private fun closeApp() {
        if (GuiUtils.confirmActionAlert(
                rb!!.getString(KeyConstants.KEY_TITLE),
                rb!!.getString(KeyConstants.KEY_CONFIRM_EXIT)
            )
        ) {
            exitProcess(0)
        }
    }

    /**
     * Creates the frame.
     */
    private fun createFrame() {
        isVisible = false
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        setLookAndFeel()
        layout = BoxLayout(contentPane, BoxLayout.Y_AXIS)
        createMenu()
        val firstRow = JPanel()
        firstRow.layout = FlowLayout()
        val secondRow = JPanel()
        secondRow.layout = FlowLayout(FlowLayout.LEADING)
        add(firstRow)
        add(secondRow)
        offsetsTextArea =
            JTextArea(visibleRows, Constants.HEX_ADDR_SIZE + HEX_STARTS.length + Constants.SPACE_STR.length)
        offsetsTextArea!!.preferredSize = DIMENSION_0_0
        offsetsTextArea!!.lineWrap = true
        offsetsTextArea!!.background = Color.BLACK
        offsetsTextArea!!.foreground = Color.WHITE
        offsetsTextArea!!.isEditable = false
        offsetsTextArea!!.disabledTextColor = Color.WHITE
        offsetsTextArea!!.isEnabled = false
        offsetsTextArea!!.font = BASE_FONT
        firstRow.add(offsetsTextArea)
        offsetsTextArea!!.text = getVisibleOffsets(offset, visibleColumns, visibleRows)
        hexTextArea = JTextArea(visibleRows, visibleColumns * Constants.HEX_VALUE_SIZE)
        hexTextArea!!.lineWrap = true
        hexTextArea!!.background = Color.BLACK
        hexTextArea!!.foreground = Color.WHITE
        hexTextArea!!.isEditable = false
        hexTextArea!!.disabledTextColor = Color.WHITE
        hexTextArea!!.isEnabled = false
        hexTextArea!!.caretColor = Color.GRAY
        hexTextArea!!.font = BASE_FONT
        firstRow.add(hexTextArea)
        hexTextArea!!.text = Utils.getHexAreaFixedWidth(
            offset,
            viewSize,
            fileBytes,
            visibleColumns
        )
        asciiTextArea = JTextArea(visibleRows, visibleColumns)
        asciiTextArea!!.lineWrap = true
        asciiTextArea!!.background = Color.BLACK
        asciiTextArea!!.foreground = Color.WHITE
        asciiTextArea!!.disabledTextColor = Color.WHITE
        asciiTextArea!!.isEnabled = false
        asciiTextArea!!.isEditable = false
        asciiTextArea!!.caretColor = Color.GRAY
        asciiTextArea!!.font = BASE_FONT
        firstRow.add(asciiTextArea)
        asciiTextArea!!.text = Utils.getTextArea(offset, viewSize, fileBytes, hexTable)
        resultsWindow = JFrame(rb!!.getString(KeyConstants.KEY_SEARCH_RESULT_TITLE))
        resultsWindow!!.layout = FlowLayout()
        searchResults = JList(arrayOfNulls(0))
        searchResults!!.selectionMode = ListSelectionModel.SINGLE_SELECTION
        searchResults!!.layoutOrientation = JList.VERTICAL
        searchResults!!.visibleRowCount = 8
        searchResults!!.font = Font(Font.MONOSPACED, Font.PLAIN, SEARCHRES_FONT_SIZE)
        val listScroller = JScrollPane(searchResults)
        listScroller.preferredSize = SEARCH_RES_DIMENSION
        resultsWindow!!.add(listScroller)
        resultsWindow!!.pack()
        resultsWindow!!.isResizable = java.lang.Boolean.FALSE
        createNewPrjWin()
        vsb = JScrollBar()
        firstRow.add(vsb)
        val offsetLabel = JLabel(rb!!.getString(KeyConstants.KEY_OFFSET_LABEL))
        secondRow.add(offsetLabel)
        offsetLabelValue = JTextField(getOffsetLabelValue(), OFFSET_LABEL_LENGTH)
        offsetLabelValue!!.isEnabled = false
        secondRow.add(offsetLabelValue)
        offsetLabel.alignmentX = LEFT_ALIGNMENT
        offsetLabelValue!!.alignmentX = LEFT_ALIGNMENT
        offEntries = ArrayList()
        currEntry = OffsetEntry()
        currEntry!!.start = -1
        currEntry!!.end = -1
        createSearchAllWin()
        isResizable = java.lang.Boolean.FALSE
        addAllListeners()
        pack()
        vsb!!.preferredSize = Dimension(vsb!!.preferredSize.getWidth().toInt(), (size.getHeight() * 0.85).toInt())
        refreshViewMode()
        setIcons()
        isVisible = true
        refreshAll()
    }

    /**
     * Sets the icons.
     */
    private fun setIcons() {
        val images: MutableList<Image> = ArrayList()
        try {
            images.add(ImageIO.read(ICON96))
            images.add(ImageIO.read(ICON32))
            images.add(ImageIO.read(ICON16))
        } catch (e: IOException) {
            Utils.logException(e)
        }
        iconImages = images
    }

    /**
     * createSearchAllWin.
     */
    private fun createSearchAllWin() {
        searchAllStringsWin = JFrame(rb!!.getString(KeyConstants.KEY_SEARCH_ALL_WIN_TITLE))
        searchAllStringsWin!!.layout =
            GridLayout(SEARCH_ALL_GRID_ROWS, SEARCH_ALL_GRID_COLS, SEARCH_ALL_GRID_HOR_GAP, SEARCH_ALL_GRID_VERT_GAP)
        val searchAllWinSkipCharsLabel =
            JLabel(rb!!.getString(KeyConstants.KEY_SEARCH_ALL_WIN_SKIP_CHARS_LABEL), SwingConstants.LEFT)
        val searchAllWinEndCharsLabel =
            JLabel(rb!!.getString(KeyConstants.KEY_SEARCH_ALL_WIN_END_CHARS_LABEL), SwingConstants.LEFT)
        searchAllWinEndCharsInput = JTextField(SEARCH_ALL_END_CHARS_LENGTH)
        searchAllWinSkipCharsOpt = JComboBox()
        for (i in 0..16) {
            searchAllWinSkipCharsOpt!!.addItem(i)
        }
        searchAllWinProgressBar = JProgressBar(SEARCH_ALL_MIN_PROGRESS, SEARCH_ALL_MAX_PROGRESS)
        searchAllStringsWin!!.add(searchAllWinSkipCharsLabel)
        searchAllStringsWin!!.add(searchAllWinSkipCharsOpt)
        searchAllStringsWin!!.add(searchAllWinEndCharsLabel)
        searchAllStringsWin!!.add(searchAllWinEndCharsInput)
        searchAllStringsWin!!.add(searchAllWinSearchButton)
        searchAllStringsWin!!.add(searchAllWinCancelButton)
        searchAllStringsWin!!.add(searchAllWinProgressBar)
        searchAllStringsWin!!.add(JLabel())
        searchAllStringsWin!!.pack()
        searchAllStringsWin!!.isResizable = java.lang.Boolean.FALSE
    }

    /**
     * createNewPrjWin.
     */
    private fun createNewPrjWin() {
        newPrjWin = JFrame(rb!!.getString(KeyConstants.KEY_NEW_PRJ_TITLE))
        newPrjWin!!.layout = GridLayout(
            NEW_PROJECT_GRID_ROWS,
            NEW_PROJECT_GRID_COLS,
            NEW_PROJECT_GRID_HOR_GAP,
            NEW_PROJECT_GRID_VERT_GAP
        )
        val newPrjWinNameLabel = JLabel(rb!!.getString(KeyConstants.KEY_NEW_PRJ_NAME), SwingConstants.LEFT)
        val newPrjWinFileLabel = JLabel(rb!!.getString(KeyConstants.KEY_NEW_PRJ_FILE), SwingConstants.LEFT)
        val newPrjWinFileTypeLabel = JLabel(rb!!.getString(KeyConstants.KEY_NEW_PRJ_FILETYPE), SwingConstants.LEFT)
        newPrjWinNameInput = JTextField(30)
        newPrjWinFileInput = JTextField()
        newPrjWinFileTypeOpt = JComboBox()
        otherEntry = SimpleEntry(rb!!.getString(KeyConstants.KEY_NEW_PRJ_OTHER), Constants.FILE_TYPE_OTHER)
        smdEntry = SimpleEntry(rb!!.getString(KeyConstants.KEY_NEW_PRJ_SMD), Constants.FILE_TYPE_MEGA_DRIVE)
        snesEntry = SimpleEntry(rb!!.getString(KeyConstants.KEY_NEW_PRJ_SNES), Constants.FILE_TYPE_SNES)
        gbEntry = SimpleEntry(rb!!.getString(KeyConstants.KEY_NEW_PRJ_NGB), Constants.FILE_TYPE_NGB)
        tapEntry = SimpleEntry(rb!!.getString(KeyConstants.KEY_NEW_PRJ_SPT), Constants.FILE_TYPE_ZXTAP)
        tzxEntry = SimpleEntry(rb!!.getString(KeyConstants.KEY_NEW_PRJ_SPZ), Constants.FILE_TYPE_TZX)
        smsEntry = SimpleEntry(rb!!.getString(KeyConstants.KEY_NEW_PRJ_SMS), Constants.FILE_TYPE_MASTER_SYSTEM)
        newPrjWinFileTypeOpt!!.addItem(otherEntry)
        newPrjWinFileTypeOpt!!.addItem(smdEntry)
        newPrjWinFileTypeOpt!!.addItem(smsEntry)
        newPrjWinFileTypeOpt!!.addItem(gbEntry)
        newPrjWinFileTypeOpt!!.addItem(snesEntry)
        newPrjWinFileTypeOpt!!.addItem(tapEntry)
        newPrjWinFileTypeOpt!!.addItem(tzxEntry)
        newPrjWin!!.add(newPrjWinFileLabel)
        newPrjWin!!.add(newPrjWinFileInput)
        newPrjWin!!.add(newPrjWinSearchFileButton)
        newPrjWin!!.add(newPrjWinNameLabel)
        newPrjWin!!.add(newPrjWinNameInput)
        newPrjWin!!.add(JLabel())
        newPrjWin!!.add(newPrjWinFileTypeLabel)
        newPrjWin!!.add(newPrjWinFileTypeOpt)
        newPrjWin!!.add(JLabel())
        newPrjWin!!.add(JLabel())
        newPrjWin!!.add(newPrjWinCreateButton)
        newPrjWin!!.add(newPrjWinCancelButton)
        newPrjWin!!.pack()
        newPrjWin!!.isResizable = java.lang.Boolean.FALSE
    }

    /**
     * Refresh view mode.
     */
    private fun refreshViewMode() {
        hexTextArea!!.caretPosition = 0
        asciiTextArea!!.caretPosition = 0
        visibleColumns = if (view16Cols!!.state) {
            MIN_COLS_AND_ROWS
        } else {
            MAX_COLS_AND_ROWS
        }
        val oldVisibleRows = visibleRows
        visibleRows = if (view16Rows!!.state) {
            MIN_COLS_AND_ROWS
        } else {
            MAX_COLS_AND_ROWS
        }
        if (oldVisibleRows > visibleRows) {
            vsb!!.preferredSize =
                Dimension(vsb!!.preferredSize.getWidth().toInt(), vsb!!.preferredSize.getHeight().toInt() / 2)
        }
        if (oldVisibleRows < visibleRows) {
            vsb!!.preferredSize =
                Dimension(vsb!!.preferredSize.getWidth().toInt(), vsb!!.preferredSize.getHeight().toInt() * 2)
        }
        hexTextArea!!.columns = visibleColumns * Constants.HEX_VALUE_SIZE
        hexTextArea!!.rows = visibleRows
        hexTextArea!!.preferredSize = DIMENSION_0_0
        asciiTextArea!!.columns = visibleColumns
        asciiTextArea!!.rows = visibleRows
        asciiTextArea!!.preferredSize = DIMENSION_0_0
        offsetsTextArea!!.preferredSize = DIMENSION_0_0
        offsetsTextArea!!.rows = visibleRows
        pack()
        // Fix for java 8+ width
        asciiTextArea!!.setSize(asciiTextArea!!.width + 1, asciiTextArea!!.height)
        asciiTextArea!!.preferredSize = Dimension(asciiTextArea!!.width + 1, asciiTextArea!!.height)
        pack()
        refreshAll()
    }

    /**
     * Adds all the program listeners.
     */
    private fun addAllListeners() {
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(we: WindowEvent) {
                closeApp()
            }
        })
        asciiTextArea!!.addCaretListener { refreshSelection() }
        asciiTextArea!!.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (e.isPopupTrigger) {
                    doPop(e)
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                if (e.isPopupTrigger) {
                    doPop(e)
                }
            }

            private fun doPop(e: MouseEvent) {
                asciiTextArea!!.caretPosition = e.x / 8 + e.y / 18 * visibleColumns
                val menu = PopUpOffsetEntry(caretEntry)
                menu.show(e.component, e.x, e.y)
            }
        })
        vsb!!.addAdjustmentListener { evt: AdjustmentEvent ->
            val source: Adjustable = evt.adjustable
            if (evt.valueIsAdjusting) {
                return@addAdjustmentListener
            }
            val orient: Int = source.orientation
            if (orient == Adjustable.VERTICAL) {
                val type: Int = evt.adjustmentType
                val value: Int = evt.value
                when (type) {
                    AdjustmentEvent.UNIT_INCREMENT -> offset += visibleColumns
                    AdjustmentEvent.UNIT_DECREMENT -> offset -= visibleColumns
                    AdjustmentEvent.BLOCK_INCREMENT -> offset += offsetBlock
                    AdjustmentEvent.BLOCK_DECREMENT -> offset -= offsetBlock
                    AdjustmentEvent.TRACK -> offset = value
                    else -> {}
                }
                asciiTextArea!!.text = Utils.getTextArea(
                    offset,
                    viewSize,
                    fileBytes,
                    hexTable
                )
                hexTextArea!!.text = Utils.getHexAreaFixedWidth(
                    offset,
                    viewSize,
                    fileBytes,
                    visibleColumns
                )
                asciiTextArea!!.caretPosition = asciiTextArea!!.text.length
            }
        }
        vsb!!.model.addChangeListener {
            offset = vsb!!.value
            refreshAll()
        }
        // Drop on new project, batch creation
        FileDrop(newPrjWin!!) { files: Array<File?>? ->
            if (files != null) {
                if (files.size > 1) {
                    newPrjWin!!.cursor = Cursor(Cursor.WAIT_CURSOR)
                    disableProjectWindow()
                    newPrjWin!!.repaint()
                    for (file: File? in files) {
                        require(file != null)
                        try {
                            ProjectUtils.createProject(file)
                        } catch (e: Exception) {
                            Utils.logException(e)
                        }
                    }
                    newPrjWin!!.cursor = Cursor(Cursor.DEFAULT_CURSOR)
                    newPrjWin!!.isVisible = false
                    JOptionPane.showMessageDialog(
                        asciiTextArea!!.parent,
                        rb!!.getString(KeyConstants.KEY_NEW_PRJ_GENERATING_MSG),
                        rb!!.getString(KeyConstants.KEY_NEW_PRJ_GENERATING_MSG),
                        JOptionPane.INFORMATION_MESSAGE
                    )
                } else {
                    for (file: File? in files) {
                        require(file != null)
                        newPrjWinFileInput!!.text = file.name
                        projectFile = file
                        selectProjectFileType(projectFile)
                        newPrjWinNameInput!!.text = ProjectUtils.getProjectName(projectFile!!.name)
                    }
                }
            }
        }
        newPrjWinFileInput!!.document.addDocumentListener(object : DocumentListener {
            override fun changedUpdate(e: DocumentEvent) {
                cleanFile()
            }

            override fun removeUpdate(e: DocumentEvent) {
                cleanFile()
            }

            override fun insertUpdate(e: DocumentEvent) {
                cleanFile()
            }

            fun cleanFile() {
                projectFile = null
            }
        })
        searchResults!!.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(evt: MouseEvent) {
                val list = evt.source as JList<TableSearchResult?>
                val tsr = list.selectedValue
                if (evt.clickCount == 1 && tsr != null) {
                    if (SwingUtilities.isLeftMouseButton(evt)) {
                        offset = tsr.offset
                        asciiTextArea!!.caretPosition = 0
                        hexTextArea!!.caretPosition = 0
                        refreshAll()
                    }
                    if (SwingUtilities.isRightMouseButton(evt)) {
                        val selectedOption = JOptionPane.showConfirmDialog(
                            null,
                            rb!!.getString(KeyConstants.KEY_SEARCH_RESULT_TABLE),
                            rb!!.getString(KeyConstants.KEY_SEARCH_RESULT_TABLE_TITLE),
                            JOptionPane.YES_NO_OPTION
                        )
                        if (selectedOption == JOptionPane.YES_OPTION) {
                            offset = tsr.offset
                            hexTable = tsr.hexTable
                            refreshAll()
                        }
                    }
                }
            }
        })
        FileDrop(this) { files: Array<File?>? ->
            if (!files.isNullOrEmpty()) {
                requestFocus()
                requestFocusInWindow()
                for (file: File? in files) {
                    require(file != null)
                    if (file.absolutePath.endsWith(EXTENSION_TABLE)) {
                        reloadTableFile(file)
                    } else {
                        if (file.absolutePath.endsWith(EXTENSION_OFFSET)) {
                            reloadOffsetsFile(file)
                        } else {
                            if (file.absolutePath.endsWith(EXTENSION_EXTRACTION)) {
                                reloadExtAsOffsetsFile(file)
                            } else {
                                reloadHexFile(file)
                            }
                        }
                    }
                }
            }
        }
        addMouseWheelListener { mouseWheelEvent: MouseWheelEvent ->
            if (mouseWheelEvent.wheelRotation < 0) {
                if (offset > visibleColumns) {
                    offset -= visibleColumns
                }
            } else {
                if (offset < fileBytes.size - viewSize) {
                    offset += visibleColumns
                }
            }
            refreshAll()
        }
        addListenerForKeys()
    }

    /**
     * Adds the listener for the key presses.
     */
    private fun addListenerForKeys() {
        addKeyListener(object : KeyListener {
            override fun keyPressed(keyEvent: KeyEvent) {
                // Auto-repeated on time
                when (keyEvent.keyCode) {
                    KeyEvent.VK_ADD, KeyEvent.VK_PLUS -> offset++
                    KeyEvent.VK_SUBTRACT, KeyEvent.VK_MINUS -> offset--
                    KeyEvent.VK_PAGE_DOWN -> offset += offsetBlock
                    KeyEvent.VK_PAGE_UP -> offset -= offsetBlock
                    KeyEvent.VK_LEFT -> if (asciiTextArea!!.caretPosition > 0) {
                        asciiTextArea!!.caretPosition = asciiTextArea!!.caretPosition - 1
                    } else {
                        offset -= visibleColumns
                        if (offset > 0) {
                            asciiTextArea!!.caretPosition = asciiTextArea!!.caretPosition + visibleColumns - 1
                        }
                    }

                    KeyEvent.VK_UP -> if (asciiTextArea!!.caretPosition > 0) {
                        var carUpPos = asciiTextArea!!.caretPosition - visibleColumns
                        if (carUpPos < 0) {
                            carUpPos += visibleColumns
                            offset -= visibleColumns
                            if (offset < 0) {
                                offset = 0
                                carUpPos = 0
                            }
                        }
                        asciiTextArea!!.caretPosition = carUpPos
                    } else {
                        offset -= visibleColumns
                    }

                    KeyEvent.VK_DOWN -> if (asciiTextArea!!.caretPosition < viewSize) {
                        var carDownPos = asciiTextArea!!.caretPosition + visibleColumns
                        if (carDownPos >= viewSize) {
                            carDownPos -= visibleColumns
                            offset += visibleColumns
                            if (offset > fileBytes.size - viewSize) {
                                offset = fileBytes.size - viewSize
                                carDownPos = viewSize - 1
                            }
                        }
                        asciiTextArea!!.caretPosition = carDownPos
                    } else {
                        offset += visibleColumns
                    }

                    KeyEvent.VK_RIGHT -> {
                        var carRightPos = asciiTextArea!!.caretPosition + 1
                        if (carRightPos >= viewSize) {
                            offset += visibleColumns
                            if (offset > fileBytes.size - viewSize) {
                                carRightPos = viewSize - 1
                            } else {
                                carRightPos -= visibleColumns
                            }
                        }
                        asciiTextArea!!.caretPosition = carRightPos
                    }

                    KeyEvent.VK_END -> if (keyEvent.modifiersEx and InputEvent.CTRL_DOWN_MASK != 0) {
                        offset = fileBytes.size - viewSize
                    } else {
                        val selectedEntry: OffsetEntry = caretEntry!!
                        if (currEntry!!.start > 0 || selectedEntry !== currEntry) {
                            endItemAction(selectedEntry)
                        }
                    }

                    KeyEvent.VK_HOME -> if (keyEvent.modifiersEx and InputEvent.CTRL_DOWN_MASK != 0) {
                        offset = 0
                    } else {
                        caretEntry!!.start = offset + asciiTextArea!!.caretPosition
                    }

                    KeyEvent.VK_INSERT -> {
                        val selectedEntry: OffsetEntry = caretEntry!!
                        if (selectedEntry !== currEntry) {
                            splitItemAction(selectedEntry)
                        }
                    }

                    KeyEvent.VK_DELETE -> {
                        val selectedEntryDel: OffsetEntry = caretEntry!!
                        if (selectedEntryDel !== currEntry) {
                            deleteItemAction(selectedEntryDel)
                        }
                    }

                    else -> {}
                }
                refreshAll()
            }

            /*
             * (non-Javadoc)
             *
             * @see
             * java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
             */
            override fun keyReleased(keyEvent: KeyEvent) {
                // No action
            }

            /*
             * (non-Javadoc)
             *
             * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
             */
            override fun keyTyped(keyEvent: KeyEvent) {
                // No action
            }
        })
    }

    /**
     * Refresh title.
     */
    private fun refreshTitle() {
        title =
            rb!!.getString(KeyConstants.KEY_TITLE) + " [" + hexFile + "] - [" + tableFile!!.name + "] - [" + offsetFile.name + "]"
    }

    /**
     * Refresh all.
     */
    private fun refreshAll() {
        offEntries.sort()
        if (offset > fileBytes.size - viewSize) {
            offset = fileBytes.size - viewSize
        }
        if (offset < 0) {
            offset = 0
        }
        var pos = asciiTextArea!!.caretPosition
        if (viewSize != asciiTextArea!!.text.length) {
            pos = 0
            asciiTextArea!!.caretPosition = 0
            hexTextArea!!.caretPosition = 0
        }
        hexTextArea!!.text = Utils.getHexAreaFixedWidth(
            offset,
            viewSize,
            fileBytes,
            visibleColumns
        )
        asciiTextArea!!.text = Utils.getTextArea(offset, viewSize, fileBytes, hexTable)
        offsetsTextArea!!.text = getVisibleOffsets(offset, visibleColumns, visibleRows)
        vsb!!.minimum = 0
        vsb!!.maximum = fileBytes.size - viewSize
        vsb!!.unitIncrement = visibleColumns
        vsb!!.blockIncrement = viewSize
        vsb!!.value = offset
        refreshTitle()
        refreshSelection()
        asciiTextArea!!.caretPosition = pos
        hexTextArea!!.caretPosition = pos * Constants.HEX_VALUE_SIZE
        asciiTextArea!!.repaint()
        hexTextArea!!.repaint()
        offsetsTextArea!!.repaint()
    }

    /**
     * Creates the menu.
     */
    private fun createMenu() {
        // Create objects
        val menuBar = JMenuBar()
        jMenuBar = menuBar

        // Menus
        val fileMenu = JMenu(rb!!.getString(KeyConstants.KEY_FILE_MENU))
        val tableMenu = JMenu(rb!!.getString(KeyConstants.KEY_TABLE_MENU))
        val offsetMenu = JMenu(rb!!.getString(KeyConstants.KEY_OFFSET_MENU))
        val toolsMenu = JMenu(rb!!.getString(KeyConstants.KEY_TOOLS_MENU))
        val helpMenu = JMenu(rb!!.getString(KeyConstants.KEY_HELP_MENU))
        val viewMenu = JMenu(rb!!.getString(KeyConstants.KEY_VIEW_MENU))

        // Items
        exit = JMenuItem(rb!!.getString(KeyConstants.KEY_EXIT_MENU_ITEM))
        openFile = JMenuItem(rb!!.getString(KeyConstants.KEY_OPEN_FILE_MENUITEM))
        newProject = JMenuItem(rb!!.getString(KeyConstants.KEY_NEW_PROJECT_MENUITEM))
        openTable = JMenuItem(rb!!.getString(KeyConstants.KEY_OPEN_TABLE_MENU_ITEM))
        saveTable = JMenuItem(rb!!.getString(KeyConstants.KEY_SAVE_TABLE_MENUITEM))
        reloadTable = JMenuItem(rb!!.getString(KeyConstants.KEY_RELOAD_TABLE_MENUITEM))
        about = JMenuItem(rb!!.getString(KeyConstants.KEY_ABOUT_MENUITEM))
        help = JMenuItem(rb!!.getString(KeyConstants.KEY_HELP_MENUITEM))
        goTo = JMenuItem(rb!!.getString(KeyConstants.KEY_GOTO_MENUITEM))
        searchRelative = JMenuItem(rb!!.getString(KeyConstants.KEY_SEARCH_RELATIVE_MENUITEM))
        searchAll = JMenuItem(rb!!.getString(KeyConstants.KEY_SEARCH_ALL_MENUITEM))
        extract = JMenuItem(rb!!.getString(KeyConstants.KEY_EXTRACT_MENUITEM))
        find = JMenuItem(rb!!.getString(KeyConstants.KEY_FIND_MENUITEM))
        openOffsets = JMenuItem(rb!!.getString(KeyConstants.KEY_OPEN_OFFSETS_MENUITEM))
        saveOffsets = JMenuItem(rb!!.getString(KeyConstants.KEY_SAVE_OFFSETS_MENUITEM))
        nextOffset = JMenuItem(rb!!.getString(KeyConstants.KEY_NEXT_RANGE_MENU_ITEM))
        prevOffset = JMenuItem(rb!!.getString(KeyConstants.KEY_PREV_RANGE_MENU_ITEM))
        clearOffsets = JMenuItem(rb!!.getString(KeyConstants.KEY_CLEAN_OFFSETS))
        view16Cols = JCheckBoxMenuItem(rb!!.getString(KeyConstants.KEY_16COLS_MENUITEM))
        view16Rows = JCheckBoxMenuItem(rb!!.getString(KeyConstants.KEY_16ROWS_MENUITEM))
        tableFilter = SimpleFilter(EXTENSION_TABLE, rb!!.getString(KeyConstants.KEY_FILTER_TABLE))
        offsetFileFilter = SimpleFilter(
            Arrays.asList(EXTENSION_OFFSET, EXTENSION_EXTRACTION),
            rb!!.getString(KeyConstants.KEY_FILTER_OFFSET)
        )
        offsetOnlyFileFilter = SimpleFilter(
            listOf(EXTENSION_OFFSET),
            rb!!.getString(KeyConstants.KEY_FILTER_OFFSET_ONLY)
        )
        extOnlyFileFilter = SimpleFilter(
            listOf(EXTENSION_EXTRACTION),
            rb!!.getString(KeyConstants.KEY_FILTER_EXT_ONLY)
        )

        // Setup menu
        fileMenu.add(openFile)
        fileMenu.add(newProject)
        fileMenu.add(exit)
        tableMenu.add(openTable)
        tableMenu.add(saveTable)
        tableMenu.add(reloadTable)
        offsetMenu.add(openOffsets)
        offsetMenu.add(saveOffsets)
        offsetMenu.add(extract)
        offsetMenu.add(nextOffset)
        offsetMenu.add(prevOffset)
        offsetMenu.add(clearOffsets)
        toolsMenu.add(goTo)
        toolsMenu.add(searchRelative)
        toolsMenu.add(find)
        toolsMenu.add(searchAll)
        viewMenu.add(view16Cols)
        viewMenu.add(view16Rows)
        helpMenu.add(help)
        helpMenu.add(about)
        menuBar.add(fileMenu)
        menuBar.add(tableMenu)
        menuBar.add(offsetMenu)
        menuBar.add(toolsMenu)
        menuBar.add(viewMenu)
        menuBar.add(helpMenu)

        // Actions
        setActions()

        // Accelerators
        openFile!!.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK)
        newProject!!.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK)
        exit!!.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK)
        goTo!!.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK)
        openTable!!.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK)
        saveTable!!.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK)
        reloadTable!!.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0)
        searchRelative!!.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK)
        searchAll!!.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK)
        extract!!.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK)
        find!!.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK)
        openOffsets!!.accelerator =
            KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK or InputEvent.SHIFT_DOWN_MASK)
        saveOffsets!!.accelerator =
            KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK or InputEvent.SHIFT_DOWN_MASK)
        nextOffset!!.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0)
        prevOffset!!.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)
        clearOffsets!!.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK)
        about!!.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0)
        help!!.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0)
        view16Cols!!.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0)
        view16Rows!!.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0)
    }

    /**
     * Next offset.
     */
    private fun nextOffset() {
        offEntries.sort()
        // We stop at the first whose start is bigger than our offset
        offEntries.forEach { entry ->
            if (entry.start > offset) {
                offset = entry.start
                refreshAll()
                return
            }
        }
    }

    /**
     * Prev offset.
     */
    private fun prevOffset() {
        offEntries.sortWith(Collections.reverseOrder())
        for (entry in offEntries) {
            if (entry.start < offset) {
                offset = entry.start
                refreshAll()
                break
            }
        }
    }

    /**
     * Clean offsets.
     */
    private fun cleanOffsets() {
        if (GuiUtils.confirmActionAlert(
                rb!!.getString(KeyConstants.KEY_CONFIRM_ACTION_TITLE),
                rb!!.getString(KeyConstants.KEY_CONFIRM_ACTION)
            )
        ) {
            offEntries.clear()
            offsetFile = File(Constants.EMPTY_OFFSET_FILE)
            refreshAll()
        }
    }

    /**
     * Sets the actions.
     */
    private fun setActions() {
        view16Cols!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_16COLS_MENUITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                refreshViewMode()
            }
        }
        view16Rows!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_16ROWS_MENUITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                refreshViewMode()
            }
        }
        help!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_HELP_MENUITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                JOptionPane.showMessageDialog(
                    help,
                    rb!!.getString(KeyConstants.KEY_HELP_DESC),
                    rb!!.getString(KeyConstants.KEY_HELP_MENUITEM),
                    JOptionPane.INFORMATION_MESSAGE
                )
            }
        }
        about!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_ABOUT_MENUITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                JOptionPane.showMessageDialog(
                    about,
                    rb!!.getString(KeyConstants.KEY_ABOUT_DESC),
                    rb!!.getString(KeyConstants.KEY_ABOUT_MENUITEM),
                    JOptionPane.INFORMATION_MESSAGE
                )
            }
        }
        extract!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_EXTRACT_MENUITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                val outFileName = tableFile!!.name.replace(
                    FileUtils.getFileExtension(tableFile!!.name).toRegex(),
                    Constants.EXTRACT_EXTENSION_NODOT
                )
                val fileChooser = JFileChooser()
                val selectedFile = File(outFileName)
                fileChooser.selectedFile = selectedFile
                var parent = selectedFile.parentFile
                if (parent == null) {
                    parent = hexFile!!.parentFile
                }
                if (parent == null) {
                    parent = tableFile!!.parentFile
                }
                fileChooser.currentDirectory = parent
                fileChooser.fileFilter = extOnlyFileFilter
                fileChooser.approveButtonText = rb!!.getString(KeyConstants.KEY_SAVE_BUTTON)
                if (fileChooser.showSaveDialog(saveOffsets) == JFileChooser.APPROVE_OPTION && confirmSelectedFile(
                        fileChooser.selectedFile
                    )
                ) {
                    runCatching {
                        FileUtils.extractAsciiFile(
                            hexTable,
                            fileBytes,
                            fileChooser.selectedFile.absolutePath,
                            offEntries,
                            false
                        )
                    }.onFailure {
                        Utils.logException(it)
                    }
                }
                refreshAll()
            }
        }
        nextOffset!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_NEXT_RANGE_MENU_ITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                nextOffset()
            }
        }
        prevOffset!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_PREV_RANGE_MENU_ITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                prevOffset()
            }
        }
        clearOffsets!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_CLEAN_OFFSETS)) {
            override fun actionPerformed(e: ActionEvent) {
                cleanOffsets()
            }
        }
        exit!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_EXIT_MENU_ITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                closeApp()
            }
        }
        openFile!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_OPEN_FILE_MENUITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                val fileChooser = JFileChooser()
                var parent = hexFile!!.parentFile
                if (parent == null) {
                    parent = tableFile!!.parentFile
                }
                fileChooser.currentDirectory = parent
                if (fileChooser.showOpenDialog(openFile) == JFileChooser.APPROVE_OPTION) {
                    reloadHexFile(fileChooser.selectedFile)
                }
            }
        }
        newProject!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_NEW_PROJECT_MENUITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                showProjectWindow()
            }
        }
        goTo!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_GOTO_MENUITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                val s = JOptionPane.showInputDialog(rb!!.getString(KeyConstants.KEY_OFFSET_INPUT))
                if (s != null && s.isNotEmpty()) {
                    try {
                        if (s.startsWith(DEC_STARTS)) {
                            offset = s.substring(DEC_STARTS.length).toInt()
                        } else {
                            offset = s.toInt(Constants.HEX_RADIX)
                        }
                    } catch (e1: NumberFormatException) {
                        // Do nothing
                    }
                    refreshAll()
                }
            }
        }
        searchAll!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_SEARCH_ALL_MENUITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                showSearchAllWindow()
            }
        }
        searchRelative!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_SEARCH_RELATIVE_MENUITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                val searchString = JOptionPane.showInputDialog(rb!!.getString(KeyConstants.KEY_SEARCH_RELATIVE))
                if (searchString != null && searchString.isNotEmpty()) {
                    try {
                        val results =
                            FileUtils.multiSearchRelative8Bits(fileBytes, searchString, SEARCH_JOKER_EXPANSIONS)
                        if (results.isEmpty()) {
                            JOptionPane.showMessageDialog(
                                help,
                                rb!!.getString(KeyConstants.KEY_NO_RESULTS_DESC),
                                rb!!.getString(KeyConstants.KEY_NO_RESULTS_TITLE),
                                JOptionPane.INFORMATION_MESSAGE
                            )
                        } else {
                            searchResults!!.setListData(results.toTypedArray())
                            resultsWindow!!.pack()
                            resultsWindow!!.setLocationRelativeTo(resultsWindow!!.parent)
                            resultsWindow!!.isVisible = true
                        }
                    } catch (e1: Exception) {
                        JOptionPane.showMessageDialog(
                            searchRelative,
                            rb!!.getString(KeyConstants.KEY_SEARCH_RELATIVE_MIN_LENGTH)
                        )
                    }
                    vsb!!.value = offset
                }
            }
        }
        find!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_FIND_MENUITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                val searchString = JOptionPane.showInputDialog(rb!!.getString(KeyConstants.KEY_FIND))
                if (searchString != null && searchString.isNotEmpty()) {
                    try {
                        val results = FileUtils.multiFindString(
                            fileBytes,
                            hexTable,
                            searchString,
                            true,
                            SEARCH_JOKER_EXPANSIONS
                        )
                        if (results.isEmpty()) {
                            JOptionPane.showMessageDialog(
                                help,
                                rb!!.getString(KeyConstants.KEY_NO_RESULTS_DESC),
                                rb!!.getString(KeyConstants.KEY_NO_RESULTS_TITLE),
                                JOptionPane.INFORMATION_MESSAGE
                            )
                        } else {
                            searchResults!!.setListData(results.toTypedArray())
                            resultsWindow!!.pack()
                            resultsWindow!!.setLocationRelativeTo(resultsWindow!!.parent)
                            resultsWindow!!.isVisible = true
                        }
                    } catch (e1: Exception) {
                        JOptionPane.showMessageDialog(searchRelative, rb!!.getString(KeyConstants.KEY_FIND_MIN_LENGTH))
                    }
                    vsb!!.value = offset
                }
            }
        }
        openTable!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_OPEN_TABLE_MENU_ITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                val fileChooser = JFileChooser()
                var parent = tableFile!!.parentFile
                if (parent == null) {
                    parent = hexFile!!.parentFile
                }
                fileChooser.currentDirectory = parent
                fileChooser.fileFilter = tableFilter
                val result = fileChooser.showOpenDialog(openTable)
                if (result == JFileChooser.APPROVE_OPTION) {
                    reloadTableFile(fileChooser.selectedFile)
                }
            }
        }
        saveTable!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_SAVE_TABLE_MENUITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                val fileChooser = JFileChooser()
                fileChooser.selectedFile = tableFile
                var parent = tableFile!!.parentFile
                if (parent == null) {
                    parent = hexFile!!.parentFile
                }
                fileChooser.currentDirectory = parent
                fileChooser.approveButtonText = rb!!.getString(KeyConstants.KEY_SAVE_BUTTON)
                fileChooser.fileFilter = tableFilter
                val result = fileChooser.showSaveDialog(saveTable)
                if (result == JFileChooser.APPROVE_OPTION) {
                    val accepted = confirmSelectedFile(fileChooser.selectedFile)
                    if (accepted) {
                        tableFile = fileChooser.selectedFile
                        if (!tableFile!!.absolutePath.endsWith(EXTENSION_TABLE)) {
                            tableFile = File(tableFile!!.absolutePath + EXTENSION_TABLE)
                        }
                        try {
                            FileUtils.writeFileAscii(tableFile!!.absolutePath, hexTable.toAsciiTable())
                        } catch (e1: Exception) {
                            Utils.logException(e1)
                        }
                    }
                    refreshAll()
                }
            }
        }
        reloadTable!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_RELOAD_TABLE_MENUITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                if (tableFile != null && tableFile!!.exists()) {
                    reloadTableFile(tableFile!!)
                }
            }
        }
        openOffsets!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_OPEN_OFFSETS_MENUITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                val fileChooser = JFileChooser()
                var parent = offsetFile.parentFile
                if (parent == null) {
                    parent = hexFile!!.parentFile
                }
                if (parent == null) {
                    parent = tableFile!!.parentFile
                }
                fileChooser.currentDirectory = parent
                fileChooser.fileFilter = offsetFileFilter
                val result = fileChooser.showOpenDialog(openOffsets)
                if (result == JFileChooser.APPROVE_OPTION) {
                    if (fileChooser.selectedFile.name.endsWith(EXTENSION_OFFSET)) {
                        reloadOffsetsFile(fileChooser.selectedFile)
                    } else {
                        reloadExtAsOffsetsFile(fileChooser.selectedFile)
                    }
                }
            }
        }
        saveOffsets!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_SAVE_OFFSETS_MENUITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                val fileChooser = JFileChooser()
                val selectedFile: File
                if (offsetFile.absolutePath.endsWith(Constants.EXTRACT_EXTENSION)) {
                    selectedFile = File(
                        FileUtils.getFilePath(offsetFile) + File.separator + tableFile!!.name.replace(
                            Constants.TBL_EXTENSION_REGEX.toRegex(),
                            Constants.OFFSET_EXTENSION
                        )
                    )
                } else {
                    selectedFile = offsetFile
                }
                fileChooser.selectedFile = selectedFile
                var parent = selectedFile.parentFile
                if (parent == null) {
                    parent = hexFile!!.parentFile
                }
                if (parent == null) {
                    parent = tableFile!!.parentFile
                }
                fileChooser.currentDirectory = parent
                fileChooser.fileFilter = offsetOnlyFileFilter
                fileChooser.approveButtonText = rb!!.getString(KeyConstants.KEY_SAVE_BUTTON)
                if (fileChooser.showSaveDialog(saveOffsets) == JFileChooser.APPROVE_OPTION && confirmSelectedFile(
                        fileChooser.selectedFile
                    )
                ) {
                    offsetFile = fileChooser.selectedFile
                    if (!offsetFile.absolutePath.endsWith(EXTENSION_OFFSET)) {
                        offsetFile = File(offsetFile.absolutePath + EXTENSION_OFFSET)
                    }
                    runCatching {
                        val offEntriesNotNull = offEntries
                        offEntriesNotNull.sort()
                        FileUtils.writeFileAscii(
                            offsetFile.absolutePath,
                            Utils.toFileString(offEntriesNotNull.toMutableList())
                        )
                    }.onFailure { Utils.logException(it) }
                }
                refreshAll()
            }
        }
        setActionsNewPrjWin()
        setActionsAllStringsWin()
    }

    /**
     * setActionsNewPrjWin.
     */
    private fun setActionsNewPrjWin() {
        newPrjWinCreateButton = JButton(rb!!.getString(KeyConstants.KEY_NEW_PRJ_CREA_BUT))
        newPrjWinCancelButton = JButton(rb!!.getString(KeyConstants.KEY_NEW_PRJ_CLOSE_BUT))
        newPrjWinSearchFileButton = JButton(rb!!.getString(KeyConstants.KEY_FIND_MENUITEM))
        newPrjWinSearchFileButton!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_FIND_MENUITEM)) {
            override fun actionPerformed(e: ActionEvent) {
                val fileChooser = JFileChooser()
                val result = fileChooser.showOpenDialog(openFile)
                if (result == JFileChooser.APPROVE_OPTION) {
                    val file = fileChooser.selectedFile
                    newPrjWinFileInput!!.text = file.name
                    projectFile = file
                    selectProjectFileType(projectFile)
                    newPrjWinNameInput!!.text = ProjectUtils.getProjectName(projectFile!!.name)
                }
            }
        }
        newPrjWinCreateButton!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_NEW_PRJ_CREA_BUT)) {
            override fun actionPerformed(e: ActionEvent) {
                var formErrors = false
                try {
                    val name = newPrjWinNameInput!!.text
                    val fileName = newPrjWinFileInput!!.text
                    formErrors =
                        (
                                (name == null) || (name.length < NEW_PROJECT_NAME_MIN_LENGTH) || (fileName == null) || fileName.isEmpty() || !Utils.isValidFileName(
                                    fileName
                                ) || !Utils.isValidFileName(name)
                                )
                    if (formErrors) {
                        JOptionPane.showMessageDialog(
                            null,
                            rb!!.getString(KeyConstants.KEY_NEW_PRJ_ERRORS_MSG),
                            rb!!.getString(KeyConstants.KEY_ERROR_TITLE),
                            JOptionPane.ERROR_MESSAGE
                        )
                    } else {
                        newPrjWin!!.cursor = Cursor(Cursor.WAIT_CURSOR)
                        disableProjectWindow()
                        newPrjWin!!.repaint()
                        ProjectUtils.createNewProject(
                            name,
                            fileName,
                            (newPrjWinFileTypeOpt!!.selectedItem as Map.Entry<String?, String?>).value.orEmpty(),
                            projectFile
                        )
                        newPrjWin!!.isVisible = false
                        JOptionPane.showMessageDialog(
                            asciiTextArea!!.parent,
                            rb!!.getString(KeyConstants.KEY_NEW_PRJ_GENERATING_MSG),
                            rb!!.getString(KeyConstants.KEY_NEW_PRJ_GENERATING_MSG),
                            JOptionPane.INFORMATION_MESSAGE
                        )
                    }
                } catch (e1: Exception) {
                    Utils.logException(e1)
                    JOptionPane.showMessageDialog(
                        null,
                        rb!!.getString(KeyConstants.KEY_ERROR),
                        rb!!.getString(KeyConstants.KEY_ERROR_TITLE),
                        JOptionPane.ERROR_MESSAGE
                    )
                } finally {
                    newPrjWin!!.cursor = Cursor(Cursor.DEFAULT_CURSOR)
                    if (!formErrors) {
                        enableProjectWindow()
                    }
                }
            }
        }
        newPrjWinCancelButton!!.action = object : AbstractAction(rb!!.getString(KeyConstants.KEY_NEW_PRJ_CLOSE_BUT)) {
            override fun actionPerformed(e: ActionEvent) {
                newPrjWin!!.isVisible = false
            }
        }
    }

    /**
     * Sets the actions all strings win.
     */
    private fun setActionsAllStringsWin() {
        searchAllWinSearchButton = JButton(rb!!.getString(KeyConstants.KEY_SEARCH_ALL_WIN_SEARCH_BUTTON))
        searchAllWinCancelButton = JButton(rb!!.getString(KeyConstants.KEY_SEARCH_ALL_WIN_CANCEL_BUTTON))
        searchAllWinCancelButton!!.action =
            object : AbstractAction(rb!!.getString(KeyConstants.KEY_SEARCH_ALL_WIN_CANCEL_BUTTON)) {
                override fun actionPerformed(e: ActionEvent) {
                    if (searchAllThread != null) {
                        searchAllThread!!.interrupt()
                        searchAllThreadError = true
                    }
                    SwingUtilities.invokeLater {
                        searchAllWinProgressBar!!.value = 0
                        enableSearchAllWindow()
                    }
                    cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
                }
            }
        searchAllWinSearchButton!!.action =
            object : AbstractAction(rb!!.getString(KeyConstants.KEY_SEARCH_ALL_WIN_SEARCH_BUTTON)) {
                private fun runSearchAllThreadAction() {
                    searchAllThreadAction()
                }

                override fun actionPerformed(e: ActionEvent) {
                    SwingUtilities.invokeLater {
                        disableSearchAllWindow()
                        searchAllWinProgressBar!!.value = SEARCH_ALL_MIN_PROGRESS
                    }
                    if ((
                                (searchAllWinEndCharsInput!!.text != null) &&
                                        (searchAllWinEndCharsInput!!.text.isNotEmpty()) &&
                                        searchAllWinEndCharsInput!!.text.matches(
                                            REGEXP_OFFSET_ENTRIES.toRegex()
                                        )
                                )
                    ) {
                        Thread { runSearchAllThreadAction() }.start()
                    } else {
                        JOptionPane.showMessageDialog(
                            asciiTextArea!!.parent,
                            rb!!.getString(KeyConstants.KEY_ALERT_INVALID_ENDCHARS),
                            rb!!.getString(KeyConstants.KEY_ALERT_INVALID_ENDCHARS_TITLE),
                            JOptionPane.ERROR_MESSAGE
                        )
                        SwingUtilities.invokeLater {
                            enableSearchAllWindow()
                            searchAllWinProgressBar!!.value = SEARCH_ALL_MIN_PROGRESS
                        }
                    }
                }
            }
    }

    /**
     * Search all thread action.
     */
    private fun searchAllThreadAction() {
        cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
        val extractFile = hexFile!!.name + Constants.EXTRACT_EXTENSION
        searchAllThreadError = false
        searchAllThread = Thread {
            try {
                var file = File(Constants.DEFAULT_DICT)
                if (!file.exists()) {
                    file = File(Constants.PARENT_DIR + Constants.DEFAULT_DICT)
                }
                if (file.exists()) {
                    FileUtils.searchAllStrings(
                        hexTable,
                        fileBytes,
                        searchAllWinSkipCharsOpt!!.selectedIndex,
                        searchAllWinEndCharsInput!!.text,
                        file.absolutePath,
                        extractFile
                    )
                } else {
                    searchAllThreadError = true
                }
            } catch (e: Exception) {
                searchAllThreadError = true
                Utils.logException(e)
            }
        }
        searchAllThread!!.start()
        synchronized(searchAllLock) {
            while (((searchAllThread != null) && searchAllThread!!.isAlive && !searchAllThread!!.isInterrupted)) {
                try {
                    searchAllLock.wait(50)
                    SwingUtilities.invokeLater {
                        searchAllWinProgressBar!!.value = hexTable.searchPercent.toInt()
                    }
                    searchAllLock.wait(50)
                } catch (ex: InterruptedException) {
                    searchAllThreadError = true
                    LOGGER.log(
                        Level.SEVERE,
                        "Exception occurred:",
                        ex
                    )
                    searchAllThread!!.interrupt()
                }
            }
        }
        cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
        if (searchAllThreadError) {
            JOptionPane.showMessageDialog(
                searchAllStringsWin,
                rb!!.getString(KeyConstants.KEY_SEARCH_ALL_WIN_ERROR),
                rb!!.getString(KeyConstants.KEY_SEARCH_ALL_WIN_ERROR),
                JOptionPane.INFORMATION_MESSAGE
            )
        } else {
            JOptionPane.showMessageDialog(
                searchAllStringsWin,
                rb!!.getString(KeyConstants.KEY_SEARCHED_ALL_DESC) + extractFile,
                rb!!.getString(KeyConstants.KEY_SEARCHED_ALL_TITLE),
                JOptionPane.INFORMATION_MESSAGE
            )
            searchAllStringsWin!!.isVisible = false
        }
        SwingUtilities.invokeLater {
            enableSearchAllWindow()
            searchAllWinProgressBar!!.value = SEARCH_ALL_MIN_PROGRESS
        }
    }

    /**
     * Confirm selected file.
     *
     * @param selectedFile the selected file
     * @return true, if successful
     */
    private fun confirmSelectedFile(selectedFile: File?): Boolean {
        var accepted = true
        if (selectedFile != null && selectedFile.exists()) {
            accepted = GuiUtils.confirmActionAlert(
                rb!!.getString(KeyConstants.KEY_CONFIRM_ACTION_TITLE),
                rb!!.getString(KeyConstants.KEY_CONFIRM_FILE_OVERWRITE_ACTION)
            )
        }
        return accepted
    }

    /**
     * Select project file type.
     *
     * @param file the file
     */
    private fun selectProjectFileType(file: File?) {
        if (file != null) {
            val extension = FileUtils.getFileExtension(file)
            newPrjWinFileTypeOpt!!.selectedItem = otherEntry
            if (Constants.EXTENSIONS_MEGADRIVE.contains(extension)) {
                newPrjWinFileTypeOpt!!.selectedItem = smdEntry
            }
            if (Constants.EXTENSIONS_SNES.contains(extension)) {
                newPrjWinFileTypeOpt!!.selectedItem = snesEntry
            }
            if (Constants.EXTENSIONS_GB.contains(extension)) {
                newPrjWinFileTypeOpt!!.selectedItem = gbEntry
            }
            if (Constants.EXTENSIONS_TAP.contains(extension)) {
                newPrjWinFileTypeOpt!!.selectedItem = tapEntry
            }
            if (Constants.EXTENSIONS_TZX_CDT.contains(extension)) {
                newPrjWinFileTypeOpt!!.selectedItem = tzxEntry
            }
            if (Constants.EXTENSIONS_SMS.contains(extension)) {
                newPrjWinFileTypeOpt!!.selectedItem = smsEntry
            }
        }
    }

    /**
     * Enable project window.
     */
    private fun enableProjectWindow() {
        newPrjWinNameInput!!.isEnabled = true
        newPrjWinFileInput!!.isEnabled = true
        newPrjWinFileTypeOpt!!.isEnabled = true
        newPrjWinCreateButton!!.isEnabled = true
        newPrjWinCancelButton!!.isEnabled = true
    }

    /**
     * Clean project window.
     */
    private fun cleanProjectWindow() {
        newPrjWinNameInput!!.text = Constants.EMPTY
        newPrjWinFileInput!!.text = Constants.EMPTY
        newPrjWinFileTypeOpt!!.selectedIndex = 0
        projectFile = null
    }

    /**
     * Disable project window.
     */
    private fun disableProjectWindow() {
        newPrjWinNameInput!!.isEnabled = false
        newPrjWinFileInput!!.isEnabled = false
        newPrjWinFileTypeOpt!!.isEnabled = false
        newPrjWinCreateButton!!.isEnabled = false
        newPrjWinCancelButton!!.isEnabled = false
    }

    /**
     * Creates the project window.
     */
    private fun showProjectWindow() {
        cleanProjectWindow()
        enableProjectWindow()
        if (hexFile != null) {
            newPrjWinFileInput!!.text = hexFile!!.name
            selectProjectFileType(hexFile)
            newPrjWinNameInput!!.text = ProjectUtils.getProjectName(hexFile!!.name)
            projectFile = hexFile
        }
        newPrjWin!!.pack()
        newPrjWin!!.setLocationRelativeTo(this)
        newPrjWin!!.isVisible = true
    }

    /**
     * Enable search all window.
     */
    private fun enableSearchAllWindow() {
        searchAllThread = null
        searchAllWinSkipCharsOpt!!.isEnabled = true
        searchAllWinEndCharsInput!!.isEnabled = true
        searchAllWinSearchButton!!.isEnabled = true
        searchAllWinCancelButton!!.isEnabled = false
    }

    /**
     * Clean search all window.
     */
    private fun cleanSearchAllWindow() {
        // Valores por defecto
        searchAllWinSkipCharsOpt!!.selectedIndex = SEARCH_ALL_DEFAULT_CHARS_INDEX
        searchAllWinEndCharsInput!!.text = SEARCH_ALL_DEFAULT_END_CHARS
        searchAllWinProgressBar!!.value = SEARCH_ALL_MIN_PROGRESS
    }

    /**
     * Disable search all window.
     */
    private fun disableSearchAllWindow() {
        searchAllWinSkipCharsOpt!!.isEnabled = false
        searchAllWinEndCharsInput!!.isEnabled = false
        searchAllWinSearchButton!!.isEnabled = false
        searchAllWinCancelButton!!.isEnabled = true
    }

    /**
     * Show search all window.
     */
    private fun showSearchAllWindow() {
        enableSearchAllWindow()
        cleanSearchAllWindow()
        searchAllStringsWin!!.pack()
        searchAllStringsWin!!.setLocationRelativeTo(this)
        searchAllStringsWin!!.isVisible = true
    }

    /**
     * Reload table file.
     *
     * @param selectedFile the selected file
     */
    private fun reloadTableFile(selectedFile: File) {
        tableFile = selectedFile
        try {
            hexTable = HexTable(tableFile!!.absolutePath)
        } catch (e1: Exception) {
            Utils.logException(e1)
        }
        refreshAll()
    }

    /**
     * Reload offsets file.
     *
     * @param selectedFile the selected file
     */
    private fun reloadOffsetsFile(selectedFile: File) {
        offsetFile = selectedFile
        try {
            offEntries = Utils.getOffsets(FileUtils.getCleanOffsets(offsetFile.absolutePath)).toMutableList()
        } catch (e1: Exception) {
            Utils.logException(e1)
        }
        refreshAll()
    }

    /**
     * Reload ext as offsets file.
     *
     * @param selectedFile the selected file
     */
    private fun reloadExtAsOffsetsFile(selectedFile: File) {
        offsetFile = selectedFile
        try {
            offEntries = Utils.getOffsets(
                FileUtils.getCleanOffsetsString(
                    FileUtils.cleanExtractedFile(selectedFile.absolutePath)
                )
            ).toMutableList()
        } catch (e1: Exception) {
            Utils.logException(e1)
        }
        refreshAll()
    }

    /**
     * Reload hex file.
     *
     * @param selectedFile the selected file
     */
    private fun reloadHexFile(selectedFile: File) {
        hexFile = selectedFile
        try {
            val newFileBytes = Files.readAllBytes(Paths.get(hexFile!!.absolutePath))
            if (newFileBytes.size >= viewSize) {
                fileBytes = newFileBytes
            } else {
                fileBytes = ByteArray(viewSize)
                System.arraycopy(newFileBytes, 0, fileBytes, 0, newFileBytes.size)
            }
        } catch (e1: Exception) {
            Utils.logException(e1)
        }
        offset = 0
        asciiTextArea!!.caretPosition = 0
        refreshAll()
    }

    /**
     * Sets the look and feel.
     */
    private fun setLookAndFeel() {
        runCatching {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
            )
        }.onFailure {
            Utils.logException(it)
        }
        rb = ResourceBundle.getBundle(Constants.RB_NAME, Locale.getDefault())
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    override fun actionPerformed(e: ActionEvent) {
        // None
    }

    companion object {
        private val LOGGER = Logger.getLogger(HexViewer::class.java.name)

        /**  serialVersion UID.  */
        private const val serialVersionUID = -4438721009010549343L

        /** The Constant DEFAULT_SPLIT_VALUE.  */
        private const val DEFAULT_SPLIT_VALUE = 1024

        /** The Constant NEW_PROJECT_NAME_MIN_LENGTH.  */
        private const val NEW_PROJECT_NAME_MIN_LENGTH = 4

        /** The Constant NEW_PROJECT_GRID_ROWS.  */
        private const val NEW_PROJECT_GRID_ROWS = 4

        /** The Constant NEW_PROJECT_GRID_COLS.  */
        private const val NEW_PROJECT_GRID_COLS = 3

        private const val NEW_PROJECT_GRID_HOR_GAP = 10

        private const val NEW_PROJECT_GRID_VERT_GAP = 10

        /** The Constant SEARCH_ALL_GRID_ROWS.  */
        private const val SEARCH_ALL_GRID_ROWS = 4

        /** The Constant SEARCH_ALL_GRID_COLS.  */
        private const val SEARCH_ALL_GRID_COLS = 2

        private const val SEARCH_ALL_GRID_HOR_GAP = 10

        private const val SEARCH_ALL_GRID_VERT_GAP = 10

        /** The Constant SEARCH_ALL_END_CHARS_LENGTH.  */
        private const val SEARCH_ALL_END_CHARS_LENGTH = 40

        /** The Constant MAX_COLS_AND_ROWS.  */
        private const val MAX_COLS_AND_ROWS = 32

        /** The Constant MIN_COLS_AND_ROWS.  */
        private const val MIN_COLS_AND_ROWS = 16

        /** The Constant HEX_STARTS.  */
        private const val HEX_STARTS = "0x"

        /** The Constant DEC_STARTS.  */
        private const val DEC_STARTS = "d"

        /** The Constant DEFAULT_TABLE.  */
        private const val DEFAULT_TABLE = "ascii.tbl"

        /** The Constant DEFAULT_HEXFILE.  */
        private const val DEFAULT_HEXFILE = "empty.hex"

        /** The Constant EXTENSION_TABLE.  */
        private const val EXTENSION_TABLE = ".tbl"

        /** The Constant EXTENSION_OFFSET.  */
        private const val EXTENSION_OFFSET = ".off"

        /** The Constant EXTENSION_EXTRACTION.  */
        private const val EXTENSION_EXTRACTION = ".ext"

        /** The Constant SEARCH_RES_DIMENSION.  */
        private val SEARCH_RES_DIMENSION = Dimension(600, 200)

        /** The Constant SEARCHRES_FONT_SIZE.  */
        private const val SEARCHRES_FONT_SIZE = 18

        /** The Constant REGEXP_OFFSET_ENTRIES.  */
        private const val REGEXP_OFFSET_ENTRIES = "[0-9A-Fa-f]{2}(-[0-9A-Fa-f]{2})*"

        /** The Constant DIMENSION_0_0.  */
        private val DIMENSION_0_0 = Dimension(0, 0)

        /** The blue painter.  */
        private val BLUE_PAINTER: Highlighter.HighlightPainter = DefaultHighlightPainter(Color.BLUE)

        /** The light gray painter.  */
        private val LGRAY_PAINTER: Highlighter.HighlightPainter = DefaultHighlightPainter(Color.LIGHT_GRAY)

        /** The yellow painter.  */
        private val YELLOW_PAINTER: Highlighter.HighlightPainter = DefaultHighlightPainter(Color.YELLOW)

        /** The orange painter.  */
        private val ORANGE_PAINTER: Highlighter.HighlightPainter = DefaultHighlightPainter(Color.ORANGE)

        /** The Constant SEARCH_ALL_DEFAULT_CHARS_INDEX.  */
        private const val SEARCH_ALL_DEFAULT_CHARS_INDEX = 4

        /** The Constant SEARCH_ALL_DEFAULT_END_CHARS.  */
        private const val SEARCH_ALL_DEFAULT_END_CHARS = "FF"

        /** The Constant SEARCH_ALL_MIN_PROGRESS.  */
        private const val SEARCH_ALL_MIN_PROGRESS = 0

        /** The Constant SEARCH_ALL_MAX_PROGRESS.  */
        private const val SEARCH_ALL_MAX_PROGRESS = 100

        /** The Constant OFFSET_LABEL_LENGTH.  */
        private const val OFFSET_LABEL_LENGTH = 50

        /** The BASE_FONT for the application.  */
        private val BASE_FONT = Font(Font.MONOSPACED, Font.PLAIN, 13)

        /** The SEARCH_JOKER_EXPANSIONS for searches.  */
        private const val SEARCH_JOKER_EXPANSIONS = 8

        /** The Constant ICON16.  */
        private val ICON16 = HexViewer::class.java.getResource("/icon/rom16.png")

        /** The Constant ICON32.  */
        private val ICON32 = HexViewer::class.java.getResource("/icon/rom32.png")

        /** The Constant ICON96.  */
        private val ICON96 = HexViewer::class.java.getResource("/icon/rom96.png")

        /**
         * Gets the visible offsets.
         *
         * @param offset the starting offset
         * @param columns num of columns
         * @param rows num of rows
         * @return the visible offsets
         */
        private fun getVisibleOffsets(offset: Int, columns: Int, rows: Int): String {
            val sb = StringBuilder((Constants.HEX_ADDR_SIZE + HEX_STARTS.length + Constants.SPACE_STR.length) * rows)
            for (i in 0 until rows) {
                sb.append(HEX_STARTS)
                sb.append(Utils.intToHexString(offset + i * columns, Constants.HEX_ADDR_SIZE))
                sb.append(Constants.S_NEWLINE)
            }
            return sb.toString()
        }

        /**
         * New HexViewer with inputFile and tableFile.
         *
         * @param inputFile the input file
         * @param tableFile the table file
         * @throws IOException the exception
         */
        @Throws(IOException::class)
        fun view(inputFile: String, tableFile: String) {
            Utils.log("Viewing Hex file \"$inputFile\"\n with table file: \"$tableFile\".")
            HexViewer(Files.readAllBytes(Paths.get(inputFile)), inputFile, HexTable(tableFile), tableFile)
        }

        /**
         * New HexViewer with inputFile and default table.
         *
         * @param inputFile the input file
         * @throws IOException the exception
         */
        @Throws(IOException::class)
        fun view(inputFile: String) {
            Utils.log("Viewing Hex file \"$inputFile\"\n with table file ascii.")
            HexViewer(Files.readAllBytes(Paths.get(inputFile)), inputFile, HexTable(0), DEFAULT_TABLE)
        }

        /**
         * New empty HexViewer.
         */
        fun view() {
            Utils.log("Viewing Hex file empty with table file ascii.")
            HexViewer(ByteArray(MAX_COLS_AND_ROWS * MAX_COLS_AND_ROWS), DEFAULT_HEXFILE, HexTable(0), DEFAULT_TABLE)
        }
    }
}
