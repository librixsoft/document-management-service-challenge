package com.clara.ops.challenge.repository;

import com.clara.ops.challenge.model.entity.DocumentEntity;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class DocumentSpecification {

  public static Specification<DocumentEntity> byFilters(
      String user, String documentName, List<String> tags) {
    return (root, query, callback) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (user != null && !user.isEmpty()) {
        predicates.add(callback.equal(root.get("user"), user));
      }

      if (documentName != null && !documentName.isEmpty()) {
        predicates.add(callback.like(root.get("documentName"), "%" + documentName + "%"));
      }

      if (tags != null && !tags.isEmpty()) {
        // Aprovecha la sintaxis nativa de arreglos del dialecto si es provisto para buscar
        // si cualquiera de los tags solicitados existe en la columna
        for (String tag : tags) {
          // Hibernate 6 mapea List<String> a array text[]. Esto puede requerir custom func on
          // Postgres:
          // Pero un "like" sobre la serializacion a string del array es la salida fallback simple.
          // Lo ideal: callback.isMember(tag, root.get("tags"));  Pero JPA lo compila nativo.
          predicates.add(
              callback.like(
                  callback.function(
                      "array_to_string", String.class, root.get("tags"), callback.literal(",")),
                  "%" + tag + "%"));
        }
      }

      return callback.and(predicates.toArray(new Predicate[0]));
    };
  }
}
