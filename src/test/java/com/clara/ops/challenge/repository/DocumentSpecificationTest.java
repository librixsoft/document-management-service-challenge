package com.clara.ops.challenge.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.clara.ops.challenge.model.entity.DocumentEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

class DocumentSpecificationTest {

  @Test
  void byFilters_withAllFilters_buildsPredicates() {
    Root<DocumentEntity> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder cb = mock(CriteriaBuilder.class);

    @SuppressWarnings("unchecked")
    Expression<String> userPath = (Expression<String>) mock(Path.class);
    @SuppressWarnings("unchecked")
    Expression<String> namePath = (Expression<String>) mock(Path.class);
    @SuppressWarnings("unchecked")
    Expression<Object> tagsPath = (Expression<Object>) mock(Path.class);

    when(root.get("user")).thenReturn((Path) userPath);
    when(root.get("documentName")).thenReturn((Path) namePath);
    when(root.get("tags")).thenReturn((Path) tagsPath);

    Predicate userPredicate = mock(Predicate.class);
    Predicate namePredicate = mock(Predicate.class);
    Predicate tagPredicate = mock(Predicate.class);
    Predicate andPredicate = mock(Predicate.class);

    when(cb.equal(userPath, "user1")).thenReturn(userPredicate);
    when(cb.like(namePath, "%doc1%")).thenReturn(namePredicate);

    @SuppressWarnings("unchecked")
    Expression<String> functionExpr = (Expression<String>) mock(Expression.class);
    Expression<String> literalExpr = mock(Expression.class);
    when(cb.function(eq("array_to_string"), eq(String.class), eq(tagsPath), any()))
        .thenReturn(functionExpr);
    when(cb.literal(",")).thenReturn(literalExpr);
    when(cb.like(functionExpr, "%tag1%")).thenReturn(tagPredicate);
    when(cb.and(any(Predicate[].class))).thenReturn(andPredicate);

    Specification<DocumentEntity> spec =
        DocumentSpecification.byFilters("user1", "doc1", List.of("tag1"));
    Predicate result = spec.toPredicate(root, query, cb);

    assertThat(result).isSameAs(andPredicate);
    verify(cb).equal(userPath, "user1");
    verify(cb).like(namePath, "%doc1%");
    verify(cb).like(functionExpr, "%tag1%");
  }

  @Test
  void byFilters_user2Doc3_buildsPredicatesLikeRequirementsTree() {
    Root<DocumentEntity> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder cb = mock(CriteriaBuilder.class);

    @SuppressWarnings("unchecked")
    Expression<String> userPath = (Expression<String>) mock(Path.class);
    @SuppressWarnings("unchecked")
    Expression<String> namePath = (Expression<String>) mock(Path.class);
    @SuppressWarnings("unchecked")
    Expression<Object> tagsPath = (Expression<Object>) mock(Path.class);

    when(root.get("user")).thenReturn((Path) userPath);
    when(root.get("documentName")).thenReturn((Path) namePath);
    when(root.get("tags")).thenReturn((Path) tagsPath);

    Predicate userPredicate = mock(Predicate.class);
    Predicate namePredicate = mock(Predicate.class);
    Predicate tagPredicate = mock(Predicate.class);
    Predicate andPredicate = mock(Predicate.class);

    when(cb.equal(userPath, "user2")).thenReturn(userPredicate);
    when(cb.like(namePath, "%doc3%")).thenReturn(namePredicate);

    @SuppressWarnings("unchecked")
    Expression<String> functionExpr = (Expression<String>) mock(Expression.class);
    Expression<String> literalExpr = mock(Expression.class);
    when(cb.function(eq("array_to_string"), eq(String.class), eq(tagsPath), any()))
        .thenReturn(functionExpr);
    when(cb.literal(",")).thenReturn(literalExpr);
    when(cb.like(functionExpr, "%tag3%")).thenReturn(tagPredicate);
    when(cb.and(any(Predicate[].class))).thenReturn(andPredicate);

    Specification<DocumentEntity> spec =
        DocumentSpecification.byFilters("user2", "doc3", List.of("tag3"));
    Predicate result = spec.toPredicate(root, query, cb);

    assertThat(result).isSameAs(andPredicate);
    verify(cb).equal(userPath, "user2");
    verify(cb).like(namePath, "%doc3%");
    verify(cb).like(functionExpr, "%tag3%");
  }

  @Test
  void byFilters_withNoFilters_returnsAndPredicate() {
    Root<DocumentEntity> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder cb = mock(CriteriaBuilder.class);

    Predicate andPredicate = mock(Predicate.class);
    when(cb.and(any(Predicate[].class))).thenReturn(andPredicate);

    Specification<DocumentEntity> spec = DocumentSpecification.byFilters(null, null, null);
    Predicate result = spec.toPredicate(root, query, cb);

    assertThat(result).isSameAs(andPredicate);
  }
}
