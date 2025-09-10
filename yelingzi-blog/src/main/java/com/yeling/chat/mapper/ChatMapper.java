package com.yeling.chat.mapper;

import com.yeling.chat.domain.dto.GroupChatMessageDto;
import com.yeling.chat.domain.dto.MessageListDto;
import com.yeling.chat.domain.dto.SingleChatMessageDto;
import com.yeling.chat.domain.entity.Chat;
import com.yeling.chat.domain.entity.GroupChat;
import com.yeling.chat.vo.response.ChatMessageResp;
import com.yeling.chat.vo.response.SingleMessageResp;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatMapper {


    @Insert("""
            INSERT INTO chat(user_id, nickname, message, to_user, ip, message_type)
            VALUES (#{userId}, #{nickname}, #{message}, #{toUser}, #{ip}, #{type})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int addChat(SingleChatMessageDto chatMessageDto);

    @Select("SELECT * FROM chat WHERE id = #{id}")
    Chat selectById(long id);

    @Insert("""
            INSERT INTO group_chat(user_id, nickname, message, to_group, ip, message_type)
            VALUES (#{userId}, #{nickname}, #{message}, #{toGroup}, #{ip}, #{type})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int addGroupChat(GroupChatMessageDto chatMessageDto);

    @Select("SELECT * FROM group_chat WHERE id = #{id}")
    ChatMessageResp selectGroupChatById(long id);

    @Select("""
            SELECT * FROM chat 
            WHERE id>#{cursor} AND (user_id=#{userId} OR to_user=#{userId}) 
            """)
    List<Chat> getSingleMessageListAfterCursorByUserId(@Param("userId")  long userId, @Param("cursor")  long cursor);

    @Select("""
            SELECT * FROM chat 
            WHERE id>#{cursor} AND nickname=#{nickname} OR to_user=#{nickname}
            """)
    List<Chat> getSingleMessageListAfterCursorByNickname(@Param("nickname")  String nickname, @Param("cursor")  long cursor);


    @Select("""
            SELECT * FROM chat 
            WHERE id>#{cursor} AND (user_id=#{userId} AND to_user=#{toChat}) OR (to_user=#{userId} AND user_id=#{toChat}) 
            LIMIT 0,#{limit}
            """)
    List<ChatMessageResp> getSingleMessageListAfterCursorByUserIdAndToChat(MessageListDto messageListDto);

    @Select("""
            SELECT * FROM chat 
            WHERE id<#{cursor} AND (user_id=#{userId} AND to_user=#{toChat}) OR (to_user=#{userId} AND user_id=#{toChat}) 
            LIMIT 0,#{limit}
            """)
    List<ChatMessageResp> getSingleMessageListBeforeCursorByUserIdAndToChat(MessageListDto messageListDto);

    @Select("""
            SELECT * FROM chat 
            WHERE id>#{cursor} AND (nickname=#{nickname} AND to_user=#{toChat}) OR (to_user=#{nickname} AND user_id=#{toChat}) 
            LIMIT 0,#{limit}
            """)
    List<ChatMessageResp> getSingleMessageListAfterCursorByNicknameAndToChat(MessageListDto messageListDto);

    @Select("""
            SELECT * FROM chat 
            WHERE id<#{cursor} AND (nickname=#{nickname} AND to_user=#{toChat}) OR (to_user=#{nickname} AND user_id=#{toChat}) 
            LIMIT 0,#{limit}
            """)
    List<ChatMessageResp> getSingleMessageListBeforeCursorByNicknameAndToChat(MessageListDto messageListDto);

    @Select("""
            SELECT * FROM group_chat 
            WHERE id<#{cursor} 
            LIMIT 0,#{limit}
            """)
    List<ChatMessageResp> getGroupMessageListBeforeCursor(MessageListDto messageListDto);

    @Select("""
            SELECT * FROM group_chat WHERE id>#{cursor} LIMIT 0,#{limit}
            """)
    List<ChatMessageResp> getGroupMessageListAfterCursor(MessageListDto messageListDto);

    @Select("""
            SELECT * FROM group_chat WHERE id>#{cursor}
            """)
    List<GroupChat> getNewGroupMessageListAfterCursor(int  cursor);

}
