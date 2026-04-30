<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getReportUrl } from '@/services/relatorios'
import { apiFetchBlob } from '@/services/api'

const route = useRoute()
const router = useRouter()

const key = computed(() => String(route.query.key ?? ''))
const format = computed(() => (route.query.format === 'excel' ? 'excel' : 'pdf'))

const isLoading = ref(true)
const blobUrl = ref<string | null>(null)
const error = ref<string | null>(null)

if (!key.value) {
  router.replace('/relatorios')
}

const reportUrl = computed(() => {
  return blobUrl.value || ''
})

async function loadReport() {
  isLoading.value = true
  error.value = null
  
  try {
    const url = getReportUrl(key.value, format.value)
    const blob = await apiFetchBlob(url)
    
    if (blobUrl.value) {
      URL.revokeObjectURL(blobUrl.value)
    }
    
    blobUrl.value = URL.createObjectURL(blob)
    
    if (format.value === 'excel') {
      const a = document.createElement('a')
      a.href = blobUrl.value
      a.download = `relatorio-${key.value}.xlsx`
      a.click()
    }
  } catch (err) {
    console.error('Erro ao carregar relatório:', err)
    error.value = 'Não foi possível carregar o relatório. Verifique sua conexão ou permissões.'
  } finally {
    isLoading.value = false
  }
}

onMounted(loadReport)

onUnmounted(() => {
  if (blobUrl.value) {
    URL.revokeObjectURL(blobUrl.value)
  }
})
</script>

<template>
  <div style="height:100vh; display:flex; flex-direction:column;">
    <div style="flex:1; overflow: hidden; position: relative;">
      <div v-if="isLoading" class="d-flex flex-column align-center justify-center" style="height: 100%;">
        <v-progress-circular indeterminate color="primary" size="64" />
        <p class="mt-4 text-body-medium">Gerando relatório...</p>
      </div>

      <div v-else-if="error" class="d-flex flex-column align-center justify-center" style="height: 100%; padding: 32px; text-align: center;">
        <v-icon color="error" icon="mdi-alert-circle-outline" size="64" />
        <p class="text-h6 mt-4">{{ error }}</p>
        <v-btn class="mt-6" color="primary" @click="loadReport">Tentar novamente</v-btn>
      </div>

      <template v-else>
        <template v-if="format === 'pdf' && blobUrl">
          <iframe
            :src="reportUrl"
            height="100%"
            width="100%"
          />
        </template>
        <template v-else-if="format === 'excel'">
          <div class="d-flex flex-column align-center justify-center" style="height: 100%; padding: 16px;">
            <v-icon color="success" icon="mdi-file-excel" size="64" />
            <p class="text-h6 mt-4">Relatório Excel Gerado</p>
            <p class="text-body-medium text-medium-emphasis mb-6">O download deve ter iniciado automaticamente.</p>
            <v-btn :href="blobUrl!" :download="`relatorio-${key}.xlsx`" color="success" prepend-icon="mdi-download" variant="flat">
              Baixar novamente
            </v-btn>
          </div>
        </template>
      </template>
    </div>
  </div>
</template>


