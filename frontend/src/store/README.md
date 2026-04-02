Store (optional)
================

This template does not include a runtime store library by default to keep the project minimal. Recommended options:

- Pinia (official): `npm install pinia`
- Simple composables: create composables under `src/composables/` for local state

Quick Pinia example (after install):

```ts
// src/store/index.ts
import { createPinia } from 'pinia'
export const pinia = createPinia()
```

Then in `src/main.ts` call `app.use(pinia)`.

