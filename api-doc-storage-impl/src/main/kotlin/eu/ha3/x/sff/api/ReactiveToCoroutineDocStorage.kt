package eu.ha3.x.sff.api

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocCreateRequest
import eu.ha3.x.sff.core.DocListPaginationRequest
import eu.ha3.x.sff.core.DocListResponse
import io.reactivex.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

/**
 * (Default template)
 * Created on 2019-01-14
 *
 * @author Ha3
 */
class ReactiveToCoroutineDocStorage(private val docStorage: SDocStorage) : RxDocStorage {
    override fun appendToDocs(request: DocCreateRequest): Single<Doc> = coroutineSingle {
        docStorage.appendToDocs(request)
    }

    override fun listAll(): Single<DocListResponse> = coroutineSingle {
        docStorage.listAll()
    }

    override fun listPaginated(request: DocListPaginationRequest)= coroutineSingle {
        docStorage.listPaginated(request)
    }

    private fun <T> coroutineSingle(coroutineFn: suspend CoroutineScope.() -> T): Single<T> = Single.create { handler ->
        try {
            val result = runBlocking(block = coroutineFn)
            handler.onSuccess(result)

        } catch (e: Exception) {
            handler.onError(e)
        }
    }
}