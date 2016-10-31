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

import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created :  2016-10-31
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
public class MockUtil {

    public static List<Integer> genInt(int start, int size) {
        List<Integer> integers = new ArrayList<>();
        for (int i = start; i < size; i++) {
            integers.add(i);
        }
        return integers;
    }

    public static void getData(int startValue, int limit, @NonNull final OnGetDataFinishCallback onGetDataFinishCallback) {
        new AsyncTask<Integer, Void, List<Integer>>() {
            @Override
            protected List<Integer> doInBackground(Integer... params) {
                SystemClock.sleep(2000);
                return genInt(params[0], params[1]);
            }

            @Override
            protected void onPostExecute(List<Integer> integers) {
                onGetDataFinishCallback.onFinish(integers);
            }

            @Override
            protected void onCancelled(List<Integer> integers) {
                onGetDataFinishCallback.onFinish(null);
            }
        }.execute(startValue, limit);
    }

    public interface OnGetDataFinishCallback {
        void onFinish(List<Integer> integers);
    }
}
