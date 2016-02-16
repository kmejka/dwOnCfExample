package pl.kmejka.dwOnCfExample.conf;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

public class ExampleConfiguration extends Configuration {

	@JsonProperty
	private String appName;

	public String getAppName() {
		return appName;
	}
}
