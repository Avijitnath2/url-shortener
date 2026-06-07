package com.avijitnath.url_shortener.repository;

import com.avijitnath.url_shortener.entity.ClickEvent;
import com.avijitnath.url_shortener.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClickEventRepository extends JpaRepository<ClickEvent,Long> {

    List<ClickEvent> findByShortUrlOrderByClickedAtDesc(ShortUrl shortUrl);

}
