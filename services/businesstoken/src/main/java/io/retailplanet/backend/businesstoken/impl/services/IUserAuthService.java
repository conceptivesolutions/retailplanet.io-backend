package io.retailplanet.backend.businesstoken.impl.services;

import io.retailplanet.backend.common.comm.userauth.IUserAuthResource;
import io.retailplanet.backend.common.processor.URL;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Path;

/**
 * @author w.glanzer, 05.09.2019
 */
@Path("/internal/userauth")
@RegisterRestClient
@URL(targetModule = URL.ETarget.USERAUTH)
public interface IUserAuthService extends IUserAuthResource
{
}
