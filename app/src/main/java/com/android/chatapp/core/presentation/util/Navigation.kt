package com.android.chatapp.core.presentation.util

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KType
import kotlin.reflect.typeOf


/**
 * To use this any part of this file any [T] Type should has @Serializable and implement Parcelable
 **/


class ParcelableNavType<T : Parcelable> constructor(val type: KType) :
    NavType<T>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): T? = bundle.getParcelable(key)

    @Suppress("UNCHECKED_CAST")
    override fun parseValue(value: String): T =
        Json.run { decodeFromString(serializersModule.serializer(type), value) } as T

    override fun put(bundle: Bundle, key: String, value: T) = bundle.putParcelable(key, value)

    companion object {
        inline operator fun <reified T : Parcelable> invoke() = ParcelableNavType<T>(typeOf<T>())
    }
}

inline val <reified T : Parcelable> T.routeArg: String
    get() = Uri.encode(Json.encodeToString(this))


/*
inline infix fun <reified T : Parcelable> NavHostController.navigateTo(destination: Pair<String, T>) =
    navigate("${destination.first}${destination.second.routeArg}")
*/

inline infix fun <reified T : Parcelable> Pair<String, T>.with(controller: NavHostController) =
    controller.navigate("${first}${second.routeArg}")

inline infix fun <reified T : Parcelable> String.assign(item: T) =
    "${this}${item.routeArg}/"

//infix fun String.assign(param: String) = "$this${param}/"

object UriAsStringSerializer : KSerializer<Uri> {

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Uri", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Uri = Uri.parse(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: Uri) = encoder.encodeString(value.toString())

}