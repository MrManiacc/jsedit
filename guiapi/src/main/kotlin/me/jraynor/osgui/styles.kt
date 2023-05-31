package me.jraynor.osgui

import imgui.ImGuiStyle
import imgui.flag.ImGuiCol

fun interface IStyle {
    fun apply(style: ImGuiStyle)
}

val Eclipse = IStyle { style ->
    style.setWindowMinSize(160f, 20f)
    style.setFramePadding(4f, 2f)
    style.setItemSpacing(8f, 2f)
    style.setItemInnerSpacing(8f, 4f)
    style.alpha = 0.95f
    style.windowRounding = 0.0f
    style.tabBorderSize = 0f
    style.tabMinWidthForCloseButton = 0f
    style.frameRounding = 0.0f
    style.indentSpacing = 8.0f
    style.columnsMinSpacing = 50.0f
    style.grabMinSize = 14.0f
    style.grabRounding = 0.0f
    style.scrollbarSize = 12.0f
    style.scrollbarRounding = 0.0f

    style.setColor(ImGuiCol.Text, 0.86f, 0.86f, 0.86f, 0.78f)
    style.setColor(ImGuiCol.TextDisabled, 0.86f, 0.86f, 0.86f, 0.28f)
    style.setColor(ImGuiCol.WindowBg, 0.13f, 0.13f, 0.13f, 1.00f)
    style.setColor(ImGuiCol.Border, 0.28f, 0.28f, 0.28f, 1.00f)
    style.setColor(ImGuiCol.BorderShadow, 0.00f, 0.00f, 0.00f, 0.00f)
    style.setColor(ImGuiCol.FrameBg, 0.25f, 0.25f, 0.25f, 1.00f)
    style.setColor(ImGuiCol.FrameBgHovered, 0.40f, 0.15f, 0.69f, 0.68f)
    style.setColor(ImGuiCol.FrameBgActive, 0.46f, 0.17f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.TitleBg, 0.20f, 0.20f, 0.20f, 1.00f)
    style.setColor(ImGuiCol.TitleBgCollapsed, 0.20f, 0.20f, 0.20f, 0.75f)
    style.setColor(ImGuiCol.TitleBgActive, 0.66f, 0.66f, 0.66f, 0.00f)
    style.setColor(ImGuiCol.MenuBarBg, 0.34f, 0.16f, 0.16f, 0.0f)
    style.setColor(ImGuiCol.ScrollbarBg, 0.20f, 0.25f, 0.30f, 1.00f)
    style.setColor(ImGuiCol.ScrollbarGrab, 0.40f, 0.15f, 0.69f, 1.00f)
    style.setColor(ImGuiCol.ScrollbarGrabHovered, 0.46f, 0.17f, 0.80f, 0.78f)
    style.setColor(ImGuiCol.ScrollbarGrabActive, 0.46f, 0.17f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.CheckMark, 0.71f, 0.22f, 0.27f, 1.00f)
    style.setColor(ImGuiCol.SliderGrab, 0.40f, 0.15f, 0.69f, 0.64f)
    style.setColor(ImGuiCol.SliderGrabActive, 0.46f, 0.17f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.Button, 0.071f, 0.078f, 0.073f, 1.0f)
    style.setColor(ImGuiCol.ButtonHovered, 0.40f, 0.15f, 0.69f, 0.86f)
    style.setColor(ImGuiCol.ButtonActive, 0.46f, 0.17f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.Header, 0.28f, 0.28f, 0.28f, 1.00f)
    style.setColor(ImGuiCol.HeaderHovered, 0.46f, 0.17f, 0.80f, 0.86f)
    style.setColor(ImGuiCol.HeaderActive, 0.46f, 0.17f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.Separator, 0.40f, 0.15f, 0.69f, 0.1f)
    style.setColor(ImGuiCol.SeparatorHovered, 0.40f, 0.15f, 0.69f, 0.8f)
    style.setColor(ImGuiCol.SeparatorActive, 0.46f, 0.17f, 0.80f, 0.5f)
    style.setColor(ImGuiCol.ResizeGrip, 0.28f, 0.28f, 0.28f, 0.04f)
    style.setColor(ImGuiCol.ResizeGripHovered, 0.40f, 0.15f, 0.69f, 0.78f)
    style.setColor(ImGuiCol.ResizeGripActive, 0.46f, 0.17f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.Tab, 0.28f, 0.28f, 0.28f, 1.00f)
    style.setColor(ImGuiCol.TabHovered, 0.40f, 0.15f, 0.69f, 0.86f)
    style.setColor(ImGuiCol.TabActive, 0.46f, 0.17f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.TabUnfocused, 0.28f, 0.28f, 0.28f, 1.00f)
    style.setColor(ImGuiCol.TabUnfocusedActive, 0.46f, 0.17f, 0.80f, 0.54f)
    style.setColor(ImGuiCol.PlotLines, 0.86f, 0.86f, 0.86f, 0.63f)
    style.setColor(ImGuiCol.PlotLinesHovered, 0.46f, 0.17f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.PlotHistogram, 0.86f, 0.86f, 0.86f, 0.63f)
    style.setColor(ImGuiCol.PlotHistogramHovered, 0.46f, 0.17f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.TextSelectedBg, 0.46f, 0.17f, 0.80f, 0.43f)
    style.setColor(ImGuiCol.DragDropTarget, 0.46f, 0.17f, 0.80f, 0.90f)
    style.setColor(ImGuiCol.NavHighlight, 0.46f, 0.17f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.NavWindowingHighlight, 0.46f, 0.17f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.NavWindowingDimBg, 0.16f, 0.16f, 0.16f, 0.73f)
    style.setColor(ImGuiCol.ModalWindowDimBg, 0.16f, 0.16f, 0.16f, 0.73f)
}

