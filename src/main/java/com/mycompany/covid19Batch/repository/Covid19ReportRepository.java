package com.mycompany.covid19Batch.repository;




import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mycompany.covid19Batch.model.Covid19Report;

@Repository
public interface Covid19ReportRepository extends JpaRepository<Covid19Report, Integer> {
	
	/**
	 * return 0, record doesn't exist
	 * @param country
	 * @param state
	 * @param date_reported
	 * @return
	 */
	@Query("SELECT count(c) FROM Covid19Report as c WHERE c.country=:country and c.state=:state and c.dateReported=:date_reported")
	int findRecordExist(@Param("country") String country,@Param("state") String state,@Param("date_reported") Date date_reported);
	

	@Transactional
	@Modifying
	@Query("UPDATE Covid19Report c SET c.confirmed=:val where c.country=:country and c.state=:state and c.dateReported=:date_reported")
	void updateConfirmedCases(@Param("country") String country,@Param("state") String state,@Param("date_reported") Date date_reported,@Param("val") int val);

	@Transactional
	@Modifying
	@Query("UPDATE Covid19Report c SET c.deaths=:val where c.country=:country and c.state=:state and c.dateReported=:date_reported")
	void updateDeathCases(@Param("country") String country,@Param("state") String state,@Param("date_reported") Date date_reported,@Param("val") int val);
	
	@Transactional
	@Modifying
	@Query("UPDATE Covid19Report c SET c.recovered=:val where c.country=:country and c.state=:state and c.dateReported=:date_reported")
	void updateRecoveredCases(@Param("country") String country,@Param("state") String state,@Param("date_reported") Date date_reported,@Param("val") int val);

}
