package com.ffzs.webflux.r2mongo_test.Controller;

import com.ffzs.webflux.r2mongo_test.models.Article;
import com.ffzs.webflux.r2mongo_test.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author: ffzs
 * @Date: 2020/8/11 上午9:35
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("article")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("findAll")
    @ResponseStatus(HttpStatus.FOUND)
    public Flux<Article> findAll() {
        return articleService.findAll();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.FOUND)
    public Mono<Article> findById(@RequestParam("id") long id) {
        return articleService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Article> save(@RequestBody Article article) {
        return articleService.save(article);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> update(@RequestBody Article article) {
        return articleService.update(article);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> update(@RequestParam("id") long id) {
        return articleService.delete(id);
    }
}
