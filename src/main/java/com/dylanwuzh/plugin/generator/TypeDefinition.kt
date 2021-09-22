package com.dylanwuzh.plugin.generator

import com.dylanwuzh.plugin.utils.toUpperCaseFirstOne

class TypeDefinition(var name: String, var subtype: String? = null) {

    val isPrimitive: Boolean = if (subtype == null) {
        isPrimitiveType(name)
    } else {
        isPrimitiveType("$name<${subtype!!.toUpperCaseFirstOne()}>")
    }

    private val isPrimitiveList: Boolean = isPrimitive && name == "List"

    companion object {
        fun fromDynamic(obj: Any?): TypeDefinition {
            val type = getTypeName(obj)
            if (type == "List") {
                val list = obj as List<*>
                val firstElementType = if (list.isNotEmpty()) {
                    getTypeName(list[0])
                } else {
                    // when array is empty insert Null just to warn the user
                    "dynamic"
                }
                return TypeDefinition(type, firstElementType)
            }
            return TypeDefinition(type)
        }
    }

    override operator fun equals(other: Any?): Boolean {
        if (other is TypeDefinition) {
            return name == other.name && subtype == other.subtype
        }
        return false
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (subtype?.hashCode() ?: 0)
        result = 31 * result + isPrimitive.hashCode()
        result = 31 * result + isPrimitiveList.hashCode()
        return result
    }

    override fun toString(): String {
        return "TypeDefinition(name='$name', subtype=$subtype, isPrimitive=$isPrimitive, isPrimitiveList=$isPrimitiveList)"
    }
}
