package ru.itb.testautomation.core.dataset.intf;

import ru.itb.testautomation.core.common.DescriptionAble;
import ru.itb.testautomation.core.common.IdAble;
import ru.itb.testautomation.core.common.Named;
import ru.itb.testautomation.core.context.intf.JSONContext;

public interface DataSet extends Named, IdAble, DescriptionAble {
    JSONContext read();
}
