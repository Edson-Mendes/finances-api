package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.dto.response.SummaryResponse;

public interface SummaryService {

  SummaryResponse monthSummary(int year, int month);

}
