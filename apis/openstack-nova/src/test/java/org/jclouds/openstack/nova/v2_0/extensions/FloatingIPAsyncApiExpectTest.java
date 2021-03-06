/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaAsyncApi;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaAsyncApiExpectTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseFloatingIPListTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseFloatingIPTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests annotation parsing of {@code FloatingIPAsyncApi}
 * 
 * @author Michael Arnold
 */
@Test(groups = "unit", testName = "FloatingIPAsyncApiExpectTest")
public class FloatingIPAsyncApiExpectTest extends BaseNovaAsyncApiExpectTest {

   public void testWhenNamespaceInExtensionsListFloatingIpPresent() throws Exception {

      NovaAsyncApi apiWhenExtensionNotInList = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse);

      assertEquals(apiWhenExtensionNotInList.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertTrue(apiWhenExtensionNotInList.getFloatingIPExtensionForZone("az-1.region-a.geo-1").isPresent());

   }
   
   public void testWhenNamespaceNotInExtensionsListFloatingIpNotPresent() throws Exception {

      NovaAsyncApi apiWhenExtensionNotInList = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, unmatchedExtensionsOfNovaResponse);

      assertEquals(apiWhenExtensionNotInList.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertFalse(apiWhenExtensionNotInList.getFloatingIPExtensionForZone("az-1.region-a.geo-1").isPresent());

   }

   public void testListFloatingIPsWhenResponseIs2xx() throws Exception {
      HttpRequest listFloatingIPs = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listFloatingIPsResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/floatingip_list.json")).build();

      NovaAsyncApi apiWhenFloatingIPsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, listFloatingIPs, listFloatingIPsResponse);

      assertEquals(apiWhenFloatingIPsExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(apiWhenFloatingIPsExist.getFloatingIPExtensionForZone("az-1.region-a.geo-1").get().listFloatingIPs().get()
            .toString(), new ParseFloatingIPListTest().expected().toString());
   }

   public void testListFloatingIPsWhenResponseIs404() throws Exception {
      HttpRequest listFloatingIPs = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listFloatingIPsResponse = HttpResponse.builder().statusCode(404).build();

      NovaAsyncApi apiWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, listFloatingIPs, listFloatingIPsResponse);

      assertTrue(apiWhenNoServersExist.getFloatingIPExtensionForZone("az-1.region-a.geo-1").get().listFloatingIPs().get()
            .isEmpty());
   }

   public void testGetFloatingIPWhenResponseIs2xx() throws Exception {
      HttpRequest getFloatingIP = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips/1")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse getFloatingIPResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/floatingip_details.json")).build();

      NovaAsyncApi apiWhenFloatingIPsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, getFloatingIP, getFloatingIPResponse);

      assertEquals(apiWhenFloatingIPsExist.getFloatingIPExtensionForZone("az-1.region-a.geo-1").get().getFloatingIP("1").get()
            .toString(), new ParseFloatingIPTest().expected().toString());
   }

   public void testGetFloatingIPWhenResponseIs404() throws Exception {
      HttpRequest getFloatingIP = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips/1")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse getFloatingIPResponse = HttpResponse.builder().statusCode(404).build();

      NovaAsyncApi apiWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, getFloatingIP, getFloatingIPResponse);

      assertNull(apiWhenNoServersExist.getFloatingIPExtensionForZone("az-1.region-a.geo-1").get().getFloatingIP("1").get());
   }

   public void testAllocateWhenResponseIs2xx() throws Exception {
      HttpRequest allocateFloatingIP = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType("{}", "application/json")).build();

      HttpResponse allocateFloatingIPResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/floatingip_details.json")).build();

      NovaAsyncApi apiWhenFloatingIPsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, allocateFloatingIP,
            allocateFloatingIPResponse);

      assertEquals(apiWhenFloatingIPsExist.getFloatingIPExtensionForZone("az-1.region-a.geo-1").get().allocate().get()
            .toString(), new ParseFloatingIPTest().expected().toString());

   }

   public void testAllocateWhenResponseIs404() throws Exception {
      HttpRequest allocateFloatingIP = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType("{}", "application/json")).build();

      HttpResponse allocateFloatingIPResponse = HttpResponse.builder().statusCode(404).build();

      NovaAsyncApi apiWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, allocateFloatingIP,
            allocateFloatingIPResponse);

      assertNull(apiWhenNoServersExist.getFloatingIPExtensionForZone("az-1.region-a.geo-1").get().allocate().get());
   }

}
