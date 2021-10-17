package nl.rabobank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@Import(MongoConfiguration.class)
public class RaboAssignmentApplication {
    public static void main(final String[] args)
    {
        SpringApplication.run(RaboAssignmentApplication.class, args);
    }
}
