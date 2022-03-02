package com.android.chatapp.feature_authentication.data.provider.token.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.android.chatapp.TokenPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object TokenPreferencesSerializer : Serializer<TokenPreferences> {
    override val defaultValue: TokenPreferences
        get() = TokenPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): TokenPreferences {
        try {
            return TokenPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: TokenPreferences, output: OutputStream) = t.writeTo(output)
}