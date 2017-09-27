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
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import java.util.function.Consumer;

import io.reactivex.Observable;

import static com.ivianuu.preconditions.Preconditions.checkNotNull;

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
                                            @NonNull String... actions) {
        checkNotNull(context, "context == null");
        checkNotNull(actions, "actions == null");
        IntentFilter intentFilter = new IntentFilter();
        for (String action : actions) {
            intentFilter.addAction(action);
        }

        return create(context, intentFilter);
    }

    /**
     * Emits the received intents of the receiver
     */
    @CheckResult @NonNull
    public static Observable<Intent> create(@NonNull Context context,
                                            @NonNull IntentFilter intentFilter) {
        checkNotNull(context, "context == null");
        checkNotNull(intentFilter, "intentFilter == null");
        return Observable.create(e -> {
            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context __, Intent intent) {
                    if (!e.isDisposed()) {
                        e.onNext(intent);
                    }
                }
            };

            e.setCancellable(() -> context.unregisterReceiver(broadcastReceiver));

            context.registerReceiver(broadcastReceiver, intentFilter);
        });
    }
}
