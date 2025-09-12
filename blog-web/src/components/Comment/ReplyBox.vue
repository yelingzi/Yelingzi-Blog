<template>
  <div class="reply-box">
    <div class="box-normal">

      <div class="reply-box-avatar">
        <el-avatar :size="50" v-if="user.avatar" :src="user.avatar" alt="" />
        <el-avatar :size="50" v-else alt="">{{ t('user') }}</el-avatar>
      </div>

      <div class="reply-box-warp">

        <div class="reply-box-text">
          <textarea v-if="textareaStyles" type="textarea" class="reply-box-textarea pen" v-model="content"
            :style="textareaStyles" @input.prevent="inputActive" :placeholder="placeholderText"
            ref="textareaRef"></textarea>
        </div>

        <div class="box-expand">
          <!-- <Emoji @add-emoji="handleEmoji" @choose-type="handleType"></Emoji> -->
          <div></div>
          <div class="reply-box-send btn" :class="sendActive ? 'send-active' : ''" @click="handleAdd">
            {{ t('comment') }}
          </div>
        </div>

      </div>

    </div>

  </div>
</template>

<script lang="ts" setup>
import { addArticleCommentService, addTalkCommentService } from '@/api/comment';
import { useUserStore } from '@/stores';
import { ElMessage, ElMessageBox } from 'element-plus';
import { computed, nextTick, onMounted, reactive, ref, toRefs, watch, type CSSProperties } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { t } from '@/utils/i18n'
const router = useRouter()
const route = useRoute()
const userState = useUserStore()
const user = userState.getUserState()

const emit = defineEmits(["reload"]);

interface Props {
  commentType?: number
  replyNickname?: string
  parentId?: number
  toId?: number
  id: number
}

const props = withDefaults(defineProps<Props>(), {
  commentType: 1,
  replyNickname: '',
  parentId: 0,
  toId: 0,
  id: 0
})
const content = ref('')
const sendActive = ref(false)

const placeholderText = computed(() =>
  props.replyNickname ? `回复 @${props.replyNickname}：` : "发一条友善的评论"
);

// const handleEmoji = (key: string) => {
//   content.value += key;
//   sendActive.value = true;
// };
// const handleType = (key: number) => {
//   emojiType.value = key;
// };

const handleLogin = () => {
  userState.setLastShowWeb(route.path)
  router.push('/login')
}

const commentCheck = async () => {
  let isLoginChecked = false; // 标志位，用于标记是否已经处理了登录检查

  if (!userState.getIsLogin()) {
    try {
      await ElMessageBox.confirm('还没进行登录，请先登录再查看', '是否登录', {
        type: 'warning',
        lockScroll: false,
        confirmButtonText: '确认',
        cancelButtonText: '取消'
      });
      handleLogin();
      // isLoginChecked = true;
    } catch (error) {
      // 处理登录失败或用户取消的情况
      ElMessage.info("登录已取消");
      // isLoginChecked = true;
      return false;
    }
  }

  if (isLoginChecked && content.value.trim() === "") {
    ElMessage.error("评论不能为空");
    return false;
  }

  // if (content.value.trim() === "") {
  //   ElMessage.error("评论不能为空");
  //   return false;
  // }
  console.log(1)
  return true;
};

