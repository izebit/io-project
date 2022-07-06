package com.iodigital.project.repositories;

import com.iodigital.project.models.Talk;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @author <a href="mailto:izebit@gmail.com">Artem Konovalov</a> <br/>
 * Date: 05.07.2022
 */
public interface TalkRepository extends ReactiveMongoRepository<Talk, String> {
}
