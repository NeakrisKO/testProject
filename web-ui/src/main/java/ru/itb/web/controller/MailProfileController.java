package ru.itb.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itb.testautomation.core.instance.mail.MailProfile;
import ru.itb.testautomation.core.instance.mail.impl.MailProfileImpl;
import ru.itb.testautomation.core.manager.CoreObjectManager;
import ru.itb.web.model.ui.UIMailProfile;
import ru.itb.web.util.UIObjectConverter;

import java.util.List;

@RestController
@RequestMapping(value = "/mail")
public class MailProfileController {
    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public List<UIMailProfile> getList(){
        return UIObjectConverter.getInstance().convertMailProfiles(CoreObjectManager.getInstance().getManager().getMailProfileList(false));
    }

    @RequestMapping(value = "get/{id}", method = RequestMethod.GET)
    @ResponseBody
    public UIMailProfile getProfile(@PathVariable Integer id) {
        return UIObjectConverter.getInstance().convertMailProfile(CoreObjectManager.getInstance().getManager().getMailProfile(id, false));
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public void createProfile(@RequestBody UIMailProfile uiMailProfile) {
        MailProfile mailProfile = new MailProfileImpl();
        mailProfile.setName(uiMailProfile.getName());
        mailProfile.setDescription(uiMailProfile.getDescription());
        mailProfile.setServer(uiMailProfile.getServer());
        mailProfile.setUser(uiMailProfile.getUser());
        mailProfile.setPassword(uiMailProfile.getPassword());
        CoreObjectManager.getInstance().getManager().addMailProfile(mailProfile);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ResponseEntity<UIMailProfile> updateProfile(@RequestBody UIMailProfile uiMailProfile) {
        MailProfile mailProfile = CoreObjectManager.getInstance().getManager().getMailProfile(uiMailProfile.getId() ,false);
        mailProfile.setName(uiMailProfile.getName());
        mailProfile.setDescription(uiMailProfile.getDescription());
        mailProfile.setServer(uiMailProfile.getServer());
        mailProfile.setUser(uiMailProfile.getUser());
        mailProfile.setPassword(uiMailProfile.getPassword());

        Integer id = CoreObjectManager.getInstance().getManager().updateMailProfile(mailProfile);
        UIMailProfile response = UIObjectConverter.getInstance().convertMailProfile(CoreObjectManager.getInstance().getManager().getMailProfile(id, false));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "remove/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void removeProfile(@PathVariable("id") Integer id) {
        CoreObjectManager.getInstance().getManager().removeMailProfile(id);
    }
}
