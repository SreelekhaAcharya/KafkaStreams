package com.KStreams.Data;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.KStreams.model.Order;

public interface OrderRepo extends MongoRepository<Order, Long>{

}
