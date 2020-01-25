// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch;

/**
 * Delegate to process a request for retry an operation
 */
@FunctionalInterface
public interface BatchAsyncBatcherRetryDelegate {
    Task invoke(ItemBatchOperation operation);
}