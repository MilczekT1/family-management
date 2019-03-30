package pl.konradboniecki.budget.familymanagement.cucumber.security;

import org.springframework.http.HttpHeaders;

public interface Security {

    HttpHeaders getSecurityHeaders();

    void basicAuthentication();

    void unathorize();

}
