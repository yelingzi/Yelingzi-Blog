<template>
  <div class="reply-warp" id="reply-wrap">

    <DividerLine margin="20px" :content="i18n.comment"></DividerLine>

    <ReplyBox @reload="reloadComments" :comment-type="commentType" :id="id"></ReplyBox>
    <div v-if="commentList.length > 0">
      <div class="reply-item" v-for="(comment, index) of commentList" :key="comment.id">
        <div class="reply-box-avatar">
          <el-avatar :size="40" :src="comment.userAvatar" />
        </div>
        <div class="content-warp">
          <div class="user-info">
            <div class="user-name">{{ comment.userNickname }}</div>
            <component v-if="comment.userId == props.authorId" :is="badgeIcon"
              style="width: 0.9rem; height: 0.9rem;;" />
          </div>
          <div class="reply-content" v-html="comment.content"></div>
          <div class="reply-info">
            <span class="reply-time">{{ getDateDiff(comment.createTime) }}</span>
            <span class="reply-like btn" @click="like(comment)">
              <SvgIcon name="icon-dianzan_kuai" class="icon-avtive" :class="{ active: comment.isLike }"
                style="margin-right: 5px" />
              <span v-show="comment.likeCount">{{ comment.likeCount }}</span>
            </span>
            <span class="reply-btn btn" @click="handleReply(comment.id)">回复</span>
          </div>

          <div class="sub-reply-item" v-for="reply of comment.children" :key="reply.id">
            <div class="reply-box-avatar">
              <el-avatar class="sub-reply-avatar" :src="reply.userAvatar" />
            </div>
            <div class="content-warp">
              <div class="sub-user-info">
                <div class="sub-user-name">{{ reply.userNickname }}</div>
                <component v-if="comment.userId == props.authorId" :is="badgeIcon"
                  style="width: 0.8rem; height: 0.8rem;margin-left: 5px;" />
              </div>
              <span class="reply-content">
                <template v-if="reply.toId !== 0">回复 <span style="color: #008ac5">@{{
                  reply.toNickname
                    }}</span> :</template>
                <span v-html="reply.content"></span>
              </span>
              <div class="reply-info">
                <span class="reply-time">{{ getDateDiff(reply.createTime) }}</span>
                <span class="reply-like btn" @click="like(reply)">
                  <SvgIcon name="icon-dianzan_kuai" class="icon-avtive" :class="{ active: comment.isLike }"
                    style="margin-right: 5px" />
                  <span v-show="reply.likeCount > 0">{{ reply.likeCount }}</span>
                </span>
                <span class="reply-btn btn" @click="handleReply(reply.parentId)">回复</span>
              </div>
            </div>

          </div>
          <!-- <div ref="readMoreRef" class="view-more" v-show="comment.replyCount > 3">
            <span>共{{ comment.replyCount }}条回复, </span>
            <span class="view-more-btn" @click="readMoreComment(index, comment)">点击查看</span>
          </div> -->
          <!-- <Paging ref="pageRef" :total="comment.replyCount" :index="index" :commentId="comment.id"
            @get-current-page="getCurrentPage"></Paging> -->
          <ReplyBox v-if="comment.isPeply" ref="replyRef" class="mt-4" :comment-type="commentType" :id="id"
            :parent-id="comment.id" @reload="reloadComments">
          </ReplyBox>
          <div class="bottom-line"></div>
        </div>
      </div>
      <!-- <div class="loading-warp" v-if="count > commentList.length">
        <n-button class="btn" color="#e9546b" @click="getList">
          加载更多...
        </n-button>
      </div> -->
    </div>
    <div v-else style="padding: 1.25rem; text-align: center">来发评论吧~</div>
  </div>
</template>

<script setup lang="ts">

import type { Comment, LikeList } from '@/types/comment';
import ReplyBox from './ReplyBox.vue';
import DividerLine from '../Hr/DividerLine.vue';
import { getDateDiff } from '@/utils/common';
import { ref, computed, reactive, toRefs, watch, nextTick, onMounted, defineAsyncComponent } from 'vue';
import { useUserStore, useI18nStore } from '@/stores';
import { addArticleCommentLikeService, addTalkCommentLikeService, delArticleCommentLikeService, delTalkCommentLikeService, getArticleCommentLikeListService, getArticleCommentListService, getTalkCommentLikeListService, getTalkCommentListService } from '@/api/comment';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter()
const route = useRoute()
const i18n = useI18nStore().currentConfig
const badgeIcon = defineAsyncComponent(() =>
  import('@/assets/icons/badge.svg')
);
interface Props {
  commentType?: number
  id?: number
  authorId: number
}

