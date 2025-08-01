package com.grantip.backend.domain.calendar.repository;

import com.grantip.backend.domain.calendar.domain.dto.response.CalendarScholarshipResponse;
import com.grantip.backend.domain.favorite.domain.entity.QFavoriteScholar;
import com.grantip.backend.domain.scholarship.domain.entity.QScholarship;
import com.grantip.backend.domain.user.domain.entity.User;
import com.grantip.backend.global.util.database.QueryDslUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CalendarRepositoryImpl implements CalendarRepositoryCustom {
  private final JPAQueryFactory queryFactory;
  private final QFavoriteScholar favoriteScholar = QFavoriteScholar.favoriteScholar;
  private final QScholarship scholarship = QScholarship.scholarship;

  @Override
  public List<CalendarScholarshipResponse> findFavoriteScholarshipsInPeriod(User user, LocalDate startDate, LocalDate endDate){
    BooleanExpression whereClause = QueryDslUtil.allOf(
        favoriteScholar.user.eq(user),
        scholarship.applicationEndDate.goe(startDate),
        scholarship.applicationStartDate.loe(endDate)
    );

    return queryFactory
        .select(Projections.constructor(CalendarScholarshipResponse.class,
            scholarship.id,
            scholarship.productName,
            scholarship.applicationStartDate,
            scholarship.applicationEndDate
        ))
        .from(favoriteScholar)
        .join(favoriteScholar.scholarship, scholarship)
        .where(whereClause)
        .fetch();
  }
}
