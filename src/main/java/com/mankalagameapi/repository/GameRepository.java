package com.mankalagameapi.repository;

import com.mankalagameapi.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepository extends MongoRepository<Game, String> {
}