val OceanBreeze = IStyle { style ->
    style.setWindowMinSize(180f, 30f)
    style.setFramePadding(5f, 3f)
    style.setItemSpacing(7f, 3f)
    style.setItemInnerSpacing(7f, 5f)
    style.alpha = 0.90f
    style.windowRounding = 5.0f
    style.frameRounding = 3.0f
    style.indentSpacing = 7.0f
    style.columnsMinSpacing = 60.0f
    style.grabMinSize = 16.0f
    style.grabRounding = 18.0f
    style.scrollbarSize = 14.0f
    style.scrollbarRounding = 18.0f

    style.setColor(ImGuiCol.Text, 0.76f, 0.83f, 0.79f, 0.78f)
    style.setColor(ImGuiCol.TextDisabled, 0.76f, 0.83f, 0.79f, 0.18f)
    style.setColor(ImGuiCol.WindowBg, 0.23f, 0.24f, 0.27f, 1.00f)
    style.setColor(ImGuiCol.Border, 0.41f, 0.41f, 1.00f, 0.10f)
    style.setColor(ImGuiCol.BorderShadow, 0.10f, 0.10f, 0.10f, 0.10f)
    style.setColor(ImGuiCol.FrameBg, 0.30f, 0.32f, 0.37f, 1.00f)
    style.setColor(ImGuiCol.FrameBgHovered, 0.82f, 0.28f, 0.39f, 0.68f)
    style.setColor(ImGuiCol.FrameBgActive, 0.82f, 0.28f, 0.39f, 1.00f)
    style.setColor(ImGuiCol.TitleBg, 0.30f, 0.32f, 0.37f, 1.00f)
    style.setColor(ImGuiCol.TitleBgCollapsed, 0.30f, 0.32f, 0.37f, 0.65f)
    style.setColor(ImGuiCol.TitleBgActive, 0.82f, 0.28f, 0.39f, 1.00f)
    style.setColor(ImGuiCol.MenuBarBg, 0.30f, 0.32f, 0.37f, 0.57f)
    style.setColor(ImGuiCol.ScrollbarBg, 0.30f, 0.32f, 0.37f, 1.00f)
    style.setColor(ImGuiCol.ScrollbarGrab, 0.19f, 0.25f, 0.26f, 1.00f)
    style.setColor(ImGuiCol.ScrollbarGrabHovered, 0.82f, 0.28f, 0.39f, 0.68f)
    style.setColor(ImGuiCol.ScrollbarGrabActive, 0.82f, 0.28f, 0.39f, 1.00f)
    style.setColor(ImGuiCol.CheckMark, 0.61f, 0.32f, 0.37f, 1.00f)
    style.setColor(ImGuiCol.SliderGrab, 0.57f, 0.87f, 0.93f, 0.24f)
    style.setColor(ImGuiCol.SliderGrabActive, 0.82f, 0.28f, 0.39f, 1.00f)
    style.setColor(ImGuiCol.Button, 0.57f, 0.87f, 0.93f, 0.24f)
    style.setColor(ImGuiCol.ButtonHovered, 0.82f, 0.28f, 0.39f, 0.96f)
    style.setColor(ImGuiCol.ButtonActive, 0.82f, 0.28f, 0.39f, 1.00f)
    style.setColor(ImGuiCol.Header, 0.82f, 0.28f, 0.39f, 0.66f)
    style.setColor(ImGuiCol.HeaderHovered, 0.82f, 0.28f, 0.39f, 0.96f)
    style.setColor(ImGuiCol.HeaderActive, 0.82f, 0.28f, 0.39f, 1.00f)
    style.setColor(ImGuiCol.Separator, 0.24f, 0.26f, 0.29f, 1.00f)
    style.setColor(ImGuiCol.SeparatorHovered, 0.82f, 0.28f, 0.39f, 0.68f)
    style.setColor(ImGuiCol.SeparatorActive, 0.82f, 0.28f, 0.39f, 1.00f)
    style.setColor(ImGuiCol.ResizeGrip, 0.57f, 0.87f, 0.93f, 0.14f)
    style.setColor(ImGuiCol.ResizeGripHovered, 0.82f, 0.28f, 0.39f, 0.68f)
    style.setColor(ImGuiCol.ResizeGripActive, 0.82f, 0.28f, 0.39f, 1.00f)
    style.setColor(ImGuiCol.Tab, 0.30f, 0.32f, 0.37f, 1.00f)
    style.setColor(ImGuiCol.TabHovered, 0.82f, 0.28f, 0.39f, 0.96f)
    style.setColor(ImGuiCol.TabActive, 0.82f, 0.28f, 0.39f, 1.00f)
    style.setColor(ImGuiCol.TabUnfocused, 0.30f, 0.32f, 0.37f, 1.00f)
    style.setColor(ImGuiCol.TabUnfocusedActive, 0.82f, 0.28f, 0.39f, 0.64f)
    style.setColor(ImGuiCol.PlotLines, 0.76f, 0.83f, 0.79f, 0.53f)
    style.setColor(ImGuiCol.PlotLinesHovered, 0.82f, 0.28f, 0.39f, 1.00f)
    style.setColor(ImGuiCol.PlotHistogram, 0.76f, 0.83f, 0.79f, 0.53f)
    style.setColor(ImGuiCol.PlotHistogramHovered, 0.82f, 0.28f, 0.39f, 1.00f)
    style.setColor(ImGuiCol.TextSelectedBg, 0.82f, 0.28f, 0.39f, 0.53f)
    style.setColor(ImGuiCol.DragDropTarget, 0.82f, 0.28f, 0.39f, 0.90f)
    style.setColor(ImGuiCol.NavHighlight, 0.82f, 0.28f, 0.39f, 1.00f)
    style.setColor(ImGuiCol.NavWindowingHighlight, 0.82f, 0.28f, 0.39f, 1.00f)
    style.setColor(ImGuiCol.NavWindowingDimBg, 0.30f, 0.32f, 0.37f, 0.73f)
    style.setColor(ImGuiCol.ModalWindowDimBg, 0.30f, 0.32f, 0.37f, 0.73f)
}



