package de.noltarium.keenio.gateway.services;

import java.util.Map;

public interface AnalyticsStorage {

	void pushData(Map<String, Object> content);

}
