package com.seguidor.item

import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder
import grails.util.Holders
import meli.exceptions.BadRequestException
import meli.exceptions.MercadoLibreAPIException
import meli.exceptions.NotFoundException
import meli.exceptions.ForbiddenException
import meli.exceptions.UnauthorizedException
import org.apache.log4j.Logger
import org.grails.web.json.JSONElement
import org.grails.web.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * Common service options
 *
 * @author Camilo Verdugo
 */
trait RestService {

    private static final Logger log = Logger.getLogger(getClass())

    RestBuilder restBuilder = Holders.grailsApplication.getMainContext().getBean('restBuilder')
    String baseURL = Holders.grailsApplication.config.getProperty('grails.serverURL')
    public init(){

    }
    JSONElement getResource(String url) {
        def response = restBuilder.get("${baseURL}${url}")
        handleResponse(response.responseEntity)
        log.info("Returning" + url + " info: " + response.responseEntity.body )
        return JSON.parse(response.responseEntity.body.toString())
    }

    JSONElement postResource(String url, JSONObject objectParam) {
        url = "${baseURL}${url}"
        def response = restBuilder.post(url) {
            json objectParam.toString()
        }
        handleResponse(response.responseEntity)
        return JSON.parse(response.responseEntity.body.toString())
    }

    JSONElement postResource(String url, String objectParam) {
        url = "${baseURL}${url}"
        def response = restBuilder.post(url) {
            contentType('application/x-www-form-urlencoded')
            body(objectParam.toString())
        }
        handleResponse(response.responseEntity)
        return JSON.parse(response.responseEntity.body.toString())
    }

    JSONElement putResource(String url, JSONObject objectParam)
    {
        url = "${baseURL}${url}"
        def response =  restBuilder.put(url){
            json objectParam
        }
        handleResponse(response.responseEntity)
        log.info("Returning" + url + " info: " + response.responseEntity.body )
        return JSON.parse(response.responseEntity.body.toString())
    }

    def handleResponse(ResponseEntity responseEntity) {
        HttpStatus statusCode = responseEntity.statusCode
        if (HttpStatus.OK != statusCode && HttpStatus.CREATED != statusCode) {
            def errorMsg = "${responseEntity.body}"
            //logger.error(errorMsg)
            if (HttpStatus.NOT_FOUND == statusCode) {
                //logger.error(errorMsg)
                throw new NotFoundException(errorMsg)
            } else if (HttpStatus.BAD_REQUEST == statusCode) {
                //logger.error(errorMsg)
                throw new BadRequestException(errorMsg)
            } else if (HttpStatus.FORBIDDEN == statusCode) {
                //logger.error(errorMsg)
                throw new ForbiddenException(errorMsg)
            } else if (HttpStatus.UNAUTHORIZED == statusCode) {
                //logger.error(errorMsg)
                throw new UnauthorizedException(errorMsg)
            } else {
                //logger.error(errorMsg)
                throw new MercadoLibreAPIException(errorMsg)
            }
        }
    }
}