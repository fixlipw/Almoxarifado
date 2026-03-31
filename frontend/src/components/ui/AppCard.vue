<script setup lang="ts">
  interface CardProps {
    title?: string
    subtitle?: string
    cardClass?: string
    contentClass?: string
    headerClass?: string
    color?: string
  }

  withDefaults(defineProps<CardProps>(), {
    title: '',
    subtitle: '',
    cardClass: '',
    contentClass: '',
    headerClass: '',
  })
</script>

<template>
  <v-card
    border
    class="mb-4"
    :class="cardClass"
    :color="color"
    rounded="lg"
    variant="flat"
  >
    <div class="pa-5" :class="contentClass">
      <div v-if="$slots.header || title || subtitle || $slots.button" class="d-flex align-start" :class="headerClass">
        <div class="flex-grow-1">
          <slot name="header">
            <div>
              <div class="text-headline-small font-weight-bold">
                {{ title }}
              </div>

              <div v-if="subtitle" class="text-body-medium text-grey-darken-1">
                {{ subtitle }}
              </div>
            </div>
          </slot>
        </div>

        <div v-if="$slots.button" class="d-flex justify-end align-self-center ml-3 ga-2">
          <slot name="button" />
        </div>
      </div>

      <slot />

      <div v-if="$slots.actions" class="d-flex justify-end align-self-end mt-4 ga-2">
        <slot name="actions" />
      </div>
    </div>
  </v-card>
</template>
