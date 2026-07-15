Router (optional)
===================

This project template does not force a router dependency. If you want route-based navigation, install `vue-router` and
create `src/router/index.ts`.

Example quick-start:

1. npm install vue-router@4
2. Create `src/router/index.ts` with a simple router and import it in `src/main.ts`.

Example router (TypeScript):

```ts
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'

const routes = [{ path: '/', name: 'home', component: HomeView }]

export const router = createRouter({ history: createWebHistory(), routes })
```

Then in `src/main.ts` call `app.use(router)` before `app.mount(...)`.

