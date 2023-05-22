package com.example.gazi.repository;

import com.example.gazi.domain.Member;
import com.example.gazi.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.latitude >= :minLat AND p.longitude >= :minLon AND p.latitude <= :maxLat AND p.longitude <= :maxLon")
    Page<Post> findAllByLocation(@Param("minLat") Double minLat, @Param("minLon") Double minLon, @Param("maxLat") Double maxLat, @Param("maxLon") Double maxLon, Pageable pageable);

    @Transactional
    Page<Post> findAllByMember(Member member, Pageable pageable);
}
