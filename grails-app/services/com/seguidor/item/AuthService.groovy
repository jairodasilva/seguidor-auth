package com.seguidor.item

import meli.exceptions.NotFoundException
import meli.exceptions.AuthException
import seguidor.auth.User
import com.newrelic.api.agent.NewRelic

trait AuthService implements RestService {

    def getUser(callerId) {
        def user = getUserInfo(callerId)
        def company
        if (user?.company_id == null || user?.company_id == -1 || user?.company_id == 0){
            throw new NotFoundException("Invalid company_id from user callerId: ${callerId}", "Invalid company_id of user")
        }
        company = getCompanyInfo(user?.company_id)
        def userBuild = User.buildUser(user, company)
        userBuild
    }

    private getUserInfo(callerId) {
        try{
            getResource("seguidor/users/${callerId}?caller.id=${callerId}")
        }catch (Exception e) {
            throw new AuthException("Error getting user", e.message, [])
        }

    }

    private getCompanyInfo(companyId) {
        try{
            getResource("seguidor/companies/${companyId}?caller.id=${companyId}")
        }catch (Exception e) {
            throw new AuthException("Error getting company", e.message, [])
        }

    }
}

