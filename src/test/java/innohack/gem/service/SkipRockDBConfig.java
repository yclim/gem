package innohack.gem.service;

import innohack.gem.dao.GEMFileDao;
import innohack.gem.dao.GEMFileRockDao;
import innohack.gem.dao.GroupDao;
import innohack.gem.dao.GroupRockDao;
import innohack.gem.dao.IGEMFileDao;
import innohack.gem.dao.IGroupDao;
import innohack.gem.dao.IMatchFileDao;
import innohack.gem.dao.MatchFileDao;
import innohack.gem.dao.MatchFileRockDao;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@TestConfiguration
@ComponentScan(
    excludeFilters = {
      @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = GEMFileRockDao.class),
      @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = GroupRockDao.class),
      @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = MatchFileRockDao.class)
    })
public class SkipRockDBConfig {

  @Bean
  public IGroupDao getGroupDao() {
    return new GroupDao();
  }

  @Bean
  public IGEMFileDao getGEMFileDao() {
    return new GEMFileDao();
  }

  @Bean
  public IMatchFileDao getMatchFileDao() {
    return new MatchFileDao();
  }
}
