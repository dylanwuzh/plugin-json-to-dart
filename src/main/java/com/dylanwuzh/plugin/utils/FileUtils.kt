package com.dylanwuzh.plugin.utils

import com.intellij.psi.PsiDirectory

/**
 * 判断Directory中是否包含这个file
 */
fun PsiDirectory.containsFile(fileName: String): Boolean {
    return this.files.filter { it.name.endsWith(".dart") }
        .firstOrNull { it.name.contains(fileName) } != null
}
