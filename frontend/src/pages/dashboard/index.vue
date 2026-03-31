<script lang="ts" setup>
  import AppAlert from '@/components/ui/AppAlert.vue'
  import AppButton from '@/components/ui/AppButton.vue'
  import AppCard from '@/components/ui/AppCard.vue'
  import AppPage from '@/components/ui/AppPage.vue'

  const user = {
    name: 'Maria Silva',
  }

  const quickActions = [
    { label: 'Ver Meus Empréstimos', icon: 'mdi-clipboard-text-outline', to: '/emprestimos' },
    { label: 'Emitir Nada Consta', icon: 'mdi-file-document-outline', to: '/nada-consta' },
  ]

  const importantAlerts = [
    {
      title: 'Prazo de Devolução',
      description: 'O prazo padrão e de 30 dias apos a aprovação do empréstimo',
      icon: 'mdi-calendar-text-outline',
      tone: 'info',
    },
    {
      title: 'Atenção aos Prazos',
      description: 'Devoluções em atraso podem resultar em bloqueio de novos empréstimos',
      icon: 'mdi-alert',
      tone: 'warning',
    },
    {
      title: 'Dicas',
      description: 'Verifique a disponibilidade antes de solicitar e cuide bem dos equipamentos',
      icon: 'mdi-check-bold',
      tone: 'success',
    },
  ] as const
</script>

<template>
  <AppPage subtitle="Acompanhe seus empréstimos e solicite novos itens" :title="'Bem vindo(a), ' + user.name">
    <v-row class="mb-6" dense>
      <v-col cols="12" md="4">
        <AppCard
          card-class="fill-height border-info"
          color="info-light"
          content-class="pa-4 d-flex flex-column"
          header-class="mb-6"
          title="Empréstimos Ativos"
        >
          <template #header>
            <div class="d-flex justify-space-between align-start w-100">
              <div class="text-title-medium font-weight-medium text-info">Empréstimos Ativos</div>
              <v-avatar color="info" rounded="lg" size="32">
                <v-icon color="white" icon="mdi-trending-up" size="20" />
              </v-avatar>
            </div>
          </template>

          <div class="mt-auto">
            <div class="text-headline-medium font-weight-bold text-info mb-1">0</div>
            <div class="text-body-small text-info font-weight-medium text-medium-emphasis">Em andamento</div>
          </div>
        </AppCard>
      </v-col>

      <v-col cols="12" md="4">
        <AppCard
          card-class="fill-height border-warning"
          color="warning-light"
          content-class="pa-4 d-flex flex-column"
          header-class="mb-6"
          title="Aguardando Aprovação"
        >
          <template #header>
            <div class="d-flex justify-space-between align-start w-100">
              <div class="text-title-medium font-weight-medium text-warning">Aguardando Aprovação</div>
              <v-avatar color="warning" rounded="lg" size="32">
                <v-icon color="white" icon="mdi-clock-outline" size="20" />
              </v-avatar>
            </div>
          </template>

          <div class="mt-auto">
            <div class="text-headline-medium font-weight-bold text-warning mb-1">1</div>
            <div class="text-body-small text-warning font-weight-medium text-medium-emphasis">Solicitações pendentes</div>
          </div>
        </AppCard>
      </v-col>

      <v-col cols="12" md="4">
        <AppCard
          card-class="fill-height border-success"
          color="success-light"
          content-class="pa-4 d-flex flex-column"
          header-class="mb-6"
          title="Itens Disponíveis"
        >
          <template #header>
            <div class="d-flex justify-space-between align-start w-100">
              <div class="text-title-medium font-weight-medium text-success">Itens Disponíveis</div>
              <v-avatar color="success" rounded="lg" size="32">
                <v-icon color="white" icon="mdi-package-variant-closed" size="20" />
              </v-avatar>
            </div>
          </template>

          <div class="mt-auto">
            <div class="text-headline-medium font-weight-bold text-success mb-1">8</div>
            <div class="text-body-small text-success font-weight-medium text-medium-emphasis">Prontas para empréstimo</div>
          </div>
        </AppCard>
      </v-col>
    </v-row>

    <v-row class="mb-6" dense>
      <!-- Quick Start -->
      <v-col cols="12" md="6">
        <AppCard
          card-class="fill-height"
          content-class="pa-4 d-flex flex-column"
          header-class="mb-4"
          subtitle="Comece por aqui"
          title="Início Rápido"
        >
          <div class="pt-2 d-flex flex-column ga-3">
            <AppButton
              class="text-none justify-start px-4"
              color="primary"
              elevation="0"
              prepend-icon="mdi-plus"
              rounded="md"
              size="large"
              to="/emprestimos"
            >
              Solicitar Novo Empréstimo
            </AppButton>

            <AppButton
              v-for="action in quickActions"
              :key="action.label"
              block
              class="text-none justify-start px-4"
              color="grey-darken-1"
              elevation="0"
              :prepend-icon="action.icon"
              rounded="md"
              size="large"
              :to="action.to"
            >
              <span class="text-high-emphasis">{{ action.label }}</span>
            </AppButton>
          </div>
        </AppCard>
      </v-col>

      <!-- Important Info -->
      <v-col cols="12" md="6">
        <AppCard
          card-class="fill-height"
          content-class="pa-4 d-flex flex-column"
          header-class="mb-4"
          title="Informações Importantes"
        >
          <div class="pt-2 d-flex flex-column ga-4">
            <AppAlert
              v-for="alert in importantAlerts"
              :key="alert.title"
              :description="alert.description"
              :icon="alert.icon"
              :title="alert.title"
              :tone="alert.tone"
            />
          </div>
        </AppCard>
      </v-col>
    </v-row>
  </AppPage>
</template>
