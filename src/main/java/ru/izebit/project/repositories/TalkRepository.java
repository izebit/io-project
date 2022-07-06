package ru.izebit.project.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.izebit.project.models.Talk;

/**
 * @author <a href="mailto:izebit@gmail.com">Artem Konovalov</a> <br/>
 * Date: 05.07.2022
 */
public interface TalkRepository extends ReactiveMongoRepository<Talk, String> {
}
