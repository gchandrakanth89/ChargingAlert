package com.gck.servicelib;

import com.gck.batteryalertlib.App;

/**
 * Created by Pervacio on 13-06-2017.
 */

public class ServiceImpl implements IService {
    @Override
    public void startService() {
        BatteryReceiverService.startService(App.getInstance(), BatteryReceiverService.START_NORMAL);
    }

    @Override
    public void startServiceForSnooze() {
        BatteryReceiverService.startService(App.getInstance(), BatteryReceiverService.START_FOR_SNOOZE);
    }

    @Override
    public void startServiceForDismiss() {
        BatteryReceiverService.startService(App.getInstance(), BatteryReceiverService.START_FOR_DISMISS);
    }
}
