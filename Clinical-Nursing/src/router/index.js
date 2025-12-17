import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
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
export default router