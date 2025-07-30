package com.grantip.backend.domain.scholarship.repository;

import com.grantip.backend.domain.scholarship.domain.dto.request.ScholarshipSearchRequest;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipSummaryResponse;
import com.grantip.backend.domain.region.domain.entity.QRegion;
import com.grantip.backend.domain.scholarship.domain.entity.QScholarship;
import com.grantip.backend.domain.scholarship.domain.entity.QScholarshipRegion;
import com.grantip.backend.domain.scholarship.domain.entity.Scholarship;
import com.grantip.backend.domain.user.domain.entity.User;
import com.grantip.backend.domain.user.domain.entity.UserExtraInfo;
import com.grantip.backend.global.util.database.QueryDslUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import java.util.List;
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
        .orderBy(scholarship.applicationEndDate.desc());

    // 카운트 쿼리
    JPAQuery<Long> countQuery = queryFactory
        .select(scholarship.countDistinct())
        .from(scholarship)
        .leftJoin(scholarshipRegion).on(scholarshipRegion.scholarship.eq(scholarship))
        .leftJoin(region).on(scholarshipRegion.region.eq(region))
        .where(combinedWhereClause);

    return QueryDslUtil.fetchPage(contentQuery, countQuery, pageable);
  }

  public List<Scholarship> findFilteredScholarships(User user, UserExtraInfo userInfo){
    // 신청 기간 필터링 - 추후 데이터 많아지면 활성화
    /*LocalDate today = LocalDate.now();
    builder.and(scholarship.applicationStartDate.loe(today))
        .and(scholarship.applicationEndDate.goe(today));*/

    // 대학 구분 필터링 - 장학금에 대학구분 조건이 없거나 사용자와 일치
    BooleanExpression universityCondition = scholarship.universityCategories.isEmpty();
    if(user.getUniversityCategory() != null){
      universityCondition = universityCondition.or(scholarship.universityCategories.contains(user.getUniversityCategory()));
    }

    // 거주 지역 필터링 장학금에 지역 조건이 없거나 사용자 주소와 일치
    BooleanExpression regionCondition = JPAExpressions
        .selectFrom(scholarshipRegion)
        .where(scholarshipRegion.scholarship.eq(scholarship))
        .notExists();

    if(user.getAddress() != null || user.getResidentAddress() != null){
      BooleanBuilder userRegionMatch = new BooleanBuilder();
      if(user.getAddress() != null){
        userRegionMatch.or(scholarshipRegion.region.eq(user.getAddress()));
      }
      if(user.getResidentAddress() != null){
        userRegionMatch.or(scholarshipRegion.region.eq(user.getResidentAddress()));
      }
      regionCondition = regionCondition.or(
          JPAExpressions
              .selectFrom(scholarshipRegion)
              .where(scholarshipRegion.scholarship.eq(scholarship).and(userRegionMatch))
              .exists()
      );
    }

    return queryFactory
        .select(scholarship)
        .distinct()
        .from(scholarship)
        // 기준 테이블들 Fetch Join
        .leftJoin(scholarship.gradeCriteria).fetchJoin()
        .leftJoin(scholarship.incomeCriteria).fetchJoin()
        .leftJoin(scholarship.generalCriteria).fetchJoin()
        .where(universityCondition, regionCondition)
        .fetch();
  }
}