val Cinder = IStyle { style ->
    style.setWindowMinSize(160f, 20f)
    style.setFramePadding(4f, 2f)
    style.setItemSpacing(6f, 2f)
    style.setItemInnerSpacing(6f, 4f)
    style.alpha = 0.95f
    style.windowRounding = 4.0f
    style.frameRounding = 2.0f
    style.indentSpacing = 6.0f
    style.columnsMinSpacing = 50.0f
    style.grabMinSize = 14.0f
    style.grabRounding = 16.0f
    style.scrollbarSize = 12.0f
    style.scrollbarRounding = 16.0f

    style.setColor(ImGuiCol.Text, 0.86f, 0.93f, 0.89f, 0.78f)
    style.setColor(ImGuiCol.TextDisabled, 0.86f, 0.93f, 0.89f, 0.28f)
    style.setColor(ImGuiCol.WindowBg, 0.13f, 0.14f, 0.17f, 1.00f)
    style.setColor(ImGuiCol.Border, 0.31f, 0.31f, 1.00f, 0.00f)
    style.setColor(ImGuiCol.BorderShadow, 0.00f, 0.00f, 0.00f, 0.00f)
    style.setColor(ImGuiCol.FrameBg, 0.20f, 0.22f, 0.27f, 1.00f)
    style.setColor(ImGuiCol.FrameBgHovered, 0.92f, 0.18f, 0.29f, 0.78f)
    style.setColor(ImGuiCol.FrameBgActive, 0.92f, 0.18f, 0.29f, 1.00f)
    style.setColor(ImGuiCol.TitleBg, 0.20f, 0.22f, 0.27f, 1.00f)
    style.setColor(ImGuiCol.TitleBgCollapsed, 0.20f, 0.22f, 0.27f, 0.75f)
    style.setColor(ImGuiCol.TitleBgActive, 0.92f, 0.18f, 0.29f, 1.00f)
    style.setColor(ImGuiCol.MenuBarBg, 0.20f, 0.22f, 0.27f, 0.47f)
    style.setColor(ImGuiCol.ScrollbarBg, 0.20f, 0.22f, 0.27f, 1.00f)
    style.setColor(ImGuiCol.ScrollbarGrab, 0.09f, 0.15f, 0.16f, 1.00f)
    style.setColor(ImGuiCol.ScrollbarGrabHovered, 0.92f, 0.18f, 0.29f, 0.78f)
    style.setColor(ImGuiCol.ScrollbarGrabActive, 0.92f, 0.18f, 0.29f, 1.00f)
    style.setColor(ImGuiCol.CheckMark, 0.71f, 0.22f, 0.27f, 1.00f)
    style.setColor(ImGuiCol.SliderGrab, 0.47f, 0.77f, 0.83f, 0.14f)
    style.setColor(ImGuiCol.SliderGrabActive, 0.92f, 0.18f, 0.29f, 1.00f)
    style.setColor(ImGuiCol.Button, 0.47f, 0.77f, 0.83f, 0.14f)
    style.setColor(ImGuiCol.ButtonHovered, 0.92f, 0.18f, 0.29f, 0.86f)
    style.setColor(ImGuiCol.ButtonActive, 0.92f, 0.18f, 0.29f, 1.00f)
    style.setColor(ImGuiCol.Header, 0.92f, 0.18f, 0.29f, 0.76f)
    style.setColor(ImGuiCol.HeaderHovered, 0.92f, 0.18f, 0.29f, 0.86f)
    style.setColor(ImGuiCol.HeaderActive, 0.92f, 0.18f, 0.29f, 1.00f)
    style.setColor(ImGuiCol.Separator, 0.14f, 0.16f, 0.19f, 1.00f)
    style.setColor(ImGuiCol.SeparatorHovered, 0.92f, 0.18f, 0.29f, 0.78f)
    style.setColor(ImGuiCol.SeparatorActive, 0.92f, 0.18f, 0.29f, 1.00f)
    style.setColor(ImGuiCol.ResizeGrip, 0.47f, 0.77f, 0.83f, 0.04f)
    style.setColor(ImGuiCol.ResizeGripHovered, 0.92f, 0.18f, 0.29f, 0.78f)
    style.setColor(ImGuiCol.ResizeGripActive, 0.92f, 0.18f, 0.29f, 1.00f)
    style.setColor(ImGuiCol.Tab, 0.20f, 0.22f, 0.27f, 1.00f)
    style.setColor(ImGuiCol.TabHovered, 0.92f, 0.18f, 0.29f, 0.86f)
    style.setColor(ImGuiCol.TabActive, 0.92f, 0.18f, 0.29f, 1.00f)
    style.setColor(ImGuiCol.TabUnfocused, 0.20f, 0.22f, 0.27f, 1.00f)
    style.setColor(ImGuiCol.TabUnfocusedActive, 0.92f, 0.18f, 0.29f, 0.54f)
    style.setColor(ImGuiCol.PlotLines, 0.86f, 0.93f, 0.89f, 0.63f)
    style.setColor(ImGuiCol.PlotLinesHovered, 0.92f, 0.18f, 0.29f, 1.00f)
    style.setColor(ImGuiCol.PlotHistogram, 0.86f, 0.93f, 0.89f, 0.63f)
    style.setColor(ImGuiCol.PlotHistogramHovered, 0.92f, 0.18f, 0.29f, 1.00f)
    style.setColor(ImGuiCol.TextSelectedBg, 0.92f, 0.18f, 0.29f, 0.43f)
    style.setColor(ImGuiCol.DragDropTarget, 0.92f, 0.18f, 0.29f, 0.90f)
    style.setColor(ImGuiCol.NavHighlight, 0.92f, 0.18f, 0.29f, 1.00f)
    style.setColor(ImGuiCol.NavWindowingHighlight, 0.92f, 0.18f, 0.29f, 1.00f)
    style.setColor(ImGuiCol.NavWindowingDimBg, 0.20f, 0.22f, 0.27f, 0.73f)
    style.setColor(ImGuiCol.ModalWindowDimBg, 0.20f, 0.22f, 0.27f, 0.73f)
}

