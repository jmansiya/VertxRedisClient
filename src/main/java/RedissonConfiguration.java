import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.JsonJacksonMapCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RedissonConfiguration {

    private RedissonClient getClienteRedis(){
        Config configuracionRedisClient = new Config();
        configuracionRedisClient.setCodec(new JsonJacksonCodec(buildObjectMapper()));
        configuracionRedisClient.useSingleServer()
                .setAddress("redis://127.0.0.1:6379");

        RedissonClient cliente = Redisson.create(configuracionRedisClient);

        return  cliente;
    }

    public void setValue(String key, Alumno alumno){
        RedissonClient clienteRedis = null;
        RBucket<Alumno> bucketAlumno = null;

        try {
            clienteRedis = this.getClienteRedis();

            bucketAlumno = clienteRedis.getBucket(key);

            System.out.println("Bucket null: " + Objects.isNull(bucketAlumno));

            bucketAlumno.set(alumno, 2000l, TimeUnit.SECONDS);
            System.out.println("Hemos almacenado el alumno: " + alumno.getNombre());
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            clienteRedis.shutdown();
        }
    }

    public Alumno getValue(String key){
        RedissonClient clienteRedis = this.getClienteRedis();
        Alumno alumno = null;

        try{
            RBucket<Alumno> bucketAlumno = clienteRedis.getBucket(key);
            alumno = bucketAlumno.get();

            if (Objects.nonNull(alumno.getNombre())) {
                System.out.println("Alumno recuperado: " + alumno.toString());
            } else {
                System.out.println("Alumno no ha podido ser recuperado");
                alumno = null;
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            clienteRedis.shutdown();
        }

        return alumno;
    }

    private ObjectMapper buildObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY).withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE).withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        mapper.addMixIn(Throwable.class, JsonJacksonCodec.ThrowableMixIn.class);

        mapper.findAndRegisterModules();

        return mapper;
    }
}
