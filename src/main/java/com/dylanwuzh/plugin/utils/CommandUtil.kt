package com.dylanwuzh.plugin.utils

import com.intellij.openapi.project.Project
import org.jetbrains.plugins.terminal.TerminalView
import com.intellij.openapi.wm.ToolWindowManager
import org.jetbrains.plugins.terminal.TerminalToolWindowFactory
import java.io.IOException
import java.lang.Exception

object CommandUtil {

    private const val TERMINAL_NAME = "JsonToDart"
    private const val COMMAND = "flutter pub run build_runner build --delete-conflicting-outputs"

    fun runFlutterBuildRunner(project: Project) {
        val workingDirectory = project.basePath
        val terminalView = TerminalView.getInstance(project)
        val window = ToolWindowManager.getInstance(project)
            .getToolWindow(TerminalToolWindowFactory.TOOL_WINDOW_ID)
        if (window == null) {
            project.showErrorNotify("Please check that the following two plugins are installed: Terminal and Shell Script")
            return
        }
        try {
            val content = window.contentManager.findContent(TERMINAL_NAME)
            if (content != null) {
                terminalView.closeTab(content)
            }
            val localTerminal = window.contentManager.findContent("Local")
            if (localTerminal != null && content != null) {
                terminalView.closeTab(content)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            val terminalWidget = terminalView.createLocalShellWidget(workingDirectory, TERMINAL_NAME)
            terminalWidget.executeCommand(COMMAND)
        } catch (e: IOException) {
            project.showErrorNotify("Cannot run command:" + COMMAND + "  " + e.message)
        }
    }
}
