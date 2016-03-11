package spring


beans = {
//    xmlns cache: 'http://www.springframework.org/schema/cache'
//    xmlns aop: 'http://www.springframework.org/schema/aop'

    restBuilder(grails.plugins.rest.client.RestBuilder) {}


//    cache.'advice'(id: 'restServiceCacheAdvice',  'cache-manager': 'grailsCacheManager') {
//        caching(cache: 'users') {
////            cacheable(method: 'getUser')
//            'cache-evict'(method: 'getUser', key: '#callerId')
//        }
//    }
//
//    // apply the cacheable behavior to MessageService
//    aop.config {
//        advisor('advice-ref': 'restServiceCacheAdvice',
//                pointcut: 'execution(* com.seguidor.item.AuthService.*(..))')
//    }
}
