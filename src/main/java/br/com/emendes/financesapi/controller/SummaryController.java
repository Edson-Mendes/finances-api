package br.com.emendes.financesapi.controller;

import br.com.emendes.financesapi.controller.dto.SummaryDto;
import br.com.emendes.financesapi.controller.openapi.SummaryControllerOpenAPI;
import br.com.emendes.financesapi.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("api/summaries")
public class SummaryController implements SummaryControllerOpenAPI {

  @Autowired
  private SummaryService summaryService;

  @Override
  @GetMapping("/{year}/{month}")
  public ResponseEntity<SummaryDto> monthSummary(
      @Min(1970) @Max(2099) @PathVariable int year,
      @Min(1) @Max(12) @PathVariable int month) {
    SummaryDto summaryDto = summaryService.monthSummary(year, month);

    return ResponseEntity.ok(summaryDto);
  }

}