val Aqua = IStyle { style ->
    style.setWindowMinSize(180f, 20f)
    style.setFramePadding(6f, 3f)
    style.setItemSpacing(8f, 3f)
    style.setItemInnerSpacing(8f, 5f)
    style.alpha = 1.0f
    style.windowRounding = 8.0f
    style.frameRounding = 4.0f
    style.indentSpacing = 8.0f
    style.columnsMinSpacing = 60.0f
    style.grabMinSize = 18.0f
    style.grabRounding = 20.0f
    style.scrollbarSize = 14.0f
    style.scrollbarRounding = 20.0f

    style.setColor(ImGuiCol.Text, 0.15f, 0.15f, 0.15f, 0.95f)
    style.setColor(ImGuiCol.TextDisabled, 0.35f, 0.35f, 0.35f, 0.78f)
    style.setColor(ImGuiCol.WindowBg, 0.95f, 0.97f, 0.98f, 1.00f)
    style.setColor(ImGuiCol.Border, 0.45f, 0.55f, 0.60f, 0.00f)
    style.setColor(ImGuiCol.BorderShadow, 0.00f, 0.00f, 0.00f, 0.00f)
    style.setColor(ImGuiCol.FrameBg, 0.80f, 0.85f, 0.88f, 1.00f)
    style.setColor(ImGuiCol.FrameBgHovered, 0.12f, 0.59f, 0.80f, 0.78f)
    style.setColor(ImGuiCol.FrameBgActive, 0.12f, 0.59f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.TitleBg, 0.60f, 0.80f, 0.85f, 1.00f)
    style.setColor(ImGuiCol.TitleBgCollapsed, 0.60f, 0.80f, 0.85f, 0.75f)
    style.setColor(ImGuiCol.TitleBgActive, 0.12f, 0.59f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.MenuBarBg, 0.80f, 0.85f, 0.88f, 0.47f)
    style.setColor(ImGuiCol.ScrollbarBg, 0.80f, 0.85f, 0.88f, 1.00f)
    style.setColor(ImGuiCol.ScrollbarGrab, 0.35f, 0.45f, 0.50f, 1.00f)
    style.setColor(ImGuiCol.ScrollbarGrabHovered, 0.12f, 0.59f, 0.80f, 0.78f)
    style.setColor(ImGuiCol.ScrollbarGrabActive, 0.12f, 0.59f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.CheckMark, 0.12f, 0.59f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.SliderGrab, 0.12f, 0.59f, 0.80f, 0.14f)
    style.setColor(ImGuiCol.SliderGrabActive, 0.12f, 0.59f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.Button, 0.80f, 0.85f, 0.88f, 0.14f)
    style.setColor(ImGuiCol.ButtonHovered, 0.12f, 0.59f, 0.80f, 0.86f)
    style.setColor(ImGuiCol.ButtonActive, 0.12f, 0.59f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.Header, 0.12f, 0.59f, 0.80f, 0.76f)
    style.setColor(ImGuiCol.HeaderHovered, 0.12f, 0.59f, 0.80f, 0.86f)
    style.setColor(ImGuiCol.HeaderActive, 0.12f, 0.59f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.Separator, 0.45f, 0.55f, 0.60f, 1.00f)
    style.setColor(ImGuiCol.SeparatorHovered, 0.12f, 0.59f, 0.80f, 0.78f)
    style.setColor(ImGuiCol.SeparatorActive, 0.12f, 0.59f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.ResizeGrip, 0.35f, 0.45f, 0.50f, 0.04f)
    style.setColor(ImGuiCol.ResizeGripHovered, 0.12f, 0.59f, 0.80f, 0.78f)
    style.setColor(ImGuiCol.ResizeGripActive, 0.12f, 0.59f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.Tab, 0.80f, 0.85f, 0.88f, 1.00f)
    style.setColor(ImGuiCol.TabHovered, 0.12f, 0.59f, 0.80f, 0.86f)
    style.setColor(ImGuiCol.TabActive, 0.12f, 0.59f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.TabUnfocused, 0.80f, 0.85f, 0.88f, 1.00f)
    style.setColor(ImGuiCol.TabUnfocusedActive, 0.12f, 0.59f, 0.80f, 0.54f)
    style.setColor(ImGuiCol.PlotLines, 0.15f, 0.15f, 0.15f, 0.63f)
    style.setColor(ImGuiCol.PlotLinesHovered, 0.12f, 0.59f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.PlotHistogram, 0.15f, 0.15f, 0.15f, 0.63f)
    style.setColor(ImGuiCol.PlotHistogramHovered, 0.12f, 0.59f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.TextSelectedBg, 0.12f, 0.59f, 0.80f, 0.43f)
    style.setColor(ImGuiCol.DragDropTarget, 0.12f, 0.59f, 0.80f, 0.90f)
    style.setColor(ImGuiCol.NavHighlight, 0.12f, 0.59f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.NavWindowingHighlight, 0.12f, 0.59f, 0.80f, 1.00f)
    style.setColor(ImGuiCol.NavWindowingDimBg, 0.45f, 0.55f, 0.60f, 0.73f)
    style.setColor(ImGuiCol.ModalWindowDimBg, 0.45f, 0.55f, 0.60f, 0.73f)
}