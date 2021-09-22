package com.dylanwuzh.plugin.generator

class Dependency(var name: String, var typeDef: TypeDefinition) {

    val className: String
        get() {
            return camelCase(name)
        }

    override fun toString(): String {
        return "name = $name ,typeDef = $typeDef"
    }
}
