package innohack.gem.web;

import innohack.gem.service.SkipRockDBConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@ComponentScan
public class ControllerConfig extends SkipRockDBConfig {

}
