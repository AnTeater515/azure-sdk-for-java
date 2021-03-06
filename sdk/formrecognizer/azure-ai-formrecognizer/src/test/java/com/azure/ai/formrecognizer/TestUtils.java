// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.formrecognizer;

import com.azure.ai.formrecognizer.implementation.Utility;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
import com.azure.core.util.Configuration;
import com.azure.core.util.CoreUtils;
import com.azure.core.util.serializer.JacksonAdapter;
import com.azure.core.util.serializer.SerializerAdapter;
import org.junit.jupiter.params.provider.Arguments;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.azure.core.test.TestBase.AZURE_TEST_SERVICE_VERSIONS_VALUE_ALL;
import static com.azure.core.test.TestBase.getHttpClients;

/**
 * Contains helper methods for generating inputs for test methods
 */
final class TestUtils {
    static final String FAKE_ENCODED_EMPTY_SPACE_URL = "https://fakeuri.com/blank%20space";
    static final String FAILED_TO_DOWNLOAD_IMAGE_CODE = "FailedToDownloadImage";
    static final String FAILED_TO_DOWNLOAD_IMAGE_ERROR_MESSAGE = "Failed to download image from input URL.";
    static final String INVALID_IMAGE_URL_ERROR_CODE = "InvalidImageURL";
    static final String IMAGE_URL_IS_BADLY_FORMATTED_ERROR_MESSAGE = "Image URL is badly formatted.";
    static final String INVALID_MODEL_ID = "a0a3998a-4c4affe66b7";
    static final String INVALID_RECEIPT_URL = "https://invalid.blob.core.windows.net/fr/contoso-allinone.jpg";
    static final String INVALID_KEY = "invalid key";
    static final String INVALID_SOURCE_URL_ERROR = "Status code 400, \"{\"error\":{\"code\":\"1003\","
        + "\"message\":\"Parameter 'Source' is not a valid Uri.\"}}\"";
    static final String INVALID_MODEL_ID_ERROR = "Invalid UUID string: " + INVALID_MODEL_ID;
    static final String NON_EXIST_MODEL_ID = "00000000-0000-0000-0000-000000000000";
    static final String NULL_SOURCE_URL_ERROR = "'trainingFilesUrl' cannot be null.";
    static final String INVALID_URL = "htttttttps://localhost:8080";
    static final String VALID_HTTPS_LOCALHOST = "https://localhost:8080";
    static final String VALID_HTTP_LOCALHOST = "http://localhost:8080";
    static final String RECEIPT_LOCAL_URL = "src/test/resources/sample_files/Test/contoso-allinone.jpg";
    static final String RECEIPT_LOCAL_PNG_URL = "src/test/resources/sample_files/Test/contoso-receipt.png";
    static final String LAYOUT_LOCAL_URL = "src/test/resources/sample_files/Test/layout1.jpg";
    static final String FORM_LOCAL_URL = "src/test/resources/sample_files/Test/Invoice_6.pdf";
    static final String FORM_1_JPG_LOCAL_URL = "src/test/resources/sample_files/Test/Form_1.jpg";
    static final String BLANK_FORM_LOCAL_URL = "src/test/resources/sample_files/Test/blank.pdf";
    static final String MULTIPAGE_INVOICE_LOCAL_URL = "src/test/resources/sample_files/Test/multipage_invoice1.pdf";
    static final long RECEIPT_FILE_LENGTH = new File(RECEIPT_LOCAL_URL).length();
    static final long RECEIPT_PNG_FILE_LENGTH = new File(RECEIPT_LOCAL_PNG_URL).length();
    static final long LAYOUT_FILE_LENGTH = new File(LAYOUT_LOCAL_URL).length();
    static final long CUSTOM_FORM_FILE_LENGTH = new File(FORM_LOCAL_URL).length();
    static final long FORM_1_JPG_FILE_LENGTH = new File(FORM_1_JPG_LOCAL_URL).length();
    static final long BLANK_FORM_FILE_LENGTH = new File(BLANK_FORM_LOCAL_URL).length();
    static final long MULTIPAGE_INVOICE_FILE_LENGTH = new File(MULTIPAGE_INVOICE_LOCAL_URL).length();
    static final String VALID_URL = "https://resources/contoso-allinone.jpg";
    static final String DISPLAY_NAME_WITH_ARGUMENTS = "{displayName} with [{arguments}]";
    private static final String AZURE_FORM_RECOGNIZER_TEST_SERVICE_VERSIONS =
        "AZURE_FORM_RECOGNIZER_TEST_SERVICE_VERSIONS";

