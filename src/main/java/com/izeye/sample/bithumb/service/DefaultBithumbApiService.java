package com.izeye.sample.bithumb.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.izeye.sample.bithumb.Currency;

/**
 * Default {@link BithumbApiService}.
 *
 * @author Johnny Lim
 */
@Service
public class DefaultBithumbApiService implements BithumbApiService {

	private static final String URL_TICKER = "https://api.bithumb.com/public/ticker/{currency}";

	private final RestTemplate restTemplate;

	public DefaultBithumbApiService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@Override
	public Map<String, Object> getTicker(Currency currency) {
		return this.restTemplate.getForObject(URL_TICKER, Map.class, currency);
	}

}
