<template>
  <v-container class="d-flex flex-column align-center justify-center text-center" fill-height fluid>
    <h1 class="text-h1 font-weight-bold text-primary mb-4">404</h1>
    <h2 class="text-h4 mb-6">Página não encontrada</h2>
    <p class="text-body-1 mb-8">A página que você está procurando não existe ou foi movida.</p>
    <AppButton color="primary" size="large" to="/dashboard">
      Voltar para o Início
    </AppButton>
  </v-container>
</template>

<route lang="yaml">
meta:
  layout: blank
</route>

<script setup lang="ts">
  import AppButton from '@/components/ui/AppButton.vue'
</script>

<style scoped>
.v-container {
  height: 100vh;
}
</style>
