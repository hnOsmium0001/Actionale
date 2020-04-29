package io.github.hnosmium0001.actionale

import java.util.*

interface ListenerMap<L : Any> : MutableMap<Any, L> {
    operator fun plusAssign(listener: L) {
        this[listener] = listener
    }
}

class HashListenerMap<L : Any> : HashMap<Any, L>(), ListenerMap<L>
class LinkedHashListenerMap<L : Any> : LinkedHashMap<Any, L>(), ListenerMap<L>
class IdentityHashListenerMap<L : Any> : IdentityHashMap<Any, L>(), ListenerMap<L>
class TreeListenerMap<L : Any> : TreeMap<Any, L>(), ListenerMap<L>