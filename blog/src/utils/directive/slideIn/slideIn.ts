import { type App } from "vue";

const OFFSET = 100;
const DURATION = 500;
const map = new WeakMap<Element, Animation>();
const ob = new IntersectionObserver((entries) => {
  for (const entry of entries) {
    if (entry.isIntersecting) {
      const animation = map.get(entry.target as Element);
      if (animation) {
        animation.play();
        ob.unobserve(entry.target as Element);
      }
    }
  }
});

const belowViewport = (el: Element): boolean => {
  const rect = el.getBoundingClientRect();
  return rect.top > window.innerHeight;
};

export default (app: App) => {
  app.directive("slide-in", {
    mounted(el: Element) {

      if (!belowViewport(el)) {
        return;
      }

      // 实现上滑动画
      const animation = el.animate(
        [
          {
            transform: `translateY(${OFFSET}px)`, // 修正拼写错误
            opacity: 0,
          },
          {
            transform: `translateY(0)`, // 修正拼写错误
            opacity: 1,
          },
        ],
        {
          duration: DURATION,
          easing: "ease-out",
          fill: "both",
        }
      );
      animation.pause();
      ob.observe(el);
      map.set(el, animation);
    },
    unmounted(el: Element) {
      ob.unobserve(el);
    },
  });
};
