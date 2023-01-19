package com.wave.hextractor.helper

import com.wave.hextractor.util.Utils
import java.awt.Color
import java.awt.Component
import java.awt.Container
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DropTargetEvent
import java.awt.dnd.DropTargetListener
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.PrintStream
import java.net.URI
import java.util.*
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.border.Border

/**
 * This class makes it easy to drag and drop files from the operating
 * system to a Java program. Any <tt>java.awt.Component</tt> can be
 * dropped onto, but only <tt>javax.swing.JComponent</tt>s will indicate
 * the drop event with a changed border.
 *
 *
 * To use this class, construct a new <tt>FileDrop</tt> by passing
 * it the target component and a <tt>Listener</tt> to receive notification
 * when file(s) have been dropped. Here is an example:
 *
 *
 * `<pre>
 * JPanel myPanel = new JPanel();
 * new FileDrop( myPanel, new FileDrop.Listener()
 * {   public void filesDropped( java.io.File[] files )
 * {
 * // handle file drop
 * ...
 * }   // end filesDropped
 * }); // end FileDrop.Listener
</pre>` *
 *
 *
 * You can specify the border that will appear when files are being dragged by
 * calling the constructor with a <tt>javax.swing.border.Border</tt>. Only
 * <tt>JComponent</tt>s will show any indication with a border.
 *
 *
 * You can turn on some debugging features by passing a <tt>PrintStream</tt>
 * object (such as <tt>System.out</tt>) into the full constructor. A <tt>null</tt>
 * value will result in no extra debugging information being output.
 *
 *
 *
 *
 * I'm releasing this code into the Public Domain. Enjoy.
 *
 *
 * *Original author: Robert Harder, rharder@usa.net*
 *
 * 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
 *
 * @author Robert Harder
 * @author rharder@users.sf.net
 * @version 1.0.1
 */
