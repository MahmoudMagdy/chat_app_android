package com.android.chatapp.feature_agreement.domain.model

data class Agreement(val title: String, val caption: String)


infix fun MutableList<Agreement>.extractAgreements(strings: Array<String>) {
    val agreements = mutableListOf<Agreement>()
    var tempTitle = ""
    strings.forEachIndexed { index, item ->
        if (index % 2 == 0 && index != 1)
            tempTitle = item
        else agreements.add(Agreement(tempTitle, item))
    }
    clear()
    addAll(agreements)
}