package me.jangluzniewicz.webstore.utils.e2e.config;

import java.io.IOException;
import java.util.List;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class ProfilesInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(ConfigurableApplicationContext context) {
    ConfigurableEnvironment env = context.getEnvironment();
    String activeProfiles = env.getProperty("spring.profiles.active");
    if (activeProfiles == null || activeProfiles.isEmpty()) {
      env.setActiveProfiles("test", "local");
      loadResource(env, new String[] {"test", "local"});
    }
  }

  private void loadResource(ConfigurableEnvironment env, String[] profiles) {
    YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
    for (String profile : profiles) {
      Resource resource = new ClassPathResource("application-" + profile + ".yaml");
      try {
        List<PropertySource<?>> sources = loader.load("application-" + profile, resource);
        sources.forEach(env.getPropertySources()::addLast);
      } catch (IOException e) {
        throw new RuntimeException("Failed to load resource: " + resource, e);
      }
    }
  }
}