const props = withDefaults(defineProps<Props>(), {
  commentType: 1,
  id: 0,
  authorId: 0
})
const commentList = ref<Comment[]>([])
const isLikeList = ref<LikeList[]>([])

const userState = useUserStore()

const replyRef = ref<any>([]);



const isLike = computed(() => (isLike: boolean) => isLike ? "like-flag" : "");

const handleLogin = () => {
  userState.setLastShowWeb(route.path)
  router.push('/login')
}

const like = (comment: Comment) => {
  if (!userState.getIsLogin()) {
    ElMessageBox.confirm('您还未登录，请先登录再点赞', '提示', {
      type: 'info',
      lockScroll: false,
      confirmButtonText: '去登录',
      cancelButtonText: '取消'
    }).then(() => {
      handleLogin();
    }).catch(() => {
      ElMessage.info("已取消");
    });
    return;
  }

  const commentId = comment.id;

  // 判断是否已经点赞
  const hasLiked = isLikeList.value.some(item => item.commentId === commentId);

  if (hasLiked) {
    // 已点赞，发送取消点赞请求
    cancelLike(commentId);
  } else {
    // 未点赞，发送点赞请求
    addLike(commentId);
  }
};

const addLike = async (commentId: number) => {
  try {
    // 根据 commentType 调用相应的服务
    let res;
    if (props.commentType === 1) {
      res = await addArticleCommentLikeService(commentId, props.id);
    } else if (props.commentType === 2) {
      res = await addTalkCommentLikeService(commentId, props.id);
    } else {
      ElMessage.error("未知的评论类型");
      return;
    }

    if (res.data.code === 1) {
      // 点赞成功，更新点赞列表和评论的点赞状态
      const newLikeItem = { commentId: res.data.data.commentId, id: res.data.data.id };
      isLikeList.value.push(newLikeItem);
      updateCommentLikeStatus(commentId, true);
      ElMessage.success("点赞成功");
    } else {
      ElMessage.error("点赞失败");
    }
  } catch (error) {
    console.error("点赞请求失败:", error);
    ElMessage.error("点赞请求失败");
  }
};

const cancelLike = async (commentId: number) => {
  try {
    // 通过 commentId 查找 isLikeList 中的 id
    const likeItem = isLikeList.value.find(item => item.commentId === commentId);
    if (!likeItem) {
      ElMessage.info("您尚未点赞");
      return;
    }

    // 根据 commentType 调用相应的服务
    let res;
    if (props.commentType === 1) {
      res = await delArticleCommentLikeService(likeItem.id);
    } else if (props.commentType === 2) {
      res = await delTalkCommentLikeService(likeItem.id);
    } else {
      ElMessage.error("未知的评论类型");
      return;
    }

    if (res.data.code === 1) {
      // 取消点赞成功，更新点赞列表和评论的点赞状态
      isLikeList.value = isLikeList.value.filter(item => item.id !== likeItem.id);
      updateCommentLikeStatus(commentId, false);
      ElMessage.success("取消点赞成功");
    } else {
      ElMessage.error("取消点赞失败");
    }
  } catch (error) {
    console.error("取消点赞请求失败:", error);
    ElMessage.error("取消点赞请求失败");
  }
};

// 更新当前评论的点赞状态
const updateCommentLikeStatus = (commentId: number, isLiked: boolean) => {

  const stack = [...commentList.value] // 使用栈进行DFS遍历

  while (stack.length) {
    const comment = stack.pop() as Comment

    if (comment.id === commentId) {
      comment.isLike = isLiked
      comment.likeCount = Math.max(0, comment.likeCount + (isLiked ? 1 : -1))
      break
    }

    if (comment.children) {
      stack.push(...comment.children)
    }
  }
};

// 查看当前页的回复评论
const getCurrentPage = (current: number, index: number, commentId: number) => {
  // getReplyList(commentId, { current: current, size: 5 }).then(({ data }) => {
  //   commentList.value[index].replyVOList = data.data;
  // });
};
const handleReply = (commentId: number) => {

  commentList.value.forEach((comment: any) => {
    if (comment.id === commentId) {
      comment.isPeply = true
    }
  });

};

