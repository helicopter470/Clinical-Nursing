import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/', component: () => import('@/views/Main.vue'),
            redirect:'/home',
            children: [
                { path: '/home' , component:() => import('@/views/main/Home.vue')},
                { path: '/nurse' , component:() => import('@/views/main/Nurse.vue')},
                { path: '/knowledge' , component:() => import('@/views/main/Knowledge.vue')},
                { path: '/knowledge/:id' , component:() => import('@/views/main/KnowledgeDetail.vue')},
                { path: '/service' , component:() => import('@/views/main/Service.vue')},
                { path: '/hp' , component:() => import('@/views/main/Hp.vue')},
                { path: '/dailyMonitor' , component:() => import('@/views/main/DailyMonitor.vue')},
                { path: '/nurseService' , component:() => import('@/views/main/NurseService.vue')},
                { path: '/nurseReserve' , component:() => import('@/views/main/NurseReserve.vue')},
                { path: '/serviceReserve' , component:() => import('@/views/main/ServiceReserve.vue')},
                { path: '/evaluation' , component:() => import('@/views/main/Evaluation.vue')},
                { path: '/careKnowledge' , component:() => import('@/views/main/CareKnowledge.vue')},
                { path: '/wardInfo' , component:() => import('@/views/main/Ward.vue')},
                { path: '/adminInfo' , component:() => import('@/views/main/AdminInfo.vue')},
                { path: '/nurseInfo' , component:() => import('@/views/main/NurseInfo.vue')},
                { path: '/patientInfo' , component:() => import('@/views/main/PatientInfo.vue')},
                { path: '/person', component: () => import('@/views/main/Person.vue')},
                { path: '/password' , component:() => import('@/views/main/Password.vue')},
            ]
        },
        { path: '/login', component: () => import('@/views/Login.vue') },
        { path: '/register', component: () => import('@/views/Register.vue') }
    ]
})

// 简单登录守卫：没有 token 不允许进入业务页
router.beforeEach((to, from, next) => {
    const whiteList = ['/login', '/register']
    if (whiteList.includes(to.path)) return next()

    let token = ''
    try {
        const user = JSON.parse(localStorage.getItem('system-user') || '{}')
        token = user?.accessToken || user?.token || user?.jwt || ''
    } catch (e) { }

    if (!token) return next('/login')
    next()
})

export default router