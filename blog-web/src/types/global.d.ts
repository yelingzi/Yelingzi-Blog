// global.d.ts
declare global {
  interface Window {
    PioInstance: {
      modules?: {
        showByDirective: (element: {
          getAttribute: (name: string) => string | null
        }) => void
      }
    }
  }

  interface HTMLElement {
    _pioHandler?: EventListenerOrEventListenerObject
  }
}

export { }
