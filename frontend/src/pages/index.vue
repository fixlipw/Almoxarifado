<script setup lang="ts">
  import { onMounted, onUnmounted, ref } from 'vue'

  const canvasRef = ref<HTMLCanvasElement | null>(null)

  onMounted(() => {
    const canvas = canvasRef.value
    if (!canvas) return
    const ctx = canvas.getContext('2d')
    if (!ctx) return

    let particles: any[] = []
    let animationFrameId: number

    const resize = () => {
      canvas.width = window.innerWidth
      canvas.height = window.innerHeight
      initParticles()
    }

    const initParticles = () => {
      particles = []
      const numParticles = Math.floor((window.innerWidth * window.innerHeight) / 12_000)
      for (let i = 0; i < numParticles; i++) {
        particles.push({
          x: Math.random() * canvas.width,
          y: Math.random() * canvas.height,
          vx: (Math.random() - 0.5) * 0.5,
          vy: (Math.random() - 0.5) * 0.5,
          radius: Math.random() * 1.5 + 0.5,
        })
      }
    }

    const drawParticles = () => {
      ctx.clearRect(0, 0, canvas.width, canvas.height)
      ctx.fillStyle = 'rgba(255, 255, 255, 0.4)'

      for (let i = 0; i < particles.length; i++) {
        const p = particles[i]
        p.x += p.vx
        p.y += p.vy

        if (p.x < 0 || p.x > canvas.width) p.vx *= -1
        if (p.y < 0 || p.y > canvas.height) p.vy *= -1

        ctx.beginPath()
        ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2)
        ctx.fill()

        for (let j = i + 1; j < particles.length; j++) {
          const p2 = particles[j]
          const dx = p.x - p2.x
          const dy = p.y - p2.y
          const dist = Math.hypot(dx, dy)

          if (dist < 120) {
            ctx.beginPath()
            ctx.strokeStyle = `rgba(255, 255, 255, ${0.12 - dist / 120 * 0.12})`
            ctx.moveTo(p.x, p.y)
            ctx.lineTo(p2.x, p2.y)
            ctx.stroke()
          }
        }
      }
      animationFrameId = requestAnimationFrame(drawParticles)
    }

    window.addEventListener('resize', resize)
    resize()
    drawParticles()

    onUnmounted(() => {
      window.removeEventListener('resize', resize)
      cancelAnimationFrame(animationFrameId)
    })
  })
