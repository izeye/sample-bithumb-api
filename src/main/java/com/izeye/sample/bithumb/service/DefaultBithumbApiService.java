package com.izeye.sample.bithumb.service;

import java.util.Map;

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

	private static final String URL_PUBLIC = "https://api.bithumb.com/public";

	private static final String URL_TICKER = URL_PUBLIC + "/ticker/{currency}";
	private static final String URL_ORDERBOOK = URL_PUBLIC + "/orderbook/{currency}";
	private static final String URL_RECENT_TRANSACTIONS = URL_PUBLIC + "/recent_transactions/{currency}";

	private final RestTemplate restTemplate;

	public DefaultBithumbApiService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@Override
	public Map<String, Object> getTicker(Currency currency) {
		return this.restTemplate.getForObject(URL_TICKER, Map.class, currency);
	}

	@Override
	public Map<String, Object> getOrderbook(Currency currency) {
		return this.restTemplate.getForObject(URL_ORDERBOOK, Map.class, currency);
	}

	@Override
	public Map<String, Object> getRecentTransactions(Currency currency) {
		return this.restTemplate.getForObject(URL_RECENT_TRANSACTIONS, Map.class, currency);
	}

}
