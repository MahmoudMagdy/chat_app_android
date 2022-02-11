package com.android.chatapp.core.data.remote

import com.android.chatapp.core.data.util.ApiException
import com.android.chatapp.core.data.util.Resource
import com.android.chatapp.core.data.util.safeApiCall
import com.android.chatapp.feature_authentication.data.provider.user.TokensProvider
import com.android.chatapp.feature_authentication.data.remote.AuthService
import com.android.chatapp.feature_authentication.data.remote.AuthServiceImpl
import com.android.chatapp.feature_authentication.data.remote.dto.RefreshTokensRequest
import com.android.chatapp.feature_authentication.data.remote.dto.RefreshTokensResponse
import com.android.chatapp.feature_chat.data.remote.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.auth.*
import io.ktor.client.features.auth.providers.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class RemoteDataSourceImpl private constructor(
    private val client: HttpClient,
    private val jsonSerializer: Json,
    tokensProvider: TokensProvider
) : RemoteDataSource {
    private var bearerProvider: BearerAuthProvider? = null
    private val authenticatedClient = client.config {
        install(Auth) {
            bearer {
                var tokens: BearerTokens? = null
                loadTokens {
                    tokens = tokensProvider.bearerTokens
                    tokens
                }

                refreshTokens {
                    val oldTokens = tokens
                    tokens = if (oldTokens == null) tokensProvider.bearerTokens
                    else getRefreshTokens(oldTokens.refreshToken).also {
                        if (it != null) GlobalScope.launch {
                            tokensProvider.updateTokens(it)
                        }
                    }
                    tokens
                }
            }
            bearerProvider = providers.last() as? BearerAuthProvider
        }
    }

    override val authService: AuthService = AuthServiceImpl(client, authenticatedClient)
    override val storageService: StorageService = StorageServiceImpl(client)
    override val chatService: ChatService = ChatServiceImpl(authenticatedClient)
    override val chatsSocketService: ChatsSocketService
        get() = ChatsSocketServiceImpl(authenticatedClient, jsonSerializer)
    override val chatSocketService: ChatSocketService
        get() = ChatSocketServiceImpl(authenticatedClient, jsonSerializer)

    init {
        GlobalScope.launch(Dispatchers.IO) {
            /**
             *   another solution is to pass the [bearerProvider] to [authService] and it clear token by itself,
             *   put I prefer current solution as it provides the ability of expansion.
             */
            authService.logout.collect { logout ->
                if (logout) bearerProvider?.clearToken()
            }
        }
    }

    private suspend fun getRefreshTokens(refreshToken: String): BearerTokens? {
        val bearerTokensResponse = safeApiCall<RefreshTokensResponse, ApiException> {
            authService.refreshTokens(RefreshTokensRequest(refreshToken))
        }
        return if (bearerTokensResponse is Resource.Success) bearerTokensResponse.data.bearerTokens(
            refreshToken
        )
        else null
    }

    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null
        private val LOCK = Any()

        operator fun invoke(
            tokensProvider: TokensProvider,
            jsonSerializer: Json = Json {
                isLenient = true
                ignoreUnknownKeys = true
            }
        ) = instance ?: synchronized(LOCK) {
            instance ?: RemoteDataSourceImpl(
                client = HttpClient(CIO) {
                    install(WebSockets)
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                    install(JsonFeature) {
                        serializer = KotlinxSerializer(jsonSerializer)
                    }
                },
                jsonSerializer = jsonSerializer,
                tokensProvider = tokensProvider
            ).also { instance = it }
        }


    }
}