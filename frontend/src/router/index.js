import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import GymChatView from '../views/GymChatView.vue'
import ManusChatView from '../views/ManusChatView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: HomeView },
    { path: '/gym', name: 'gym', component: GymChatView },
    { path: '/manus', name: 'manus', component: ManusChatView }
  ]
})

export default router
