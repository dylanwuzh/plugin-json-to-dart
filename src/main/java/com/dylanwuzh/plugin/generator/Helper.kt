package com.dylanwuzh.plugin.generator

import com.dylanwuzh.plugin.utils.toUpperCaseFirstOne
import java.math.BigDecimal

private val PRIMITIVE_TYPES = mapOf(
    "int" to true,
    "num" to true,
    "double" to true,
    "String" to true,
    "bool" to true,
    "List" to true,
    "DateTime" to true,
    "List<int>" to true,
    "List<double>" to true,
    "List<String>" to true,
    "List<bool>" to true,
    "List<num>" to true,
    "List<DateTime>" to true,
    "List<dynamic>" to true,
    "List" to true,
    "Null" to true,
    "var" to true,
    "dynamic" to true
)

/**
 * 是否是主数据类型
 */
fun isPrimitiveType(typeName: String): Boolean {
    return PRIMITIVE_TYPES[typeName.replace("?", "")] ?: false
}

val dartKeyword = mutableListOf(
    "abstract", "dynamic", "implements", "show",
    "as", "else", "import ", "static",
    "assert", "enum", "in", "super",
    "async", "export", "interface", "switch",
    "await", "external", "is", "sync",
    "break", "extends", "library", "this",
    "case", "factory", "mixin", "throw",
    "catch", "false", "new", "true",
    "class", "final", "null", "try",
    "const", "finally", "on", "typedef",
    "continue", "for", "operator", "var",
    "covariant", "Function", "part", "void",
    "default", "get", "rethrow", "while",
    "deferred", "hide", "return", "with",
    "do", "if", "set", "yield", "list", "map"
)

fun getTypeName(obj: Any?): String {
    return when (obj) {
        is String -> "String"
        is Int -> "int"
        is Double -> "double"
        is Long -> "int"
        is BigDecimal -> "double"
        is Boolean -> "bool"
        null -> "Null"
        is List<*> -> "List"
        else -> "Class"
    }
}

fun camelCase(init: String): String {
    val newInit = init.replace("-", "_")
    if (newInit.contains("_").not()) {
        return newInit.toUpperCaseFirstOne()
    }
    val ret = StringBuilder(newInit.length)
    for (word in newInit.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
        if (word.isNotEmpty()) {
            ret.append(word.substring(0, 1).toUpperCase())
            ret.append(word.substring(1).toLowerCase())
        }
        if (ret.length != newInit.length)
            ret.append(" ")
    }
    return ret.toString().replace(" ", "")
}

fun camelCaseFirstLower(text: String): String {
    if (text.isEmpty()) {
        return text
    }
    val camelCaseText = if (text.contains("_")) {
        camelCase(text)
    } else {
        text
    }
    if (camelCaseText.length == 1) {
        return camelCaseText.toLowerCase()
    }
    val firstChar = camelCaseText.substring(0, 1).toLowerCase()
    val rest = camelCaseText.substring(1)
    return "$firstChar$rest"
}

//驼峰命名
fun fixFieldName(name: String, typeDef: TypeDefinition? = null, privateField: Boolean = false): String {
    val newName = name.replace("-", "_")
    var properName = newName
    if (newName.startsWith('_') || newName.startsWith("[0-9]")) {
        val firstCharType = typeDef?.name?.substring(0, 1)?.toLowerCase()
        properName = "$firstCharType$newName"
    }
    val fieldName = camelCaseFirstLower(properName)
    if (privateField) {
        return "_$fieldName"
    }
    return filedKeywordRename(fieldName)
}

fun filedKeywordRename(key: String): String {
    var notKeyWord = key
    //关键字的修改字段名
    if (dartKeyword.contains(key.toLowerCase()) || key.first().isDigit()) {
        notKeyWord = "x${key.toUpperCaseFirstOne()}"
    }
    return notKeyWord
}
