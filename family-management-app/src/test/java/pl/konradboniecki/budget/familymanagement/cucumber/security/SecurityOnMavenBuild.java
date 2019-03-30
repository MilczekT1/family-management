package pl.konradboniecki.budget.familymanagement.cucumber.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import pl.konradboniecki.chassis.tools.ChassisSecurityBasicAuthHelper;

@Slf4j
public class SecurityOnMavenBuild implements Security {

    @Autowired
    private ChassisSecurityBasicAuthHelper chassisSecurityBasicAuthHelper;
    private HttpHeaders securityHeaders = new HttpHeaders();

    @Override
    public HttpHeaders getSecurityHeaders() {
        return securityHeaders;
    }

    @Override
    public void basicAuthentication() {
        String baToken = chassisSecurityBasicAuthHelper.getBasicAuthHeaderValue();
        securityHeaders.set(HttpHeaders.AUTHORIZATION, baToken);
    }

    @Override
    public void unathorize() {
        securityHeaders = new HttpHeaders();
    }
}
