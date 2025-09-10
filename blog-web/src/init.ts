

export const init = () => {

  // 监听是否离开网页
  window.addEventListener('visibilitychange', () => {
    if (!document.hidden) {
      document.title = "咦，舍得回来了！｜ 叶玲子的小栈";
    } else {
      document.title = "瓜皮人呢！还不回来？｜ 叶玲子的小栈";
    }
  });


  // 如果是ip访问，直接跳转到域名
  let ipRegex = /(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)/;
  let herf = window.location.href;
  if ((ipRegex.test(herf)) && import.meta.env.MODE == 'production') {
    window.location.href = import.meta.env.VITE_BLOG_URL + window.location.pathname + window.location.search;
  }

}

export default {
  init
}
