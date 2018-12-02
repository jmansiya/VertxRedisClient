import io.vertx.core.Future;
import io.vertx.redis.RedisClient;
import org.redisson.api.RedissonClient;

import java.util.Objects;

public class Application {
    public static void main(String [ ] args)
    {

        try {
            //Tratamos de recuperar el alumno anterior de Vertx con el cliente Reddison.
            RedissonConfiguration redissonConfiguration =  new RedissonConfiguration();

            for(int i = 0; i < 10; i++) {
                redissonConfiguration.setValue(""+i, getAlumnoPrueba(i));
            }

            for (int i = 0; i < 10; i++) {
                Alumno alumnoConRedison = redissonConfiguration.getValue("" + i);
                if (Objects.nonNull(alumnoConRedison)) {
                    System.out.println("Alumno recuperado [" + i + "] " + alumnoConRedison.toString());
                } else {
                    System.out.println("Alumno no recuperado [" + i + "] ");
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    private static  Alumno getAlumnoPrueba(int i){
        Alumno alumno = new Alumno();

        alumno.setNombre("Jose");
        alumno.setApellidos("Mansilla Garcia-Gil");
        alumno.setEdad(i);

        return alumno;
    }

}