</script>
<template>
  <div class="hero-container bg-grey-darken-4">
    <canvas ref="canvasRef" class="particles-canvas" />

    <v-container class="d-flex justify-space-between align-center py-2 position-relative z-1">
      <div class="d-flex align-center">
        <v-img
          alt="Brasão"
          class="mr-3"
          contain
          height="42"
          src="/brasao.png"
          width="36"
        />
        <div>
          <div class="text-title-medium font-weight-bold text-white d-none d-sm-block">
            Almoxarifado UFC
          </div>
          <div class="text-body-small text-medium-emphasis">Campus Quixadá</div>
        </div>
      </div>
      <div>
        <v-btn
          class="text-none font-weight-bold px-4 rounded-pill text-grey-darken-4 glass-button"
          color="warning"
          elevation="4"
          size="small"
          to="/dashboard"
          variant="outlined"
        >
          Acessar Sistema
        </v-btn>
      </div>
    </v-container>

    <v-container class="flex-grow-1 d-flex flex-column justify-center align-center text-center px-4 position-relative z-1">
      <h1 class="text-headline-large md:text-h3 font-weight-black text-white mb-2 main-title text-wrap">
        Gestão Inteligente de<br class="d-none d-md-block">
        <span class="text-warning"> Estoque e Empréstimos</span>
      </h1>

      <p class="text-title-medium md:text-h6 text-grey-lighten-2 font-weight-regular mb-4 subtitle" style="line-height: 1.3;">
        Uma plataforma moderna, 100% web, projetada para simplificar o controle de materiais e a gestão de pedidos na sua instituição.
      </p>

      <v-row align="stretch" class="w-100 justify-center glass-features rounded-xl pa-2 mx-auto">
        <v-col class="d-flex py-1" cols="12" md="4">
          <v-card border="0" class="glass-card flex-grow-1 text-center pa-3" elevation="0">
            <v-icon class="mb-1" color="warning" icon="mdi-package-variant-closed" size="32" />
            <h3 class="text-title-medium text-white font-weight-bold mb-1">Inventário</h3>
            <p class="text-body-small text-grey-lighten-1" style="line-height: 1.2;">
              Controle rigoroso e atualizado de todos os itens cadastrados no almoxarifado em tempo real.
            </p>
          </v-card>
        </v-col>

        <v-col class="d-flex py-1" cols="12" md="4">
          <v-card border="0" class="glass-card flex-grow-1 text-center pa-3" elevation="0">
            <v-icon class="mb-1" color="warning" icon="mdi-hand-extended-outline" size="32" />
            <h3 class="text-title-medium text-white font-weight-bold mb-1">Empréstimos</h3>
            <p class="text-body-small text-grey-lighten-1" style="line-height: 1.2;">
              Acompanhamento detalhado de retiradas, devoluções e situação pendente dos materiais.
            </p>
          </v-card>
        </v-col>

        <v-col class="d-flex py-1" cols="12" md="4">
          <v-card border="0" class="glass-card flex-grow-1 text-center pa-3" elevation="0">
            <v-icon class="mb-1" color="warning" icon="mdi-chart-timeline-variant-shimmer" size="32" />
            <h3 class="text-title-medium text-white font-weight-bold mb-1">Transparência</h3>
            <p class="text-body-small text-grey-lighten-1" style="line-height: 1.2;">
              Centralização de operações em um único ambiente acessível a qualquer hora e em qualquer lugar.
            </p>
          </v-card>
        </v-col>
      </v-row>
    </v-container>

    <!-- Footer Area -->
    <v-footer class="bg-transparent text-center d-flex flex-column py-2 mt-auto position-relative z-1" elevation="0">
      <div class="text-white text-body-small font-weight-bold mb-0">
        Universidade Federal do Ceará - Campus Quixadá
      </div>
      <div class="text-grey-lighten-1 text-body-small mb-0" style="font-size: 0.70rem !important;">
        Av. José de Freitas Queiroz, 5003 - Cedro, Quixadá - CE, 63902-580
      </div>
      <div class="text-grey-lighten-1 text-body-small mb-0" style="font-size: 0.70rem !important;">
        Contato: (88) 3412-0919 | Site: <a class="text-warning text-decoration-none" href="https://www.quixada.ufc.br" target="_blank">www.quixada.ufc.br</a>
      </div>
      <v-divider class="my-1 w-75 mx-auto border-opacity-25" color="white" />
      <div class="text-grey-darken-1 text-body-small" style="font-size: 0.65rem !important;">
        &copy; {{ new Date().getFullYear() }} Almoxarifado UFC. Desenvolvido para a gestão inteligente de estoque.
      </div>
    </v-footer>
  </div>
</template>

<route lang="yaml">
meta:
  layout: blank
</route>

<style scoped>
.particles-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.z-1 {
  z-index: 1;
}

.hero-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  background: linear-gradient(rgba(18, 18, 18, 0.4), rgba(0, 0, 0, 0.8)), url('/ufc_panorama.jpeg') no-repeat center center;
  background-size: cover;
  background-attachment: fixed;
}

.main-title {
  line-height: 1.2;
  text-shadow: 0px 4px 15px rgba(0,0,0,0.8);
}

.subtitle {
  max-width: 750px;
  line-height: 1.6;
  text-shadow: 0px 2px 4px rgba(0,0,0,0.8);
}

.glass-features {
  max-width: 1100px;
  background: rgba(255, 255, 255, 0.02);
  backdrop-filter: blur(3px);
  -webkit-backdrop-filter: blur(3px);
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.glass-card {
  background: rgba(0, 0, 0, 0.3) !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  transition: transform 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275), background 0.3s ease;
  border-radius: 16px;
}

.glass-card:hover {
  transform: translateY(-10px);
  background: rgba(255, 255, 255, 0.08) !important;
  border-color: rgba(255, 255, 255, 0.2) !important;
}
</style>
