package ch.kk7.config4j.binding.map;

import ch.kk7.config4j.binding.BindingType;
import ch.kk7.config4j.binding.ConfigBinder;
import ch.kk7.config4j.binding.ConfigBinding;
import ch.kk7.config4j.format.ConfigFormat.ConfigFormatMap;
import ch.kk7.config4j.format.FormatSettings;
import ch.kk7.config4j.source.simple.SimpleConfig;
import ch.kk7.config4j.source.simple.SimpleConfigMap;

import java.util.Map;

public class MapBinding<T> implements ConfigBinding<Map<String, T>> {
	private final MapBuilder builder;
	private final ConfigBinding<T> componentDescription;

	public MapBinding(MapBuilder builder, BindingType bindingType, ConfigBinder configBinder) {
		this.builder = builder;
		//noinspection unchecked
		componentDescription = (ConfigBinding<T>) configBinder.toConfigBinding(bindingType);
	}

	@Override
	public ConfigFormatMap describe(FormatSettings formatSettings) {
		return ConfigFormatMap.anyKeyMap(formatSettings, componentDescription.describe(formatSettings));
	}

	@Override
	public Map<String, T> bind(SimpleConfig config) {
		if (!(config instanceof SimpleConfigMap)) {
			throw new IllegalStateException("expected a config map, but got: " + config);
		}
		Map<String, T> map = builder.newInstance();
		for (Map.Entry<String, SimpleConfig> configEntry : ((SimpleConfigMap) config).map()
				.entrySet()) {
			T item = componentDescription.bind(configEntry.getValue());
			map.put(configEntry.getKey(), item);
		}
		return builder.tryHarden(map);
	}
}