package com.grantip.backend.global.util.database;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * QueryDSL 쿼리 구성 및 페이징 처리를 위한 유틸리티 클래스.
 * 현재 프로젝트의 장학금 검색 기능에 필수적인 메서드들로만 구성됩니다.
 */
@UtilityClass
public class QueryDslUtil {

  /**
   * 여러 BooleanExpression을 하나의 AND 절로 결합합니다.
   * 모든 expression이 null이면 null을 반환합니다.
   */
  public BooleanExpression allOf(BooleanExpression... expressions) {
    BooleanExpression result = null;
    if (expressions == null || expressions.length == 0) {
      return null;
    }
    for (BooleanExpression expression : expressions) {
      result = combineWhereClause(result, expression);
    }
    return result;
  }

  /**
   * 여러 BooleanExpression을 하나의 OR 절로 결합합니다.
   * 모든 expression이 null이면 null을 반환합니다.
   */
  public BooleanExpression anyOf(BooleanExpression... expressions) {
    BooleanExpression result = null;
    if (expressions == null || expressions.length == 0) {
      return null;
    }
    for (BooleanExpression expression : expressions) {
      if (expression != null) {
        result = (result == null) ? expression : result.or(expression);
      }
    }
    return result;
  }

  /**
   * 주어진 값(value)이 null이 아닐 때만 EQ(=) 조건을 생성합니다.
   */
  public <T> BooleanExpression eqIfNotNull(SimpleExpression<T> path, T value) {
    return value != null ? path.eq(value) : null;
  }

  /**
   * value가 null 또는 빈 문자열이 아닐 때만 대소문자 구분 없는 LIKE 조건을 생성합니다.
   */
  public BooleanExpression likeIgnoreCase(StringExpression path, String value) {
    return StringUtils.hasText(value) ? path.lower().like("%" + value.trim().toLowerCase() + "%") : null;
  }

  /**
   * JPAQuery에 동적 정렬을 적용합니다.
   * customSortMap에 프로퍼티명이 매핑된 ComparableExpression이 있으면 우선 사용합니다.
   */
  public <D, E> void applySorting(
      JPAQuery<D> query,
      Pageable pageable,
      Class<E> entityClass, String alias,
      Map<String, ComparableExpression<?>> customSortMap
  ) {
    if (pageable.getSort().isEmpty()) {
      return;
    }
    PathBuilder<E> builder = new PathBuilder<>(entityClass, alias);
    for (Sort.Order order : pageable.getSort()) {
      String property = order.getProperty();
      boolean asc = order.isAscending();

      ComparableExpression<?> expression = customSortMap.get(property);
      if (expression == null) {
        expression = builder.getComparable(property, Comparable.class);
      }
      query.orderBy(createOrderSpecifier(expression, asc));
    }
  }

  /**
   * JPAQuery에 동적 정렬을 적용합니다. customSortMap이 없는 경우 사용합니다.
   */
  public <D, E> void applySorting(JPAQuery<D> query, Pageable pageable, Class<E> entityClass, String alias) {
    applySorting(query, pageable, entityClass, alias, Collections.emptyMap());
  }


  /**
   * QueryDSL JPAQuery를 이용한 페이징 처리합니다.
   * contentQuery와 countQuery를 실행하여 Page 객체로 반환합니다.
   */
  public <T> Page<T> fetchPage(JPAQuery<T> contentQuery, JPAQuery<Long> countQuery, Pageable pageable) {
    List<T> content = contentQuery
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long total = countQuery.fetchOne();
    total = (total == null) ? 0L : total;

    return new PageImpl<>(content, pageable, total);
  }

  // --- 내부 사용 메서드 (private) ---

  private BooleanExpression combineWhereClause(BooleanExpression baseClause, BooleanExpression additionalClause) {
    if (additionalClause == null) {
      return baseClause;
    }
    return (baseClause == null) ? additionalClause : baseClause.and(additionalClause);
  }

  private <T extends Comparable<?>> OrderSpecifier<T> createOrderSpecifier(ComparableExpression<T> path, boolean asc) {
    return new OrderSpecifier<>(asc ? Order.ASC : Order.DESC, path);
  }
}