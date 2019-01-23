package eu.ha3.x.sff.api

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.SEvent
import eu.ha3.x.sff.test.TestSample
import eu.ha3.x.sff.test.testBlocking
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch

/**
 * (Default template)
 * Created on 2019-01-23
 *
 * @author gyam
 */
internal class ChannelledSubjectsTest {
    // https://github.com/Kotlin/kotlinx.coroutines/blob/master/reactive/coroutines-guide-reactive.md

    private val SUT = ChannelledSubjects(10)

    @AfterEach
    internal fun tearDown() {
        SUT.closeEverything()
    }

    @Test
    internal fun `it should publish an event to the channel`() = testBlocking {
        val latch = CountDownLatch(1)
        val expected = SEvent.DocumentCreated(Doc("hello", TestSample.zonedDateTime))

        // Capture
        val channel = SUT.onDocumentCreatedChannel()
        launch {
            channel.consumeEach { doc ->
                // Verify
                assertThat(doc).isEqualTo(expected)
                latch.countDown()
            }
        }
        delay(200)

        // Exercise
        channel.offer(expected)
        yield()

        // Verify
        latch.await()
        channel.close()
    }

    @Test
    internal fun `it should receive the same broadcast event to multiple consumers`() = testBlocking {
        val consumerA = CountDownLatch(1)
        val consumerB = CountDownLatch(1)
        val expected = SEvent.DocumentCreated(Doc("hello", TestSample.zonedDateTime))

        // Capture
        val channel = SUT.onDocumentCreatedChannel()
        launch {
            channel.openSubscription().consumeEach { doc ->
                // Verify
                assertThat(doc).isEqualTo(expected)
                consumerA.countDown()
            }
        }
        launch {
            channel.openSubscription().consumeEach { doc ->
                // Verify
                assertThat(doc).isEqualTo(expected)
                consumerB.countDown()
            }
        }
        delay(200)

        // Exercise
        channel.offer(expected)
        yield()

        // Verify
        consumerA.await()
        consumerB.await()
        channel.close()
    }
}