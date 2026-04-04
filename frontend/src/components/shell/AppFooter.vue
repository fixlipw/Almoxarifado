<template>
  <v-footer border class="app-footer py-4" color="surface" role="contentinfo">
    <v-container class="py-0">
      <v-row align="center" justify="space-between">
        <v-col class="text-center text-sm-left" cols="12" sm="8">
          <div class="text-body-medium font-weight-bold mb-1">
            Universidade Federal do Ceará - Campus Quixadá
          </div>
          <div class="text-body-small text-medium-emphasis mb-1">
            Av. José de Freitas Queiroz, 5003 - Cedro, Quixadá - CE, 63902-580
          </div>
          <div class="d-flex align-center justify-center justify-sm-start flex-wrap text-body-small text-medium-emphasis">
            <span>Contato: (88) 3412-0919</span>
            <v-icon aria-hidden="true" class="mx-1" color="medium-emphasis" icon="mdi-circle-small" size="14" />
            <span>Site:</span>
            <v-btn
              aria-label="Acessar o site do Campus Quixadá em nova guia"
              class="px-1 py-0 h-auto text-none text-body-small font-weight-medium"
              color="primary"
              href="https://www.quixada.ufc.br"
              :ripple="false"
              target="_blank"
              variant="plain"
            >
              www.quixada.ufc.br
            </v-btn>
          </div>
        </v-col>

        <v-col class="text-center text-sm-right mt-3 mt-sm-0" cols="12" sm="4">
          <div class="text-body-small text-medium-emphasis">
            &copy; {{ new Date().getFullYear() }} Almoxarifado UFC.<br>
            Desenvolvido para gestão inteligente.
          </div>
        </v-col>
      </v-row>
    </v-container>
  </v-footer>
</template>

<style scoped>
.app-footer {
  flex: none;
}
</style>
