package com.seguidor.item

import meli.exceptions.NotFoundException
import seguidor.auth.User
import com.newrelic.api.agent.NewRelic

trait AuthService implements RestService {

    def getUser(callerId) {
        def userBuild
        try {
            def user = getUserInfo(callerId)
            def company
            if (user?.company_id == null || user?.company_id == -1 || user?.company_id == 0){
                throw new NotFoundException("Error getting company_id from user callerId: ${callerId}", "Error getting user")
            }
            company = getCompanyInfo(user?.company_id)
            userBuild = User.buildUser(user, company)
        } catch(Exception e) {
            throw e
        }
        userBuild
    }

    private getUserInfo(callerId) {
        getResource("seguidor/users/${callerId}?caller.id=${callerId}")
    }

    private getCompanyInfo(companyId) {
        getResource("seguidor/companies/${companyId}?caller.id=${companyId}")
    }
}

