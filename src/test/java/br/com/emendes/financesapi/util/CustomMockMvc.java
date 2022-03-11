package br.com.emendes.financesapi.util;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Component
public class CustomMockMvc {
  
  @Autowired
  private MockMvc mockMvc;

  public MvcResult get(String uri, String token, int status) throws Exception{
    return mockMvc
    .perform(MockMvcRequestBuilders
    .get(uri)
    .contentType(MediaType.APPLICATION_JSON)
    .header(HttpHeaders.AUTHORIZATION, token))
    .andExpect(MockMvcResultMatchers.status().is(status))
    .andReturn();
  }

  public MvcResult post(String uri, Map<String, Object> params, String token, int status) throws Exception {
    String body = new ObjectMapper().writeValueAsString(params);
    
    return mockMvc
        .perform(MockMvcRequestBuilders
          .post(uri)
          .content(body)
          .contentType(MediaType.APPLICATION_JSON)
          .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(MockMvcResultMatchers.status().is(status))
        .andReturn();
  }

  public MvcResult put(String uri, Map<String, Object> params, String token, int status) throws Exception {
    String body = new ObjectMapper().writeValueAsString(params);

    return mockMvc
        .perform(MockMvcRequestBuilders
            .put(uri)
            .content(body)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(MockMvcResultMatchers.status().is(status))
        .andReturn();
  }

  public MvcResult delete(String uri, String token, int status) throws Exception {
    return mockMvc
        .perform(MockMvcRequestBuilders
            .delete(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(MockMvcResultMatchers.status().is(status))
        .andReturn();
  }

}
