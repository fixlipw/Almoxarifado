<script setup lang="ts">
  import { onMounted, onUnmounted, ref } from 'vue'

  const canvasRef = ref<HTMLCanvasElement | null>(null)

  onMounted(() => {
    const canvas = canvasRef.value
    if (!canvas) return
    const ctx = canvas.getContext('2d')
    if (!ctx) return

    let particles: any[] = []
    let animationFrameId: number | null = null

    const PARTICLE_DENSITY = 12_000
    const MAX_PARTICLES = 300
    const BASE_CONNECT_DISTANCE = 120

    const getSize = () => {
      const w = Math.max(1, canvas.clientWidth)
      const h = Math.max(1, canvas.clientHeight)
      return { w, h }
    }

    const resize = () => {
      if (animationFrameId != null) {
        cancelAnimationFrame(animationFrameId)
        animationFrameId = null
      }

      const dpr = Math.max(1, window.devicePixelRatio || 1)
      const { w, h } = getSize()

      canvas.width = Math.floor(w * dpr)
      canvas.height = Math.floor(h * dpr)
      canvas.style.width = `${w}px`
      canvas.style.height = `${h}px`

      ctx.setTransform(dpr, 0, 0, dpr, 0, 0)

      initParticles()
      drawParticles()
    }

    const initParticles = () => {
      particles = []
      const { w, h } = getSize()
      const rawNum = Math.floor((w * h) / PARTICLE_DENSITY)
      const numParticles = Math.max(10, Math.min(rawNum, MAX_PARTICLES))

      for (let i = 0; i < numParticles; i++) {
        particles.push({
          x: Math.random() * w,
          y: Math.random() * h,
          vx: (Math.random() - 0.5) * 0.6,
          vy: (Math.random() - 0.5) * 0.6,
          radius: Math.random() * 1.5 + 0.5,
        })
      }
    }

    const drawParticles = () => {
      const { w, h } = getSize()
      ctx.clearRect(0, 0, w, h)
      ctx.fillStyle = 'rgba(255, 255, 255, 0.4)'

      const connectDistance = Math.max(60, Math.min(BASE_CONNECT_DISTANCE, Math.sqrt(w * h) / 8))

      for (let i = 0; i < particles.length; i++) {
        const p = particles[i]
        p.x += p.vx
        p.y += p.vy

        if (p.x < 0) {
          p.x = 0
          p.vx *= -1
        }
        if (p.x > w) {
          p.x = w
          p.vx *= -1
        }
        if (p.y < 0) {
          p.y = 0
          p.vy *= -1
        }
        if (p.y > h) {
          p.y = h
          p.vy *= -1
        }

        ctx.beginPath()
        ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2)
        ctx.fill()

        for (let j = i + 1; j < particles.length; j++) {
          const p2 = particles[j]
          const dx = p.x - p2.x
          const dy = p.y - p2.y
          const dist = Math.hypot(dx, dy)

          if (dist < connectDistance) {
            const alpha = Math.max(0, 0.12 * (1 - dist / connectDistance))
            if (alpha <= 0) continue
            ctx.beginPath()
            ctx.strokeStyle = `rgba(255, 255, 255, ${alpha})`
            ctx.moveTo(p.x, p.y)
            ctx.lineTo(p2.x, p2.y)
            ctx.stroke()
          }
        }
      }

      animationFrameId = requestAnimationFrame(drawParticles)
    }

    window.addEventListener('resize', resize)
    const ro = new ResizeObserver(() => resize())
    ro.observe(canvas)

    resize()

    onUnmounted(() => {
      window.removeEventListener('resize', resize)
      ro.disconnect()
      if (animationFrameId != null) cancelAnimationFrame(animationFrameId)
    })
  })
</script>

