// directive/index.ts
import slideIn from "./slideIn/slideIn";
import live2d from "./live2d/live2d";
import type { App } from "vue";

export default (App: App) => {
  slideIn(App)
  live2d(App)
}
