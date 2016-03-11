package com.seguidor.item

import meli.exceptions.NotFoundException
import seguidor.auth.User

trait AuthService implements RestService {

    def getUser(callerId) {
        def user = getUserInfo(callerId)
        def company = getCompanyInfo(user?.company_id)
        User.buildUser(user, company)
    }

    private getUserInfo(callerId) {
        getResource("seguidor/users/${callerId}?caller.id=${callerId}")
    }

    private getCompanyInfo(companyId) {
        getResource("seguidor/companies/${companyId}?caller.id=${companyId}")
    }
}
