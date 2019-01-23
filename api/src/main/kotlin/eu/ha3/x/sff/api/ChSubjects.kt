package eu.ha3.x.sff.api

import eu.ha3.x.sff.core.SEvent
import kotlinx.coroutines.channels.BroadcastChannel

/**
 * (Default template)
 * Created on 2019-01-23
 *
 * @author gyam
 */
interface ChSubjects {
    fun onDocumentCreatedChannel(): BroadcastChannel<SEvent.DocumentCreated>
    fun closeEverything(): Unit
}
