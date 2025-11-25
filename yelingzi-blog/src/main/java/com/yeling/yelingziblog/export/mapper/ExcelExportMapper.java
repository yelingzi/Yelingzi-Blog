package com.yeling.yelingziblog.export.mapper;

import com.yeling.yelingziblog.album.entity.Album;
import com.yeling.yelingziblog.album.entity.Photo;
import com.yeling.yelingziblog.article.entity.Article;
import com.yeling.yelingziblog.article.entity.Tag;
import com.yeling.yelingziblog.chat.entity.Chat;
import com.yeling.yelingziblog.chat.entity.GroupChat;
import com.yeling.yelingziblog.chatai.entity.ChatAi;
import com.yeling.yelingziblog.other.entity.Friend;
import com.yeling.yelingziblog.other.entity.Message;
import com.yeling.yelingziblog.talk.entity.Talk;
import com.yeling.yelingziblog.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExcelExportMapper {

    @Select("""
            SELECT id,title,brief,article_cover,category,tag_list,article_url,star_count,like_count,read_count,comment_count,
            user_id,nickname,user_avatar,is_original,original_url,is_top,state,create_time,update_time
             FROM article 
            """)
    List<Article> exportArticle();

    List<Tag> exportArticleTag();

    @Select("""
            SELECT * FROM message
            """)
    List<Message> exportMessage();

    @Select("""
            SELECT * FROM users
            """)
    List<User> exportUser();

    @Select("""
            SELECT * FROM talks
            """)
    List<Talk> exportTalk();

    @Select("""
            SELECT * FROM friend
            """)
    List<Friend> exportFriend();


    @Select("""
            SELECT * FROM album
            """)
    List<Album> exportAlbum();

    @Select("""
            SELECT * FROM photo
            """)
    List<Photo> exportPhoto();

    @Select("""
            SELECT * FROM chat
            """)
    List<Chat> exportChat();

    @Select("""
            SELECT * FROM group_chat
            """)
    List<GroupChat> exportGroupChat();

    @Select("""
            SELECT * FROM chat_ai
            """)
    List<ChatAi> exportAIChat();
}
