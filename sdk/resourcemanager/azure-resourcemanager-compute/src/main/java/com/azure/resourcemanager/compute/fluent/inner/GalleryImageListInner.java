// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.compute.fluent.inner;

import com.azure.core.annotation.Fluent;
import com.azure.core.util.logging.ClientLogger;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** The GalleryImageList model. */
@Fluent
public final class GalleryImageListInner {
    @JsonIgnore private final ClientLogger logger = new ClientLogger(GalleryImageListInner.class);

    /*
     * A list of Shared Image Gallery images.
     */
    @JsonProperty(value = "value", required = true)
    private List<GalleryImageInner> value;

    /*
     * The uri to fetch the next page of Image Definitions in the Shared Image
     * Gallery. Call ListNext() with this to fetch the next page of gallery
     * Image Definitions.
     */
    @JsonProperty(value = "nextLink")
    private String nextLink;

    /**
     * Get the value property: A list of Shared Image Gallery images.
     *
     * @return the value value.
     */
    public List<GalleryImageInner> value() {
        return this.value;
    }

    /**
     * Set the value property: A list of Shared Image Gallery images.
     *
     * @param value the value value to set.
     * @return the GalleryImageListInner object itself.
     */
    public GalleryImageListInner withValue(List<GalleryImageInner> value) {
        this.value = value;
        return this;
    }

    /**
     * Get the nextLink property: The uri to fetch the next page of Image Definitions in the Shared Image Gallery. Call
     * ListNext() with this to fetch the next page of gallery Image Definitions.
     *
     * @return the nextLink value.
     */
    public String nextLink() {
        return this.nextLink;
    }

    /**
     * Set the nextLink property: The uri to fetch the next page of Image Definitions in the Shared Image Gallery. Call
     * ListNext() with this to fetch the next page of gallery Image Definitions.
     *
     * @param nextLink the nextLink value to set.
     * @return the GalleryImageListInner object itself.
     */
    public GalleryImageListInner withNextLink(String nextLink) {
        this.nextLink = nextLink;
        return this;
    }

    /**
     * Validates the instance.
     *
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (value() == null) {
            throw logger
                .logExceptionAsError(
                    new IllegalArgumentException("Missing required property value in model GalleryImageListInner"));
        } else {
            value().forEach(e -> e.validate());
        }
    }
}
