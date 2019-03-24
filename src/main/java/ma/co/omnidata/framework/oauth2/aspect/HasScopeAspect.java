package ma.co.omnidata.framework.oauth2.aspect;

import java.util.Arrays;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.stereotype.Component;

import ma.co.omnidata.framework.oauth2.annotation.HasScope;

/**
 * Created by Abouaggad on 17/09/2018.
 */

@Aspect
@Component
public class HasScopeAspect {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Pointcut("@annotation(hasScope)")
	public void hasScopeAnnotation(HasScope hasScope) {
		// hasScope method pointcut
	}

	@Pointcut("execution(* *(..)) && hasScopeAnnotation(hasScope)")
	public void hasScopeMethod(HasScope hasScope) {
		// hasScope execution pointcut
	}

	@SuppressWarnings("unchecked")
	@Before("hasScopeMethod(hasScope)")
	public void beforeMethodExecution(JoinPoint jp, HasScope hasScope) {
		logger.debug("checking scope authorities");
		if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
			KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			AccessToken token = kp.getKeycloakSecurityContext().getToken();
			if (!hasScope.value().isEmpty() && !clientHasScope(token, hasScope.value())) {
				Throwable failure = new InsufficientScopeException("Insufficient scope for this resource", new HashSet<>(Arrays.asList(hasScope.value())));
				throw new AccessDeniedException(failure.getMessage(), failure);
			}
		} else {
			logger.debug("Security context is null or user not connected");
			throw new AccessDeniedException("No authentication context found!");
		}

	}

	private boolean clientHasScope(AccessToken token, String scope) {
		StringTokenizer stringTokenizer = new StringTokenizer(token.getScope(), " ");

		if (stringTokenizer.countTokens() > 0) {
			while (stringTokenizer.hasMoreTokens()) {
				if (stringTokenizer.nextToken().equalsIgnoreCase(scope)) {
					return true;
				}
			}
		}
		return false;
	}
}
