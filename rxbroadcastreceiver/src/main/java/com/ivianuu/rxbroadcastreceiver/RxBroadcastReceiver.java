/*
 * Copyright 2017 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.rxbroadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Observable;

import static com.ivianuu.rxbroadcastreceiver.Preconditions.checkNotNull;

/**
 * Static factory methods to create observables from broadcast receivers
 */
public final class RxBroadcastReceiver {

    private RxBroadcastReceiver() {
        // no instances
    }

    /**
     * Emits the received intents of the receiver
     */
    @CheckResult @NonNull
    public static Observable<Intent> create(@NonNull Context context,
                                            @NonNull IntentFilter intentFilter) {
        return create(context, intentFilter, null);
    }

    /**
     * Emits the received intents of the receiver
     */
    @CheckResult @NonNull
    public static Observable<Intent> create(@NonNull Context context,
                                            @NonNull IntentFilter intentFilter,
                                            @Nullable String broadcastPermission) {
        return create(context, intentFilter, broadcastPermission, null);
    }

    /**
     * Emits the received intents of the receiver
     */
    @CheckResult @NonNull
    public static Observable<Intent> create(@NonNull Context context,
                                            @NonNull IntentFilter intentFilter,
                                            @Nullable String broadcastPermission,
                                            @Nullable Handler schedulerHandler) {
        checkNotNull(context, "context == null");
        checkNotNull(intentFilter, "intentFilter == null");
        return Observable.create(e -> {
            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context __, Intent intent) {
                    e.onNext(intent);
                }
            };

            e.setCancellable(() -> context.unregisterReceiver(broadcastReceiver));

            context.registerReceiver(
                    broadcastReceiver, intentFilter, broadcastPermission, schedulerHandler);
        });
    }
}
