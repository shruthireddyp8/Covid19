package com.mycompany.covid19Batch.scheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.List;
import java.net.URI;
import java.sql.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mycompany.covid19Batch.model.Covid19Report;
import com.mycompany.covid19Batch.repository.Covid19ReportRepository;

@Component
public class ScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	@Autowired
	private Covid19ReportRepository covid19Repository;
	
	private static String COVID_DATA_URL_CONFIRMED = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";

	private static String COVID_DATA_URL_DEATHS = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Deaths.csv";
	
	private static String COVID_DATA_URL_RECOVERED ="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Recovered.csv";
	
	
	RestTemplate restTemplate = new RestTemplate();
	
	@Scheduled(cron = "30 57 19 * * * ")
	public void reportCurrentTime() throws ParseException {
		log.info("The time is now {}", new Date(System.currentTimeMillis()));
		List<Covid19Report> newStats = new ArrayList<>();
		
		ResponseEntity<String> responseConfirmed
		  = restTemplate.getForEntity(COVID_DATA_URL_CONFIRMED, String.class);
		System.out.println(responseConfirmed.getBody());
		String strConfirmed = responseConfirmed.getBody();
		
		
		ResponseEntity<String> responseDeath
		  = restTemplate.getForEntity(COVID_DATA_URL_DEATHS, String.class);
		System.out.println(responseDeath.getBody());
		String strDeaths = responseDeath.getBody();
		
		
		ResponseEntity<String> responseRecovered
		  = restTemplate.getForEntity(COVID_DATA_URL_RECOVERED, String.class);
		System.out.println(responseRecovered.getBody());
		String strRecovered = responseRecovered.getBody();	
		try {
			convertingJsonToModel(strConfirmed,"confirmed");
		}catch(Exception e) {
			log.error(e.getMessage());
		}
		try {
			convertingJsonToModel(strDeaths,"deaths");
		}catch(Exception e) {
			log.error(e.getMessage());
		}
		try {
			convertingJsonToModel(strRecovered,"recovered");
		}catch(Exception e) {
			log.error(e.getMessage());
		}
		
			
		
	}
	/*
	 * responseBody, response body is with comma separated values having first row of header information and next rows are of values
	 * type can be confirmed,death or recovered
	 */
	public void convertingJsonToModel(String responseBody, String type) throws ParseException {
		String[] strArray = responseBody.split("\\R");
		String[] headers = strArray[0].split(",");
		for(int i=1; i<=strArray.length-2; i++) {
			String[] values = strArray[i].split(",");
			String state = values[0];
			String country = values[1];
			for(int j=4;j<=values.length-1;j++) {
				Covid19Report report = new Covid19Report();
				report.setState(state);
				report.setCountry(country);
				
				SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
				java.util.Date date = formatter.parse(headers[j]);
				java.sql.Date sqlDate = new Date(date.getTime());
				report.setDateReported(sqlDate);
				
				if(covid19Repository.findRecordExist(report.getCountry(), report.getState(), report.getDateReported())==0) {
					covid19Repository.save(report);
				}
				if(type.equalsIgnoreCase("confirmed")) {
					report.setConfirmed(Integer.parseInt(values[j]));
					covid19Repository.updateConfirmedCases(report.getCountry(), report.getState(), report.getDateReported(), report.getConfirmed());
				}else if(type.equalsIgnoreCase("deaths")) {
					report.setDeaths(Integer.parseInt(values[j]));
					covid19Repository.updateDeathCases(report.getCountry(), report.getState(), report.getDateReported(), report.getDeaths());
				}else if(type.equalsIgnoreCase("recovered")) {
					report.setRecovered(Integer.parseInt(values[j]));
					covid19Repository.updateRecoveredCases(report.getCountry(), report.getState(), report.getDateReported(), report.getRecovered());
				}
				
			}
			
			
			
		}
		
		
		
	}
}