class FileDrop(
    out: PrintStream?,
    c: Component,
    dragBorder: Border?,
    recursive: Boolean,
    listener: Listener?
) {
    /** The normal border.  */
    private var normalBorder: Border? = null

    /** The drop listener.  */
    private var dropListener: DropTargetListener? = null

    /**
     * Constructs a [FileDrop] with a default light-blue border
     * and, if <var>c</var> is a [Container], recursively
     * sets all elements contained within as drop targets, though only
     * the top level container will change borders.
     *
     * @param c Component on which files will be dropped.
     * @param listener Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    constructor(c: Component, listener: Listener?) : this(
        null, // Logging stream
        c, // Drop target
        BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor), // Drag border
        true, // Recursive
        listener
    ) // end constructor

    /**
     * Constructor with a default border and the option to recursively set drop targets.
     * If your component is a <tt>java.awt.Container</tt>, then each of its children
     * components will also listen for drops, though only the parent will change borders.
     *
     * @param c Component on which files will be dropped.
     * @param recursive Recursively set children as drop targets.
     * @param listener Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    constructor(c: Component, recursive: Boolean, listener: Listener?) : this(
        null, // Logging stream
        c, // Drop target
        BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor), // Drag border
        recursive, // Recursive
        listener
    ) // end constructor

    /**
     * Constructor with a default border and debugging optionally turned on.
     * With Debugging turned on, more status messages will be displayed to
     * <tt>out</tt>. A common way to use this constructor is with
     * <tt>System.out</tt> or <tt>System.err</tt>. A <tt>null</tt> value for
     * the parameter <tt>out</tt> will result in no debugging output.
     *
     * @param out the out
     * @param c Component on which files will be dropped.
     * @param listener Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    constructor(out: PrintStream?, c: Component, listener: Listener?) : this(
        out, // Logging stream
        c, // Drop target
        BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor),
        false, // Recursive
        listener
    ) // end constructor

    /**
     * Constructor with a default border, debugging optionally turned on
     * and the option to recursively set drop targets.
     * If your component is a <tt>java.awt.Container</tt>, then each of its children
     * components will also listen for drops, though only the parent will change borders.
     * With Debugging turned on, more status messages will be displayed to
     * <tt>out</tt>. A common way to use this constructor is with
     * <tt>System.out</tt> or <tt>System.err</tt>. A <tt>null</tt> value for
     * the parameter <tt>out</tt> will result in no debugging output.
     *
     * @param out the out
     * @param c Component on which files will be dropped.
     * @param recursive Recursively set children as drop targets.
     * @param listener Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    constructor(
        out: PrintStream?,
        c: Component,
        recursive: Boolean,
        listener: Listener?
    ) : this(
        out, // Logging stream
        c, // Drop target
        BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor), // Drag border
        recursive, // Recursive
        listener
    ) // end constructor

    /**
     * Constructor with a specified border.
     *
     * @param c Component on which files will be dropped.
     * @param dragBorder Border to use on <tt>JComponent</tt> when dragging occurs.
     * @param listener Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    constructor(c: Component, dragBorder: Border?, listener: Listener?) : this(
        null, // Logging stream
        c, // Drop target
        dragBorder, // Drag border
        false, // Recursive
        listener
    ) // end constructor

    /**
     * Constructor with a specified border and the option to recursively set drop targets.
     * If your component is a <tt>java.awt.Container</tt>, then each of its children
     * components will also listen for drops, though only the parent will change borders.
     *
     * @param c Component on which files will be dropped.
     * @param dragBorder Border to use on <tt>JComponent</tt> when dragging occurs.
     * @param recursive Recursively set children as drop targets.
     * @param listener Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    constructor(
        c: Component,
        dragBorder: Border?,
        recursive: Boolean,
        listener: Listener?
    ) : this(null, c, dragBorder, recursive, listener) // end constructor

    /**
     * Constructor with a specified border and debugging optionally turned on.
     * With Debugging turned on, more status messages will be displayed to
     * <tt>out</tt>. A common way to use this constructor is with
     * <tt>System.out</tt> or <tt>System.err</tt>. A <tt>null</tt> value for
     * the parameter <tt>out</tt> will result in no debugging output.
     *
     * @param out PrintStream to record debugging info or null for no debugging.
     * @param c Component on which files will be dropped.
     * @param dragBorder Border to use on <tt>JComponent</tt> when dragging occurs.
     * @param listener Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    constructor(
        out: PrintStream?,
        c: Component,
        dragBorder: Border?,
        listener: Listener?
    ) : this(
        out, // Logging stream
        c, // Drop target
        dragBorder, // Drag border
        false, // Recursive
        listener
    ) // end constructor

    /**
     * Full constructor with a specified border and debugging optionally turned on.
     * With Debugging turned on, more status messages will be displayed to
     * <tt>out</tt>. A common way to use this constructor is with
     * <tt>System.out</tt> or <tt>System.err</tt>. A <tt>null</tt> value for
     * the parameter <tt>out</tt> will result in no debugging output.
     *
     * @param out PrintStream to record debugging info or null for no debugging.
     * @param c Component on which files will be dropped.
     * @param dragBorder Border to use on <tt>JComponent</tt> when dragging occurs.
     * @param recursive Recursively set children as drop targets.
     * @param listener Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    init {
        if (supportsDnD()) { // Make a drop listener
            dropListener = object : DropTargetListener {
                override fun dragEnter(evt: DropTargetDragEvent) {
                    log(out, "FileDrop: dragEnter event.")

                    // Is this an acceptable drag event?
                    if (isDragOk(out, evt)) {
                        // If it's a Swing component, set its border
                        if (c is JComponent) {
                            val jc = c
                            normalBorder = jc.border
                            log(out, "FileDrop: normal border saved.")
                            jc.border = dragBorder
                            log(out, "FileDrop: drag border set.")
                        } // end if: JComponent

                        // Acknowledge that it's okay to enter
                        evt.acceptDrag(DnDConstants.ACTION_COPY)
                        log(out, "FileDrop: event accepted.")
                    } // end if: drag ok
                    else { // Reject the drag event
                        evt.rejectDrag()
                        log(out, "FileDrop: event rejected.")
                    } // end else: drag not ok
                } // end dragEnter

                override fun dragOver(evt: DropTargetDragEvent) { // This is called continually as long as
                    // the mouse is
                    // over the drag target.
                } // end dragOver

                override fun drop(evt: DropTargetDropEvent) {
                    log(out, "FileDrop: drop event.")
                    try { // Get whatever was dropped
                        val tr = evt.transferable

                        // Is it a file list?
                        if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            // Say we'll take it.
                            evt.acceptDrop(DnDConstants.ACTION_COPY)
                            log(out, "FileDrop: file list accepted.")

                            // Get a useful list
                            val fileList = tr
                                .getTransferData(DataFlavor.javaFileListFlavor) as List<File>

                            // Alert listener to drop.
                            listener?.filesDropped(fileList.toTypedArray())

                            // Mark that drop is completed.
                            evt.dropTargetContext.dropComplete(true)
                            log(out, "FileDrop: drop complete.")
                        } // end if: file list
                        else // this section will check for a reader flavor.
                            {
                                val flavors = tr.transferDataFlavors
                                var handled = false
                                for (flavor in flavors) {
                                    if (flavor.isRepresentationClassReader) {
                                        // Say we'll take it.
                                        evt.acceptDrop(DnDConstants.ACTION_COPY)
                                        log(out, "FileDrop: reader accepted.")
                                        val reader = flavor.getReaderForText(tr)
                                        val br = BufferedReader(reader)
                                        listener?.filesDropped(createFileArray(br, out))

                                        // Mark that drop is completed.
                                        evt.dropTargetContext.dropComplete(true)
                                        log(out, "FileDrop: drop complete.")
                                        handled = true
                                        break
                                    }
                                }
                                if (!handled) {
                                    log(out, "FileDrop: not a file list or reader - abort.")
                                    evt.rejectDrop()
                                }
                                // END 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
                            } // end else: not a file list
                    } // end try
                    catch (io: IOException) {
                        log(out, "FileDrop: IOException - abort:")
                        io.printStackTrace(out)
                        evt.rejectDrop()
                    } // end catch IOException
                    catch (ufe: UnsupportedFlavorException) {
                        log(out, "FileDrop: UnsupportedFlavorException - abort:")
                        ufe.printStackTrace(out)
                        evt.rejectDrop()
                    } // end catch: UnsupportedFlavorException
                    finally {
                        // If it's a Swing component, reset its border
                        if (c is JComponent) {
                            c.border = normalBorder
                            log(out, "FileDrop: normal border restored.")
                        } // end if: JComponent
                    } // end finally
                } // end drop

                override fun dragExit(evt: DropTargetEvent) {
                    log(out, "FileDrop: dragExit event.")
                    // If it's a Swing component, reset its border
                    if (c is JComponent) {
                        c.border = normalBorder
                        log(out, "FileDrop: normal border restored.")
                    } // end if: JComponent
                } // end dragExit

                override fun dropActionChanged(evt: DropTargetDragEvent) {
                    log(out, "FileDrop: dropActionChanged event.")
                    // Is this an acceptable drag event?
                    if (isDragOk(out, evt)) {
                        evt.acceptDrag(DnDConstants.ACTION_COPY)
                        log(out, "FileDrop: event accepted.")
                    } // end if: drag ok
                    else {
                        evt.rejectDrag()
                        log(out, "FileDrop: event rejected.")
                    } // end else: drag not ok
                } // end dropActionChanged
            } // end DropTargetListener

            // Make the component (and possibly children) drop targets
            makeDropTarget(out, c, recursive)
        } // end if: supports dnd
        else {
            log(out, "FileDrop: Drag and drop is not supported with this JVM")
        } // end else: does not support DnD
    } // end constructor
    // END 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
    /**
     * Make drop target.
     *
     * @param out the out
     * @param c the c
     * @param recursive the recursive
     */
    private fun makeDropTarget(out: PrintStream?, c: Component, recursive: Boolean) {
        // Make drop target
        val dt = DropTarget()
        try {
            dt.addDropTargetListener(dropListener)
        } // end try
        catch (e: TooManyListenersException) {
            Utils.logException(e)
            log(out, "FileDrop: Drop will not work due to previous error. Do you have another listener attached?")
        } // end catch

        // Listen for hierarchy changes and remove the drop target when the parent gets
        // cleared out.
        // end hierarchyChanged
        c.addHierarchyListener {
            log(out, "FileDrop: Hierarchy changed.")
            val parent: Component? = c.parent
            if (parent == null) {
                c.dropTarget = null
                log(out, "FileDrop: Drop target cleared from component.")
            } // end if: null parent
            else {
                DropTarget(c, dropListener)
                log(out, "FileDrop: Drop target added to component.")
            } // end else: parent not null
        } // end hierarchy listener
        if (c.parent != null) {
            DropTarget(c, dropListener)
        }
        if (recursive && c is Container) {
            // Get the container

            // Get it's components
            val comps = c.components

            // Set it's components as listeners also
            for (comp in comps) {
                makeDropTarget(out, comp, true)
            }
        } // end if: recursively set components as listener
    } // end dropListener

    /**
     * Determine if the dragged data is a file list.
     *
     * @param out the out
     * @param evt the evt
     * @return true, if is drag ok
     */
    private fun isDragOk(out: PrintStream?, evt: DropTargetDragEvent): Boolean {
        var ok = false

        // Get data flavors being dragged
        val flavors = evt.currentDataFlavors

        // See if any of the flavors are a file list
        var i = 0
        while (!ok && i < flavors.size) {
            // BEGIN 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
            // Is the flavor a file list?
            val curFlavor = flavors[i]
            if (curFlavor.equals(DataFlavor.javaFileListFlavor) ||
                curFlavor.isRepresentationClassReader
            ) {
                ok = true
            }
            // END 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
            i++
        } // end while: through flavors

        // If logging is enabled, show data flavors
        if (out != null) {
            if (flavors.size == 0) {
                log(out, "FileDrop: no data flavors.")
            }
            i = 0
            while (i < flavors.size) {
                log(out, flavors[i].toString())
                i++
            }
        } // end if: logging enabled
        return ok
    } // end isDragOk
    /* ******** I N N E R I N T E R F A C E L I S T E N E R ******** */
    /**
     * Implement this inner interface to listen for when files are dropped. For example
     * your class declaration may begin like this:
     * `<pre>
     * public class MyClass implements FileDrop.Listener
     * ...
     * public void filesDropped( java.io.File[] files )
     * {
     * ...
     * }   // end filesDropped
     * ...
     </pre>` *
     *
     * @since 1.1
     */
    fun interface Listener {
        /**
         * This method is called when files have been successfully dropped.
         *
         * @param files An array of <tt>File</tt>s that were dropped.
         * @since 1.0
         */
        fun filesDropped(files: Array<File?>)
    } // end inner-interface Listener

    companion object {
        /** Discover if the running JVM is modern enough to have drag and drop.  */
        private var supportsDnD: Boolean? = null

        /** The default border color.  */
        private val defaultBorderColor = Color(0f, 0f, 1f, 0.25f)

        /**
         * Supports dn D.
         *
         * @return true, if successful
         */
        private fun supportsDnD(): Boolean { // Static Boolean
            if (supportsDnD == null) {
                val support: Boolean = try {
                    Class.forName("java.awt.dnd.DnDConstants")
                    true
                } // end try
                catch (e: Exception) {
                    false
                } // end catch
                supportsDnD = support
            } // end if: first time through
            return supportsDnD!!
        } // end supportsDnD

        /** The Constant ZERO_CHAR_STRING.  */ // BEGIN 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
        private const val ZERO_CHAR_STRING = "" + 0.toChar()

        /**
         * Creates the file array.
         *
         * @param bReader the b reader
         * @param out the out
         * @return the file[]
         */
        private fun createFileArray(bReader: BufferedReader, out: PrintStream?): Array<File?> {
            try {
                val list: MutableList<File?> = ArrayList()
                var line: String
                while (bReader.readLine().also { line = it } != null) {
                    readFileLine(out, list, line)
                }
                return list.toTypedArray()
            } catch (ex: IOException) {
                log(out, "FileDrop: IOException")
            }
            return arrayOfNulls(0)
        }

        private fun readFileLine(out: PrintStream?, list: MutableList<File?>, line: String) {
            try {
                // kde seems to append a 0 char to the end of the reader
                if (ZERO_CHAR_STRING == line) {
                    return
                }
                val file = File(URI(line))
                list.add(file)
            } catch (ex: Exception) {
                log(out, "Error with " + line + ": " + ex.message)
            }
        }

        /**
         * Outputs <tt>message</tt> to <tt>out</tt> if it's not null.
         *
         * @param out the out
         * @param message the message
         */
        private fun log(out: PrintStream?, message: String) { // Log message if requested
            out?.println(message)
        } // end log

        /**
         * Removes the drag-and-drop hooks from the component and optionally
         * from the all children. You should call this if you add and remove
         * components after you've set up the drag-and-drop.
         *
         * @param out Optional [PrintStream] for logging drag and drop messages
         * @param c The component to unregister
         * @param recursive Recursively unregister components within a container
         * @since 1.0
         */
        fun remove(out: PrintStream?, c: Component, recursive: Boolean) { // Make sure we
            // support dnd.
            if (supportsDnD()) {
                log(out, "FileDrop: Removing drag-and-drop hooks.")
                c.dropTarget = null
                if (recursive && c is Container) {
                    val comps = c.components
                    for (comp in comps) {
                        remove(out, comp, true)
                    }
                }
            }
        }
    }
}
