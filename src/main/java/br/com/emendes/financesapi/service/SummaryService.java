package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.controller.dto.SummaryDto;

public interface SummaryService {

  SummaryDto monthSummary(int year, int month);

}
