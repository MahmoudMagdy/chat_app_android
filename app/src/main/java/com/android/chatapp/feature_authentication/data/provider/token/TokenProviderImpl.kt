package com.android.chatapp.feature_authentication.data.provider.token

import androidx.datastore.core.DataStore
import com.android.chatapp.TokenPreferences
import com.android.chatapp.core.domain.util.logError
import com.android.chatapp.feature_authentication.data.provider.token.util.takeIfNotEmpty
import io.ktor.client.features.auth.providers.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException

class TokenProviderImpl(private val store: DataStore<TokenPreferences>) : TokenProvider {
    private val token = store.data.catch { exception ->
        // dataStore.data throws an IOException when an error is encountered when reading data
        if (exception is IOException) {
            logError("Error reading sort order preferences.", exception)
            emit(TokenPreferences.getDefaultInstance())
        } else {
            throw exception
        }
    }

    override suspend fun uid(): Long? = token.first().takeIfNotEmpty?.id
    override suspend fun bearerTokens(): BearerTokens? =
        token.first().takeIfNotEmpty?.run { BearerTokens(accessToken, refreshToken) }

    override suspend fun set(token: TokenPreferences) = store.updateData { token }

    override suspend fun update(bearerTokens: BearerTokens) =
        store.updateData { preferences ->
            preferences.toBuilder().apply {
                accessToken = bearerTokens.accessToken
                refreshToken = bearerTokens.refreshToken
            }.build()
        }

    override suspend fun logout() =
        store.updateData { preferences ->
            preferences.toBuilder().clear().build()
        }

}