    static final String BLANK_PDF = "blank.pdf";
    static final String FORM_JPG = "Form_1.jpg";
    static final String INVOICE_1_PDF = "Invoice_1.pdf";
    static final String TEST_DATA_PNG = "testData.png";

    static final Duration ONE_NANO_DURATION = Duration.ofNanos(1);
    static final Duration DEFAULT_DURATION = Duration.ofSeconds(5);

    private TestUtils() {
    }

    static InputStream getFileData(String fileName) {
        final HttpClient httpClient = new NettyAsyncHttpClientBuilder().build();
        final HttpResponse httpResponse =
            httpClient.send(new HttpRequest(HttpMethod.GET, fileName)).block();

        if (httpResponse == null) {
            return new ByteArrayInputStream(new byte[]{});
        }

        final Mono<byte[]> bodyAsByteArrayMono = httpResponse.getBodyAsByteArray();
        if (bodyAsByteArrayMono == null) {
            return new ByteArrayInputStream(new byte[]{});
        }

        final byte[] bodyAsByteArray = bodyAsByteArrayMono.block();
        if (bodyAsByteArray == null) {
            return new ByteArrayInputStream(new byte[]{});
        }
        return new ByteArrayInputStream(bodyAsByteArray);
    }

    static InputStream getContentDetectionFileData(String localFileUrl) {
        try {
            return new FileInputStream(localFileUrl);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Local file not found.", e);
        }
    }

    static Flux<ByteBuffer> getReplayableBufferData(String localFileUrl) {
        Mono<InputStream> dataMono = Mono.defer(() -> {
            try {
                return Mono.just(new FileInputStream(localFileUrl));
            } catch (FileNotFoundException e) {
                return Mono.error(new RuntimeException("Local file not found.", e));
            }
        });
        return dataMono.flatMapMany(new Function<InputStream, Flux<ByteBuffer>>() {
            @Override
            public Flux<ByteBuffer> apply(InputStream inputStream) {
                return Utility.toFluxByteBuffer(inputStream);
            }
        });
    }

    static SerializerAdapter getSerializerAdapter() {
        return JacksonAdapter.createDefaultSerializerAdapter();
    }

    /**
     * Returns a stream of arguments that includes all combinations of eligible {@link HttpClient HttpClients} and
     * service versions that should be tested.
     *
     * @return A stream of HttpClient and service version combinations to test.
     */
    static Stream<Arguments> getTestParameters() {
        // when this issues is closed, the newer version of junit will have better support for
        // cartesian product of arguments - https://github.com/junit-team/junit5/issues/1427
        List<Arguments> argumentsList = new ArrayList<>();
        getHttpClients()
            .forEach(httpClient -> {
                Arrays.stream(FormRecognizerServiceVersion.values()).filter(
                    TestUtils::shouldServiceVersionBeTested)
                    .forEach(serviceVersion -> argumentsList.add(Arguments.of(httpClient, serviceVersion)));
            });
        return argumentsList.stream();
    }

    /**
     * Returns whether the given service version match the rules of test framework.
     *
     * <ul>
     * <li>Using latest service version as default if no environment variable is set.</li>
     * <li>If it's set to ALL, all Service versions in {@link FormRecognizerServiceVersion} will be tested.</li>
     * <li>Otherwise, Service version string should match env variable.</li>
     * </ul>
     *
     * Environment values currently supported are: "ALL", "${version}".
     * Use comma to separate http clients want to test.
     * e.g. {@code set AZURE_TEST_SERVICE_VERSIONS = V1_0, V2_0}
     *
     * @param serviceVersion ServiceVersion needs to check
     * @return Boolean indicates whether filters out the service version or not.
     */
    private static boolean shouldServiceVersionBeTested(FormRecognizerServiceVersion serviceVersion) {
        String serviceVersionFromEnv =
            Configuration.getGlobalConfiguration().get(AZURE_FORM_RECOGNIZER_TEST_SERVICE_VERSIONS);
        if (CoreUtils.isNullOrEmpty(serviceVersionFromEnv)) {
            return FormRecognizerServiceVersion.getLatest().equals(serviceVersion);
        }
        if (AZURE_TEST_SERVICE_VERSIONS_VALUE_ALL.equalsIgnoreCase(serviceVersionFromEnv)) {
            return true;
        }
        String[] configuredServiceVersionList = serviceVersionFromEnv.split(",");
        return Arrays.stream(configuredServiceVersionList).anyMatch(configuredServiceVersion ->
            serviceVersion.getVersion().equals(configuredServiceVersion.trim()));
    }
}

