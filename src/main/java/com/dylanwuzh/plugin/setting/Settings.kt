package com.dylanwuzh.plugin.setting

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "JsonToDartBeanSettings", storages = [(Storage("JsonToDartBeanSettings.xml"))])
data class Settings(
    var isOpenNullSafety: Boolean? = null,
    var isOpenNullAble: Boolean? = null
) : PersistentStateComponent<Settings> {

    override fun getState(): Settings {
        return this
    }

    override fun loadState(state: Settings) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
