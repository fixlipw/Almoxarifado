<script lang="ts" setup>
import {computed, onMounted, onUnmounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ArrowLeft, Download, FileSpreadsheet, Loader2, RefreshCw, TriangleAlert} from 'lucide-vue-next'
import PDFViewer from '@embedpdf/vue-pdf-viewer'
import {Button} from '@/components/ui/button'
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from '@/components/ui/card'
import {apiFetchBlob} from '@/services/api'
import {getReportUrl} from '@/services/relatorios'

const route = useRoute()
const router = useRouter()
const key = computed(() => String(route.query.key ?? ''))
const format = computed(() => route.query.format === 'excel' ? 'excel' : 'pdf')
const loading = ref(true)
const error = ref('')
const blobUrl = ref('')

async function load() {
  if (!key.value) return router.replace('/relatorios')
  loading.value = true
  error.value = ''
  try {
    const blob = await apiFetchBlob(getReportUrl(key.value, format.value))
    if (blobUrl.value) URL.revokeObjectURL(blobUrl.value)
    blobUrl.value = URL.createObjectURL(blob)
    if (format.value === 'excel') download()
  } catch {
    error.value = 'Não foi possível gerar o relatório. Verifique sua conexão e permissões.'
  } finally {
    loading.value = false
  }
}

function download() {
  if (!blobUrl.value) return
  const link = document.createElement('a')
  link.href = blobUrl.value
  link.download = `relatorio-${key.value}.${format.value === 'excel' ? 'xlsx' : 'pdf'}`
  link.rel = 'noopener'
  link.click()
}

onMounted(load)
onUnmounted(() => {
  if (blobUrl.value) URL.revokeObjectURL(blobUrl.value)
})
</script>
<template>
  <div class="flex min-h-screen flex-col bg-muted/30">
    <header class="flex h-16 items-center gap-3 border-b bg-background px-4">
      <Button aria-label="Voltar" size="icon" variant="ghost" @click="router.push('/relatorios')">
        <ArrowLeft/>
      </Button>
      <div><p class="text-sm font-medium">Relatório de {{ key }}</p>
        <p class="text-xs text-muted-foreground">{{ format.toUpperCase() }}</p></div>
      <Button v-if="blobUrl" class="ml-auto" variant="outline" @click="download">
        <Download/>
        Baixar
      </Button>
    </header>
    <main class="flex flex-1 p-4">
      <div v-if="loading" class="grid flex-1 place-items-center text-center">
        <div>
          <Loader2 class="mx-auto mb-3 size-8 animate-spin text-primary"/>
          <p class="font-medium">Gerando relatório...</p></div>
      </div>
      <Card v-else-if="error" class="m-auto max-w-md">
        <CardHeader>
          <TriangleAlert class="mb-2 size-8 text-destructive"/>
          <CardTitle>Falha ao gerar relatório</CardTitle>
          <CardDescription>{{ error }}</CardDescription>
        </CardHeader>
        <CardContent>
          <Button @click="load">
            <RefreshCw/>
            Tentar novamente
          </Button>
        </CardContent>
      </Card>
      <div v-else-if="format === 'pdf'"
           class="min-h-[calc(100vh-6rem)] flex-1 overflow-hidden rounded-lg border bg-background">
        <PDFViewer :config="{ src: blobUrl, disabledCategories: ['redaction'], theme: { preference: 'light' } }"
                   class="h-full w-full"/>
      </div>
      <Card v-else class="m-auto max-w-md text-center">
        <CardHeader>
          <FileSpreadsheet class="mx-auto mb-2 size-10 text-emerald-600"/>
          <CardTitle>Planilha gerada</CardTitle>
          <CardDescription>O download foi iniciado automaticamente.</CardDescription>
        </CardHeader>
        <CardContent>
          <Button @click="download">
            <Download/>
            Baixar novamente
          </Button>
        </CardContent>
      </Card>
    </main>
  </div>
</template>
