<script lang="ts" setup>
import type {DialogDescriptionProps} from "reka-ui"
import {DialogDescription, useForwardProps} from "reka-ui"
import type {HTMLAttributes} from "vue"
import {reactiveOmit} from "@vueuse/core"
import {cn} from "@/lib/utils"

const props = defineProps<DialogDescriptionProps & { class?: HTMLAttributes["class"] }>()

const delegatedProps = reactiveOmit(props, "class")

const forwardedProps = useForwardProps(delegatedProps)
</script>

<template>
  <DialogDescription
      :class="cn('text-sm text-muted-foreground', props.class)"
      v-bind="forwardedProps"
  >
    <slot/>
  </DialogDescription>
</template>
