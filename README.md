# Spring WebFlux (2): mongodb+springdoc 创建 RESTful api

相较于mysql数据库，mongodb是比较早支持响应式等异步读写的nosql数据库，跟mysql一样，可以使用`ReactiveCrudRepository`快速编写实现crud，而且各层内容都基本相同。整个过程跟上一篇使用mysql相比，只有model使用表不同和配置数据库链接不同之外基本一样，当然mongodb和mysql这两种数据库使用的业务场景不同，mongodb多用来作为文章等的存储数据库，而mysql多用来存储结构性数据。



## 一个示例：

### 使用docker-compose启动mongodb数据库：

```yml
version: '3'
services:
	mongo:
      image: mongo:4.2
      restart: always
      networks:
        - spring
      volumes:
          - ./mongo/db:/data/db  
          - ./monogo/configdb:/data/configdb
      ports:
          - 27017:27017
      environment:
        MONGO_INITDB_ROOT_USERNAME: ffzs
        MONGO_INITDB_ROOT_PASSWORD: 123zxc

    mongo-express:
      networks:
        - spring
      image: mongo-express
      restart: always
      ports:
        - 8081:8081
      environment:
        ME_CONFIG_MONGODB_ENABLE_ADMIN: 'false'
        ME_CONFIG_MONGODB_AUTH_DATABASE: ffzs
        ME_CONFIG_MONGODB_AUTH_USERNAME: ffzs
        ME_CONFIG_MONGODB_AUTH_PASSWORD: 123zxc
        ME_CONFIG_BASICAUTH_USERNAME: ffzs
        ME_CONFIG_BASICAUTH_PASSWORD: 123zxc
```



### 通过gradle管理依赖

```java
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-mongodb-reactive'
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
    implementation 'org.springdoc:springdoc-openapi-webflux-ui:1.4.1'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation('org.springframework.boot:spring-boot-starter-test') {
        exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
    }
    testImplementation 'io.projectreactor:reactor-test'
}
```

### 编写配置

编写mongodb的链接url

```yml
spring:
  data:
    mongodb:
      uri: mongodb://ffzs:123zxc@localhost:27017/ffzs
```

### 编写model

基本跟mysql的一样，不同在于mongodb使用Document指定表，表中有如下内容：

+ id
+ author：作者
+ title：标题
+ text：正文

```java
@Data
@Document(collection = "article")  // mongodb映射表
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
public class Article {

    @Id
    private Long id;
    private String author;
    private String title;
    private String text;
}
```

### 编写dao

直接继承`ReactiveCrudRepository`，少了很多麻烦

```java
import com.ffzs.webflux.r2mongo_test.models.Article;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author: ffzs
 * @Date: 2020/8/11 上午9:22
 */

public interface ArticleDao extends ReactiveCrudRepository<Article, Long> {

}
```

### service编写

实现简单的增删改查

```java
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
```

### controller编写

controller主要注意每一个路由的访问类型不要出现重复，不然的话会冲突报错

```java
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
```

### 测试

数据库链接成功：

![image-20200811114346828](README.assets/image-20200811114346828.png)

swagger访问正常：

![image-20200811114430125](README.assets/image-20200811114430125.png)



## 项目代码

可以自己运行试一下：

github