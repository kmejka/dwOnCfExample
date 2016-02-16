package pl.kmejka.dwOnCfExample;

import de.thomaskrille.dropwizard_template_config.TemplateConfigBundle;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import pl.kmejka.dwOnCfExample.conf.ExampleConfiguration;
import pl.kmejka.dwOnCfExample.resource.ExampleResource;

public class ExampleApplication extends Application<ExampleConfiguration> {

	public static void main(String[] args) throws Exception {
		if (args == null || args.length == 0) {
			args = new String[] { "server", ClassLoader.getSystemResource("./conf.yml").getFile() };
		}
		new ExampleApplication().run(args);
	}

	@Override
	public void initialize(final Bootstrap<ExampleConfiguration> bootstrap) {
		bootstrap.addBundle(new TemplateConfigBundle());
	}

	@Override
	public void run(ExampleConfiguration configuration, Environment environment) throws Exception {
		final ExampleResource exampleResource = new ExampleResource(configuration.getAppName());
		environment.jersey().register(exampleResource);
	}
}
