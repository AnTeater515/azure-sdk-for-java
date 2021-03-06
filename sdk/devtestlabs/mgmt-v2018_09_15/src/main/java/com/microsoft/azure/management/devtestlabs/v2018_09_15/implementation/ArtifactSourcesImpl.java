/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 *
 */

package com.microsoft.azure.management.devtestlabs.v2018_09_15.implementation;

import com.microsoft.azure.arm.model.implementation.WrapperImpl;
import com.microsoft.azure.management.devtestlabs.v2018_09_15.ArtifactSources;
import rx.Completable;
import rx.Observable;
import rx.functions.Func1;
import com.microsoft.azure.Page;
import com.microsoft.azure.management.devtestlabs.v2018_09_15.ArtifactSource;

class ArtifactSourcesImpl extends WrapperImpl<ArtifactSourcesInner> implements ArtifactSources {
    private final DevTestLabsManager manager;

    ArtifactSourcesImpl(DevTestLabsManager manager) {
        super(manager.inner().artifactSources());
        this.manager = manager;
    }

    public DevTestLabsManager manager() {
        return this.manager;
    }

    @Override
    public ArtifactSourceImpl define(String name) {
        return wrapModel(name);
    }

    private ArtifactSourceImpl wrapModel(ArtifactSourceInner inner) {
        return  new ArtifactSourceImpl(inner, manager());
    }

    private ArtifactSourceImpl wrapModel(String name) {
        return new ArtifactSourceImpl(name, this.manager());
    }

    @Override
    public Observable<ArtifactSource> listAsync(final String resourceGroupName, final String labName) {
        ArtifactSourcesInner client = this.inner();
        return client.listAsync(resourceGroupName, labName)
        .flatMapIterable(new Func1<Page<ArtifactSourceInner>, Iterable<ArtifactSourceInner>>() {
            @Override
            public Iterable<ArtifactSourceInner> call(Page<ArtifactSourceInner> page) {
                return page.items();
            }
        })
        .map(new Func1<ArtifactSourceInner, ArtifactSource>() {
            @Override
            public ArtifactSource call(ArtifactSourceInner inner) {
                return wrapModel(inner);
            }
        });
    }

    @Override
    public Observable<ArtifactSource> getAsync(String resourceGroupName, String labName, String name) {
        ArtifactSourcesInner client = this.inner();
        return client.getAsync(resourceGroupName, labName, name)
        .flatMap(new Func1<ArtifactSourceInner, Observable<ArtifactSource>>() {
            @Override
            public Observable<ArtifactSource> call(ArtifactSourceInner inner) {
                if (inner == null) {
                    return Observable.empty();
                } else {
                    return Observable.just((ArtifactSource)wrapModel(inner));
                }
            }
       });
    }

    @Override
    public Completable deleteAsync(String resourceGroupName, String labName, String name) {
        ArtifactSourcesInner client = this.inner();
        return client.deleteAsync(resourceGroupName, labName, name).toCompletable();
    }

}
