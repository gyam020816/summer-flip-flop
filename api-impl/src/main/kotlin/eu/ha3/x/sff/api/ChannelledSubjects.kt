package eu.ha3.x.sff.api

import eu.ha3.x.sff.core.SEvent
import kotlinx.coroutines.channels.BroadcastChannel

/**
 * (Default template)
 * Created on 2019-01-23
 *
 * @author gyam
 */
class ChannelledSubjects(val capacity: Int) : ChSubjects {
    private val onDocumentCreated = BroadcastChannel<SEvent.DocumentCreated>(capacity)

    override fun onDocumentCreatedChannel() = onDocumentCreated

    override fun closeEverything() {
        onDocumentCreated.close()
    }
}