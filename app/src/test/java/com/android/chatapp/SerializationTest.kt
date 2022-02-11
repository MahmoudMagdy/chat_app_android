package com.android.chatapp

import com.android.chatapp.feature_gallery.presentation.util.Gallery
import com.android.chatapp.feature_gallery.presentation.util.GallerySettings
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

class SerializationTest {
    @Test
    fun testSerializeGallerySettings() {
        val settings = GallerySettings(true, -1, Gallery.VIDEO)
        println(settings)
        val encodedSettings = Json.encodeToString(settings)
        println(encodedSettings)
        val restoredSettings: GallerySettings = Json.decodeFromString(encodedSettings)
        println(restoredSettings)
    }
}