// 修改 Vue 指令实现 (directives/pio.ts)
import type { App, Directive } from 'vue'

const pioDirective: Directive = {
  mounted(el: HTMLElement, binding) {
    // 获取全局实例
    const pio = window.PioInstance

    // 参数处理
    const { value } = binding
    const text = value?.text || el.innerText.trim()
    const type = value?.type || 'default'

    // 创建自定义事件监听
    const handler = () => {
      if (!pio?.modules?.showByDirective) return

      // 直接调用方法代替属性读取
      pio.modules.showByDirective({
        getAttribute: (name: string) => {
          return { text, type }[name.replace('data-pio-', '')]
        }
      })
    }

    // 添加事件监听
    el.addEventListener('mouseenter', handler)

    // 在元素上存储引用以便卸载
    el._pioHandler = handler
  },
  unmounted(el: HTMLElement) {
    // 移除事件监听
    if (el._pioHandler) {
      el.removeEventListener('mouseenter', el._pioHandler)
      delete el._pioHandler
    }
  }
}

export default (app: App) => {
  app.directive('pio', pioDirective);
};