const handleAdd = async () => {
  if (await !commentCheck()) {
    return
  }
  console.log(2)
  // 解析表情
  // commentForm.value.commentContent = commentContent.value.replace(/\[.+?\]/g, (str) => {
  //   if (emojiType.value === 0) {
  //     if (emojiList[str] === undefined) {
  //       return str;
  //     }
  //     return (
  //       "<img src= '" +
  //       emojiList[str] +
  //       "' width='21' height='21' style='margin: 0 1px;vertical-align: text-bottom'/>"
  //     );
  //   }
  //   if (emojiType.value === 1) {
  //     if (tvList[str] === undefined) {
  //       return str;
  //     }
  //     return (
  //       "<img src= '" +
  //       tvList[str] +
  //       "' width='21' height='21' style='margin: 0 1px;vertical-align: text-bottom'/>"
  //     );
  //   }
  //   return str;
  // });
  console.log(props.id)
  let res;
  if (props.commentType == 1) {
    res = await addArticleCommentService({
      toId: props.toId,
      toNickname: props.replyNickname,
      content: content.value,
      articleId: props.id,
      parentId: props.parentId
    })
  } else if (props.commentType == 2) {
    res = await addTalkCommentService({
      toId: props.toId,
      toNickname: props.replyNickname,
      content: content.value,
      talkId: props.id,
      parentId: props.parentId
    })
  }

  if (res && res.data.code) {
    console.log('评论成功')
    emit('reload', res.data.data);
  }

};
// const setReply = (flag: boolean) => {
//   show.value = flag;
// };

const textareaStyles = ref<CSSProperties>({
  height: '52px',
  overflowY: 'hidden'
});
const textareaRef = ref<HTMLTextAreaElement | null>(null);

const inputActive = () => {
  adjustTextareaHeight();
};

const adjustTextareaHeight = () => {
  if (!textareaRef.value) return;

  // 重置高度以便正确计算滚动高度
  textareaRef.value.style.height = 'auto';

  // 确保内容不为空时计算高度
  if (textareaRef.value.value.trim() === '') {
    textareaRef.value.style.height = '52px';
    textareaRef.value.style.overflowY = 'hidden';
    return;
  }

  // 计算新的高度（不超过200px）
  const newHeight = Math.min(textareaRef.value.scrollHeight, 200);

  // 应用新高度
  textareaRef.value.style.height = `${newHeight}px`;

  // 如果内容超过200px，显示滚动条
  if (textareaRef.value.scrollHeight > 200) {
    textareaRef.value.style.overflowY = 'auto';
  } else {
    textareaRef.value.style.overflowY = 'hidden';
  }
};

onMounted(() => {
  nextTick(() => {
    adjustTextareaHeight();
  });

});

// defineExpose({ commentForm, nickname, setReply });
</script>

<style lang="scss" scoped>
@use '@/assets/styles/common.scss' as com;
@use '@/assets/styles/mixin.scss' as m;

/* 评论框 */
.reply-box {
  width: 100%;
  box-sizing: border-box;
}

.box-normal {
  display: flex;
}

.reply-box-avatar {
  @include m.flex;
  margin-right: 10px;
  align-items: normal;
}

.reply-box-warp {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.reply-box-text {
  width: 100%;
}

.reply-box-textarea {
  word-wrap: break-word;
  box-sizing: border-box;
  letter-spacing: 1px;
  position: relative;
  vertical-align: baseline;
  white-space: pre-wrap;
  width: 100%;
  word-break: break-all;
  font-family: inherit;

  border-radius: 6px;
  background-color: var(--comment-color);
  font-size: 12px;
  line-height: 38px;
  color: var(--grey-9);
  resize: none;
  outline: none;
  transition: all 0.6s ease;
  min-height: 38px;
  max-height: 200px;
  padding: 8px 10px;
  overflow-y: hidden;


  /* 鼠标悬停状态 */
  &:hover {
    background-color: var(--color-cyan-light);
    border-color: var(--color-pink);
  }

  /* 输入框聚焦状态 */
  &:focus {
    background-color: var(--grey-0);
    border-color: var(--color-pink);
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(255, 105, 180, 0.15);
    line-height: 1.5;
  }


  /* 占位符样式 */
  &::placeholder {
    color: var(--grey-4);
    transition: all 0.3s;
  }

  &:focus::placeholder {
    color: transparent;
  }
}

/* 发送按钮激活状态 */
.send-active {
  background-color: var(--color-pink) !important;
  color: white !important;
}

.box-expand {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 12px;
}

.reply-box-send {
  @include m.flex;
  flex-basis: 70px;
  margin-left: 10px;
  border-radius: 4px;
  background-color: var(--comment-btn);
  color: var(--grey-0);
  height: 30px;
}
</style>
