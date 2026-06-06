<script lang="ts" setup>
import {computed, onMounted} from 'vue'
import InstitutionFooter from '@/components/common/InstitutionFooter.vue'
import ParticleCanvas from '@/components/ui/ParticleCanvas.vue'
import {useThemePreference} from '@/composables/useThemePreference'
import {useAuthStore} from '@/stores/auth'

const {currentTheme, initializeTheme, toggleTheme} = useThemePreference()
const authStore = useAuthStore()

const heroTheme = computed(() => (
    currentTheme.value === 'dark'
        ? {
          sheetClass: 'landing-shell landing-shell--dark',
          cardClass: 'hero-panel hero-panel--dark',
          chipClass: 'hero-chip hero-chip--dark',
          eyebrowClass: 'hero-eyebrow hero-eyebrow--dark',
          titleClass: 'hero-title hero-title--dark',
          subtitleClass: 'hero-subtitle hero-subtitle--dark',
          statClass: 'hero-stat hero-stat--dark',
          particleFill: 'rgba(255, 255, 255, 0.45)',
          particleStroke: '255, 255, 255',
          accentLabel: 'Visão noturna',
          accentIcon: 'mdi-weather-night',
          brandClass: 'text-white',
          themeButtonClass: 'text-white',
          themeIcon: 'mdi-weather-night',
        }
        : {
          sheetClass: 'landing-shell landing-shell--light',
          cardClass: 'hero-panel hero-panel--light',
          chipClass: 'hero-chip hero-chip--light',
          eyebrowClass: 'hero-eyebrow hero-eyebrow--light',
          titleClass: 'hero-title hero-title--light',
          subtitleClass: 'hero-subtitle hero-subtitle--light',
          statClass: 'hero-stat hero-stat--light',
          particleFill: 'rgba(15, 23, 42, 0.28)',
          particleStroke: '15, 23, 42',
          accentLabel: 'Aurora acadêmica',
          accentIcon: 'mdi-white-balance-sunny',
          brandClass: 'text-black',
          themeButtonClass: 'text-black',
          themeIcon: 'mdi-weather-sunny',
        }
))

const featureCards = [
  {
    icon: 'mdi-package-variant-closed',
    title: 'Inventário',
    text: 'Controle rigoroso e atualizado de todos os itens cadastrados no almoxarifado em tempo real.',
  },
  {
    icon: 'mdi-hand-extended-outline',
    title: 'Empréstimos',
    text: 'Acompanhamento detalhado de retiradas, devoluções e situação pendente dos materiais.',
  },
  {
    icon: 'mdi-chart-timeline-variant-shimmer',
    title: 'Transparência',
    text: 'Centralização de operações em um único ambiente acessível a qualquer hora e em qualquer lugar.',
  },
]

onMounted(() => {
  initializeTheme()
})
</script>

<template>
  <v-sheet
      :class="heroTheme.sheetClass"
      aria-label="Página Inicial do Sistema de Almoxarifado"
      class="d-flex flex-column position-relative"
      role="region"
  >
    <ParticleCanvas
        :particle-fill="heroTheme.particleFill"
        :particle-stroke="heroTheme.particleStroke"
    />

    <v-toolbar aria-label="Cabeçalho da página inicial com informações institucionais e acesso ao sistema"
               class="position-relative pt-4"
               color="transparent"
               flat
               role="banner"
               style="z-index: 1; overflow: visible;"
    >
      <v-container class="d-flex justify-space-between align-center py-0">
        <div class="d-flex align-center">
          <v-img
              alt="Brasão da Universidade Federal do Ceará"
              class="mr-4"
              contain
              height="46"
              src="/brasao.png"
              width="40"
          />
          <div class="d-flex flex-column">
            <span :class="heroTheme.brandClass" class="text-title-medium text-sm-h6 font-weight-bold lh-1">Almoxarifado UFC</span>
            <span :class="currentTheme === 'dark' ? 'text-grey-lighten-1' : 'text-grey'"
                  class="text-body-small text-sm-body-2">Campus Quixadá</span>
          </div>
        </div>

        <v-btn
            :aria-label="currentTheme === 'dark' ? 'Mudar para tema claro' : 'Mudar para tema escuro'"
            :class="heroTheme.themeButtonClass"
            :icon="heroTheme.themeIcon"
            density="comfortable"
            variant="text"
            @click="toggleTheme"
        />
      </v-container>
    </v-toolbar>

    <v-container class="flex-grow-1 d-flex align-center justify-center position-relative px-4 py-12" role="main"
                 style="z-index: 1;">
      <div class="d-flex flex-column align-center text-center w-100" style="max-width: 1200px;">
        <div
            :class="heroTheme.titleClass"
            aria-level="1"
            class="mb-6 text-wrap w-100"
            role="heading"
        >
          Sistema de Gerenciamento de<br class="d-none d-sm-block">
          <span class="text-warning">Estoque e Empréstimos</span>
        </div>

        <div
            :class="heroTheme.subtitleClass"
            aria-level="2"
            class="mb-6 w-100 w-md-75 mx-auto"
            role="heading"
        >
          Uma plataforma moderna, 100% web, projetada para simplificar o controle de materiais, organizar pedidos e dar
          mais clareza à operação do campus.
        </div>

        <v-btn
            aria-label="Acessar o Sistema do Almoxarifado"
            class="text-none font-weight-bold px-6 rounded-pill text-grey-darken-4 mb-6"
            color="warning"
            elevation="4"
            :to="authStore.isAuthenticated ? '/dashboard' : '/auth/login'"
            variant="flat"
        >
          {{ authStore.isAuthenticated ? 'Acessar Dashboard' : 'Entrar no Sistema' }}
        </v-btn>

        <v-row class="w-100 justify-center">
          <v-col v-for="card in featureCards" :key="card.title" class="d-flex" cols="12" md="4">
            <v-card
                :class="heroTheme.cardClass"
                class="flex-grow-1 text-center pa-8 rounded-xl"
                elevation="0"
                variant="outlined"
            >
              <v-icon
                  :icon="card.icon"
                  aria-hidden="true"
                  class="mb-4"
                  color="warning"
                  size="36"
              />
              <div :class="currentTheme === 'dark' ? 'text-white' : 'text-grey-darken-4'" aria-level="3"
                   class="text-body-large font-weight-bold mb-3" role="heading">
                {{ card.title }}
              </div>
              <div :class="currentTheme === 'dark' ? 'text-grey-lighten-2' : 'text-grey-darken-1'"
                   class="text-body-medium text-wrap">
                {{ card.text }}
              </div>
            </v-card>
          </v-col>
        </v-row>
      </div>
    </v-container>

    <InstitutionFooter :theme="currentTheme" class="position-relative mt-auto" mode="hero" style="z-index: 1;"/>
  </v-sheet>
