package com.seguidor.item

import meli.exceptions.NotFoundException
import meli.exceptions.AuthException
import seguidor.auth.User

trait AuthService implements RestService {

    def getUser(callerId) {
<<<<<<< HEAD
        def user = getUserInfo(callerId)
        def company
        if (user?.company_id == null || user?.company_id == -1 || user?.company_id == 0){
            throw new NotFoundException("Invalid company_id from user callerId: ${callerId}", "Invalid company_id of user")
=======
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
>>>>>>> origin/develop
        }
        company = getCompanyInfo(user?.company_id)
        User.buildUser(user, company)
    }

    private getUserInfo(callerId) {
        try {
            getResource("seguidor/users/${callerId}?caller.id=${callerId}")
        } catch (Exception e) {
            throw new AuthException("Error getting user", e.message, [])
        }
    }

    private getCompanyInfo(companyId) {
        try{
            getResource("seguidor/companies/${companyId}?caller.id=${companyId}")
        } catch (Exception e) {
            throw new AuthException("Error getting company", e.message, [])
        }

    }
}

