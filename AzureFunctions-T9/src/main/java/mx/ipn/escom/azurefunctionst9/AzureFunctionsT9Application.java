package mx.ipn.escom.azurefunctionst9;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;

@SpringBootApplication
public class AzureFunctionsT9Application {

    public static void main(String[] args) {
        SpringApplication.run(AzureFunctionsT9Application.class, args);
    }

    @Bean
    ObjectMapper getMapper(){
        return new ObjectMapper();
    }
}
