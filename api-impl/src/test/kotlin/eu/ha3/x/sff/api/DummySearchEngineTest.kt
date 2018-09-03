package eu.ha3.x.sff.api

import eu.ha3.x.sff.core.SearchResult
import io.reactivex.observers.TestObserver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * (Default template)
 * Created on 2018-09-03
 *
 * @author Ha3
 */
internal class DummySearchEngineTest {
    val SUT = DummySearchEngine()

    @Test
    fun `it should return a terminated observable`() {
        // Setup
        val subscriber = TestObserver<SearchResult>()

        // Exercise
        val obs = SUT.search()
        obs.subscribe(subscriber)

        // Verify
        assertThat(subscriber.isTerminated).isTrue();
        assertThat(subscriber.values()).containsExactly(SearchResult("some dummy value"))
    }
}