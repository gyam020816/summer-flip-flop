package eu.ha3.x.sff.api

import eu.ha3.x.sff.core.SearchResult
import io.reactivex.Observable

/**
 * (Default template)
 * Created on 2018-09-03
 *
 * @author Ha3
 */
interface ISearchEngine {
    fun search(): Observable<SearchResult>
}