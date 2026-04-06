<script setup lang="ts">
  import type {AppCardProps} from "@/components/ui/types.ts";

  withDefaults(defineProps<AppCardProps>(), {
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
      <v-row v-if="$slots.header || title || subtitle || $slots.button" :class="headerClass" align="start">
        <v-col cols="12" sm="" class="flex-grow-1">
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
        </v-col>

        <v-col v-if="$slots.button" cols="12" sm="auto" class="d-flex justify-center justify-sm-end align-center">
          <slot name="button" />
        </v-col>
      </v-row>

      <slot />

      <div v-if="$slots.actions" class="d-flex justify-end align-self-end mt-4 ga-2">
        <slot name="actions" />
      </div>
    </div>
  </v-card>
</template>
