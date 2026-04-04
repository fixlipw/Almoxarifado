<script setup lang="ts">

</script>

<template>
  <v-container class="d-flex flex-column align-center justify-center" style="height: 100vh">
    <v-card class="pa-6" max-width="400">
      <v-card-title class="text-h5">Login</v-card-title>
      <v-card-text>
        <v-form>
          <v-text-field label="Email" type="email" required />
          <v-text-field label="Senha" type="password" required />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-btn color="primary" block>Entrar</v-btn>
      </v-card-actions>
    </v-card>
  </v-container>
</template>

<style scoped>

</style>