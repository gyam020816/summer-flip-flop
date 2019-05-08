package eu.ha3.x.sff.connector.kgraphql

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import eu.ha3.x.sff.api.SDocStorage
import eu.ha3.x.sff.core.Doc
import eu.ha3.x.sff.core.DocCreateRequest
import eu.ha3.x.sff.core.DocListResponse
import eu.ha3.x.sff.test.TestSample
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * (Default template)
 * Created on 2019-05-08
 *
 * @author Ha3
 */
class KGraphqlSchemaTest {
    private val mockDocStorage = mock<SDocStorage>()
    private val SUT = KGraphqlSchema(mockDocStorage)

    @Test
    fun `it should return a list of docs`() {
        val expected = listOf(Doc("someDoc", TestSample.zonedDateTime))
        mockDocStorage.stub {
            onBlocking { listAll() }.doReturn(DocListResponse(expected))
        }

        // Exercise
        val result = SUT.schema().execute(
"""{
  docs {
    name
    createdAt
  }
}""")

        // Verify
        assertThat(result).isEqualToNormalizingNewlines(
"""{
  "data" : {
    "docs" : [ {
      "name" : "someDoc",
      "createdAt" : "${TestSample.zonedDateTimeSerialized}"
    } ]
  }
}"""
        )
    }

    @Test
    fun `it should create a doc`() {
        val request = DocCreateRequest("someDoc")
        val expected = Doc("someDoc", TestSample.zonedDateTime)
        mockDocStorage.stub {
            onBlocking { appendToDocs(request) }.doReturn(expected)
        }

        // Exercise
        val result = SUT.schema().execute(
"""{
  createDoc(name: "someDoc") {
    name
    createdAt
  }
}""")

        // Verify
        assertThat(result).isEqualToNormalizingNewlines(
"""{
  "data" : {
    "createDoc" : {
      "name" : "someDoc",
      "createdAt" : "${TestSample.zonedDateTimeSerialized}"
    }
  }
}"""
        )
    }

    @Test
    fun `it should return the name of the docs`() {
        val expected = listOf(
                Doc("someDoc", TestSample.zonedDateTime),
                Doc("someOtherDoc", TestSample.zonedDateTime)
        )
        mockDocStorage.stub {
            onBlocking { listAll() }.doReturn(DocListResponse(expected))
        }

        // Exercise
        val result = SUT.schema().execute(
                """{
  docs {
    name
  }
}""")

        // Verify
        assertThat(result).isEqualToNormalizingNewlines(
                """{
  "data" : {
    "docs" : [ {
      "name" : "someDoc"
    }, {
      "name" : "someOtherDoc"
    } ]
  }
}"""
        )
    }
}