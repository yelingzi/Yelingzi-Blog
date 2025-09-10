// utils/icon.ts
import { defineAsyncComponent } from 'vue'

// 定义明确的类型接口
interface IconComponents {
  Article: ReturnType<typeof defineAsyncComponent>
  Edit: ReturnType<typeof defineAsyncComponent>
  User: ReturnType<typeof defineAsyncComponent>
  Category: ReturnType<typeof defineAsyncComponent>
  Comment: ReturnType<typeof defineAsyncComponent>
  View: ReturnType<typeof defineAsyncComponent>
  Tags: ReturnType<typeof defineAsyncComponent>
  Team: ReturnType<typeof defineAsyncComponent>
  RiLi: ReturnType<typeof defineAsyncComponent>
  Talk: ReturnType<typeof defineAsyncComponent>
  Message: ReturnType<typeof defineAsyncComponent>
}

// 使用类型断言和明确类型
const Icons: IconComponents = {
  Article: defineAsyncComponent(() => import('@/assets/icons/article.svg')),
  Edit: defineAsyncComponent(() => import('@/assets/icons/edit.svg')),
  User: defineAsyncComponent(() => import('@/assets/icons/user.svg')),
  Category: defineAsyncComponent(() => import('@/assets/icons/category.svg')),
  Comment: defineAsyncComponent(() => import('@/assets/icons/comment.svg')),
  View: defineAsyncComponent(() => import('@/assets/icons/view.svg')),
  Tags: defineAsyncComponent(() => import('@/assets/icons/tags.svg')),
  Team: defineAsyncComponent(() => import('@/assets/icons/team.svg')),
  RiLi: defineAsyncComponent(() => import('@/assets/icons/calendar-check.svg')),
  Talk: defineAsyncComponent(() => import('@/assets/icons/talk.svg')),
  Message: defineAsyncComponent(() => import('@/assets/icons/message.svg'))
} as const

export type IconName = keyof typeof Icons
export default Icons