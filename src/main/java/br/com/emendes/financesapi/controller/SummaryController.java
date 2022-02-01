package br.com.emendes.financesapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.emendes.financesapi.controller.dto.SummaryDto;
import br.com.emendes.financesapi.service.SummaryService;
@RestController
@RequestMapping("/resumo")
public class SummaryController {

  @Autowired
  private SummaryService summaryService;

  @GetMapping("/{year}/{month}")
  public SummaryDto monthSummary(@PathVariable Integer year, @PathVariable Integer month){
    
    return summaryService.monthSummary(year, month);
  }

  

}
