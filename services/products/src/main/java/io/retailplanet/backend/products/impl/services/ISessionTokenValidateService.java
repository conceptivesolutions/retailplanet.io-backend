package io.retailplanet.backend.products.impl.services;

import io.retailplanet.backend.common.comm.businesstoken.ISessionTokenValidateResource;
import io.retailplanet.backend.common.processor.URL;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Path;

/**
 * @author w.glanzer, 05.09.2019
 */
@Path("/internal/validate")
@RegisterRestClient
@URL(targetModule = URL.ETarget.BUSINESSTOKEN)
public interface ISessionTokenValidateService extends ISessionTokenValidateResource
{
}
