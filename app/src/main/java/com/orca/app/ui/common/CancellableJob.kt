package com.orca.app.ui.common

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class CancellableJob {
    private var job: Job? = null

    fun launch(
        scope: CoroutineScope,
        onCancel: () -> Unit = {},
        block: suspend CoroutineScope.() -> Unit,
    ) {
        job?.cancel()
        job = scope.launch {
            try {
                block()
            } catch (_: CancellationException) {
                onCancel()
            }
        }
    }

    fun cancel() {
        job?.cancel()
    }
}
