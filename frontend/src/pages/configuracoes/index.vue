<script lang="ts" setup>
import {Check, MonitorCog, Moon, Palette, Sun} from 'lucide-vue-next'
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion'
import {Button} from '@/components/ui/button'
import {Card} from '@/components/ui/card'
import {type ThemeMode, themeOptions, type ThemePreset, useThemePreference} from '@/composables/useThemePreference'
import {useNotificationStore} from '@/stores/notifications'

const theme = useThemePreference()
const notifications = useNotificationStore()

function selectPreset(value: ThemePreset) {
  if (theme.preset.value === value) return

  theme.setPreset(value)
  notifications.success('Tema aplicado.')
}

function selectMode(value: ThemeMode) {
  if (theme.mode.value === value) return

  theme.setMode(value)
  notifications.success('Modo de aparência atualizado.')
}
</script>

<template>
  <div class="space-y-7">
    <header>
      <p class="text-sm font-medium text-primary">Preferências</p>
      <h1 class="text-3xl font-bold tracking-tight">Configurações</h1>
      <p class="mt-1 text-sm text-muted-foreground">
        Personalize como o Almoxarifado aparece para você.
      </p>
    </header>

    <Card class="overflow-hidden">
      <Accordion
        class="w-full"
        type="multiple"
      >
        <AccordionItem class="px-5 sm:px-6" value="appearance">
          <AccordionTrigger class="gap-4 py-5 text-left hover:no-underline">
            <span class="flex min-w-0 items-center gap-3">
              <span class="grid size-10 shrink-0 place-items-center rounded-xl bg-primary/10 text-primary">
                <MonitorCog class="size-5"/>
              </span>
              <span>
                <span class="block font-semibold text-foreground">Modo de aparência</span>
                <span class="mt-0.5 block text-sm font-normal text-muted-foreground">
                  Escolha entre a interface clara e escura.
                </span>
              </span>
            </span>
          </AccordionTrigger>

          <AccordionContent class="pb-6">
            <div class="grid gap-3 sm:grid-cols-2">
              <Button
                :aria-pressed="theme.mode.value === 'light'"
                :variant="theme.mode.value === 'light' ? 'default' : 'outline'"
                class="h-auto justify-start gap-3 p-4"
                type="button"
                @click="selectMode('light')"
              >
                <Sun class="size-5 shrink-0"/>
                <span class="text-left">
                  <strong class="block">Claro</strong>
                  <small class="font-normal opacity-75">Mais luminoso e leve</small>
                </span>
              </Button>

              <Button
                :aria-pressed="theme.mode.value === 'dark'"
                :variant="theme.mode.value === 'dark' ? 'default' : 'outline'"
                class="h-auto justify-start gap-3 p-4"
                type="button"
                @click="selectMode('dark')"
              >
                <Moon class="size-5 shrink-0"/>
                <span class="text-left">
                  <strong class="block">Escuro</strong>
                  <small class="font-normal opacity-75">Confortável em pouca luz</small>
                </span>
              </Button>
            </div>
          </AccordionContent>
        </AccordionItem>

        <AccordionItem class="border-b-0 px-5 sm:px-6" value="themes">
          <AccordionTrigger class="gap-4 py-5 text-left hover:no-underline">
            <span class="flex min-w-0 items-center gap-3">
              <span class="grid size-10 shrink-0 place-items-center rounded-xl bg-primary/10 text-primary">
                <Palette class="size-5"/>
              </span>
              <span>
                <span class="block font-semibold text-foreground">Temas</span>
                <span class="mt-0.5 block text-sm font-normal text-muted-foreground">
                  Selecione uma paleta para toda a interface.
                </span>
              </span>
            </span>
          </AccordionTrigger>

          <AccordionContent class="pb-6">
            <div class="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
              <button
                v-for="option in themeOptions"
                :key="option.id"
                :aria-pressed="theme.preset.value === option.id"
                :class="theme.preset.value === option.id
                  ? 'border-primary ring-2 ring-primary/25'
                  : 'hover:border-primary/50 hover:bg-accent/40'"
                class="group relative rounded-xl border bg-background p-4 text-left transition focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                type="button"
                @click="selectPreset(option.id)"
              >
                <span
                  v-if="theme.preset.value === option.id"
                  class="absolute right-3 top-3 grid size-6 place-items-center rounded-full bg-primary text-primary-foreground"
                >
                  <Check class="size-3.5"/>
                </span>

                <span aria-hidden="true" class="mb-4 flex gap-1.5">
                  <span
                    v-for="color in option.colors"
                    :key="color"
                    :style="{backgroundColor: color}"
                    class="size-8 rounded-full border shadow-sm"
                  />
                </span>

                <strong class="block pr-7">{{ option.name }}</strong>
                <span class="mt-1 block pr-6 text-xs text-muted-foreground">
                  {{ option.description }}
                </span>
              </button>
            </div>
          </AccordionContent>
        </AccordionItem>
      </Accordion>
    </Card>
  </div>
</template>
