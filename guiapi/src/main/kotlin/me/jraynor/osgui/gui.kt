package me.jraynor.osgui

import imgui.ImGui
import imgui.ImGuiWindowClass
import imgui.ImVec2
import imgui.flag.*
import imgui.type.ImBoolean
import imgui.type.ImString

fun interface Renderer {
    fun render()
}


abstract class RendererElement<T : Renderer> : Renderer {
    var parent: RendererElement<*>? = null
        private set

    /**
     * {Optional} This will render the gui element and all of its children.
     * Only allows the callback to be set once. Doesn't allow the value to be set to null.
     */
    private var callback: (T.() -> Unit)? = null
        set(value) = //Only set the name if it is null
            if (value != null && field == null) field = value else {
                error("Cannot set name to $value because it is already set to ${field ?: "null"}")
            }


    private val children: MutableList<Renderer> = arrayListOf()

    fun render(init: T.() -> Unit) {
        callback = init
    }

    /**
     * This will add a child to the gui element
     */
    infix fun with(child: Renderer) {
        this.children.add(child)
        //Store a reference to the parent if the child is a renderer element
        if (child is RendererElement<*>) child.parent = this
    }

    /**
     * This will remove a child from the gui element
     */
    infix fun without(instance: Renderer): Boolean {
        if (instance is RendererElement<*>) instance.parent = null
        return children.remove(instance)
    }

    /**
     * This will add a child to the gui element
     */
    operator fun plusAssign(child: Renderer) = with(child)

    /**
     * Allow us to delegate the rendering of children to the element.
     */
    protected fun propagate() = children.forEach(Renderer::render)

    protected operator fun invoke(): Unit = callback?.invoke(this@RendererElement as T) ?: Unit
}


class Label : RendererElement<Label>() {
    /**
     * {Required} This will store contents of the label.
     */
    var contents: (() -> String)? = null
        set(value) = //Only set the name if it is null
            if (value != null && field == null) field = value else {
                error("Cannot set name to $value because it is already set to ${field ?: "null"}")
            }

    override fun render() {
        if (contents == null) error("Cannot render a label with a null contents")
        ImGui.text(contents!!())
    }

}

fun <T : RendererElement<T>> T.label(contents: String) {
    val label = Label()
    label.contents = { contents }
    this.with(label)
}


fun <T : RendererElement<T>> T.label(contents: () -> String) {
    val label = Label()
    label.contents = contents
    this.with(label)
}


fun window(init: Window.() -> Unit): Window {
    val window = Window()
    window.init()
    return window
}

fun window(name: String, init: Window.() -> Unit): Window {
    val window = Window()
    window.name = name
    window.init()
    return window
}

open class Window : RendererElement<Window>() {
    protected val shown = ImBoolean(false)
    private var pos: ImVec2? = null
    private var size: ImVec2? = null

    /**
     * {Required} This will store the name of the window.
     */
    var name: String? = null
        set(value) = //Only set the name if it is null
            if (value != null && field == null) field = value else {
                error("Cannot set name to $value because it is already set to ${field ?: "null"}")
            }
    var titleBarSize = 20f

    /**
     * {Optional} This will store the flags for the window. Defaults to none
     */
    var flags = Flags<Flag>()

    /**
     * This will show the window
     */
    fun show() = shown.set(true)

    /**
     * This will hide the window
     */
    fun hide() = shown.set(false)

    override fun render() {
        if (name == null) error("Cannot render a window with a null name")

        if (size != null) {
            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f)
            ImGui.pushStyleColor(ImGuiCol.WindowBg, 22, 22, 22, 255)
            ImGui.pushStyleColor(ImGuiCol.Text, 0, 0, 0, 0)
            ImGui.setNextWindowSize(size!!.x, 20f)
            if (ImGui.begin(
                    "$name##title_bar",
                    ImGuiWindowFlags.NoScrollbar or ImGuiWindowFlags.NoScrollWithMouse or ImGuiWindowFlags.NoResize or ImGuiWindowFlags.NoCollapse or ImGuiWindowFlags.NoSavedSettings or ImGuiWindowFlags.NoTitleBar
                )
            ) {
                ImGui.text(name)
                pos = ImGui.getWindowPos()
            }
            ImGui.end()
            ImGui.popStyleColor(2)
            ImGui.popStyleVar()
        }
        if (pos != null)
            ImGui.setNextWindowPos(pos!!.x, pos!!.y + 20)
        val topMost = ImGuiWindowClass().apply {
            viewportFlagsOverrideClear = ImGuiViewportFlags.TopMost
            setClassId(ImGui.getID("TopMost"))
        }


