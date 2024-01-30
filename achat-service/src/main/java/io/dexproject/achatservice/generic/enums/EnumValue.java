package io.dexproject.achatservice.generic.enums;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "enums")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class EnumValue {
  private final String id;
  private final String value;

  public EnumValue(String id, String value) {
    this.id = id;
    this.value = value;
  }
}
