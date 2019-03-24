package ma.co.omnidata.framework.oauth2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Abouaggad on 28/11/2017.
 */
@ConfigurationProperties(prefix = "keycloak-add-on")
public class KeycloakServiceProperties {
	private List<String> unprotectedpaths = new ArrayList<>();

	public List<String> getUnprotectedpaths() {
		return unprotectedpaths;
	}

	public void setUnprotectedpaths(List<String> unprotectedpaths) {
		this.unprotectedpaths = unprotectedpaths;
	}
	
}