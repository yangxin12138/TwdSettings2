package com.twd.setting.module.systemequipment.model;

import com.twd.setting.widgets.RatioLineCustomView;
import com.twd.setting.widgets.RatioLineCustomView.ItemData;

import java.util.List;

public class StorageInfoData {
    public String freeSizeStr;
    public int iconRes;
    public boolean isSystemData;
    public List<RatioLineCustomView.ItemData> itemDataList;
    public String nameStr;
    public String totalSizeStr;

    public StorageInfoData(boolean _isSystemData, String _nameStr, int _iconRes, String _totalSizeStr, String _freeSizeStr, List<RatioLineCustomView.ItemData> _itemDataList) {
        isSystemData = _isSystemData;
        nameStr = _nameStr;
        iconRes = _iconRes;
        totalSizeStr = _totalSizeStr;
        freeSizeStr = _freeSizeStr;
        itemDataList = _itemDataList;
    }
}

