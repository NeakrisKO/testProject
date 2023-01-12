package ru.itb.web.converter;

import ru.itb.testautomation.core.utils.Converter;
import ru.itb.testautomation.core.utils.ObjectUpdater;
import ru.itb.web.model.security.UserProfile;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UIUserProfileConverter implements Converter {
    @Override
    public Collection<?> convert(Collection<?> data) {
        Set<UserProfile> result = new HashSet<>();
        for(Object otherNestedObject : data) {
            UserProfile userProfile = new UserProfile();
            ObjectUpdater updater = new ObjectUpdater();
            updater.update(userProfile, otherNestedObject);
            result.add(userProfile);
        }
        return result;
    }
}
