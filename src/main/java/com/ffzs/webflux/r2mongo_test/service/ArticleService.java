package com.ffzs.webflux.r2mongo_test.service;

import com.ffzs.webflux.r2mongo_test.dao.ArticleDao;
import com.ffzs.webflux.r2mongo_test.models.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author: ffzs
 * @Date: 2020/8/11 上午9:23
 */

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleDao articleDao;

    public Flux<Article> findAll() {
        return articleDao.findAll();
    }

    public Mono<Article> findById(long id) {
        return articleDao.findById(id);
    }

    public Mono<Article> save(Article article) {
        return articleDao.save(article);
    }

    public Mono<Void> update(Article article) {
        return findById(article.getId())
                .map(a -> article.withId(a.getId()))
                .flatMap(articleDao::save)
                .then();
    }

    public Mono<Void> delete(long id) {
        return articleDao.deleteById(id);
    }
}
