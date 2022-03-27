package br.com.emendes.financesapi.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.emendes.financesapi.config.security.TokenService;
import br.com.emendes.financesapi.controller.dto.SummaryDto;
import br.com.emendes.financesapi.service.SummaryService;

@RestController
@RequestMapping("/resumo")
public class SummaryController {

  @Autowired
  private SummaryService summaryService;

  @Autowired
  private TokenService tokenService;

  @GetMapping("/{year}/{month}")
  public ResponseEntity<SummaryDto> monthSummary(@PathVariable Integer year, @PathVariable Integer month,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return summaryService.monthSummary(year, month, userId);
  }

}
