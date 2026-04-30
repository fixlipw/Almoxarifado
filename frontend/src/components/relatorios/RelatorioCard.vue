<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'

const props = defineProps({
  title: { type: String, required: true },
  subtitle: { type: String, required: false, default: '' },
  icon: { type: String, required: false, default: 'mdi-file-document-outline' },
  keyName: { type: String, required: true },
  avatarColor: { type: String, required: false, default: 'info' },
})

const emit = defineEmits<{
  (e: 'download', payload: { key: string; format: 'pdf' | 'excel'; url: string }): void
}>()

function download(format: 'pdf' | 'excel') {
  const viewerUrl = `/relatorios/visualizar?key=${encodeURIComponent(props.keyName)}&format=${format}`
  // open viewer route in a new tab so the app can render the PDF in a full page
  window.open(viewerUrl, '_blank')
  emit('download', { key: props.keyName, format, url: viewerUrl })
}
</script>

<template>
  <v-card border class="mb-4" rounded="lg" variant="flat">
    <div class="pa-5">
      <div class="d-flex justify-space-between align-start w-100 mb-4">
        <div class="flex-grow-1">
          <div class="text-title-medium font-weight-medium">{{ title }}</div>
          <div v-if="subtitle" class="text-body-medium text-grey-darken-1">{{ subtitle }}</div>
        </div>

        <v-avatar :color="avatarColor" rounded="lg" size="40" variant="tonal">
          <v-icon color="white" :icon="icon" size="20" />
        </v-avatar>
      </div>

      <div class="d-flex justify-end mt-4 ga-2">
        <AppButton class="text-none" variant="flat" prepend-icon="mdi-download" @click.prevent="download('pdf')">PDF</AppButton>
        <AppButton class="text-none" variant="flat" prepend-icon="mdi-file-excel" @click.prevent="download('excel')">Excel</AppButton>
      </div>
    </div>
  </v-card>
</template>


