package com.avijitnath.url_shortener.repository;

import com.avijitnath.url_shortener.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<ShortUrl,Long> {

    Optional<ShortUrl> findByShortCode(String code);

}
