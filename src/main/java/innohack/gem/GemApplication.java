package innohack.gem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GemApplication { 

  public static void main(String[] args) {
    System.out.println("test");
    SpringApplication.run(GemApplication.class, args);
  }
}
