package nl.rabobank.customerstatementprocessor.config;

import io.micrometer.core.instrument.util.StringUtils;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedirectFilter implements Filter {

  // Filter to redirect to swagger-ui when accessing the root path
  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    if (StringUtils.isBlank(request.getRequestURI())
        || request.getRequestURI().equals(Constants.CONTEXT_ROOT_PATH)) {
      response.sendRedirect(Constants.SWAGGER_UI_PATH);
      return;
    }
    filterChain.doFilter(servletRequest, servletResponse);
  }
}
