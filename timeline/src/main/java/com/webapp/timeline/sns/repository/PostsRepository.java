package com.webapp.timeline.sns.repository;

import com.webapp.timeline.sns.domain.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsRepository extends JpaRepository<Posts, Integer> {

    /*
    * save() 레코드 저장 (insert, update)

    *   findOne() primary key로 레코드 한건 찾기
        findAll()	 전체 레코드 불러오기. 정렬(sort), 페이징(pageable) 가능
        count()	 레코드 갯수
        delete()	 레코드 삭제
     */


}
