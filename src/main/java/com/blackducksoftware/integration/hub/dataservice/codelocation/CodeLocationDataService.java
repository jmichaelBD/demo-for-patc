/**
 * hub-common
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.dataservice.codelocation;

import static com.blackducksoftware.integration.hub.api.UrlConstants.SEGMENT_API;
import static com.blackducksoftware.integration.hub.api.UrlConstants.SEGMENT_BOM_IMPORT;
import static com.blackducksoftware.integration.hub.api.UrlConstants.SEGMENT_CODE_LOCATIONS;
import static com.blackducksoftware.integration.hub.api.UrlConstants.SEGMENT_SCAN_SUMMARIES;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.generated.enumeration.CodeLocationType;
import com.blackducksoftware.integration.hub.api.generated.view.CodeLocationView;
import com.blackducksoftware.integration.hub.api.generated.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.api.view.ScanSummaryView;
import com.blackducksoftware.integration.hub.exception.DoesNotExistException;
import com.blackducksoftware.integration.hub.request.HubPagedRequest;
import com.blackducksoftware.integration.hub.request.HubRequest;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.service.HubService;

import okhttp3.Response;

public class CodeLocationDataService extends HubService {
    public CodeLocationDataService(final RestConnection restConnection) {
        super(restConnection);
    }

    public void importBomFile(final File file) throws IntegrationException {
        importBomFile(file, "application/ld+json");
    }

    public void importBomFile(final File file, final String mediaType) throws IntegrationException {
        String jsonPayload;
        try {
            jsonPayload = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new IntegrationException("Failed to import Bom file: " + file.getAbsolutePath() + " to the Hub because : " + e.getMessage(), e);
        }
        final HubRequest hubRequest = getHubRequestFactory().createRequest(Arrays.asList(SEGMENT_API, SEGMENT_BOM_IMPORT));
        try (Response response = hubRequest.executePost(mediaType, jsonPayload)) {
        }
    }

    public List<CodeLocationView> getAllCodeLocationsForCodeLocationType(final CodeLocationType codeLocationType) throws IntegrationException {
        final HubPagedRequest hubPagedRequest = getHubRequestFactory().createPagedRequest(Arrays.asList(SEGMENT_API, SEGMENT_CODE_LOCATIONS)).addQueryParameter("codeLocationType", codeLocationType.toString());
        final List<CodeLocationView> allCodeLocations = getAllResponses(hubPagedRequest, CodeLocationView.class);
        return allCodeLocations;
    }

    public void unmapCodeLocations(final List<CodeLocationView> codeLocationItems) throws IntegrationException {
        for (final CodeLocationView codeLocationItem : codeLocationItems) {
            unmapCodeLocation(codeLocationItem);
        }
    }

    public void unmapCodeLocation(final CodeLocationView codeLocationItem) throws IntegrationException {
        final String codeLocationItemUrl = getHref(codeLocationItem);
        final CodeLocationView requestCodeLocationView = createRequestCodeLocationView(codeLocationItem, "");
        updateCodeLocation(codeLocationItemUrl, getGson().toJson(requestCodeLocationView));
    }

    public void mapCodeLocation(final CodeLocationView codeLocationItem, final ProjectVersionView version) throws IntegrationException {
        mapCodeLocation(codeLocationItem, getHref(version));
    }

    public void mapCodeLocation(final CodeLocationView codeLocationItem, final String versionUrl) throws IntegrationException {
        final String codeLocationItemUrl = getHref(codeLocationItem);
        final CodeLocationView requestCodeLocationView = createRequestCodeLocationView(codeLocationItem, versionUrl);
        updateCodeLocation(codeLocationItemUrl, getGson().toJson(requestCodeLocationView));
    }

    public void updateCodeLocation(final CodeLocationView codeLocationItem) throws IntegrationException {
        final String codeLocationItemUrl = getHref(codeLocationItem);
        updateCodeLocation(codeLocationItemUrl, getGson().toJson(codeLocationItem));
    }

    public void updateCodeLocation(final String codeLocationItemUrl, final String codeLocationItemJson) throws IntegrationException {
        final HubRequest request = getHubRequestFactory().createRequest(codeLocationItemUrl);
        Response response = null;
        try {
            response = request.executePut(codeLocationItemJson);
        } finally {
            if (response != null) {
                response.close();
            }
        }

    }

    public void deleteCodeLocations(final List<CodeLocationView> codeLocationItems) throws IntegrationException {
        for (final CodeLocationView codeLocationItem : codeLocationItems) {
            deleteCodeLocation(codeLocationItem);
        }
    }

    public void deleteCodeLocation(final CodeLocationView codeLocationItem) throws IntegrationException {
        final String codeLocationItemUrl = getHref(codeLocationItem);
        deleteCodeLocation(codeLocationItemUrl);
    }

    public void deleteCodeLocation(final String codeLocationItemUrl) throws IntegrationException {
        final HubRequest request = getHubRequestFactory().createRequest(codeLocationItemUrl);
        request.executeDelete();
    }

    public CodeLocationView getCodeLocationByName(final String codeLocationName) throws IntegrationException {
        if (StringUtils.isNotBlank(codeLocationName)) {
            final HubPagedRequest hubPagedRequest = getHubRequestFactory().createPagedRequest(Arrays.asList(SEGMENT_API, SEGMENT_CODE_LOCATIONS));
            hubPagedRequest.q = "name:" + codeLocationName;
            final List<CodeLocationView> codeLocations = getAllResponses(hubPagedRequest, CodeLocationView.class);
            for (final CodeLocationView codeLocation : codeLocations) {
                if (codeLocationName.equals(codeLocation.name)) {
                    return codeLocation;
                }
            }
        }

        throw new DoesNotExistException("This Code Location does not exist. Code Location: " + codeLocationName);
    }

    public CodeLocationView getCodeLocationById(final String codeLocationId) throws IntegrationException {
        final List<String> segments = new ArrayList<>(Arrays.asList(SEGMENT_API, SEGMENT_CODE_LOCATIONS));
        segments.add(codeLocationId);
        final HubRequest request = getHubRequestFactory().createRequest(segments);
        return getResponse(request, CodeLocationView.class);
    }

    private CodeLocationView createRequestCodeLocationView(final CodeLocationView codeLocationItem, final String versionUrl) {
        final CodeLocationView requestCodeLocationView = new CodeLocationView();
        requestCodeLocationView.createdAt = codeLocationItem.createdAt;
        requestCodeLocationView.mappedProjectVersion = versionUrl;
        requestCodeLocationView.name = codeLocationItem.name;
        requestCodeLocationView.type = codeLocationItem.type;
        requestCodeLocationView.updatedAt = codeLocationItem.updatedAt;
        requestCodeLocationView.url = codeLocationItem.url;
        return requestCodeLocationView;
    }

    public ScanSummaryView getScanSummaryViewById(final String scanSummaryId) throws IntegrationException {
        final List<String> segments = Arrays.asList(SEGMENT_API, SEGMENT_SCAN_SUMMARIES);
        segments.add(scanSummaryId);
        final HubRequest request = getHubRequestFactory().createRequest(segments);
        return getResponse(request, ScanSummaryView.class);
    }
}
