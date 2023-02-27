package br.com.emendes.financesapi.controller;

import br.com.emendes.financesapi.controller.openapi.SummaryControllerOpenAPI;
import br.com.emendes.financesapi.dto.response.SummaryResponse;
import br.com.emendes.financesapi.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "api/summaries", produces = "application/json;charset=UTF-8")
public class SummaryController implements SummaryControllerOpenAPI {

  private final SummaryService summaryService;

  @Override
  @GetMapping("/{year}/{month}")
  public ResponseEntity<SummaryResponse> monthSummary(@PathVariable int year, @PathVariable int month) {
    SummaryResponse summaryResponse = summaryService.monthSummary(year, month);

    return ResponseEntity.ok(summaryResponse);
  }

}