        ImGui.setNextWindowClass(topMost)
        if (ImGui.begin("##$name", flags.value or ImGuiWindowFlags.NoCollapse )) {
            size = ImGui.getWindowSize()
            //check if the mouse is in the bottom right of the window to allow resizing
            if (ImGui.isMouseHoveringRect(
                    ImGui.getWindowPos().x + ImGui.getWindowSize().x - 25,
                    ImGui.getWindowPos().y + ImGui.getWindowSize().y - 25,
                    ImGui.getWindowPos().x + ImGui.getWindowSize().x + 25,
                    ImGui.getWindowPos().y + ImGui.getWindowSize().y + 25
                )
            ) {
                ImGui.setMouseCursor(ImGuiMouseCursor.ResizeNWSE)
            }
            //Also allow on the right side
            if (ImGui.isMouseHoveringRect(
                    ImGui.getWindowPos().x + ImGui.getWindowSize().x,
                    ImGui.getWindowPos().y,
                    ImGui.getWindowPos().x + ImGui.getWindowSize().x + 25,
                    ImGui.getWindowPos().y + ImGui.getWindowSize().y
                )
            ) {
                flags -= Flag.NoResize
                ImGui.setMouseCursor(ImGuiMouseCursor.ResizeNWSE)

            }
            this()
            propagate()
        }
        ImGui.end()

        when (ImGui.getMouseCursor()) {
            ImGuiMouseCursor.ResizeNWSE -> {
                flags -= Flag.NoResize
            }

            ImGuiMouseCursor.ResizeEW -> {
                flags += Flag.NoResize
            }

            ImGuiMouseCursor.ResizeNS -> {
                flags += Flag.NoResize
            }


        }
    }


    enum class Flag(override val value: Int) : IntFlags {
        None(ImGuiWindowFlags.None),
        NoTitleBar(ImGuiWindowFlags.NoTitleBar),
        NoResize(ImGuiWindowFlags.NoResize),
        NoMove(ImGuiWindowFlags.NoMove),
        NoScrollbar(ImGuiWindowFlags.NoScrollbar),
        NoScrollWithMouse(ImGuiWindowFlags.NoScrollWithMouse),
        NoCollapse(ImGuiWindowFlags.NoCollapse),
        AlwaysAutoResize(ImGuiWindowFlags.AlwaysAutoResize),
        NoBackground(ImGuiWindowFlags.NoBackground),
        NoSavedSettings(ImGuiWindowFlags.NoSavedSettings),
        NoMouseInputs(ImGuiWindowFlags.NoMouseInputs),
        MenuBar(ImGuiWindowFlags.MenuBar),
        HorizontalScrollbar(ImGuiWindowFlags.HorizontalScrollbar),
        NoFocusOnAppearing(ImGuiWindowFlags.NoFocusOnAppearing),
        NoBringToFrontOnFocus(ImGuiWindowFlags.NoBringToFrontOnFocus),
        AlwaysVerticalScrollbar(ImGuiWindowFlags.AlwaysVerticalScrollbar),
        AlwaysHorizontalScrollbar(ImGuiWindowFlags.AlwaysHorizontalScrollbar),
        AlwaysUseWindowPadding(ImGuiWindowFlags.AlwaysUseWindowPadding),
        NoNavInputs(ImGuiWindowFlags.NoNavInputs),
        NoNavFocus(ImGuiWindowFlags.NoNavFocus),
        UnsavedDocument(ImGuiWindowFlags.UnsavedDocument),
        NoNav(ImGuiWindowFlags.NoNav),
        NoDecoration(ImGuiWindowFlags.NoDecoration),
        NoInputs(ImGuiWindowFlags.NoInputs),

    }

}

fun childWindow(init: ChildWindow.() -> Unit): ChildWindow {
    val window = ChildWindow()
    window.init()
    return window
}

