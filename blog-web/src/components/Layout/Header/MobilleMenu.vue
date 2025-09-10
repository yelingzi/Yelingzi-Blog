<template>
  <el-drawer :visible.sync="visible" direction="ltr" :with-header="false" size="50%">
    <div class="mobile-menu">
      <div class="menu-header">
        <img :src="$store.state.webSiteInfo.logo" alt="logo" class="logo">
        <h2 class="site-name">{{ $store.state.webSiteInfo.name }}</h2>
      </div>
      <div class="menu-content">
        <TransitionGroup name="menu-item">
          <router-link v-for="route in routes" :key="route.path" :to="route.path" class="menu-item"
            :class="{ active: $route.path === route.path }" @click.native="closeMenu">
            <i v-if="route.meta?.icon" :class="route.meta.icon"></i>
            <span>{{ getMenuTitle(route) }}</span>
          </router-link>
        </TransitionGroup>
      </div>
      <div class="menu-footer">
        <p>© {{ new Date().getFullYear() }} {{ $store.state.webSiteInfo.title }}</p>
      </div>
    </div>
  </el-drawer>
</template>

<script>
export default {
  name: 'MobileMenu',
  data() {
    return {
      visible: false
    }
  },
  watch: {
    '$store.state.mobileMenuVisible': {
      immediate: true,
      handler(val) {
        this.visible = val
      }
    },
    visible(val) {
      if (val !== this.$store.state.mobileMenuVisible) {
        this.$store.commit('setMobileMenuVisible', val)
      }
    }
  },
  computed: {
    routes() {
      const rootRoute = this.$router.options.routes.find(route => route.path === '/')
      if (!rootRoute || !rootRoute.children) return []

      return rootRoute.children.filter(route => {
        if (!route.name || route.meta?.hidden) return false
        return true
      })
    }
  },
  methods: {
    closeMenu() {
      this.$store.commit('setMobileMenuVisible', false)
    },
    getMenuTitle(route) {
      return route.meta?.title?.split(' - ')[0] || route.name
    }
  }
}
</script>

<style scoped>
.mobile-menu {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: var(--bg-color);
}

.menu-header {
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  height: 36px;
  width: auto;
  border-radius: 5px;
  transition: transform 0.3s ease;
}

.logo:hover {
  transform: scale(1.05);
}

.site-name {
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--text-color, #615959);
  margin: 0;
}

.menu-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  color: var(--text-color, #918484);
  text-decoration: none;
  border-radius: 8px;
  margin-bottom: 8px;
  transition: all 0.3s ease;
}

.menu-item i {
  margin-right: 12px;
  font-size: 18px;
  transition: transform 0.3s ease;
}

.menu-item span {
  flex: 1;
  font-weight: 500;
}

.menu-item:hover {
  background-color: var(--hover-color, #f5f5f5);
  transform: translateX(4px);
}

.menu-item:hover i {
  transform: scale(1.1);
}

.menu-item.active {
  background-color: var(--primary-color, #1890ff);
  color: #fff;
}

.menu-footer {
  padding: 16px;
  text-align: center;
  color: var(--text-secondary-color, #666);
  font-size: 0.9rem;
  border-top: 1px solid var(--border-color, #eee);
}

/* 菜单项动画 */
.menu-item-enter-active,
.menu-item-leave-active {
  transition: all 0.3s ease;
}

.menu-item-enter,
.menu-item-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>
