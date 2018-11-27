import io.vertx.core.Future;
import io.vertx.redis.RedisClient;

import java.util.Objects;

public class Application {
    public static void main(String [ ] args)
    {
        RedisClientConfiguration redisClientConfiguration = new RedisClientConfiguration();

        try {
            redisClientConfiguration.start();

            for (int i = 0; i < 10; i++){
                Future<Void> futuroSetValue = redisClientConfiguration.setValue("" + i, getAlumnoPrueba(i));
                futuroSetValue.setHandler(h -> {
                    if (h.succeeded()){
                        System.out.println("Main. Future Set Alumno Correcto.");
                    } else {
                        System.out.println("Main. Future Set Alumno Fail: "  + h.cause().getMessage());
                    }
                });

            }

            for (int i = 0; i < 11; i++) {
                Future<Alumno> futureGetValue = redisClientConfiguration.getValue("" + i);
                futureGetValue.setHandler(h -> {
                    if (h.succeeded()) {
                        System.out.println("Main. Future Get Alumno Correcto.");
                        if(Objects.nonNull(h.result())){
                            System.out.println(h.result().toString());
                        }
                    } else {
                        System.out.println("Main. Future Set Alumno Fail: " + h.cause().getMessage());
                    }
                });
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
