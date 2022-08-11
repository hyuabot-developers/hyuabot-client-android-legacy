package app.kobuggi.hyuabot.utils

import androidx.lifecycle.Observer

class EventObserver<T>(private val onEventUnhandledEvent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event ?: return
        if (event.peekContent() == null && !event.hasBeenHandled) {
            event.getContentIfNotHandled()
            onEventUnhandledEvent(event.peekContent())
            return
        }
        event.getContentIfNotHandled()?.let {
            value -> onEventUnhandledEvent(value)
        }
    }
}