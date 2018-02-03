package com.behsa;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

/**
 * @author Esa Hekmatizadeh
 */
public class GanjexDeployableContainer implements DeployableContainer<GanjexConfiguration> {
	public Class<GanjexConfiguration> getConfigurationClass() {
		return GanjexConfiguration.class;
	}

	public void setup(GanjexConfiguration ganjexConfiguration) {

	}

	public void start() throws LifecycleException {

	}

	public void stop() throws LifecycleException {

	}

	public ProtocolDescription getDefaultProtocol() {
		return new ProtocolDescription("ganjex");
	}

	public ProtocolMetaData deploy(Archive<?> archive) throws DeploymentException {
		return null;
	}

	public void undeploy(Archive<?> archive) throws DeploymentException {

	}

	public void deploy(Descriptor descriptor) throws DeploymentException {

	}

	public void undeploy(Descriptor descriptor) throws DeploymentException {

	}
}
