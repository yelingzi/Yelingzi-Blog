<template>

  <div class="parent">
    <Vue3DraggableResizable :initW="110" :initH="120" v-model:x="x" v-model:y="y" v-model:w="w" v-model:h="h"
      :parent="true" v-model:active="active" :draggable="true" :resizable="true" @activated="print('activated')"
      @deactivated="print('deactivated')" @drag-start="print('drag-start')" @resize-start="print('resize-start')"
      @dragging="print('dragging')" @resizing="print('resizing')" @drag-end="print('drag-end')"
      @resize-end="print('resize-end')">
      这是一个测试示例
    </Vue3DraggableResizable>
  </div>

</template>

<script>
import { defineComponent } from 'vue'
import Vue3DraggableResizable from 'vue3-draggable-resizable'
// // 默认样式
// import 'vue3-draggable-resizable/dist/Vue3DraggableResizable.css'
export default defineComponent({
  components: { Vue3DraggableResizable },
  data() {
    return {

      y: 100, x: 100,
      // h: 100,
      // w: 100,
      active: false
    }
  },
  methods: {
    print(val) {
      console.log(val)
    }
  }
})
</script>
<style scoped lang="scss">
.parent {
  width: 200px;
  height: 200px;
  position: absolute;
  top: 100px;
  left: 100px;
  border: 1px solid #000;
  user-select: none;
}
</style>
