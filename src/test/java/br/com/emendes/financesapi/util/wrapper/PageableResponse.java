package br.com.emendes.financesapi.util.wrapper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class PageableResponse<T> extends PageImpl<T> {
  private boolean first;
  private boolean last;
  private int totalPages;
  private int numberOfElements;

  @JsonCreator(mode = Mode.PROPERTIES )
  public PageableResponse(@JsonProperty("content") List<T> content,
                          @JsonProperty("number") int number,
                          @JsonProperty("size") int size,
                          @JsonProperty("totalElements") int totalElements,
                          @JsonProperty("last") boolean last,
                          @JsonProperty("first") boolean first,
                          @JsonProperty("totalPages") int totalPages,
                          @JsonProperty("numberOfElements") int numberOfElements,
                          @JsonProperty("pageable") JsonNode pageable,
                          @JsonProperty("sort") JsonNode sort) {
    super(content, PageRequest.of(number, size), totalElements);

    this.last = last;
    this.first = first;
    this.totalPages = totalPages;
    this.numberOfElements = numberOfElements;

  }

  @Override
  public boolean isFirst() {
    return first;
  }

  public void setFirst(boolean first) {
    this.first = first;
  }

  @Override
  public boolean isLast() {
    return last;
  }

  public void setLast(boolean last) {
    this.last = last;
  }

  @Override
  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  @Override
  public int getNumberOfElements() {
    return numberOfElements;
  }

  public void setNumberOfElements(int numberOfElements) {
    this.numberOfElements = numberOfElements;
  }
}
