package com.KStreams.Controller;

import java.util.List;
import java.util.Properties;

import javax.annotation.PreDestroy;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.KStreams.Data.OrderHistoryRepo;
import com.KStreams.Data.OrderRepo;
import com.KStreams.model.Order;
import com.KStreams.model.OrderHistory;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@Configuration
@EnableKafka
public class Controller {

	private static final Logger log = LoggerFactory.getLogger(Controller.class);
	@Autowired
	OrderRepo orepo;

	@Autowired
	OrderHistoryRepo orderhisrepo;

	@Autowired
	private KafkaTemplate<String, Order> kafkaTemplate;

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapserver;
	
	private KafkaStreams streams;

	Serde<String> stringSerde = Serdes.String();
	Serializer<JsonNode> jsonSerializer = new JsonSerializer();
	Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
	Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

	@GetMapping("/order")
	public ResponseEntity<List> getOrders(){
		List<Order> orders = orepo.findAll();
		return new ResponseEntity<>(orders,HttpStatus.OK);
	}

	@GetMapping("/orderhistory")
	public ResponseEntity<List> getOrderHistory(){
		List<OrderHistory> orders = orderhisrepo.findAll();
		return new ResponseEntity<>(orders,HttpStatus.OK);
	}

	@PostMapping("/order")
	public ResponseEntity<List<OrderHistory>> addOrders(@RequestBody Order order){
		orepo.save(order);
		ProducerRecord<String,Order> record = new ProducerRecord<String, Order>("orders_topic", order);
		kafkaTemplate.send(record);		

		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-stream-processing-application");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapserver);
		props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		StreamsConfig config = new StreamsConfig(props);



		KStreamBuilder builder = new KStreamBuilder();
		builder.stream(stringSerde, jsonSerde, "orders_topic")
		.foreach(new ForeachAction<String, JsonNode>() {
			@Override
			public void apply(String arg0, JsonNode ord) {
				OrderHistory ordh = new OrderHistory();
				Long profileId=ord.path("prflId").asLong();
				String orderDesc = ord.path("orderDesc").asText();
				log.info("prflid is"+profileId+"and desc is"+orderDesc);
				ordh.setPrflId(profileId);
				ordh.setOrderDesc(orderDesc);
				orderhisrepo.save(ordh);
			}
		});
		streams = new KafkaStreams(builder, config);
		streams.start();
		List<OrderHistory> orders = orderhisrepo.findAll() ;
		return new ResponseEntity<>(orders,HttpStatus.OK);
	}

	@PreDestroy
	public void closeStream() {
		streams.close();
	}
}
