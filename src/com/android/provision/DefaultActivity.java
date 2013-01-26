/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.provision;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;

/**
 * Application that sets the provisioned bit, like SetupWizard does.
 */
public class DefaultActivity extends Activity {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Add a persistent setting to allow other apps to know the device has been provisioned.
        Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);

        // Add a persistent setting to allow other apps to know the user has complete the
        // setup wizard. This value is used by some components of the system (like QuickSettings
        // or KeyguardSelector). This flag need to be set, before the provisioned bit was set.
        // The key is hidden in aosp, so use reflection to obtain his actual key identifier.
        String userSetupCompleteKey = getUserSetupCompleteKey();
        if (userSetupCompleteKey != null) {
            Settings.Secure.putInt(getContentResolver(), userSetupCompleteKey, 1);
        }

        // remove this activity from the package manager.
        PackageManager pm = getPackageManager();
        ComponentName name = new ComponentName(this, DefaultActivity.class);
        pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        // terminate the activity.
        finish();
    }

    private String getUserSetupCompleteKey() {
        try {
            return (String)Settings.Secure.class.getDeclaredField("USER_SETUP_COMPLETE").get(null);
        } catch (Exception ex) {
            // Ignore
        }
        return null;
    }
}

