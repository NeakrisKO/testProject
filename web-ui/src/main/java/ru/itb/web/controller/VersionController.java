package ru.itb.web.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.itb.web.model.ui.version.VersionInfo;
import ru.itb.web.util.SVNVersionUtils;

@RestController
@RequestMapping(value = "/version")
public class VersionController {

    @RequestMapping(value = "version", method = RequestMethod.GET)
    @ResponseBody
    public VersionInfo version() {
        return SVNVersionUtils.getInstance().getVersionInfo();
    }
}
