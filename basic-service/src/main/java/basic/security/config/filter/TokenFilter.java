package basic.security.config.filter;

import basic.BasicController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static basic.Messages.CSRF_TOKEN_ERROR;

/**
 * Filter for check CSRF token before execution operation.
 * If token doesn't match, break operation and send error.
 */
public class TokenFilter extends GenericFilterBean {

    private HttpSessionCsrfTokenRepository tokenRepository = new HttpSessionCsrfTokenRepository();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (BasicController.LOGIN.equals(((HttpServletRequest) request).getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        CsrfToken sessionToken = tokenRepository.loadToken((HttpServletRequest) request);
        if (sessionToken != null) {
            String headerToken = ((HttpServletRequest) request).getHeader(sessionToken.getHeaderName());
            if (StringUtils.equals(sessionToken.getToken(), headerToken)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        ((HttpServletResponse) response).sendError(HttpStatus.UNAUTHORIZED.value(), CSRF_TOKEN_ERROR);
    }
}
