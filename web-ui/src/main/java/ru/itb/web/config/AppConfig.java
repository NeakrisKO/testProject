package ru.itb.web.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import ru.itb.testautomation.core.manager.CoreObjectManager;
import ru.itb.testautomation.core.manager.ExcelDataSetListManager;
import ru.itb.testautomation.core.manager.SystemPreferenceManager;
import ru.itb.web.converter.RoleToUserProfileConverter;
import ru.itb.web.util.SVNVersionUtils;

import static ru.itb.web.config.constants.TAPConsts.SVN_LOGIN;
import static ru.itb.web.config.constants.TAPConsts.SVN_PASSWORD;
import static ru.itb.web.config.constants.TAPConsts.SVN_PATH;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ru.itb.web")
public class AppConfig extends WebMvcConfigurerAdapter {
    private static final Logger LOGGER = LogManager.getLogger(AppConfig.class);
    private RoleToUserProfileConverter roleToUserProfileConverter = new RoleToUserProfileConverter();

    public AppConfig() {
        try {
            CoreObjectManager.getInstance().init();
            LOGGER.info("Initialize CoreObject Manager");
        } catch (Exception exc) {
            LOGGER.error("Error initialization object core object manager", exc);
        }
        initDataSetManager();
        collectVersionInfo();
    }

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        final CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(1000000);
        return multipartResolver;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/view/");
        viewResolver.setSuffix(".jsp");
        registry.viewResolver(viewResolver);
    }

    /**
     * Configure ResourceHandlers to serve static resources like CSS/ Javascript etc...
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("/static/");
    }

    /**
     * Configure Converter to be used.
     * In our example, we need a converter to convert string values[Roles] to UserProfiles in newUser.jsp
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(roleToUserProfileConverter);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer matcher) {
        matcher.setUseRegisteredSuffixPatternMatch(true);
    }

    private void initDataSetManager() {
        try {
            LOGGER.info("Initialize DataSet Manager");
            ExcelDataSetListManager.getInstance().loadDataSet(SystemPreferenceManager.getInstance().getPreference().get("dataset_path"));
            ExcelDataSetListManager.getInstance().get();
        } catch (Exception exc) {
            LOGGER.error("Can't read DataSet path - check TAP preference", exc);
        }
    }

    private void collectVersionInfo() {
        String svnRepository = SystemPreferenceManager.getInstance().getPreference(SVN_PATH);
        String svnLogin = SystemPreferenceManager.getInstance().getPreference(SVN_LOGIN);
        String svnPassword = SystemPreferenceManager.getInstance().getPreference(SVN_PASSWORD);
        try {
            LOGGER.info("Initialize SVNVersionUtils Manager");
            SVNVersionUtils.getInstance().collectInfo(svnRepository,svnLogin, svnPassword);
        } catch (Exception exc) {
            LOGGER.error("Can't collect info from: "+ svnRepository , exc);
        }

    }
}