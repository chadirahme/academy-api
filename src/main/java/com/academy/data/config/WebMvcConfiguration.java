package com.academy.data.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.AppCacheManifestTransformer;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
//@ComponentScan
public class WebMvcConfiguration implements WebMvcConfigurer {

//    @Bean
//    public ViewResolver beanNameViewResolver() {
//        BeanNameViewResolver resolver = new BeanNameViewResolver();
//        return resolver;
//    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**").setCachePeriod(7200).addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new VersionResourceResolver()
                        //.addFixedVersionStrategy(this.version, "/**/*.js")
                        .addContentVersionStrategy("/**"))
                .addTransformer(new AppCacheManifestTransformer());
    }

    @Bean
    public ViewResolver jspViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("classpath:/static/jasper/");
        viewResolver.setSuffix(".jrxml");
        //viewResolver.setReportDataKey("datasource");
        viewResolver.setViewNames("rpt_*");
        //viewResolver.setViewClass(JasperReportsMultiFormatView.class);
        viewResolver.setOrder(0);
        return viewResolver;
    }

//    @Bean
//    public JasperReportsViewResolver getJasperReportsViewResolver() {
//        JasperReportsViewResolver resolver = new JasperReportsViewResolver();
//        resolver.setPrefix("classpath:/static/jasper/");
//        resolver.setSuffix(".jrxml");
//        resolver.setReportDataKey("datasource");
//        resolver.setViewNames("rpt_*");
//        resolver.setViewClass(JasperReportsMultiFormatView.class);
//        resolver.setOrder(0);
//        return resolver;
//    }

}
