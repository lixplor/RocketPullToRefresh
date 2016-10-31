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

package com.lixplor.rocketpulltorefresh.footer;

import android.content.Context;
import android.view.View;

/**
 * Created :  2016-10-30
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
public abstract class AbsFooter {

    protected View mFooter = null;
    protected Context mContext = null;

    public AbsFooter(Context context){
        mContext = context;
    }

    public abstract View getFooter();
    public abstract void changeToPullUp();
    public abstract void changeToRelease();
    public abstract void changeToLoading();
    public abstract void changeToFinish();
    public abstract void reset();
}
