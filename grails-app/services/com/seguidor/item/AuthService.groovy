package com.seguidor.item

import meli.exceptions.NotFoundException
import seguidor.auth.User


trait AuthService implements RestService {

    def cachedUsers = [:]

    def getUser(callerId) {
        def user, company
        if (!cachedUsers.get(callerId)) {
            user = getUserInfo(callerId)
            company = getCompanyInfo(user.company_id)
            cachedUsers.put(callerId, User.buildUser(user, company))
        }
        cachedUsers.get(callerId)
    }

    private getUserInfo(callerId) {
        try {
            return getResource("seguidor/users/${callerId}")
        }
        catch (Exception e) {
            throw new NotFoundException('callerId does not exist', 'Invalid caller.id')
        }
    }

    private getCompanyInfo(companyId) {
        try {
            return getResource("seguidor/companies/${companyId}?caller.id=${companyId}")
        }
        catch (Exception e) {
            throw new NotFoundException('Company.id does not exist', 'Invalid caller.id')
        }
    }
}
