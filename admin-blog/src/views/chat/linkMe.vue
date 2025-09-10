<template>
  <div class="container">
    <el-empty description="内容装修中，请联系邮箱：yelingzi55@163.com"></el-empty>
  </div>
</template>

<script lang="ts" setup>

const socket = new WebSocket('ws://localhost:8090/ws');

socket.onopen = () => {
  console.log('Connected to WebSocket server.');
  // 发送心跳消息
  setInterval(() => {
    socket.send('ping'); // 发送心跳消息
  }, 25000); // 每 25 秒发送一次心跳消息
};

socket.onmessage = (event) => {
  console.log('Message from server: ', event.data);
};

socket.onclose = (event) => {
  console.log('WebSocket connection closed: ', event);
};


</script>

<style lang="scss" scoped>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  margin: 0;
}
</style>
