package br.com.emendes.financesapi.controller;

import java.net.URI;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ExpenseControllerTests {
  
  @Autowired
  private MockMvc mockMvc;

  @Test
  public void deveriaDevolver200ECategoriaIgualAOutrasQuandoNaoEnviamosACategoria() throws Exception{
    URI uri = new URI("/despesas");
    JSONObject jsonObject = new JSONObject();

    jsonObject.put("description", "Gasolina");
    jsonObject.put("value", 341.87);
    jsonObject.put("date", "2022-01-28");

    String body = jsonObject.toString();

    mockMvc
			.perform(MockMvcRequestBuilders
					.post(uri)
					.content(body)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers
					.status()
					.is(201))
      .andExpect(MockMvcResultMatchers.jsonPath("$.category").value("OUTRAS"));
  }

  @Test
  public void deveriaDevolver400QuandoNaoEnviamosDescricao() throws Exception{
    URI uri = new URI("/despesas");
    JSONObject jsonObject = new JSONObject();

    jsonObject.put("value", 341.87);
    jsonObject.put("date", "2022-01-28");
    jsonObject.put("category", "TRANSPORTE");

    String body = jsonObject.toString();

    mockMvc
			.perform(MockMvcRequestBuilders
					.post(uri)
					.content(body)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers
					.status()
					.is(400));
  }

  @Test
  public void jsonDevolvidoDeveriaSerIgualAoEnviadoComIdIgualA2() throws Exception{
    URI uri = new URI("/despesas");
    JSONObject jsonObject = new JSONObject();

    jsonObject.put("description", "Uber");
    jsonObject.put("value", 241.87);
    jsonObject.put("date", "2022-01-30");
    jsonObject.put("category", "TRANSPORTE");

    String body = jsonObject.toString();

    jsonObject.put("id", 2);

    // String jsonContent = jsonObject.toString();

    mockMvc
			.perform(MockMvcRequestBuilders
					.post(uri)
					.content(body)
					.contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers
        .jsonPath("$.category")
        .value("TRANSPORTE"))
      .andExpect(MockMvcResultMatchers
        .jsonPath("$.description")
        .value("Uber"))
      .andExpect(MockMvcResultMatchers
        .jsonPath("$.date")
        .value("2022-01-30"))
      .andExpect(MockMvcResultMatchers
        .jsonPath("$.value")
        .value(241.87));

  }

  @Test
  public void deveriaDevolver200NaBuscaPorId1() throws Exception {
    URI uriPost = new URI("/despesas");
    URI uriGet = new URI("/despesas/1");
    
    JSONObject jsonObject = new JSONObject();

    jsonObject.put("description", "Mercado da esquina");
    jsonObject.put("value", 582.80);
    jsonObject.put("date", "2022-01-30");
    jsonObject.put("category", "ALIMENTACAO");

    String body = jsonObject.toString();

    mockMvc
			.perform(MockMvcRequestBuilders
					.post(uriPost)
					.content(body)
					.contentType(MediaType.APPLICATION_JSON));

    mockMvc
      .perform(MockMvcRequestBuilders
        .get(uriGet))
      .andExpect(MockMvcResultMatchers
				.status()
				.is(200));
      // .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
  }

}
