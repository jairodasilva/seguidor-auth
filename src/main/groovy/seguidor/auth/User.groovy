package seguidor.auth

import org.grails.web.json.JSONElement

@SuppressWarnings(['SpaceAroundMapEntryColon'])
class User {

    int id
    String nickname
    String firstName
    String lastName
    int companyId
    String email
    String type
    String role
    String status
    String areaCode
    String phone
    String areaCode2
    String phone2
    String companyName
    String companyFirstName
    String companyLastName
    String companyPhone
    String companyLogo

    def getPermissions() {
        def permissions
        switch (this.role.toString()) {
            case 'admin':
                permissions = [
                        searchCommunity: true,
                        searchInventory: true,
                        showItem       : true,
                        deleteItem     : true,
                        addItem        : true,
                        updateItem     : true,
                        publishItem    : true,
                        stopItem       : true,
                        finishItem     : true,
                        questionsItems : true,
                        assigneUser    : true
                ]
                break
            case 'director':
                permissions = [
                        searchCommunity: true,
                        searchInventory: true,
                        showItem       : true,
                        deleteItem     : true,
                        addItem        : true,
                        updateItem     : true,
                        publishItem    : true,
                        stopItem       : true,
                        finishItem     : true,
                        questionsItems : true,
                        assigneUser    : false
                ]
                break
            case 'assistant':
                permissions = [
                        searchCommunity: true,
                        searchInventory: true,
                        showItem       : true,
                        deleteItem     : true,
                        addItem        : true,
                        updateItem     : true,
                        publishItem    : true,
                        stopItem       : true,
                        finishItem     : true,
                        questionsItems : true,
                        assigneUser    : false
                ]
                break
            case 'agent':
                permissions = [
                        searchCommunity: true,
                        searchInventory: true,
                        showItem       : true,
                        deleteItem     : false,
                        addItem        : true,
                        updateItem     : false,
                        publishItem    : false,
                        stopItem       : false,
                        finishItem     : false,
                        questionsItems : true,
                        assigneUser    : false
                ]
                break
        }
    }

    static User buildUser(JSONElement userData, JSONElement companyData) {
        def areaCode = (userData.containsKey('phone') && userData.get('phone').containsKey('area_code')) ? userData.get('phone').get('area_code') : ''
        def number = (userData.containsKey('phone') && userData.get('phone').containsKey('number')) ? userData.get('phone').get('number') : ''
        def area_code2 = (userData.containsKey('alternative_phone') && userData.get('alternative_phone').containsKey('area_code')) ? userData.get('alternative_phone').get('area_code') : ''
        def number2 = (userData.containsKey('alternative_phone') && userData.get('alternative_phone').containsKey('number')) ? userData.get('alternative_phone').get('number') : ''
        def companyPhone = (companyData?.phone && companyData.phone?.area_code) ? companyData.get('phone').get('area_code') + ' - '+ companyData.get('phone').get('number') : ''
        def companyFirstName = (companyData?.first_name) ? companyData.get('first_name') : ''
        def companyLastName = (companyData?.last_name) ? companyData.get('last_name') : ''
        def companyName = (companyData?.company_name) ? companyData.get('company_name') : ''
        new User(
            id:userData.id,
            nickname:userData?.nickname,
            firstName:userData?.first_name,
            lastName:userData?.last_name,
            companyId:userData?.company_id,
            email:userData?.email,
            type:userData?.user_type,
            role:userData?.role,
            status:userData?.status,
            areaCode:areaCode,
            phone:number,
            areaCode2:area_code2,
            phone2:number2,
            companyName:companyName,
            companyFirstName:companyFirstName,
            companyLastName:companyLastName,
            companyPhone:companyPhone,
            companyLogo:companyData?.logo
        )
    }

}