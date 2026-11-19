package com.kinde.filter;

import com.kinde.constants.KindeAuthenticationAction;
import com.kinde.stepup.StepUp;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

import static com.kinde.constants.KindeJ2eeConstants.ACR_VALUES;
import static com.kinde.constants.KindeJ2eeConstants.MAX_AGE;

/**
 * Protects resources that need a stronger or more recent authentication than an ordinary
 * sign-in (step-up authentication, RFC 9470).
 *
 * <p>Configure the requirement with the {@code acr_values} ({@code mfa} or {@code phr}) and/or
 * {@code max_age} (seconds) init parameters:
 *
 * <pre>{@code
 * <filter>
 *   <filter-name>KindeStepUpFilter</filter-name>
 *   <filter-class>com.kinde.filter.KindeStepUpFilter</filter-class>
 *   <init-param><param-name>acr_values</param-name><param-value>mfa</param-value></init-param>
 *   <init-param><param-name>max_age</param-name><param-value>300</param-value></init-param>
 * </filter>
 * }</pre>
 *
 * <p>The session's tokens are checked on every request. When they don't meet the requirement
 * the user is redirected to Kinde, which asks only for what is missing. A redirect discards
 * the body of a POST, so check the requirement before showing a form rather than on its
 * submission. If the user cannot meet the requirement, a {@link ServletException} is thrown
 * whose root cause is an {@link com.kinde.exceptions.UnmetAuthenticationRequirementsException}.
 */
public class KindeStepUpFilter extends KindeAuthenticationFilter {

    private String acrValues;
    private Integer maxAge;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            String acr = filterConfig.getInitParameter(ACR_VALUES);
            this.acrValues = acr == null || acr.isBlank() ? null : StepUp.normalizeAcrValues(acr);
            String age = StepUp.normalizeMaxAgeParameter(filterConfig.getInitParameter(MAX_AGE));
            this.maxAge = age == null ? null : Integer.valueOf(age);
        } catch (IllegalArgumentException e) {
            throw new ServletException("Invalid KindeStepUpFilter configuration: " + e.getMessage(), e);
        }
        if (this.acrValues == null && this.maxAge == null) {
            throw new ServletException("KindeStepUpFilter requires the acr_values and/or max_age init parameter");
        }
        if (this.maxAge != null && this.maxAge == 0) {
            // No session is ever "0 seconds old", so every request would redirect to Kinde
            throw new ServletException("KindeStepUpFilter cannot use max_age=0: no request would ever pass. "
                    + "Use a short window such as 60, or KindeStepUpServlet for a one-off verification");
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter(servletRequest, servletResponse, filterChain, KindeAuthenticationAction.STEP_UP, acrValues, maxAge);
    }
}
