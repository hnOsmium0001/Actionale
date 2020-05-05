package io.github.hnosmium0001.actionale.core

import java.util.*

interface ListenerMap<L> : MutableMap<Any, L> where L : Any {
    operator fun plusAssign(listener: L) {
        this[listener] = listener
    }
}

class HashListenerMap<L> : HashMap<Any, L>(), ListenerMap<L> where L : Any
class LinkedHashListenerMap<L> : LinkedHashMap<Any, L>(), ListenerMap<L> where L : Any
class IdentityHashListenerMap<L> : IdentityHashMap<Any, L>(), ListenerMap<L> where L : Any
class TreeListenerMap<L> : TreeMap<Any, L>(), ListenerMap<L> where L : Any