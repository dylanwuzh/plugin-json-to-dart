package com.dylanwuzh.plugin.generator

import com.dylanwuzh.plugin.setting.Settings
import com.dylanwuzh.plugin.utils.toLowerCaseFirstOne
import com.dylanwuzh.plugin.utils.toUpperCaseFirstOne
import com.intellij.openapi.application.ApplicationManager

class ClassDefinition(private val name: String, private val privateFields: Boolean = false) {

    val fields = mutableMapOf<String, TypeDefinition>()

    val dependencies: List<Dependency>
        get() {
            val dependenciesList = mutableListOf<Dependency>()
            val keys = fields.keys
            keys.forEach { k ->
                if (fields[k]!!.isPrimitive.not()) {
                    dependenciesList.add(Dependency(k, fields[k]!!))
                }
            }
            return dependenciesList
        }

    fun addField(key: String, typeDef: TypeDefinition) {
        fields[key] = typeDef
    }

    private fun addTypeDef(typeDef: TypeDefinition, sb: StringBuffer) {
        if (typeDef.name == "Null") {
            sb.append("dynamic")
        } else {
            sb.append(typeDef.name)
        }
        if (typeDef.subtype != null) {
            // 如果是List就把名字修改成单数
            sb.append("<${typeDef.subtype!!}>")
        }
    }

    /**
     * 字段的集合
     */
    private val fieldsContent: String
        get() {
            val settings = ApplicationManager.getApplication().getService(Settings::class.java)
            val isOpenNullSafety = settings.isOpenNullSafety == true
            val isOpenNullAble = settings.isOpenNullAble == true
            val prefix = if (isOpenNullSafety && !isOpenNullAble) "late " else ""
            val suffix = if (isOpenNullSafety && isOpenNullAble) "?" else ""
            return fields.keys.map { key ->
                val f = fields[key]
                val fieldName = fixFieldName(key, f, privateFields)
                val sb = StringBuffer()
                // 如果驼峰命名后不一致，才这样
                if (fieldName != key) {
                    sb.append('\t')
                    sb.append("@JSONField(name: \"${key}\")\n")
                }
                sb.append('\t')
                sb.append(prefix)
                addTypeDef(f!!, sb)
                sb.append(suffix)
                sb.append(" $fieldName;")
                return@map sb.toString()
            }.joinToString("\n")
        }

    private val annotationContent: String
        get() {
            val content = StringBuffer()
            content.append("part '${name.toLowerCaseFirstOne()}.g.dart';\n")
            content.append('\n')
            content.append("@JsonSerializable()")
            return content.toString()
        }

    private val constructorContent: String
        get() {
            val keys = fields.keys
            val content = StringBuffer()
            content.append('\t')
            content.append("${name}({")
            keys.forEachIndexed { index, key ->
                content.append("this.${key}")
                if (index < keys.size - 1) {
                    content.append(", ")
                }
            }
            content.append("});")
            return content.toString()
        }

    private val jsonPatternContent: String
        get() {
            val content = StringBuffer()
            content.append('\t')
            content.append("factory ${name}.fromJson(Map<String, dynamic> json) => _\$${name}FromJson(json);\n")
            content.append('\n')
            content.append('\t')
            content.append("Map<String, dynamic> toJson() => _\$${name}ToJson(this);")
            return content.toString()
        }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + privateFields.hashCode()
        result = 31 * result + fields.hashCode()
        return result
    }

    override operator fun equals(other: Any?): Boolean {
        if (other is ClassDefinition) {
            if (name != other.name) {
                return false
            }
            return fields.keys.firstOrNull { k ->
                other.fields.keys.firstOrNull { ok ->
                    fields[k] == other.fields[ok]
                } == null
            } == null
        }
        return false
    }

    override fun toString(): String {
        return if (privateFields) {
            ""
        } else {
            "${annotationContent}\nclass $name {\n$fieldsContent\n\n$constructorContent\n\n$jsonPatternContent\n}\n"
        }
    }
}
