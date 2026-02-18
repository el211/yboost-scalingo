package dev.oreo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GuestMessageRepo extends MongoRepository<GuestMessage, String> {
}
