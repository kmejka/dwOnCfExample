package pl.kmejka.dwOnCfExample.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class ExampleResource {

	private final String appName;

	public ExampleResource(final String appName) {
		this.appName = appName;
	}

	@GET
//	@Path(value = "/")
	@Produces(MediaType.TEXT_PLAIN)
	public Response helloWorld() {
		return Response.ok("Hello world, I'm "+appName).build();
	}
}