</template>

<style scoped>
.landing-shell {
  min-height: 100vh;
  overflow: hidden;
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
}

.landing-shell--dark {
  background-image: linear-gradient(135deg, rgba(2, 6, 23, 0.9), rgba(15, 23, 42, 0.76)),
  url('/ufc_panorama.jpeg');
}

.landing-shell--light {
  background-image: linear-gradient(135deg, rgba(248, 250, 252, 0.9), rgba(226, 232, 240, 0.7)),
  url('/ufc_panorama.jpeg');
}

.landing-shell::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 15% 20%, rgba(245, 158, 11, 0.24), transparent 26%),
  radial-gradient(circle at 85% 18%, rgba(14, 165, 233, 0.2), transparent 30%);
  pointer-events: none;
}


.hero-panel {
  backdrop-filter: blur(14px);
  border-width: 1px;
}

.hero-panel--dark {
  background: rgba(15, 23, 42, 0.24);
  border-color: rgba(255, 255, 255, 0.14);
}

.hero-panel--light {
  background: rgba(255, 255, 255, 0.42);
  border-color: rgba(255, 255, 255, 0.52);
  box-shadow: 0 20px 60px rgba(15, 23, 42, 0.12);
}

.hero-chip {
  font-weight: 700;
  letter-spacing: 0.02em;
}

.hero-chip--dark {
  background: rgba(15, 23, 42, 0.45);
  color: #f8fafc;
  border: 1px solid rgba(255, 255, 255, 0.16);
}

.hero-chip--light {
  background: rgba(255, 255, 255, 0.58);
  color: #0f172a;
  border: 1px solid rgba(255, 255, 255, 0.72);
}

.hero-eyebrow {
  font-weight: 700;
  letter-spacing: 0.04em;
}

.hero-eyebrow--dark {
  background: rgba(255, 255, 255, 0.08);
  color: #e2e8f0;
}

.hero-eyebrow--light {
  background: rgba(15, 23, 42, 0.08);
  color: #0f172a;
}

.hero-title {
  font-size: clamp(1.25rem, 3.5vw, 1.75rem);
  font-weight: 800;
  line-height: 1.3;
}

.hero-title--dark {
  color: #ffffff;
}

.hero-title--light {
  color: #0f172a;
}

.hero-subtitle {
  font-size: clamp(0.9rem, 1.25vw, 1rem);
  line-height: 1.6;
  max-width: 860px;
}

.hero-subtitle--dark {
  color: rgba(226, 232, 240, 0.92);
}

.hero-subtitle--light {
  color: rgba(15, 23, 42, 0.84);
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  width: 100%;
}

.hero-stat {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 18px 20px;
  border-radius: 20px;
  text-align: left;
  backdrop-filter: blur(14px);
}

.hero-stat strong {
  font-size: 1rem;
}

.hero-stat span {
  font-size: 0.95rem;
  line-height: 1.55;
}

.hero-stat--dark {
  background: rgba(15, 23, 42, 0.28);
  color: rgba(248, 250, 252, 0.96);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.hero-stat--light {
  background: rgba(255, 255, 255, 0.44);
  color: rgba(15, 23, 42, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.68);
}

@media (max-width: 959px) {
  .hero-stats {
    grid-template-columns: 1fr;
  }
}
</style>
