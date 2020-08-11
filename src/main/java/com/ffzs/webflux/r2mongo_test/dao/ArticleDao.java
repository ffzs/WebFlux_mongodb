package com.ffzs.webflux.r2mongo_test.dao;

import com.ffzs.webflux.r2mongo_test.models.Article;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author: ffzs
 * @Date: 2020/8/11 上午9:22
 */

public interface ArticleDao extends ReactiveCrudRepository<Article, Long> {

}
