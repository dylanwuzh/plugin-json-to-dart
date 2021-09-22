package com.dylanwuzh.plugin.generator

import com.dylanwuzh.plugin.utils.toUpperCaseFirstOne
import com.dylanwuzh.plugin.utils.upperCharToUnderLine
import com.dylanwuzh.plugin.utils.upperTable

class CollectInfo {

    /**
     * 用户输入的类名
     */
    var userInputClassName = ""

    /**
     * 用户输入的JSON
     */
    var userInputJson = ""

    /**
     * 用户输入的类名转为文件名
     */
    fun transformInputClassNameToFileName(): String {
        return userInputClassName.upperCharToUnderLine()
    }

    /**
     * 用户输入的名字转为首个class的名字(文件中的类名)
     */
    fun firstClassName(): String {
        return if (userInputClassName.contains("_")) {
            (upperTable(userInputClassName)).toUpperCaseFirstOne()
        } else {
            (userInputClassName).toUpperCaseFirstOne()
        }
    }
}
