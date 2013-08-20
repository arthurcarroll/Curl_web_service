/**
 * Copyright 2012 Wordnik, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tireintelligence.rest.example.util;

import com.wordnik.swagger.jaxrs.ApiAuthorizationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;

import javax.ws.rs.core.*;

/**
 *
 * The rules are maintained in simple map with key as path and a boolean value
 * indicating given path is secure or not. For method level security the key is
 * combination of http method and path .
 *
 * If the resource or method is secure then it can only be viewed using a
 * secured api key
 *
 * Note: Objective of this class is not to provide fully functional
 * implementation of authorization filter. This is only a sample demonstration
 * how API authorization filter works.
 *
 */
public class ApiAuthorizationFilterImpl implements ApiAuthorizationFilter {

    static Logger logger = LoggerFactory.getLogger(ApiAuthorizationFilterImpl.class);
    boolean isFilterInitialized = false;
    Map<String, Boolean> methodSecurityAnotations = new HashMap<String, Boolean>();
    Map<String, Boolean> classSecurityAnotations = new HashMap<String, Boolean>();
    String securekeyId = "special-key";
    String unsecurekeyId = "default-key";

    @Override
    public boolean authorize(String apiPath, String method,
            HttpHeaders headers, UriInfo uriInfo) {
        boolean canAccess = true;
        String apiKey = uriInfo.getQueryParameters().getFirst("api_key");
        String mName = method.toUpperCase();
        if (isPathSecure(mName + ":" + apiPath, false)) {
            if (securekeyId.equals(apiKey)) {
                canAccess = true;
            } else {
                canAccess = false;
            }
        }
        return canAccess;
    }

    @Override
    public boolean authorizeResource(String apiPath, HttpHeaders headers, UriInfo uriInfo) {
        boolean canAccess = true;

        String apiKey = uriInfo.getQueryParameters().getFirst("api_key");
        if (isPathSecure(apiPath, true)) {
            if (securekeyId.equals(apiKey)) {
                canAccess = true;
            } else {
                canAccess = false;
            }
        } else {
            canAccess = true;
        }
        return canAccess;
    }

    private boolean isPathSecure(String apiPath, boolean isResource) {
        if (!isFilterInitialized) {
            initialize();
        }
        if (isResource) {
            if (classSecurityAnotations.keySet().contains(apiPath)) {
                return classSecurityAnotations.get(apiPath);
            } else {
                return false;
            }
        } else {
            if (methodSecurityAnotations.keySet().contains(apiPath)) {
                return methodSecurityAnotations.get(apiPath);
            } else {
                return false;
            }
        }
    }

    private void initialize() {
//        //initialize classes (no .format here)
        //classSecurityAnotations.put("/curlClient", false);
    }
}