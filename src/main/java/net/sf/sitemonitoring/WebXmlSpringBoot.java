package net.sf.sitemonitoring;

import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.MimeMappings;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Slf4j
@Configuration
public class WebXmlSpringBoot extends WebMvcConfigurerAdapter implements EmbeddedServletContainerCustomizer {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("forward:/index.xhtml");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		super.addViewControllers(registry);
	}

	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
		MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
		mappings.add("eot", "application/vnd.ms-fontobject");
		mappings.add("otf", "font/opentype");
		mappings.add("ttf", "application/x-font-ttf");
		mappings.add("woff", "application/x-font-woff");
		mappings.add("svg", "image/svg+xml");
		mappings.add("woff2", "application/x-font-woff2");
		container.setMimeMappings(mappings);
	}
	
	class JsfServletRegistrationBean extends ServletRegistrationBean {

		private boolean dev;

		public JsfServletRegistrationBean(boolean dev) {
			super();
			this.dev = dev;
		}

		@Override
		public void onStartup(ServletContext servletContext) throws ServletException {
			try {
				WebXmlCommon.initialize(servletContext, dev);
			} catch (ServletException ex) {
				log.error("couldn't initialize WebXmlCommon", ex);
			}
		}
	}


	@Bean
	public ServletRegistrationBean facesServletRegistration(Environment environment) {
		boolean dev = false;
		String[] activeProfiles = environment.getActiveProfiles();
		if (Arrays.asList(activeProfiles).contains("dev")) {
			dev = true;
		}
		ServletRegistrationBean servletRegistrationBean = new JsfServletRegistrationBean(dev);
		return servletRegistrationBean;
	}

}