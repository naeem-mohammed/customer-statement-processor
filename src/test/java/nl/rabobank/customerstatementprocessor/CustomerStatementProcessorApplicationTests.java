package nl.rabobank.customerstatementprocessor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import lombok.RequiredArgsConstructor;
import nl.rabobank.customerstatementprocessor.config.RedirectFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@RequiredArgsConstructor
class CustomerStatementProcessorApplicationTests {

  private final WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @BeforeEach
  public void setUp() {
    mockMvc = webAppContextSetup(webApplicationContext).addFilters(new RedirectFilter()).build();
  }

  @Test
  public void when_SwaggerURiIsAccessed_Then_AssertSwaggerResourceFound() throws Exception {
    mockMvc.perform(get("/swagger-ui/")).andExpect(status().isOk()).andReturn().getResponse();
  }

  @Test
  public void when_RootPathIsAccessed_Then_AssertRedirectedToSwagger() throws Exception {
    mockMvc
        .perform(get("/"))
        .andExpect(redirectedUrl("/swagger-ui/"))
        .andExpect(status().is3xxRedirection())
        .andReturn()
        .getResponse();
  }
}
