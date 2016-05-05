package com.seguidor.item

import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder
import grails.util.Holders
import meli.exceptions.*
import org.apache.log4j.Logger
import org.grails.web.json.JSONElement
import org.grails.web.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.StringHttpMessageConverter
import org.grails.web.converters.exceptions.ConverterException

import java.nio.charset.Charset
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
        this.restBuilder.restTemplate.setMessageConverters([new StringHttpMessageConverter(Charset.defaultCharset.forName("UTF-8"))])
    }

    JSONElement handleParsing(response, url){
        try{
            return JSON.parse(response.responseEntity?.body?.toString())
        } catch(ConverterException e){
            throw new MercadoLibreAPIException("Url: "+ url +" detail: "+response.responseEntity.toString(), "error parsing json", [])
        }
    }

    JSONElement getResource(String url) {
        def response = restBuilder.get("${baseURL}${url}")
        handleResponse(response.responseEntity, url)
        handleParsing(response, url)
    }

    JSONElement postResource(String url, JSONObject objectParam) {
        url = "${baseURL}${url}"
        def response = restBuilder.post(url) {
            json objectParam.toString()
            header('Content-Type',' application/json;charset=UTF-8')
        }
        handleResponse(response.responseEntity, url)
        handleParsing(response, url)
    }

    JSONElement postResource(String url, String objectParam) {
        url = "${baseURL}${url}"
        def response = restBuilder.post(url) {
            contentType('application/x-www-form-urlencoded')
            body(objectParam.toString())
        }
        handleResponse(response.responseEntity, url)
        handleParsing(response, url)
    }

    JSONElement putResource(String url, JSONObject objectParam)
    {
        url = "${baseURL}${url}"
        def response =  restBuilder.put(url){
            json objectParam.toString()
            header('Content-Type',' application/json;charset=UTF-8')
        }
        handleResponse(response.responseEntity, url)
        handleParsing(response, url)
    }

    // TODO Deprecar
    def handleResponse(ResponseEntity responseEntity) {
        def statusCode =  responseEntity.statusCode
        if (!(statusCode in [HttpStatus.ACCEPTED, HttpStatus.CREATED, HttpStatus.OK, HttpStatus.NO_CONTENT])) {
            def errorMsg = "${responseEntity.body}"
            log.error("Url: " + url + " error: " + responseEntity.toString() )
            if (HttpStatus.NOT_FOUND == statusCode) {
                throw new NotFoundException(errorMsg)
            } else if (HttpStatus.BAD_REQUEST == statusCode) {
                throw new BadRequestException(errorMsg)
            } else if (HttpStatus.FORBIDDEN == statusCode) {
                throw new ForbiddenException(errorMsg)
            } else if (HttpStatus.UNAUTHORIZED == statusCode) {
                throw new UnauthorizedException(errorMsg)
            } else if (HttpStatus.CONFLICT == statusCode) {
                throw new ConflictException(errorMsg)
            } else {
                throw new MercadoLibreAPIException(errorMsg)
            }
        }
    }

    def handleResponse(ResponseEntity responseEntity, String url) {
        def statusCode =  responseEntity.statusCode
        if (!(statusCode in [HttpStatus.ACCEPTED, HttpStatus.CREATED, HttpStatus.OK, HttpStatus.NO_CONTENT])) {
            def errorMsg = "${responseEntity.body}"
            log.error("Url: " + url + " error: " + responseEntity.toString() )
            if (HttpStatus.NOT_FOUND == statusCode) {
                throw new NotFoundException(errorMsg, url)
            } else if (HttpStatus.BAD_REQUEST == statusCode) {
                throw new BadRequestException(errorMsg, url)
            } else if (HttpStatus.FORBIDDEN == statusCode) {
                throw new ForbiddenException(errorMsg, url)
            } else if (HttpStatus.UNAUTHORIZED == statusCode) {
                throw new UnauthorizedException(errorMsg, url)
            }else if (HttpStatus.CONFLICT == statusCode) {
                    throw new ConflictException(errorMsg, url)
            } else {
                throw new MercadoLibreAPIException(errorMsg, url, [])
            }
        }
    }
}