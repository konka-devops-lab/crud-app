# crud-app
### 🔐 Redis SSL Handling in Spring Boot

* In **local development**, use **non-SSL Redis** (default Redis Docker image).
  So set:

  ```properties
  spring.data.redis.ssl.enabled=${REDIS_SSL:false}
  ```

* In **production (like AWS ElastiCache)**, Redis usually uses **SSL**.
  So pass:

  ```bash
  -e REDIS_SSL=true
  ```

* This dynamic setup helps avoid connection errors when switching between local and cloud environments.

✅ Use `${REDIS_SSL:false}` to **default to false** unless overridden.

---

```bash
docker create network crud
```

```bash
docker run -d --name crud-mysql -e MYSQL_ROOT_PASSWORD=siva --network crud crud-mysql:latest
```

```bash
docker run -d --name crud-redis --network crud redis
```

```bash
docker run -d --name crud-backend \
    -e DB_HOST=crud-mysql \
    -e DB_NAME=crud_app \
    -e DB_USER=crud \
    -e DB_PASSWORD=CrudApp@1 \
    -e REDIS_HOST=crud-redis \
    --network crud \
    crud-backend:latest
```

```bash
docker run -d --name crud-frontend \
  -p 80:80 \
  -v $(pwd)/nginx.conf:/etc/nginx/conf.d/default.conf:ro \
  --network crud \
  crud-frontend:latest
```


