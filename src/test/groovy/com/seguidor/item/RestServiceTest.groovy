package com.seguidor.item

import com.mercadolibre.opensource.frameworks.restclient.RestClient
import spock.lang.Specification

class RestServiceSpecs extends Specification {

    def restClient = Mock(RestClient)
    def service = new Object().withTraits(RestService)

    def setup() {
        service.restClient = restClient
    }

    def "should get a resource"() {
        when:
        service.getResource(uri)

        then:
        1 * restClient.get({ it["uri"] == uri })

        where:
        uri          | _
        "/users/111" | _
        "/users/222" | _
        "/users/333" | _
    }

    def "should post a resource"() {
        when:
        service.postResource(uri, data)

        then:
        1 * restClient.post({ it["uri"] == uri && it["data"] == data })

        where:
        uri      | data
        "/users" | [name:'John', age:25]
        "/users" | [name:'Paul', age:31]
        "/users" | [name:'Cris', age:18]
    }

    def "should put a resource"() {
        when:
        service.putResource(uri, data)

        then:
        1 * restClient.put({ it["uri"] == uri && it["data"] == data })

        where:
        uri        | data
        "/users/1" | [age:19]
        "/users/2" | [age:27]
        "/users/3" | [age:18]
    }

    def "should delete a resource"() {
        when:
        service.deleteResource(uri)

        then:
        1 * restClient.delete({ it["uri"] == uri })

        where:
        uri          | _
        "/users/111" | _
        "/users/222" | _
        "/users/333" | _
    }

    def "should only request with absolute uri"() {
        when:
        service.getResource(uri)
        service.postResource(uri, null)
        service.putResource(uri, null)
        service.deleteResource(uri)

        then:
        1 * restClient.get({ it["uri"] == fixed })
        1 * restClient.post({ it["uri"] == fixed })
        1 * restClient.put({ it["uri"] == fixed })
        1 * restClient.delete({ it["uri"] == fixed })

        where:
        uri         | fixed
        "users/111" | "/users/111"
        "users/222" | "/users/222"
        "users/333" | "/users/333"
    }

    def "should add the X-Api-Test header when running on test scope"() {
        when:
        Scope.metaClass.static.TEST = { test }
        service.getResource(uri)
        service.postResource(uri, null)
        service.putResource(uri, null)
        service.deleteResource(uri)

        then:
        1 * restClient.get({ it["uri"] == uri && it["headers"] == headers })
        1 * restClient.post({ it["uri"] == uri && it["headers"] == headers })
        1 * restClient.put({ it["uri"] == uri && it["headers"] == headers })
        1 * restClient.delete({ it["uri"] == uri && it["headers"] == headers })

        where:
        uri        | test  | headers
        "/users/1" | true  | ["Encoding":"UTF-8", "X-Api-Test": true]
        "/users/2" | false | ["Encoding":"UTF-8"]
        "/users/3" | false | ["Encoding":"UTF-8"]
    }

}
