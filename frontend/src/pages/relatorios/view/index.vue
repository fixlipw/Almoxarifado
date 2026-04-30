<script lang="ts" setup>
import {computed, onMounted, onUnmounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {getReportUrl} from '@/services/relatorios'
import {apiFetchBlob} from '@/services/api'
import PDFViewer from "@embedpdf/vue-pdf-viewer";

const route = useRoute()
const router = useRouter()

const key = computed(() => String(route.query.key ?? ''))
const format = computed(() => (route.query.format === 'excel' ? 'excel' : 'pdf'))

const localePortuguese = {
  code: 'pt-BR',
  name: 'Português (Brasil)',
  translations: {
    document: {
      open: 'Abrir documento',
      close: 'Fechar documento',
      print: 'Imprimir',
      protect: 'Proteger documento',
      export: 'Fazer download',
      fullscreen: 'Tela cheia',
    },
    history: {
      undo: 'Desfazer',
      redo: 'Refazer',
    },
    panel: {
      sidebar: 'Barra lateral',
      search: 'Pesquisar',
      comment: 'Comentários',
      annotationStyle: 'Estilos de anotação',
    },
    mode: {
      view: 'Visualização',
      annotate: 'Anotação',
      shapes: 'Formas',
      insert: 'Inserção',
      form: 'Formulários',
      redact: 'Redação',
    },
    pan: {
      toggle: 'Alternar para ferramenta de movimentação',
    },
    pointer: {
      toggle: 'Alternar para ferramenta de seleção',
    },
    page: {
      settings: 'Configurações de página',
      spreadMode: 'Modo de exibição',
        single: 'Página única',
        twoOdd: 'Duas páginas (início ímpar)',
        twoEven: 'Duas páginas (início par)',
      scrollLayout: 'Layout de rolagem',
        vertical: 'Vertical',
        horizontal: 'Horizontal',
      rotation: 'Orientação da página',
      previous: 'Página anterior',
      next: 'Próxima página',
    },
    rotate: {
      clockwise: 'Girar no sentido horário',
      counterclockwise: 'Girar no sentido anti-horário',
    },
    search: {
      placeholder: 'Pesquisar no documento',
      caseSensitive: 'Diferenciar maiúsculas de minúsculas',
      wholeWord: 'Palavras inteiras',
    },
    zoom: {
      in: 'Ampliar',
      out: 'Reduzir',
      fitPage: 'Ajustar à página',
      fitWidth: 'Ajustar à largura',
      marquee: 'Zoom de seleção',
    },
    comments: {
      emptyState: 'Nenhum comentário',
      add: 'Adicionar comentário',
      delete: 'Excluir comentário',
      edit: 'Editar comentário',
      page: 'Página',
      commentCount: '{count} comentário(s)',
    },
    capture: {
      screenshot: 'Capturar tela',
      dragTip: 'Arraste para selecionar a área de captura',
      title: 'Captura de tela',
      cancel: 'Cancelar',
      download: 'Fazer download',
    },
    stamp: {
      createFromSelected: 'Criar carimbo a partir da seleção',
    },
    blendMode: {
      normal: 'Normal',
      multiply: 'Multiplicação',
      screen: 'Tela',
      overlay: 'Sobreposição',
      darken: 'Escurecer',
      lighten: 'Clarear',
      colorDodge: 'Brilho de cor',
      colorBurn: 'Queima de cor',
      hardLight: 'Luz forte',
      softLight: 'Luz suave',
      difference: 'Diferença',
      exclusion: 'Exclusão',
      hue: 'Matiz',
      saturation: 'Saturação',
      color: 'Cor',
      luminosity: 'Luminosidade',
    },
    signature: {
      title: 'Assinatura',
      type: 'Tipo de assinatura',
      draw: 'Desenhar',
      typeText: 'Digitar',
      upload: 'Enviar imagem',
    },
    annotation: {
      defaults: 'Padrões',
      selectAnnotation: 'Selecionar anotação',
      comment: 'Comentário',
      addLink: 'Adicionar link',
      style: 'Estilo',
      deleteSelected: 'Excluir',
      highlight: 'Realçar',
      underline: 'Sublinhar',
      strikeout: 'Tachado',
      squiggly: 'Sublinhado ondulado',
      ink: 'Desenho à mão livre',
      inkHighlighter: 'Marcador de texto',
      text: 'Texto',
      insertText: 'Inserir texto',
      replaceText: 'Substituir texto',
      callout: 'Balão de fala',
      rectangle: 'Retângulo',
      fontSize: 'Tamanho da fonte',
      fontFamily: 'Família da fonte',
      fontColor: 'Cor da fonte',
      strokeColor: 'Cor da borda',
      strockeWidth: 'Largura da borda',
      fillColor: 'Cor de preenchimento',
      circle: 'Círculo',
      line: 'Linha',
      arrow: 'Seta',
      polygon: 'Polígono',
      polyline: 'Polilinha',
      group: 'Agrupar',
      ungroup: 'Desagrupar',
      styles: 'Estilos',
      opacity: 'Opacidade',
      blendMode: 'Modo de mesclagem',
    },
    insert: {
      rubberStamp: 'Carimbo',
      image: 'Imagem',
    },
    form: {
      textfield: 'Campo de texto',
      checkbox: 'Caixa de seleção',
      radio: 'Botão de opção',
      select: 'Lista suspensa',
      listbox: 'Caixa de listagem',
    }
  }
}

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
        <v-progress-circular color="primary" indeterminate size="64"/>
        <p class="mt-4 text-body-medium">Gerando relatório...</p>
      </div>

      <div v-else-if="error" class="d-flex flex-column align-center justify-center"
           style="height: 100%; padding: 32px; text-align: center;">
        <v-icon color="error" icon="mdi-alert-circle-outline" size="64"/>
        <p class="text-h6 mt-4">{{ error }}</p>
        <v-btn class="mt-6" color="primary" @click="loadReport">Tentar novamente</v-btn>
      </div>

      <template v-else>
        <template v-if="format === 'pdf' && blobUrl">
          <div style="height: 100vh;">
            <PDFViewer
              :config="{
                src: reportUrl,
                disabledCategories: ['redaction'],
                i18n: {
                  defaultLocale: 'pt-BR',
                  locales: [localePortuguese],
                },
                theme: { preference: 'light' }
              }"
              :style="{ width: '100%', height: '100%' }"
            />
          </div>
        </template>
        <template v-else-if="format === 'excel'">
          <div class="d-flex flex-column align-center justify-center" style="height: 100%; padding: 16px;">
            <v-icon color="success" icon="mdi-file-excel" size="64"/>
            <p class="text-h6 mt-4">Relatório Excel Gerado</p>
            <p class="text-body-medium text-medium-emphasis mb-6">O download deve ter iniciado automaticamente.</p>
            <v-btn :download="`relatorio-${key}.xlsx`" :href="blobUrl!" color="success" prepend-icon="mdi-download"
                   variant="flat">
              Baixar novamente
            </v-btn>
          </div>
        </template>
      </template>
    </div>
  </div>
</template>


