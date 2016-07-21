package com.seguidor.item

import com.mercadolibre.opensource.frameworks.restclient.RestClient
import meli.exceptions.BadRequestException
import meli.exceptions.ConflictException
import meli.exceptions.MercadoLibreAPIException
import meli.exceptions.NotFoundException
import meli.exceptions.ForbiddenException
import meli.exceptions.UnauthorizedException
import org.apache.log4j.Logger
import org.grails.web.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

/**
 * Common service options
 *
 * @author Camilo Verdugo
 */
trait RestService {
    private static final Logger log = Logger.getLogger(getClass())

    @Autowired
    RestClient restClient

    def getResource(String uri){
        uri = absolute(uri)

        def result
        restClient.get(uri: uri,
                headers: headers(),
                success: {
                    result = it.data
                },
                failure: {
                    onFailure(uri, 'GET', [], it)
                }
        )

        convertJsonNulltoPrimitiveNull(result)
    }

    def postResource(String uri, data) {
        uri = absolute(uri)

        def result
        restClient.post(uri: uri,
                data: data,
                headers: headers(),
                success: {
                    result = it.data
                },
                failure: {
                    onFailure(uri, 'POST', data, it)
                }
        )

        convertJsonNulltoPrimitiveNull(result)
    }

    def deleteResource(String uri) {
        uri = absolute(uri)

        def result
        restClient.delete(uri: uri,
                headers: headers(),
                success: {
                    result = it.data
                },
                failure: {
                    onFailure(uri, 'DELETE', [], it)
                }
        )

        convertJsonNulltoPrimitiveNull(result)
    }

    def putResource(String uri, data) {
        uri = absolute(uri)

        def result
        restClient.put(uri: uri,
                data: data,
                headers: headers(),
                success: {
                    result = it.data
                },
                failure: {
                    onFailure(uri, 'PUT', data, it)
                }
        )

        convertJsonNulltoPrimitiveNull(result)
    }

    void onFailure(uri, verb, jsonData, response) {
        def reason = (response?.data)?response?.data?.toString():response?.exception?.cause
        def errorMsg = "Error on ${verb} to URI: [${uri}], StatusCode: [${response?.status?.statusCode}], " +
                "Reason: [${reason}]\n"

        log.error(errorMsg)
        if(!response?.status){
            throw new MercadoLibreAPIException(errorMsg.toString())
        }
        this.handleError(Integer.valueOf((response?.status?.statusCode)?:500), errorMsg.toString(), uri)
        if (response.exception) {
            throw response.exception
        } else {
            throw new RuntimeException(errorMsg)
        }
    }

    def getErrorMessage(error, url) {
        return "${error} [Url: ${url}]"
    }

    def handleError(Integer code, String errorMessage, String url) {
        HttpStatus statusCode = HttpStatus.valueOf(code)
        if (!(statusCode in [HttpStatus.ACCEPTED, HttpStatus.CREATED, HttpStatus.OK, HttpStatus.NO_CONTENT])) {
            switch (statusCode) {
                case HttpStatus.NOT_FOUND:
                    throw new NotFoundException(getErrorMessage(errorMessage,url))
                case HttpStatus.BAD_REQUEST:
                    throw new BadRequestException(getErrorMessage(errorMessage,url))
                case HttpStatus.FORBIDDEN:
                    throw new ForbiddenException(getErrorMessage(errorMessage,url))
                case HttpStatus.UNAUTHORIZED:
                    throw new UnauthorizedException(getErrorMessage(errorMessage,url))
                case HttpStatus.CONFLICT:
                    throw new ConflictException(getErrorMessage(errorMessage,url))
                default:
                    throw new MercadoLibreAPIException(getErrorMessage(errorMessage,url))
            }
        }
    }

    def convertJsonNulltoPrimitiveNull(object) {
        if (object instanceof Collection) {
            object.each {
                convertJsonNulltoPrimitiveNull(it)
            }
        }

        if (object instanceof Map) {
            for (tuple in object) {
                if (tuple.value.getClass() == net.sf.json.JSONNull || net.sf.json.JSONNull.instance == tuple.value || JSONObject.NULL == tuple.value) {
                    tuple.value = null
                } else if(tuple.value instanceof Map || tuple.value instanceof Collection) {
                    convertJsonNulltoPrimitiveNull(tuple.value)
                }

            }
        }
        object
    }

    def private headers() {
        def value = ["Encoding": "UTF-8"]
        if (Scope.TEST()) {
            value.put("X-Api-Test", true)
        }
        return value
    }

    def private absolute(String uri) {
        uri.startsWith('/') ? uri : ('/' + uri);
    }

}