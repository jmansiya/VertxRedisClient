import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.client.codec.JsonJacksonMapCodec;

public class ExtendedJsonJacksonMapCodec extends JsonJacksonMapCodec {

    public ExtendedJsonJacksonMapCodec() {
        super(String.class, Alumno.class);
    }

    @Override
    protected void init(ObjectMapper objectMapper) {
        super.init(objectMapper);
    }

}
