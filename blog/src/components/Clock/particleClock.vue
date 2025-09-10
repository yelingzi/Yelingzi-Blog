<template>
  <div class="canvas-container">
    <canvas ref="canvasEl" class="canvas"></canvas>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue'

// 使用 ref 管理 DOM 元素和上下文
const canvasEl = ref<HTMLCanvasElement | null>(null)
const ctx = ref<CanvasRenderingContext2D | null>(null)
const particles = ref<Particle[]>([])
const text = ref<string>('')
const containerSize = ref({ width: 300, height: 100 })

class Particle {
  size: number
  x: number
  y: number

  constructor() {
    if (!canvasEl.value) {
      this.size = 0
      this.x = 0
      this.y = 0
      return
    }
    this.size = getRandom(2 * devicePixelRatio, 4 * devicePixelRatio)
    const r = Math.min(canvasEl.value.width, canvasEl.value.height) / 2
    const rad = (getRandom(0, 360) * Math.PI) / 180
    const cx = canvasEl.value.width / 2
    const cy = canvasEl.value.height / 2
    this.x = cx + r * Math.cos(rad)
    this.y = cy + r * Math.sin(rad)
  }


  draw() {
    if (!ctx.value) return
    ctx.value.beginPath()
    ctx.value.arc(this.x, this.y, this.size, 0, 2 * Math.PI)
    ctx.value.fillStyle = '#5445544d'
    ctx.value.fill()
  }

  moveTo(tx: number, ty: number) {
    const duration = 400
    const startX = this.x
    const startY = this.y
    const startTime = Date.now()

    const animate = () => {
      const progress = Math.min((Date.now() - startTime) / duration, 1)
      this.x = startX + (tx - startX) * progress
      this.y = startY + (ty - startY) * progress

      if (progress < 1) {
        requestAnimationFrame(animate)
      }
    }

    requestAnimationFrame(animate)
  }

}

const getRandom = (min: number, max: number) => {
  return Math.floor(Math.random() * (max + 1 - min) + min)
}

// 初始化画布
const initCanvas = () => {
  if (!canvasEl.value) return

  const container = canvasEl.value.parentElement!
  const { width, height } = container.getBoundingClientRect()
  containerSize.value = { width, height }

  const dpr = window.devicePixelRatio
  canvasEl.value.width = width * dpr
  canvasEl.value.height = height * dpr

  canvasEl.value.style.width = `${width}px`
  canvasEl.value.style.height = `${height * 1}px`
  ctx.value = canvasEl.value.getContext('2d', { willReadFrequently: true })
}

const clear = () => {
  if (!ctx.value || !canvasEl.value) return

  ctx.value.clearRect(0, 0, canvasEl.value.width, canvasEl.value.height)
}

const draw = () => {

  clear()
  update()
  particles.value.forEach(p => p.draw())


  requestAnimationFrame(draw)
}


const getText = () => {
  return new Date().toTimeString().substring(0, 8)
}

const update = () => {

  const curText = getText()
  if (!ctx.value || !canvasEl.value) return
  if (text.value === curText) return

  clear()
  text.value = curText

  ctx.value.fillStyle = '#000000'
  ctx.value.textBaseline = 'middle'
  ctx.value.font = `${Math.min(containerSize.value.height * 0.5, 80)}px 'Roboto Mono', sans-serif`
  ctx.value.textAlign = 'center'
  ctx.value.fillText(text.value, canvasEl.value.width / 2, canvasEl.value.height / 2)
  const points = getPoints()
  clear()
  for (let i = 0; i < points.length; i++) {
    const [x, y] = points[i]
    let p = particles.value[i]
    if (!p) {
      p = new Particle()
      particles.value.push(p)
    }
    p.moveTo(x, y)
  }
  if (points.length < particles.value.length) {
    particles.value.splice(points.length)
  }

}

const getPoints = () => {
  const points: number[][] = []
  if (!ctx.value || !canvasEl.value) return points

  const { data } = ctx.value.getImageData(
    0, 0,
    canvasEl.value.width,
    canvasEl.value.height
  )

  const gap = 2
  const width = canvasEl.value.width
  const height = canvasEl.value.height



  for (let i = 0; i < width; i += gap) {
    for (let j = 0; j < height; j += gap) {
      const index = (Math.floor(i) + Math.floor(j) * width) * 4

      if (
        data[index] === 0 &&
        data[index + 1] === 0 &&
        data[index + 2] === 0 &&
        data[index + 3] === 255
      ) {
        points.push([i, j])
      }
    }
  }
  return points
}

onMounted(() => {

  initCanvas()

  draw()
  console.log(canvasEl.value?.width)
  console.log(canvasEl.value?.height)
})


</script>

<style lang="scss" scoped>
.canvas-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
  border-radius: 10px;
  overflow: hidden;
  background: linear-gradient(to bottom right, #fce4ec, #ffffff);
}

.canvas {
  display: block;
  width: 100%;
  height: 100%;
}
</style>
