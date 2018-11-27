import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

import java.util.Objects;

public class RedisClientConfiguration extends AbstractVerticle {
    RedisClient client;

    @Override
    public void stop() throws Exception {
        System.out.println("Closing VERTX");
        vertx.close(h -> {
            if (h.succeeded()){
                System.out.println("Hemos cerrado correctamente la conexión con REDIS. ");
            } else {
                System.out.println("Se ha producido un error al cerrar la conexión con redis: " + h.cause().getMessage());
            }
        });
    }

    @Override
    public void start() throws Exception {
        // If a config file is set, read the host and port.
        vertx = Vertx.vertx();
        // Create the redis client
        client = RedisClient.create(vertx,
                getRedisOptions());

        client.set("key", "value JOSE", r -> {
            if (r.succeeded()) {
                System.out.println("key stored");
                client.get("key", s -> {
                    System.out.println("Retrieved value: " + s.result());

                    System.out.println("Closing Redis connection...");
                    client.close(t -> {
                        if (t.succeeded()){
                            System.out.println("Hemos cerrado correctamente la conexión con REDIS. ");
                        } else {
                            System.out.println("Se ha producido un error al cerrar la conexión con redis: " + t.cause().getMessage());
                        }

                        try {
                            stop();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                });

                System.out.println("Hemos salido.");
            } else {
                System.out.println("Connection or Operation Failed " + r.cause());
            }
        });

        System.out.println("Finalizamos completamente.");
    }

    public Future<Alumno> getValue(String key){
        // If a config file is set, read the host and port.
        vertx = Vertx.vertx();
        // Create the redis client
        client = RedisClient.create(vertx,
                getRedisOptions());

        Future<Alumno> future = Future.future();

        client.getBinary(key, bufferAsyncResult -> {
            if (bufferAsyncResult.succeeded()){
                if(Objects.nonNull(bufferAsyncResult.result())) {
                    future.complete(Json.decodeValue(bufferAsyncResult.result(), Alumno.class));
                } else {
                    future.complete(null);
                }
            } else {
                future.fail(bufferAsyncResult.cause());
            }

            try {
                stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return future;
    }

    public Future<Void> setValue(String key, Alumno alumno){
        // If a config file is set, read the host and port.
        vertx = Vertx.vertx();
        // Create the redis client
        client = RedisClient.create(vertx,
                getRedisOptions());

        Future<Void> future = Future.future();
        Buffer buffer = Json.encodeToBuffer(alumno);

        client.setBinary(key, buffer, bufferAsyncResult -> {
            if (bufferAsyncResult.succeeded()){
                future.complete(bufferAsyncResult.result());
            } else {
                future.fail(bufferAsyncResult.cause());
            }

            try {
                stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return future;
    }

    private RedisOptions getRedisOptions(){
        RedisOptions redisOptions = new RedisOptions();

        redisOptions.setHost("127.0.0.1");
        redisOptions.setPort(6379);

        return redisOptions;
    }

}
