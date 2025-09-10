<!-- <template> -->
<!-- 置顶 / 音乐 / 设置 -->
<!-- <div class="top-set user-select-none">
    <div v-show="route.path != '/start' && isMobi" class="top pointer kbn-custom" data-tip=" 置顶 " style="padding: 5px 0"
      :class="{ topShow: state.scrollTop > 200 }" @click="handleTop">
      <i class="iconfont icon-yooxi"></i>
    </div>
    <div class="set pointer kbn-custom" data-tip="快捷键 提示" v-if="!isMobi">
      <el-popover popper-class="key-code-tip-pop" :effect="state.isDark" width="300" placement="right" :offset="18"
        title="" trigger="hover">
        <template #reference>
          <img width="25px" height="25px" :src="getImgIcon('icon-tip.svg')" />
        </template>
        <div class="key-code-tip-warp user-select-none">
          <div class="title" style="margin-bottom: 10px; font-size: 16px">
            快捷键
            <span style="font-size: 12px; color: #999; margin-left: 5px; color: #999">Mac/Win键位关系 Cmd - Win Opt -
              Alt</span>
          </div>
          <div class="tip-item" style="display: flex; justify-content: space-between" v-for="(item, index) in visitList"
            :key="index">
            <div class="key-group">{{ item.key }}</div>
            <div class="tip-content">{{ item.tip }}</div>
          </div>
        </div>
      </el-popover>
    </div>
    <div v-show="route.path != '/start'" class="set pointer kbn-custom" data-tip=" 搜索 " @click="handleSearch(false)"
      v-if="isMobi">
      <img width="25px" height="25px" :src="getImgIcon('icon-search.svg')" />
    </div>
    <div class="set pointer kbn-custom" data-tip=" 音乐播放器 "
      style="display: flex; align-items: center; justify-content: center" @click="handleShowMusicPlayer">
      <img v-if="state.isMusicPlaying" width="25px" height="25px" style="border-radius: 50%"
        :src="getOnlineImgUrl('/imgs/common/playing.gif')" alt="" />
      <img v-else width="25px" height="25px" :src="getImgIcon('icon-music.svg')" />
    </div>
    <div class="set pointer kbn-custom" data-tip=" 设置 " @click="handleSetShow">
      <img width="25px" height="25px" :src="getImgIcon('icon-set.svg')" />
    </div>
  </div>
</template>

<script lang="ts" setup>
// 逻辑可提取到 useHeader 组合式函数
</script>

<style lang="scss" scoped></style> -->
