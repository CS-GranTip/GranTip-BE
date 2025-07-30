package com.grantip.backend.domain.scholarship.repository;

import com.grantip.backend.domain.scholarship.domain.dto.request.ScholarshipSearchRequest;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipSummaryResponse;
import com.grantip.backend.domain.region.domain.entity.QRegion;
import com.grantip.backend.domain.scholarship.domain.entity.QScholarship;
import com.grantip.backend.domain.scholarship.domain.entity.QScholarshipRegion;
import com.grantip.backend.global.util.database.QueryDslUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.util.StringUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ScholarshipRepositoryImpl implements ScholarshipRepositoryCustom {
  private final JPAQueryFactory queryFactory;
  private final QScholarship scholarship = QScholarship.scholarship;
  private final QScholarshipRegion scholarshipRegion = QScholarshipRegion.scholarshipRegion;
  private final QRegion region = QRegion.region;

  @Override
  public Page<ScholarshipSummaryResponse> searchScholarships(ScholarshipSearchRequest request, Pageable pageable){

    // 키워드 검색
    BooleanExpression keywordCondition = null;
    if(StringUtils.hasText(request.getKeyword())){
      String keyword = request.getKeyword();
      keywordCondition = QueryDslUtil.anyOf(
          QueryDslUtil.likeIgnoreCase(scholarship.productName, keyword),
          QueryDslUtil.likeIgnoreCase(region.regionName, keyword)
      );
    }

    // 카테고리 필터링
    BooleanExpression categoryFilterCondition = QueryDslUtil.eqIfNotNull(
        scholarship.scholarshipCategory, request.getCategory()
    );

    // 최종 WHERE절 결합
    BooleanExpression combinedWhereClause = QueryDslUtil.allOf(
        keywordCondition,
        categoryFilterCondition
    );

    JPAQuery<ScholarshipSummaryResponse> contentQuery = queryFactory
        .select(Projections.constructor(ScholarshipSummaryResponse.class,
            scholarship.id,
            scholarship.productName,
            scholarship.providerName,
            scholarship.productType,
            scholarship.scholarshipCategory,
            scholarship.applicationStartDate,
            scholarship.applicationEndDate
        ))
        .from(scholarship)
        .leftJoin(scholarshipRegion).on(scholarshipRegion.scholarship.eq(scholarship))
        .leftJoin(region).on(scholarshipRegion.region.eq(region))
        .where(combinedWhereClause)
        .distinct()
        .orderBy(scholarship.applicationStartDate.desc());

    // 카운트 쿼리
    JPAQuery<Long> countQuery = queryFactory
        .select(scholarship.countDistinct())
        .from(scholarship)
        .leftJoin(scholarshipRegion).on(scholarshipRegion.scholarship.eq(scholarship))
        .leftJoin(region).on(scholarshipRegion.region.eq(region))
        .where(combinedWhereClause);

    return QueryDslUtil.fetchPage(contentQuery, countQuery, pageable);
  }
}
