package com.seguidor.item

import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder
import grails.util.Holders
import meli.exceptions.BadRequestException
import meli.exceptions.MercadoLibreAPIException
import meli.exceptions.NotFoundException
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

    private handleResponse(ResponseEntity responseEntity){
        HttpStatus statusCode = responseEntity.statusCode
        if (HttpStatus.OK!=statusCode && HttpStatus.ACCEPTED!=statusCode){
            def errorMsg = "${responseEntity.body}"
            log.error(errorMsg)
            if (HttpStatus.NOT_FOUND == statusCode) {
                log.error(errorMsg)
                throw new NotFoundException(errorMsg)
            } else if (HttpStatus.BAD_REQUEST == statusCode) {
                log.error(errorMsg)
                throw new BadRequestException(errorMsg)
            } else {
                log.error(errorMsg)
                throw new MercadoLibreAPIException(errorMsg)
            }
        }
    }
}
