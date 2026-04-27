<script setup lang="ts">
export interface AppTableColumn {
  key: string
  title: string
  align?: 'start' | 'center' | 'end'
  sortable?: boolean
  width?: string
}

export interface AppTableProps {
  headers: AppTableColumn[]
  items: any[]
  loading?: boolean
  emptyText?: string
  itemKey: string
}

const props = withDefaults(defineProps<AppTableProps>(), {
  items: () => [],
  loading: false,
  emptyText: 'Nenhum registro encontrado.',
  itemKey: 'id',
})
</script>

<template>
  <v-table class="app-table border rounded-lg" hover>
    <thead>
      <tr>
        <th
          v-for="header in headers"
          :key="header.key"
          :class="['text-' + (header.align || 'start'), 'font-weight-bold text-medium-emphasis text-caption text-uppercase']"
          :style="header.width ? `width: ${header.width}` : undefined"
        >
          {{ header.title }}
        </th>
      </tr>
    </thead>

    <tbody>
      <!-- Loading -->
      <tr v-if="loading">
        <td :colspan="headers.length" class="text-center py-10">
          <v-progress-circular color="primary" indeterminate size="32" />
        </td>
      </tr>

      <!-- Vazio -->
      <tr v-else-if="items.length === 0">
        <td :colspan="headers.length" class="text-center py-10 text-medium-emphasis">
          {{ emptyText }}
        </td>
      </tr>

      <!-- Linhas -->
      <tr v-for="(item, index) in items" v-else :key="item[itemKey] ?? index">
        <td
          v-for="header in headers"
          :key="header.key"
          :class="['text-' + (header.align || 'start')]"
        >
          <slot :item="item" :name="`item.${header.key}`">
            {{ item[header.key] ?? '—' }}
          </slot>
        </td>
      </tr>
    </tbody>

    <!-- Slot de rodapé (paginação, totais, etc.) -->
    <tfoot v-if="$slots.footer">
      <tr>
        <td :colspan="headers.length" class="pa-0">
          <slot name="footer" />
        </td>
      </tr>
    </tfoot>
  </v-table>
</template>

<style scoped>
.app-table {
  width: 100%;
}

.app-table :deep(thead th) {
  letter-spacing: 0.04em;
  white-space: nowrap;
}

.app-table :deep(tbody tr:last-child td) {
  border-bottom: none;
}
</style>