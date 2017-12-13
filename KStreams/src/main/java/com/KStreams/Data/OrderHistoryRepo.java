package com.KStreams.Data;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.KStreams.model.OrderHistory;

public interface OrderHistoryRepo extends MongoRepository<OrderHistory, Long>{

}
