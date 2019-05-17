package eu.ha3.x.sff.system

import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.core.NoMessage
import io.reactivex.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

/**
 * (Default template)
 * Created on 2018-12-09
 *
 * @author Ha3
 */
class ReactiveToCoroutineDocPersistenceSystem(private val docSystem: SDocPersistenceSystem) : RxDocPersistenceSystem {
    override fun listAll(): Single<DocListResponse> = coroutineSingle {
        docSystem.listAll()
    }

    override fun appendToDocs(doc: Doc): Single<NoMessage> = coroutineSingle {
        docSystem.appendToDocs(doc)
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
