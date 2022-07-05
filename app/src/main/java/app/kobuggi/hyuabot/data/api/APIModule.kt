package app.kobuggi.hyuabot.data.api

import app.kobuggi.hyuabot.BuildConfig
import com.apollographql.apollo3.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object APIModule {
    @Provides
    fun provideGraphQLClient() = ApolloClient.Builder()
        .serverUrl("${BuildConfig.server_url}/api/v2")
        .build()
}