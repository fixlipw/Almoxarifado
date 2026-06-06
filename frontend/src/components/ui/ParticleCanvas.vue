<script lang="ts" setup>
import {onMounted, onUnmounted, ref} from 'vue'

interface Props {
  particleFill: string
  particleStroke: string
}

const props = withDefaults(defineProps<Props>(), {
  particleFill: 'rgba(255, 255, 255, 0.45)',
  particleStroke: '255, 255, 255',
})

const canvasRef = ref<HTMLCanvasElement | null>(null)

onMounted(() => {
  const canvas = canvasRef.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  if (!ctx) return

  type Particle = {
    x: number
    y: number
    vx: number
    vy: number
    radius: number
  }

  let particles: Particle[] = []
  let animationFrameId: number | null = null

  const PARTICLE_DENSITY = 12_000
  const MAX_PARTICLES = 300
  const BASE_CONNECT_DISTANCE = 120

  const getSize = () => {
    const w = Math.max(1, canvas.clientWidth)
    const h = Math.max(1, canvas.clientHeight)
    return {w, h}
  }

  const resize = () => {
    if (animationFrameId != null) {
      cancelAnimationFrame(animationFrameId)
      animationFrameId = null
    }

    const dpr = Math.max(1, globalThis.window.devicePixelRatio || 1)
    const {w, h} = getSize()

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
    const {w, h} = getSize()
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
    const {w, h} = getSize()
    ctx.clearRect(0, 0, w, h)
    ctx.fillStyle = props.particleFill

    const connectDistance = Math.max(60, Math.min(BASE_CONNECT_DISTANCE, Math.sqrt(w * h) / 8))

    for (let i = 0; i < particles.length; i++) {
      const particle = particles[i]
      particle.x += particle.vx
      particle.y += particle.vy

      if (particle.x < 0) {
        particle.x = 0
        particle.vx *= -1
      }
      if (particle.x > w) {
        particle.x = w
        particle.vx *= -1
      }
      if (particle.y < 0) {
        particle.y = 0
        particle.vy *= -1
      }
      if (particle.y > h) {
        particle.y = h
        particle.vy *= -1
      }

      ctx.beginPath()
      ctx.arc(particle.x, particle.y, particle.radius, 0, Math.PI * 2)
      ctx.fill()

      for (let j = i + 1; j < particles.length; j++) {
        const connectedParticle = particles[j]
        const dx = particle.x - connectedParticle.x
        const dy = particle.y - connectedParticle.y
        const distance = Math.hypot(dx, dy)

        if (distance < connectDistance) {
          const alpha = Math.max(0, 0.12 * (1 - distance / connectDistance))
          if (alpha <= 0) continue
          ctx.beginPath()
          ctx.strokeStyle = `rgba(${props.particleStroke}, ${alpha})`
          ctx.moveTo(particle.x, particle.y)
          ctx.lineTo(connectedParticle.x, connectedParticle.y)
          ctx.stroke()
        }
      }
    }

    animationFrameId = requestAnimationFrame(drawParticles)
  }

  globalThis.window.addEventListener('resize', resize)
  const ro = new ResizeObserver(() => resize())
  ro.observe(canvas)

  resize()

  onUnmounted(() => {
    globalThis.window.removeEventListener('resize', resize)
    ro.disconnect()
    if (animationFrameId != null) cancelAnimationFrame(animationFrameId)
  })
})
</script>

<template>
  <canvas
      ref="canvasRef"
      class="particle-canvas"
  />
</template>

<style scoped>
.particle-canvas {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}
</style>
