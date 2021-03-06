package com.kulagin.realtchecker.notifications;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.kulagin.realtchecker.core.ApartmentsComparer;
import com.kulagin.realtchecker.core.ApartmentsSorter;
import com.kulagin.realtchecker.core.SourceType;
import com.kulagin.realtchecker.core.impl.ApartmentsLoaderOnliner;
import com.kulagin.realtchecker.notifications.db.ApartmentsStorerMongo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OnlinerConfiguration {
    private final MongoTemplate mongoTemplate;
    
    @Bean
    @ConditionalOnProperty(value = "service.onliner.enabled", havingValue = "true", matchIfMissing = true)
    public Checker onlinerChecker(
            ApartmentsLoaderOnliner apartmentsLoader,
            ApartmentsSorter apartmentsSorter,
            @Qualifier("apartmentsStorerOnliner")
            ApartmentsStorerMongo apartmentsStorer,
            ApartmentsPrettyPrinterHTML apartmentsPrettyPrinter,
            ApartmentsNotifier apartmentsNotifier,
            ApartmentsComparer apartmentsComparer) {
        return new Checker(
                apartmentsLoader,
                apartmentsSorter,
                apartmentsStorer,
                apartmentsPrettyPrinter,
                apartmentsNotifier,
                apartmentsComparer
        );
    }
    
    @Bean
    public ApartmentsStorerMongo apartmentsStorerOnliner() {
        return new ApartmentsStorerMongo(mongoTemplate, SourceType.ONLINER);
    }
}
