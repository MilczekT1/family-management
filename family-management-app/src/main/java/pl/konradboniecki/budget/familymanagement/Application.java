package pl.konradboniecki.budget.familymanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import pl.konradboniecki.chassis.ChassisApplication;

@EnableMongoRepositories
@ChassisApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
