package ru.itb.web.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.itb.web.model.security.UserProfile;
import ru.itb.web.service.impl.UserProfileServiceImpl;
import ru.itb.web.service.intf.UserProfileService;

@Component
public class RoleToUserProfileConverter implements Converter<Object, UserProfile>{

    private static final Logger LOGGER = LogManager.getLogger(RoleToUserProfileConverter.class);

    private UserProfileService userProfileService = new UserProfileServiceImpl();

    public UserProfile convert(Object element) {
        Integer id = Integer.parseInt((String)element);
        UserProfile profile= userProfileService.getById(id);
        LOGGER.info("Profile : {}",profile);
        return profile;
    }

}
