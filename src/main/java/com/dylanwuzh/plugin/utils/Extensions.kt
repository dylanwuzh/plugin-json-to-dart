package com.dylanwuzh.plugin.utils

import com.intellij.notification.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.project.Project
import java.awt.Component
import java.awt.Container
import javax.swing.Box
import javax.swing.BoxLayout

fun Container.addComponentIntoVerticalBoxAlignmentLeft(component: Component) {
    if (layout is BoxLayout) {
        val hBox = Box.createHorizontalBox()
        hBox.add(component)
        hBox.add(Box.createHorizontalGlue())
        add(hBox)
    }
}

fun Project.showErrorNotify(notifyMessage: String) {
    try {
        NotificationGroupManager.getInstance().getNotificationGroup("JSON to Dart Class")
            .createNotification(notifyMessage, NotificationType.ERROR)
            .notify(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Project.showInfoNotify(message: String) {
    try {
        NotificationGroupManager.getInstance().getNotificationGroup("JSON to Dart Class")
            .createNotification(message, NotificationType.INFORMATION)
            .notify(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * do the action that could be roll-back
 */
fun Project?.executeCouldRollBackAction( action: (Project?) -> Unit) {
    CommandProcessor.getInstance().executeCommand(this, {
        ApplicationManager.getApplication().runWriteAction {
            action.invoke(this)
        }
    }, "JsonToDartBean", "JsonToDartBean")
}
