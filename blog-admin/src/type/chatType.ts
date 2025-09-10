export interface Chat {
  id: string,
  route: string,
  chatType: 'single' | 'group',
  chatMessageList: ChatMessage[],
  nickname: string,
  avatar: string,
  unReadChat: number,
  lastMessage: ChatMessage
}

export interface ChatMessage {
  messageType: string,
  message: string,
  id: number,
  userId: number,
  nickname: string,
  userAvatar: string,
  createTime: string,
}

export interface NewSingleChatBrief {
  info: ChatInfo
  messages: ChatMessage[]
}

export interface NewGroupChatBrief {
  info: ChatInfo
  count: number
  lastMessage: ChatMessage
}

interface ChatInfo {
  type: 'group' | 'single'
  nickname: string
  avatar: string
  id: string,
}

export interface WsMsg {
  receiver: string
  message: ChatMessage
}