// 重新加载评论列表
const reloadComments = (newComment: Comment) => {
  console.log(newComment)
  if (newComment.parentId == 0) {
    commentList.value.unshift({
      ...newComment,
      replyCount: 0,
      children: [],
      isPeply: false
    }
    );
  } else {
    commentList.value.forEach(comment => {

      if (comment.id === newComment.parentId) {
        comment.children.unshift(newComment)
      }

    });
  }
};
// 重新加载回复评论
const reloadReplies = (index: number) => {
  // getReplyList(commentList.value[index].id, {
  //   current: pageRef.value[index].current,
  //   size: 5,
  // }).then(({ data }) => {
  //   commentList.value[index].replyVOList = data.data;
  //   commentList.value[index].replyCount++;
  //   // 隐藏回复框
  //   replyRef.value[index].setReply(false);
  //   // 回复大于5条展示分页
  //   if (commentList.value[index].replyCount > 5) {
  //     pageRef.value[index].setPaging(true);
  //   }
  //   // 隐藏查看更多
  //   readMoreRef.value[index].style.display = "none";
  // });
};

const getIsLikeList = async () => {
  if (!userState.getIsLogin()) {
    return
  }
  if (props.commentType == 1) {
    const res = await getArticleCommentLikeListService(props.id)
    isLikeList.value = res.data.data
  } else if (props.commentType == 2) {
    const res = await getTalkCommentLikeListService(props.id)
    isLikeList.value = res.data.data
  }
}

const getList = async () => {
  await getIsLikeList()

  console.log("文章或说说ID:" + props.id)
  // 1为文章 2为说说
  if (props.commentType == 1) {
    const res = await getArticleCommentListService(props.id)
    commentList.value = res.data.data
  } else if (props.commentType == 1) {
    const res = await getTalkCommentListService(props.id)
    commentList.value = res.data.data
  }

  //点赞列表不为空，更新评论列表点赞状态
  if (isLikeList.value.length > 0) {
    const likeMap: { [key: number]: boolean } = {};
    isLikeList.value.forEach(item => {
      likeMap[item.commentId] = true;
    });

    commentList.value.forEach(comment => {
      comment.isLike = likeMap[comment.id] || false;

      if (comment.children && comment.children.length > 0) {
        comment.children.forEach(childComment => {
          childComment.isLike = likeMap[childComment.id] || false;
        });
      }

    });
  }

};

onMounted(() => {
  getList();

});
</script>

<style lang="scss" scoped>
@use "@/assets/styles/mixin.scss" as mi;

.reply-warp {
  padding: 0 20px;
}

.reply-title {
  display: flex;
  align-items: center;
  margin: 22px 0;
  padding-left: 10px;
  font-size: 20px;
  font-weight: 600;
  line-height: 40px;
}


.reply-item {
  display: flex;
  padding: 1rem 20px 0px 20px;


  .content-warp {
    flex: auto;
    margin-left: 0.6rem;
  }

  .bottom-line {
    border-bottom: 1px solid var(--grey-3);
    margin-top: 0.5rem;
  }
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
  height: 30px;

  .user-name {
    font-size: 16px;
    font-weight: 500;
    margin-right: 12px;
  }
}

.sub-reply-item {
  display: flex;

  font-size: 15px;
  line-height: 24px;
  margin-top: 12px;
}

.sub-user-info {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}

.sub-user-name {
  font-size: 13px;
  line-height: 24px;
}

.reply-content {
  overflow: hidden;
  word-wrap: break-word;
  word-break: break-word;
  white-space: pre-wrap;
  font-size: 0.9375rem;
  line-height: 1.5;
  vertical-align: baseline;
  margin-top: 5px;
}

.reply-info {
  display: flex;
  align-items: center;
  margin-top: 0.125rem;
  font-size: 0.8125rem;
  color: #9499a0;
}

.reply-time {
  margin-right: 15px;
  padding-top: 2px;
}

.reply-like {
  display: flex;
  align-items: center;
  margin-right: 17px;

}

@keyframes like-animation {
  0% {
    transform: scale(1);
  }

  50% {
    transform: scale(1.4);
    /* 放大到140% */
  }

  100% {
    transform: scale(1.1);
    /* 最终保持110%大小 */
  }
}

.active {
  color: var(--color-red);
  animation: like-animation 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94) both;
  display: inline-block;
}


.icon-avtive {
  transition: transform 0.3s ease-out;
}

.reply-btn {
  &:hover {
    color: var(--color-pink);
  }
}
</style>
