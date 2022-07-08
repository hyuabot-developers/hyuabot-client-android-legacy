package app.kobuggi.hyuabot.ui.main.cafeteria

import androidx.lifecycle.ViewModel
import com.apollographql.apollo3.ApolloClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CafeteriaViewModel @Inject constructor(private val client: ApolloClient) : ViewModel() {
    fun fetchData() {

    }
}

