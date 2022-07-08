//package br.com.emendes.financesapi.util;
//
//import java.util.Locale;
//import java.util.Map;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//@Component
//public class CustomMockMvc {
//
//  @Autowired
//  private MockMvc mockMvc;
//
//  public MvcResult get(String uri, String token, int status) {
//    try {
//      MvcResult result = mockMvc
//          .perform(MockMvcRequestBuilders
//              .get(uri)
//              .contentType(MediaType.APPLICATION_JSON)
//              .header(HttpHeaders.AUTHORIZATION, token))
//          .andExpect(MockMvcResultMatchers.status().is(status))
//          .andReturn();
//
//      result.getResponse().setCharacterEncoding("UTF-8");
//      return result;
//    } catch (Exception e) {
//      throw new RuntimeException(e.getMessage());
//    }
//  }
//
//  public MvcResult post(String uri, Map<String, Object> params, String token, int status) {
//    try {
//      String body = new ObjectMapper().writeValueAsString(params);
//
//      MvcResult result = mockMvc
//          .perform(MockMvcRequestBuilders
//              .post(uri)
//              .content(body)
//              .locale(Locale.forLanguageTag("pt-BR"))
//              .characterEncoding("UTF-8")
//              .contentType(MediaType.APPLICATION_JSON)
//              .header(HttpHeaders.AUTHORIZATION, token))
//          .andExpect(MockMvcResultMatchers.status().is(status))
//          .andReturn();
//
//      result.getResponse().setCharacterEncoding("UTF-8");
//      return result;
//    } catch (Exception e) {
//      throw new RuntimeException(e.getMessage());
//    }
//  }
//
//  public MvcResult put(String uri, Map<String, Object> params, String token, int status) {
//    try {
//      String body = new ObjectMapper().writeValueAsString(params);
//
//      MvcResult result = mockMvc
//          .perform(MockMvcRequestBuilders
//              .put(uri)
//              .content(body)
//              .contentType(MediaType.APPLICATION_JSON)
//              .header(HttpHeaders.AUTHORIZATION, token))
//          .andExpect(MockMvcResultMatchers.status().is(status))
//          .andReturn();
//
//      result.getResponse().setCharacterEncoding("UTF-8");
//      return result;
//    } catch (Exception e) {
//      throw new RuntimeException(e.getMessage());
//    }
//  }
//
//  public MvcResult delete(String uri, String token, int status) {
//    try {
//      MvcResult result = mockMvc
//        .perform(MockMvcRequestBuilders
//            .delete(uri)
//            .contentType(MediaType.APPLICATION_JSON)
//            .header(HttpHeaders.AUTHORIZATION, token))
//        .andExpect(MockMvcResultMatchers.status().is(status))
//        .andReturn();
//
//    result.getResponse().setCharacterEncoding("UTF-8");
//    return result;
//    } catch (Exception e) {
//      throw new RuntimeException(e.getMessage());
//    }
//  }
//
//}
