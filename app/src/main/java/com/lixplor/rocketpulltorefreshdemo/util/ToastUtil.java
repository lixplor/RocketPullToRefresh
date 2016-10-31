/*
 *  Copyright 2016 Lixplor
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.lixplor.rocketpulltorefreshdemo.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created :  2016-10-30
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
public class ToastUtil {

    private static Toast sToast;

    private static synchronized void createToast(Context context){
        if(sToast == null){
            sToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
        }
    }

    public static void shortT(Context context, CharSequence text){
        createToast(context);
        sToast.setDuration(Toast.LENGTH_SHORT);
        sToast.setText(text);
        sToast.show();
    }

    public static void longT(Context context, CharSequence text){
        createToast(context);
        sToast.setDuration(Toast.LENGTH_LONG);
        sToast.setText(text);
        sToast.show();
    }
}
