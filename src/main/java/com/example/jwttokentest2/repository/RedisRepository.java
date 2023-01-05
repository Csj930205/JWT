package com.example.jwttokentest2.repository;

import com.example.jwttokentest2.entity.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisRepository extends CrudRepository<Token, String> {
    Token findByKey(String key);
}
