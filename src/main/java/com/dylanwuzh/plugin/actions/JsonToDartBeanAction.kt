package com.dylanwuzh.plugin.actions

import com.dylanwuzh.plugin.generator.ModelGenerator
import com.dylanwuzh.plugin.ui.JsonInputDialog
import com.dylanwuzh.plugin.utils.*
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.jetbrains.lang.dart.DartFileType
import com.jetbrains.lang.dart.psi.DartFile

class JsonToDartBeanAction : AnAction("JsonToDartBean") {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.getData(PlatformDataKeys.PROJECT) ?: return

        val dataContext = event.dataContext
        val module = LangDataKeys.MODULE.getData(dataContext) ?: return

        val directory = when (val navigatable = LangDataKeys.NAVIGATABLE.getData(dataContext)) {
            is PsiDirectory -> navigatable
            is PsiFile -> navigatable.containingDirectory
            else -> {
                val root = ModuleRootManager.getInstance(module)
                root.sourceRoots
                    .asSequence()
                    .mapNotNull { PsiManager.getInstance(project).findDirectory(it) }
                    .firstOrNull()
            }
        } ?: return

        val psiFileFactory = PsiFileFactory.getInstance(project)
        try {
            JsonInputDialog(project) { collectInfo ->
                val fileName = collectInfo.transformInputClassNameToFileName()
                when {
                    directory.containsFile("$fileName.dart") -> {
                        project.showErrorNotify("The $fileName.dart already exists")
                        false
                    }
                    else -> {
                        // 生成 dart 文件的内容
                        val generatorClassContent = ModelGenerator(collectInfo).generateDartClassesToString()
                        // 生成 dart 文件
                        generateDartDataClassFile(
                            fileName,
                            generatorClassContent,
                            project,
                            psiFileFactory,
                            directory
                        )

                        val notifyMessage = "Dart Data Class file generated successful"
                        project.showInfoNotify(notifyMessage)
                        true
                    }
                }
            }.show()

            // 执行 flutter pub run build_runner build 命令
            runFlutterCommand(project)
        } catch (e: Exception) {
            project.showErrorNotify(e.message!!)
        }
    }

    private fun generateDartDataClassFile(
        fileName: String,
        classCodeContent: String,
        project: Project?,
        psiFileFactory: PsiFileFactory,
        directory: PsiDirectory
    ) {
        project.executeCouldRollBackAction {
            val file: DartFile = psiFileFactory.createFileFromText(
                "$fileName.dart",
                DartFileType.INSTANCE,
                classCodeContent
            ) as DartFile
            directory.add(file)
        }
    }

    private fun runFlutterCommand(project: Project) {
        project.executeCouldRollBackAction {
            CommandUtil.runFlutterBuildRunner(project)
        }
    }
}
