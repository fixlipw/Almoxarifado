<template>
  <v-container class="d-flex align-center justify-center" fill-height fluid>
    <v-card class="pa-4" width="400">
      <v-card-title class="text-center text-headline-medium">Login</v-card-title>
      <v-card-text>
        <v-form @submit.prevent>
          <v-text-field label="E-mail" required type="email" />
          <v-text-field label="Senha" required type="password" />
          <AppButton block class="mt-4" color="primary" type="submit">Entrar</AppButton>
        </v-form>
      </v-card-text>
    </v-card>
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
