package com.example.gazi.repository;

import com.example.gazi.domain.PostCart;
import com.example.gazi.domain.Repost;
import com.example.gazi.domain.RepostCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepostCartRepository extends JpaRepository<RepostCart, Long> {
    RepostCart findByRepost(Repost repost);
}
