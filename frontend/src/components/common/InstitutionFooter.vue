<script setup lang="ts">
  import { computed } from 'vue'

  const props = withDefaults(defineProps<{
    mode?: 'hero' | 'app'
    theme?: 'light' | 'dark'
  }>(), {
    mode: 'app',
    theme: 'light',
  })

  const footerClasses = computed(() => [
    'institution-footer',
    `institution-footer--${props.mode}`,
    `institution-footer--${props.theme}`,
  ])

  const dividerColor = computed(() => (
    props.theme === 'dark' ? 'rgba(148, 163, 184, 0.35)' : 'rgba(15, 23, 42, 0.14)'
  ))
</script>

<template>
  <v-footer :class="footerClasses" role="contentinfo">
    <v-container class="py-0">
      <v-row align="center" class="px-0" justify="space-between">
        <v-col class="text-center text-sm-left" cols="12" sm="8">
          <span class="d-block institution-footer__title text-body-large font-weight-bold mb-2">
            Universidade Federal do Ceará - Campus Quixadá
          </span>
          <span class="d-block institution-footer__meta text-body-medium mb-2">
            Av. José de Freitas Queiroz, 5003 - Cedro, Quixadá - CE, 63902-580
          </span>
          <div class="d-flex align-center justify-center justify-sm-start flex-wrap institution-footer__meta text-body-medium">
            <span>Contato: (88) 3412-0919</span>
            <v-icon aria-hidden="true" class="mx-2" :color="theme === 'dark' ? 'grey-darken-1' : 'grey-lighten-1'" icon="mdi-circle-small" size="16" />
            <span>Site:</span>
            <v-btn
              aria-label="Acessar o site do Campus Quixadá em nova guia"
              class="px-1 py-0 h-auto text-none font-weight-bold text-body-medium"
              color="warning"
              href="https://www.quixada.ufc.br"
              :ripple="false"
              target="_blank"
              variant="plain"
            >
              www.quixada.ufc.br
            </v-btn>
          </div>
        </v-col>

        <v-col class="text-center text-sm-right mt-4 mt-sm-0" cols="12" sm="4">
          <span class="institution-footer__meta text-body-medium">
            &copy; {{ new Date().getFullYear() }} Almoxarifado UFC.<br>
            Desenvolvido para gestão inteligente.
          </span>
        </v-col>
      </v-row>
    </v-container>
  </v-footer>
</template>

<style scoped>
.institution-footer {
  position: relative;
  padding: 24px 0;
}

.institution-footer::before {
  content: '';
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.institution-footer--hero {
  background: transparent;
}

.institution-footer--hero.institution-footer--dark::before {
  background: linear-gradient(180deg, rgba(2, 6, 23, 0) 0%, rgba(2, 6, 23, 0.18) 100%);
}

.institution-footer--hero.institution-footer--light::before {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0) 0%, rgba(248, 250, 252, 0.3) 100%);
}

.institution-footer--app.institution-footer--dark {
  background:
    radial-gradient(circle at top left, rgba(245, 158, 11, 0.16), transparent 35%),
    linear-gradient(135deg, #020617 0%, #0f172a 42%, #111827 100%);
}

.institution-footer--app.institution-footer--light {
  background:
    radial-gradient(circle at top left, rgba(245, 158, 11, 0.22), transparent 34%),
    linear-gradient(135deg, #f8fafc 0%, #e2e8f0 48%, #ffffff 100%);
  border-top: 1px solid v-bind(dividerColor);
}

.institution-footer__title,
.institution-footer__meta {
  position: relative;
  z-index: 1;
}

.institution-footer--dark .institution-footer__title {
  color: #ffffff;
}

.institution-footer--dark .institution-footer__meta {
  color: rgba(226, 232, 240, 0.88);
}

.institution-footer--light .institution-footer__title {
  color: #0f172a;
}

.institution-footer--light .institution-footer__meta {
  color: rgba(51, 65, 85, 0.92);
}
</style>
