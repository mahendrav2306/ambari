/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.events.listeners.upgrade;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.apache.ambari.server.events.HostComponentVersionAdvertisedEvent;
import org.apache.ambari.server.events.publishers.VersionEventPublisher;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponent;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.ServiceImpl;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;

/**
 * StackVersionListener tests.
 */
public class StackVersionListenerTest {

  private static final String DESIRED_VERSION = "1.2.3.4-5678";
  private static final String SERVICE_COMPONENT_NAME = "Some component name";
  private static final String SERVICE_NAME = "Service name";

  @Test
  public void testOnAmbariEvent() throws Exception {
    VersionEventPublisher publisher = createNiceMock(VersionEventPublisher.class);
    Cluster cluster = createNiceMock(Cluster.class);
    ServiceComponentHost sch = createNiceMock(ServiceComponentHost.class);
    RepositoryVersionEntity repositoryVersionEntity = createNiceMock(RepositoryVersionEntity.class);
    Service service = createNiceMock(Service.class);
    ServiceComponent serviceComponent = createNiceMock(ServiceComponent.class);

    expect(serviceComponent.getDesiredVersion()).andReturn(DESIRED_VERSION);
    expect(service.getServiceComponent(SERVICE_COMPONENT_NAME)).andReturn(serviceComponent);

    expect(cluster.getClusterId()).andReturn(99L);
    expect(cluster.getService(anyString())).andReturn(service);

    expect(sch.getServiceName()).andReturn(SERVICE_NAME);
    expect(sch.getServiceComponentName()).andReturn(SERVICE_COMPONENT_NAME);
    expect(sch.recalculateHostVersionState()).andReturn(repositoryVersionEntity).atLeastOnce();

    cluster.recalculateClusterVersionState(repositoryVersionEntity);
    EasyMock.expectLastCall().atLeastOnce();

    // Replay and assert expectations
    replay(cluster, sch, serviceComponent, service);

    HostComponentVersionAdvertisedEvent event = new HostComponentVersionAdvertisedEvent(cluster, sch, DESIRED_VERSION);
    StackVersionListener listener = new StackVersionListener(publisher);

    listener.onAmbariEvent(event);

    verify(cluster, sch);
  }
}