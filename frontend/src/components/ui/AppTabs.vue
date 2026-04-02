<script setup lang="ts">
  import type {TabItem} from "@/components/ui/types.ts";

  import { computed, useSlots } from 'vue'

  const props = withDefaults(defineProps<{
    modelValue: string | number
    items: TabItem[]
    showContent?: boolean
  }>(), {
    showContent: undefined,
  })

  const emit = defineEmits<{
    'update:modelValue': [value: string | number]
  }>()

  const slots = useSlots()

  const tab = computed({
    get: () => props.modelValue,
    set: (value: string | number) => emit('update:modelValue', value),
  })

  const hasPanelSlots = computed(() => {
    if (slots.panel) {
      return true
    }

    return props.items.some(item => Boolean(slots[`panel-${String(item.value)}`]))
  })

  const shouldRenderContent = computed(() => {
    return props.showContent ?? hasPanelSlots.value
  })
</script>

<template>
  <v-sheet border rounded="lg">
    <v-tabs v-model="tab" color="primary" slider-transition="fade" variant="text">
      <v-tab
        v-for="item in items"
        :key="item.value"
        :ripple="false"
        rounded="lg"
        :value="item.value"
      >
        <span>{{ item.label }}</span>
        <v-chip
          v-if="item.badge !== undefined"
          class="ml-2"
          color="warning"
          label
          size="x-small"
          variant="flat"
        >
          {{ item.badge }}
        </v-chip>
      </v-tab>
    </v-tabs>

    <v-divider v-if="shouldRenderContent" />

    <v-tabs-window v-if="shouldRenderContent" v-model="tab">
      <v-tabs-window-item
        v-for="item in items"
        :key="`panel-${item.value}`"
        :value="item.value"
      >
        <slot :item="item" :name="`panel-${String(item.value)}`" :value="item.value">
          <slot :item="item" name="panel" :value="item.value" />
        </slot>
      </v-tabs-window-item>
    </v-tabs-window>
  </v-sheet>
</template>