fun Window.childWindow(name: String, init: ChildWindow.() -> Unit) {
    val window = me.jraynor.osgui.childWindow(init)
    window.name = name
    this.with(window)
}

fun Window.childWindow(init: ChildWindow.() -> Unit) {
    val window = me.jraynor.osgui.childWindow(init)
    this.with(window)
}

class ChildWindow : Window() {

    override fun render() {
        if (name == null) error("Cannot render a window with a null name")
        if (ImGui.beginChild(name)) {
            this()
            propagate()
        }
        ImGui.endChild()
    }
}


fun textInput(init: TextInput.() -> Unit): TextInput {
    val textInput = TextInput()
    textInput.init()
    return textInput
}

fun RendererElement<*>.textInput(
    name: String,
    flags: Flags<TextInput.Flag> = Flags(),
    callback: (String) -> Unit
) {
    val textInput = TextInput()
    textInput.label = name
    textInput.flags = flags
    textInput.callback = callback
    this.with(textInput)
}


class TextInput : RendererElement<TextInput>() {
    private val buffer: ImString = ImString(256)

    /**
     * {Required} This will store the name of the window.
     */
    var label: String? = null
        set(value) = //Only set the name if it is null
            if (value != null && field == null) field = value else {
                error("Cannot set name to $value because it is already set to ${field ?: "null"}")
            }

    var callback: ((String) -> Unit)? = null
        set(value) = //Only set the name if it is null
            if (value != null && field == null) field = value else {
                error("Cannot set name to $value because it is already set to ${field ?: "null"}")
            }

    val value: String get() = buffer.get()

    var flags = Flags<Flag>()
    override fun render() {
        if (label == null) error("Cannot render a text input with a null label")
        if (ImGui.inputText(label, buffer))
            callback?.invoke(value)
    }


    enum class Flag(override val value: Int) : IntFlags {
        None(ImGuiInputTextFlags.None),
        CharsDecimal(ImGuiInputTextFlags.CharsDecimal),
        CharsHexadecimal(ImGuiInputTextFlags.CharsHexadecimal),
        CharsUppercase(ImGuiInputTextFlags.CharsUppercase),
        CharsNoBlank(ImGuiInputTextFlags.CharsNoBlank),
        AutoSelectAll(ImGuiInputTextFlags.AutoSelectAll),
        EnterReturnsTrue(ImGuiInputTextFlags.EnterReturnsTrue),
        CallbackCompletion(ImGuiInputTextFlags.CallbackCompletion),
        CallbackHistory(ImGuiInputTextFlags.CallbackHistory),
        CallbackAlways(ImGuiInputTextFlags.CallbackAlways),
        CallbackCharFilter(ImGuiInputTextFlags.CallbackCharFilter),
        AllowTabInput(ImGuiInputTextFlags.AllowTabInput),
        CtrlEnterForNewLine(ImGuiInputTextFlags.CtrlEnterForNewLine),
        NoHorizontalScroll(ImGuiInputTextFlags.NoHorizontalScroll),
        ReadOnly(ImGuiInputTextFlags.ReadOnly),
        Password(ImGuiInputTextFlags.Password),
        NoUndoRedo(ImGuiInputTextFlags.NoUndoRedo),
        CharsScientific(ImGuiInputTextFlags.CharsScientific),
        CallbackResize(ImGuiInputTextFlags.CallbackResize),
    }

}

interface IntFlags {
    val value: Int
}


class Flags<T : IntFlags>(vararg flags: T) {
    var value: Int = Window.Flag.None.value
        private set

    /**
     * Construct the flags
     */
    init {
        flags.forEach { value = value or it.value }
    }

    /**
     * This will add a flag to the current flags
     */
    fun add(vararg flag: T) {
        flag.forEach { value = value or it.value }
    }

    /**
     * This will remove a flag from the current flags
     */
    fun remove(vararg flag: T) {
        flag.forEach { value = value and it.value.inv() }
    }

    operator fun plusAssign(flag: T) {
        value = value or flag.value
    }

    operator fun minusAssign(flag: T) {
        value = value and flag.value.inv()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Flags<*> //Cast the other to a flag

        return value == other.value
    }

    override fun hashCode(): Int {
        return value
    }


}


