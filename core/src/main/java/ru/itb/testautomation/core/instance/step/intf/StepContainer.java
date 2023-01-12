package ru.itb.testautomation.core.instance.step.intf;

import ru.itb.testautomation.core.common.*;

import java.util.List;

public interface StepContainer extends Named, DescriptionAble, IdAble, AuditAble , SortAble {
    List<Step> getSteps();
}
