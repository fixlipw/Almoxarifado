<script lang="ts" setup>
import {Check, MonitorCog, Moon, Palette, Sun} from 'lucide-vue-next'
import {Button} from '@/components/ui/button'
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from '@/components/ui/card'
import {useNotificationStore} from '@/stores/notifications'
import {type ThemeMode, themeOptions, type ThemePreset, useThemePreference} from '@/composables/useThemePreference'

const theme = useThemePreference()
const notifications = useNotificationStore()

function selectPreset(value: ThemePreset) {
  theme.setPreset(value)
  notifications.success('Tema aplicado.')
}

function selectMode(value: ThemeMode) {
  theme.setMode(value)
  notifications.success('Modo de aparência atualizado.')
}
</script>

<template>
  <div class="space-y-7">
    <div>
      <p class="text-sm font-medium text-primary">Preferências</p>
      <h1 class="text-3xl font-bold tracking-tight">Configurações</h1>
      <p class="mt-1 text-sm text-muted-foreground">Personalize como o Almoxarifado aparece para você.</p>
    </div>

    <Card>
      <CardHeader>
        <div class="flex items-start gap-3">
          <span class="grid size-10 shrink-0 place-items-center rounded-xl bg-primary/10 text-primary"><MonitorCog
              class="size-5"/></span>
          <div>
            <CardTitle>Modo de aparência</CardTitle>
            <CardDescription>Escolha entre a interface clara e escura.</CardDescription>
          </div>
        </div>
      </CardHeader>
      <CardContent class="grid gap-3 sm:grid-cols-2">
        <Button :variant="theme.mode.value === 'light' ? 'default' : 'outline'" class="h-auto justify-start gap-3 p-4"
                @click="selectMode('light')">
          <Sun class="size-5"/>
          <span class="text-left"><strong class="block">Claro</strong><small class="font-normal opacity-75">Mais luminoso e leve</small></span>
        </Button>
        <Button :variant="theme.mode.value === 'dark' ? 'default' : 'outline'" class="h-auto justify-start gap-3 p-4"
                @click="selectMode('dark')">
          <Moon class="size-5"/>
          <span class="text-left"><strong class="block">Escuro</strong><small class="font-normal opacity-75">Confortável em pouca luz</small></span>
        </Button>
      </CardContent>
    </Card>

    <Card>
      <CardHeader>
        <div class="flex items-start gap-3">
          <span class="grid size-10 shrink-0 place-items-center rounded-xl bg-primary/10 text-primary"><Palette
              class="size-5"/></span>
          <div>
            <CardTitle>Temas</CardTitle>
            <CardDescription>
              Temas pré-definidos a sua escolha.
            </CardDescription>
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <div class="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
          <button v-for="option in themeOptions" :key="option.id"
                  :class="theme.preset.value === option.id ? 'border-primary ring-2 ring-primary/25' : 'hover:border-primary/50'"
                  class="group relative rounded-xl border bg-background p-4 text-left transition"
                  type="button"
                  @click="selectPreset(option.id)">
            <span v-if="theme.preset.value === option.id"
                  class="absolute right-3 top-3 grid size-6 place-items-center rounded-full bg-primary text-primary-foreground"><Check
                class="size-3.5"/></span>
            <span class="mb-4 flex gap-1.5">
              <span v-for="color in option.colors" :key="color" :style="{ backgroundColor: color }"
                    class="size-8 rounded-full border shadow-sm"/>
            </span>
            <strong class="block">{{ option.name }}</strong>
            <span class="mt-1 block pr-6 text-xs text-muted-foreground">{{ option.description }}</span>
          </button>
        </div>
      </CardContent>
    </Card>
  </div>
</template>