<template>
  <v-sheet
    aria-label="Página Inicial do Sistema de Almoxarifado"
    class="d-flex flex-column position-relative"
    role="region"
    style="min-height: 100vh; background: linear-gradient(rgba(0, 0, 0, 0.75), rgba(0, 0, 0, 0.95)), url('/ufc_panorama.jpeg') no-repeat center center fixed; background-size: cover;"
  >
    <canvas
      ref="canvasRef"
      aria-hidden="true"
      class="position-absolute top-0 left-0 w-100 h-100"
      style="pointer-events: none; z-index: 0;"
    />

    <v-toolbar
      class="position-relative pt-4"
      color="transparent"
      flat
      role="banner"
      style="z-index: 1; overflow: visible;"
    >
      <v-container class="d-flex justify-space-between align-center py-0">
        <div class="d-flex align-center">
          <v-img
            alt="Brasão da Universidade Federal do Ceará"
            class="mr-4"
            contain
            height="46"
            src="/brasao.png"
            width="40"
          />
          <div class="d-flex flex-column">
            <span class="text-title-medium text-sm-h6 font-weight-bold text-white lh-1">Almoxarifado UFC</span>
            <span class="text-body-small text-sm-body-2 text-grey-lighten-1">Campus Quixadá</span>
          </div>
        </div>

        <v-btn
          aria-label="Acessar o Sistema do Almoxarifado"
          class="text-none font-weight-bold px-6 rounded-pill text-grey-darken-4"
          color="warning"
          elevation="4"
          size="large"
          to="/dashboard"
          variant="flat"
        >
          Acessar Sistema
        </v-btn>
      </v-container>
    </v-toolbar>

    <v-container class="flex-grow-1 d-flex align-center justify-center position-relative px-4 py-12" role="main" style="z-index: 1;">

      <div class="d-flex flex-column align-center text-center w-100" style="max-width: 1200px;">

        <div
          aria-level="1"
          class="text-headline-large text-md-display-small text-lg-h2 font-weight-black text-white mb-6 text-wrap w-100"
          role="heading"
        >
          Gestão Inteligente de<br class="d-none d-sm-block">
          <span class="text-warning"> Estoque e Empréstimos</span>
        </div>

        <div
          aria-level="2"
          class="text-body-large text-sm-h6 text-md-headline-medium text-grey-lighten-2 font-weight-regular mb-12 w-100 w-md-75 mx-auto"
          role="heading"
        >
          Uma plataforma moderna, 100% web, projetada para simplificar o controle de materiais e a gestão de pedidos na sua instituição.
        </div>

        <v-row class="w-100 justify-center">
          <v-col class="d-flex" cols="12" md="4">
            <v-card
              class="flex-grow-1 text-center pa-8 rounded-xl"
              color="grey-lighten-1"
              elevation="0"
              style="background: rgba(15, 15, 15, 0.1); backdrop-filter: blur(1px);"
              variant="outlined"
            >
              <v-icon
                aria-hidden="true"
                class="mb-4"
                color="warning"
                icon="mdi-package-variant-closed"
                size="48"
              />
              <div aria-level="3" class="text-headline-small font-weight-bold mb-3 text-white" role="heading">
                Inventário
              </div>
              <div class="text-body-large text-wrap text-grey-lighten-2">
                Controle rigoroso e atualizado de todos os itens cadastrados no almoxarifado em tempo real.
              </div>
            </v-card>
          </v-col>

          <v-col class="d-flex" cols="12" md="4">
            <v-card
              class="flex-grow-1 text-center pa-8 rounded-xl"
              color="grey-lighten-1"
              elevation="0"
              style="background: rgba(15, 15, 15, 0.1); backdrop-filter: blur(1px);"
              variant="outlined"
            >
              <v-icon
                aria-hidden="true"
                class="mb-4"
                color="warning"
                icon="mdi-hand-extended-outline"
                size="48"
              />
              <div aria-level="3" class="text-headline-small font-weight-bold mb-3 text-white" role="heading">
                Empréstimos
              </div>
              <div class="text-body-large text-wrap text-grey-lighten-2">
                Acompanhamento detalhado de retiradas, devoluções e situação pendente dos materiais.
              </div>
            </v-card>
          </v-col>

          <v-col class="d-flex" cols="12" md="4">
            <v-card
              class="flex-grow-1 text-center pa-8 rounded-xl"
              color="grey-lighten-1"
              elevation="0"
              style="background: rgba(15, 15, 15, 0.1); backdrop-filter: blur(1px);"
              variant="outlined"
            >
              <v-icon
                aria-hidden="true"
                class="mb-4"
                color="warning"
                icon="mdi-chart-timeline-variant-shimmer"
                size="48"
              />
              <div aria-level="3" class="text-headline-small font-weight-bold mb-3 text-white" role="heading">
                Transparência
              </div>
              <div class="text-body-large text-wrap text-grey-lighten-2">
                Centralização de operações em um único ambiente acessível a qualquer hora e em qualquer lugar.
              </div>
            </v-card>
          </v-col>
        </v-row>

      </div>
    </v-container>

    <v-footer class="position-relative py-6 mt-auto" color="transparent" role="contentinfo" style="z-index: 1;">
      <v-container class="py-0">
        <v-row align="center" class="px-0" justify="space-between">
          <v-col class="text-center text-sm-left" cols="12" sm="8">
            <span class="d-block text-white text-body-large font-weight-bold mb-2">
              Universidade Federal do Ceará - Campus Quixadá
            </span>
            <span class="d-block text-grey-lighten-1 text-body-medium mb-2">
              Av. José de Freitas Queiroz, 5003 - Cedro, Quixadá - CE, 63902-580
            </span>
            <div class="d-flex align-center justify-center justify-sm-start flex-wrap text-grey-lighten-1 text-body-medium">
              <span>Contato: (88) 3412-0919</span>
              <v-icon aria-hidden="true" class="mx-2 text-grey-darken-1" icon="mdi-circle-small" size="16" />
              <span>Site:</span>
              <v-btn
                aria-label="Acessar o site do Campus Quixadá em nova guia"
                class="px-1 py-0 h-auto text-none font-weight-bold text-body-medium"
                color="warning"
                href="https://www.quixada.ufc.br"
                :ripple="false"
                target="_blank"
                variant="plain"
              >
                www.quixada.ufc.br
              </v-btn>
            </div>
          </v-col>

          <v-col class="text-center text-sm-right mt-4 mt-sm-0" cols="12" sm="4">
            <span class="text-grey-lighten-1 text-body-medium">
              &copy; {{ new Date().getFullYear() }} Almoxarifado UFC.<br>
              Desenvolvido para gestão inteligente.
            </span>
          </v-col>
        </v-row>
      </v-container>
    </v-footer>
  </v-sheet>
</template>

<route lang="yaml">
meta:
  layout: blank
</route>
