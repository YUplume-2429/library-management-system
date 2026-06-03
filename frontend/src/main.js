import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'

const app = createApp(App)

// 使用 Pinia 状态管理
app.use(createPinia())

// 使用路由
app.use(router)

// 使用 Element Plus
app.use(ElementPlus)

app.mount('#app')
