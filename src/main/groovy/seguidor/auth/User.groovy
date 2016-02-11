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
    String companyPhone

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
                        questionsItems : true
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
                        questionsItems : true
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
                        questionsItems : true
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
                        questionsItems : true
                ]
                break
        }
    }

    static User buildUser(JSONElement userData, JSONElement companyData) {
        new User(
            id:userData.id,
            nickname:userData.nickname,
            firstName:userData.first_name,
            lastName:userData.last_name,
            companyId:userData.company_id,
            email:userData.email,
            type:userData.user_type,
            role:userData.role,
            status:userData.status,
            areaCode:userData?.phone?.area_code,
            phone:userData?.phone?.number,
            areaCode2:userData?.alternative_phone?.area_code ? userData?.alternative_phone?.area_code:'',
            phone2:userData?.alternative_phone?.number ? userData?.alternative_phone?.number:'',
            companyName:"${companyData?.company?.first_name} ${companyData?.company?.last_name}",
            companyPhone:"${companyData?.company?.phone?.area_code} ${companyData?.company?.phone?.number}"
        )
    }

}