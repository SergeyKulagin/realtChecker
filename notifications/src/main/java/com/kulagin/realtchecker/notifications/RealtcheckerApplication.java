package com.kulagin.realtchecker.notifications;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kulagin.realtchecker.core.*;
import com.kulagin.realtchecker.core.model.Apartment;
import com.kulagin.realtchecker.core.model.Context;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@SpringBootApplication
@EnableScheduling
@Log4j2
@ComponentScan("com.kulagin.realtchecker.core")
public class RealtcheckerApplication implements CommandLineRunner{

  @Autowired
  private Environment environment;

  @Autowired
  private ApartmentInitialContextLoader contextLoader;
  @Autowired
  private ApartmentsLoader aparmentsLoader;
  @Autowired
  private ApartmentsSorter apartmentsSorter;
  @Autowired
  @Qualifier("filesystem")
  private ApartmentsStorer apartmentsStorer;
  @Autowired
  private ApartmentsPrettyPrinter apartmentsPrettyPrinter;
  @Autowired
  private ApartmentsNotifier apartmentsNotifier;
  @Autowired
  private ApartmentsComparer apartmentsComparer;

  public static void main(String[] args) {
    SpringApplication.run(RealtcheckerApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    // run on start-up
    log.info("Run the check job with the schedule {}", environment.getProperty("cron.check-schedule"));
    runCheck();
  }

  @Scheduled(cron = "${cron.check-schedule}")
  public void runPeriodicalCheck(){
    runCheck();
  }

  private void runCheck() {
    final Context context = contextLoader.loadContext();
    final List<Apartment> apartmentList = aparmentsLoader.load();
    context.setApartments(apartmentList);
    apartmentsSorter.sort(context);
    apartmentsStorer.store(context);
    apartmentsPrettyPrinter.printReport(context);
    apartmentsComparer.compare(context);
    apartmentsNotifier.notify(context);
  }

  @Bean
  public ObjectMapper objectMapper() {
    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    return objectMapper;
  }
}