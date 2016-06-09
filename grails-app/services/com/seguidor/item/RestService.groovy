package com.seguidor.item

import grails.converters.JSON
import meli.exceptions.BadRequestException
import meli.exceptions.ConflictException
import meli.exceptions.MercadoLibreAPIException
import meli.exceptions.NotFoundException
import meli.exceptions.ForbiddenException
import meli.exceptions.UnauthorizedException
import com.mercadolibre.opensource.frameworks.restclient.RestClient
import org.springframework.beans.factory.annotation.Autowired
import org.apache.log4j.Logger
import org.grails.web.json.JSONObject
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

    def getResource(String url){
        if(!url.startsWith('/')) {
            url = '/' + url
        }

        def info

        restClient.get(uri: "${url}".toString(),
                success: {
                    info = it.data
                },
                failure: {
                    onFailure(url, 'GET', [], it)
                }
        )

        convertJsonNulltoPrimitiveNull(info)
        info
    }

    def postResource(String uri, jsonData) {
        if(!uri.startsWith('/')) {
            uri = '/' + uri
        }

        def jsonResult
        restClient.post(uri: uri.toString(),
                data: jsonData,
                headers: [ "Encoding" : "UTF-8"],
                success: {
                    jsonResult = it.data
                },
                failure: {
                    onFailure(uri, 'POST', jsonData, it)
                })

        convertJsonNulltoPrimitiveNull(jsonResult)
        jsonResult
    }

    def deleteResource(String uri) {
        if(!uri.startsWith('/')) {
            uri = '/' + uri
        }

        def jsonResult
        restClient.delete(uri: uri.toString(),
                headers: [ "Encoding" : "UTF-8"],
                success: {
                    jsonResult = it.data
                },
                failure: {
                    onFailure(uri, 'DELETE', [], it)
                })

        convertJsonNulltoPrimitiveNull(jsonResult)
        jsonResult
    }

    def putResource(String uri, jsonData) {
        if(!uri.startsWith('/')) {
            uri = '/' + uri
        }

        def jsonResult

        restClient.put(uri: uri.toString(),
                data: jsonData,
                headers: [ "Encoding" : "UTF-8"],
                success: {
                    jsonResult = it.data
                },
                failure: {
                    onFailure(uri, 'PUT', jsonData, it)
                })

        convertJsonNulltoPrimitiveNull(jsonResult)
        jsonResult
    }

    void onFailure(uri, verb, jsonData, response) {
        def errorMsg = "Error on ${verb} to URI: [${uri}], StatusCode: [${response?.status?.statusCode}], Reason: ${response?.data?.toString()}\n"

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

}