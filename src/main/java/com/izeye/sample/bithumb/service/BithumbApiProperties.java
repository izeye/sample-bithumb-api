package com.izeye.sample.bithumb.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * {@link ConfigurationProperties} for Bithumb API.
 *
 * @author Johnny Lim
 */
@ConfigurationProperties(prefix = "bithumb")
@Data
public class BithumbApiProperties {

	private String apiKey;
	private String secretKey;

}
